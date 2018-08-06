package caceresenzo.apps.boxplay.fragments.culture;

import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.fragments.BaseTabLayoutFragment;
import caceresenzo.apps.boxplay.fragments.culture.searchngo.SearchAndGoFragment;

public class CultureFragment extends BaseTabLayoutFragment {
	
	public static final int PAGE_SEARCHANDGO = 0;
	
	@Override
	protected void initialize() {
		addFragment(new SearchAndGoFragment(), R.string.boxplay_culture_searchngo_title);
	}
	
	@Override
	protected int getMenuItemIdByPageId(int pageId) {
		switch (pageId) {
			default:
			case PAGE_SEARCHANDGO: {
				return R.id.drawer_boxplay_culture_searchngo;
			}
		}
	}
	
	public CultureFragment withSearchAndGo() {
		return (CultureFragment) withPage(PAGE_SEARCHANDGO);
	}
	
	public static CultureFragment getCultureFragment() {
		return (CultureFragment) INSTANCE;
	}
	
}