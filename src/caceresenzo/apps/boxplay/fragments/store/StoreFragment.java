package caceresenzo.apps.boxplay.fragments.store;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.fragments.ViewPagerAdapter;

public class StoreFragment extends Fragment {
	
	public static final int PAGE_VIDEO = 0;
	public static final int PAGE_MUSIC = 1;
	public static final int PAGE_FILES = 2;
	
	private static StoreFragment INSTANCE;
	
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private OnPageChangeListener onPageChangeListener;
	private int onOpenPageId = 0, lastOpenPosition = 0;
	
	public StoreFragment() {
		;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_store, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		INSTANCE = this;
		
		if (savedInstanceState == null) {
			viewPager = (ViewPager) getView().findViewById(R.id.fragment_store_viewpager_container);
			setViewPager(viewPager);
			
			tabLayout = (TabLayout) getView().findViewById(R.id.fragment_store_tablayout_container);
			tabLayout.setupWithViewPager(viewPager);
			// setIcons();
		}
		
		if (viewPager != null) {
			viewPager.setCurrentItem(onOpenPageId, true);
		}
	}
	
	private void setViewPager(final ViewPager viewPager) {
		adapter = new ViewPagerAdapter(getChildFragmentManager());
		
		adapter.addFragment(new PageVideoStoreFragment(), getString(R.string.boxplay_store_video_video));
		// adapter.addFragment(new PageVideoStoreFragment(), "");
		adapter.addFragment(new PageMusicStoreFragment(), getString(R.string.boxplay_store_music_music));
		// adapter.addFragment(new PageMusicStoreFragment(), "");
		// adapter.addFragment(new FeatureFragment(), getString(R.string.boxplay_store_files_files));
		// adapter.addFragment(new FeatureFragment(), "");
		
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(10);
		
		onPageChangeListener = new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				lastOpenPosition = position;
				
				BoxPlayActivity.getViewHelper().unselectAllMenu();
				BoxPlayActivity.getViewHelper().updateSeachMenu(position);
				
				switch (position) {
					default:
					case PAGE_VIDEO: {
						BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_store_video).setChecked(true);
						break;
					}
					case PAGE_MUSIC: {
						BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_store_music).setChecked(true);
						break;
					}
					// case PAGE_FILES:
					// BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_store_files).setChecked(true);
					// break;
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				;
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				;
			}
		};
		
		viewPager.addOnPageChangeListener(onPageChangeListener);
	}
	
	// private void setIcons() {
	// tabLayout.getTabAt(PAGE_VIDEO).setIcon(R.drawable.icon_video_96px_q);
	// tabLayout.getTabAt(PAGE_MUSIC).setIcon(R.drawable.icon_music_96px_q);
	// tabLayout.getTabAt(PAGE_FILES).setIcon(R.drawable.icon_file_96px);
	// }
	
	public Fragment getActualFragment() {
		return adapter.getItem(lastOpenPosition);
	}
	
	public StoreFragment withVideo() {
		return withPage(PAGE_VIDEO);
	}
	
	public StoreFragment withMusic() {
		return withPage(PAGE_MUSIC);
	}
	
	public StoreFragment withFiles() {
		return withPage(PAGE_FILES);
	}
	
	public StoreFragment withPage(int pageId) {
		if (viewPager == null) {
			this.onOpenPageId = pageId;
		} else {
			viewPager.setCurrentItem(pageId);
		}
		return this;
	}
	
	public static StoreFragment getStoreFragment() {
		return INSTANCE;
	}
	
}