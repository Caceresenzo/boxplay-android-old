package caceresenzo.apps.boxplay.activities;

import java.util.ArrayList;
import java.util.List;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.ViewHelper;
import caceresenzo.apps.boxplay.fragments.other.SettingsFragment;
import caceresenzo.apps.boxplay.fragments.other.about.AboutFragment;
import caceresenzo.apps.boxplay.fragments.social.SocialFragment;
import caceresenzo.apps.boxplay.fragments.store.StoreFragment;
import caceresenzo.apps.boxplay.fragments.store.StorePageFragment;
import caceresenzo.apps.boxplay.managers.TutorialManager.Tutorialable;
import caceresenzo.apps.boxplay.managers.XManagers;
import caceresenzo.libs.comparator.Version;
import caceresenzo.libs.comparator.VersionType;

public class BoxPlayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Tutorialable {
	
	public static final int REQUEST_ID_UPDATE = 20;
	public static final int REQUEST_ID_VLC_VIDEO = 40;
	public static final int REQUEST_ID_VLC_AUDIO = 41;
	public static final int REQUEST_ID_PERMISSION = 100;
	public static final String FILEPROVIDER_AUTHORITY = "caceresenzo.apps.boxplay.provider";
	
	public static final int TUTORIAL_PROGRESS_DRAWER = 0, //
			TUTORIAL_PROGRESS_SEARCH = 1, //
			TUTORIAL_PROGRESS_MENU = 2, //
			TUTORIAL_PROGRESS_SWIPE = 3, //
			TUTORIAL_PROGRESS_VIDEO = 4, //
			TUTORIAL_PROGRESS_MUSIC = 5; //
	
	private static BoxPlayActivity INSTANCE;
	private static Handler HANDLER = new Handler();
	private static XManagers MANAGERS = new XManagers();
	private static ViewHelper HELPER = new ViewHelper();
	
	private static final Version VERSION = new Version("3.0.5", VersionType.BETA);
	
	private Toolbar toolbar;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private NavigationView navigationView;
	private CoordinatorLayout coordinatorLayout;
	private Menu optionsMenu;
	
