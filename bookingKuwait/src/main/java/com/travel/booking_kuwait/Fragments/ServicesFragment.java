package com.travel.booking_kuwait.Fragments;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.travel.booking_kuwait.R;

public class ServicesFragment extends Fragment {
	
	public ServicesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		final View rootView = inflater.inflate(R.layout.fragment_services, container, false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		return rootView;
		
	}
}
