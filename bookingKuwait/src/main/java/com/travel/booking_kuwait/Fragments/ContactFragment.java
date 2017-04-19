package com.travel.booking_kuwait.Fragments;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.travel.booking_kuwait.R;

import java.util.List;

public class ContactFragment extends Fragment {
	
	ImageButton ibFb, ibIn, ibTw;
	
	public ContactFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		final View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ibFb	=	(ImageButton) rootView.findViewById(R.id.ib_fb);
		ibIn	=	(ImageButton) rootView.findViewById(R.id.ib_in);
		ibTw	=	(ImageButton) rootView.findViewById(R.id.ib_tw);
		
		ibFb.setVisibility(View.GONE);
		ibIn.setVisibility(View.GONE);
		ibTw.setVisibility(View.GONE);
		
		ibFb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					Intent i=new Intent(Intent.ACTION_VIEW,
							Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/checknbook"));
					startActivity(i);
				}
		        catch(ActivityNotFoundException ex)
		        {    
	            	Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, 
	            			Uri.parse("https://www.facebook.com/checknbook"));
	                startActivity(unrestrictedIntent);
				}
			}
		});

		ibIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://checknbook"));
				final PackageManager packageManager = getActivity().getPackageManager();
				final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 
						PackageManager.MATCH_DEFAULT_ONLY);
				if (list.isEmpty()) {
				    intent = new Intent(Intent.ACTION_VIEW, 
				    		Uri.parse("http://www.linkedin.com/profile/view?id=checknbook"));
				}
				startActivity(intent);
			}
		});
		
		ibTw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/"));
					startActivity(i);
				}
		        catch(ActivityNotFoundException ex)
		        {
					Fragment fragment=new WebFragment();
					Bundle bundle = new Bundle();
					bundle.putString("Received URL","https://twitter.com/");
					android.app.FragmentManager fragmentManager = getFragmentManager();
					android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragment.setArguments(bundle);
					fragmentTransaction.replace(R.id.frame_container, fragment, "Web");
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.commit();
		        }
			}
		});
		
		return rootView;
		
	}
}
