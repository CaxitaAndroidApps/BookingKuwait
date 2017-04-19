package com.travel.booking_kuwait;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;

import com.travel.booking_kuwait.Support.CommonFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 2000;
	CommonFunctions cd;
	HttpsURLConnection urlConnection = null;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		cd = new CommonFunctions(SplashScreen.this);
		if (cd.isConnectingToInternet())
			new UpdateChecker().execute();
		else
			noInternetAlert();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if (cd.isConnectingToInternet())
			new UpdateChecker().execute();
		else
			noInternetAlert();
		super.onRestart();
	}

	public void splash() {
		if (cd.isConnectingToInternet()) {
			new Handler().postDelayed(new Runnable() {

				/*
				 * Showing splash screen with a timer. This will be useful when
				 * you want to show case your app logo / company
				 */

				@Override
				public void run() {
					// This method will be executed once the timer is over
					// Start your app main activity

					Intent i = new Intent(SplashScreen.this, MainActivity.class);
					startActivity(i);
					// close this activity
					finish();
				}
			}, SPLASH_TIME_OUT);
		} else {
			noInternetAlert();
		}
	}

	public void noInternetAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle(getResources().getString(
				R.string.error_no_internet_title));

		// Setting Dialog Message
		alertDialog.setMessage(getResources().getString(
				R.string.error_no_internet_msg));

		// Setting OK Button
		alertDialog.setPositiveButton(
				getResources().getString(R.string.error_no_internet_settings),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// intent to move mobile settings
						startActivity(new Intent(
								Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		alertDialog.setNegativeButton(
				getResources().getString(R.string.error_no_internet_close_app),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						finish();
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						// System.exit(0);
					}
				});

		// Showing Alert Message
		alertDialog.setCancelable(false);
		alertDialog.show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	private void makePostRequest() {
		// making POST request.
		try {
			URL url = new URL("https://bookingkuwait.com/version.json");
			CookieManager cookieManager = CookieManager.getInstance();
			String cookie = cookieManager.getCookie(url.toString());
			Log.i("url", url.toString());
			urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Cookie", cookie);
			urlConnection.setConnectTimeout(15000);
			urlConnection.setRequestMethod("GET");
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			String resultString = convertStreamToString(in);
			urlConnection.disconnect();
			parseResult(resultString);
		} catch (SocketTimeoutException e) {
			// Log exception
			e.printStackTrace();
			urlConnection.disconnect();
		} catch (NullPointerException e) {
			// Log exception
			e.printStackTrace();
			urlConnection.disconnect();
		} catch (IOException e) {
			// Log exception
			e.printStackTrace();
			urlConnection.disconnect();
		}
	}

	private void parseResult(String result) {
		// Parse String to JSON object
		try {
			JSONObject json = new JSONObject(result);
			int status = json.getInt("version_android");

			int versionName = 0;
			try {
				versionName = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (status > versionName) {
				CommonFunctions.updateApp = true;
			} else {
				CommonFunctions.updateApp = false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class UpdateChecker extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			makePostRequest();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			splash();
			super.onPostExecute(result);
		}

	}

}
