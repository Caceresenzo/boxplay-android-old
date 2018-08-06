package caceresenzo.apps.boxplay.helper;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.budiyev.android.imageloader.ImageLoader;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import caceresenzo.android.libs.application.ApplicationUtils;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.activities.MusicActivity;
import caceresenzo.apps.boxplay.activities.VideoActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.store.PageMusicStoreFragment.MusicStoreSubCategory;
import caceresenzo.apps.boxplay.fragments.store.PageVideoStoreFragment.VideoStoreSubCategory;
import caceresenzo.libs.boxplay.models.element.MusicElement;
import caceresenzo.libs.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.libs.boxplay.models.store.music.MusicFile;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicAuthorType;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicGenre;
import caceresenzo.libs.boxplay.models.store.video.VideoFile;
import caceresenzo.libs.boxplay.models.store.video.VideoGroup;
import caceresenzo.libs.boxplay.models.store.video.VideoSeason;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoFileType;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoType;

public class ViewHelper {
	private BoxPlayActivity boxPlayActivity;
	
	private static Map<MenuIdItem, MenuItem> drawerMenuIds = new HashMap<MenuIdItem, MenuItem>();
	private static Map<Object, String> enumCacheTranslation = new HashMap<Object, String>();
	
	/*
	 * Cache
	 */
	public void prepareCache(BoxPlayActivity activity) {
		this.boxPlayActivity = activity;
		
		/*
		 * Menu cache
		 */
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_store_video, true), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_store_music, true), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_connect_feed), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_connect_friends), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_connect_chat), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_culture_searchngo), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_premium_adult), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_other_settings), null);
		drawerMenuIds.put(new MenuIdItem(R.id.drawer_boxplay_other_about), null);
		
		/*
		 * Translation cache
		 */
		// Video
		enumCacheTranslation.put(VideoFileType.ANIME, boxPlayActivity.getString(R.string.boxplay_store_video_file_type_anime));
		enumCacheTranslation.put(VideoFileType.SERIE, boxPlayActivity.getString(R.string.boxplay_store_video_file_type_serie));
		enumCacheTranslation.put(VideoFileType.ANIMEMOVIE, boxPlayActivity.getString(R.string.boxplay_store_video_file_type_animemovie));
		enumCacheTranslation.put(VideoFileType.MOVIE, boxPlayActivity.getString(R.string.boxplay_store_video_file_type_movie));
		enumCacheTranslation.put(VideoFileType.UNKNOWN, boxPlayActivity.getString(R.string.boxplay_store_video_enum_unknown));
		
		enumCacheTranslation.put(VideoType.EPISODE, boxPlayActivity.getString(R.string.boxplay_store_video_type_episode));
		enumCacheTranslation.put(VideoType.OAV, boxPlayActivity.getString(R.string.boxplay_store_video_type_oav));
		enumCacheTranslation.put(VideoType.SPECIAL, boxPlayActivity.getString(R.string.boxplay_store_video_type_special));
		enumCacheTranslation.put(VideoType.MOVIE, boxPlayActivity.getString(R.string.boxplay_store_video_type_movie));
		enumCacheTranslation.put(VideoType.OTHER, boxPlayActivity.getString(R.string.boxplay_store_video_type_other));
		enumCacheTranslation.put(VideoType.UNKNOWN, boxPlayActivity.getString(R.string.boxplay_store_video_enum_unknown));
		
		enumCacheTranslation.put(ElementLanguage.FR, boxPlayActivity.getString(R.string.boxplay_store_video_language_french));
		enumCacheTranslation.put(ElementLanguage.ENSUBFR, boxPlayActivity.getString(R.string.boxplay_store_video_language_english_subtitle_french));
		enumCacheTranslation.put(ElementLanguage.JPSUBFR, boxPlayActivity.getString(R.string.boxplay_store_video_language_japanese_subtitle_french));
		enumCacheTranslation.put(ElementLanguage.UNKNOWN, boxPlayActivity.getString(R.string.boxplay_store_video_enum_unknown));
		
		enumCacheTranslation.put(VideoStoreSubCategory.YOURLIST, boxPlayActivity.getString(R.string.boxplay_store_video_category_your_list));
		enumCacheTranslation.put(VideoStoreSubCategory.RECOMMENDED, boxPlayActivity.getString(R.string.boxplay_store_video_category_recommended));
		enumCacheTranslation.put(VideoStoreSubCategory.ANIMES, boxPlayActivity.getString(R.string.boxplay_store_video_category_animes));
		enumCacheTranslation.put(VideoStoreSubCategory.MOVIES, boxPlayActivity.getString(R.string.boxplay_store_video_category_movies));
		enumCacheTranslation.put(VideoStoreSubCategory.SERIES, boxPlayActivity.getString(R.string.boxplay_store_video_category_series));
		enumCacheTranslation.put(VideoStoreSubCategory.RANDOM, boxPlayActivity.getString(R.string.boxplay_store_video_category_random));
		enumCacheTranslation.put(VideoStoreSubCategory.RELEASE, boxPlayActivity.getString(R.string.boxplay_store_video_category_release));
		
		// Music
		enumCacheTranslation.put(MusicAuthorType.AUTHOR, boxPlayActivity.getString(R.string.boxplay_store_music_author_type_author));
		enumCacheTranslation.put(MusicAuthorType.BAND, boxPlayActivity.getString(R.string.boxplay_store_music_author_type_band));
		enumCacheTranslation.put(MusicAuthorType.UNKNOWN, boxPlayActivity.getString(R.string.boxplay_store_music_type_unknown));
		
		enumCacheTranslation.put(MusicGenre.AFRICAN, boxPlayActivity.getString(R.string.boxplay_store_music_type_african));
		enumCacheTranslation.put(MusicGenre.ASIAN, boxPlayActivity.getString(R.string.boxplay_store_music_type_asian));
		enumCacheTranslation.put(MusicGenre.COMEDY, boxPlayActivity.getString(R.string.boxplay_store_music_type_comedy));
		enumCacheTranslation.put(MusicGenre.COUNTRY, boxPlayActivity.getString(R.string.boxplay_store_music_type_country));
		enumCacheTranslation.put(MusicGenre.EASY_LISTENING, boxPlayActivity.getString(R.string.boxplay_store_music_type_easy_listening));
		enumCacheTranslation.put(MusicGenre.ELECTRONIC, boxPlayActivity.getString(R.string.boxplay_store_music_type_electronic));
		enumCacheTranslation.put(MusicGenre.FOLK, boxPlayActivity.getString(R.string.boxplay_store_music_type_folk));
		enumCacheTranslation.put(MusicGenre.HIP_HOP, boxPlayActivity.getString(R.string.boxplay_store_music_type_hip_hop));
		enumCacheTranslation.put(MusicGenre.JAZZ, boxPlayActivity.getString(R.string.boxplay_store_music_type_jazz));
		enumCacheTranslation.put(MusicGenre.LATIN, boxPlayActivity.getString(R.string.boxplay_store_music_type_latin));
		enumCacheTranslation.put(MusicGenre.POP, boxPlayActivity.getString(R.string.boxplay_store_music_type_pop));
		enumCacheTranslation.put(MusicGenre.RAP, boxPlayActivity.getString(R.string.boxplay_store_music_type_rap));
		enumCacheTranslation.put(MusicGenre.ROCK, boxPlayActivity.getString(R.string.boxplay_store_music_type_rock));
		enumCacheTranslation.put(MusicGenre.SOCA, boxPlayActivity.getString(R.string.boxplay_store_music_type_soca));
		enumCacheTranslation.put(MusicGenre.UNKNOWN, boxPlayActivity.getString(R.string.boxplay_store_music_type_unknown));
		
		enumCacheTranslation.put(MusicStoreSubCategory.YOURLIST, boxPlayActivity.getString(R.string.boxplay_store_music_category_your_list));
		enumCacheTranslation.put(MusicStoreSubCategory.RECOMMENDED, boxPlayActivity.getString(R.string.boxplay_store_music_category_recommended));
		enumCacheTranslation.put(MusicStoreSubCategory.ALBUMS, boxPlayActivity.getString(R.string.boxplay_store_music_category_albums));
		enumCacheTranslation.put(MusicStoreSubCategory.RANDOM, boxPlayActivity.getString(R.string.boxplay_store_music_category_random));
		enumCacheTranslation.put(MusicStoreSubCategory.RELEASE, boxPlayActivity.getString(R.string.boxplay_store_music_category_release));
		
		/*
		 * Vlc
		 */
		checkVlc();
		DATEFORMAT_VIDEO_DURATION.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public void recache() {
		if (boxPlayActivity == null) {
			return;
		}
		
		// boxPlayActivity.toast(R.string.boxplay_viewhelper_recaching).show();
		
		drawerMenuIds.clear();
		enumCacheTranslation.clear();
		
		prepareCache(boxPlayActivity);
	}
	
	/*
	 * Menu Help
	 */
	public void unselectAllMenu() {
		Menu menu = boxPlayActivity.getNavigationView().getMenu();
		for (MenuIdItem menuIdItem : drawerMenuIds.keySet()) {
			MenuItem item = drawerMenuIds.get(menuIdItem);
			if (item == null) {
				drawerMenuIds.put(menuIdItem, item = menu.findItem(menuIdItem.getId()));
			}
			
			if (item != null && item.isChecked()) {
				item.setChecked(false);
			}
		}
		// BoxPlayActivity.getBoxPlayActivity().toast(String.format("\"looped over %s item\"", drawerMenuIds.keySet().size())).show();
	}
	
	public void updateSeachMenu(int nextId) {
		for (MenuIdItem menuIdItem : drawerMenuIds.keySet()) {
			if (menuIdItem.getId() == nextId) {
				try {
					boxPlayActivity.getOptionsMenu().findItem(R.id.menu_main_action_search).setVisible(menuIdItem.isSearchAllowed());
				} catch (Exception exception) {
					;
				}
				break;
			}
		}
	}
	
	/*
	 * Fragment Help
	 */
	private Fragment lastFragment;
	
	public Fragment getLastFragment() {
		return lastFragment;
	}
	
	public void setLastFragment(Fragment lastFragment) {
		this.lastFragment = lastFragment;
	}
	
	/*
	 * View Help
	 */
	public void downloadToImageView(ImageView imageView, String url) {
		if (imageView == null) {
			return;
		}
		
		if (url == null) {
			imageView.setImageDrawable(new ColorDrawable(color(R.color.colorError)));
			return;
		}
		
		ImageLoader.with(boxPlayActivity) //
				.from(url) //
				.errorDrawable(new ColorDrawable(color(R.color.colorError))) //
				.load(imageView); //
	}
	
	public void clearImageCache() {
		ImageLoader imageLoader = ImageLoader.with(boxPlayActivity);
		imageLoader.clearMemoryCache();
		imageLoader.clearStorageCache();
		imageLoader.clearAllCaches();
	}
	
	/*
	 * Video Help
	 */
	private static VideoGroup passingVideoGroup;
	
	public void startVideoActivity(VideoGroup group) {
		passingVideoGroup = group;
		boxPlayActivity.startActivity(new Intent(boxPlayActivity, VideoActivity.class));
	}
	
	public VideoGroup getPassingVideoGroup() {
		return passingVideoGroup;
	}
	
	public String formatVideoFile(VideoFile video) {
		VideoSeason season = video.getParentSeason();
		VideoGroup group = season.getParentGroup();
		
		String raw;
		if (group.hasSeason()) {
			raw = boxPlayActivity.getString(R.string.boxplay_store_video_activity_vlc_title);
			
			return String.format(raw, //
					enumToStringCacheTranslation(video.getFileType()).toUpperCase(), //
					group.getTitle(), //
					season.getSeasonValue(), //
					enumToStringCacheTranslation(video.getVideoType()).toLowerCase(), //
					video.getRawEpisodeValue() //
			);
		} else {
			raw = boxPlayActivity.getString(R.string.boxplay_store_video_activity_vlc_title_movie);
			
			return String.format(raw, //
					enumToStringCacheTranslation(video.getFileType()).toUpperCase(), //
					group.getTitle() //
			);
		}
	}
	
	/*
	 * Music Help
	 */
	private static MusicElement passingMusicElement;
	
	public void startMusicActivity(MusicElement element) {
		if (element == null) {
			return;
		}
		
		if (element instanceof MusicFile && ((MusicFile) element).getParentAlbum() != null) {
			element = ((MusicFile) element).getParentAlbum();
		}
		
		passingMusicElement = element;
		boxPlayActivity.startActivity(new Intent(boxPlayActivity, MusicActivity.class));
	}
	
	public MusicElement getPassingMusicElement() {
		return passingMusicElement;
	}
	
	/*
	 * Translation Help
	 */
	public String enumToStringCacheTranslation(Object enumType) {
		return enumToStringCacheTranslation(enumType, false);
	}
	
	public String enumToStringCacheTranslation(Object enumType, boolean moreThanOne) {
		if (enumType == null) {
			return null;
		}
		
		String raw = enumCacheTranslation.get(enumType);
		
		if (raw == null) {
			return String.valueOf(enumType);
		}
		
		if (!raw.contains("%s")) {
			return raw;
		}
		return String.format(raw, moreThanOne ? "s" : "");
	}
	
	/*
	 * VLC Help
	 */
	private static boolean vlcInstalled = false;
	
	public boolean isVlcInstalled() {
		return vlcInstalled;
	}
	
	public void checkVlc() {
		vlcInstalled = ApplicationUtils.isAppInstalled(boxPlayActivity, "org.videolan.vlc");
	}
	
	/*
	 * Time Help
	 */
	public static final SimpleDateFormat DATEFORMAT_VIDEO_DURATION = new SimpleDateFormat("HH:mm:ss");
	
	/*
	 * Classes
	 */
	
	class MenuIdItem {
		private int id;
		private boolean allowSearch;
		
		public MenuIdItem(int id) {
			this(id, false);
		}
		
		public MenuIdItem(int id, boolean allowSearch) {
			this.id = id;
			this.allowSearch = allowSearch;
		}
		
		public int getId() {
			return id;
		}
		
		public boolean isSearchAllowed() {
			return allowSearch;
		}
	}
	
	public MenuIdItem getMenuIdItemById(int id) {
		for (MenuIdItem menuIdItem : drawerMenuIds.keySet()) {
			if (menuIdItem.getId() == id) {
				return menuIdItem;
			}
		}
		return null;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static int color(@ColorRes int colorRessource) {
		if (Build.VERSION.SDK_INT < 23) {
			return BoxPlayApplication.getBoxPlayApplication().getResources().getColor(colorRessource);
		} else {
			return BoxPlayApplication.getBoxPlayApplication().getColor(colorRessource);
		}
	}
	
}