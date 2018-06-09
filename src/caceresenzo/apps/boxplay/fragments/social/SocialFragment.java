package caceresenzo.apps.boxplay.fragments.social;

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
import caceresenzo.apps.boxplay.fragments.utils.PlaceholderFragment;

public class SocialFragment extends Fragment {
	
	public static final int PAGE_FEED = 0;
	public static final int PAGE_FRIEND = 1;
	public static final int PAGE_CHAT = 2;
	
	private static SocialFragment INSTANCE;
	
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private OnPageChangeListener onPageChangeListener;
	private int onOpenPageId = 0, lastOpenPosition = 0;
	
	public SocialFragment() {
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
		}
		
		if (viewPager != null) {
			viewPager.setCurrentItem(onOpenPageId, true);
		}
	}
	
	private void setViewPager(final ViewPager viewPager) {
		adapter = new ViewPagerAdapter(getChildFragmentManager());
		
		// adapter.addFragment(new PageVideoStoreFragment(), getString(R.string.boxplay_store_video_video));
		// adapter.addFragment(new PageMusicStoreFragment(), getString(R.string.boxplay_store_music_music));
		adapter.addFragment(new PlaceholderFragment(), "FEEDS");
		adapter.addFragment(new PlaceholderFragment(), "FRIENDS");
		adapter.addFragment(new PlaceholderFragment(), "CHATS");
		
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
					case PAGE_FEED: {
						BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_connect_feed).setChecked(true);
						break;
					}
					case PAGE_FRIEND: {
						BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_connect_friends).setChecked(true);
						break;
					}
					case PAGE_CHAT: {
						BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_connect_chat).setChecked(true);
						break;
					}
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
	
	public Fragment getActualFragment() {
		return adapter.getItem(lastOpenPosition);
	}
	
	public SocialFragment withFeed() {
		return withPage(PAGE_FEED);
	}
	
	public SocialFragment withFriend() {
		return withPage(PAGE_FRIEND);
	}
	
	public SocialFragment withChat() {
		return withPage(PAGE_CHAT);
	}
	
	public SocialFragment withPage(int pageId) {
		if (viewPager == null) {
			this.onOpenPageId = pageId;
		} else {
			viewPager.setCurrentItem(pageId);
		}
		return this;
	}
	
	public static SocialFragment getSocialFragment() {
		return INSTANCE;
	}
	
}