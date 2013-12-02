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
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import fr.eurecom.wifast.library.JSONDownload;

public class MainActivity extends Activity {
	public static Properties prop;
	public static JSONArray types;
	public static HashMap<String, JSONObject> menu_map;
	
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
	    MainActivity.this.locationFound(true);
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
        
    private class JSONMenuDownloadedCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			JSONObject obj = (JSONObject)msg.obj;
			try {
				MainActivity.types = obj.getJSONArray("types");
				MainActivity.menu_map = new HashMap<String, JSONObject>();
				
				JSONArray items = obj.getJSONArray("items");
				for (int i=0; i<items.length(); i++) {
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