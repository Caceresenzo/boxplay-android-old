package caceresenzo.apps.boxplay.managers;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import android.support.design.widget.Snackbar;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.base.BaseBoxPlayActivty;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.other.about.PageAboutHostingFragment;
import caceresenzo.apps.boxplay.fragments.store.StorePageFragment;
import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.network.Downloader;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.thread.ThreadUtils;

public class DataManager extends AbstractManager {
	
	public static final String TAG = DataManager.class.getSimpleName();
	
	private JsonObject serverJsonData;
	private int serverJsonRevision = 0;
	
	private boolean working = false, workableDataReady = false;
	
	private Snackbar snackbar;
	
	@Override
	protected void initializeWhenUiReady(BaseBoxPlayActivty attachedActivity) {
		fetchData(false);
	}
	
	public void fetchData(final boolean forceFetch) {
		if (!working) {
			working = true;
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (!BoxPlayApplication.getBoxPlayApplication().isUiReady()) {
						ThreadUtils.sleep(100L);
					}
					
					BoxPlayApplication.getHandler().post(new Runnable() {
						@Override
						public void run() {
							snackbar = BoxPlayApplication.getBoxPlayApplication().snackbar(getString(R.string.boxplay_store_video_data_downloading), Snackbar.LENGTH_INDEFINITE);
							snackbar.show();
						}
					});
					
					final int oldServerJsonRevision = serverJsonRevision;
					try {
						String content = Downloader.getUrlContent(BoxPlayApplication.getBoxPlayApplication().getString(R.string.boxplay_data_download_url));
						serverJsonData = new JsonObject((Map<?, ?>) new JsonParser().parse(new StringReader(content)));
						
						serverJsonRevision = ParseUtils.parseInt(serverJsonData.get("json_revision"), 0);
					} catch (IOException | JsonException exception) {
						exception.printStackTrace();
					}
					
					working = false;
					workableDataReady = true;
					
					BoxPlayApplication.getHandler().post(new Runnable() {
						@Override
						public void run() {
							snackbar.dismiss();
							
							boolean newContent = oldServerJsonRevision != serverJsonRevision;
							boolean force = getManagers().getPreferences().getBoolean(getString(R.string.boxplay_other_settings_boxplay_pref_force_factory_key), false);
							
							if (forceFetch) {
								force = true;
							}
							
							if (newContent || force) {
								getManagers().getVideoManager().callFactory();
								getManagers().getMusicManager().callFactory();
								getManagers().getServerManager().callFactory();
							}
							
							for (StorePageFragment storePageFragment : StorePageFragment.getStorePageFragmentRegisteredInstances()) {
								if (storePageFragment != null) {
									storePageFragment.callDataUpdater(newContent);
								}
							}
							
							if (PageAboutHostingFragment.getPageAboutPageFragment() != null) {
								PageAboutHostingFragment.getPageAboutPageFragment().updateServers();
							}
						}
					});
				}
			}, TAG).start();
		}
	}
	
	public JsonObject getJsonData() {
		return serverJsonData;
	}
	
	public boolean isWorkableDataReady() {
		return workableDataReady;
	}
	
}