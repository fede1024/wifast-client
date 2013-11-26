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
import java.util.Properties;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import fr.eurecom.wifast.library.JSONDownload;

public class MainActivity extends Activity {
	public static Properties prop;
	public static JSONObject menu_json;
	
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
    		if (MainActivity.menu_json == null) {
//    			ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
//    			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    			String stringUrl = MainActivity.prop.getProperty("get_menu_url");
//    			if (networkInfo != null && networkInfo.isConnected()) {
    			Callback c = new MyCallBack();
    			new JSONDownload(c).execute("GET", "JSONObject", stringUrl);
//    			} else {
//    				textView.setText("No network connection available.");
//    			}
    		} else {
    			this.gotMenu(MainActivity.menu_json);
    		}
    	} else {
    		System.out.println("AAAAAAAAAAAAAAA");
    	}
    }
    
    protected void gotMenu(JSONObject obj) {
    	MainActivity.menu_json = obj;
		Button menu_btn = (Button)findViewById(R.id.menus_menu_button);
		menu_btn.setEnabled(true);
    }
    
    private class MyCallBack implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			MainActivity.this.gotMenu((JSONObject)msg.obj);
    		return true;
		}

	}

}