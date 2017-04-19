package com.travel.booking_kuwait.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.travel.booking_kuwait.R;
import com.travel.booking_kuwait.Support.CommonFunctions;

public class WebFragment extends Fragment {
	
	ProgressBar pb_line;
	String url;
	int dp1;
	WebView wv1;
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		String url = this.getArguments().getString("Received URL");
		
		Log.d("URL",url);
		View rootView = inflater.inflate(R.layout.activity_web, container, false);
		wv1=(WebView) rootView.findViewById(R.id.wvContact);
		wv1.setVisibility(View.INVISIBLE);
		pb_line	  = (ProgressBar) rootView.findViewById(R.id.pb_line);

		wv1.getSettings().setRenderPriority(RenderPriority.HIGH);
		wv1.getSettings().setJavaScriptEnabled(true);
		final ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) wv1.getLayoutParams();

		wv1.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				Log.d("Started","Loading");
				
				if(!url.contains("http://www.matarat.com/"))
				{
					final float scale = getResources().getDisplayMetrics().density;
					int dp1 = (int) ((0) * scale + 0.5f);
					p.topMargin = dp1;

					wv1.setLayoutParams(p);
				}
				else
				{
					final float scale = getResources().getDisplayMetrics().density;
					if(CommonFunctions.lang.equalsIgnoreCase("en"))
						dp1 = (int) ((-100) * scale + 0.5f);
					else
						dp1 = (int) ((-105) * scale + 0.5f);
					p.topMargin = dp1;

					wv1.setLayoutParams(p);
				}
				
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
					
				//Inject javascript code to the url given
				//Not display the element
					
				wv1.setVisibility(View.VISIBLE);
				pb_line.setVisibility(View.GONE);
				
				Log.d("Finished","Loading");
				super.onPageFinished(view, url);
			}
			
			@Override 
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
				
				if(wv1.getProgress()==100){
					pb_line.setVisibility(View.INVISIBLE);
				}
				else
				{
					pb_line.setVisibility(View.VISIBLE);
				}
				pb_line.setProgress(wv1.getProgress());
			}
			
		});
		 
		/*wv1.setBackgroundColor(0x00000000); */
		wv1.loadUrl(url);
		
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		wv1.clearCache(true);
//		wv2.clearCache(true);
		wv1.destroy();
//		wv2.destroy();
		super.onDestroyView();
	}
	
}
