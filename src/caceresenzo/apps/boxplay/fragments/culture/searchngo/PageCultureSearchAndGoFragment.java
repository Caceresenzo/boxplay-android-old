package caceresenzo.apps.boxplay.fragments.culture.searchngo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager.SearchAndGoSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class PageCultureSearchAndGoFragment extends Fragment {
	
	private SearchAndGoManager searchAndGoManager;
	
	private List<SearchAndGoResult> results;
	
	private List<SearchSuggestionItem> suggestions = new ArrayList<>();
	
	private MaterialSearchBar materialSearchBar;
	private RelativeLayout progressContainerRelativeLayout;
	private TextView actualProgressTextView, lastProgressTextView;
	private ImageButton bookmarkImageButton, historyImageButton, settingsImageButton;
	private RecyclerView searchResultRecyclerView;
	
	private SearchAndGoResultViewAdapter searchAdapter;
	private SearchSuggestionsAdapter searchSuggestionsAdapter;
	
	public PageCultureSearchAndGoFragment() {
		this.searchAndGoManager = BoxPlayActivity.getManagers().getSearchAndGoManager();
		
		this.results = new ArrayList<>();
		// JetAnimeSearchAndGoProvider fakeProvider = new JetAnimeSearchAndGoProvider();
		// results.add(new SearchAndGoResult(fakeProvider, "Hello", "http://google.com", "http://aez.com/aze.jpg", SearchCapability.ANIME));
		// results.add(new SearchAndGoResult(fakeProvider, "Hello2", "http://google.com2", "http://aez.com/aze.jpg2", SearchCapability.MANGA));
		
		this.searchAndGoManager.bindCallback(new SearchAndGoSearchCallback() {
			@Override
			public void onSearchStart() {
				searchStart();
				updateProgress("Starting global search...");
			}
			
			@Override
			public void onSearchSorting() {
				updateProgress("Sorting global search...");
			}
			
			@Override
			public void onSearchFinish(Map<String, SearchAndGoResult> workmap) {
				// BoxPlayActivity.getBoxPlayActivity().toast("Callback: Search finished! size: " + workmap.size()).show();
				results.clear();
				results.addAll(workmap.values());
				searchAdapter.notifyDataSetChanged();
				
				updateProgress("Searching finished!");
				searchStop();
			}
			
			@Override
			public void onSearchFail(Exception exception) {
				updateProgress("Searching failed!");
				searchStop();
			}
			
			@Override
			public void onProviderStarted(SearchAndGoProvider provider) {
				updateProgress("Searching on " + provider.getSiteName());
			}
			
			@Override
			public void onProviderSorting(SearchAndGoProvider provider) {
				updateProgress("Sorting results...");
			}
			
			@Override
			public void onProviderFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
				updateProgress("Finished searching on " + provider.getSiteName());
			}
			
			@Override
			public void onProviderSearchFail(SearchAndGoProvider provider, Exception exception) {
				updateProgress("Search failed on " + provider.getSiteName());
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_culture_searchngo, container, false);
		
		materialSearchBar = (MaterialSearchBar) view.findViewById(R.id.fragment_culture_searchno_materialsearchbar_searchbar);
		searchSuggestionsAdapter = new SearchSuggestionsAdapter(getLayoutInflater());
		searchSuggestionsAdapter.setSuggestions(suggestions);
		materialSearchBar.setCustomSuggestionAdapter(searchSuggestionsAdapter);
		
		materialSearchBar.setOnSearchActionListener(new OnSearchActionListener() {
			@Override
			public void onSearchConfirmed(CharSequence text) {
				searchAndGoManager.search(text.toString());
				searchSuggestionsAdapter.addSuggestion(new SearchSuggestionItem(text.toString()));
			}
			
			@Override
			public void onSearchStateChanged(boolean enabled) {
				if (!enabled) {
					results.clear();
					searchAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void onButtonClicked(int buttonCode) {
				;
			}
		});
		
		progressContainerRelativeLayout = (RelativeLayout) view.findViewById(R.id.fragment_culture_searchno_realtivelayout_progress_container);
		
		actualProgressTextView = (TextView) view.findViewById(R.id.fragment_culture_searchno_textview_progress_actual);
		lastProgressTextView = (TextView) view.findViewById(R.id.fragment_culture_searchno_textview_progress_last);
		
		bookmarkImageButton = (ImageButton) view.findViewById(R.id.fragment_culture_searchno_imagebutton_bookmark);
		historyImageButton = (ImageButton) view.findViewById(R.id.fragment_culture_searchno_imagebutton_history);
		settingsImageButton = (ImageButton) view.findViewById(R.id.fragment_culture_searchno_imagebutton_settings);
		
		searchResultRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_culture_searchno_recyclerview_search_result);
		searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		searchResultRecyclerView.setAdapter(searchAdapter = new SearchAndGoResultViewAdapter(results));
		searchResultRecyclerView.setHasFixedSize(true);
		searchResultRecyclerView.setNestedScrollingEnabled(false);
		
		setSearchBarHidden(false);
		
		return view;
	}
	
	public void searchStart() {
		setSearchBarHidden(true);
	}
	
	public void searchStop() {
		setSearchBarHidden(false);
	}
	
	public void setSearchBarHidden(boolean hidden) {
		progressContainerRelativeLayout.setVisibility(hidden ? View.VISIBLE : View.GONE);
		materialSearchBar.setVisibility(hidden ? View.GONE : View.VISIBLE);
		searchResultRecyclerView.setVisibility(hidden ? View.GONE : View.VISIBLE);
	}
	
	private String lastProgress = "-";
	
	public void updateProgress(String progress) {
		actualProgressTextView.setText(progress);
		lastProgressTextView.setText(lastProgress);
		
		lastProgress = progress;
	}
	
	class SearchSuggestionsAdapter extends SuggestionsAdapter<SearchSuggestionItem, SuggestionHolder> {
		
		public SearchSuggestionsAdapter(LayoutInflater inflater) {
			super(inflater);
		}
		
		@Override
		public int getSingleViewHeight() {
			return 80;
		}
		
		@Override
		public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = getLayoutInflater().inflate(R.layout.item_search_suggestion, parent, false);
			return new SuggestionHolder(view);
		}
		
		@Override
		public void onBindSuggestionHolder(SearchSuggestionItem suggestion, SuggestionHolder holder, int position) {
			holder.bind(suggestion);
		}
		
		@Override
		public Filter getFilter() {
			return new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					String term = constraint.toString();
					
					if (term.isEmpty()) {
						suggestions = suggestions_clone;
					} else {
						suggestions = new ArrayList<>();
						for (SearchSuggestionItem item : suggestions_clone) {
							if (item.getQuery().toLowerCase().contains(term.toLowerCase())) {
								suggestions.add(item);
							}
						}
					}
					
					results.values = suggestions;
					
					return results;
				}
				
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					suggestions = (ArrayList<SearchSuggestionItem>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
	
	class SuggestionHolder extends RecyclerView.ViewHolder {
		protected TextView titleTextView, subtitleTextView;
		
		public SuggestionHolder(View itemView) {
			super(itemView);
			titleTextView = (TextView) itemView.findViewById(R.id.item_search_suggestion_textview_title);
			subtitleTextView = (TextView) itemView.findViewById(R.id.item_search_suggestion_textview_subtitle);
		}
		
		public void bind(SearchSuggestionItem suggestion) {
			titleTextView.setText(suggestion.getQuery());
			
			if (suggestion.getDate() != null) {
				subtitleTextView.setText(suggestion.getDate().toLocaleString());
			} else {
				subtitleTextView.setVisibility(View.GONE);
			}
		}
	}
	
	class SearchSuggestionItem {
		private final String query;
		private Date date;
		
		public SearchSuggestionItem(String query) {
			this(query, new Date(System.currentTimeMillis()));
		}
		
		public SearchSuggestionItem(String query, Date date) {
			this.query = query;
			this.date = date;
		}
		
		public String getQuery() {
			return query;
		}
		
		public Date getDate() {
			return date;
		}
		
		public void updateDate() {
			setDate(new Date(System.currentTimeMillis()));
		}
		
		public void setDate(Date date) {
			this.date = date;
		}
	}
	
	class SearchAndGoResultViewAdapter extends RecyclerView.Adapter<SearchAndGoResultViewHolder> {
		private List<SearchAndGoResult> list;
		
		public SearchAndGoResultViewAdapter(List<SearchAndGoResult> list) {
			this.list = list;
		}
		
		@Override
		public SearchAndGoResultViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_culture_searchngo_search_element, viewGroup, false);
			return new SearchAndGoResultViewHolder(view);
		}
		
		@Override
		public void onBindViewHolder(SearchAndGoResultViewHolder viewHolder, int position) {
			SearchAndGoResult item = list.get(position);
			viewHolder.bind(item);
		}
		
		@Override
		public int getItemCount() {
			return list.size();
		}
	}
	
	class SearchAndGoResultViewHolder extends RecyclerView.ViewHolder {
		private TextView titleTextView, contentTextView, providerTextView, typeTextView;
		private ImageView thumbnailImageView;
		
		public SearchAndGoResultViewHolder(View itemView) {
			super(itemView);
			
			titleTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_title);
			contentTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_content);
			providerTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_provider);
			typeTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_type);
			
			thumbnailImageView = (ImageView) itemView.findViewById(R.id.item_culture_searchngo_search_element_imageview_thumbnail);
		}
		
		public void bind(SearchAndGoResult result) {
			titleTextView.setText(result.getName());
			contentTextView.setText("-/-"); // TOOD: Make a description generator
			providerTextView.setText(result.getParentProvider().getSiteName().toUpperCase());
			typeTextView.setText(result.getType().toString().toUpperCase());
			
			BoxPlayActivity.getViewHelper().downloadToImageView(thumbnailImageView, result.getBestImageUrl());
		}
	}
	
}