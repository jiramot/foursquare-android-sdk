/*
 * Copyright 2010 Small Light Room CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiramot.foursquare.android;

/**
 * Encapsulation of Foursquare.
 *
 * @author jiramot@gmail.com
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

public class Foursquare {

	private static final String LOGIN = "oauth";
	public static final String API_END_POING_BASE_URL = "https://api.foursquare.com/v2/";
	public static String REDIRECT_URI;
	public static final String API_URL = "https://foursquare.com/oauth2/";
	//public static final String CANCEL_URI = "";
	public static final String TOKEN = "access_token";
	public static final String EXPIRES = "expires_in";
	public static final String SINGLE_SIGN_ON_DISABLED = "service_disabled";
	public static String AUTHENTICATE_URL = "https://foursquare.com/oauth2/authenticate";// +

	private String mClientId;
	private String mClientSecret;
	private String mAccessToken = null;

	private DialogListener mAuthDialogListener;
	
	public Foursquare(String clientId, String clientSecret, String redirectUrl) {
		if (clientId == null || clientSecret == null) {
			throw new IllegalArgumentException(
					"You must specify your application ID when instantiating "
							+ "a Foursquare object. See README for details.");
		}
		mClientId = clientId;
		mClientSecret = clientSecret;
		REDIRECT_URI = redirectUrl;
	}

	public void authorize(Activity activity, final DialogListener listener) {
		mAuthDialogListener = listener;
		startDialogAuth(activity);
	}

	private void startDialogAuth(Activity activity) {
		CookieSyncManager.createInstance(activity);
		Bundle params = new Bundle();
		dialog(activity, LOGIN, params, new DialogListener() {

			public void onComplete(Bundle values) {
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				String _token = values.getString(TOKEN);
				setAccessToken(_token);
				// setAccessExpiresIn(values.getString(EXPIRES));
				if (isSessionValid()) {
					Log.d("Foursquare-authorize",
							"Login Success! access_token=" + getAccessToken());
					mAuthDialogListener.onComplete(values);
				} else {
					mAuthDialogListener.onFoursquareError(new FoursquareError(
							"Failed to receive access token."));
				}
			}

			public void onError(DialogError error) {
				Log.d("Foursquare-authorize", "Login failed: " + error);
				mAuthDialogListener.onError(error);
			}

			public void onFoursquareError(FoursquareError error) {
				Log.d("Foursquare-authorize", "Login failed: " + error);
				mAuthDialogListener.onFoursquareError(error);
			}

			public void onCancel() {
				Log.d("Foursquare-authorize", "Login canceled");
				mAuthDialogListener.onCancel();
			}
		});
	}

	public void dialog(Context context, String action, Bundle parameters,
			final DialogListener listener) {

		String endpoint = "";

		parameters.putString("client_id", mClientId);
		parameters.putString("display", "touch");
		if (action.equals(LOGIN)) {
			endpoint = AUTHENTICATE_URL;
			parameters.putString("client_secret", mClientSecret);
			parameters.putString("response_type", "token");
			parameters.putString("redirect_uri", REDIRECT_URI);
		}

//		if (isSessionValid()) {
//			parameters.putString(TOKEN, getAccessToken());
//		}
		String url = endpoint + "?" + Util.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Util.showAlert(context, "Error",
					"Application requires permission to access the Internet");
		} else {
			new FoursquareDialog(context, url, listener).show();
		}
	}

	public boolean isSessionValid() {
		if (getAccessToken() != null) {
			return true;
		}
		return false;
	}

	public void setAccessToken(String token) {
		mAccessToken = token;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public String request(String graphPath) throws MalformedURLException,
			IOException {
		return request(graphPath, new Bundle(), "GET");
	}

	public String request(String graphPath, Bundle parameters)
			throws MalformedURLException, IOException {
		return request(graphPath, parameters, "GET");
	}

	public String request(String graphPath, Bundle params, String httpMethod)
			throws FileNotFoundException, MalformedURLException, IOException {
		params.putString("format", "json");
		if (isSessionValid()) {
			params.putString("oauth_token", getAccessToken());
		}
		String url = API_END_POING_BASE_URL + graphPath;
		return Util.openUrl(url, httpMethod, params);
	}

	public static interface DialogListener {

		/**
		 * Called when a dialog completes.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 * @param values
		 *            Key-value string pairs extracted from the response.
		 */
		public void onComplete(Bundle values);

		/**
		 * Called when a Foursquare responds to a dialog with an error.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onFoursquareError(FoursquareError e);

		/**
		 * Called when a dialog has an error.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onError(DialogError e);

		/**
		 * Called when a dialog is canceled by the user.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onCancel();

	}
}
