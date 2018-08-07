package caceresenzo.apps.boxplay.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;
import caceresenzo.libs.boxplay.culture.searchngo.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;

public class SearchAndGoManager extends AbstractManager {
	
	private Worker worker;
	private List<SearchAndGoProvider> providers;
	
	private SearchAndGoSearchCallback callback;
	
	@Override
	public void initialize() {
		this.worker = new Worker();
		
		this.providers = new ArrayList<>();
		
		DEBUG();
	}
	
	public void DEBUG() {
		providers.add(ProviderManager.JETANIME.create());
	}
	
	public void bindCallback(SearchAndGoSearchCallback callback) {
		this.callback = callback;
	}
	
	public void search(String query) {
		if (worker.isRunning()) {
			boxPlayActivity.toast("Worker not available").show();
			return;
		}
		
		worker.updateLocal(query);
		worker.start();
	}
	
	private class Worker extends Thread {
		
		private boolean running = false;
		
		private String localSearchQuery = "";
		private List<SearchAndGoProvider> localProviders = new ArrayList<>();
		
		@Override
		public void run() {
			running(true);
			
			if (callback != null) {
				boxPlayHandler.post(new Runnable() {
					@Override
					public void run() {
						callback.onSearchStart();
					}
				});
			}
			
			final List<SearchAndGoResult> localResults = new ArrayList<>();
			
			try {
				
				for (SearchAndGoProvider provider : localProviders) {
					Map<String, SearchAndGoResult> workmap = provider.work(localSearchQuery);
					
					for (Entry<String, SearchAndGoResult> entry : workmap.entrySet()) {
						localResults.add(entry.getValue());
					}
				}
			} catch (final Exception exception) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onSearchFail(exception);
						}
					});
				}
			}
			
			if (callback != null) {
				boxPlayHandler.post(new Runnable() {
					@Override
					public void run() {
						callback.onSearchFinish(localResults);
					}
				});
			}
			
			running(false);
		}
		
		private void updateLocal(String query) {
			this.localSearchQuery = query;
			
			this.localProviders.clear();
			this.localProviders.addAll(providers);
		}
		
		private boolean isRunning() {
			return running;
		}
		
		private void running(boolean state) {
			running = state;
			
			if (!state) {
				worker = new Worker(); // New instance, this one will be forgot
			}
		}
		
	}
	
	public static interface SearchAndGoSearchCallback {
		
		void onSearchStart();
		
		void onSearchFinish(List<SearchAndGoResult> results);
		
		void onSearchFail(Exception exception);
		
	}
	
}