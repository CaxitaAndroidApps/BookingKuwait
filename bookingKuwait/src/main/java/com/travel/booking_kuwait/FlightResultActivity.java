package com.travel.booking_kuwait;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.travel.booking_kuwait.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.travel.booking_kuwait.adapter.FlightResultAdapter;
import com.travel.booking_kuwait.model.FlightResultItem;
import com.travel.booking_kuwait.R;

public class FlightResultActivity extends Activity implements
		OnItemClickListener {

	private Locale myLocale;
	String strFlightType, strFromCode, strTocode, strFromCity, strToCity;
	String strFromDate, strToDate;
	int adultCount, childCount, infantCount, tripNo;
	boolean isRoundTrip = false;
	public static String flightClass = null;
	public static boolean blChild;

	TextView tvFlightHeader, tvFlightDates, tvProgressText, tvCurrency;
	static ListView lvFlightResult;
	ScrollView svResult;
	static LinearLayout llSort;
	// Spinner spSort;
	private static ArrayList<FlightResultItem> flightResultItem,
			flightResultItemsTemp, filteredResult;
	static ArrayList<String> arrayAirline, checkedAirlines;
	static ArrayList<String> arrayAirports, checkedAirports;
	String str_url = "";
	String main_url = "";

	static String flightName, flightLogo, departureDateTime,
			departureTimeString;
	static String arrivalDateTime, arrivalTimeString, totalDurationInMinutes;
	static int stops;
	Boolean blHasNonStop = false, blHasOneStop = false, blHasMultStop = false;
	Boolean blNonStop = false, blOneStop = false, blMultiStop = false;
	Boolean blSortPrice = true, blSortDep = false, blSortArrival = false,
			blSortDuration = false, blSortAirName = false;
	String strSortPriceType = "Low", strSortDepType = null,
			strSortArrivalType = null, strSortDurationType = null,
			strSortAirNameType = null;

	public static Activity activityResult;

	// filter and sort

	ImageView ivClose;
	LinearLayout llRangeBar, llDepartBar, llLandingBar, llLayover;
	TextView tvRangeMax, tvRangeMin, tvLayoverMin, tvLayoverMax;
	TextView tvDepartMin, tvDepartMax, tvLandingMin, tvLandingMax;
	CheckBox cbNonStop, cbOneStop, cbMultiStop;
	LinearLayout llOneStop, llNonStop, llMultistop, llArlineList,
			llAirportList;
	LinearLayout llTabOutbound, llTabReturn;
	LinearLayout ll12A6AFrom, ll6A12PFrom, ll12P6PFrom, ll6P12AFrom, ll12A6ATo,
			ll6A12PTo, ll12P6PTo, ll6P12Ato;
	TextView tv12A6AFrom, tv6A12PFrom, tv12P6PFrom, tv6P12AFrom, tv12A6ATo,
			tv6A12PTo, tv12P6PTo, tv6P12Ato;
	CheckBox cbCheckAll, cbTabOutbound, cbTabReturn;
	CheckBox cb12A6AFrom, cb6A12PFrom, cb12P6PFrom, cb6P12AFrom, cb12A6ATo,
			cb6A12PTo, cb12P6PTo, cb6P12Ato;
	Button btnApply;

	static String str12a6aFromOut, str6a12pFromOut, str12p6pFromOut,
			str6p12aFromOut, str12a6aToOut, str6a12pToOut, str12p6pToOut,
			str6p12aToOut, str12a6aFromRet, str6a12pFromRet, str12p6pFromRet,
			str6p12aFromRet, str12a6aToRet, str6a12pToRet, str12p6pToRet,
			str6p12aToRet;

	static Double filterMinPrice = 0.0, filterMaxPrice = 0.0;
	static Long filterDepLow, filterDepHigh, filterArrLow, filterArrHigh;
//			filterLayLow, filterLayHigh;
	Long filterMinDep, filterMaxDep, filterMinArr, filterMaxArr; //, filterMinLay,
//			filterMaxLay;
	Boolean blPriceFilter = false, blDepTimeFilter = false,
			blArrTimeFilter = false, blStopFilter = false,
			blNameFilter = false, blLayoverFilter = false,
			blLayAirportFilter = false;
	Boolean blOutbound = true, blReturn = false, bl12a6aFrom = false,
			bl6a12pFrom = false, bl12p6pFrom = false, bl6p12aFrom = false,
			bl12a6aTo = false, bl6a12pTo = false, bl12p6pTo = false,
			bl6p12aTo = false, blCurr = false;
	Dialog dialogSort, loaderDialog, curr;
	TextView tvSortBy, tvSortByType;
	CommonFunctions cf;

	// String strSessionId = null;
	String[] sortText, sortHeading;

	public static boolean blSession = true;

	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityResult = this;

		getActionBar().hide();
		cf = new CommonFunctions(this);
		loadLocale();

		setContentView(R.layout.activity_search_result_flight);

		initialize();
		getIntentValues();
		setViewValues();

		if (CommonFunctions.modify) {
			// strSessionId = CommonFunctions.strSearchId;
			flightResultItem.addAll(CommonFunctions.flighResult);
			lvFlightResult.setAdapter(new FlightResultAdapter(
					FlightResultActivity.this, flightResultItem, isRoundTrip));
			setDefaultValues();
			CommonFunctions.modify = false;
			CommonFunctions.strSearchId = null;
			CommonFunctions.flighResult.clear();
			for (FlightResultItem fItem : flightResultItem) {
				if (!arrayAirline.contains(fItem.str_AirlineName)
						&& !fItem.str_AirlineName.equalsIgnoreCase(""))
					arrayAirline.add(fItem.str_AirlineName);
			}
		} else {

			new backMethod().execute("");
		}
		activity = this;
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if(!blSession)
			new backMethod().execute();
		super.onRestart();
	}
	
	private void initialize() {
		// TODO Auto-generated method stub
		tvFlightHeader = (TextView) findViewById(R.id.tv_Flight_Hd);
		tvFlightDates = (TextView) findViewById(R.id.tv_date);
		lvFlightResult = (ListView) findViewById(R.id.lv_flight_result);
		// svResult = (ScrollView) findViewById(R.id.sv_flight_results);
		tvCurrency = (TextView) findViewById(R.id.tv_currency);
		tvCurrency.setText(CommonFunctions.strCurrency);

		loaderDialog = new Dialog(FlightResultActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		tvProgressText = (TextView) loaderDialog
				.findViewById(R.id.tv_progress_text);
		tvProgressText.setText(getResources().getString(
				R.string.searching_flight));
		tvProgressText.setVisibility(View.VISIBLE);

		dialogSort = new Dialog(FlightResultActivity.this,
				android.R.style.Theme_Translucent);
		dialogSort.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSort.setContentView(R.layout.dialog_sort);

		llSort = (LinearLayout) dialogSort.findViewById(R.id.ll_sort_options);
		tvSortBy = (TextView) findViewById(R.id.tv_sort_by);
		tvSortByType = (TextView) findViewById(R.id.tv_sort_by_type);

		flightResultItem = new ArrayList<FlightResultItem>();
		flightResultItemsTemp = new ArrayList<FlightResultItem>();
		arrayAirline = new ArrayList<String>();
		checkedAirlines = new ArrayList<String>();
		arrayAirports = new ArrayList<String>();
		checkedAirports = new ArrayList<String>();

		main_url = CommonFunctions.main_url + CommonFunctions.lang
				+ "/FlightApi/FlightSearchApi?";

		sortText = getResources().getStringArray(R.array.sort_items_flight);
		sortHeading = getResources().getStringArray(
				R.array.sort_item_flight_heading);
		tvSortByType.setText(sortHeading[0]);
		tvSortBy.setText(sortText[0]);

		// filter

		ivClose = (ImageView) findViewById(R.id.iv_close);

		llTabOutbound = (LinearLayout) findViewById(R.id.ll_tab_outbound);
		llTabReturn = (LinearLayout) findViewById(R.id.ll_tab_return);

		ll12A6AFrom = (LinearLayout) findViewById(R.id.ll_12a6a_from);
		ll6A12PFrom = (LinearLayout) findViewById(R.id.ll_6a12p_from);
		ll12P6PFrom = (LinearLayout) findViewById(R.id.ll_12p6p_from);
		ll6P12AFrom = (LinearLayout) findViewById(R.id.ll_6p12a_from);
		ll12A6ATo = (LinearLayout) findViewById(R.id.ll_12a6a_to);
		ll6A12PTo = (LinearLayout) findViewById(R.id.ll_6a12p_to);
		ll12P6PTo = (LinearLayout) findViewById(R.id.ll_12p6p_to);
		ll6P12Ato = (LinearLayout) findViewById(R.id.ll_6p12a_to);

		tv12A6AFrom = (TextView) findViewById(R.id.tv_price_12a6a_from);
		tv6A12PFrom = (TextView) findViewById(R.id.tv_price_6a12p_from);
		tv12P6PFrom = (TextView) findViewById(R.id.tv_price_12p6p_from);
		tv6P12AFrom = (TextView) findViewById(R.id.tv_price_6p12a_from);
		tv12A6ATo = (TextView) findViewById(R.id.tv_price_12a6a_to);
		tv6A12PTo = (TextView) findViewById(R.id.tv_price_6a12p_to);
		tv12P6PTo = (TextView) findViewById(R.id.tv_price_12p6p_to);
		tv6P12Ato = (TextView) findViewById(R.id.tv_price_6p12a_to);

		cbTabOutbound = (CheckBox) findViewById(R.id.cb_outbound);
		cbTabReturn = (CheckBox) findViewById(R.id.cb_return);

		cb12A6AFrom = (CheckBox) findViewById(R.id.cb_12a6a_from);
		cb6A12PFrom = (CheckBox) findViewById(R.id.cb_6a12p_from);
		cb12P6PFrom = (CheckBox) findViewById(R.id.cb_12p6p_from);
		cb6P12AFrom = (CheckBox) findViewById(R.id.cb_6p12a_from);
		cb12A6ATo = (CheckBox) findViewById(R.id.cb_12a6a_to);
		cb6A12PTo = (CheckBox) findViewById(R.id.cb_6a12p_to);
		cb12P6PTo = (CheckBox) findViewById(R.id.cb_12p6p_to);
		cb6P12Ato = (CheckBox) findViewById(R.id.cb_6p12a_to);

		llRangeBar = (LinearLayout) findViewById(R.id.ll_range_bar);
		tvRangeMax = (TextView) findViewById(R.id.tv_range_max);
		tvRangeMin = (TextView) findViewById(R.id.tv_range_min);

		llDepartBar = (LinearLayout) findViewById(R.id.ll_depart_bar);
		llLandingBar = (LinearLayout) findViewById(R.id.ll_landing_bar);
		tvDepartMin = (TextView) findViewById(R.id.tv_depart_min);
		tvDepartMax = (TextView) findViewById(R.id.tv_depart_max);
		tvLandingMin = (TextView) findViewById(R.id.tv_landing_min);
		tvLandingMax = (TextView) findViewById(R.id.tv_landing_max);

		llLayover = (LinearLayout) findViewById(R.id.ll_layover_bar);
		tvLayoverMin = (TextView) findViewById(R.id.tv_layover_min);
		tvLayoverMax = (TextView) findViewById(R.id.tv_layover_max);

		cbNonStop = (CheckBox) findViewById(R.id.cb_check_non_stop);
		cbOneStop = (CheckBox) findViewById(R.id.cb_check_one_stop);
		cbMultiStop = (CheckBox) findViewById(R.id.cb_check_multi_stop);

		llOneStop = (LinearLayout) findViewById(R.id.ll_one_stop);
		llNonStop = (LinearLayout) findViewById(R.id.ll_non_stop);
		llMultistop = (LinearLayout) findViewById(R.id.ll_multi_stop);

		cbCheckAll = (CheckBox) findViewById(R.id.cb_check_all);
		llArlineList = (LinearLayout) findViewById(R.id.ll_airline_list);
		llAirportList = (LinearLayout) findViewById(R.id.ll_layover_airport_list);

		btnApply = (Button) findViewById(R.id.btn_apply_filter);

	}

	private void getIntentValues() {
		// TODO Auto-generated method stub
		str_url = getIntent().getExtras().getString("url", "");

		String temp = str_url.substring(
				str_url.lastIndexOf("passengerdetail") + 20,
				str_url.lastIndexOf("passengerdetail") + 27);
		blChild = Integer.parseInt(temp.substring(2, 3)) > 0 ? true : false;

		str_url = str_url + "&searchID=";
		strFlightType = getIntent().getExtras().getString("flight_type", "");
		flightClass = getIntent().getExtras().getString("class", "");
		tripNo = getIntent().getExtras().getInt("trip_nos");
		if (!strFlightType.equalsIgnoreCase("Multicity")) {
			strFromCode = getIntent().getExtras().getString("from_code", "");
			strTocode = getIntent().getExtras().getString("to_code", "");
			strFromCity = getIntent().getExtras().getString("from_city", "");
			strToCity = getIntent().getExtras().getString("to_city", "");
			strFromDate = getIntent().getExtras().getString("departure_date",
					"");
			strToDate = "";
			if (strFlightType.equalsIgnoreCase("RoundTrip")) {
				strToDate = getIntent().getExtras().getString("arrival_date",
						"");
				isRoundTrip = true;
			}

			adultCount = getIntent().getExtras().getInt("adult_count");
			childCount = getIntent().getExtras().getInt("child_count");
			infantCount = getIntent().getExtras().getInt("infant_count");

		} else {
			String tempPlace, tempDates, temp1, temp2, temp3, temp4, temp5, temp6;
			if (tripNo == 2) {
				temp1 = getIntent().getExtras().getString("to1", "");
				temp2 = getIntent().getExtras().getString("from2", "");
				if (!temp1.equalsIgnoreCase(temp2))
					temp1 = temp1 + ", " + temp2;
				tempPlace = getIntent().getExtras().getString("from1", "")
						+ " -> " + temp1 + " -> "
						+ getIntent().getExtras().getString("to2", "");

				tempDates = getIntent().getExtras().getString("date1", "")
						+ " -> "
						+ getIntent().getExtras().getString("date2", "");
				if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
					tempDates = getIntent().getExtras().getString("date2", "")
							+ " <- "
							+ getIntent().getExtras().getString("date1", "");
				}
			} else if (tripNo == 3) {
				temp1 = getIntent().getExtras().getString("to1", "");
				temp2 = getIntent().getExtras().getString("from2", "");
				temp3 = getIntent().getExtras().getString("to2", "");
				temp4 = getIntent().getExtras().getString("from3", "");
				if (!temp1.equalsIgnoreCase(temp2))
					temp1 = temp1 + ", " + temp2;
				if (!temp3.equalsIgnoreCase(temp4))
					temp3 = temp3 + ", " + temp4;
				tempPlace = getIntent().getExtras().getString("from1", "")
						+ " -> " + temp1 + " -> " + temp3 + " -> "
						+ getIntent().getExtras().getString("to3", "");

				tempDates = getIntent().getExtras().getString("date1", "")
						+ " -> "
						+ getIntent().getExtras().getString("date2", "")
						+ " -> "
						+ getIntent().getExtras().getString("date3", "");

				if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
					tempDates = getIntent().getExtras().getString("date3", "")
							+ " <- "
							+ getIntent().getExtras().getString("date2", "")
							+ " <- "
							+ getIntent().getExtras().getString("date1", "");
				}

			} else {
				temp1 = getIntent().getExtras().getString("to1", "");
				temp2 = getIntent().getExtras().getString("from2", "");
				temp3 = getIntent().getExtras().getString("to2", "");
				temp4 = getIntent().getExtras().getString("from3", "");
				temp5 = getIntent().getExtras().getString("to3", "");
				temp6 = getIntent().getExtras().getString("from4", "");
				if (!temp1.equalsIgnoreCase(temp2))
					temp1 = temp1 + ", " + temp2;
				if (!temp3.equalsIgnoreCase(temp4))
					temp3 = temp3 + ", " + temp4;
				if (!temp5.equalsIgnoreCase(temp6))
					temp5 = temp5 + ", " + temp6;
				tempPlace = getIntent().getExtras().getString("from1", "")
						+ " -> " + temp1 + " -> " + temp3 + " -> " + temp5
						+ " -> " + getIntent().getExtras().getString("to4", "");

				tempDates = getIntent().getExtras().getString("date1", "")
						+ " -> "
						+ getIntent().getExtras().getString("date2", "")
						+ " -> "
						+ getIntent().getExtras().getString("date3", "")
						+ " -> "
						+ getIntent().getExtras().getString("date4", "");

				if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
					tempDates = getIntent().getExtras().getString("date4", "")
							+ " <- "
							+ getIntent().getExtras().getString("date3", "")
							+ " <- "
							+ getIntent().getExtras().getString("date2", "")
							+ " <- "
							+ getIntent().getExtras().getString("date1", "");
				}
			}

			tvFlightHeader.setText(tempPlace);
			tvFlightDates.setText(tempDates);

		}
	}

	private void setViewValues() {
		if (!strFlightType.equalsIgnoreCase("Multicity")) {
			if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
				if (strFlightType.equalsIgnoreCase("OneWay"))
					tvFlightDates.setText(strFromDate);
				else if (strFlightType.equalsIgnoreCase("RoundTrip"))
					tvFlightDates.setText(strToDate + " <- " + strFromDate);
			} else {
				if (strFlightType.equalsIgnoreCase("OneWay"))
					tvFlightDates.setText(strFromDate);
				else if (strFlightType.equalsIgnoreCase("RoundTrip"))
					tvFlightDates.setText(strFromDate + " -> " + strToDate);

			}
			tvFlightHeader.setText(strFromCity + " -> " + strToCity);
		}
		tvFlightHeader.setSelected(true);
	}

	private class backMethod extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			flightResultItem.clear();
			flightResultItemsTemp.clear();
			arrayAirline.clear();
			checkedAirlines.clear();
			loaderDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			if (params[0].isEmpty()) {
				String resultString = makePostRequest(false, "");
				if (resultString != null)
					parseRoundtripResult(resultString);
			} else if (!params[0].equalsIgnoreCase(CommonFunctions.strCurrency)) {
				String resultString = makePostRequest(true, params[0]);
				if (resultString != null) {
					JSONObject jObj;
					try {
						CommonFunctions.strCurrency = params[0];
						jObj = new JSONObject(resultString);
						jObj = jObj.getJSONObject("data");
						parseRoundtripResult(jObj.getString("Item"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
			if (flightResultItem.size() > 0) {
				blCurr = false;
				tvCurrency.setText(CommonFunctions.strCurrency);
				lvFlightResult.setAdapter(new FlightResultAdapter(
						FlightResultActivity.this, flightResultItem,
						isRoundTrip));
				setDefaultValues();
				System.out
						.println("------------------Finished displaying-------------");
			} else {
				((LinearLayout) findViewById(R.id.ll_filter)).setEnabled(false);
				((LinearLayout) findViewById(R.id.ll_sort)).setEnabled(false);
			}
		}
	}

	public void setDefaultValues() {

		flightResultItemsTemp.addAll(flightResultItem);
		// setting selected filter vales
		filterMinPrice = (flightResultItem).get(0).doubleFlightPrice;
		filterMaxPrice = (flightResultItem).get(flightResultItem.size() - 1).doubleFlightPrice;

		ArrayList<Long> al = new ArrayList<Long>();
		// ArrayList<Long> a2 = new ArrayList<Long>();
//		ArrayList<Long> a3 = new ArrayList<Long>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
				new Locale("en"));
		Calendar cal = Calendar.getInstance();

		try {
			cal.setTime(dateFormat.parse("12:00 AM"));
			filterDepLow = cal.getTimeInMillis();
			filterArrLow = cal.getTimeInMillis();

			cal.setTime(dateFormat.parse("11:59 PM"));
			filterDepHigh = cal.getTimeInMillis();
			filterArrHigh = cal.getTimeInMillis();

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(
				CommonFunctions.lang));

		int minutes = -1;
		for (FlightResultItem fItem : flightResultItem) {
			// al.add(fItem.DepartDateTimeOne);
			// a2.add(fItem.ArrivalDateTimeOne);
//			a3.add(fItem.longLayoverTimeInMins);

			try {
				cal.setTime(dateFormat.parse(fItem.DepartTimeOne));
				minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
						+ cal.get(Calendar.MINUTE);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!bl12a6aFrom && minutes > 0 && minutes < 360) {
				str12a6aFromOut = fItem.strDisplayRate;
				bl12a6aFrom = true;
			}
			if (!bl6a12pFrom && minutes > 360 && minutes < 720) {
				str6a12pFromOut = fItem.strDisplayRate;
				bl6a12pFrom = true;
			}
			if (!bl12p6pFrom && minutes > 720 && minutes < 1080) {
				str12p6pFromOut = fItem.strDisplayRate;
				bl12p6pFrom = true;
			}
			if (!bl6p12aFrom && minutes > 1080 && minutes < 1440) {
				str6p12aFromOut = fItem.strDisplayRate;
				bl6p12aFrom = true;
			}

			try {
				cal.setTime(dateFormat.parse(fItem.ArrivalTimeOne));
				minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
						+ cal.get(Calendar.MINUTE);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!bl12a6aTo && minutes > 0 && minutes < 360) {
				str12a6aToOut = fItem.strDisplayRate;
				bl12a6aTo = true;
			}
			if (!bl6a12pTo && minutes > 360 && minutes < 720) {
				str6a12pToOut = fItem.strDisplayRate;
				bl6a12pTo = true;
			}
			if (!bl12p6pTo && minutes > 720 && minutes < 1080) {
				str12p6pToOut = fItem.strDisplayRate;
				bl12p6pTo = true;
			}
			if (!bl6p12aTo && minutes > 1080 && minutes < 1440) {
				str6p12aToOut = fItem.strDisplayRate;
				bl6p12aTo = true;
			}
		}

		bl12a6aFrom = false;
		bl6a12pFrom = false;
		bl12p6pFrom = false;
		bl6p12aFrom = false;
		bl12a6aTo = false;
		bl6a12pTo = false;
		bl12p6pTo = false;
		bl6p12aTo = false;

		tv12A6AFrom
				.setText(str12a6aFromOut != null ? CommonFunctions.strCurrency
						+ " " + str12a6aFromOut : null);

		tv6A12PFrom
				.setText(str6a12pFromOut != null ? CommonFunctions.strCurrency
						+ " " + str6a12pFromOut : null);

		tv12P6PFrom
				.setText(str12p6pFromOut != null ? CommonFunctions.strCurrency
						+ " " + str12p6pFromOut : null);

		tv6P12AFrom
				.setText(str6p12aFromOut != null ? CommonFunctions.strCurrency
						+ " " + str6p12aFromOut : null);

		tv12A6ATo.setText(str12a6aToOut != null ? CommonFunctions.strCurrency
				+ " " + str12a6aToOut : null);

		tv6A12PTo.setText(str6a12pToOut != null ? CommonFunctions.strCurrency
				+ " " + str6a12pToOut : null);

		tv12P6PTo.setText(str12p6pToOut != null ? CommonFunctions.strCurrency
				+ " " + str12p6pToOut : null);

		tv6P12Ato.setText(str6p12aToOut != null ? CommonFunctions.strCurrency
				+ " " + str6p12aToOut : null);

		// Collections.sort(al);
		// filterDepLow = al.get(0);
		// filterDepHigh = al.get(al.size() - 1);
		//
		// Collections.sort(a2);
		// filterArrLow = a2.get(0);
		// filterArrHigh = a2.get(a2.size() - 1);

//		Collections.sort(a3);
//		filterLayLow = a3.get(0);
//		filterLayHigh = a3.get(a3.size() - 1);

		al.clear();
		for (FlightResultItem fItem : flightResultItem) {
			al.add((long) fItem.intFlightStopsOne);
			if (strFlightType.equalsIgnoreCase("RoundTrip")) {
				try {
					cal.setTime(dateFormat.parse(fItem.DepartTimeTwo));
					minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
							+ cal.get(Calendar.MINUTE);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!bl12a6aFrom && minutes > 0 && minutes < 360) {
					str12a6aFromRet = fItem.strDisplayRate;
					bl12a6aFrom = true;
				}
				if (!bl6a12pFrom && minutes > 360 && minutes < 720) {
					str6a12pFromRet = fItem.strDisplayRate;
					bl6a12pFrom = true;
				}
				if (!bl12p6pFrom && minutes > 720 && minutes < 1080) {
					str12p6pFromRet = fItem.strDisplayRate;
					bl12p6pFrom = true;
				}
				if (!bl6p12aFrom && minutes > 1080 && minutes < 1440) {
					str6p12aFromRet = fItem.strDisplayRate;
					bl6p12aFrom = true;
				}

				try {
					cal.setTime(dateFormat.parse(fItem.ArrivalTimeTwo));
					minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
							+ cal.get(Calendar.MINUTE);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!bl12a6aTo && minutes > 0 && minutes < 360) {
					str12a6aToRet = fItem.strDisplayRate;
					bl12a6aTo = true;
				}
				if (!bl6a12pTo && minutes > 360 && minutes < 720) {
					str6a12pToRet = fItem.strDisplayRate;
					bl6a12pTo = true;
				}
				if (!bl12p6pTo && minutes > 720 && minutes < 1080) {
					str12p6pToRet = fItem.strDisplayRate;
					bl12p6pTo = true;
				}
				if (!bl6p12aTo && minutes > 1080 && minutes < 1440) {
					str6p12aToRet = fItem.strDisplayRate;
					bl6p12aTo = true;
				}
			}
		}

		bl12a6aFrom = false;
		bl6a12pFrom = false;
		bl12p6pFrom = false;
		bl6p12aFrom = false;
		bl12a6aTo = false;
		bl6a12pTo = false;
		bl12p6pTo = false;
		bl6p12aTo = false;

		blHasNonStop = al.contains((long) 0) ? true : false;
		blHasOneStop = al.contains((long) 1) ? true : false;
		blHasMultStop = (al.contains((long) 2) || al.contains((long) 3)
				|| al.contains((long) 4) || al.contains((long) 5)) ? true
				: false;

		filterMinDep = filterDepLow;
		filterMaxDep = filterDepHigh;
		filterMinArr = filterArrLow;
		filterMaxArr = filterArrHigh;
//		filterMinLay = filterLayLow;
//		filterMaxLay = filterLayHigh;
		lvFlightResult.setOnItemClickListener(FlightResultActivity.this);

		if (strFlightType.equalsIgnoreCase("Multicity"))
			((LinearLayout) findViewById(R.id.ll_timing_filter))
					.setVisibility(View.GONE);
		else if (strFlightType.equalsIgnoreCase("OneWay"))
			llTabReturn.setVisibility(View.GONE);

		((TextView) findViewById(R.id.tv_filter_from)).setText(strFromCode);
		((TextView) findViewById(R.id.tv_filter_to)).setText(strTocode);

	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.ll_modify:
			CommonFunctions.modify = true;
			// CommonFunctions.strSearchId = strSessionId;
			CommonFunctions.flighResult = new ArrayList<FlightResultItem>();
			CommonFunctions.flighResult.addAll(flightResultItem);
			finish();
			break;
		case R.id.tv_sort_by:
			showSortDialog();
			break;
		case R.id.ll_filter:
			if (((LinearLayout) findViewById(R.id.ll_sort_layout))
					.getVisibility() == View.VISIBLE)
				((LinearLayout) findViewById(R.id.ll_sort_layout))
						.setVisibility(View.GONE);
			if (((ScrollView) findViewById(R.id.sv_filter)).getVisibility() == View.VISIBLE)
				((ScrollView) findViewById(R.id.sv_filter))
						.setVisibility(View.GONE);
			else
				showFilterDialog();
			break;
		case R.id.ll_sort:
			if (((ScrollView) findViewById(R.id.sv_filter)).getVisibility() == View.VISIBLE)
				((ScrollView) findViewById(R.id.sv_filter))
						.setVisibility(View.GONE);
			if (((LinearLayout) findViewById(R.id.ll_sort_layout))
					.getVisibility() == View.VISIBLE)
				((LinearLayout) findViewById(R.id.ll_sort_layout))
						.setVisibility(View.GONE);
			else
				((LinearLayout) findViewById(R.id.ll_sort_layout))
						.setVisibility(View.VISIBLE);
			break;
		case R.id.ll_currency:
			dialogCurrency();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (((ScrollView) findViewById(R.id.sv_filter)).getVisibility() == View.VISIBLE)
			((ScrollView) findViewById(R.id.sv_filter))
					.setVisibility(View.GONE);
		else
			finish();
	}

	private String makePostRequest(boolean blCurr, String strCurrency) {

		// HttpClient httpClient = new DefaultHttpClient();
		// HttpGet httpGet = new HttpGet(main_url+str_url);

		// //Post Data
		// List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		// nameValuePair.add(new BasicNameValuePair("username", "test_user"));
		// nameValuePair.add(new BasicNameValuePair("password", "123456789"));

		// Encoding POST data
		// try {
		// httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// } catch (UnsupportedEncodingException e) {
		// // log exception
		// e.printStackTrace();
		// }

		// making POST request.
		try {

			URL url = null;
			if (blCurr) {
				url = new URL(CommonFunctions.main_url + CommonFunctions.lang
						+ "/FlightApi/CurrencyConvert?currency=" + strCurrency);// +
																				// "&searchID="
				// + strSessionId);
			} else {
				// strSessionId = CommonFunctions.getRandomString(6) + "_";
				url = new URL(main_url + str_url);// + strSessionId);
			}

			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			String cookie = cookieManager.getCookie(url.toString());
			Log.i("url", url.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			// urlConnection.setReadTimeout(15000);
			urlConnection.setRequestProperty("Cookie", cookie);
			urlConnection.setConnectTimeout(15000);
			urlConnection.setRequestMethod("GET");

			// Get cookies from responses and save into the cookie manager
			List<String> cookieList = urlConnection.getHeaderFields().get(
					"Set-Cookie");
			if (cookieList != null) {
				for (String cookieTemp : cookieList) {
					cookieManager.setCookie(urlConnection.getURL().toString(),
							cookieTemp);
				}
			}

			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			String resultString = convertStreamToString(in);
			urlConnection.disconnect();

			System.out.println("------------------Received result-------------"
					+ resultString);
			return resultString;
		} catch (SocketException e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(1);
		} catch (NullPointerException e) {
			// Log exception
			e.printStackTrace();
			handler.sendEmptyMessage(3);
		} catch (IOException e) {
			// Log exception
			e.printStackTrace();
			handler.sendEmptyMessage(1);
		}
		return null;
	}

	private void parseRoundtripResult(String result) {
		try {
			if (result != null) {
				JSONArray jarray = null;
				// Parse String to JSON object
				jarray = new JSONArray(result);

				if (jarray.length() == 0) {
					handler.sendEmptyMessage(3);
				} else {
					JSONObject c = null, allJourney = null, listFlight = null;
					FlightResultItem fItem = null;

					int length = jarray.length();
					System.out.println("Result count = " + length);
					// if(length > 40)
					// length = 40;

					for (int i = 0; i < length; i++) {
						fItem = new FlightResultItem();
						c = jarray.getJSONObject(i);

						JSONArray allJourneyArray = c
								.getJSONArray("AllJourney");
						for (int j = 0; j < allJourneyArray.length(); j++) {
							allJourney = allJourneyArray.getJSONObject(j);
							JSONArray listFlightArray = allJourney
									.getJSONArray("ListFlight");
							listFlight = listFlightArray.getJSONObject(0);

							flightName = listFlight.getString("FlightName");
							departureDateTime = listFlight
									.getString("DepartureDateTime");
							departureTimeString = listFlight
									.getString("DepartureTimeString");

							listFlight = listFlightArray
									.getJSONObject(listFlightArray.length() - 1);
							arrivalDateTime = listFlight
									.getString("ArrivalDateTime");
							arrivalTimeString = listFlight
									.getString("ArrivalTimeString");

							totalDurationInMinutes = allJourney
									.getString("TotalDurationInMinutes");
							stops = Integer.parseInt(allJourney
									.getString("stops"));

							departureDateTime = departureDateTime.substring(6,
									departureDateTime.length() - 2);
							arrivalDateTime = arrivalDateTime.substring(6,
									arrivalDateTime.length() - 2);

							switch (j) {
							case 0:
								fItem.DepartTimeOne = departureTimeString;
								fItem.ArrivalTimeOne = arrivalTimeString;
								fItem.intFlightStopsOne = stops;

//								fItem.longLayoverTimeInMins = listFlightArray
//										.getJSONObject(0).getLong(
//												"LayoverTimeMinutes");
//
//								if (fItem.intFlightStopsOne > 0) {
//									fItem.strLayoverAirport = listFlightArray
//											.getJSONObject(0).getString(
//													"ArrivalAirportName");
//									if (!arrayAirports
//											.contains(fItem.strLayoverAirport))
//										arrayAirports
//												.add(fItem.strLayoverAirport);
//								}

								fItem.DepartDateTimeOne = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeOne = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsOne = Long
										.parseLong(totalDurationInMinutes);
								fItem.str_AirlineName = flightName;

								if (!arrayAirline
										.contains(fItem.str_AirlineName)
										&& !fItem.str_AirlineName
												.equalsIgnoreCase(""))
									arrayAirline.add(fItem.str_AirlineName);
								break;
							case 1:
								fItem.DepartTimeTwo = departureTimeString;
								fItem.ArrivalTimeTwo = arrivalTimeString;
								// fItem.strFlightStopsTwo = stops;
								// fItem.strFlightDurationTwo = totalDuration;
								fItem.DepartDateTimeTwo = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeTwo = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsTwo = Long
										.parseLong(totalDurationInMinutes);

								break;
							case 2:
								fItem.DepartTimeThree = departureTimeString;
								fItem.ArrivalTimeThree = arrivalTimeString;
								// fItem.strFlightStopsThree = stops;
								// fItem.strFlightDurationThree = totalDuration;
								fItem.DepartDateTimeThree = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeThree = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsThree = Long
										.parseLong(totalDurationInMinutes);

								break;
							case 3:
								fItem.DepartTimeFour = departureTimeString;
								fItem.ArrivalTimeFour = arrivalTimeString;
								// fItem.strFlightStopsFour = stops;
								// fItem.strFlightDurationFour = totalDuration;
								fItem.DepartDateTimeFour = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeFour = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsFour = Long
										.parseLong(totalDurationInMinutes);

								break;
							}
						}
						fItem.intApiId = c.getInt("ApiId");
						fItem.strTripId = c.getString("TripId");
						fItem.doubleFlightPrice = Double.parseDouble(c
								.getString("FinalTotalFare"));
						fItem.strDisplayRate = c.getString("FinalTotalFare");
						fItem.strDeepLink = c.getString("deeplinkURL");
						fItem.blRefundType = c.getBoolean("IsRefundable");
						fItem.jarray = allJourneyArray;
						fItem.fareQuoteArray = c
								.getJSONArray("FareQuoteDetails");
						flightResultItem.add(fItem);

					}
				}

			}
			System.out
					.println("------------------Parsing finished-------------");

		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			if (blCurr)
				handler.sendEmptyMessage(4);
			else
				handler.sendEmptyMessage(3);
		} catch (NullPointerException e) {
			e.printStackTrace();
			if (blCurr)
				handler.sendEmptyMessage(4);
			else
				handler.sendEmptyMessage(3);
		} catch (Exception e) {
			e.printStackTrace();
			if (blCurr)
				handler.sendEmptyMessage(4);
			else
				handler.sendEmptyMessage(2);
		}
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private void showFilterDialog() {

		blReturn = cbTabReturn.isChecked();
		blOutbound = cbTabOutbound.isChecked();

		cb12A6AFrom.setChecked(bl12a6aFrom);
		cb6A12PFrom.setChecked(bl6a12pFrom);
		cb12P6PFrom.setChecked(bl12p6pFrom);
		cb6P12AFrom.setChecked(bl6p12aFrom);
		cb12A6ATo.setChecked(bl12a6aTo);
		cb6A12PTo.setChecked(bl6a12pTo);
		cb12P6PTo.setChecked(bl12p6pTo);
		cb6P12Ato.setChecked(bl6p12aTo);

		OnClickListener clikcr = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {

				case R.id.ll_tab_outbound:
					if (!cbTabOutbound.isChecked()) {
						cbTabOutbound.setChecked(true);
						cbTabReturn.setChecked(false);

						((TextView) findViewById(R.id.tv_filter_from))
								.setText(strFromCode);
						((TextView) findViewById(R.id.tv_filter_to))
								.setText(strTocode);

						tv12A6AFrom
								.setText(str12a6aFromOut != null ? CommonFunctions.strCurrency
										+ " " + str12a6aFromOut
										: null);

						tv6A12PFrom
								.setText(str6a12pFromOut != null ? CommonFunctions.strCurrency
										+ " " + str6a12pFromOut
										: null);

						tv12P6PFrom
								.setText(str12p6pFromOut != null ? CommonFunctions.strCurrency
										+ " " + str12p6pFromOut
										: null);

						tv6P12AFrom
								.setText(str6p12aFromOut != null ? CommonFunctions.strCurrency
										+ " " + str6p12aFromOut
										: null);

						tv12A6ATo
								.setText(str12a6aToOut != null ? CommonFunctions.strCurrency
										+ " " + str12a6aToOut
										: null);

						tv6A12PTo
								.setText(str6a12pToOut != null ? CommonFunctions.strCurrency
										+ " " + str6a12pToOut
										: null);

						tv12P6PTo
								.setText(str12p6pToOut != null ? CommonFunctions.strCurrency
										+ " " + str12p6pToOut
										: null);

						tv6P12Ato
								.setText(str6p12aToOut != null ? CommonFunctions.strCurrency
										+ " " + str6p12aToOut
										: null);
					}
					break;

				case R.id.ll_tab_return:
					if (!cbTabReturn.isChecked()) {
						cbTabReturn.setChecked(true);
						cbTabOutbound.setChecked(false);

						((TextView) findViewById(R.id.tv_filter_from))
								.setText(strTocode);
						((TextView) findViewById(R.id.tv_filter_to))
								.setText(strFromCode);

						tv12A6AFrom
								.setText(str12a6aFromRet != null ? CommonFunctions.strCurrency
										+ " " + str12a6aFromRet
										: null);
						tv6A12PFrom
								.setText(str6a12pFromRet != null ? CommonFunctions.strCurrency
										+ " " + str6a12pFromRet
										: null);
						tv12P6PFrom
								.setText(str12p6pFromRet != null ? CommonFunctions.strCurrency
										+ " " + str12p6pFromRet
										: null);
						tv6P12AFrom
								.setText(str6p12aFromRet != null ? CommonFunctions.strCurrency
										+ " " + str6p12aFromRet
										: null);
						tv12A6ATo
								.setText(str12a6aToRet != null ? CommonFunctions.strCurrency
										+ " " + str12a6aToRet
										: null);
						tv6A12PTo
								.setText(str6a12pToRet != null ? CommonFunctions.strCurrency
										+ " " + str6a12pToRet
										: null);
						tv12P6PTo
								.setText(str12p6pToRet != null ? CommonFunctions.strCurrency
										+ " " + str12p6pToRet
										: null);
						tv6P12Ato
								.setText(str6p12aToRet != null ? CommonFunctions.strCurrency
										+ " " + str6p12aToRet
										: null);
					}
					break;

				case R.id.ll_12a6a_from:
					if (cb12A6AFrom.isChecked()) {
						cb12A6AFrom.setChecked(false);
					} else {
						cb12A6AFrom.setChecked(true);
						cb6A12PFrom.setChecked(false);
						cb12P6PFrom.setChecked(false);
						cb6P12AFrom.setChecked(false);
					}
					break;

				case R.id.ll_6a12p_from:
					if (cb6A12PFrom.isChecked()) {
						cb6A12PFrom.setChecked(false);
					} else {
						cb12A6AFrom.setChecked(false);
						cb6A12PFrom.setChecked(true);
						cb12P6PFrom.setChecked(false);
						cb6P12AFrom.setChecked(false);
					}
					break;

				case R.id.ll_12p6p_from:
					if (cb12P6PFrom.isChecked()) {
						cb12P6PFrom.setChecked(false);
					} else {
						cb12A6AFrom.setChecked(false);
						cb6A12PFrom.setChecked(false);
						cb12P6PFrom.setChecked(true);
						cb6P12AFrom.setChecked(false);
					}
					break;

				case R.id.ll_6p12a_from:
					if (cb6P12AFrom.isChecked()) {
						cb6P12AFrom.setChecked(false);
					} else {
						cb12A6AFrom.setChecked(false);
						cb6A12PFrom.setChecked(false);
						cb12P6PFrom.setChecked(false);
						cb6P12AFrom.setChecked(true);
					}
					break;

				case R.id.ll_12a6a_to:
					if (cb12A6ATo.isChecked()) {
						cb12A6ATo.setChecked(false);
					} else {
						cb12A6ATo.setChecked(true);
						cb6A12PTo.setChecked(false);
						cb12P6PTo.setChecked(false);
						cb6P12Ato.setChecked(false);
					}
					break;

				case R.id.ll_6a12p_to:
					if (cb6A12PTo.isChecked()) {
						cb6A12PTo.setChecked(false);
					} else {
						cb12A6ATo.setChecked(false);
						cb6A12PTo.setChecked(true);
						cb12P6PTo.setChecked(false);
						cb6P12Ato.setChecked(false);
					}
					break;

				case R.id.ll_12p6p_to:
					if (cb12P6PTo.isChecked()) {
						cb12P6PTo.setChecked(false);
					} else {
						cb12A6ATo.setChecked(false);
						cb6A12PTo.setChecked(false);
						cb12P6PTo.setChecked(true);
						cb6P12Ato.setChecked(false);
					}
					break;

				case R.id.ll_6p12a_to:
					if (cb6P12Ato.isChecked()) {
						cb6P12Ato.setChecked(false);
					} else {
						cb12A6ATo.setChecked(false);
						cb6A12PTo.setChecked(false);
						cb12P6PTo.setChecked(false);
						cb6P12Ato.setChecked(true);
					}
					break;

				default:
					break;
				}
			}
		};

		llTabOutbound.setOnClickListener(clikcr);
		llTabReturn.setOnClickListener(clikcr);
		ll12A6AFrom.setOnClickListener(clikcr);
		ll6A12PFrom.setOnClickListener(clikcr);
		ll12P6PFrom.setOnClickListener(clikcr);
		ll6P12AFrom.setOnClickListener(clikcr);
		ll12A6ATo.setOnClickListener(clikcr);
		ll6A12PTo.setOnClickListener(clikcr);
		ll12P6PTo.setOnClickListener(clikcr);
		ll6P12Ato.setOnClickListener(clikcr);

		final boolean blNonStopTmp = blNonStop, blOneStopTmp = blOneStop, blMultiStopTmp = blMultiStop;
		final Double minValuePrice = filterMinPrice, maxValuePrice = filterMaxPrice;
		final Long minDep = filterMinDep, maxDep = filterMaxDep, minArr = filterMinArr, maxArr = filterMaxArr;
//		final Long minLay = filterMinLay, maxLay = filterMaxLay;

		if (!blHasNonStop)
			llNonStop.setVisibility(View.GONE);
		if (!blHasOneStop)
			llOneStop.setVisibility(View.GONE);
		if (!blHasMultStop)
			llMultistop.setVisibility(View.GONE);

		cbNonStop.setChecked(blNonStop);
		cbOneStop.setChecked(blOneStop);
		cbMultiStop.setChecked(blMultiStop);

		final RangeSeekBar<Double> priceBar = new RangeSeekBar<Double>(this);
		// Set the range
		priceBar.setRangeValues(
				(flightResultItem).get(0).doubleFlightPrice,
				(flightResultItem).get(flightResultItem.size() - 1).doubleFlightPrice);
		priceBar.setSelectedMinValue(filterMinPrice);
		priceBar.setSelectedMaxValue(filterMaxPrice);
		String price = String.format(new Locale("en"), "%.3f", filterMinPrice);
		tvRangeMin.setText(CommonFunctions.strCurrency + " " + price);
		price = String.format(new Locale("en"), "%.3f", filterMaxPrice);
		tvRangeMax.setText(CommonFunctions.strCurrency + " " + price);

		final RangeSeekBar<Long> departBar = new RangeSeekBar<Long>(this);
		departBar.setRangeValues(filterDepLow, filterDepHigh);
		departBar.setSelectedMinValue(filterMinDep);
		departBar.setSelectedMaxValue(filterMaxDep);
		String minDepart = new SimpleDateFormat("hh:mm aa", new Locale(
				CommonFunctions.lang)).format(new Date(filterMinDep));
		String maxDepart = new SimpleDateFormat("hh:mm aa", new Locale(
				CommonFunctions.lang)).format(new Date(filterMaxDep));
		if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
			minDepart = new SimpleDateFormat("hh:mm", new Locale("en"))
					.format(new Date(filterMinDep))
					+ " "
					+ new SimpleDateFormat("a").format(new Date(filterMinDep));
			maxDepart = new SimpleDateFormat("hh:mm", new Locale("en"))
					.format(new Date(filterMaxDep))
					+ " "
					+ new SimpleDateFormat("a").format(new Date(filterMaxDep));
		}
		tvDepartMin.setText(String.valueOf(minDepart));
		tvDepartMax.setText(String.valueOf(maxDepart));

		if (isRoundTrip) {
			final RangeSeekBar<Long> returnBar = new RangeSeekBar<Long>(this);
			returnBar.setRangeValues(filterArrLow, filterArrHigh);
			returnBar.setSelectedMinValue(filterMinArr);
			returnBar.setSelectedMaxValue(filterMaxArr);
			String minLanding = new SimpleDateFormat("hh:mm aa", new Locale(
					CommonFunctions.lang)).format(new Date(filterMinArr));
			String maxLanding = new SimpleDateFormat("hh:mm aa", new Locale(
					CommonFunctions.lang)).format(new Date(filterMaxArr));
			if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
				minLanding = new SimpleDateFormat("hh:mm", new Locale("en"))
						.format(new Date(filterMinArr))
						+ " "
						+ new SimpleDateFormat("a").format(new Date(
								filterMinDep));
				maxLanding = new SimpleDateFormat("hh:mm", new Locale("en"))
						.format(new Date(filterMaxArr))
						+ " "
						+ new SimpleDateFormat("a").format(new Date(
								filterMaxDep));
			}
			tvLandingMin.setText(String.valueOf(minLanding));
			tvLandingMax.setText(String.valueOf(maxLanding));

			returnBar
					.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
						@Override
						public void onRangeSeekBarValuesChanged(
								RangeSeekBar<?> bar, Long minValue,
								Long maxValue) {
							// handle changed range values
							String minLanding = new SimpleDateFormat("hh:mm a")
									.format(new Date(minValue));
							String maxLanding = new SimpleDateFormat("hh:mm a")
									.format(new Date(maxValue));

							if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
								minLanding = new SimpleDateFormat("hh:mm",
										new Locale("en")).format(new Date(
										minValue))
										+ " "
										+ new SimpleDateFormat("a")
												.format(new Date(minValue));
								maxLanding = new SimpleDateFormat("hh:mm",
										new Locale("en")).format(new Date(
										maxValue))
										+ " "
										+ new SimpleDateFormat("a")
												.format(new Date(maxValue));
							}

							tvLandingMin.setText(String.valueOf(minLanding));
							tvLandingMax.setText(String.valueOf(maxLanding));

							filterMinArr = minValue;
							filterMaxArr = maxValue;

							blArrTimeFilter = true;
						}
					});

			returnBar.setNotifyWhileDragging(true);
			llLandingBar.removeAllViews();
			llLandingBar.addView(returnBar);
			if (filterArrLow == filterArrHigh) {
				returnBar.setEnabled(false);
			}
		} else {
			((LinearLayout) findViewById(R.id.ll_return))
					.setVisibility(View.GONE);
		}

