package com.travel.booking_kuwait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.travel.booking_kuwait.model.Room;
import com.travel.booking_kuwait.R;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HotelFragment extends Fragment implements View.OnClickListener {

	ImageView iv;
	TextView tvCity, tvNationality, tvHeader, tvNoResult;
	TextView tvCheckinDate, tvCheckoutDate;
	LinearLayout llCheckinDate, llCheckoutDate;
	LinearLayout llRoom1, llRoom2, llRoom3, llRoom4, llRoom5;
	LinearLayout llCityname, llNationality;
	Spinner spAdult1Count, spAdult2Count, spAdult3Count, spAdult4Count,
			spAdult5Count;
	Spinner spChild1Count, spChild2Count, spChild3Count, spChild4Count,
			spChild5Count;
	Spinner spChild1Age_1, spChild2Age_1, spChild3Age_1;
	Spinner spChild1Age_2, spChild2Age_2, spChild3Age_2;
	Spinner spChild1Age_3, spChild2Age_3, spChild3Age_3;
	Spinner spChild1Age_4, spChild2Age_4, spChild3Age_4;
	Spinner spChild1Age_5, spChild2Age_5, spChild3Age_5;
	Spinner spRoomCount;
	Button searchbtn;
	ImageButton imgbtnClearCity, imgbtnClearNationality, ibClose;
	AutoCompleteTextView actSuggestion;
	String strCity, strNationality;
	String strCheckinDate, strCheckoutDate;
	int roomCount = 1;
	String main_url = null;

	// temp values
	int adult_count, child_count, child1age, child2age;

	SimpleDateFormat sdfPrint, sdfUrl;
	Date selcheckindate, selCheckoutdate, minCheckout;

	Calendar currday;
	Date nextYearCheckin, nextYearCheckout;
	ArrayList<String> arrayCountry, arrayCity;

	private Dialog dialogDate, dialogSuggestion;
	View rootView;
	AssetManager am;
	CommonFunctions cf;
	Room[] rooms = new Room[5];

	Intent inte;
	
	public HotelFragment() {
		// TODO Auto-generated constructor stub
		rootView = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		cf = new CommonFunctions(getActivity().getApplicationContext());

		rootView = inflater.inflate(R.layout.fragment_hotel, container, false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

		am = getActivity().getAssets();
		new backMethod().execute();
		initialize();
		setListeners();

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(CommonFunctions.modify)
			iv.setVisibility(View.VISIBLE);
		else
			iv.setVisibility(View.GONE);
		super.onResume();
	}
	
	private void initialize() {
		// TODO Auto-generated method stub
		iv = (ImageView) rootView.findViewById(R.id.iv_close);
		
		tvCity = (TextView) rootView.findViewById(R.id.tv_city);
		tvNationality = (TextView) rootView.findViewById(R.id.tv_nationality);
		tvCheckinDate = (TextView) rootView.findViewById(R.id.tv_check_in_date);
		tvCheckoutDate = (TextView) rootView
				.findViewById(R.id.tv_check_out_date);
		llRoom1 = (LinearLayout) rootView.findViewById(R.id.ll_room1);
		llRoom2 = (LinearLayout) rootView.findViewById(R.id.ll_room2);
		llRoom3 = (LinearLayout) rootView.findViewById(R.id.ll_room3);
		llRoom4 = (LinearLayout) rootView.findViewById(R.id.ll_room4);
		llRoom5 = (LinearLayout) rootView.findViewById(R.id.ll_room5);
		llCheckinDate = (LinearLayout) rootView.findViewById(R.id.ll_check_in);
		llCheckoutDate = (LinearLayout) rootView
				.findViewById(R.id.ll_check_out);
		llCityname = (LinearLayout) rootView.findViewById(R.id.ll_city_name);
		llNationality = (LinearLayout) rootView
				.findViewById(R.id.ll_nationality);
		spRoomCount = (Spinner) rootView.findViewById(R.id.sp_room_nos);
		spAdult1Count = (Spinner) rootView.findViewById(R.id.sp_adult_count_1);
		spAdult2Count = (Spinner) rootView.findViewById(R.id.sp_adult_count_2);
		spAdult3Count = (Spinner) rootView.findViewById(R.id.sp_adult_count_3);
		spAdult4Count = (Spinner) rootView.findViewById(R.id.sp_adult_count_4);
		spAdult5Count = (Spinner) rootView.findViewById(R.id.sp_adult_count_5);
		spChild1Count = (Spinner) rootView.findViewById(R.id.sp_child_count_1);
		spChild2Count = (Spinner) rootView.findViewById(R.id.sp_child_count_2);
		spChild3Count = (Spinner) rootView.findViewById(R.id.sp_child_count_3);
		spChild4Count = (Spinner) rootView.findViewById(R.id.sp_child_count_4);
		spChild5Count = (Spinner) rootView.findViewById(R.id.sp_child_count_5);

		spChild1Age_1 = (Spinner) rootView.findViewById(R.id.sp_child1_age_1);
		spChild1Age_2 = (Spinner) rootView.findViewById(R.id.sp_child1_age_2);
		spChild1Age_3 = (Spinner) rootView.findViewById(R.id.sp_child1_age_3);
		spChild1Age_4 = (Spinner) rootView.findViewById(R.id.sp_child1_age_4);
		spChild1Age_5 = (Spinner) rootView.findViewById(R.id.sp_child1_age_5);
		spChild2Age_1 = (Spinner) rootView.findViewById(R.id.sp_child2_age_1);
		spChild2Age_2 = (Spinner) rootView.findViewById(R.id.sp_child2_age_2);
		spChild2Age_3 = (Spinner) rootView.findViewById(R.id.sp_child2_age_3);
		spChild2Age_4 = (Spinner) rootView.findViewById(R.id.sp_child2_age_4);
		spChild2Age_5 = (Spinner) rootView.findViewById(R.id.sp_child2_age_5);
		spChild3Age_1 = (Spinner) rootView.findViewById(R.id.sp_child3_age_1);
		spChild3Age_2 = (Spinner) rootView.findViewById(R.id.sp_child3_age_2);
		spChild3Age_3 = (Spinner) rootView.findViewById(R.id.sp_child3_age_3);
		spChild3Age_4 = (Spinner) rootView.findViewById(R.id.sp_child3_age_4);
		spChild3Age_5 = (Spinner) rootView.findViewById(R.id.sp_child3_age_5);

		searchbtn = (Button) rootView.findViewById(R.id.btn_search);
		imgbtnClearCity = (ImageButton) rootView
				.findViewById(R.id.imgbtn_close_city);
		imgbtnClearNationality = (ImageButton) rootView
				.findViewById(R.id.imgbtn_close_nationality);

		strCity = strNationality = "";
		strCheckinDate = strCheckoutDate = null;

		String[] classArray = this.getResources().getStringArray(
				R.array.room_spinner_items);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.tv_spinner, classArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRoomCount.setAdapter(adapter);

		classArray = new String[] { "1", "2", "3", "4", "5" };
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.tv_spinner,
				classArray);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spAdult1Count.setAdapter(adapter);
		spAdult2Count.setAdapter(adapter);
		spAdult3Count.setAdapter(adapter);
		spAdult4Count.setAdapter(adapter);
		spAdult5Count.setAdapter(adapter);

		spAdult1Count.setSelection(1);
		spAdult2Count.setSelection(1);
		spAdult3Count.setSelection(1);
		spAdult4Count.setSelection(1);
		spAdult5Count.setSelection(1);
		
		classArray = new String[] { "0", "1", "2" };
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.tv_spinner,
				classArray);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spChild1Count.setAdapter(adapter);
		spChild2Count.setAdapter(adapter);
		spChild3Count.setAdapter(adapter);
		spChild4Count.setAdapter(adapter);
		spChild5Count.setAdapter(adapter);

		classArray = new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "10", "11" };
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.tv_spinner,
				classArray);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spChild1Age_1.setAdapter(adapter);
		spChild1Age_2.setAdapter(adapter);
		spChild1Age_3.setAdapter(adapter);
		spChild1Age_4.setAdapter(adapter);
		spChild1Age_5.setAdapter(adapter);
		spChild2Age_1.setAdapter(adapter);
		spChild2Age_2.setAdapter(adapter);
		spChild2Age_3.setAdapter(adapter);
		spChild2Age_4.setAdapter(adapter);
		spChild2Age_5.setAdapter(adapter);
		spChild3Age_1.setAdapter(adapter);
		spChild3Age_2.setAdapter(adapter);
		spChild3Age_3.setAdapter(adapter);
		spChild3Age_4.setAdapter(adapter);
		spChild3Age_5.setAdapter(adapter);

		spChild1Age_1.setSelection(1);
		spChild1Age_2.setSelection(1);
		spChild1Age_3.setSelection(1);
		spChild1Age_4.setSelection(1);
		spChild1Age_5.setSelection(1);
		spChild2Age_1.setSelection(1);
		spChild2Age_2.setSelection(1);
		spChild2Age_3.setSelection(1);
		spChild2Age_4.setSelection(1);
		spChild2Age_5.setSelection(1);
		spChild3Age_1.setSelection(1);
		spChild3Age_2.setSelection(1);
		spChild3Age_3.setSelection(1);
		spChild3Age_4.setSelection(1);
		spChild3Age_5.setSelection(1);
		
		rooms[0] = new Room();
		showRooms();

		currday = Calendar.getInstance();
		nextYearCheckin = currday.getTime();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 312);
		nextYearCheckin = cal.getTime();

		sdfUrl = new SimpleDateFormat("ddMMMyyyy", new Locale("en"));
		sdfPrint = new SimpleDateFormat("dd/MM/yyyy", new Locale("en"));

		// to set next day
		currday.add(Calendar.DATE, 1);
		tvCheckinDate.setText(sdfPrint.format(currday.getTime()));
		strCheckinDate = sdfUrl.format(currday.getTime());

		selcheckindate = currday.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 2);
		minCheckout = cal.getTime();
		selCheckoutdate = minCheckout;

		tvCheckoutDate.setText(sdfPrint.format(minCheckout.getTime()));
		strCheckoutDate = sdfUrl.format(minCheckout.getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 313);
		nextYearCheckout = cal.getTime();

		imgbtnClearCity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvCity.setText(null);
				strCity = "";
				imgbtnClearCity.setVisibility(View.GONE);
			}
		});

		imgbtnClearNationality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvNationality.setText(null);
				strNationality = "";
				imgbtnClearNationality.setVisibility(View.GONE);
			}
		});

		dialogSuggestion = new Dialog(getActivity(),
				android.R.style.Theme_Translucent);
		dialogSuggestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSuggestion.getWindow().setGravity(Gravity.TOP);
		dialogSuggestion.setContentView(R.layout.dialog_suggestion);
		dialogSuggestion.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		tvHeader = (TextView) dialogSuggestion.findViewById(R.id.tv_header);
		tvNoResult = (TextView) dialogSuggestion.findViewById(R.id.tv_no_match);
		ibClose = (ImageButton) dialogSuggestion.findViewById(R.id.ib_close);
		actSuggestion = (AutoCompleteTextView) dialogSuggestion
				.findViewById(R.id.act_view);

		dialogDate = new Dialog(getActivity(),
				android.R.style.Theme_Translucent);
		dialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDate.getWindow().setGravity(Gravity.TOP);
		dialogDate.setContentView(R.layout.dialog_date_picker);

		strNationality = "KW";
		tvNationality.setText("Kuwait");
		imgbtnClearNationality.setVisibility(View.VISIBLE);
	}

	public void setListeners() {
		// TODO Auto-generated method stub
		iv.setOnClickListener(this);
		llCheckinDate.setOnClickListener(this);
		llCheckoutDate.setOnClickListener(this);
		llCityname.setOnClickListener(this);
		llNationality.setOnClickListener(this);
		searchbtn.setOnClickListener(this);

		spRoomCount.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				roomCount = position + 1;
				showRooms();
				if (roomCount == 1)
				{
					rooms[1] = new Room();
					rooms[2] = new Room();
					rooms[3] = new Room();
					rooms[4] = new Room();
				} else if (roomCount == 2)
				{
					rooms[2] = new Room();
					rooms[3] = new Room();
					rooms[4] = new Room();
				}
				else if (roomCount == 3)
				{
					rooms[3] = new Room();
					rooms[4] = new Room();
				}
				else if (roomCount == 4)
				{
					rooms[4] = new Room();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
		
		OnItemSelectedListener listenerAdultCount = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				switch (parent.getId()) {
				case R.id.sp_adult_count_1:
					rooms[0].adult_count = position + 1;
					break;
				case R.id.sp_adult_count_2:
					rooms[1].adult_count = position + 1;
					break;
				case R.id.sp_adult_count_3:
					rooms[2].adult_count = position + 1;
					break;
				case R.id.sp_adult_count_4:
					rooms[3].adult_count = position + 1;
					break;
				case R.id.sp_adult_count_5:
					rooms[4].adult_count = position + 1;
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		};
		
		OnItemSelectedListener listenerChildCount = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				switch (parent.getId()) {
				case R.id.sp_child_count_1:
					rooms[0].child_count = position;
					switch (position) {
					case 0:	((LinearLayout) rootView.findViewById(R.id.ll_child1_age_1)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_1)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_1)).setVisibility(View.GONE);
							spChild1Age_1.setSelection(1);
							spChild2Age_1.setSelection(1);
							spChild3Age_1.setSelection(1);
							break;
					case 1: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_1)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_1)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_1)).setVisibility(View.GONE);
							spChild2Age_1.setSelection(1);
							spChild3Age_1.setSelection(1);
							break;
					case 2: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_1)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_1)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_1)).setVisibility(View.GONE);
							spChild3Age_1.setSelection(1);
							break;
					case 3: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_1)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_1)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_1)).setVisibility(View.VISIBLE);
							break;
					}
					break;
				case R.id.sp_child_count_2:
					rooms[1].child_count = position;
					switch (position) {
					case 0:	((LinearLayout) rootView.findViewById(R.id.ll_child1_age_2)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_2)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_2)).setVisibility(View.GONE);
							spChild1Age_2.setSelection(1);
							spChild2Age_2.setSelection(1);
							spChild3Age_2.setSelection(1);
							break;
					case 1: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_2)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_2)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_2)).setVisibility(View.GONE);
							spChild2Age_2.setSelection(1);
							spChild3Age_2.setSelection(1);
							break;
					case 2: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_2)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_2)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_2)).setVisibility(View.GONE);
							spChild3Age_2.setSelection(1);
							break;
					case 3: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_2)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_2)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_2)).setVisibility(View.VISIBLE);
							break;
					}
					break;
				case R.id.sp_child_count_3:
					rooms[2].child_count = position;
					switch (position) {
					case 0:	((LinearLayout) rootView.findViewById(R.id.ll_child1_age_3)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_3)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_3)).setVisibility(View.GONE);
							spChild1Age_3.setSelection(1);
							spChild2Age_3.setSelection(1);
							spChild3Age_3.setSelection(1);
							break;
					case 1: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_3)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_3)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_3)).setVisibility(View.GONE);
							spChild2Age_3.setSelection(1);
							spChild3Age_3.setSelection(1);
							break;
					case 2: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_3)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_3)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_3)).setVisibility(View.GONE);
							spChild3Age_3.setSelection(1);
							break;
					case 3: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_3)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_3)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_3)).setVisibility(View.VISIBLE);
							break;
					}
					break;
				case R.id.sp_child_count_4:
					rooms[3].child_count = position;
					switch (position) {
					case 0:	((LinearLayout) rootView.findViewById(R.id.ll_child1_age_4)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_4)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_4)).setVisibility(View.GONE);
							spChild1Age_4.setSelection(1);
							spChild2Age_4.setSelection(1);
							spChild3Age_4.setSelection(1);
							break;
					case 1: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_4)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_4)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_4)).setVisibility(View.GONE);
							spChild2Age_4.setSelection(1);
							spChild3Age_4.setSelection(1);
							break;
					case 2: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_4)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_4)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_4)).setVisibility(View.GONE);
							spChild3Age_4.setSelection(1);
							break;
					case 3: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_4)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_4)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_4)).setVisibility(View.VISIBLE);
							break;
					}
					break;
				case R.id.sp_child_count_5:
					rooms[4].child_count = position;
					switch (position) {
					case 0:	((LinearLayout) rootView.findViewById(R.id.ll_child1_age_5)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_5)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_5)).setVisibility(View.GONE);
							spChild1Age_5.setSelection(1);
							spChild2Age_5.setSelection(1);
							spChild3Age_5.setSelection(1);
							break;
					case 1: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_5)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_5)).setVisibility(View.INVISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_5)).setVisibility(View.GONE);
							spChild2Age_5.setSelection(1);
							spChild3Age_5.setSelection(1);
							break;
					case 2: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_5)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_5)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_5)).setVisibility(View.GONE);
							spChild3Age_5.setSelection(1);
							break;
					case 3: ((LinearLayout) rootView.findViewById(R.id.ll_child1_age_5)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child2_age_5)).setVisibility(View.VISIBLE);
							((LinearLayout) rootView.findViewById(R.id.ll_child3_age_5)).setVisibility(View.VISIBLE);
							break;
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		};
		
		OnItemSelectedListener listenerChildAge = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (parent.getId()) {
				case R.id.sp_child1_age_1: rooms[0].setChild1Age(position+1); break;
				case R.id.sp_child2_age_1: rooms[0].setChild2Age(position+1); break;
				case R.id.sp_child3_age_1: rooms[0].setChild3Age(position+1); break;
				
				case R.id.sp_child1_age_2: rooms[1].setChild1Age(position+1); break;
				case R.id.sp_child2_age_2: rooms[1].setChild2Age(position+1); break;
				case R.id.sp_child3_age_2: rooms[1].setChild3Age(position+1); break;
				
				case R.id.sp_child1_age_3: rooms[2].setChild1Age(position+1); break;
				case R.id.sp_child2_age_3: rooms[2].setChild2Age(position+1); break;
				case R.id.sp_child3_age_3: rooms[2].setChild3Age(position+1); break;
				
				case R.id.sp_child1_age_4: rooms[3].setChild1Age(position+1); break;
				case R.id.sp_child2_age_4: rooms[3].setChild2Age(position+1); break;
				case R.id.sp_child3_age_4: rooms[3].setChild3Age(position+1); break;
				
				case R.id.sp_child1_age_5: rooms[4].setChild1Age(position+1); break;
				case R.id.sp_child2_age_5: rooms[4].setChild2Age(position+1); break;
				case R.id.sp_child3_age_5: rooms[4].setChild3Age(position+1); break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		};

		spAdult1Count.setOnItemSelectedListener(listenerAdultCount);
		spAdult2Count.setOnItemSelectedListener(listenerAdultCount);
		spAdult3Count.setOnItemSelectedListener(listenerAdultCount);
		spAdult4Count.setOnItemSelectedListener(listenerAdultCount);
		spAdult5Count.setOnItemSelectedListener(listenerAdultCount);
		spChild1Count.setOnItemSelectedListener(listenerChildCount);
		spChild2Count.setOnItemSelectedListener(listenerChildCount);
		spChild3Count.setOnItemSelectedListener(listenerChildCount);
		spChild4Count.setOnItemSelectedListener(listenerChildCount);
		spChild5Count.setOnItemSelectedListener(listenerChildCount);
		
		spChild1Age_1.setOnItemSelectedListener(listenerChildAge);
		spChild1Age_2.setOnItemSelectedListener(listenerChildAge);
		spChild1Age_3.setOnItemSelectedListener(listenerChildAge);
		spChild1Age_4.setOnItemSelectedListener(listenerChildAge);
		spChild1Age_5.setOnItemSelectedListener(listenerChildAge);
		spChild2Age_1.setOnItemSelectedListener(listenerChildAge);
		spChild2Age_2.setOnItemSelectedListener(listenerChildAge);
		spChild2Age_3.setOnItemSelectedListener(listenerChildAge);
		spChild2Age_4.setOnItemSelectedListener(listenerChildAge);
		spChild2Age_5.setOnItemSelectedListener(listenerChildAge);
		spChild3Age_1.setOnItemSelectedListener(listenerChildAge);
		spChild3Age_2.setOnItemSelectedListener(listenerChildAge);
		spChild3Age_3.setOnItemSelectedListener(listenerChildAge);
		spChild3Age_4.setOnItemSelectedListener(listenerChildAge);
		spChild3Age_5.setOnItemSelectedListener(listenerChildAge);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_close:
			startActivity(inte);
			break;
		case R.id.ll_check_in:
			showCheckinDialog();
			break;
		case R.id.ll_check_out:
			showCheckoutDialog();
			break;
		case R.id.ll_city_name:
			showCityDialog();
			break;
		case R.id.ll_nationality:
			showNationalityDialog();
			break;
		case R.id.btn_search:
			searchHotel();
			break;
		}
	}

	// dialog for check in date
	public void showCheckinDialog() {
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(currday.getTime(), nextYearCheckin)
				.inMode(SelectionMode.SINGLE).withSelectedDate(selcheckindate);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.checkin_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDateSelected(Date date) {
				selcheckindate = calendar.getSelectedDate();

				tvCheckinDate.setText(sdfPrint.format(date.getTime()));
				strCheckinDate = sdfUrl.format(date.getTime());

				Calendar cal = Calendar.getInstance();
				cal.setTime(calendar.getSelectedDate());
				cal.add(Calendar.DAY_OF_YEAR, 1);
				minCheckout = cal.getTime();

				tvCheckoutDate.setText(sdfPrint.format(minCheckout.getTime()));
				strCheckoutDate = sdfUrl.format(minCheckout.getTime());
				selCheckoutdate = minCheckout;
				dialogDate.dismiss();

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						showCheckoutDialog();
					}
				}, 250);

			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llCheckinDate.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llCheckinDate.setEnabled(true);
			}
		});

	}

	ArrayList<Date> dates = new ArrayList<Date>();

	public void showCheckoutDialog() {
		dates.clear();
		dates.add(selcheckindate);
		dates.add(selCheckoutdate);
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(selcheckindate, nextYearCheckout)
				.inMode(SelectionMode.MULTIPLE).withSelectedDates(dates);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.checkout_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub
				calendar.init(selcheckindate, nextYearCheckout)
						.inMode(SelectionMode.MULTIPLE)
						.withSelectedDates(dates);
				List<Date> dts = new ArrayList<Date>();
				dts = calendar.getSelectedDates();
				if (dts.get(1).compareTo(date) == 0) {
					selCheckoutdate = date;

					tvCheckoutDate.setText(sdfPrint.format(date.getTime()));
					strCheckoutDate = sdfUrl.format(date.getTime());

					dialogDate.dismiss();
				}
			}

			@Override
			public void onDateSelected(Date date) {
				selCheckoutdate = date;

				tvCheckoutDate.setText(sdfPrint.format(date.getTime()));
				strCheckoutDate = sdfUrl.format(date.getTime());

				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llCheckoutDate.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llCheckoutDate.setEnabled(true);
			}
		});

	}

	// for handling addition and removal of rooms
	public void showRooms() {
		if (roomCount == 1) {
			llRoom2.setVisibility(View.GONE);
			llRoom3.setVisibility(View.GONE);
			llRoom4.setVisibility(View.GONE);
			llRoom5.setVisibility(View.GONE);

			spAdult2Count.setSelection(1);
			spAdult3Count.setSelection(1);
			spAdult4Count.setSelection(1);
			spAdult5Count.setSelection(1);
			spChild2Count.setSelection(0);
			spChild3Count.setSelection(0);
			spChild4Count.setSelection(0);
			spChild5Count.setSelection(0);

			// spChild1Age_2.setVisibility(View.INVISIBLE);
			// spChild2Age_2.setVisibility(View.INVISIBLE);
			// spChild1Age_3.setVisibility(View.INVISIBLE);
			// spChild2Age_3.setVisibility(View.INVISIBLE);
			// spChild1Age_4.setVisibility(View.INVISIBLE);
			// spChild2Age_4.setVisibility(View.INVISIBLE);
			// spChild1Age_5.setVisibility(View.INVISIBLE);
			// spChild2Age_5.setVisibility(View.INVISIBLE);

		} else if (roomCount == 2) {
			llRoom2.setVisibility(View.VISIBLE);
			llRoom3.setVisibility(View.GONE);
			llRoom4.setVisibility(View.GONE);
			llRoom5.setVisibility(View.GONE);

			spAdult3Count.setSelection(1);
			spAdult4Count.setSelection(1);
			spAdult5Count.setSelection(1);
			spChild3Count.setSelection(0);
			spChild4Count.setSelection(0);
			spChild5Count.setSelection(0);

			// spChild1Age_3.setVisibility(View.INVISIBLE);
			// spChild2Age_3.setVisibility(View.INVISIBLE);
			// spChild1Age_4.setVisibility(View.INVISIBLE);
			// spChild2Age_4.setVisibility(View.INVISIBLE);
			// spChild1Age_5.setVisibility(View.INVISIBLE);
			// spChild2Age_5.setVisibility(View.INVISIBLE);
		} else if (roomCount == 3) {
			llRoom2.setVisibility(View.VISIBLE);
			llRoom3.setVisibility(View.VISIBLE);
			llRoom4.setVisibility(View.GONE);
			llRoom5.setVisibility(View.GONE);

			spAdult4Count.setSelection(1);
			spAdult5Count.setSelection(1);
			spChild4Count.setSelection(0);
			spChild5Count.setSelection(0);

			// spChild1Age_4.setVisibility(View.INVISIBLE);
			// spChild2Age_4.setVisibility(View.INVISIBLE);
			// spChild1Age_5.setVisibility(View.INVISIBLE);
			// spChild2Age_5.setVisibility(View.INVISIBLE);
		} else if (roomCount == 4) {
			llRoom2.setVisibility(View.VISIBLE);
			llRoom3.setVisibility(View.VISIBLE);
			llRoom4.setVisibility(View.VISIBLE);
			llRoom5.setVisibility(View.GONE);

			spAdult5Count.setSelection(1);
			spChild5Count.setSelection(0);

			// spChild1Age_5.setVisibility(View.INVISIBLE);
			// spChild2Age_5.setVisibility(View.INVISIBLE);
		} else if (roomCount == 5) {
			llRoom2.setVisibility(View.VISIBLE);
			llRoom3.setVisibility(View.VISIBLE);
			llRoom4.setVisibility(View.VISIBLE);
			llRoom5.setVisibility(View.VISIBLE);
		}
	}

	// public void showdialogRoom(Room tempRoom, final int roomNo)
	// {
	// dialogRoom = new Dialog(getActivity());
	// dialogRoom.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// dialogRoom.getWindow().setGravity(Gravity.CENTER);
	// dialogRoom.setContentView(R.layout.dialog_room_details);
	//
	// Button minusAdultCount, plusAdultCount;
	// Button minusChildCount, plusChildCount;
	// Button minusChild1Age, plusChild1Age;
	// Button minusChild2Age, plusChild2Age;
	// Button okBtn;
	// ImageView closeIv;
	//
	// adult_count = tempRoom.getAdultCount();
	// child_count = tempRoom.getChildCount();
	// child1age = tempRoom.getChild1Age();
	// child2age = tempRoom.getChild2Age();
	//
	// final TextView adultCount, childCount, child1Age, child2Age;
	// final LinearLayout child1Detail, child2Detail;
	//
	// minusAdultCount = (Button) dialogRoom.findViewById(R.id.btn_minus_adult);
	// plusAdultCount = (Button) dialogRoom.findViewById(R.id.btn_plus_adult);
	//
	// minusChildCount = (Button) dialogRoom.findViewById(R.id.btn_minus_child);
	// plusChildCount = (Button) dialogRoom.findViewById(R.id.btn_plus_child);
	//
	// minusChild1Age = (Button)
	// dialogRoom.findViewById(R.id.btn_minus_child1_age);
	// minusChild2Age = (Button)
	// dialogRoom.findViewById(R.id.btn_minus_child2_age);
	// plusChild1Age = (Button)
	// dialogRoom.findViewById(R.id.btn_plus_child1_age);
	// plusChild2Age = (Button)
	// dialogRoom.findViewById(R.id.btn_plus_child2_age);
	//
	// adultCount = (TextView) dialogRoom.findViewById(R.id.tv_adult_count);
	// childCount = (TextView) dialogRoom.findViewById(R.id.tv_child_count);
	// child1Age = (TextView) dialogRoom.findViewById(R.id.tv_child1_age);
	// child2Age = (TextView) dialogRoom.findViewById(R.id.tv_child2_age);
	//
	// child1Detail = (LinearLayout)
	// dialogRoom.findViewById(R.id.ll_child1_detail);
	// child2Detail = (LinearLayout)
	// dialogRoom.findViewById(R.id.ll_child2_detail);
	//
	// okBtn = (Button) dialogRoom.findViewById(R.id.btn_ok);
	// closeIv = (ImageView) dialogRoom.findViewById(R.id.iv_close_dialog);
	//
	// adultCount.setText(String.valueOf(adult_count));
	// childCount.setText(String.valueOf(child_count));
	// child1Age.setText(String.valueOf(child1age));
	// child2Age.setText(String.valueOf(child2age));
	//
	// if(child_count == 1)
	// {
	// child1Detail.setVisibility(View.VISIBLE);
	// }
	// if(child_count == 2)
	// {
	// child1Detail.setVisibility(View.VISIBLE);
	// child2Detail.setVisibility(View.VISIBLE);
	// }
	//
	// // ------------------- minus button clicks ------------------------
	// minusAdultCount.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(adult_count > 1)
	// {
	// adult_count--;
	// adultCount.setText(String.valueOf(adult_count));
	// }
	// }
	// });
	//
	// minusChildCount.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(child_count > 0)
	// {
	// child_count--;
	// childCount.setText(String.valueOf(child_count));
	// }
	// if(child_count == 1)
	// {
	// child2Detail.setVisibility(View.GONE);
	// child2age = 1;
	// child2Age.setText(String.valueOf(child2age));
	// }
	// if(child_count == 0)
	// {
	// child1Detail.setVisibility(View.GONE);
	// child1age = 1;
	// child1Age.setText(String.valueOf(child1age));
	// }
	// }
	// });
	//
	// minusChild1Age.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(child1age > 1)
	// {
	// child1age--;
	// child1Age.setText(String.valueOf(child1age));
	// }
	// }
	// });
	//
	// minusChild2Age.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(child2age > 1)
	// {
	// child2age--;
	// child2Age.setText(String.valueOf(child2age));
	// }
	// }
	// });
	//
	// //------------------ plus button clicks ----------------
	// plusAdultCount.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(adult_count < 5)
	// {
	// adult_count++;
	// adultCount.setText(String.valueOf(adult_count));
	// }
	// }
	// });
	//
	// plusChildCount.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(child_count < 2)
	// {
	// child_count++;
	// childCount.setText(String.valueOf(child_count));
	// }
	// if(child_count == 1)
	// child1Detail.setVisibility(View.VISIBLE);
	// if(child_count == 2)
	// child2Detail.setVisibility(View.VISIBLE);
	// }
	// });
	//
	// plusChild1Age.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(child1age < 11)
	// {
	// child1age++;
	// child1Age.setText(String.valueOf(child1age));
	// }
	// }
	// });
	//
	// plusChild2Age.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(child2age < 11)
	// {
	// child2age++;
	// child2Age.setText(String.valueOf(child2age));
	// }
	// }
	// });
	//
	// closeIv.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// dialogRoom.dismiss();
	// }
	// });
	//
	// okBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(roomNo == 1)
	// {
	// rooms[0].setAdultCount(adult_count);
	// rooms[0].setChildCount(child_count);
	// rooms[0].setChild1Age(child1age);
	// rooms[0].setChild2Age(child2age);
	// tvadult1_count.setText(String.valueOf(adult_count));
	// tvchild1_count.setText(String.valueOf(child_count));
	// if(rooms[0].getChildCount() == 1)
	// {
	// tvChild1Age_1.setVisibility(View.VISIBLE);
	// tvChild2Age_1.setVisibility(View.GONE);
	// tvChild1Age_1.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// }
	// else if(rooms[0].getChildCount() == 2)
	// {
	// tvChild1Age_1.setVisibility(View.VISIBLE);
	// tvChild2Age_1.setVisibility(View.VISIBLE);
	// tvChild1Age_1.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// tvChild2Age_1.setText(getResources().getString(R.string.child2_age)+
	// " "+ child2age);
	// }
	// else
	// {
	// tvChild1Age_1.setVisibility(View.GONE);
	// tvChild2Age_1.setVisibility(View.GONE);
	// }
	//
	// }
	// else if(roomNo == 2)
	// {
	// rooms[1].setAdultCount(adult_count);
	// rooms[1].setChildCount(child_count);
	// rooms[1].setChild1Age(child1age);
	// rooms[1].setChild2Age(child2age);
	// tvadult2_count.setText(String.valueOf(adult_count));
	// tvchild2_count.setText(String.valueOf(child_count));
	// if(rooms[1].getChildCount() == 1)
	// {
	// tvChild1Age_2.setVisibility(View.VISIBLE);
	// tvChild2Age_2.setVisibility(View.GONE);
	// tvChild1Age_2.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// }
	// else if(rooms[1].getChildCount() == 2)
	// {
	// tvChild1Age_2.setVisibility(View.VISIBLE);
	// tvChild2Age_2.setVisibility(View.VISIBLE);
	// tvChild1Age_2.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// tvChild2Age_2.setText(getResources().getString(R.string.child2_age)+
	// " "+ child2age);
	// }
	// else
	// {
	// tvChild1Age_2.setVisibility(View.GONE);
	// tvChild2Age_2.setVisibility(View.GONE);
	// }
	//
	// }
	// else if(roomNo == 3)
	// {
	// rooms[2].setAdultCount(adult_count);
	// rooms[2].setChildCount(child_count);
	// rooms[2].setChild1Age(child1age);
	// rooms[2].setChild2Age(child2age);
	// tvadult3_count.setText(String.valueOf(adult_count));
	// tvchild3_count.setText(String.valueOf(child_count));
	// if(rooms[2].getChildCount() == 1)
	// {
	// tvChild1Age_3.setVisibility(View.VISIBLE);
	// tvChild2Age_3.setVisibility(View.GONE);
	// tvChild1Age_3.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// }
	// else if(rooms[2].getChildCount() == 2)
	// {
	// tvChild1Age_3.setVisibility(View.VISIBLE);
	// tvChild2Age_3.setVisibility(View.VISIBLE);
	// tvChild1Age_3.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// tvChild2Age_3.setText(getResources().getString(R.string.child2_age)+
	// " "+ child2age);
	// }
	// else
	// {
	// tvChild1Age_3.setVisibility(View.GONE);
	// tvChild2Age_3.setVisibility(View.GONE);
	// }
	// }
	// else if(roomNo == 4)
	// {
	// rooms[3].setAdultCount(adult_count);
	// rooms[3].setChildCount(child_count);
	// rooms[3].setChild1Age(child1age);
	// rooms[3].setChild2Age(child2age);
	// tvadult4_count.setText(String.valueOf(adult_count));
	// tvchild4_count.setText(String.valueOf(child_count));
	// if(rooms[3].getChildCount() == 1)
	// {
	// tvChild1Age_4.setVisibility(View.VISIBLE);
	// tvChild2Age_4.setVisibility(View.GONE);
	// tvChild1Age_4.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// }
	// else if(rooms[3].getChildCount() == 2)
	// {
	// tvChild1Age_4.setVisibility(View.VISIBLE);
	// tvChild2Age_4.setVisibility(View.VISIBLE);
	// tvChild1Age_4.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// tvChild2Age_4.setText(getResources().getString(R.string.child2_age)+
	// " "+ child2age);
	// }
	// else
	// {
	// tvChild1Age_4.setVisibility(View.GONE);
	// tvChild2Age_4.setVisibility(View.GONE);
	// }
	// }
	// else
	// {
	// rooms[4].setAdultCount(adult_count);
	// rooms[4].setChildCount(child_count);
	// rooms[4].setChild1Age(child1age);
	// rooms[4].setChild2Age(child2age);
	// tvadult5_count.setText(String.valueOf(adult_count));
	// tvchild5_count.setText(String.valueOf(child_count));
	// if(rooms[4].getChildCount() == 1)
	// {
	// tvChild1Age_5.setVisibility(View.VISIBLE);
	// tvChild2Age_5.setVisibility(View.GONE);
	// tvChild1Age_5.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// }
	// else if(rooms[4].getChildCount() == 2)
	// {
	// tvChild1Age_5.setVisibility(View.VISIBLE);
	// tvChild2Age_5.setVisibility(View.VISIBLE);
	// tvChild1Age_5.setText(getResources().getString(R.string.child1_age)+
	// " "+ child1age);
	// tvChild2Age_5.setText(getResources().getString(R.string.child2_age)+
	// " "+ child2age);
	// }
	// else
	// {
	// tvChild1Age_5.setVisibility(View.GONE);
	// tvChild2Age_5.setVisibility(View.GONE);
	// }
	// }
	// dialogRoom.dismiss();
	// }
	// });
	// dialogRoom.show();
	// }

	public void showCityDialog() {
		tvHeader.setText(R.string.cityorhotel);
		tvNoResult.setVisibility(View.GONE);
		actSuggestion.setText(null);
		actSuggestion.setHint(R.string.search_city_hotel);
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				
				String resource[], temp = null;
				temp = actSuggestion.getText().toString();
				if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
					resource = temp.split(" \n ");
					temp = resource[0];
					strCity = resource[1];
					resource = strCity.split(" - ");
					strCity = resource[0];
					resource = temp.split(" - ");
					temp = resource[0];
					tvCity.setText(temp);
				} else {
					resource = temp.split(" - ");
					temp = resource[0];
					strCity = temp;
					tvCity.setText(strCity);
				}
				imgbtnClearCity.setVisibility(View.VISIBLE);

				// SharedPreferences sharedpreferences =
				// getActivity().getSharedPreferences("default_values",
				// Context.MODE_PRIVATE);
				// Editor editor = sharedpreferences.edit();
				// editor.putString("strCity", strCity);
				// editor.commit();

				dialogSuggestion.dismiss();
			}
		};
		actSuggestion.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = actSuggestion.getText().toString()
						.toLowerCase();
				ArrayList<String> listItems = new ArrayList<String>();
				for (String listItem : arrayCity) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}
				}
				if (listItems.size() == 0) {
					if (tvNoResult.getVisibility() == View.GONE)
						tvNoResult.setVisibility(View.VISIBLE);
				} else {
					if (tvNoResult.getVisibility() == View.VISIBLE)
						tvNoResult.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), R.layout.tv_autocomplete,
						listItems);
				actSuggestion.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogSuggestion.dismiss();
			}
		});

		actSuggestion.setOnItemClickListener(onitem);
		dialogSuggestion.show();
	}

	public void showNationalityDialog() {
		tvHeader.setText(R.string.nationality);
		tvNoResult.setVisibility(View.GONE);
		actSuggestion.setText(null);
		actSuggestion.setHint(R.string.search_nation);
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				strNationality = actSuggestion.getText().toString();
				String resource[] = strNationality.split("-");
				strNationality = resource[1].replace(" ", "");
				tvNationality.setText(resource[0].trim());
				imgbtnClearNationality.setVisibility(View.VISIBLE);

				// SharedPreferences sharedpreferences =
				// getActivity().getSharedPreferences("default_values",
				// Context.MODE_PRIVATE);
				// Editor editor = sharedpreferences.edit();
				// editor.putString("strNationality", strNationality);
				// editor.commit();

				dialogSuggestion.dismiss();
			}
		};
		actSuggestion.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = s.toString().toLowerCase();
				ArrayList<String> listItems = new ArrayList<String>();
				for (String listItem : arrayCountry) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}

				}
				if (listItems.size() == 0) {
					if (tvNoResult.getVisibility() == View.GONE)
						tvNoResult.setVisibility(View.VISIBLE);
				} else {
					if (tvNoResult.getVisibility() == View.VISIBLE)
						tvNoResult.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						listItems);
				actSuggestion.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogSuggestion.dismiss();
			}
		});

		actSuggestion.setOnItemClickListener(onitem);
		dialogSuggestion.show();
	}

	public void searchHotel() {
		if (cf.isConnectingToInternet()) {
			if (!strCity.equals("")) {
				if (!strNationality.equals("")) {
					int passengers = 0;
					strCity = strCity.replace(" / ", "%20");
					strCity = strCity.replace("/", "%20");
					strCity = strCity.replace(" ", "%20");
					String strPassenger = "", url = "";
					System.out.println("Room count" + roomCount);
					for (int i = 0; i < roomCount; ++i) {
						if (i != 0)
							strPassenger = strPassenger + ",";

						if (rooms[i].getChildCount() == 0) {
							passengers = passengers + rooms[i].getAdultCount();
							strPassenger = strPassenger
									+ rooms[i].getAdultCount() + "-"
									+ rooms[i].getChildCount();
						} else if (rooms[i].getChildCount() == 1) {
							passengers = passengers + rooms[i].getAdultCount()
									+ rooms[i].getChildCount();
							strPassenger = strPassenger
									+ rooms[i].getAdultCount() + "-"
									+ rooms[i].getChildCount() + "-"
									+ rooms[i].getChild1Age() + "-0-0";
						} else if (rooms[i].getChildCount() == 2){
							passengers = passengers + rooms[i].getAdultCount()
									+ rooms[i].getChildCount();
							strPassenger = strPassenger
									+ rooms[i].getAdultCount() + "-"
									+ rooms[i].getChildCount() + "-"
									+ rooms[i].getChild1Age() + "-"
									+ rooms[i].getChild2Age() + "-0";
						} else {
							passengers = passengers + rooms[i].getAdultCount()
									+ rooms[i].getChildCount();
							strPassenger = strPassenger
									+ rooms[i].getAdultCount() + "-"
									+ rooms[i].getChildCount() + "-"
									+ rooms[i].getChild1Age() + "-"
									+ rooms[i].getChild2Age() + "-"
									+ rooms[i].getChild3Age();
						}
					}
					url = strCity + "/" + strCheckinDate + "/"
							+ strCheckoutDate + "/" + strNationality + "/"
							+ strPassenger + "/" + CommonFunctions.strCurrency;

					System.out.println("URL " + url);
					System.out.println("city" + tvCity.getText().toString());
					System.out.println("checkinDate " + strCheckinDate);
					System.out.println("checkoutDate " + strCheckoutDate);
					System.out.println("passengers " + passengers);
					System.out.println("roomCount " + roomCount);

					inte = new Intent(getActivity(), HotelResultActivity.class);
					inte.putExtra("url", url);
					inte.putExtra("city", tvCity.getText().toString());
					inte.putExtra("checkinDate", tvCheckinDate.getText()
							.toString());
					inte.putExtra("checkoutDate", tvCheckoutDate.getText()
							.toString());
					inte.putExtra("passengers", passengers);
					inte.putExtra("roomCount", roomCount);
					
					if(CommonFunctions.modify)
					{
						CommonFunctions.modify = false;
						CommonFunctions.HotelResult.clear();
					}
					
					startActivity(inte);
				} else
					showAlert(getResources().getString(
							R.string.err_msg_select_nation));
			} else
				showAlert(getResources()
						.getString(R.string.err_msg_select_city));
		} else
			noInternetAlert();
	}

	public void showAlert(String errorMsg) {
		String titleMsg = getResources().getString(R.string.error_title);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		// Setting Dialog Title
		alertDialog.setTitle(titleMsg);

		// Setting Dialog Message
		alertDialog.setMessage(errorMsg);

		// Setting OK Button
		alertDialog.setPositiveButton(getResources().getString(R.string.ok),
				null);

		alertDialog.show();
	}

	public void noInternetAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		// Setting Dialog Title
		alertDialog.setTitle(getResources().getString(
				R.string.error_no_internet_title));

		// Setting Dialog Message
		alertDialog.setMessage(getResources().getString(
				R.string.error_no_internet_msg));

		// Setting Icon to Dialog

		// Setting OK Button
		alertDialog.setPositiveButton(
				getResources().getString(R.string.error_no_internet_settings),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// intent to move mobile settings
						getActivity().startActivity(
								new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		alertDialog.setNegativeButton(
				getResources().getString(R.string.error_no_internet_cancel),
				null);

		// Showing Alert Message
		alertDialog.show();
	}

	private void loadAssets() {
		// TODO Auto-generated method stub

		String citylist = null, countrylist = null;
		InputStream file = null, file1 = null;
		try {
			if(CommonFunctions.lang.equalsIgnoreCase("en"))
				file = am.open("citylist.txt");
			else
				file = am.open("citylist_ar.txt");
			file1 = am.open("countrylist.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.e("Testing", "Starting to read");
		BufferedReader reader = null, reader1 = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file));
			reader1 = new BufferedReader(new InputStreamReader(file1));

			StringBuilder builder = new StringBuilder();
			StringBuilder builder1 = new StringBuilder();

			String line = null, line1 = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			while ((line1 = reader1.readLine()) != null) {
				builder1.append(line1);
			}

			citylist = builder.toString();
			countrylist = builder1.toString();

			arrayCity = new ArrayList<String>();
			arrayCountry = new ArrayList<String>();

			if (citylist != null) {
				JSONArray jsonArray = new JSONArray(citylist);

				for (int i = 0; i < jsonArray.length(); i++) {
					arrayCity.add(jsonArray.getString(i));
				}
			}
			if (countrylist != null) {
				JSONObject json1 = new JSONObject(countrylist);
				JSONArray airlinelist = json1.getJSONArray("countrylist");
				JSONObject c1 = null;
				for (int i = 0; i < airlinelist.length(); i++) {
					c1 = airlinelist.getJSONObject(i);
					arrayCountry.add(c1.getString("CountryName") + " - "
							+ c1.getString("CountryCode"));
				}
				airlinelist = null;
			}

			citylist = null;
			countrylist = null;
			file.close();
			file1.close();
			reader.close();
			reader1.close();
			builder = null;
			builder1 = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class backMethod extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			loadAssets();
			return null;
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		rooms[0] = null;
		rooms[1] = null;
		rooms[2] = null;
		rooms[3] = null;
		rooms[4] = null;
		arrayCity = null;
		arrayCountry = null;
		super.onDestroyView();
	}

}