	private SlidingUpPanelLayout slidingUpPanelLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boxplay);
		INSTANCE = this;
		
		initializeViews();
		initializeSystems();
		
		MANAGERS.initialize(this) //
				.initializeConfig() //
				.initializePermission() //
				.initializeData() //
				.initializeElements() //
				.initializeUpdate() //
				.initializeTutorial() //
				.finish() //
		;
		
		MANAGERS.getMusicManager().registerMusicSlidingPanel(slidingUpPanelLayout);
		
		// if (savedInstanceState == null) {
		// showFragment(new PlaceholderFragment());
		// }
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
		
		super.onDestroy();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			case BoxPlayActivity.REQUEST_ID_VLC_VIDEO: {
				VideoActivity videoActivity = VideoActivity.getVideoActivity();
				if (videoActivity != null) {
					videoActivity.onActivityResult(requestCode, resultCode, data);
				}
				break;
			}
		}
	}
	
	private void initializeViews() {
		toolbar = (Toolbar) findViewById(R.id.activity_video_toolbar_bar);
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
	
	private void initializeSystems() {
		HELPER.prepareCache(this);
	}
	
	Rect storeRectangle = new Rect(24, 24, 24, 24);
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (menu instanceof MenuBuilder) {
			((MenuBuilder) menu).setOptionalIconsVisible(true);
		}
		getMenuInflater().inflate(R.menu.main, menu);
		optionsMenu = menu;
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		// Fragment lastFrament = HELPER.getLastFragment();
		
		switch (id) {
			case R.id.menu_main_action_update:
				MANAGERS.getUpdateManager().showDialog();
				return true;
			case R.id.menu_main_action_search:
			default:
				// if (lastFrament != null && lastFrament instanceof StoreFragment) {
				// return ((StorePageFragment) StoreFragment.getStoreFragment().getActualFragment()).onOptionsItemSelected(item);
				// }
				StorePageFragment.handleSearch(item);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
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
	
	public void forceFragmentPath(int id) {
		MenuItem targetItem = navigationView.getMenu().findItem(id);
		
		if (targetItem != null) {
			onNavigationItemSelected(navigationView.getMenu().findItem(id));
		}
	}
	
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
			// case R.id.drawer_boxplay_home:
			// showFragment(new PlaceholderFragment());
			// break;
			case R.id.drawer_boxplay_store_video:
			case R.id.drawer_boxplay_store_music:
				// case R.id.drawer_boxplay_store_files:
				StoreFragment storeFragment;
				if (actualFragment != null && actualFragment instanceof StoreFragment) {
					storeFragment = ((StoreFragment) actualFragment);
				} else {
					storeFragment = new StoreFragment();
				}
				switch (id) {
					case R.id.drawer_boxplay_store_video:
						storeFragment = (StoreFragment) storeFragment.withVideo();
						break;
					case R.id.drawer_boxplay_store_music:
						storeFragment = (StoreFragment) storeFragment.withMusic();
						break;
					// case R.id.drawer_boxplay_store_files:
					// storeFragment = (StoreFragment) storeFragment.withFiles();
					// break;
				}
				showFragment(storeFragment);
				break;
			case R.id.drawer_boxplay_connect_feed:
			case R.id.drawer_boxplay_connect_friends:
			case R.id.drawer_boxplay_connect_chat:
				SocialFragment socialFragment;
				if (actualFragment != null && actualFragment instanceof SocialFragment) {
					socialFragment = ((SocialFragment) actualFragment);
				} else {
					socialFragment = new SocialFragment();
				}
				switch (id) {
					case R.id.drawer_boxplay_connect_feed:
						socialFragment = (SocialFragment) socialFragment.withFeed();
						break;
					case R.id.drawer_boxplay_connect_friends:
						socialFragment = (SocialFragment) socialFragment.withFriend();
						break;
					case R.id.drawer_boxplay_connect_chat:
						socialFragment = (SocialFragment) socialFragment.withChat();
						break;
				}
				showFragment(socialFragment);
				break;
			case R.id.drawer_boxplay_other_settings: {
				showFragment(new SettingsFragment());
				break;
			}
			case R.id.drawer_boxplay_other_about: {
				showFragment(new AboutFragment());
				break;
			}
			default: {
				return false;
			}
		}
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
	
	private void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager //
				.beginTransaction() //
				.replace(R.id.activity_boxplay_framelayout_container_main, fragment) //
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) //
				.commit() //
		;
		HELPER.setLastFragment(fragment);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public TapTargetSequence getTapTargetSequence() {
		List<TapTarget> sequences = new ArrayList<TapTarget>();
		Display display = getWindowManager().getDefaultDisplay();
		
		Rect storeRectangle = new Rect(24, 24, 24, 24);
		storeRectangle.offsetTo(display.getWidth() / 2, display.getHeight() / 2);
		
		sequences.add( //
				TapTarget.forToolbarNavigationIcon(toolbar, getString(R.string.boxplay_tutorial_main_drawer_title), getString(R.string.boxplay_tutorial_main_drawer_description)) //
						.id(TUTORIAL_PROGRESS_DRAWER) //
						.dimColor(android.R.color.black) // Background
						.outerCircleColor(R.color.green) // Big circle
						.targetCircleColor(R.color.dark_blue) // Moving circle color (animation)
						.textColor(android.R.color.black) //
						.transparentTarget(true) //
						.cancelable(false) //
		); //
		
		sequences.add( //
				TapTarget.forToolbarMenuItem(toolbar, R.id.menu_main_action_search, getString(R.string.boxplay_tutorial_main_search_title), getString(R.string.boxplay_tutorial_main_search_description)) //
						.id(TUTORIAL_PROGRESS_SEARCH) //
						.dimColor(android.R.color.black) //
						.outerCircleColor(R.color.green) //
						.targetCircleColor(R.color.dark_blue) //
						.textColor(android.R.color.black) //
						.transparentTarget(true) //
						.cancelable(false) //
		); //
		
		sequences.add( //
				TapTarget.forToolbarOverflow(toolbar, getString(R.string.boxplay_tutorial_main_options_title), getString(R.string.boxplay_tutorial_main_options_description)) //
						.id(TUTORIAL_PROGRESS_MENU) //
						.dimColor(android.R.color.black) //
						.outerCircleColor(R.color.green) //
						.targetCircleColor(R.color.dark_blue) //
						.textColor(android.R.color.black) //
						.transparentTarget(true) //
						.cancelable(false) //
		); //
		
		sequences.add(TapTarget.forBounds(storeRectangle, getString(R.string.boxplay_tutorial_main_swipe_title), getString(R.string.boxplay_tutorial_main_swipe_description)) //
				.id(TUTORIAL_PROGRESS_SWIPE) //
				.icon(getResources().getDrawable(R.mipmap.image_hand_swipe)) //
				.dimColor(android.R.color.black) //
				.outerCircleColor(R.color.green) //
				.targetCircleColor(R.color.dark_blue) //
				.textColor(android.R.color.black) //
				.cancelable(false) //
		); //
		
		Fragment lastFragment = HELPER.getLastFragment();
		if (lastFragment != null && lastFragment instanceof StoreFragment) {
			sequences.add(TapTarget.forBounds(storeRectangle, getString(R.string.boxplay_tutorial_main_video_title), getString(R.string.boxplay_tutorial_main_video_description)) //
					.id(TUTORIAL_PROGRESS_VIDEO) //
					.dimColor(android.R.color.transparent) //
					.outerCircleColor(R.color.green) //
					.targetCircleColor(android.R.color.transparent) //
					.textColor(android.R.color.black) //
					.outerCircleAlpha(0.8F) //
					.transparentTarget(true) //
					.cancelable(false) //
			); //
			
			sequences.add(TapTarget.forBounds(storeRectangle, getString(R.string.boxplay_tutorial_main_music_title), getString(R.string.boxplay_tutorial_main_music_description)) //
					.id(TUTORIAL_PROGRESS_MUSIC) //
					.dimColor(android.R.color.transparent) //
					.outerCircleColor(R.color.green) //
					.targetCircleColor(android.R.color.transparent) //
					.textColor(android.R.color.black) //
					.outerCircleAlpha(0.8F) //
					.transparentTarget(true) //
					.cancelable(false) //
			); //
		}
		
		return new TapTargetSequence(this) //
				.targets(sequences).listener(new TapTargetSequence.Listener() {
					@Override
					public void onSequenceFinish() {
						Fragment lastFragment = HELPER.getLastFragment();
						if (lastFragment != null && lastFragment instanceof StoreFragment) {
							((StoreFragment) lastFragment).withVideo();
						}
						
						MANAGERS.getTutorialManager().saveTutorialFinished(BoxPlayActivity.this);
					}
					
					@Override
					public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
						int id = lastTarget.id() + 1;
						
						switch (id) {
							case TUTORIAL_PROGRESS_VIDEO:
							case TUTORIAL_PROGRESS_MUSIC:
								Fragment lastFragment = HELPER.getLastFragment();
								if (lastFragment != null && lastFragment instanceof StoreFragment) {
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
					
					@Override
					public void onSequenceCanceled(TapTarget lastTarget) {
						; // Impossible
					}
				});
	}
	
	public Snackbar snackbar(String text, int duration) {
		Snackbar snackbar = Snackbar.make(coordinatorLayout, text, duration);
		return snackbar;
	}
	
	public Snackbar snackbar(int ressourceId, int duration, Object... args) {
		Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(ressourceId, args), duration);
		return snackbar;
	}
	
	public StyleableToast toast(String string) {
		return StyleableToast.makeText(this, string, R.style.AllStyles);
	}
	
	public StyleableToast toast(int ressourceId, Object... args) {
		return StyleableToast.makeText(this, getString(ressourceId, args), Toast.LENGTH_LONG, R.style.AllStyles);
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
	
	public static Version getVersion() {
		return VERSION;
	}
	
	public static ViewHelper getViewHelper() {
		return HELPER;
	}
	
	public static XManagers getManagers() {
		return MANAGERS;
	}
	
	public static Handler getHandler() {
		return HANDLER;
	}
	
	public static BoxPlayActivity getBoxPlayActivity() {
		return INSTANCE;
	}
}
