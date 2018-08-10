package caceresenzo.apps.boxplay.managers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.activities.VideoActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;
import caceresenzo.libs.boxplay.factory.VideoFactory;
import caceresenzo.libs.boxplay.factory.VideoFactory.VideoFactoryListener;
import caceresenzo.libs.boxplay.models.element.implementations.VideoElement;
import caceresenzo.libs.boxplay.models.store.video.VideoFile;
import caceresenzo.libs.boxplay.models.store.video.VideoGroup;
import caceresenzo.libs.boxplay.models.store.video.VideoSeason;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.parse.ParseUtils;

public class VideoManager extends AbstractManager {
	
	private static final String JSON_KEY_WATCHING_GROUP = "watching_group";
	private static final String JSON_KEY_WATCHED_SEASON = "watched_season";
	private static final String JSON_KEY_DATA_VIDEO = "data_video";
	private HashMap<Object, Object> mapJsonUserData = new HashMap<Object, Object>();
	private final File videoStoreDotJsonFile = new File(getManagers().baseDataDirectory, "videoStore.json");
	
	private VideoFactory videoFactory = new VideoFactory();
	private List<VideoGroup> groups;
	
	private VideoFile lastVideoFileOpen;
	
	public void initialize() {
		groups = new ArrayList<VideoGroup>();
	}
	
	public void callFactory() {
		groups.clear();
		
		videoFactory.parseServerJson(new VideoFactoryListener() {
			@Override
			public void onJsonMissingFileType() {
				boxPlayActivity.snackbar("Warning. Factory returned onJsonMissingFileType();", Snackbar.LENGTH_LONG).show();
			}
			
			@Override
			public void onVideoSeasonInvalidSeason(String element) {
				boxPlayActivity.snackbar("Warning. Factory returned onVideoSeasonInvalidSeason(element=\"" + element + "\");", Snackbar.LENGTH_LONG).show();
			}
			
			@Override
			public void onJsonNull() {
				boxPlayActivity.snackbar(R.string.boxplay_error_manager_json_null, Snackbar.LENGTH_LONG).show();
			}
			
			@Override
			public void onVideoGroupCreated(VideoGroup videoGroup) {
				groups.add(videoGroup);
			}
		}, BoxPlayActivity.getManagers().getDataManager().getJsonData());
		
		// Collections.sort(groups, VideoGroup.COMPARATOR);
		Collections.shuffle(groups);
		
		prepareConfigurator();
	}
	
