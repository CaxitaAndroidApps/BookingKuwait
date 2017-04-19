package com.travel.booking_kuwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.travel.booking_kuwait.HotelResultActivity;
import com.travel.booking_kuwait.MarkerActivity;
import com.travel.booking_kuwait.R;
import com.travel.booking_kuwait.SearchPageActivity;
import com.travel.booking_kuwait.Support.CommonFunctions;
import com.travel.booking_kuwait.model.HotelResultItem;

import java.util.ArrayList;
import java.util.Locale;

public class HotelResultAdapter extends BaseAdapter{

	
	private Context context;
	private ArrayList<HotelResultItem> hotelResultItem;
//	String strSessionId;
	public HotelResultAdapter(Context context, ArrayList<HotelResultItem> hotelResultItem) {//, String strSessionId){
		this.context = context;
		this.hotelResultItem = hotelResultItem;
//		this.strSessionId = strSessionId;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return hotelResultItem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hotelResultItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.search_result_item_hotel, null);
            
        }
		
		final HotelResultItem hItem = hotelResultItem.get(position);
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.iv_hotel_logo);
		iv.setImageDrawable(null);
		if(!hItem.strHotelThumbImage.contains("no_image")){
			Bitmap bmp = HotelResultActivity.getBitmapFromMemCache(hItem.strHotelThumbImage);
			iv.setImageBitmap(bmp);
        }
		else
		{
			iv.setImageResource(R.drawable.ic_no_image);
		}
		
		
		((TextView) convertView.findViewById(R.id.tv_hotel_name)).setText(hItem.strHotelName);
        ((TextView) convertView.findViewById(R.id.tv_place)).setText(hItem.strHotelAddress);
        
        String price = String.format(new Locale("en"), "%.3f", Double.parseDouble(hItem.strDisplayRate));
        
        ((TextView) convertView.findViewById(R.id.tv_hotel_cost)).setText(CommonFunctions.strCurrency+" "+price);
        ((RatingBar) convertView.findViewById(R.id.rb_hotel_ratng)).setRating(hItem.floatHotelRating);
        
        if(hItem.strBoardTypes.toLowerCase().contains("breakfast") ||
        		hItem.strBoardTypes.toLowerCase().contains("الا�?طار")  ||
        		hItem.strBoardTypes.toLowerCase().contains("ا�?طار"))
        	((LinearLayout) convertView.findViewById(R.id.ll_breakfast_included)).setVisibility(View.VISIBLE);
        else
        	((LinearLayout) convertView.findViewById(R.id.ll_breakfast_included)).setVisibility(View.GONE);
        
        LinearLayout llMap = (LinearLayout) convertView.findViewById(R.id.ll_map);
        llMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent marker = new Intent(context, MarkerActivity.class);
				marker.putExtra("HotelName", 	hItem.strHotelName);
				marker.putExtra("HotelAddress", hItem.strHotelAddress);
				marker.putExtra("Latitude", 	hItem.strHotelLattitude);
				marker.putExtra("Langitude", 	hItem.strHotelLongitude);
				context.startActivity(marker);
				
			}
		});
        
        if(hItem.strHotelLattitude == 0.0 || hItem.strHotelLongitude == 0.0)
        	llMap.setVisibility(View.GONE);
        else
        	llMap.setVisibility(View.VISIBLE);
        
        ((LinearLayout) convertView.findViewById(R.id.ll_details)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent details = new Intent(context,
						SearchPageActivity.class);
				details.putExtra("url", hItem.strDeepLinkUrl);
				context.startActivity(details);
			}
		});
        
        return convertView;
	}

}
