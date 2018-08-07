package caceresenzo.apps.boxplay.fragments.culture.searchngo;

import java.util.ArrayList;
import java.util.List;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager;
import caceresenzo.apps.boxplay.managers.SearchAndGoManager.SearchAndGoSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.SearchAndGoResult;

public class PageCultureSearchAndGoFragment extends Fragment {
	
	private SearchAndGoManager searchAndGoManager;
	
	private List<SearchAndGoResult> results;
	
	private MaterialSearchBar searchBar;
	private ImageButton bookmarkImageButton, historyImageButton, settingsImageButton;
	private RecyclerView searchResultRecyclerView;
	
	private SearchAndGoResultViewAdapter searchAdapter;
	
	public PageCultureSearchAndGoFragment() {
		this.searchAndGoManager = BoxPlayActivity.getManagers().getSearchAndGoManager();
		
		this.results = new ArrayList<>();
		// JetAnimeSearchAndGoProvider fakeProvider = new JetAnimeSearchAndGoProvider();
		// results.add(new SearchAndGoResult(fakeProvider, "Hello", "http://google.com", "http://aez.com/aze.jpg", SearchCapability.ANIME));
		// results.add(new SearchAndGoResult(fakeProvider, "Hello2", "http://google.com2", "http://aez.com/aze.jpg2", SearchCapability.MANGA));
		
		this.searchAndGoManager.bindCallback(new SearchAndGoSearchCallback() {
			@Override
			public void onSearchStart() {
				BoxPlayActivity.getBoxPlayActivity().toast("Callback: Search starting...").show();
			}
			
			@Override
			public void onSearchFinish(List<SearchAndGoResult> searchResults) {
				BoxPlayActivity.getBoxPlayActivity().toast("Callback: Search finished! size: " + searchResults.size()).show();
				results.clear();
				results.addAll(searchResults);
				searchAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onSearchFail(Exception exception) {
				;
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_culture_searchngo, container, false);
		
		searchBar = (MaterialSearchBar) view.findViewById(R.id.fragment_culture_searchno_materialsearchbar_searchbar);
		searchBar.setOnSearchActionListener(new OnSearchActionListener() {
			@Override
			public void onSearchConfirmed(CharSequence text) {
				BoxPlayActivity.getBoxPlayActivity().toast("MaterialSearchBar: onSearchConfirmed()").show();
				searchAndGoManager.search(text.toString());
			}
			
			@Override
			public void onSearchStateChanged(boolean enabled) {
				BoxPlayActivity.getBoxPlayActivity().toast("MaterialSearchBar: onSearchStateChanged()").show();
			}
			
			@Override
			public void onButtonClicked(int buttonCode) {
				BoxPlayActivity.getBoxPlayActivity().toast("MaterialSearchBar: onButtonClicked()").show();
			}
		});
		
		bookmarkImageButton = (ImageButton) view.findViewById(R.id.fragment_culture_searchno_imagebutton_bookmark);
		historyImageButton = (ImageButton) view.findViewById(R.id.fragment_culture_searchno_imagebutton_history);
		settingsImageButton = (ImageButton) view.findViewById(R.id.fragment_culture_searchno_imagebutton_settings);
		
		searchResultRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_culture_searchno_recyclerview_search_result);
		searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		searchResultRecyclerView.setAdapter(searchAdapter = new SearchAndGoResultViewAdapter(results));
		searchResultRecyclerView.setHasFixedSize(true);
		searchResultRecyclerView.setNestedScrollingEnabled(false);
		
		return view;
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