	@SuppressWarnings("unchecked")
	private void prepareConfigurator() {
		mapJsonUserData.clear();
		
		List<String> watchingGroups = new ArrayList<String>();
		List<String> watchedSeasons = new ArrayList<String>();
		HashMap<String, HashMap<String, Object>> dataVideos = new HashMap<String, HashMap<String, Object>>();
		
		mapJsonUserData.put(JSON_KEY_WATCHING_GROUP, watchingGroups);
		mapJsonUserData.put(JSON_KEY_WATCHED_SEASON, watchedSeasons);
		mapJsonUserData.put(JSON_KEY_DATA_VIDEO, dataVideos);
		
		if (!videoStoreDotJsonFile.exists()) {
			return;
		}
		
		JsonObject json;
		try {
			json = (JsonObject) new JsonParser().parse(new FileReader(videoStoreDotJsonFile));
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
		
		try {
			for (String groupString : (List<String>) json.get(JSON_KEY_WATCHING_GROUP)) {
				watchingGroups.add(groupString);
			}
		} catch (Exception exception) {
			;
		}
		
		try {
			for (String seasonString : (List<String>) json.get(JSON_KEY_WATCHED_SEASON)) {
				watchedSeasons.add(seasonString);
			}
		} catch (Exception exception) {
			;
		}
		
		try {
			for (Entry<String, HashMap<String, Object>> entry : ((HashMap<String, HashMap<String, Object>>) json.get(JSON_KEY_DATA_VIDEO)).entrySet()) {
				String videoName = entry.getKey();
				HashMap<String, Object> data = entry.getValue();
				
				dataVideos.put(videoName, data);
			}
		} catch (Exception exception) {
			;
		}
		
		for (VideoGroup group : groups) {
			if (watchingGroups.contains(group.toString())) {
				group.setAsWatching(true);
			}
			
			for (VideoSeason season : group.getSeasons()) {
				if (watchedSeasons.contains(season.toString())) {
					season.asWatched(true);
				}
				
				for (VideoFile video : season.getVideos()) {
					if (dataVideos.containsKey(video.toString())) {
						HashMap<String, Object> data = dataVideos.get(video.toString());
						
						video.asWatched(ParseUtils.parseBoolean(data.get("watched"), false));
						video.newSavedTime(ParseUtils.parseLong(data.get("savedTime"), -1));
						video.newDuration(ParseUtils.parseLong(data.get("duration"), -1));
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param element
	 * @param mode
	 * @param value
	 *            - VideoGroup: WATCHED/UNWATCHED \n- VideoSeason: WATCHED/UNWATCHED \n- VideoFile: WATCHED, UNWATCHED, TIMEDATA
	 */
	@SuppressWarnings("unchecked")
	public void callConfigurator(VideoElement element) {
		if (element == null) {
			return;
		}
		
		if (element instanceof VideoGroup) { // Add to list
			VideoGroup group = (VideoGroup) element;
			List<String> watchingGroup = (List<String>) mapJsonUserData.get(JSON_KEY_WATCHING_GROUP);
			
			if (group.isWatching()) {
				watchingGroup.add(group.toString());
			} else {
				watchingGroup.remove(group.toString());
			}
		} else if (element instanceof VideoSeason) { // Season has been marked as seen
			VideoSeason season = (VideoSeason) element;
			List<String> watchedSeason = (List<String>) mapJsonUserData.get(JSON_KEY_WATCHED_SEASON);
			
			if (season.isWatched()) {
				watchedSeason.add(season.toString());
			} else {
				watchedSeason.remove(season.toString());
			}
		} else if (element instanceof VideoFile) { // Video has been marked as saw or time data update
			VideoFile video = (VideoFile) element;
			HashMap<String, HashMap<String, Object>> videoDataMap = (HashMap<String, HashMap<String, Object>>) mapJsonUserData.get(JSON_KEY_DATA_VIDEO);
			HashMap<String, Object> data = new HashMap<String, Object>();
			
			if (video.isWatched()) {
				data.put("watched", true);
			}
			if (video.getSavedTime() > 1 && video.getDuration() != 0) { // Not 0 or -1
				data.put("savedTime", video.getSavedTime());
				data.put("duration", video.getDuration());
			}
			// TODO: Add queue
			
			if (data.isEmpty()) {
				videoDataMap.remove(video.toString());
			} else {
				videoDataMap.put(video.toString(), data);
			}
		}
		
		JsonObject object = new JsonObject(mapJsonUserData);
		// DialogUtils.showDialog(VideoActivity.getVideoActivity(), "json", "JSON: " + object.toJsonString());
		try {
			getManagers().writeLocalFile(videoStoreDotJsonFile, object.toJsonString());
		} catch (IOException exception) {
			exception.printStackTrace();
			boxPlayActivity.toast("IOException: " + exception.getLocalizedMessage()).show();
			boxPlayActivity.snackbar(R.string.boxplay_error_config_failed, Snackbar.LENGTH_LONG).show();
		}
	}
	
	public void openVLC(VideoFile videoFile) {
		long savedTime = videoFile.getSavedTime();
		
		try {
			Uri uri = Uri.parse(videoFile.getUrl());
			Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
			vlcIntent.setPackage("org.videolan.vlc");
			vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
			vlcIntent.putExtra("position", savedTime > 1 ? savedTime : 1);
			vlcIntent.putExtra("title", BoxPlayActivity.getViewHelper().formatVideoFile(videoFile));
			vlcIntent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
			
			Activity context;
			if (VideoActivity.getVideoActivity() != null) {
				context = VideoActivity.getVideoActivity();
			} else {
				context = boxPlayActivity;
			}
			context.startActivityForResult(vlcIntent, BoxPlayApplication.REQUEST_ID_VLC_VIDEO);
		} catch (Exception exception) {
			// BoxPlayActivity.getBoxPlayActivity().appendError("Error when starting VLC. \n\nIs VLC installed? \n\nException: " + exception.getLocalizedMessage());
		}
		
		lastVideoFileOpen = videoFile;
	}
	
	public void openVLC(String url, String title) {
		try {
			Uri uri = Uri.parse(url);
			Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
			vlcIntent.setPackage("org.videolan.vlc");
			vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
			// vlcIntent.putExtra("position", 1);
			vlcIntent.putExtra("title", title != null ? title : url);
			vlcIntent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
			
			Activity context;
			if (VideoActivity.getVideoActivity() != null) {
				context = VideoActivity.getVideoActivity();
			} else {
				context = boxPlayActivity;
			}
			context.startActivityForResult(vlcIntent, BoxPlayApplication.REQUEST_ID_VLC_VIDEO_URL);
		} catch (Exception exception) {
			// BoxPlayActivity.getBoxPlayActivity().appendError("Error when starting VLC. \n\nIs VLC installed? \n\nException: " + exception.getLocalizedMessage());
		}
	}
	
	public List<VideoGroup> getGroups() {
		return groups;
	}
	
	public VideoFile getLastVideoFileOpen() {
		return lastVideoFileOpen;
	}
	
}