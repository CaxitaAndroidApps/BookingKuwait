package com.travel.booking_kuwait.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.travel.booking_kuwait.CommonFunctions;
import com.travel.booking_kuwait.FlightResultActivity;
import com.travel.booking_kuwait.R;
import com.travel.booking_kuwait.model.FlightResultItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FlightResultAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<FlightResultItem> flightResultItem;
	private boolean isRoundTrip = false;
	String strBaggageDetails = null;
	CommonFunctions cf;
	
	public FlightResultAdapter(Context context, ArrayList<FlightResultItem> flightResultItem, boolean isRoundTrip){
		this.context = context;
		this.flightResultItem = flightResultItem;
		this.isRoundTrip = isRoundTrip;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return flightResultItem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return flightResultItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		boolean blStart = false;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
	                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.search_result_item_flight, null);
            blStart = true;
        }
		
		final FlightResultItem fItem  = flightResultItem.get(position);
		cf = new CommonFunctions(context);
		JSONObject allJourney, listFlight, segment;
		JSONArray jarray, listFlightArray, segmentArray;
    	ImageView ivFlightLogo;
    	InputStream ims;
		Drawable d;
		LinearLayout llFlightResult;
    	String flightName, flightCode, flightNumber, stops, equipmentNo;
    	
		jarray 		= fItem.jarray;
		
		String price = String.format(new Locale("en"), "%.3f", Double.parseDouble(fItem.strDisplayRate));
		((TextView) convertView.findViewById(R.id.tv_flight_price)).setText(CommonFunctions.strCurrency+" "+price);
		
		if(fItem.blRefundType){
			((TextView) convertView.findViewById(R.id.tv_refundable)).setText(context.getResources().getString(R.string.refund));
			((TextView) convertView.findViewById(R.id.tv_refundable)).setTextColor(Color.parseColor("#008000")); 
		} else {
			((TextView) convertView.findViewById(R.id.tv_refundable)).setText(context.getResources().getString(R.string.non_refund));
			((TextView) convertView.findViewById(R.id.tv_refundable)).setTextColor(Color.parseColor("#B71C1C")); 
		}
		
		llFlightResult = (LinearLayout) convertView.findViewById(R.id.ll_flight_items);
		
		try{
			LayoutInflater mInflater = (LayoutInflater)
	                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			for(int i=0; i<jarray.length(); i++)
			{
				View vFlightItem = null;
				if(blStart)
					vFlightItem = mInflater.inflate(R.layout.flight_result_item_trip_details, null);
				else {
					vFlightItem = llFlightResult.getChildAt(i);
				}
				
				if(i == 1) {
					if(isRoundTrip && CommonFunctions.lang.equalsIgnoreCase("en"))
						((ImageView) vFlightItem.findViewById(R.id.iv_trip_type)).setImageResource(R.drawable.ic_return);
					else if (isRoundTrip)
						((ImageView) vFlightItem.findViewById(R.id.iv_trip_type)).setImageResource(R.drawable.ic_onward);
				}
				
				allJourney = jarray.getJSONObject(i);
				listFlightArray = allJourney.getJSONArray("ListFlight");
				listFlight	= listFlightArray.getJSONObject(0);
				ivFlightLogo = (ImageView) vFlightItem.findViewById(R.id.iv_flight_logo);
				try 
				{
				    // get input stream
				    ims = context.getAssets().open(listFlight.getString("FlightLogo"));
				    // load image as Drawable
				    d = Drawable.createFromStream(ims, null);
				    // set image to ImageView
				    ivFlightLogo.setImageDrawable(d);
				    ims.close();
				    d = null;
				}
				catch(IOException ex) 
				{
					ex.printStackTrace();
					ivFlightLogo.setImageResource(R.drawable.ic_no_image);
				}
				
				flightName		= listFlight.getString("FlightName");		
				flightCode 		= listFlight.getString("FlightCode");
				equipmentNo		= listFlight.getString("EquipmentNumber");
				flightNumber	= listFlight.getString("FlightNumber");
				
				((TextView) vFlightItem.findViewById(R.id.tv_from)).setText(listFlight.getString("DepartureCityName"));
				((TextView) vFlightItem.findViewById(R.id.tv_airline_name)).setText(flightName);
				((TextView) vFlightItem.findViewById(R.id.tv_equipment)).setText(equipmentNo);
				
				String totalDuration = allJourney.getString("TotalDuration");
				
				if(allJourney.getInt("stops") == 0)
					stops = context.getResources().getString(R.string.non_stop);
				else if(allJourney.getInt("stops") == 1)
					stops = allJourney.getInt("stops") + " " + context.getResources().getString(R.string.one_stop);
				else
					stops = allJourney.getInt("stops") + " " + context.getResources().getString(R.string.more_stop);
				
				((TextView) vFlightItem.findViewById(R.id.tv_flight_time)).setText(totalDuration);
				((TextView) vFlightItem.findViewById(R.id.tv_flight_stops)).setText(stops);
				
				listFlight	= listFlightArray.getJSONObject(listFlightArray.length()-1);
				((TextView) vFlightItem.findViewById(R.id.tv_to)).setText(listFlight.getString("ArrivalCityName"));
				
				String strFlightDetails = "";
				LinearLayout llTransit = (LinearLayout) vFlightItem.findViewById(R.id.ll_transit);
				LinearLayout llBaggage = (LinearLayout) vFlightItem.findViewById(R.id.ll_baggage);
				LinearLayout llFlightDetails = (LinearLayout) vFlightItem.findViewById(R.id.ll_flight_details_list);
				
				int listFlightArrayCount = listFlightArray.length();
				
				boolean blReuse = llTransit.getChildCount() == listFlightArrayCount ? true : false;
				
				if(!blReuse) {
					llTransit.removeAllViews();
					llBaggage.removeAllViews();
					llFlightDetails.removeAllViews();
				}
				
				for(int j = 0; j < listFlightArray.length(); ++j)
				{
					View vTransitItemView = null;
					View vBaggageView = null;
					View vFlightDetails = null;
					
					if(!blReuse) {
						vTransitItemView = mInflater.inflate(R.layout.transit_airport_item, null);
						vBaggageView = mInflater.inflate(R.layout.baggage_info_item, null);
						vFlightDetails = mInflater.inflate(R.layout.item_flight_details, null);
					} else {
						vTransitItemView = llTransit.getChildAt(j);
						vBaggageView = llBaggage.getChildAt(j);
						vFlightDetails = llFlightDetails.getChildAt(j);
					}
					

					listFlight	= listFlightArray.getJSONObject(j);
					
					((TextView) vTransitItemView.findViewById(R.id.tv_depart_time)).setText(listFlight.getString("DepartureTimeString"));
					((TextView) vTransitItemView.findViewById(R.id.tv_arrival_time)).setText(listFlight.getString("ArrivalTimeString"));
					((TextView) vTransitItemView.findViewById(R.id.tv_depart_city)).setText(listFlight.getString("DepartureCityName"));
					((TextView) vTransitItemView.findViewById(R.id.tv_arrival_city)).setText(listFlight.getString("ArrivalCityName"));
				
					((TextView) vTransitItemView.findViewById(R.id.tv_transit_time)).setText(listFlight.getString("TransitTime"));

					strFlightDetails = context.getResources().getString(R.string.from) + " " + listFlight.getString("DepartureAirportName") + " " +
							context.getResources().getString(R.string.to) + " " + listFlight.getString("ArrivalAirportName");
					((TextView) vFlightDetails.findViewById(R.id.tv_airport_details)).setText(strFlightDetails);
					
					strFlightDetails = 
							listFlight.getString("FlightName") + " | " + listFlight.getString("EquipmentNumber") + " | " 
							+ context.getResources().getString(R.string.booking_class) + " : " + listFlight.getString("BookingCode") + " | "
							+ context.getResources().getString(R.string.meals) + " : " + listFlight.getString("MealCode");
					
					((TextView) vFlightDetails.findViewById(R.id.tv_flight_info)).setText(strFlightDetails);
					((TextView) vFlightDetails.findViewById(R.id.tv_depart_date)).setText(listFlight.getString("DepartureDateString"));
					((TextView) vFlightDetails.findViewById(R.id.tv_arrival_date)).setText(listFlight.getString("ArrivalDateString"));
					((TextView) vFlightDetails.findViewById(R.id.tv_depart_time)).setText(listFlight.getString("DepartureTimeString"));
					((TextView) vFlightDetails.findViewById(R.id.tv_arrival_time)).setText(listFlight.getString("ArrivalTimeString"));
					((TextView) vFlightDetails.findViewById(R.id.tv_duration)).setText(listFlight.getString("DurationPerLeg"));
					
					if(listFlightArray.length() == 1)
					{
						((TextView) vTransitItemView.findViewById(R.id.tv_direct)).setVisibility(View.VISIBLE);
						((TextView) vTransitItemView.findViewById(R.id.tv_transit_time)).setVisibility(View.GONE);
					}
					else
					{
						((TextView) vTransitItemView.findViewById(R.id.tv_direct)).setVisibility(View.GONE);
						((TextView) vTransitItemView.findViewById(R.id.tv_transit_time)).setVisibility(View.VISIBLE);
						if(listFlight.getString("TransitTime").equals("")) {
							((TextView) vTransitItemView.findViewById(R.id.tv_transit_time)).setVisibility(View.GONE);
							((LinearLayout) vFlightDetails.findViewById(R.id.ll_transit_time)).setVisibility(View.GONE);
						}
						else
						{
							((TextView) vFlightDetails.findViewById(R.id.tv_transit_time)).setText(listFlight.getString("TransitTime"));
							((LinearLayout) vFlightDetails.findViewById(R.id.ll_transit_time)).setVisibility(View.VISIBLE);
						}
					}
					
					flightCode 		= listFlight.getString("FlightCode");
					flightNumber	= listFlight.getString("FlightNumber");
					
					if(fItem.intApiId == 26)
					{
						if(CommonFunctions.lang.equalsIgnoreCase("en"))
							((TextView) vBaggageView.findViewById(R.id.tv_flight_number)).setText(flightCode+" - "+flightNumber+" : ");
						else
							((TextView) vBaggageView.findViewById(R.id.tv_flight_number)).setText(" : "+flightNumber+" - "+flightCode);
						
						((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_adult)).setText(context.getResources().getString(R.string.baggage_travel_fusion));
						((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_child)).setVisibility(View.GONE);
						((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_infant)).setVisibility(View.GONE);
					}
					else {
						String baggage = null;
						segmentArray = listFlight.getJSONArray("SegmentDetails");
						for(int  k = 0; k < segmentArray.length(); ++k)
						{
							segment = segmentArray.getJSONObject(k);
							if(k ==1 && !FlightResultActivity.blChild)
								k++;
							
							switch(k){
							case 0: baggage = segment.getString("Baggage");
									if(CommonFunctions.lang.equalsIgnoreCase("en"))
									{
										((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_adult)).setText(context.getResources().getString(R.string.adult) + " - "+baggage);
										((TextView) vBaggageView.findViewById(R.id.tv_flight_number)).setText(flightCode+" - "+flightNumber+" : ");
									}
									else
									{
										((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_adult)).setText(baggage + " - "+ context.getResources().getString(R.string.adult));
										((TextView) vBaggageView.findViewById(R.id.tv_flight_number)).setText(" : "+flightNumber+" - "+flightCode);
									}
									
									break;
							case 1: baggage = segment.getString("Baggage");
									((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_child)).setVisibility(View.VISIBLE);
									if(CommonFunctions.lang.equalsIgnoreCase("en"))
									{
										((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_child)).setText(context.getResources().getString(R.string.children) + " - "+baggage);
									}
									else
									{
										((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_child)).setText(baggage + " - "+context.getResources().getString(R.string.children));
									}
									break;
							case 2: baggage = segment.getString("Baggage");
									((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_infant)).setVisibility(View.VISIBLE);
									if(CommonFunctions.lang.equalsIgnoreCase("en"))
									{
										((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_infant)).setText(context.getResources().getString(R.string.infant) + " - "+baggage);
									}
									else
									{
										((TextView) vBaggageView.findViewById(R.id.tv_baggage_info_infant)).setText(baggage + " - "+context.getResources().getString(R.string.infant));
									}
									break;
							}
						}
					}
					if(!blReuse) {
						llTransit.addView(vTransitItemView);
						llFlightDetails.addView(vFlightDetails);
						llBaggage.addView(vBaggageView);
					}
				}
				
				final LinearLayout llflight_dtls = (LinearLayout) vFlightItem.findViewById(R.id.ll_flight_details);
				llflight_dtls.setVisibility(View.GONE);
				final LinearLayout llBaggageInfo = (LinearLayout) vFlightItem.findViewById(R.id.ll_baggage_info);
				llBaggageInfo.setVisibility(View.GONE);
				
				((TextView) vFlightItem.findViewById(R.id.tv_flight_details)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(llflight_dtls.getVisibility() == View.VISIBLE)
							llflight_dtls.setVisibility(View.GONE);
						else{
							llflight_dtls.setVisibility(View.VISIBLE);
							llBaggageInfo.setVisibility(View.GONE);
						}
					}
				});
				
				
				((TextView) vFlightItem.findViewById(R.id.tv_baggage_info)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(llBaggageInfo.getVisibility() == View.VISIBLE)
							llBaggageInfo.setVisibility(View.GONE);
						else{
							llflight_dtls.setVisibility(View.GONE);
							llBaggageInfo.setVisibility(View.VISIBLE);
						}
					}
				});
				if(blStart)
					llFlightResult.addView(vFlightItem);
			}
		} catch(Exception ex) 
		{
			ex.printStackTrace();
		}
		
        return convertView;
	}

}
