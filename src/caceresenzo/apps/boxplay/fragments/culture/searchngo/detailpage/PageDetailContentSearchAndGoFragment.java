package caceresenzo.apps.boxplay.fragments.culture.searchngo.detailpage;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.helper.ViewHelper;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class PageDetailContentSearchAndGoFragment extends Fragment {
	
	private ViewHelper viewHelper;
	
	private boolean uiReady = false;
	
	private List<AdditionalResultData> contents = new ArrayList<>();
	
	private RecyclerView recyclerView;
	private ProgressBar progressBar;
	
	private ContentViewAdapter adapter;
	
	public PageDetailContentSearchAndGoFragment() {
		this.viewHelper = BoxPlayActivity.getViewHelper();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_culture_searchngo_activitypage_details, container, false);
		
		progressBar = (ProgressBar) view.findViewById(R.id.fragment_culture_searchngo_activitypage_details_progressbar_loading);
		
		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_culture_searchngo_activitypage_details_recyclerview_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(adapter = new ContentViewAdapter(contents));
		recyclerView.setHasFixedSize(true);
		recyclerView.setNestedScrollingEnabled(false);
		
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		
		uiReady = true;
		
		return view;
	}
	
	public void applyResult(SearchAndGoResult result, List<AdditionalResultData> additionals) {
		this.contents.clear();
		this.contents.addAll(additionals);
		
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			
			recyclerView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
	
	public boolean isUiReady() {
		return uiReady;
	}
	
	class ContentViewAdapter extends RecyclerView.Adapter<ContentViewHolder> {
		private List<AdditionalResultData> list;
		
		public ContentViewAdapter(List<AdditionalResultData> list) {
			this.list = list;
		}
		
		@Override
		public ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_culture_searchandgo_activitypage_detail_content, viewGroup, false);
			return new ContentViewHolder(view);
		}
		
		@Override
		public void onBindViewHolder(ContentViewHolder viewHolder, int position) {
			AdditionalResultData item = list.get(position);
			viewHolder.bind(item);
		}
		
		@Override
		public int getItemCount() {
			return list.size();
		}
	}
	
	class ContentViewHolder extends RecyclerView.ViewHolder {
		private View view;
		private TextView typeTextView, contentTextView;
		private ImageView iconImageView;
		
		public ContentViewHolder(View itemView) {
			super(itemView);
			
			view = itemView;
			
			typeTextView = (TextView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_textview_type);
			
			iconImageView = (ImageView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_imageview_icon);
			contentTextView = (TextView) itemView.findViewById(R.id.item_culture_searchandgo_activitypage_detail_content_textview_content);
		}
		
		public void bind(AdditionalResultData additionalData) {
			typeTextView.setText(viewHelper.enumToStringCacheTranslation(additionalData.getType()));
			
			int targetRessourceId;
			switch (additionalData.getType()) {
				case ITEM_VIDEO: {
					targetRessourceId = R.drawable.icon_video_library_light;
					break;
				}
				
				case ITEM_CHAPTER: {
					targetRessourceId = R.drawable.icon_books_library_light;
					break;
				}
				
				default: {
					targetRessourceId = R.drawable.icon_close;
					break;
				}
			}
			
			iconImageView.setImageResource(targetRessourceId);
			
			contentTextView.setText(additionalData.convert());
		}
	}
	
}