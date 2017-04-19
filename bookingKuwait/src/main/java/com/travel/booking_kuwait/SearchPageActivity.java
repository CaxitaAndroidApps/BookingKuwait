package com.travel.booking_kuwait;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.travel.booking_kuwait.Support.CommonFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class SearchPageActivity extends Activity {

	ProgressBar pb_line;
	String url, type, id, sessionID, urlLoading;
	int dp1;
	WebView wv1;
	ImageView menuBtn, ivBack;
	Dialog loaderDialog;
	CommonFunctions cf;
	private Locale myLocale;

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

	@SuppressLint({ "SetJavaScriptEnabled", "InflateParams" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadLocale();
		setContentView(R.layout.activity_web);
		cf = new CommonFunctions(this);
		sessionID = getIntent().getExtras().getString("sessionID", "Empty");
		url = getIntent().getExtras().getString("url", "Empty");
		type = getIntent().getExtras().getString("type", "Empty");
		id = getIntent().getExtras().getString("tripId", "Empty");

		// ============== Define a Custom Header for Navigation
		// drawer=================//
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header_1, null);

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

		loaderDialog = new Dialog(SearchPageActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		// loaderDialog.setCancelable(false);

		wv1 = (WebView) findViewById(R.id.wvContact);
		menuBtn = (ImageView) v.findViewById(R.id.iv_home);
		ivBack = (ImageView) findViewById(R.id.iv_back);

		menuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!urlLoading.contains("/Shared/ProgressPayment")) {
					if (type.equalsIgnoreCase("flight"))
						FlightResultActivity.activity.finish();
					else if (type.equalsIgnoreCase("hotel"))
						HotelResultActivity.activity.finish();
					finish();
					Intent home = new Intent(SearchPageActivity.this, MainActivity.class);
					home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(home);

				}
			}
		});

		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!urlLoading.contains("/Shared/ProgressPayment")) {
					if (urlLoading.contains("/Flight/ShowTicket") ||
							urlLoading.contains("/Hotel/Voucher")) {
						if (type.equalsIgnoreCase("flight"))
							FlightResultActivity.activity.finish();
						else if(type.equalsIgnoreCase("hotel"))
							HotelResultActivity.activity.finish();
						finish();
						Intent home = new Intent(SearchPageActivity.this, MainActivity.class);
						home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(home);
					} else if (wv1.canGoBack()) {
						wv1.goBack();
					} else {
						finish();
					}
				}
			}
		});

		pb_line = (ProgressBar) findViewById(R.id.pb_line);
		wv1.getSettings().setRenderPriority(RenderPriority.HIGH);
		wv1.getSettings().setJavaScriptEnabled(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}

		wv1.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				Log.d("Started", "Loading");
				System.out.println("Loading url" + url);
				urlLoading = url;
				if (url.contains(CommonFunctions.main_url)
						|| url.contains(CommonFunctions.main_url + CommonFunctions.lang)
						|| url.contains("http://bookingkuwait.com/")) {
					if(url.contains("https")) {
						url = url.replace("https", "http");
						wv1.loadUrl(url);
					}
				}


				if (url.equalsIgnoreCase(CommonFunctions.main_url)
						|| url.equalsIgnoreCase(CommonFunctions.main_url + CommonFunctions.lang)
						|| url.equalsIgnoreCase("http://bookingkuwait.com/")) {
					if (type.equalsIgnoreCase("flight"))
						cf.showToast(getResources().getString(
								R.string.flight_expired_web));
					else if (type.equalsIgnoreCase("hotel"))
						cf.showToast(getResources().getString(
								R.string.hotel_expired_web));
					finish();
				} else if (url.contains("Flight/GetBackResults")
							|| url.contains("Hotel/GetBackToHotelResultURL")
							|| url.contains("FlightApi/BackToResult")) {
						finish();
				} else {
					loaderDialog.show();
					wv1.setVisibility(View.INVISIBLE);
				}

				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub

				// Inject javascript code to the url given
				// Not display the element
				wv1.setVisibility(View.VISIBLE);
				pb_line.setVisibility(View.GONE);
				Log.d("Finished", "Loading" + url);

				super.onPageFinished(view, url);

			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);

				if (wv1.getProgress() > 80) {
					pb_line.setVisibility(View.GONE);
					wv1.setVisibility(View.VISIBLE);
					if (loaderDialog.isShowing())
						loaderDialog.dismiss();
				} else {
					pb_line.setVisibility(View.VISIBLE);
				}
				pb_line.setProgress(wv1.getProgress());
			}
		});

		if(url.contains("https"))
			url = url.replace("https", "http");

		wv1.loadUrl(url);

		if (type.equalsIgnoreCase("flight") || type.equalsIgnoreCase("hotel"))
			new backService().execute();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if (type.equalsIgnoreCase("flight") || type.equalsIgnoreCase("hotel"))
			new backService().execute();
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (loaderDialog.isShowing())
			loaderDialog.dismiss();
		wv1.stopLoading();
		wv1.clearCache(true);
		wv1.clearFormData();
		wv1.destroy();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!urlLoading.contains("/Shared/ProgressPayment")) {
			if (urlLoading.contains("/Flight/ShowTicket")) {
				if (type.equalsIgnoreCase("flight"))
					FlightResultActivity.activity.finish();
				else if (type.equalsIgnoreCase("hotel"))
					HotelResultActivity.activity.finish();
				wv1.stopLoading();
				finish();
				Intent home = new Intent(SearchPageActivity.this, MainActivity.class);
				home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(home);
			} else if (wv1.canGoBack()) {
				wv1.goBack();
			} else {
				wv1.stopLoading();
				finish();
			}
		}
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

	private class backService extends AsyncTask<Void, Void, String> {

		String sessionResult;

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			URL url = null;
			HttpURLConnection urlConnection = null;
			try {
				if (type.equalsIgnoreCase("flight"))
					url = new URL(CommonFunctions.main_url
							+ "en/FlightApi/AvailResult?tripId=" + id);
				else
					url = new URL(CommonFunctions.main_url
							+ "en/HotelApi/AvailResult");
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
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (!sessionResult.equalsIgnoreCase("true")) {
				if (type.equalsIgnoreCase("flight")) {
					FlightResultActivity.blSession = false;
					cf.showToast(getResources().getString(
							R.string.flight_expired_web));
				} else {
					HotelResultActivity.blSession = false;
					cf.showToast(getResources().getString(
							R.string.hotel_expired_web));
				}
				finish();
			}

			super.onPostExecute(result);
		}

	}

}
