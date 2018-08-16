package caceresenzo.apps.boxplay.fragments.culture.searchngo.detailpage;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import caceresenzo.android.libs.dialog.DialogUtils;
import caceresenzo.android.libs.internet.AndroidDownloader;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.MangaChapterReaderActivity;
import caceresenzo.apps.boxplay.activities.SearchAndGoDetailActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.dialog.WorkingProgressDialog;
import caceresenzo.apps.boxplay.helper.ViewHelper;
import caceresenzo.apps.boxplay.managers.DebugManager;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager;
import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor.VideoContentExtractorProgressCallback;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.thread.HelpedThread;

/**
 * Content page for the {@link SearchAndGoDetailActivity}
 * 
 * @author Enzo CACERES
 */
public class PageDetailContentSearchAndGoFragment extends Fragment {
	
	public static final String ACTION_STREAMING = "action.streaming";
	public static final String ACTION_DOWNLOAD = "action.download";
	
	private BoxPlayApplication boxPlayApplication;
	private Handler handler;
	private ViewHelper viewHelper;
	private SearchAndGoManager searchAndGoManager;
	private DebugManager debugManager;
	
	private boolean uiReady = false;
	
	private SearchAndGoResult result;
	private List<AdditionalResultData> contents = new ArrayList<>();
	
	private RecyclerView recyclerView;
	private ProgressBar progressBar;
	
	private ContentViewAdapter adapter;
	
	private WorkingProgressDialog progressDialog;
	
	private VideoExtractionWorker videoExtractionWorker;
	
	public PageDetailContentSearchAndGoFragment() {
		this.boxPlayApplication = BoxPlayApplication.getBoxPlayApplication();
		this.handler = BoxPlayApplication.getHandler();
		this.viewHelper = BoxPlayApplication.getViewHelper();
		this.searchAndGoManager = BoxPlayApplication.getManagers().getSearchAndGoManager();
		this.debugManager = BoxPlayApplication.getManagers().getDebugManager();
		
		this.videoExtractionWorker = new VideoExtractionWorker();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_culture_searchngo_activitypage_details, container, false);
		
		this.progressDialog = WorkingProgressDialog.create(SearchAndGoDetailActivity.getSearchAndGoDetailActivity());
		
		progressBar = (ProgressBar) view.findViewById(R.id.fragment_culture_searchngo_activitypage_details_progressbar_loading);
		
		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_culture_searchngo_activitypage_details_recyclerview_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(adapter = new ContentViewAdapter(contents));
		recyclerView.setHasFixedSize(true);
		recyclerView.setNestedScrollingEnabled(false);
		
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		
		uiReady = true;
		
