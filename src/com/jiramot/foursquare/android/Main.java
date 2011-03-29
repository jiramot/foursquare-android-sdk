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
 * Encapsulation of a MainActivity: a demo for using api
 *
 * @author jiramot@gmail.com
 */
import java.io.IOException;
import java.net.MalformedURLException;

import com.jiramot.foursquare.android.Foursquare.DialogListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Main extends Activity {
	Foursquare foursquare;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		foursquare = new Foursquare(
				"YOUR_CLIENT_ID",
				"YOUR_CLIENT_SECRET"
				"REDIRECT_URL");

		foursquare.authorize(this, new FoursquareAuthenDialogListener());

	}

	private class FoursquareAuthenDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			try {
				String aa = null;
				aa = foursquare.request("users/self");
				Log.d("Foursquare-Main", aa);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onFoursquareError(FoursquareError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

	}
}