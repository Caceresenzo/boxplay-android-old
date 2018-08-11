package caceresenzo.apps.boxplay.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;
import caceresenzo.apps.boxplay.fragments.culture.searchngo.PageCultureSearchAndGoFragment.SearchHistoryItem;
import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;
import caceresenzo.apps.boxplay.managers.XManagers.SubManager;
import caceresenzo.libs.boxplay.culture.searchngo.callback.ProviderSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.callback.SearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.codec.chartable.JsonCharTable;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.SimpleLineStringBuilder;
import caceresenzo.libs.string.StringUtils;
import caceresenzo.libs.thread.HelpedThread;

public class SearchAndGoManager extends AbstractManager {
	
	private SearchSuggestionSubManager searchSuggestionSubManager;
	
	private Worker worker;
	private List<SearchAndGoProvider> providers;
	
	private SearchAndGoSearchCallback callback;
	
	private List<SearchHistoryItem> suggestions = new ArrayList<>();
	
	@Override
	public void initialize() {
		this.searchSuggestionSubManager = new SearchSuggestionSubManager();
		this.searchSuggestionSubManager.load();
		
		this.worker = new Worker();
		
		this.providers = new ArrayList<>();
		providers.addAll(ProviderManager.createAll()); // TODO: Do a user selection
		
		registerCallbacks();
	}
	
	@Override
	protected void destroy() {
		// this.searchSuggestionSubManager.save();
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
	
	public List<SearchHistoryItem> getSuggestions() {
		return suggestions;
	}
	
	private class Worker extends HelpedThread {
		private String localSearchQuery = "";
		private List<SearchAndGoProvider> localProviders = new ArrayList<>();
		
		@Override
		protected void onRun() {
			try {
				SearchAndGoProvider.provide(localProviders, localSearchQuery, true);
			} catch (Exception exception) {
				; // Handled by callbacks
			}
		}
		
		private void updateLocal(String query) {
			this.localSearchQuery = query;
			
			this.localProviders.clear();
			this.localProviders.addAll(providers);
		}
		
		@Override
		protected void onCancelled() {
			;
		}
		
		@Override
		protected void onFinished() {
			worker = new Worker(); // New instance, this one will be forgot
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
			public void onSearchSorting() {
				if (callback != null) {
					boxPlayHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onSearchSorting();
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
	
	public SearchSuggestionSubManager getSearchSuggestionSubManager() {
		return searchSuggestionSubManager;
	}
	
	public class SearchSuggestionSubManager extends SubManager implements JsonCharTable {
		private final String JSON_KEY_SUGGESTIONS = "suggestions";
		private final String JSON_KEY_SUGGESTIONS_QUERY = "query";
		private final String JSON_KEY_SUGGESTIONS_DATE = "date";
		
		private File suggestionFile = new File(getManagers().getBaseDataDirectory(), "suggestions.json");
		
		@SuppressWarnings("unchecked")
		public void load() {
			suggestions.clear();
			
			try {
				JsonObject json = (JsonObject) new JsonParser().parse(StringUtils.fromFile(suggestionFile));
				
				List<Map<String, Object>> suggestionsList = (List<Map<String, Object>>) json.get(JSON_KEY_SUGGESTIONS);
				
				for (Map<String, Object> suggestionDataMap : suggestionsList) {
					String query = ParseUtils.parseString(suggestionDataMap.get(JSON_KEY_SUGGESTIONS_QUERY), null);
					long date = ParseUtils.parseLong(suggestionDataMap.get(JSON_KEY_SUGGESTIONS_DATE), -1);
					
					if (query == null || date == -1) {
						continue;
					}
					
					suggestions.add(new SearchHistoryItem(query, date));
				}
				
				suggestions.sort(new Comparator<SearchHistoryItem>() {
					@Override
					public int compare(SearchHistoryItem item1, SearchHistoryItem item2) {
						return (int) (item2.getDate().getTime() - item1.getDate().getTime());
					}
				});
				
				// DialogUtils.showDialog(boxPlayActivity, "success when loading", "Suggestions: " + suggestions.size() + "\n\nFile data: " + StringUtils.fromFile(suggestionFile));
			} catch (Exception exception) {
				// DialogUtils.showDialog(boxPlayActivity, "exception when loading", StringUtils.fromException(exception));
				// suggestions = new ArrayList<>();
			}
			
			// while (suggestions.size() > 3) {
			// suggestions.remove(0);
			// }
			
			save();
		}
		
		public void save() {
			save(new ArrayList<SearchHistoryItem>());
		}
		
		public void save(List<SearchHistoryItem> newSuggestions) {
			SimpleLineStringBuilder builder = new SimpleLineStringBuilder();
			
			suggestions.addAll(newSuggestions);
			
			builder.appendln("{");
			
			builder.appendln(TAB + "\"" + JSON_KEY_SUGGESTIONS + "\": [");
			
			List<String> alreadySavedQuery = new ArrayList<>();
			
			Iterator<SearchHistoryItem> iterator = suggestions.iterator();
			while (iterator.hasNext()) {
				SearchHistoryItem suggestion = iterator.next();
				
				if (alreadySavedQuery.contains(suggestion.getQuery())) {
					continue;
				}
				alreadySavedQuery.add(suggestion.getQuery());
				
				builder.appendln(TAB + TAB + "{");
				
				builder.appendln(TAB + TAB + TAB + "\"" + JSON_KEY_SUGGESTIONS_QUERY + "\": \"" + (suggestion.getQuery().replace("\\", "\\\\").replace("\"", "\\\"")) + "\",");
				builder.appendln(TAB + TAB + TAB + "\"" + JSON_KEY_SUGGESTIONS_DATE + "\": " + suggestion.getDate().getTime() + "");
				
				// JsonObject jsonObject = new JsonObject();
				// jsonObject.put(JSON_KEY_SUGGESTIONS_QUERY, suggestion.getQuery());
				// jsonObject.put(JSON_KEY_SUGGESTIONS_DATE, suggestion.getDate());
				
				// builder.appendln(TAB + TAB + TAB + jsonObject.toJsonString());
				
				builder.appendln(TAB + TAB + "}" + (iterator.hasNext() ? "," : ""));
			}
			
			builder.appendln(TAB + "]");
			
			builder.appendln("}");
			
			try {
				getManagers().writeLocalFile(suggestionFile, builder.toString());
			} catch (Exception exception) {
				Log.e(getClass().getSimpleName(), "Failed to save suggestions.", exception);
			}
			
			// String readFromFile = "";
			// try {
			// readFromFile = StringUtils.fromFile(suggestionFile);
			// } catch (Exception e) {
			// readFromFile = e.getLocalizedMessage();
			// }
			
			// DialogUtils.showDialog(boxPlayActivity, "json", "file: " + suggestionFile.getAbsolutePath() + "\n\nalready: " + alreadySavedQuery + "\n\nlist: " + newSuggestions + "\n\nbuilder: " + builder.toString() + "\n\nread from file: " + readFromFile);
			
		}
	}
	
	public static interface SearchAndGoSearchCallback {
		
		void onSearchStart();
		
		void onSearchSorting();
		
		void onSearchFinish(Map<String, SearchAndGoResult> workmap);
		
		void onSearchFail(Exception exception);
		
		void onProviderStarted(SearchAndGoProvider provider);
		
		void onProviderSorting(SearchAndGoProvider provider);
		
		void onProviderFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap);
		
		void onProviderSearchFail(SearchAndGoProvider provider, Exception exception);
		
	}
	
}