//		final RangeSeekBar<Long> layoverBar = new RangeSeekBar<Long>(this);
//		layoverBar.setRangeValues(filterLayLow, filterLayHigh);
//		layoverBar.setSelectedMinValue(filterMinLay);
//		layoverBar.setSelectedMaxValue(filterMaxLay);
//		String minLayover = null, maxLayover = null;
//		if (CommonFunctions.lang.equalsIgnoreCase("en")) {
//			minLayover = filterMinLay / 60 % 24 + " Hrs :" + filterMinLay % 60
//					+ " Mins";
//			maxLayover = filterMaxLay / 60 % 24 + " Hrs :" + filterMaxLay % 60
//					+ " Mins";
//		} else {
//			minLayover = filterMinLay / 60 % 24 + " ساعة :" + filterMinLay % 60
//					+ " دقيقة";
//			maxLayover = filterMaxLay / 60 % 24 + " ساعة :" + filterMaxLay % 60
//					+ " دقيقة";
//		}
//
//		tvLayoverMin.setText(String.valueOf(minLayover));
//		tvLayoverMax.setText(String.valueOf(maxLayover));

		priceBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Double>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Double minValue, Double maxValue) {
				// handle changed range values
				String price = String
						.format(new Locale("en"), "%.3f", minValue);
				tvRangeMin.setText(CommonFunctions.strCurrency + " " + price);
				price = String.format(new Locale("en"), "%.3f", maxValue);
				tvRangeMax.setText(CommonFunctions.strCurrency + " " + price);
				filterMinPrice = minValue;
				filterMaxPrice = maxValue;
				blPriceFilter = true;

			}
		});

		departBar
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Long minValue, Long maxValue) {
						// handle changed range values
						String minDepart = new SimpleDateFormat("hh:mm aa",
								new Locale(CommonFunctions.lang))
								.format(new Date(minValue));
						String maxDepart = new SimpleDateFormat("hh:mm aa",
								new Locale(CommonFunctions.lang))
								.format(new Date(maxValue));

						if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
							minDepart = new SimpleDateFormat("hh:mm",
									new Locale("en"))
									.format(new Date(minValue))
									+ " "
									+ new SimpleDateFormat("a")
											.format(new Date(minValue));
							maxDepart = new SimpleDateFormat("hh:mm",
									new Locale("en"))
									.format(new Date(maxValue))
									+ " "
									+ new SimpleDateFormat("a")
											.format(new Date(maxValue));
						}

						tvDepartMin.setText(String.valueOf(minDepart));
						tvDepartMax.setText(String.valueOf(maxDepart));

						filterMinDep = minValue;
						filterMaxDep = maxValue;

						blDepTimeFilter = true;
					}
				});

