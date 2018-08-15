package caceresenzo.apps.boxplay.activities;

import java.util.List;

import com.github.chrisbanes.photoview.HackyProblematicViewPager;
import com.github.chrisbanes.photoview.PhotoView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.BaseViewPagerAdapter;
import caceresenzo.apps.boxplay.fragments.utils.ViewFragment;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager;
import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.string.StringUtils;
import caceresenzo.libs.thread.HelpedThread;

public class MangaChapterReaderActivity extends AppCompatActivity {
	
	public static final int OFFSET_NEXT_PAGE = 1;
	public static final int OFFSET_PREVIOUS_PAGE = -1;
	
	private static MangaChapterReaderActivity INSTANCE;
	
	private static ChapterItemResultData RESULT;
	
	private BoxPlayActivity boxPlayActivity;
	private Handler handler;
	private SearchAndGoManager searchAndGoManager;
	
	private SlidingUpPanelLayout slidingUpPanelLayout;
	
	private ViewPager mangaViewPager;
	private BaseViewPagerAdapter pagerAdapter;
	
	private TextView infoTextView;
	
	private TextView errorTextView;
	private ProgressBar loadingProgressBar;
	
	private ExtractionWorker extractionWorker;
	
	private int chapterSize;
	
	public MangaChapterReaderActivity() {
		INSTANCE = this;
		
		this.boxPlayActivity = BoxPlayActivity.getBoxPlayActivity();
		this.handler = BoxPlayActivity.getHandler();
		this.searchAndGoManager = BoxPlayActivity.getManagers().getSearchAndGoManager();
		
		this.extractionWorker = new ExtractionWorker();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_manga_chapter_reader);
		
		if (RESULT == null || !(RESULT.getImageContentProvider() instanceof IMangaContentProvider)) {
			if (boxPlayActivity != null) {
				boxPlayActivity.toast(getString(R.string.boxplay_error_activity_invalid_data)).show();
			}
			finish();
		}
		
		initializeViews();
		
		initializeManga();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		INSTANCE = null;
		
		extractionWorker.cancel();
	}
	
	private void initializeViews() {
		slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_manga_chapter_reader_slidinglayout_container);
		
		mangaViewPager = (HackyProblematicViewPager) findViewById(R.id.activity_manga_chapter_reader_viewpager_container);
		mangaViewPager.setAdapter(pagerAdapter = new BaseViewPagerAdapter(getSupportFragmentManager()));
		
		mangaViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				updateSelectedPage(position + 1);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				;
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				;
			}
		});
		
		infoTextView = (TextView) findViewById(R.id.activity_manga_chapter_reader_textview_info);
		
		errorTextView = (TextView) findViewById(R.id.activity_manga_chapter_reader_textview_error);
		loadingProgressBar = (ProgressBar) findViewById(R.id.activity_manga_chapter_reader_progressbar_loading);
	}
	
	private void initializeManga() {
		if (extractionWorker.isRunning()) {
			boxPlayActivity.toast("ExtractionWorker is busy").show();
			return;
		}
		
		setViewerHidden(true);
		
		extractionWorker.applyData(RESULT).start();
	}
	
	private void setViewerHidden(boolean hidden) {
		slidingUpPanelLayout.setVisibility(hidden ? View.GONE : View.VISIBLE);
		
		loadingProgressBar.setVisibility(hidden ? View.VISIBLE : View.GONE);
		errorTextView.setVisibility(View.GONE);
	}
	
	private void displayError(final Exception exception) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				setViewerHidden(true);
				loadingProgressBar.setVisibility(View.GONE);
				
				errorTextView.setVisibility(View.VISIBLE);
				errorTextView.setText(getString(R.string.boxplay_manga_chapter_reader_format_error, exception.getLocalizedMessage(), StringUtils.fromException(exception)));
				
			}
		});
	}
	
	private void setPageByOffset(int offset) {
		if (offset < 0) {
			return;
		}
		
		if (mangaViewPager.getCurrentItem() <= pagerAdapter.getCount()) {
			mangaViewPager.setCurrentItem(mangaViewPager.getCurrentItem() + 1);
		} else {
			// TODO: Notify user that chapter has ended
		}
		
		updateSelectedPage(mangaViewPager.getCurrentItem() + 1);
	}
	
	/**
	 * Update the selected page panel
	 * 
	 * @param selectedPage
	 *            Actual position + 1 to remove the offset
	 */
	private void updateSelectedPage(int selectedPage) {
		infoTextView.setText(getString(R.string.boxplay_manga_chapter_reader_format_info, RESULT.getName(), selectedPage, chapterSize));
	}
	
	public static void start(ChapterItemResultData result) {
		MangaChapterReaderActivity.RESULT = result;
		
		BoxPlayApplication application = BoxPlayApplication.getBoxPlayApplication();
		
		Intent intent = new Intent(application, MangaChapterReaderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		application.startActivity(intent);
	}
	
	class ExtractionWorker extends HelpedThread {
		private final MangaChapterReaderActivity parentActivity;
		private ChapterItemResultData result;
		
		private IMangaContentProvider mangaContentProvider;
		private MangaChapterContentExtractor chapterContentExtractor;
		
		private List<String> imageUrls;
		
		public ExtractionWorker() {
			this.parentActivity = INSTANCE;
		}
		
		@Override
		protected void onRun() {			
			try {
				imageUrls = chapterContentExtractor.getImageUrls(mangaContentProvider.extractMangaPageUrl(result));
			} catch (Exception exception) {
				displayError(exception);
			}
			
			if (imageUrls == null) {
				cancel();
			}
		}
		
		@Override
		protected void onFinished() {
			chapterSize = imageUrls.size();
			
			handler.post(new Runnable() {
				@Override
				public void run() {
					for (String imageUrl : imageUrls) {
						PhotoView imageView = new PhotoView(parentActivity);
						
						imageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								setPageByOffset(OFFSET_NEXT_PAGE);
							}
						});
						
						pagerAdapter.addFragment(new ViewFragment(imageView, false), "");
						
						BoxPlayActivity.getViewHelper().downloadToImageView(imageView, imageUrl);
						
						pagerAdapter.notifyDataSetChanged(); // Need to be called everytime
					}
					
					mangaViewPager.setOffscreenPageLimit(chapterSize);
					pagerAdapter.notifyDataSetChanged(); // Just to be sure
					
					updateSelectedPage(1);
					
					setViewerHidden(false);
				}
			});
			
		}
		
		@Override
		protected void onCancelled() {
			;
		}
		
		public ExtractionWorker applyData(ChapterItemResultData result) {
			if (result == null) {
				return this;
			}
			
			this.result = result;
			
			this.mangaContentProvider = (IMangaContentProvider) result.getImageContentProvider();
			this.chapterContentExtractor = (MangaChapterContentExtractor) searchAndGoManager.createMangaExtractorFromCompatible(mangaContentProvider.getCompatibleExtractorClass());
			
			return this;
		}
		
	}
	
}