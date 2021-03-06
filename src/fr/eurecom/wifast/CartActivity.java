package fr.eurecom.wifast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CartActivity extends Activity {
	private ListView listView;
	private MenuItemArrayAdapter adapter;
	protected ImageButton payBt;
	protected ProgressBar progBar;
	protected TextView priceTV;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WiFastApp app = (WiFastApp) getApplicationContext();
        if(app.doCheckinIfNeeded()){
        	this.finish();
        	return;
        }

        this.setContentView(R.layout.activity_cart);

        final ActionBar actionBar = this.getActionBar();
        
        payBt = (ImageButton)findViewById(R.id.pay_button);
        priceTV = (TextView)findViewById(R.id.priceTV);
        progBar = (ProgressBar)findViewById(R.id.cartProgressBar);
        
        Callback updatePrices = new newItemCallback(priceTV);
        updatePrices.handleMessage(null);
        
        MenuItemArrayAdapter.removeItemCallback = updatePrices;

        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager, attaching the adapter.
        // Transform a JSONArray into a ArrayList<JSONObject>
		ArrayList<JSONObject> items = new ArrayList<JSONObject>();
        try {
			JSONArray tmpArray = WiFastApp.current_order.getOrderArray();
			for (int i = 0; i < tmpArray.length(); i++)
				items.add((JSONObject)tmpArray.get(i));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Sort items by name
        Collections.sort(items, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                try {
					return j1.getString("name").compareToIgnoreCase(j2.getString("name"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return 0;
            }
        });
        
        listView = (ListView) this.findViewById(R.id.listview);
		this.adapter = new MenuItemArrayAdapter(this, items, true);
		listView.setAdapter(this.adapter);

		listView.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void payButtonPressed(View view){
    	System.out.println("Finish order menu button");
    	addToFavorites();
    	if(WiFastApp.current_order.getItems().isEmpty()){
			Toast.makeText(this, "Your shop list is empty.", Toast.LENGTH_SHORT).show();
			return;
    	}
//		payBt.setEnabled(false);
//		progBar.setVisibility(View.VISIBLE);
//		Toast.makeText(this, "Order finished. Sending to server.", Toast.LENGTH_SHORT).show();
//		final SharedPreferences pref = getSharedPreferences("wifast", Context.MODE_PRIVATE);
//		String uuid = pref.getString(WiFastApp.PROPERTY_UUID, "");
//
//		Callback c = new orderSentCallback();
//		WiFastApp.current_order.sendToServer(uuid, c);
		Intent intent = new Intent(this, PaymentActivity.class);
		startActivity(intent);
		finish();
    }
    
    private class newItemCallback implements Callback {
		TextView priceTV;
		
		public newItemCallback(TextView priceTV){
			this.priceTV = priceTV;
		}
		
		@Override
		public boolean handleMessage(Message msg) {
			Double cost = WiFastApp.current_order.getTotalCost();
			Log.d("kjkjkjk", cost.toString());
			if (WiFastApp.promotion_id != -1) {
				try {
    				JSONObject promo = WiFastApp.promotions.getJSONObject(WiFastApp.promotion_id);
    				int euro = promo.getInt("euro");
    				cost -= euro;
    				if (cost < 0) cost = 0.0; //You mustn't have a negative total!
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
			}
			Log.d("kjkjkjk", cost.toString());
				
			priceTV.setText(new DecimalFormat("0.00 €").format(cost));
			return false;
		}
	}

    /*
	private class orderSentCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			payBt.setEnabled(true);
			if(msg == null || msg.obj == null){
				Toast.makeText(getApplicationContext(), "Order error, please try again.", Toast.LENGTH_SHORT).show();
				payBt.setEnabled(true);
				progBar.setVisibility(View.INVISIBLE);
				return true;
			}

            Intent intent = new Intent(getApplicationContext(), CashRegister.class);
            JSONObject obj = (JSONObject)msg.obj;
            try {
                String time = obj.getString("expected_time");
                intent.putExtra("EXP_TIME", time);
            } catch (JSONException e) {
                Log.e("ORDER", "Error decoding expected time");
            }
            
            if (WiFastApp.promotion_id != -1) {
            	try {
    				JSONObject promo = WiFastApp.promotions.getJSONObject(WiFastApp.promotion_id);
    				int points = promo.getInt("points");
    				
    				WiFastApp.points -= points;
                	if (WiFastApp.points < 0) WiFastApp.points = 0;
                	
                	WiFastApp.promotion_id = -1;    				
    			} catch (JSONException e) { 
    				e.printStackTrace();
    			}
            	
            }

	        startActivity(intent);
			finish();
			return false;
		}
	} */
	
	private void addToFavorites(){
    	final SharedPreferences prefs = getSharedPreferences("wifast", Context.MODE_PRIVATE);
        String favorites[] = prefs.getString("FAVORITES:"+WiFastApp.shopManager.getShopBrand(), "").split(";");
        HashSet<String> favSet = new LinkedHashSet<String>();
        String favString = "";
        int count = 0, i = 0;
        
		Enumeration<String> en = WiFastApp.current_order.items.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			favSet.add(key);
			count++;
		}
		
		for(i = 0; i < favorites.length && count < 20; i++)
			if (!favSet.contains(favorites[i])){
				favSet.add(favorites[i]);
				count++;
			}
		
		for(String f : favSet)
			favString += ";" + f;
		
        //Log.d("BOH", "FAV:"+favString);
        
        // Writing data to SharedPreferences
        Editor editor = prefs.edit();
        editor.putString("FAVORITES:"+WiFastApp.shopManager.getShopBrand(), favString);
        editor.commit();
	}
	
	public void promotionsButton(View view){
		Intent intent = new Intent(this, PromotionActivityDialog.class);
		startActivity(intent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (WiFastApp.promotion_id != -1 && WiFastApp.promotions != null) {
			try {
				JSONObject promo = WiFastApp.promotions.getJSONObject(WiFastApp.promotion_id);
				int points = promo.getInt("points");
				int euro = promo.getInt("euro");
				
				((LinearLayout) findViewById(R.id.promotion_layout)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.promotion_text)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.promotion_points)).setText(points+" points: ");
				((TextView) findViewById(R.id.promotion_euro)).setText("-"+euro+"€");
			} catch (JSONException e) {
				e.printStackTrace();
				((LinearLayout) findViewById(R.id.promotion_layout)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.promotion_text)).setVisibility(View.VISIBLE);
			}
			
		} else {
			((LinearLayout) findViewById(R.id.promotion_layout)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.promotion_text)).setVisibility(View.VISIBLE);
		}
		Callback updatePrices = new newItemCallback(priceTV);
        updatePrices.handleMessage(null);
	}
}
