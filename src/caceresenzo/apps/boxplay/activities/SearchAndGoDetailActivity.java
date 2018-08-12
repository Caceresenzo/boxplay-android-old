package caceresenzo.apps.boxplay.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.BaseViewPagerAdapter;
import caceresenzo.apps.boxplay.fragments.culture.searchngo.detailpage.PageDetailContentSearchAndGoFragment;
import caceresenzo.apps.boxplay.fragments.culture.searchngo.detailpage.PageDetailInfoSearchAndGoFragment;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.thread.HelpedThread;
import caceresenzo.libs.thread.ThreadUtils;

public class SearchAndGoDetailActivity extends AppCompatActivity {
	
	private static SearchAndGoDetailActivity INSTANCE;
	private static SearchAndGoResult RESULT;
	
	private BoxPlayActivity boxPlayActivity;
	
	private Toolbar toolbar;
	private ActionBar actionBar;
	
	private TabLayout tabLayout;
	private ViewPager viewPager;
	
	private BaseViewPagerAdapter adapter;
	
	private PageDetailInfoSearchAndGoFragment infoFragment;
	private PageDetailContentSearchAndGoFragment contentFragment;
	
	private Worker worker;
	
	public SearchAndGoDetailActivity() {
		INSTANCE = this;
		
		boxPlayActivity = BoxPlayActivity.getBoxPlayActivity();
		
		worker = new Worker();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchandgo_detail);
		INSTANCE = this;
		
		if (RESULT == null) {
			if (boxPlayActivity != null) {
				boxPlayActivity.toast(getString(R.string.boxplay_error_activity_invalid_data)).show();
			}
			finish();
		}
		
		initializeStrings();
		
		initializeViews();
		
		displayResult();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		INSTANCE = null;
		
		infoFragment = null;
		contentFragment = null;
		
		worker.cancel();
	}
	
	private void initializeStrings() {
		;
	}
	
	private void initializeViews() {
		toolbar = (Toolbar) findViewById(R.id.activity_searchandgo_detail_toolbar_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		tabLayout = (TabLayout) findViewById(R.id.activity_searchandgo_detail_tablayout_container);
		viewPager = (ViewPager) findViewById(R.id.activity_searchandgo_detail_viewpager_container);
		
		adapter = new BaseViewPagerAdapter(getSupportFragmentManager());
		
		adapter.addFragment(infoFragment = new PageDetailInfoSearchAndGoFragment(), "INFO");
		adapter.addFragment(contentFragment = new PageDetailContentSearchAndGoFragment(), "CONTENT");
		
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		
		tabLayout.setupWithViewPager(viewPager);
	}
	
	private void displayResult() {
		actionBar.setTitle(RESULT.getName());
		worker.applyResult(RESULT).start();
	}
	
	public static void start(SearchAndGoResult result) {
		SearchAndGoDetailActivity.RESULT = result;
		
		BoxPlayApplication application = BoxPlayApplication.getBoxPlayApplication();
		
		application.startActivity(new Intent(application, SearchAndGoDetailActivity.class));
	}
	
	class Worker extends HelpedThread {
		private final SearchAndGoDetailActivity parentActivity;
		private SearchAndGoResult result;
		
		private List<AdditionalResultData> additionals;
		private List<AdditionalResultData> contents;
		
		public Worker() {
			this.parentActivity = INSTANCE;
		}
		
		@Override
		protected void onRun() {
			SearchAndGoProvider provider = result.getParentProvider();
			
			additionals = provider.fetchMoreData(result);
			contents = provider.fetchContent(result);
		}
		
		@Override
		protected void onFinished() {
			if (parentActivity != INSTANCE) {
				return;
			}
			
			if (additionals != null) {
				if (infoFragment == null) {
					return;
				}
				
				while (!infoFragment.isUiReady()) {
					ThreadUtils.sleep(20L);
				}
				
				BoxPlayActivity.getHandler().post(new Runnable() {
					@Override
					public void run() {
						infoFragment.applyResult(RESULT, additionals);
					}
				});
			}
			
			if (contents != null) {
				if (infoFragment == null) {
					return;
				}
				
				while (!contentFragment.isUiReady()) {
					ThreadUtils.sleep(20L);
				}
				
				BoxPlayActivity.getHandler().post(new Runnable() {
					@Override
					public void run() {
						contentFragment.applyResult(RESULT, contents);
					}
				});
			}
		}
		
		@Override
		protected void onCancelled() {
			;
		}
		
		public Worker applyResult(SearchAndGoResult result) {
			this.result = result;
			return this;
		}
	}
	
}