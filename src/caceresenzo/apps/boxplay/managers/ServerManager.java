package caceresenzo.apps.boxplay.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.design.widget.Snackbar;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.factory.ServerFactory;
import caceresenzo.apps.boxplay.factory.ServerFactory.ServerFactoryListener;
import caceresenzo.apps.boxplay.managers.XManagers.AManager;
import caceresenzo.apps.boxplay.models.server.ServerHosting;

public class ServerManager extends AManager {
	
	private ServerFactory musicFactory = new ServerFactory();
	private List<ServerHosting> hostings;
	
	public void initialize() {
		hostings = new ArrayList<ServerHosting>();
	}
	
	public void callFactory() {
		hostings.clear();
		
		musicFactory.parseServerJson(new ServerFactoryListener() {
			@Override
			public void onJsonMissingContent() {
				boxPlayActivity.snackbar("Warning. (Server)Factory returned onJsonMissingContent();", Snackbar.LENGTH_LONG).show();
			}
			
			@Override
			public void onJsonNull() {
				boxPlayActivity.snackbar(R.string.boxplay_error_manager_json_null, Snackbar.LENGTH_LONG).show();
			}
			
			@Override
			public void onServerHostingCreated(ServerHosting ServerHosting) {
				hostings.add(ServerHosting);
			}
		}, BoxPlayActivity.getManagers().getDataManager().getJsonData());
		
		Collections.sort(hostings, ServerHosting.COMPARATOR);
	}
	
	public List<ServerHosting> getServerHostings() {
		return hostings;
	}
	
}