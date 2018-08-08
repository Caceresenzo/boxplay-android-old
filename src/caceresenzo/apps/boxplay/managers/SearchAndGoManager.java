package caceresenzo.apps.boxplay.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;
import caceresenzo.libs.boxplay.culture.searchngo.callback.ProviderSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.callback.SearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class SearchAndGoManager extends AbstractManager {
	
	private Worker worker;
	private List<SearchAndGoProvider> providers;
	
	private SearchAndGoSearchCallback callback;
	
	@Override
	public void initialize() {
		this.worker = new Worker();
		
		this.providers = new ArrayList<>();
		providers.addAll(ProviderManager.createAll()); // TODO: Do a user selection
		
		registerCallbacks();
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
			
			try {
				SearchAndGoProvider.provide(localProviders, localSearchQuery, true);
			} catch (Exception exception) {
				; // Handled by callbacks
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
	
	private void registerCallbacks() {
		ProviderCallback.registerSearchallback(new SearchCallback() { // Unused for now
			@Override
			public void onSearchStarting() {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onSearchStart();
						}
					});
				}
			}
			
			@Override
			public void onSearchFinished(final Map<String, SearchAndGoResult> workmap) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onSearchFinish(workmap);
						}
					});
				}
			}
			
			@Override
			public void onSearchFail(final Exception exception) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onSearchFail(exception);
						}
					});
				}
			}
		});
		
		ProviderCallback.registerProviderSearchallback(new ProviderSearchCallback() {
			@Override
			public void onProviderSearchStarting(final SearchAndGoProvider provider) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onProviderStarted(provider);
						}
					});
				}
			}
			
			@Override
			public void onProviderSorting(final SearchAndGoProvider provider) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onProviderSorting(provider);
						}
					});
				}
			}
			
			@Override
			public void onProviderSearchFinished(final SearchAndGoProvider provider, final Map<String, SearchAndGoResult> workmap) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onProviderFinished(provider, workmap);
						}
					});
				}
			}
			
			@Override
			public void onProviderFailed(final SearchAndGoProvider provider, final Exception exception) {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onProviderSearchFail(provider, exception);
						}
					});
				}
			}
		});
	}
	
	public static interface SearchAndGoSearchCallback {
		
		void onSearchStart();
		
		void onSearchFinish(Map<String, SearchAndGoResult> workmap);
		
		void onSearchFail(Exception exception);
		
		void onProviderStarted(SearchAndGoProvider provider);
		
		void onProviderSorting(SearchAndGoProvider provider);
		
		void onProviderFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap);
		
		void onProviderSearchFail(SearchAndGoProvider provider, Exception exception);
		
	}
	
}