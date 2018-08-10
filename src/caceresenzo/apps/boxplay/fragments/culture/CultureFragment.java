package caceresenzo.apps.boxplay.fragments.culture;

import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.fragments.BaseTabLayoutFragment;
import caceresenzo.apps.boxplay.fragments.culture.searchngo.PageCultureSearchAndGoDetailFragment;
import caceresenzo.apps.boxplay.fragments.culture.searchngo.PageCultureSearchAndGoFragment;

public class CultureFragment extends BaseTabLayoutFragment {
	
	public static final int PAGE_SEARCHANDGO = 0;
	public static final int PAGE_SEARCHANDGO_DETAIL = 1;
	
	@Override
	protected void initialize() {
		addFragment(new PageCultureSearchAndGoFragment(), R.string.boxplay_culture_searchngo_title);
		addFragment(new PageCultureSearchAndGoDetailFragment(), "detail");
	}
	
	@Override
	protected int getMenuItemIdByPageId(int pageId) {
		switch (pageId) {
			default:
			case PAGE_SEARCHANDGO: {
				return R.id.drawer_boxplay_culture_searchngo;
			}
			
			case PAGE_SEARCHANDGO_DETAIL: {
				return R.id.drawer_boxplay_culture_searchngo;
			}
		}
	}
	
	public CultureFragment withSearchAndGo() {
		return (CultureFragment) withPage(PAGE_SEARCHANDGO);
	}
	
	public CultureFragment withSearchAndGoDetail() {
		return (CultureFragment) withPage(PAGE_SEARCHANDGO_DETAIL);
	}
	
	public static CultureFragment getCultureFragment() {
		return (CultureFragment) INSTANCE;
	}
	
}