//		layoverBar
//				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
//					@Override
//					public void onRangeSeekBarValuesChanged(
//							RangeSeekBar<?> bar, Long minValue, Long maxValue) {
//						// handle changed range values
//						String minlay = null, maxLay = null;
//						if (CommonFunctions.lang.equalsIgnoreCase("en")) {
//							minlay = minValue / 60 % 24 + " Hrs :" + minValue
//									% 60 + " Mins";
//							maxLay = maxValue / 60 % 24 + " Hrs :" + maxValue
//									% 60 + " Mins";
//						} else {
//							minlay = minValue / 60 % 24 + " ساعة :" + minValue
//									% 60 + " دقيقة";
//							maxLay = maxValue / 60 % 24 + " ساعة :" + maxValue
//									% 60 + " دقيقة";
//						}
//
//						tvLayoverMin.setText(String.valueOf(minlay));
//						tvLayoverMax.setText(String.valueOf(maxLay));
//
//						filterMinLay = minValue;
//						filterMaxLay = maxValue;
//
//						blLayoverFilter = true;
//					}
//				});

		llRangeBar.removeAllViews();
		llDepartBar.removeAllViews();
//		llLayover.removeAllViews();

		priceBar.setNotifyWhileDragging(true);
		departBar.setNotifyWhileDragging(true);
