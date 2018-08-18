package caceresenzo.apps.boxplay.fragments.mylist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.SearchAndGoDetailActivity;
import caceresenzo.apps.boxplay.activities.VideoActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.helper.ViewHelper;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.models.element.BoxPlayElement;
import caceresenzo.libs.boxplay.models.store.video.VideoGroup;

public class PageWatchLaterListFragment extends Fragment {
	
	/* Managers */
	private BoxPlayApplication boxPlayApplication;
	private ViewHelper viewHelper;
	
	/* Content */
	private List<WatchLaterRecyclerViewItem> watchLaterItems;
	
	/* Views */
	private RecyclerView recyclerView;
	
	private ProgressBar loadingProgressBar;
	
	private TextView infoTextView;
	
	/* Constructor */
	public PageWatchLaterListFragment() {
		this.boxPlayApplication = BoxPlayApplication.getBoxPlayApplication();
		this.viewHelper = BoxPlayApplication.getViewHelper();
		
		this.watchLaterItems = new ArrayList<>();
		
		SearchAndGoResult dummyResult = new SearchAndGoResult(ProviderManager.MANGALEL.create(), //
				"Arifureta Shokugyou de Sekai Saikyou", //
				"https://www.manga-lel.com/manga/arifureta-shokugyou-de-sekai-saikyou/", //
				"https://www.manga-lel.com//uploads/manga/arifureta-shokugyou-de-sekai-saikyou/cover/cover_250x350.jpg");
		
		watchLaterItems.add(new CultureSearchAndGoResultItem(dummyResult));
		
		for (Object object : BoxPlayElement.getInstances().values()) {
			if (object instanceof VideoGroup) {
				VideoGroup videoGroup = (VideoGroup) object;
				
				if (videoGroup.isWatching()) {
					watchLaterItems.add(new StoreVideoGroupItem(videoGroup));
				}
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mylist_watchlater, container, false);
		
		this.recyclerView = view.findViewById(R.id.fragment_mylist_watchlater_recyclerview_content);
		this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		this.recyclerView.setAdapter(new WatchListAdapter());
		
		this.loadingProgressBar = (ProgressBar) view.findViewById(R.id.fragment_mylist_watchlater_progressbar_loading);
		
		this.infoTextView = (TextView) view.findViewById(R.id.fragment_mylist_watchlater_textview_info_text);
		
		// setListHidden(true);
		setListHidden(false);
		
		return view;
	}
	
	private void setListHidden(boolean hidden) {
		recyclerView.setVisibility(hidden ? View.GONE : View.VISIBLE);
		
		loadingProgressBar.setVisibility(hidden ? View.VISIBLE : View.GONE);
		infoTextView.setVisibility(View.GONE);
	}
	
	public class WatchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View view = inflater.inflate(R.layout.item_mylist_watchlater_base_element, parent, false);
			
			switch (viewType) {
				case WatchLaterRecyclerViewItem.TYPE_STORE_VIDEO_GROUP: {
					return new StoreVideoGroupViewHolder(view);
				}
				
				case WatchLaterRecyclerViewItem.TYPE_CULTURE_SEARCHANDGO_RESULT: {
					return new CultureSearchAndGoItemViewHolder(view);
				}
				
				default: {
					return null;
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
			((Bindable<WatchLaterRecyclerViewItem>) holder).bind(watchLaterItems.get(position));
		}
		
		@Override
		public int getItemViewType(int position) {
			return watchLaterItems.get(position).getType();
		}
		
		@Override
		public int getItemCount() {
			return watchLaterItems.size();
		}
	}
	
	class StoreVideoGroupViewHolder extends BaseItemViewHolder implements Bindable<StoreVideoGroupItem> {
		public StoreVideoGroupViewHolder(View itemView) {
			super(itemView);
		}
		
		@Override
		public void bind(StoreVideoGroupItem item) {
			final VideoGroup group = item.getVideoGroup();
			
			titleTextView.setText(group.getTitle());
			viewHelper.downloadToImageView(thumbnailImageView, group.getGroupImageUrl());
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					VideoActivity.start(group);
				}
			});
		}
		
		@Override
		public int getSourceStringRessourceId() {
			return R.string.boxplay_mylist_watchlater_source_store_video;
		}
	}
	
	class StoreVideoGroupItem extends WatchLaterRecyclerViewItem {
		private VideoGroup videoGroup;
		
		public StoreVideoGroupItem(VideoGroup videoGroup) {
			this.videoGroup = videoGroup;
		}
		
		public VideoGroup getVideoGroup() {
			return videoGroup;
		}
		
		@Override
		public int getType() {
			return TYPE_STORE_VIDEO_GROUP;
		}
	}
	
	class CultureSearchAndGoItemViewHolder extends BaseItemViewHolder implements Bindable<CultureSearchAndGoResultItem> {
		public CultureSearchAndGoItemViewHolder(View itemView) {
			super(itemView);
		}
		
		@Override
		public void bind(CultureSearchAndGoResultItem item) {
			final SearchAndGoResult searchAndGoResult = item.getSearchAndGoResult();
			
			titleTextView.setText(searchAndGoResult.getName());
			viewHelper.downloadToImageView(thumbnailImageView, searchAndGoResult.getImageUrl());
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					SearchAndGoDetailActivity.start(searchAndGoResult);
				}
			});
		}
		
		@Override
		public int getSourceStringRessourceId() {
			return R.string.boxplay_mylist_watchlater_source_culture_searchngo;
		}
	}
	
	class CultureSearchAndGoResultItem extends WatchLaterRecyclerViewItem {
		private SearchAndGoResult searchAndGoResult;
		
		public CultureSearchAndGoResultItem(SearchAndGoResult searchAndGoResult) {
			this.searchAndGoResult = searchAndGoResult;
		}
		
		public SearchAndGoResult getSearchAndGoResult() {
			return searchAndGoResult;
		}
		
		@Override
		public int getType() {
			return TYPE_CULTURE_SEARCHANDGO_RESULT;
		}
	}
	
	abstract class BaseItemViewHolder extends RecyclerView.ViewHolder {
		protected View view;
		protected TextView sourceTextView, titleTextView;
		protected ImageView thumbnailImageView;
		
		public BaseItemViewHolder(View itemView) {
			super(itemView);
			
			this.view = itemView;
			
			this.sourceTextView = (TextView) itemView.findViewById(R.id.item_mylist_watchlater_base_element_textview_source);
			this.titleTextView = (TextView) itemView.findViewById(R.id.item_mylist_watchlater_base_element_textview_title);
			this.thumbnailImageView = (ImageView) itemView.findViewById(R.id.item_mylist_watchlater_base_element_imageview_thumbnail);
			
			this.sourceTextView.setText(getSourceStringRessourceId());
		}
		
		public abstract int getSourceStringRessourceId();
	}
	
	abstract static class WatchLaterRecyclerViewItem {
		public static final int TYPE_STORE_VIDEO_GROUP = 0;
		public static final int TYPE_CULTURE_SEARCHANDGO_RESULT = 1;
		
		public abstract int getType();
	}
	
	interface Bindable<T extends WatchLaterRecyclerViewItem> {
		void bind(T item);
	}
	
}