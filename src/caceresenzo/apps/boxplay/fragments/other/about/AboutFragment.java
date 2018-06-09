package caceresenzo.apps.boxplay.fragments.other.about;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.fragments.ViewPagerAdapter;
import caceresenzo.apps.boxplay.fragments.utils.ViewFragment;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {
	
	public static final int PAGE_ABOUT = 0;
	public static final int PAGE_HOSTING = 1;
	public static final int PAGE_CHANGELOG = 2;
	public static final int PAGE_LIBRARIES = 3;
	
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	
	private int onOpenPageId = 0;
	
	public AboutFragment() {
		;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null) {
			viewPager = (ViewPager) getView().findViewById(R.id.fragment_about_viewpager_container);
			setViewPager(viewPager);
			
			tabLayout = (TabLayout) getView().findViewById(R.id.fragment_about_tablayout_container);
			tabLayout.setupWithViewPager(viewPager);
		}
		
		if (viewPager != null) {
			viewPager.setCurrentItem(onOpenPageId, true);
		}
	}
	
	private void setViewPager(ViewPager viewPager) {
		adapter = new ViewPagerAdapter(getChildFragmentManager());
		
		String teamFormat = getString(R.string.boxplay_other_about_group_team_format);
		View aboutView = new AboutPage(getActivity()) //
				.isRTL(false) //
				.setImage(R.drawable.icon_boxplay_easter_egg) //
				.setDescription(getString(R.string.boxplay_other_about_description)) //
				.addGroup(getString(R.string.boxplay_other_about_group_app_information)) //
				.addItem(new Element(getString(R.string.boxplay_other_about_group_app_information_version, BoxPlayActivity.getVersion().get()), null)) //
				.addGroup(getString(R.string.boxplay_other_about_group_team_programmer)) //
				.addItem(new Element(String.format(teamFormat, "Enzo CACERES, *Caceresenzo", getString(R.string.boxplay_other_about_group_team_format_type_application)), null)) //
				.addItem(new Element(String.format(teamFormat, "Fanbien SENUT--SCHAPPACHER, *TheWhoosher", getString(R.string.boxplay_other_about_group_team_format_type_api)), null)) //
				.addGroup(getString(R.string.boxplay_other_about_group_team_designer)) //
				.addItem(new Element(String.format(teamFormat, "Enzo CACERES, *Caceresenzo", getString(R.string.boxplay_other_about_group_team_format_type_ui)), null)) //
				.addItem(new Element(String.format(teamFormat, "Quentin BOTTA, *valgrebon", getString(R.string.boxplay_other_about_group_team_format_type_icons)), null)) //
				.addGroup(getString(R.string.boxplay_other_about_group_team_supporter)) //
				.addItem(new Element(String.format(teamFormat, "Jérémie BLERAUD", getString(R.string.boxplay_other_about_group_team_format_type_because)), null)) //
				.addItem(new Element(String.format(teamFormat, "Dorian HARDY, *thegostisdead", getString(R.string.boxplay_other_about_group_team_format_type_hosting)), null)) //
				.addGroup(getString(R.string.boxplay_other_about_group_team_helper)) //
				.addItem(new Element(String.format(teamFormat, "*dandar3", getString(R.string.boxplay_other_about_group_team_format_type_eclipse)), null)) //
				.create() //
		; //
		
		adapter.addFragment(new ViewFragment(aboutView), getString(R.string.boxplay_other_about_about));
		adapter.addFragment(new PageAboutHostingFragment(), getString(R.string.boxplay_other_about_hosting));
		adapter.addFragment(new PageAboutChangeLogFragment(), getString(R.string.boxplay_other_about_changelog));
		adapter.addFragment(new PageAboutLibrariesFragment(), getString(R.string.boxplay_other_about_libraries));
		
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(10);
	}
	
	public AboutFragment withAbout() {
		return withPage(PAGE_ABOUT);
	}
	
	public AboutFragment withHosting() {
		return withPage(PAGE_HOSTING);
	}
	
	public AboutFragment withChangeLog() {
		return withPage(PAGE_CHANGELOG);
	}
	
	public AboutFragment withLibraries() {
		return withPage(PAGE_LIBRARIES);
	}
	
	public AboutFragment withPage(int pageId) {
		if (viewPager == null) {
			this.onOpenPageId = pageId;
		} else {
			viewPager.setCurrentItem(pageId);
		}
		BoxPlayActivity.getViewHelper().unselectAllMenu();
		BoxPlayActivity.getBoxPlayActivity().getNavigationView().getMenu().findItem(R.id.drawer_boxplay_other_about).setChecked(true);
		return this;
	}
}