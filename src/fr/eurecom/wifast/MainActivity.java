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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import fr.eurecom.wifast.library.JSONDownload;

public class MainActivity extends Activity {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String server_url; 
    
    protected TextView pointsTV;

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
        
        WiFastApp app = (WiFastApp) getApplicationContext();
        if(app.doCheckinIfNeeded()){
        	this.finish();
        	return;
        }
        
        setContentView(R.layout.activity_main);
        
        TextView title = (TextView) this.findViewById(R.id.welcomeTitle);
		this.pointsTV = (TextView)findViewById(R.id.points);
		TextView d_button1 = (TextView)findViewById(R.id.d_button1);
		TextView d_button2 = (TextView)findViewById(R.id.d_button2);
		TextView d_button3 = (TextView)findViewById(R.id.d_button3);
		TextView d_button4 = (TextView)findViewById(R.id.d_button4);
		
		Typeface typface=Typeface.createFromAsset(getAssets(),"fonts/OleoScriptSwashCaps-Regular.ttf");
        title.setTypeface(typface);
        pointsTV.setTypeface(typface);
		d_button1.setTypeface(typface);
		d_button2.setTypeface(typface);
		d_button3.setTypeface(typface);
		d_button4.setTypeface(typface);
        title.setText(WiFastApp.shopManager.getShopName());
        pointsTV.setText("Points: loading");
        
        server_url = WiFastApp.getProperty("server_url");
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
    
	@Override
    public void onResume(){
		super.onResume();

        pointsTV.setText("Points: loading");

		String serverURL = WiFastApp.getProperty("server_url")+"/api/getPoints";

    	final SharedPreferences prefs = getSharedPreferences("wifast", Context.MODE_PRIVATE);
        String uuid = prefs.getString(WiFastApp.PROPERTY_UUID, "");
        Log.d("DEBUG", "UUID: " + uuid);
	        
		try {
	        serverURL += "?shop=" + URLEncoder.encode(WiFastApp.shopManager.getShopName(), "UTF-8");

			if(uuid != null)
				serverURL += "&uuid=" + URLEncoder.encode(uuid, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PointsCallback c = new PointsCallback();
		new JSONDownload(c).execute("GET", "JSONObject", serverURL);
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
        return getSharedPreferences("wifast", Context.MODE_PRIVATE);
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
//                    storeRegistrationId(context, regid);
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
    	final SharedPreferences prefs = getGCMPreferences(context);
        String uuid = prefs.getString(WiFastApp.PROPERTY_UUID, "");
        if (uuid.isEmpty()) {
        	HTTPRequest r = new HTTPRequest(new GetIdCallback(), "GET", this.server_url+"/api/getId", null);
    		r.execute();
        } else {
        	String data = "id="+uuid+"&token="+this.regid;
			HTTPRequest r = new HTTPRequest(new SendTokenCallback(), "POST", this.server_url+"/api/setToken", data);
			r.execute();
        }
    	
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
			if(msg.obj != null) {
				try {
					JSONObject o = new JSONObject((String)msg.obj);
					if (o.getInt("code") == 0 && regid != null) {
						storeRegistrationId(context, regid);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
    		return false;
		}
	}
    
    private class GetIdCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			
			if(msg.obj == null || msg.obj.equals(""))
				return false;
			String id = (String)msg.obj;
			Log.d("ID", "get id: "+id);
			final SharedPreferences prefs = getGCMPreferences(context);
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putString(WiFastApp.PROPERTY_UUID, id);
	        editor.commit();
	        
			String data = "id="+id+"&token="+MainActivity.this.regid;
			HTTPRequest r = new HTTPRequest(new SendTokenCallback(), "POST", MainActivity.this.server_url+"/api/setToken", data);
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

    private class PointsCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			Integer p = 0;
			
			if(msg.obj == null)
				Log.d("ERROR", "Error getting points.");
			else {
				JSONObject resp = (JSONObject)msg.obj;
				
				try {
					p = resp.getInt("points");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (WiFastApp.points != p)
				Log.d("DEBUG", "Points update " + WiFastApp.points + " => " + p);

			WiFastApp.points = p;
			pointsTV.setText("Points: " + p);

    		return true;
		}
	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	WiFastApp app;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.change_shop:
				Intent i = getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(getBaseContext().getPackageName());
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        app = (WiFastApp) getApplicationContext();
			        app.setToCheckinAgain();
			        finish();
				startActivity(i);
				return true;
            case R.id.exit_app:
            	Intent intent = new Intent(Intent.ACTION_MAIN);
            	intent.addCategory(Intent.CATEGORY_HOME);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        app = (WiFastApp) getApplicationContext();
		        app.setToCheckinAgain();
		        finish();
            	startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void menuButtonPressed(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void favoritesButtonPressed(View view) {
        Intent intent = new Intent(this, Favorites.class);
        startActivity(intent);
    }
    
    public void promotionButtonPressed(View view) {
    	Intent intent = new Intent(this, PromotionActivity.class);
        startActivity(intent);
    }

    public void cartButtonPressed(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
}