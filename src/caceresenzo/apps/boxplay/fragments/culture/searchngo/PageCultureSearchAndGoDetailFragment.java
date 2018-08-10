package caceresenzo.apps.boxplay.fragments.culture.searchngo;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.fragments.culture.CultureFragment;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class PageCultureSearchAndGoDetailFragment extends Fragment {
	
	private List<AdditionalResultData> localResults = new ArrayList<>();
	
	private RecyclerView recyclerView;
	
	private AdditionalResultViewAdapter adapter;
	
	private Worker worker = new Worker();
	
	public PageCultureSearchAndGoDetailFragment() {
		PageCultureSearchAndGoFragment.detailFragment = this;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.test_fragment_recyclerview, container, false);
		
		recyclerView = (RecyclerView) view.findViewById(R.id.test_fragment_recyclerview_recyclerview_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(adapter = new AdditionalResultViewAdapter(localResults));
		recyclerView.setHasFixedSize(true);
		recyclerView.setNestedScrollingEnabled(false);
		
		return view;
	}
	
	public void applyResult(SearchAndGoResult result) {
		if (worker.running) {
			BoxPlayActivity.getBoxPlayActivity().toast("DetailWorker is busy").show();
			return;
		}
		
		worker = new Worker();
		worker.applyResult(result).start();
	}
	
	private class Worker extends Thread {
		public boolean running = false;
		
		private SearchAndGoResult result;
		
		@Override
		public void run() {
			running = true;
			
			final List<AdditionalResultData> additionals = result.getParentProvider().fetchMoreData(result);
			
			if (additionals != null) {
				BoxPlayActivity.getHandler().post(new Runnable() {
					
					@Override
					public void run() {
						localResults.clear();
						localResults.addAll(additionals);
						adapter.notifyDataSetChanged();
						
						CultureFragment.getCultureFragment().withSearchAndGoDetail();
					}
				});
			}
			
			running = false;
		}
		
		public Worker applyResult(SearchAndGoResult result) {
			this.result = result;
			return this;
		}
	}
	
	class AdditionalResultViewAdapter extends RecyclerView.Adapter<AdditionalResultViewHolder> {
		private List<AdditionalResultData> list;
		
		public AdditionalResultViewAdapter(List<AdditionalResultData> list) {
			this.list = list;
		}
		
		@Override
		public AdditionalResultViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_culture_searchngo_detail_info, viewGroup, false);
			return new AdditionalResultViewHolder(view);
		}
		
		@Override
		public void onBindViewHolder(AdditionalResultViewHolder viewHolder, int position) {
			AdditionalResultData item = list.get(position);
			viewHolder.bind(item);
		}
		
		@Override
		public int getItemCount() {
			return list.size();
		}
	}
	
	class AdditionalResultViewHolder extends RecyclerView.ViewHolder {
		private TextView typeTextView, contentTextView;
		
		public AdditionalResultViewHolder(View itemView) {
			super(itemView);
			
			typeTextView = (TextView) itemView.findViewById(R.id.item_culture_searchandgo_detail_info_textview_type);
			contentTextView = (TextView) itemView.findViewById(R.id.item_culture_searchandgo_detail_info_textview_content);
		}
		
		public void bind(AdditionalResultData additionalResultData) {
			typeTextView.setText(additionalResultData.getType().toString());
			contentTextView.setText(additionalResultData.convert());
			// contentTextView.setText(additionalResultData.getData().toString());
		}
	}
	
}