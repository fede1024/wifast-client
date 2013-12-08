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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        TextView title = (TextView) this.findViewById(R.id.welcomeTitle);
        
        title.setText("Welcome to " + WiFastApp.shopManager.getShopName());
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
    
    public void menuButtonPressed(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void ordersButtonPressed(View view) {
        System.out.println("orders_button");
    }

    public void profileButtonPressed(View view) {
        System.out.println("profile_button");
    }

    public void cartButtonPressed(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
}