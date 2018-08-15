package caceresenzo.apps.boxplay.activities;

import java.util.ArrayList;
import java.util.List;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.culture.CultureFragment;
import caceresenzo.apps.boxplay.fragments.other.SettingsFragment;
import caceresenzo.apps.boxplay.fragments.other.about.AboutFragment;
import caceresenzo.apps.boxplay.fragments.premium.adult.AdultExplorerFragment;
import caceresenzo.apps.boxplay.fragments.social.SocialFragment;
import caceresenzo.apps.boxplay.fragments.store.StoreFragment;
import caceresenzo.apps.boxplay.fragments.store.StorePageFragment;
import caceresenzo.apps.boxplay.helper.LocaleHelper;
import caceresenzo.apps.boxplay.helper.ViewHelper;
import caceresenzo.apps.boxplay.managers.TutorialManager.Tutorialable;
import caceresenzo.apps.boxplay.managers.XManagers;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

/**
 * Main BoxPlay class
 * 
 * @author Enzo CACERES
 */
public class BoxPlayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Tutorialable {
	
	public static final boolean BUILD_DEBUG = false;
	
	/**
	 * Tutorial path id
	 */
	public static final int TUTORIAL_PROGRESS_DRAWER = 0;
	public static final int TUTORIAL_PROGRESS_SEARCH = 1;
	public static final int TUTORIAL_PROGRESS_MENU = 2;
	public static final int TUTORIAL_PROGRESS_SWIPE = 3;
	public static final int TUTORIAL_PROGRESS_VIDEO = 4;
	public static final int TUTORIAL_PROGRESS_MUSIC = 5;
	
	private static BoxPlayActivity INSTANCE;
	private static Handler HANDLER = new Handler();
	private static XManagers MANAGERS = new XManagers();
	private static ViewHelper HELPER = new ViewHelper();
	
	private Toolbar toolbar;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private NavigationView navigationView;
	private CoordinatorLayout coordinatorLayout;
	private Menu optionsMenu;
	
	private SlidingUpPanelLayout slidingUpPanelLayout;
	
