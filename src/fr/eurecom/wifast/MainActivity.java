/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.eurecom.wifast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import fr.eurecom.wifast.library.JSONDownload;

public class MainActivity extends Activity {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "580048588523";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //this.appState = ((WiFastApp)getApplicationContext());
        
        setContentView(R.layout.activity_main);

	    locationFound(true);			// Start menu download FIXME strange name
	    
	    context = getApplicationContext();
	    if(checkPlayServices()) {
	    	gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
	    }
    }
    
    public void onResume(){
    	super.onResume();
    	checkPlayServices();
        if(WiFastApp.shopManager.checkLocationEnabled() == false){
		    Intent intent = new Intent(this, EnableSettingsDialog.class);
		    this.startActivity(intent);
        }
        else{
		    /* Get the nearest shops depending on the GPS position */
	    	if (WiFastApp.shopManager.locationIsValid() == false){ // ADD: or shop list not downloaded
				WiFastApp.shopManager.updateLocation();
			    JSONShopsCallback c = new JSONShopsCallback();    
		        WiFastApp.shopManager.getJSONShops(c);
	    	}
    	}
    }
    
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Object, Integer, String>() {
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
            
			protected String doInBackground(Object... arg0) {
				String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
			}

        }.execute(null, null, null);
    }
    
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
    	Log.i(TAG, "sendRegistrationIdToBackend");
    	HTTPRequest r = new HTTPRequest(new GetIdCallback(), "GET", "http://192.168.1.76:5000/api/getId", null);
		r.execute();
        // Your implementation here.
    }
    
    public class HTTPRequest extends AsyncTask<String, Void, String> {
		private Callback callback;
		private String url;
		private String data;
		private String method;
		private Object result;

		public HTTPRequest(Callback callback, String method, String url, String data) {
			this.callback = callback;
			this.url = url;
			this.data = data;
			this.method = method;
		}

		@Override
		protected String doInBackground(String... urls) {
			System.out.println("in doInBackground");
			// params comes from the execute() call: params[0] is the url.
			try {
				URL url = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod(this.method);
				conn.setDoInput(true);
				if (this.data != null && this.method.equals("POST")) {
			        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			        osw.write(this.data.toString());
			        osw.flush();
			        osw.close();
				}
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				//process response - need to get xml response back.
				InputStream stream = conn.getInputStream();
				InputStreamReader isReader = new InputStreamReader(stream ); 

				//put output stream into a string
				BufferedReader br = new BufferedReader(isReader );
		        String line;
		        while ((line = br.readLine()) != null) {
		        	this.result = line;
		        }
				System.out.println("response code: "+response);
				return "OK";
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Log.d("HTTP", "onPostExecute");
			Message msg = new Message();
			msg.obj = this.result;
			this.callback.handleMessage(msg);
		}
	}
    
    private class SendTokenCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			Log.d("TOKEN", "handle message");
			if(msg.obj == null)
				return false;
			
    		return true;
		}
	}
    
    private class GetIdCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			
			if(msg.obj == null)
				return false;
			String id = (String)msg.obj;
			Log.d("ID", "get id: "+id);
			WiFastApp.id = id;
			String data = "id="+id+"&token="+MainActivity.this.getRegistrationId(MainActivity.this);
			HTTPRequest r = new HTTPRequest(new SendTokenCallback(), "POST", "http://192.168.1.76:5000/api/setToken", data);
			r.execute();
    		return true;
		}
	}
    
    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_menu_button:
            	System.out.println("Cart menu button");
            	Intent intent = new Intent(this, CartActivity.class);
            	startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void buttonPressed(View view) {
    	Intent intent;
        switch (view.getId()) {
            case R.id.menus_menu_button:
                System.out.println("menu_button");
                intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.orders_menu_button:
                System.out.println("orders_button");
                break;
            case R.id.profile_menu_button:
                System.out.println("profile_button");
                break;
            case R.id.cart_menu_button:
                System.out.println("drinks_button");
                intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                break;
            default:
                System.out.println("default");
        }
    }
    
    public void locationFound(boolean found) {
    	if (found) {
    		if (WiFastApp.menu_map == null) {
//    			ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
//    			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    			String stringUrl = WiFastApp.getProperty("get_menu_url");
//    			if (networkInfo != null && networkInfo.isConnected()) {
    			Callback c = new JSONMenuDownloadedCallback();
    			new JSONDownload(c).execute("GET", "JSONObject", stringUrl);
//    			} else {
//    				textView.setText("No network connection available.");
//    			}
    		} else {
    			this.gotMenu();
    		}
    	} else {
    		System.out.println("AAAAAAAAAAAAAAA");
    	}
    }
    
    protected void gotMenu() {
		Button menu_btn = (Button)findViewById(R.id.menus_menu_button);
		menu_btn.setEnabled(true);
    }
    
    private class JSONShopsCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			
			if(msg.obj == null)
				return false;
			
			WiFastApp.shops  = (JSONArray) msg.obj;
			
			try {
				int shops_len = WiFastApp.shops.length();
				
				if(shops_len > 1) {
					ShopsListDialog dialog = ShopsListDialog.newInstance(R.string.title_activity_shopslist_dialog);
					
					for(int i=0; i<shops_len; i++)
						dialog.addShop(WiFastApp.shops.get(i).toString());
					
					if(getFragmentManager() != null)
						dialog.show(getFragmentManager(), "dialog"); 
					
					/*String currentShop = dialog.getCorrectShop();
					System.out.println(currentShop);*/
				    locationFound(true);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		return true;
		}
	}
        
    private class JSONMenuDownloadedCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.obj == null){
				Toast.makeText(getApplicationContext(), "Error downloading menu.", Toast.LENGTH_LONG).show();
				return false;
			}
			
    		JSONObject obj = (JSONObject)msg.obj;
			try {
				WiFastApp.types = obj.getJSONArray("types");
				WiFastApp.menu_map = new HashMap<String, JSONObject>();
				int items_len;
				
				JSONArray items = obj.getJSONArray("items");
				items_len = items.length();
				for (int i=0; i<items_len; i++) {
					JSONObject tmp = items.getJSONObject(i);
					WiFastApp.menu_map.put(tmp.getString("name"), tmp);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			gotMenu();
    		return true;
		}
	}
}