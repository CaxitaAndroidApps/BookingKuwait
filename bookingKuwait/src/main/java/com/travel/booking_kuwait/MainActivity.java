package com.travel.booking_kuwait;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.travel.booking_kuwait.Fragments.AboutFragment;
import com.travel.booking_kuwait.Fragments.ContactFragment;
import com.travel.booking_kuwait.Fragments.FlightFragment;
import com.travel.booking_kuwait.Fragments.HomeFragment;
import com.travel.booking_kuwait.Fragments.HotelFragment;
import com.travel.booking_kuwait.Support.CommonFunctions;
import com.travel.booking_kuwait.adapter.NavDrawerListAdapter;
import com.travel.booking_kuwait.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

	public static boolean loadDef0 = true;
	static int menu_pos = 0;
	int i = 0;
	Fragment fragment = null;
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction;
	ImageButton menuBtn;
	ImageView ivLogo;
	LinearLayout llHome, llslider, llContainer;
	String lang = "en";
	LinearLayout llFlight, llHotel;
	TextView tvFlight, tvHotel, tvFlightHotel, tvCurrency, toggle;
	Dialog dialogFAQ, curr;
	private DrawerLayout mDrawerLayout;
	private boolean doubleBackToExitPressedOnce = false;
	private ListView mDrawerList;
	// slide menu items
	private String[] navMenuTitles;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private Locale myLocale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		loadLocale();

		fragmentManager = getFragmentManager();
		
		initialize();

		addListItems();
		loadAppBar();

		displayView(0);
		
		if(CommonFunctions.updateApp) {
			AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);

	        // Setting Dialog Title
	        alertDialog.setTitle("New update available");

	        // Setting Dialog Message
	        alertDialog.setMessage("New version of application is availabale, please update application to continue..");

	        // Setting OK Button
	        alertDialog.setPositiveButton(getResources().getString(R.string.ok), new AlertDialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//intent to move mobile settings
					final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
					try {
						finishAffinity();
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
					    
					} catch (android.content.ActivityNotFoundException anfe) {
						finishAffinity();
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
					}
				}});
	        alertDialog.setNegativeButton(getResources().getString(R.string.error_no_internet_close_app), new DialogInterface.OnClickListener() {
	    		
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			// TODO Auto-generated method stub
	    			finishAffinity();
	    		}
	    	});

	        // Showing Alert Message
	        alertDialog.setCancelable(false);
	        alertDialog.show();
		}
	}

	private void initialize() {
		setContentView(R.layout.activity_main);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		llslider = (LinearLayout) findViewById(R.id.ll_slider);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		llHome = (LinearLayout) findViewById(R.id.ll_home_layout);
		llContainer = (LinearLayout) findViewById(R.id.frame_container);
		llFlight = (LinearLayout) findViewById(R.id.ll_flight);
		llHotel = (LinearLayout) findViewById(R.id.ll_hotel);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if (CommonFunctions.modify)
			getActionBar().hide();
		else
			getActionBar().show();
		if (loadDef0)
			displayView(0);
		super.onRestart();
	}

	private void loadAppBar() {
		// ============== Define a Custom Header for Navigation
		// drawer=================//
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header, null);

		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(v);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			try {
				Toolbar parent = (Toolbar) v.getParent();
				parent.setContentInsetsAbsolute(0, 0);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}

		menuBtn = (ImageButton) v.findViewById(R.id.imgLefttMenu);
		toggle = (TextView) v.findViewById(R.id.tb_lang);
		tvCurrency = (TextView) v.findViewById(R.id.tv_currency);
		ivLogo = (ImageView) v.findViewById(R.id.iv_logo);
		menuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDrawerLayout.isDrawerOpen(llslider)) {
					mDrawerLayout.closeDrawer(llslider);
				} else {
					mDrawerLayout.openDrawer(llslider);
				}
			}
		});

		toggle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CommonFunctions.lang.equalsIgnoreCase("en"))
					lang = "ar";
				else
					lang = "en";

				changeLang(lang);
			}
		});

		tvCurrency.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogCurrency();
			}
		});

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

	public void clicker(View v) {
		fragmentTransaction = fragmentManager.beginTransaction();
		switch (v.getId()) {
		case R.id.ll_flight:

			llHome.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
			menu_pos = -1;
			llFlight.setEnabled(false);
			llHotel.setEnabled(false);
			fragment = new FlightFragment();
			fragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			fragmentTransaction.replace(R.id.frame_container, fragment,
					"flight");
			fragmentTransaction.commit();
			mDrawerList.setItemChecked(0, false);
			loadDef0 = false;
			ivLogo.setVisibility(View.VISIBLE);
			break;
		case R.id.ll_hotel:
			llHome.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
			menu_pos = -1;
			llFlight.setEnabled(false);
			llHotel.setEnabled(false);
			fragment = new HotelFragment();
			fragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			fragmentTransaction
					.replace(R.id.frame_container, fragment, "hotel");
			fragmentTransaction.commit();
			mDrawerList.setItemChecked(0, false);
			loadDef0 = false;
			ivLogo.setVisibility(View.VISIBLE);
			break;

		case R.id.ll_deals:
			Intent deal = new Intent(MainActivity.this,
					SearchPageActivity.class);
			deal.putExtra("url", "http://bookingkuwait.com/"
					+ CommonFunctions.lang
					+ "/Shared/CmsPage?pagename=TravelDeals&section=app");
			startActivity(deal);
			break;
		case R.id.callus:
			Intent call = new Intent(Intent.ACTION_DIAL);
			call.setData(Uri.parse("tel:+96590979740"));
			startActivity(call);
		default:
			break;
		}
	}

	private void addListItems() {
		// TODO Auto-generated method stub
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6]));

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		loadDef0 = true;
		mDrawerLayout.closeDrawer(llslider);
		mDrawerList.setItemChecked(position, true);
		switch (position) {
		case 0: {
			ivLogo.setVisibility(View.GONE);
			llContainer.setVisibility(View.GONE);
			llFlight.setEnabled(true);
			llHotel.setEnabled(true);
			llHome.setVisibility(View.VISIBLE);
			fragment = new HomeFragment();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.frame_container, fragment, "home");
			fragmentTransaction.commit();
			break;
		}

		case 1: {
			fragment = new AboutFragment();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.frame_container, fragment, "about");
			fragmentTransaction.commit();
			llHome.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			break;
		}

		case 2:
			Intent services = new Intent(MainActivity.this,
					SearchPageActivity.class);
			services.putExtra("url", "http://bookingkuwait.com/"
					+ CommonFunctions.lang
					+ "/Shared/CmsPage?pagename=OurServices&section=app");
			startActivity(services);
			break;

		case 3:
			Intent news = new Intent(MainActivity.this,
					SearchPageActivity.class);
			news.putExtra("url", "http://bookingkuwait.com/"
					+ CommonFunctions.lang
					+ "/Shared/CmsPage?pagename=News&section=app");
			startActivity(news);
			break;

		case 4: {
			fragment = new ContactFragment();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.frame_container, fragment, "contact");
			fragmentTransaction.commit();
			llHome.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			break;
		}

		case 5: {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry");
			intent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "booking.express.kw@gmail.com" });
			Intent mailer = Intent.createChooser(intent, "Send Mail");
			startActivity(mailer);
			mDrawerList.setItemChecked(position, false);
			if (menu_pos == 0) {
				llHome.setVisibility(View.VISIBLE);
				position = 0;
				mDrawerList.setItemChecked(position, true);
			} else {
				mDrawerList.setItemChecked(position, false);
				position = menu_pos;
				mDrawerList.setItemChecked(position, true);
			}

			break;
		}
		default: {
			if (fragment != null) {
				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
			} else {
				// error in creating fragment
				Log.e("MainActivity", "Error in creating fragment");
				break;
			}
		}
		}
		menu_pos = position;
	}

	public void changeLang(String lang) {
		if (lang.equalsIgnoreCase(""))
			return;
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

		initialize();
		loadAppBar();
		addListItems();
		if (fragment != null && !fragment.getTag().equalsIgnoreCase("home")) {
			llHome.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
			if (fragment.getTag().equalsIgnoreCase("hotel")) {
				fragment = null;
				fragment = new HotelFragment();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(R.id.frame_container, fragment,
						"hotel");
				fragmentTransaction.commit();
			} else if (fragment.getTag().equalsIgnoreCase("flight")) {
				fragment = null;
				fragment = new FlightFragment();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(R.id.frame_container, fragment,
						"flight");
				fragmentTransaction.commit();
			} else
				displayView(0);
		} else
			displayView(0);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (!CommonFunctions.modify) {
			if (fragment != null && !fragment.getTag().contains("home")) {
				// add your code here
				displayView(0);
			}

			else if (doubleBackToExitPressedOnce) {
				finishAffinity();
			} else {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.exit_msg),
						Toast.LENGTH_SHORT).show();
				doubleBackToExitPressedOnce = true;
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						doubleBackToExitPressedOnce = false;
					}
				}, 3000);
			}
		}

	}

	private void dialogCurrency() {
		curr = new Dialog(MainActivity.this, android.R.style.Theme_Translucent);
		curr.requestWindowFeature(Window.FEATURE_NO_TITLE);
		curr.getWindow().setGravity(Gravity.TOP);
		curr.setContentView(R.layout.dialog_currency);
		curr.setCancelable(false);

		final ImageView close;
		final LinearLayout llKWD, llINR, llUSD, llQAR, llEUR, llAED, llSAR, llIQD, llGBP;
		final LinearLayout llGEL, llBHD;
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

				CommonFunctions.strCurrency = "KWD";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "INR";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "USD";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "QAR";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "EUR";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "AED";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "SAR";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "IQD";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "GBP";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "GEL";
				tvCurrency.setText(CommonFunctions.strCurrency);
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

				CommonFunctions.strCurrency = "BHD";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		curr.show();

	}

	/**
	 * Slide menu item click listener
	 */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			// display view for selected nav drawer item
			if (menu_pos != position)
				displayView(position);
		}
	}

}