	private static Fragment fragmentToOpen = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boxplay);
		INSTANCE = this;
		
		initializeViews();
		initializeSystems();
		
		MANAGERS.initialize(this);
		
		MANAGERS.getMusicManager().registerMusicSlidingPanel(slidingUpPanelLayout);
		
		// if (savedInstanceState == null) {
		// showFragment(new PlaceholderFragment());
		// }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (fragmentToOpen != null) {
			HANDLER.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (fragmentToOpen instanceof SettingsFragment) {
						((SettingsFragment) fragmentToOpen).reset();
						forceFragmentPath(R.id.drawer_boxplay_other_settings);
						
						HANDLER.postDelayed(new Runnable() {
							@Override
							public void run() {
								((SettingsFragment) fragmentToOpen).scrollToPreference(getString(R.string.boxplay_other_settings_application_pref_language_key));
								fragmentToOpen = null;
							}
						}, 200);
					}
					
					showFragment(fragmentToOpen);
				}
			}, 200);
		}
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base));
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		onNavigationItemSelected(navigationView.getMenu().findItem(R.id.drawer_boxplay_store_video));
		navigationView.getMenu().findItem(R.id.drawer_boxplay_store_video).setChecked(true);
		
		// getManagers().getUpdateManager().debugForceFirstTimeInstalled();
		if (MANAGERS.getUpdateManager().isFirstTimeInstalled()) {
			HANDLER.postDelayed(new Runnable() {
				@Override
				public void run() {
					getManagers().getTutorialManager().executeActivityTutorial(BoxPlayActivity.this);
				}
			}, 1000);
		} else {
			if (MANAGERS.getUpdateManager().isFirstRunOnThisUpdate()) {
				HANDLER.postDelayed(new Runnable() {
					@Override
					public void run() {
						HELPER.updateSeachMenu(R.id.drawer_boxplay_other_about);
						showFragment(new AboutFragment().withChangeLog());
					}
					
				}, 3000);
			}
		}
		MANAGERS.getUpdateManager().saveUpdateVersion();
	}
	
	@Override
	protected void onStop() {
		// MANAGERS.getMusicManager().saveDatabase();
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		if (MANAGERS.getUpdateManager().isFirstTimeInstalled()) {
			MANAGERS.getUpdateManager().updateFirstTimeInstalled();
		}
		
		MANAGERS.getMusicManager().saveDatabase();
		
		MANAGERS.destroy();
		
		super.onDestroy();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			case BoxPlayApplication.REQUEST_ID_VLC_VIDEO: {
				VideoActivity videoActivity = VideoActivity.getVideoActivity();
				if (videoActivity != null) {
					videoActivity.onActivityResult(requestCode, resultCode, data);
				}
				break;
			}
		}
	}
	
	/**
	 * Asking Activity to recreate without any specific fragment to open after recreation
	 */
	public void askRecreate() {
		askRecreate(null);
	}
	
	/**
	 * Asking Activity to recreate but with a specific fragment to open after recreation
	 * 
	 * If oldFrangent is null, default fragment will be open
	 * 
	 * @param oldFrangent
	 *            Target fragment
	 */
	public void askRecreate(Fragment oldFrangent) {
		fragmentToOpen = oldFrangent;
		recreate();
	}
	
	/**
	 * Function to initialize views
	 */
	private void initializeViews() {
		toolbar = (Toolbar) findViewById(R.id.activity_boxplay_toolbar_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setElevation(0);
		
		drawer = (DrawerLayout) findViewById(R.id.activity_boxplay_drawerlayout_container);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(actionBarDrawerToggle);
		actionBarDrawerToggle.syncState();
		
		navigationView = (NavigationView) findViewById(R.id.activity_boxplay_navigationview_container);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.getMenu().getItem(0).setChecked(true);
		
		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_boxplay_coordinatorlayout_container);
		
		slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_boxplay_slidinglayout_container);
	}
	
	/**
	 * Function to initialize sub-systems, like cache
	 */
	private void initializeSystems() {
		HELPER.prepareCache(this);
	}
	
	private void initializeDebug() {
		if (!BUILD_DEBUG) {
			return;
		}
		
		optionsMenu.findItem(R.id.menu_main_action_debug).setVisible(true);
		
		HANDLER.postDelayed(new Runnable() {
			@Override
			public void run() {
				// showFragment(new CultureFragment().withSearchAndGo());
			}
		}, 200);
	}
	
	private void onDebugClick(MenuItem menuItem) {
		// startActivity(new Intent(this, VideoPlayerActivity.class));
		
		// Manga
		SearchAndGoDetailActivity.start(new SearchAndGoResult(ProviderManager.MANGALEL.create(), //
				"Arifureta Shokugyou de Sekai Saikyou", //
				"https://www.manga-lel.com/manga/arifureta-shokugyou-de-sekai-saikyou/", //
				"https://www.manga-lel.com//uploads/manga/arifureta-shokugyou-de-sekai-saikyou/cover/cover_250x350.jpg")); //
		
		// MangaChapterReaderActivity.start(null);
		
		// Anime
		// SearchAndGoDetailActivity.start(new SearchAndGoResult(ProviderManager.JETANIME.create(), //
		// "Death March Kara Hajimaru Isekai Kyousoukyoku", //
		// "https://www.jetanime.co/anime/death-march-kara-hajimaru-isekai-kyousoukyoku/", //
		// "https://www.jetanime.co/assets/imgs/death-march-kara-hajimaru-isekai-kyousoukyoku.jpg")); //
	}
	
	/**
	 * Used to show the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (menu instanceof MenuBuilder) {
			((MenuBuilder) menu).setOptionalIconsVisible(true);
		}
		
		getMenuInflater().inflate(R.menu.main, menu);
		optionsMenu = menu;
		
		initializeDebug();
		
		return true;
	}
	
	/**
	 * Function call when someone clicked on main menu item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		// Fragment lastFrament = HELPER.getLastFragment();
		
		switch (id) {
			case R.id.menu_main_action_update: {
				MANAGERS.getUpdateManager().showDialog();
				return true;
			}
			
			case R.id.menu_main_action_search: {
				// if (lastFrament != null && lastFrament instanceof StoreFragment) {
				// return ((StorePageFragment) StoreFragment.getStoreFragment().getActualFragment()).onOptionsItemSelected(item);
				// }
				StorePageFragment.handleSearch(item);
				break;
			}
			
			case R.id.menu_main_action_debug: {
				onDebugClick(item);
				break;
			}
			
			default: {
				toast("Unhandled onOptionsItemSelected(item.getTitle() = \"" + item.getTitle() + "\");");
				break;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Called when someone press the back button
	 * 
	 * Added a custom behavior:
	 * 
	 * If someone has the option to open/collapse the drawer menu with the back button, application will never stop
	 * 
	 * If someone don't have this option, and the drawer is already close, the application will quit
	 */
	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED) && !slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.HIDDEN)) {
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			} else {
				if (BoxPlayApplication.getBoxPlayApplication().getPreferences().getBoolean(getString(R.string.boxplay_other_settings_menu_pref_drawer_extend_collapse_back_button_key), true)) {
					drawer.openDrawer(GravityCompat.START);
				} else {
					super.onBackPressed();
				}
			}
		}
	}
	
	/**
	 * Force a new selected item for the drawer
	 * 
	 * @param id
	 *            Correspond to the id of the menu, if don't exists, nothing will append
	 */
	public void forceFragmentPath(int id) {
		MenuItem targetItem = navigationView.getMenu().findItem(id);
		
		if (targetItem != null) {
			onNavigationItemSelected(navigationView.getMenu().findItem(id));
		}
	}
	
	/**
	 * Drawer function, called when a item has been clicked
	 */
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();
		Fragment actualFragment = HELPER.getLastFragment();
		
		if (item.isCheckable()) {
			HELPER.unselectAllMenu();
		}
		item.setChecked(true);
		HELPER.updateSeachMenu(id);
		
		switch (id) {
			/*
			 * Store
			 */
			case R.id.drawer_boxplay_store_video:
			case R.id.drawer_boxplay_store_music: {
				StoreFragment storeFragment;
				
				if (actualFragment instanceof StoreFragment) {
					storeFragment = ((StoreFragment) actualFragment);
				} else {
					storeFragment = new StoreFragment();
				}
				
				switch (id) {
					default:
					case R.id.drawer_boxplay_store_video: {
						storeFragment.withVideo();
						break;
					}
					case R.id.drawer_boxplay_store_music: {
						storeFragment.withMusic();
						break;
					}
				}
				
				showFragment(storeFragment);
				break;
			}
			
			/*
			 * Social
			 */
			case R.id.drawer_boxplay_connect_feed:
			case R.id.drawer_boxplay_connect_friends:
			case R.id.drawer_boxplay_connect_chat: {
				SocialFragment socialFragment;
				
				if (actualFragment instanceof SocialFragment) {
					socialFragment = ((SocialFragment) actualFragment);
				} else {
					socialFragment = new SocialFragment();
				}
				
				switch (id) {
					default:
					case R.id.drawer_boxplay_connect_feed: {
						socialFragment.withFeed();
						break;
					}
					case R.id.drawer_boxplay_connect_friends: {
						socialFragment.withFriend();
						break;
					}
					case R.id.drawer_boxplay_connect_chat: {
						socialFragment.withChat();
						break;
					}
				}
				
				showFragment(socialFragment);
				break;
			}
			
			/*
			 * Culture
			 */
			case R.id.drawer_boxplay_culture_searchngo: {
				CultureFragment cultureFragment;
				
				if (actualFragment instanceof CultureFragment) {
					cultureFragment = ((CultureFragment) actualFragment);
				} else {
					cultureFragment = new CultureFragment();
				}
				
				switch (id) {
					default:
					case R.id.drawer_boxplay_culture_searchngo: {
						cultureFragment.withSearchAndGo();
						break;
					}
				}
				
				showFragment(cultureFragment);
				break;
			}
			
			/*
			 * Premium
			 */
			// TODO: Do PremiumFragment instead of AdultExplorerFragment
			case R.id.drawer_boxplay_premium_adult: {
				if (MANAGERS.getPremiumManager().isPremiumKeyValid()) {
					showFragment(new AdultExplorerFragment());
				} else {
					MANAGERS.getPremiumManager().updateLicence(null);
					showFragment(actualFragment);
				}
				
				break;
			}
			
			/*
			 * Settings
			 */
			case R.id.drawer_boxplay_other_settings: {
				showFragment(new SettingsFragment());
				break;
			}
			
			/*
			 * About
			 */
			case R.id.drawer_boxplay_other_about: {
				showFragment(new AboutFragment());
				break;
			}
			
			/*
			 * Default
			 */
			default: {
				toast("Unhandled onNavigationItemSelected(item.getTitle() = \"" + item.getTitle() + "\");");
				return false;
			}
		}
		
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
	
	/**
	 * Function used to fill the main {@link FrameLayout} of the application with a fragment instance
	 * 
	 * @param fragment
	 *            The new fragment
	 */
	public void showFragment(Fragment fragment) {
		if (fragment == null) {
			return;
		}
		try {
			FragmentManager fragmentManager = getSupportFragmentManager();
			
			fragmentManager //
					.beginTransaction() //
					.replace(R.id.activity_boxplay_framelayout_container_main, fragment) //
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) //
					.commit() //
			;
			
			HELPER.setLastFragment(fragment);
		} catch (Exception exception) {
			fragmentToOpen = fragment;
		}
	}
	
	/**
	 * Tutorialable, used to create the tutorial path
	 */
	@SuppressWarnings("deprecation")
	@Override
	public TapTargetSequence getTapTargetSequence() {
		List<TapTarget> sequences = new ArrayList<TapTarget>();
		Display display = getWindowManager().getDefaultDisplay();
		
		Rect storeRectangle = new Rect(24, 24, 24, 24);
		storeRectangle.offsetTo(display.getWidth() / 2, display.getHeight() / 2);
		
		sequences.add(applyTutorialObjectTheme( //
				TapTarget.forToolbarNavigationIcon(toolbar, getString(R.string.boxplay_tutorial_main_drawer_title), getString(R.string.boxplay_tutorial_main_drawer_description)) //
						.id(TUTORIAL_PROGRESS_DRAWER) //
		)); //
		
		sequences.add(applyTutorialObjectTheme( //
				TapTarget.forToolbarMenuItem(toolbar, R.id.menu_main_action_search, getString(R.string.boxplay_tutorial_main_search_title), getString(R.string.boxplay_tutorial_main_search_description)) //
						.id(TUTORIAL_PROGRESS_SEARCH) //
		)); //
		
		sequences.add(applyTutorialObjectTheme( //
				TapTarget.forToolbarOverflow(toolbar, getString(R.string.boxplay_tutorial_main_options_title), getString(R.string.boxplay_tutorial_main_options_description)) //
						.id(TUTORIAL_PROGRESS_MENU))); //
		
		sequences.add(applyTutorialObjectTheme(TapTarget.forBounds(storeRectangle, getString(R.string.boxplay_tutorial_main_swipe_title), getString(R.string.boxplay_tutorial_main_swipe_description)) //
				.id(TUTORIAL_PROGRESS_SWIPE) //
				.icon(getResources().getDrawable(R.mipmap.image_hand_swipe)) //
		)); //
		
		Fragment lastFragment = HELPER.getLastFragment();
		if (lastFragment instanceof StoreFragment) {
			sequences.add(applyTutorialObjectTheme( //
					TapTarget.forBounds(storeRectangle, getString(R.string.boxplay_tutorial_main_video_title), getString(R.string.boxplay_tutorial_main_video_description)) //
							.id(TUTORIAL_PROGRESS_VIDEO) //
							.outerCircleAlpha(0.8F) //
			) //
					.dimColor(android.R.color.transparent) //
					.targetCircleColor(android.R.color.transparent) //
			); //
			
			sequences.add(applyTutorialObjectTheme( //
					TapTarget.forBounds(storeRectangle, getString(R.string.boxplay_tutorial_main_music_title), getString(R.string.boxplay_tutorial_main_music_description)) //
							.id(TUTORIAL_PROGRESS_MUSIC) //
							.outerCircleAlpha(0.8F) //
			) //
					.dimColor(android.R.color.transparent) //
					.targetCircleColor(android.R.color.transparent) //
			); //
		}
		
		return new TapTargetSequence(this) //
				.targets(sequences).listener(new TapTargetSequence.Listener() {
					@Override
					public void onSequenceFinish() {
						Fragment lastFragment = HELPER.getLastFragment();
						if (lastFragment instanceof StoreFragment) {
							((StoreFragment) lastFragment).withVideo();
						}
						
						MANAGERS.getTutorialManager().saveTutorialFinished(BoxPlayActivity.this);
					}
					
					@Override
					public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
						int id = lastTarget.id() + 1;
						
						switch (id) {
							case TUTORIAL_PROGRESS_VIDEO:
							case TUTORIAL_PROGRESS_MUSIC: {
								Fragment lastFragment = HELPER.getLastFragment();
								
								if (lastFragment instanceof StoreFragment) {
									switch (id) {
										case TUTORIAL_PROGRESS_VIDEO: {
											((StoreFragment) lastFragment).withVideo();
											break;
										}
										
										case TUTORIAL_PROGRESS_MUSIC: {
											((StoreFragment) lastFragment).withMusic();
											break;
										}
									}
								}
								
								break;
							}
						}
					}
					
					@Override
					public void onSequenceCanceled(TapTarget lastTarget) {
						; // Impossible
					}
				});
	}
	
	/**
	 * Quick function to apply tutorial common theme, reduce code
	 * 
	 * @param tapTarget
	 *            Actual sequence
	 * @return The actual sequence, but with common theme applied
	 */
	private TapTarget applyTutorialObjectTheme(TapTarget tapTarget) {
		return tapTarget.dimColor(android.R.color.black) // Background
				.outerCircleColor(R.color.colorAccent) // Big circle
				.targetCircleColor(R.color.colorPrimary) // Moving circle color (animation)
				.textColor(android.R.color.black) //
				.transparentTarget(true) //
				.cancelable(false); //
	}
	
	public Snackbar snackbar(String text, int duration) {
		return Snackbar.make(coordinatorLayout, text, duration);
	}
	
	public Snackbar snackbar(int ressourceId, int duration, Object... args) {
		return Snackbar.make(coordinatorLayout, getString(ressourceId, args), duration);
	}
	
	public StyleableToast toast(String string) {
		return StyleableToast.makeText(this, string, R.style.customStylableToastStyle);
	}
	
	public StyleableToast toast(int ressourceId, Object... args) {
		return StyleableToast.makeText(this, getString(ressourceId, args), Toast.LENGTH_LONG, R.style.customStylableToastStyle);
	}
	
	public Toolbar getToolbar() {
		return toolbar;
	}
	
	public DrawerLayout getDrawer() {
		return drawer;
	}
	
	public ActionBarDrawerToggle getActionBarDrawerToggle() {
		return actionBarDrawerToggle;
	}
	
	public NavigationView getNavigationView() {
		return navigationView;
	}
	
	public CoordinatorLayout getCoordinatorLayout() {
		return coordinatorLayout;
	}
	
	public Menu getOptionsMenu() {
		return optionsMenu;
	}
	
	/**
	 * Get the {@link ViewHelper} instance
	 */
	public static ViewHelper getViewHelper() {
		return HELPER;
	}
	
	/**
	 * Get the {@link XManagers} instance
	 */
	public static XManagers getManagers() {
		return MANAGERS;
	}
	
	/**
	 * Get the main {@link Handler} instance
	 */
	public static Handler getHandler() {
		return HANDLER;
	}
	
	/**
	 * Get the activity instance
	 */
	public static BoxPlayActivity getBoxPlayActivity() {		
		return INSTANCE;
	}
	
}