		return view;
	}
	
	public void applyResult(SearchAndGoResult result, List<AdditionalResultData> additionals) {
		this.result = result;
		
		this.contents.clear();
		this.contents.addAll(additionals);
		
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			
			recyclerView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Tell if the ui has been {@link View#findViewById(int)} and all view are ready to use
	 * 
	 * @return If the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished
	 */
	public boolean isUiReady() {
		return uiReady;
	}
	
	/**
	 * View adapter for content list item
	 * 
	 * @author Enzo CACERES
	 */
	class ContentViewAdapter extends RecyclerView.Adapter<ContentViewHolder> {
		private List<AdditionalResultData> list;
		
		public ContentViewAdapter(List<AdditionalResultData> list) {
			this.list = list;
		}
		
		@Override
		public ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_culture_searchandgo_activitypage_detail_content, viewGroup, false);
			return new ContentViewHolder(view);
		}
		
		@Override
		public void onBindViewHolder(ContentViewHolder viewHolder, int position) {
			AdditionalResultData item = list.get(position);
			viewHolder.bind(item);
		}
		
		@Override
		public int getItemCount() {
			return list.size();
		}
	}
	
	/**
	 * View holder for content item
	 * 
	 * @author Enzo CACERES
	 */
	class ContentViewHolder extends RecyclerView.ViewHolder {
		private View view;
		private TextView typeTextView, contentTextView;
		private ImageView iconImageView, downloadImageView;
		
		public ContentViewHolder(View itemView) {
			super(itemView);
			
			view = itemView;
			
			typeTextView = (TextView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_textview_type);
			
			iconImageView = (ImageView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_imageview_icon);
			contentTextView = (TextView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_textview_content);
			downloadImageView = (ImageView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_imageview_download);
		}
		
		public void bind(final AdditionalResultData additionalData) {
			typeTextView.setText(viewHelper.enumToStringCacheTranslation(additionalData.getType()));
			
			int targetRessourceId;
			boolean validType = true, hideDownload = true;
			switch (additionalData.getType()) {
				case ITEM_VIDEO: {
					targetRessourceId = R.drawable.icon_video_library_light;
					hideDownload = false;
					break;
				}
				
				case ITEM_CHAPTER: {
					targetRessourceId = R.drawable.icon_books_library_light;
					break;
				}
				
				default: {
					targetRessourceId = R.drawable.icon_close;
					validType = false;
					break;
				}
			}
			
			iconImageView.setImageResource(targetRessourceId);
			contentTextView.setText(additionalData.convert());
			downloadImageView.setVisibility(hideDownload ? View.GONE : View.VISIBLE);
			
			if (validType) {
				OnClickListener onClickListener = new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (videoExtractionWorker.isRunning()) {
							boxPlayApplication.toast("ExtractionWorker is busy").show();
							return;
						}
						
						videoExtractionWorker = new VideoExtractionWorker();
						
						String action = ACTION_STREAMING;
						if (view.equals(downloadImageView)) {
							action = ACTION_DOWNLOAD;
						}
						
						switch (additionalData.getType()) {
							case ITEM_VIDEO: {
								videoExtractionWorker.applyData((VideoItemResultData) additionalData.getData(), action).start();
								progressDialog.show();
								break;
							}
							
							case ITEM_CHAPTER: {
								MangaChapterReaderActivity.start((ChapterItemResultData) additionalData.getData());
								break;
							}
							
							default: {
								throw new IllegalStateException(); // Impossible to reach
							}
						}
						
					}
				};
				
				view.setOnClickListener(onClickListener);
				downloadImageView.setOnClickListener(onClickListener);
			}
		}
	}
	
	/**
	 * Worker thread to extract video direct link
	 * 
	 * @author Enzo CACERES
	 */
	class VideoExtractionWorker extends HelpedThread {
		private VideoItemResultData videoItem;
		private String action;
		
		private IVideoContentProvider videoContentProvider;
		private VideoContentExtractor extractor;
		
		private String directUrl;
		
		@Override
		protected void onRun() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					progressDialog.update(R.string.boxplay_culture_searchngo_extractor_status_downloading_video_site_url, videoItem.getUrl());
				}
			});
			
			try {
				directUrl = extractor.extractDirectVideoUrl(videoContentProvider.extractVideoPageUrl(videoItem), new VideoContentExtractorProgressCallback() {
					@Override
					public void onDownloadingUrl(final String targetUrl) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog.update(R.string.boxplay_culture_searchngo_extractor_status_downloading_url, targetUrl);
							}
						});
					}
					
					@Override
					public void onFileNotAvailable() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								boxPlayApplication.toast(R.string.boxplay_culture_searchngo_extractor_status_file_not_available).show();
							}
						});
					}
					
					@Override
					public void onExtractingLink() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog.update(R.string.boxplay_culture_searchngo_extractor_status_extracting_link);
							}
						});
					}
					
					@Override
					public void onFormattingResult() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog.update(R.string.boxplay_culture_searchngo_extractor_status_formatting_result);
							}
						});
					}
				});
				
				switch (action) {
					case ACTION_STREAMING: {
						BoxPlayApplication.getManagers().getVideoManager().openVLC(directUrl, result.getName() + "\n" + videoItem.getName());
						break;
					}
					
					case ACTION_DOWNLOAD: {
						handler.post(new Runnable() {
							@Override
							public void run() {
								String filename = String.format("%s %s.mp4", FileUtils.remplaceIllegalChar(result.getName()), videoItem.getName());
								Log.e(getClass().getSimpleName(), "Downloading file: " + filename);
								AndroidDownloader.askDownload(boxPlayApplication, Uri.parse(directUrl), filename);
							}
						});
						break;
					}
					
					default: {
						throw new IllegalStateException();
					}
				}
			} catch (Exception exception) {
				extractor.notifyException(exception);
				BoxPlayApplication.getBoxPlayApplication().toast(R.string.boxplay_culture_searchngo_extractor_error_failed_to_extract, exception.getLocalizedMessage());
			}
			
			if (debugManager.openLogsAtExtractorEnd()) {
				DialogUtils.showDialog(BoxPlayApplication.getHandler(), getContext(), "Extraction logs", extractor.getLogger().getContent());
			}
		}
		
		@Override
		protected void onFinished() {
			closeDialog();
		}
		
		@Override
		protected void onCancelled() {
			closeDialog();
		}
		
		private void closeDialog() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					progressDialog.update("");
					progressDialog.hide();
				}
			});
		}
		
		/**
		 * Initialize worker thread local data
		 * 
		 * @param videoItem
		 *            Target {@link VideoItemResultData} that will be extracted
		 * @param action
		 * @return Itself, now call {@link #start()}
		 */
		public VideoExtractionWorker applyData(VideoItemResultData videoItem, String action) {
			this.videoItem = videoItem;
			this.action = action;
			
			videoContentProvider = videoItem.getVideoContentProvider();
			extractor = searchAndGoManager.createVideoExtractorFromCompatible(videoContentProvider.getCompatibleExtractorClass());
			
			return this;
		}
	}
	
}