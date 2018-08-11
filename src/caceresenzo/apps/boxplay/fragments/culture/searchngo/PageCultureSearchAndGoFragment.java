package caceresenzo.apps.boxplay.fragments.culture.searchngo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import caceresenzo.android.libs.dialog.DialogUtils;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.activities.SearchAndGoDetailActivity;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager.SearchAndGoSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class PageCultureSearchAndGoFragment extends Fragment {
	
	private SearchAndGoManager searchAndGoManager;
	
	private List<SearchAndGoResult> results;
	
	private MaterialSearchBar materialSearchBar;
	private RelativeLayout progressContainerRelativeLayout;
	private TextView actualProgressTextView, lastProgressTextView;
	private ImageButton bookmarkImageButton, historyImageButton, settingsImageButton;
	private RecyclerView searchResultRecyclerView;
	
	private SearchAndGoResultViewAdapter searchAdapter;
	
	private OnSearchActionListener onSearchActionListener;
	
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
		
		materialSearchBar.setOnSearchActionListener(onSearchActionListener = new OnSearchActionListener() {
			@Override
			public void onSearchConfirmed(CharSequence text) {
				searchAndGoManager.search(text.toString());
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
				materialSearchBar.hideSuggestionsList();
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
	
	class SearchHistoryHolder extends RecyclerView.ViewHolder {
		private View view;
		protected TextView titleTextView, subtitleTextView;
		
		public SearchHistoryHolder(View itemView) {
			super(itemView);
			view = itemView;
			titleTextView = (TextView) itemView.findViewById(R.id.item_search_suggestion_textview_title);
			subtitleTextView = (TextView) itemView.findViewById(R.id.item_search_suggestion_textview_subtitle);
		}
		
		public void bind(final SearchHistoryItem suggestion) {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					materialSearchBar.setText(suggestion.getQuery());
					onSearchActionListener.onSearchConfirmed(suggestion.getQuery());
				}
			});
			
			titleTextView.setText(suggestion.getQuery());
			
			if (suggestion.getDate() != null) {
				subtitleTextView.setText(suggestion.getDate().toLocaleString());
			} else {
				subtitleTextView.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * Class to hold information about a search history
	 * 
	 * @author Enzo CACERES
	 */
	public static class SearchHistoryItem {
		private final String query;
		private long date;
		
		public SearchHistoryItem(String query) {
			this(query, System.currentTimeMillis());
		}
		
		public SearchHistoryItem(String query, long date) {
			this.query = query;
			this.date = date;
		}
		
		public String getQuery() {
			return query;
		}
		
		public Date getDate() {
			return new Date(date);
		}
		
		public void updateDate() {
			setDate(new Date(System.currentTimeMillis()));
		}
		
		public void setDate(Date date) {
			this.date = date.getTime();
		}
		
		@Override
		public String toString() {
			return "SearchSuggestionItem[query=" + query + ", date=" + date + "]";
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
		private View view;
		private TextView titleTextView, contentTextView, providerTextView, typeTextView;
		private ImageView thumbnailImageView;
		
		public SearchAndGoResultViewHolder(View itemView) {
			super(itemView);
			
			view = itemView;
			
			titleTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_title);
			contentTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_content);
			providerTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_provider);
			typeTextView = (TextView) itemView.findViewById(R.id.item_culture_searchngo_search_element_textview_type);
			
			thumbnailImageView = (ImageView) itemView.findViewById(R.id.item_culture_searchngo_search_element_imageview_thumbnail);
		}
		
		public void bind(final SearchAndGoResult result) {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					SearchAndGoDetailActivity.start(result);
				}
			});
			
			titleTextView.setText(result.getName());
			contentTextView.setText("-/-"); // TOOD: Make a description generator
			providerTextView.setText(result.getParentProvider().getSiteName().toUpperCase());
			typeTextView.setText(result.getType().toString().toUpperCase());
			
			BoxPlayActivity.getViewHelper().downloadToImageView(thumbnailImageView, result.getBestImageUrl());
		}
	}
	
}