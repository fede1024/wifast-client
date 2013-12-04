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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import fr.eurecom.wifast.library.ShopListManager;
import fr.eurecom.wifast.library.JSONDownload;

public class MainActivity extends Activity {
	public static Properties prop;
	public static JSONArray types, shops;
	public static HashMap<String, JSONObject> menu_map;
	
	private ShopListManager shopManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        prop = new Properties();
		try {
			Resources resources = this.getResources();
			InputStream rawResource = resources.openRawResource(R.raw.wifast_properties);
			prop.load(rawResource);
			rawResource.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    prop.list(System.out);

	    MainActivity.this.locationFound(true);		// Start menu download

        shopManager = new ShopListManager(this);	// Start getting shop list
    }
    
    public void onResume(){
    	super.onResume();
        if(shopManager.checkLocationEnabled() == false){
		    Intent intent = new Intent(this, EnableSettingsDialog.class);
		    this.startActivity(intent);
        }
        else{
		    /* Get the nearest shops depending on the GPS position */
	    	if (shopManager.locationIsValid() == false){ // ADD: or shop list not downloaded
				shopManager.updateLocation();
			    JSONShopsCallback c = new JSONShopsCallback();    
		        shopManager.getJSONShops(c);
	    	}
    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void buttonPressed(View view) {
        switch (view.getId()) {
            case R.id.menus_menu_button:
                System.out.println("menu_button");
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.orders_menu_button:
                System.out.println("orders_button");
                break;
            case R.id.profile_menu_button:
                System.out.println("profile_button");
                break;
            case R.id.cart_menu_button:
                System.out.println("drinks_button");
                break;
            default:
                System.out.println("default");
        }
    }
    
    public void locationFound(boolean found) {
    	if (found) {
    		if (MainActivity.menu_map == null) {
//    			ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
//    			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    			String stringUrl = MainActivity.prop.getProperty("get_menu_url");
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
			MainActivity.shops  = (JSONArray) msg.obj;
			try {
				int shops_len = MainActivity.shops.length();
				
				if(shops_len > 1) {
					ShopsListDialog dialog = ShopsListDialog.newInstance(R.string.title_activity_shopslist_dialog);
					
					for(int i=0; i<shops_len; i++)
						dialog.addShop(MainActivity.shops.get(i).toString());
					
					// FIXME if the activity is closed it crashes IllegalSTateException: activity has been destroyed
					dialog.show(getFragmentManager(), "dialog"); 
					
					/*String currentShop = dialog.getCorrectShop();
					System.out.println(currentShop);*/
				    MainActivity.this.locationFound(true);
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
    		JSONObject obj = (JSONObject)msg.obj;
			try {
				MainActivity.types = obj.getJSONArray("types");
				MainActivity.menu_map = new HashMap<String, JSONObject>();
				int items_len;
				
				JSONArray items = obj.getJSONArray("items");
				items_len = items.length();
				for (int i=0; i<items_len; i++) {
					JSONObject tmp = items.getJSONObject(i);
					MainActivity.menu_map.put(tmp.getString("name"), tmp);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			MainActivity.this.gotMenu();
    		return true;
		}
	}
}