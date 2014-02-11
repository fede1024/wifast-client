package fr.eurecom.wifast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.wifast.library.JSONDownload;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SplashScreen extends Activity {
	private static TextView currentAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash_screen);
		
		currentAction = (TextView)findViewById(R.id.current_action);
		TextView wifast = (TextView)findViewById(R.id.wifast_splash);
		
		Typeface typface=Typeface.createFromAsset(getAssets(),"fonts/OleoScriptSwashCaps-Regular.ttf");
		currentAction.setTypeface(typface);
		wifast.setTypeface(typface);

		// Be sure to reinitialize WiFastApp static members
		WiFastApp status = (WiFastApp)getApplicationContext();
		status.onCreate();

		//final Handler handler = new Handler();
		//handler.postDelayed(new StartMainActivity(this), 5000);
	}

    public void onResume(){
    	super.onResume();
    	continueCheckin();
    }
    
    public void continueCheckin(){
    	WiFastApp app = (WiFastApp)getApplicationContext();
    	
        if(app.checkLocationEnabled() == false){
		    Intent intent = new Intent(this, EnableSettingsDialog.class);
		    intent.putExtra(EnableSettingsDialog.MISSING_ARG_NAME, EnableSettingsDialog.MISSING_LOCATION);
		    this.startActivity(intent);
        }
        else if(app.checkNetworkEnabled() == false){
		    Intent intent = new Intent(this, EnableSettingsDialog.class);
		    intent.putExtra(EnableSettingsDialog.MISSING_ARG_NAME, EnableSettingsDialog.MISSING_NETWORK);
		    this.startActivity(intent);
        }
        else{
		    /* Get the nearest shops depending on the GPS position */
	    	if(WiFastApp.shopManager.getShopName() == null){
				currentAction.setText("Getting position");
		    	if (WiFastApp.shopManager.locationIsValid() == false)
					WiFastApp.shopManager.updateLocation();
			    JSONShopsCallback c = new JSONShopsCallback();    
		        WiFastApp.shopManager.getJSONShops(c);
	        }
	        else if(WiFastApp.menu_map == null){
				currentAction.setText("Downloading menu");
	        	downloadMenu();
	        }
	        else {
				currentAction.setText("Done");
				WiFastApp.checkedIn = true;
			    Intent intent = new Intent(this, MainActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			    finish();
	        }
    	}
    }
    
    private class JSONShopsCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			
			if(msg.obj == null){ 
				Log.d("ERROR", "Shop list not downloaded.");
				currentAction.setText("Error getting shop list");
				return false;
			}
			
			WiFastApp.shops  = (JSONArray) msg.obj;
			
			if(WiFastApp.shops.length() > 1) {
	        	Context c = getApplicationContext();
			    Intent intent = new Intent(c, ShopListActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    c.startActivity(intent);
			}
			else {
				String shopName = null;
				try {
					shopName = WiFastApp.shops.get(0).toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				WiFastApp.shopManager.setShopName(shopName);
				continueCheckin();
			}

    		return true;
		}
	}

    public void downloadMenu() {
		String serverURL = WiFastApp.getProperty("server_url")+"/api/getMenu";
		Callback c = new JSONMenuDownloadedCallback();

		try {
	    	final SharedPreferences prefs = getSharedPreferences("wifast", Context.MODE_PRIVATE);
	        String uuid = prefs.getString(WiFastApp.PROPERTY_UUID, "");
	        Log.d("DEBUG", "UUID: " + uuid);
	        
	        serverURL += "?shop=" + URLEncoder.encode(WiFastApp.shopManager.getShopName(), "UTF-8");

			if(uuid != null)
				serverURL += "&uuid=" + URLEncoder.encode(uuid, "UTF-8");

			new JSONDownload(c).execute("GET", "JSONObject", serverURL);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private class JSONMenuDownloadedCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.obj == null){
				Toast.makeText(getApplicationContext(), "Error downloading menu - retry.", Toast.LENGTH_SHORT).show();
				currentAction.setText("Menu download error.");
				Log.d("ERROR", "Download menu error.");
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.d("ERROR", "Download menu error - retry.");
						continueCheckin();
					}
				}, 5000);
				return false;
			}
			
    		JSONObject obj = (JSONObject)msg.obj;
			try {
				WiFastApp.types = obj.getJSONArray("types");
				WiFastApp.points = obj.getInt("points");
				WiFastApp.menu_map = new HashMap<String, JSONObject>();
				WiFastApp.promotions = obj.getJSONArray("promotions");
				Log.d("FOTTITI", WiFastApp.promotions.toString());
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
			
			continueCheckin();
			
    		return true;
		}
	}
}