//		layoverBar.setNotifyWhileDragging(true);

		llRangeBar.addView(priceBar);
		llDepartBar.addView(departBar);
//		llLayover.addView(layoverBar);

//		if (arrayAirports.size() == 0) {
			((LinearLayout) findViewById(R.id.ll_layover))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.ll_layover_airports))
					.setVisibility(View.GONE);
//		}

		if (filterMinPrice == filterMaxPrice)
			priceBar.setEnabled(false);

		if (filterDepLow == filterDepHigh) {
			departBar.setEnabled(false);
		}
//		if (filterLayLow == filterLayHigh) {
//			layoverBar.setEnabled(false);
//		}

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				blOneStop = blOneStopTmp;
				blNonStop = blNonStopTmp;
				blMultiStop = blMultiStopTmp;

				filterMinPrice = minValuePrice;
				filterMaxPrice = maxValuePrice;
				filterMinDep = minDep;
				filterMaxDep = maxDep;
				filterMinArr = minArr;
				filterMaxArr = maxArr;
//				filterMinLay = minLay;
//				filterMaxLay = maxLay;

				((ScrollView) findViewById(R.id.sv_filter))
						.setVisibility(View.GONE);
			}
		});

		llNonStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (blNonStop)
					blNonStop = false;
				else
					blNonStop = true;
				cbNonStop.setChecked(blNonStop);
			}
		});

		llOneStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (blOneStop)
					blOneStop = false;
				else
					blOneStop = true;
				cbOneStop.setChecked(blOneStop);
			}
		});

		llMultistop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (blMultiStop)
					blMultiStop = false;
				else
					blMultiStop = true;
				cbMultiStop.setChecked(blMultiStop);
			}
		});

		llArlineList.removeAllViews();

		if (arrayAirline.size() > 0) {
			for (int i = 0; i < arrayAirline.size(); ++i) {
				final View view = getLayoutInflater().inflate(
						R.layout.custom_check_box_list_item, null);
				((CheckBox) view.findViewById(R.id.cb_check_airline))
						.setClickable(false);
				((TextView) view.findViewById(R.id.tv_airline_name))
						.setText(arrayAirline.get(i).toString());
				if (checkedAirlines.contains(arrayAirline.get(i))) {
					((CheckBox) view.findViewById(R.id.cb_check_airline))
							.setChecked(true);
					cbCheckAll.setChecked(true);
				}
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (((CheckBox) view
								.findViewById(R.id.cb_check_airline))
								.isChecked())
							((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.setChecked(false);
						else
							((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.setChecked(true);

						View view;
						for (int j = 0; j < arrayAirline.size(); ++j) {
							view = (View) llArlineList.findViewById(j);
							if (((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.isChecked()) {
								cbCheckAll.setChecked(true);
								break;
							} else
								cbCheckAll.setChecked(false);
						}
					}
				});
				view.setId(i);
				llArlineList.addView(view);
			}
		} else {
			cbCheckAll.setEnabled(false);
		}

		cbCheckAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view;
				if (cbCheckAll.isChecked()) {
					for (int i = 0; i < arrayAirline.size(); ++i) {
						view = (View) llArlineList.findViewById(i);
						((CheckBox) view.findViewById(R.id.cb_check_airline))
								.setChecked(true);
					}
					checkedAirlines.clear();
					checkedAirlines.addAll(arrayAirline);
				} else {
					for (int i = 0; i < arrayAirline.size(); ++i) {
						view = (View) llArlineList.findViewById(i);
						((CheckBox) view.findViewById(R.id.cb_check_airline))
								.setChecked(false);
					}
					checkedAirlines.clear();
				}
			}
		});

		llAirportList.removeAllViews();

