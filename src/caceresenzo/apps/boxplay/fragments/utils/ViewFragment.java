package caceresenzo.apps.boxplay.fragments.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import caceresenzo.apps.boxplay.R;

public class ViewFragment extends Fragment {
	
	public static final String TAG = ViewFragment.class.getSimpleName();
	
	private FrameLayout containerFrameLayout;
	private View targetView;
	
	public ViewFragment() {
		;
	}
	
	public ViewFragment(View view) {
		setTargetView(view);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view, container, false);
		containerFrameLayout = (FrameLayout) view.findViewById(R.id.fragment_view_framelayout_container);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (targetView != null) {
			containerFrameLayout.addView(targetView);
		}
	}
	
	public View getTargetView() {
		return targetView;
	}
	
	public void setTargetView(View targetView) {
		this.targetView = targetView;
	}
	
}