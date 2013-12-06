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

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import fr.eurecom.wifast.library.JSONDownload;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //this.appState = ((WiFastApp)getApplicationContext());
        
        setContentView(R.layout.activity_main);

	    locationFound(true);			// Start menu download FIXME strange name
    }
    
    public void onResume(){
    	super.onResume();
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