//		if (arrayAirports.size() > 0) {
//			for (int i = 0; i < arrayAirports.size(); ++i) {
//				final View view = getLayoutInflater().inflate(
//						R.layout.custom_check_box_list_item, null);
//				((CheckBox) view.findViewById(R.id.cb_check_airline))
//						.setClickable(false);
//				((TextView) view.findViewById(R.id.tv_airline_name))
//						.setText(arrayAirports.get(i).toString());
//				if (checkedAirports.contains(arrayAirports.get(i))) {
//					((CheckBox) view.findViewById(R.id.cb_check_airline))
//							.setChecked(true);
//				}
//				view.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						if (((CheckBox) view
//								.findViewById(R.id.cb_check_airline))
//								.isChecked())
//							((CheckBox) view
//									.findViewById(R.id.cb_check_airline))
//									.setChecked(false);
//						else
//							((CheckBox) view
//									.findViewById(R.id.cb_check_airline))
//									.setChecked(true);
//
//						// View view;
//						// for (int j = 0; j < arrayAirports.size(); ++j) {
//						// view = (View) llAirportList.findViewById(j);
//						// if (((CheckBox) view
//						// .findViewById(R.id.cb_check_airline))
//						// .isChecked()) {
//						// cbCheckAll.setChecked(true);
//						// break;
//						// } else
//						// cbCheckAll.setChecked(false);
//						// }
//					}
//				});
//				view.setId(i);
//				llAirportList.addView(view);
//			}
//		}

		btnApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				blOutbound = cbTabOutbound.isChecked();
				blReturn = cbTabReturn.isChecked();

				bl12a6aFrom = cb12A6AFrom.isChecked();
				bl6a12pFrom = cb6A12PFrom.isChecked();
				bl12p6pFrom = cb12P6PFrom.isChecked();
				bl6p12aFrom = cb6P12AFrom.isChecked();

				bl12a6aTo = cb12A6ATo.isChecked();
				bl6a12pTo = cb6A12PTo.isChecked();
				bl12p6pTo = cb12P6PTo.isChecked();
				bl6p12aTo = cb6P12Ato.isChecked();

				checkedAirlines.clear();
				int i = 0;
				for (i = 0; i < arrayAirline.size(); ++i) {
					final View view = (View) llArlineList.findViewById(i);
					if (((CheckBox) view.findViewById(R.id.cb_check_airline))
							.isChecked())
						checkedAirlines.add(arrayAirline.get(i));
				}

				checkedAirports.clear();
				for (i = 0; i < arrayAirports.size(); ++i) {
					final View view = (View) llAirportList.findViewById(i);
					if (((CheckBox) view.findViewById(R.id.cb_check_airline))
							.isChecked())
						checkedAirports.add(arrayAirports.get(i));
				}

				blNameFilter = checkedAirlines.size() > 0 ? true : false;

				blLayAirportFilter = checkedAirports.size() > 0 ? true : false;

				new filter().execute();

				// ((ScrollView) findViewById(R.id.sv_filter))
				// .setVisibility(View.GONE);
				// blSortPrice = true;
				// blSortDep = false;
				// blSortArrival = false;
				// blSortDuration = false;
				// strSortPriceType = "Low";
				// strSortDepType = null;
				// strSortArrivalType = null;
				// strSortDurationType = null;

			}
		});

		((ScrollView) findViewById(R.id.sv_filter)).scrollTo(0, 0);
		((ScrollView) findViewById(R.id.sv_filter)).setVisibility(View.VISIBLE);

	}

	private void showSortDialog() {
		llSort.removeAllViews();
		for (int i = 0; i < sortText.length; ++i) {
			final View view = getLayoutInflater().inflate(
					R.layout.sort_list_item, null);
			if (i % 2 == 0) {
				final TextView tvView = (TextView) getLayoutInflater().inflate(
						R.layout.tv_autocomplete, null);
				tvView.setText(sortHeading[i / 2]);
				llSort.addView(tvView);
			}
			final RadioButton rb = (RadioButton) view
					.findViewById(R.id.rb_sort_item);
			rb.setClickable(false);
			((TextView) view.findViewById(R.id.tv_sort_item_name))
					.setText(sortText[i]);

			switch (i) {
			case 0:
				rb.setChecked(blSortPrice
						&& strSortPriceType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 1:
				rb.setChecked(blSortPrice
						&& strSortPriceType.equalsIgnoreCase("High") ? true
						: false);
				break;
			case 2:
				rb.setChecked(blSortDep
						&& strSortDepType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 3:
				rb.setChecked(blSortDep
						&& strSortDepType.equalsIgnoreCase("High") ? true
						: false);
				break;
			case 4:
				rb.setChecked(blSortArrival
						&& strSortArrivalType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 5:
				rb.setChecked(blSortArrival
						&& strSortArrivalType.equalsIgnoreCase("High") ? true
						: false);
				break;
			case 6:
				rb.setChecked(blSortDuration
						&& strSortDurationType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 7:
				rb.setChecked(blSortDuration
						&& strSortDurationType.equalsIgnoreCase("High") ? true
						: false);
				break;
			case 8:
				rb.setChecked(blSortAirName
						&& strSortAirNameType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 9:
				rb.setChecked(blSortAirName
						&& strSortAirNameType.equalsIgnoreCase("High") ? true
						: false);
				break;
			}

			view.setId(i);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!rb.isChecked()) {
						rb.setChecked(true);

						tvSortBy.setText(sortText[view.getId()]);
						tvSortByType.setText(sortHeading[view.getId() / 2]);

						switch (view.getId()) {
						case 0:
							blSortPrice = true;
							blSortDep = false;
							blSortArrival = false;
							blSortDuration = false;
							blSortAirName = false;
							strSortPriceType = "Low";

							break;

						case 1:
							blSortPrice = true;
							blSortDep = false;
							blSortArrival = false;
							blSortDuration = false;
							blSortAirName = false;
							strSortPriceType = "High";
							break;

						case 2:
							blSortPrice = false;
							blSortDep = true;
							blSortArrival = false;
							blSortDuration = false;
							blSortAirName = false;
							strSortDepType = "Low";
							break;

						case 3:
							blSortPrice = false;
							blSortDep = true;
							blSortArrival = false;
							blSortDuration = false;
							blSortAirName = false;
							strSortDepType = "High";
							break;

						case 4:
							blSortPrice = false;
							blSortDep = false;
							blSortArrival = true;
							blSortDuration = false;
							blSortAirName = false;
							strSortArrivalType = "Low";
							break;

						case 5:
							blSortPrice = false;
							blSortDep = false;
							blSortArrival = true;
							blSortDuration = false;
							blSortAirName = false;
							strSortArrivalType = "High";
							break;

						case 6:
							blSortPrice = false;
							blSortDep = false;
							blSortArrival = false;
							blSortDuration = true;
							blSortAirName = false;
							strSortDurationType = "Low";
							break;

						case 7:
							blSortPrice = false;
							blSortDep = false;
							blSortArrival = false;
							blSortDuration = true;
							blSortAirName = false;
							strSortDurationType = "High";
							break;

						case 8:
							blSortPrice = false;
							blSortDep = false;
							blSortArrival = false;
							blSortDuration = false;
							blSortAirName = true;
							strSortAirNameType = "Low";
							break;

						case 9:
							blSortPrice = false;
							blSortDep = false;
							blSortArrival = false;
							blSortDuration = false;
							blSortAirName = true;
							strSortAirNameType = "High";
							break;

						default:
							break;
						}
						if (((LinearLayout) findViewById(R.id.ll_sort_layout))
								.getVisibility() == View.VISIBLE)
							((LinearLayout) findViewById(R.id.ll_sort_layout))
									.setVisibility(View.GONE);
						sortArrayList();
					}
					dialogSort.dismiss();
				}
			});
			llSort.addView(view);

		}
		dialogSort.show();

	}

	private void sortArrayList() {
		ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
		if (blPriceFilter || blDepTimeFilter || blArrTimeFilter || blNonStop
				|| blOneStop || blMultiStop || blNameFilter || blLayoverFilter
				|| bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
				|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo
				|| blLayAirportFilter) {
			temp = filteredResult;
		} else {
			temp = flightResultItemsTemp;
		}

		if (blSortPrice) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.doubleFlightPrice)
								.compareTo(rhs.doubleFlightPrice);
					}
				});
				if (strSortPriceType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}
		} else if (blSortDep) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.DepartDateTimeOne)
								.compareTo(rhs.DepartDateTimeOne);
					}
				});
				if (strSortDepType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}
		} else if (blSortArrival) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.ArrivalDateTimeOne)
								.compareTo(rhs.ArrivalDateTimeOne);
					}
				});
				if (strSortArrivalType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}
		} else if (blSortDuration) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.longFlightDurationInMinsOne)
								.compareTo(rhs.longFlightDurationInMinsOne);
					}
				});
				if (strSortDurationType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}

		} else {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return lhs.str_AirlineName
								.compareToIgnoreCase(rhs.str_AirlineName);
					}
				});
				if (strSortAirNameType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}

		}

		lvFlightResult.setAdapter(new FlightResultAdapter(
				FlightResultActivity.this, temp, isRoundTrip));
		// lvFlightResult.setOnItemClickListener(this);
		// if(blPriceFilter || blDepTimeFilter || blArrTimeFilter || blNonStop
		// || blOneStop || blMultiStop )
		// {
		// filteredResult = temp;
		// }
		// else
		// {
		// flightResultItemsTemp = temp;
		// }
	}

	private class filter extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			filteredResult = new ArrayList<FlightResultItem>();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if (blNonStop || blOneStop || blMultiStop) {
				for (FlightResultItem fitem : flightResultItemsTemp) {
					if (fitem.intFlightStopsOne == 0 && blNonStop)
						filteredResult.add(fitem);
					else if (fitem.intFlightStopsOne == 1 && blOneStop)
						filteredResult.add(fitem);
					else if (fitem.intFlightStopsOne > 1 && blMultiStop)
						filteredResult.add(fitem);
				}
			} else
				filteredResult.addAll(flightResultItemsTemp);

			if (!strFlightType.equalsIgnoreCase("Multicity")) {
				if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
						|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo) {
					ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"hh:mm aa", new Locale(CommonFunctions.lang));
					Calendar cal = Calendar.getInstance();
					int minutes = -1;
					if (blOutbound) {
						if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
								|| bl6p12aFrom) {
							for (FlightResultItem fitem : filteredResult) {

								try {
									cal.setTime(dateFormat
											.parse(fitem.DepartTimeOne));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aFrom && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pFrom && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pFrom && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aFrom && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}

						if (bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo) {
							temp.clear();
							for (FlightResultItem fitem : filteredResult) {
								try {
									cal.setTime(dateFormat
											.parse(fitem.ArrivalTimeOne));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aTo && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pTo && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pTo && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aTo && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}
					} else if (blReturn) {
						if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
								|| bl6p12aFrom) {
							for (FlightResultItem fitem : filteredResult) {

								try {
									cal.setTime(dateFormat
											.parse(fitem.DepartTimeTwo));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aFrom && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pFrom && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pFrom && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aFrom && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}
						if (bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo) {
							temp.clear();
							for (FlightResultItem fitem : filteredResult) {
								try {
									cal.setTime(dateFormat
											.parse(fitem.ArrivalTimeTwo));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aTo && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pTo && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pTo && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aTo && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}
					}

				}
			}

			if (blPriceFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				for (FlightResultItem fitem : filteredResult) {
					if (fitem.doubleFlightPrice >= filterMinPrice
							&& fitem.doubleFlightPrice <= filterMaxPrice) {
						temp.add(fitem);
					}
				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blDepTimeFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
						new Locale(CommonFunctions.lang));
				Calendar cal = Calendar.getInstance();
				for (FlightResultItem fitem : filteredResult) {

					try {
						cal.setTime(dateFormat.parse(fitem.DepartTimeOne));
						if (cal.getTimeInMillis() >= filterMinDep
								&& cal.getTimeInMillis() <= filterMaxDep) {
							temp.add(fitem);
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blArrTimeFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
						new Locale(CommonFunctions.lang));
				Calendar cal = Calendar.getInstance();
				for (FlightResultItem fitem : filteredResult) {
					try {
						cal.setTime(dateFormat.parse(fitem.DepartTimeTwo));
						if (cal.getTimeInMillis() >= filterMinArr
								&& cal.getTimeInMillis() <= filterMaxArr) {
							temp.add(fitem);
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

//			if (blLayoverFilter) {
//				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
//				for (FlightResultItem fitem : filteredResult) {
//					if (fitem.longLayoverTimeInMins >= filterMinLay
//							&& fitem.longLayoverTimeInMins <= filterMaxLay) {
//						temp.add(fitem);
//					}
//				}
//				filteredResult.clear();
//				filteredResult.addAll(temp);
//			}

			if (blNameFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				for (FlightResultItem fitem : filteredResult) {
					if (checkedAirlines.contains(fitem.str_AirlineName)) {
						temp.add(fitem);
					}
				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

//			if (blLayAirportFilter) {
//				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
//				for (FlightResultItem fitem : filteredResult) {
//					if (checkedAirports.contains(fitem.strLayoverAirport)) {
//						temp.add(fitem);
//					}
//				}
//				filteredResult.clear();
//				filteredResult.addAll(temp);
//			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			((ScrollView) findViewById(R.id.sv_filter))
					.setVisibility(View.GONE);
			if (filteredResult.size() > 0)
				sortArrayList();
			else {
				lvFlightResult.setAdapter(null);
				noResultAlert(true, getResources()
						.getString(R.string.no_result));
			}
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flightResultItem.clear();
		flightResultItemsTemp.clear();
		arrayAirline.clear();
		checkedAirlines.clear();
	}

	public static FlightResultItem selectedFItem;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		selectedFItem = (FlightResultItem) lvFlightResult
				.getItemAtPosition(position);

		new backService().execute(selectedFItem);

	}

	public class backService extends AsyncTask<FlightResultItem, Void, String> {

		FlightResultItem fItem;
		String sessionResult = "";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// loaderDialog.show();
			// tvProgressText.setText(getResources().getString(
			// R.string.checking_flight));
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(FlightResultItem... params) {
			// TODO Auto-generated method stub
			URL url = null;
			HttpURLConnection urlConnection = null;
			try {
				url = new URL(CommonFunctions.main_url
						+ "en/FlightApi/AvailResult?tripId="
						+ params[0].strTripId);
				// + "&searchID=" + strSessionId);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());
				Log.i("url", url.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				// urlConnection.setReadTimeout(15000);
				urlConnection.setRequestProperty("Cookie", cookie);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setRequestMethod("GET");
				InputStream in;
				in = new BufferedInputStream(urlConnection.getInputStream());
				sessionResult = convertStreamToString(in);
				System.out.println("result" + sessionResult);
				urlConnection.disconnect();
				JSONObject json = new JSONObject(sessionResult);
				sessionResult = json.getString("Status");
				if (sessionResult.equalsIgnoreCase("true"))
					fItem = params[0];

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				urlConnection.disconnect();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				urlConnection.disconnect();
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// urlConnection.disconnect();
				e.printStackTrace();
			}
			return params[0].strDeepLink;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			// if (loaderDialog.isShowing())
			// loaderDialog.dismiss();
			if (sessionResult.equalsIgnoreCase("true")) {

				Intent details = new Intent(FlightResultActivity.this,
						SearchPageActivity.class);
				// details.putExtra("sessionID", strSessionId);
				details.putExtra("url", result);
				startActivity(details);
			} else {
				tvProgressText.setText(getResources().getString(
						R.string.flight_expired));
				new backMethod().execute("");
			}
			super.onPostExecute(result);
		}

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						"There is a problem on your Network. Please try again later.");

			} else if (msg.what == 2) {

				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						"There is a problem on your application. Please report it.");

			} else if (msg.what == 3) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						getResources().getString(R.string.no_result));
			} else if (msg.what == 4) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						"Something went wrong. Please try again later");
			}

		}
	};

	public void noResultAlert(final boolean filter, String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setMessage(msg);

		if (filter)
			alertDialog.setPositiveButton(
					getResources().getString(R.string.reset_filter),
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							flightResultItemsTemp.clear();
							flightResultItemsTemp.addAll(flightResultItem);

							filterMinPrice = (flightResultItem).get(0).doubleFlightPrice;
							filterMaxPrice = (flightResultItem)
									.get(flightResultItem.size() - 1).doubleFlightPrice;

							// ArrayList<Long> al = new ArrayList<Long>();
							// for (FlightResultItem fItem : flightResultItem) {
							// al.add(fItem.DepartDateTimeOne);
							// }
							// Collections.sort(al);
							// filterDepLow = al.get(0);
							// filterDepHigh = al.get(al.size() - 1);
							//
							// al.clear();
							// for (FlightResultItem fItem : flightResultItem) {
							// al.add(fItem.ArrivalDateTimeOne);
							// }
							// Collections.sort(al);
							// filterArrLow = al.get(0);
							// filterArrHigh = al.get(al.size() - 1);

							filterMinDep = filterDepLow;
							filterMaxDep = filterDepHigh;
							filterMinArr = filterArrLow;
							filterMaxArr = filterArrHigh;
//							filterMinLay = filterLayLow;
//							filterMaxLay = filterLayHigh;
							blOutbound = true;
							blReturn = false;
							bl12a6aFrom = false;
							bl6a12pFrom = false;
							bl12p6pFrom = false;
							bl6p12aFrom = false;
							bl12a6aTo = false;
							bl6a12pTo = false;
							bl12p6pTo = false;
							bl6p12aTo = false;
							blOneStop = false;
							blNonStop = false;
							blMultiStop = false;
							blPriceFilter = false;
							blDepTimeFilter = false;
							blArrTimeFilter = false;
							blLayoverFilter = false;
							blStopFilter = false;
							blNameFilter = false;
							blLayAirportFilter = false;
							checkedAirlines.clear();
							checkedAirports.clear();
							cbCheckAll.setChecked(false);
							sortArrayList();
						}
					});

		else {
			alertDialog.setPositiveButton(
					getResources().getString(R.string.ok),
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (!blCurr)
								finish();
						}
					});
		}
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	private void dialogCurrency() {
		curr = new Dialog(FlightResultActivity.this,
				android.R.style.Theme_Translucent);
		curr.requestWindowFeature(Window.FEATURE_NO_TITLE);
		curr.getWindow().setGravity(Gravity.TOP);
		curr.setContentView(R.layout.dialog_currency);
		curr.setCancelable(false);

		final ImageView close;
		final LinearLayout llKWD, llINR, llUSD, llQAR, llEUR, llAED, llSAR, llIQD, llGBP;
		final LinearLayout llGEL, llBHD, llOMR;
		close = (ImageView) curr.findViewById(R.id.iv_close);
		llKWD = (LinearLayout) curr.findViewById(R.id.ll_KWD);
		llINR = (LinearLayout) curr.findViewById(R.id.ll_INR);
		llUSD = (LinearLayout) curr.findViewById(R.id.LL_USD);
		llQAR = (LinearLayout) curr.findViewById(R.id.ll_QAR);
		llEUR = (LinearLayout) curr.findViewById(R.id.ll_EUR);
		llAED = (LinearLayout) curr.findViewById(R.id.LL_AED);
		llSAR = (LinearLayout) curr.findViewById(R.id.LL_SAR);
		llIQD = (LinearLayout) curr.findViewById(R.id.ll_IQD);
		llGBP = (LinearLayout) curr.findViewById(R.id.ll_GBP);
		llGEL = (LinearLayout) curr.findViewById(R.id.ll_GEL);
		llBHD = (LinearLayout) curr.findViewById(R.id.ll_BHD);
		llOMR = (LinearLayout) curr.findViewById(R.id.ll_OMR);

		// default

		if (CommonFunctions.strCurrency.equalsIgnoreCase("KWD"))
			llKWD.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("INR"))
			llINR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("USD"))
			llUSD.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("QAR"))
			llQAR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("EUR"))
			llEUR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("AED"))
			llAED.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("SAR"))
			llSAR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("IQD"))
			llIQD.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("GBP"))
			llGBP.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("GEL"))
			llGEL.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("OMR"))
			llOMR.setBackgroundResource(R.drawable.border_with_background);
		else
			llBHD.setBackgroundResource(R.drawable.border_with_background);

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				curr.dismiss();
			}
		});

		llKWD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border_with_background);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("KWD");

				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llINR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border_with_background);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("INR");
				// CommonFunctions.strCurrency = "INR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llUSD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border_with_background);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("USD");
				// CommonFunctions.strCurrency = "USD";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llQAR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border_with_background);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("QAR");
				// CommonFunctions.strCurrency = "QAR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llEUR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border_with_background);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("EUR");
				// CommonFunctions.strCurrency = "EUR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llAED.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border_with_background);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("AED");
				// CommonFunctions.strCurrency = "AED";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llSAR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border_with_background);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("SAR");
				// CommonFunctions.strCurrency = "SAR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llIQD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border_with_background);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("IQD");
				// CommonFunctions.strCurrency = "IQD";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llGBP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border_with_background);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("GBP");
				// CommonFunctions.strCurrency = "GBP";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llGEL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border_with_background);
				llBHD.setBackgroundResource(R.drawable.border);

				blCurr = true;
				new backMethod().execute("GEL");
				// CommonFunctions.strCurrency = "GEL";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llBHD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border_with_background);

				blCurr = true;
				new backMethod().execute("BHD");
				// CommonFunctions.strCurrency = "BHD";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llOMR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);
				llOMR.setBackgroundResource(R.drawable.border_with_background);

				blCurr = true;
				new backMethod().execute("OMR");
				// CommonFunctions.strCurrency = "OMR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		curr.show();

	}

	private void loadLocale() {
		// TODO Auto-generated method stub
		SharedPreferences sharedpreferences = this.getSharedPreferences(
				"CommonPrefs", Context.MODE_PRIVATE);
		String lang = sharedpreferences.getString("Language", "en");
		System.out.println("Default lang: " + lang);
		if (lang.equalsIgnoreCase("ar")) {
			myLocale = new Locale(lang);
			saveLocale(lang);
			Locale.setDefault(myLocale);
			android.content.res.Configuration config = new android.content.res.Configuration();
			config.locale = myLocale;
			this.getBaseContext()
					.getResources()
					.updateConfiguration(
							config,
							this.getBaseContext().getResources()
									.getDisplayMetrics());
			CommonFunctions.lang = "ar";
		} else {
			myLocale = new Locale(lang);
			saveLocale(lang);
			Locale.setDefault(myLocale);
			android.content.res.Configuration config = new android.content.res.Configuration();
			config.locale = myLocale;
			this.getBaseContext()
					.getResources()
					.updateConfiguration(
							config,
							this.getBaseContext().getResources()
									.getDisplayMetrics());
			CommonFunctions.lang = "en";
		}
	}

	public void saveLocale(String lang) {
		CommonFunctions.lang = lang;
		String langPref = "Language";
		SharedPreferences prefs = this.getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}

}
