package fr.eurecom.wifast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.wifast.library.Order;
import fr.eurecom.wifast.library.ReloadingActivity;

public class CartActivity extends ReloadingActivity {
	private ListView listView;
	private MenuItemArrayAdapter adapter;
	protected ImageButton payBt;
	protected ProgressBar progBar;
	protected TextView priceTV;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_cart);

        final ActionBar actionBar = this.getActionBar();
        
        payBt = (ImageButton)findViewById(R.id.pay_button);
        priceTV = (TextView)findViewById(R.id.priceTV);
        progBar = (ProgressBar)findViewById(R.id.cartProgressBar);
        
        Callback updatePrices = new newItemCallback();
        updatePrices.handleMessage(null);

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
		this.adapter = new MenuItemArrayAdapter(this, items);
		listView.setAdapter(this.adapter);

		listView.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order, menu); */
        return super.onCreateOptionsMenu(menu);
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    this.finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void payButtonPressed(View view){
    	System.out.println("Finish order menu button");
    	if(WiFastApp.current_order.getItems().isEmpty()){
			Toast.makeText(this, "Your shop list is empty.", Toast.LENGTH_SHORT).show();
			return;
    	}
		payBt.setEnabled(false);
		progBar.setVisibility(View.VISIBLE);
		Toast.makeText(this, "Order finished. Sending to server.", Toast.LENGTH_SHORT).show();
		final SharedPreferences pref = getSharedPreferences("wifast", Context.MODE_PRIVATE);
		String uuid = pref.getString(WiFastApp.PROPERTY_UUID, "");

		Callback c = new orderSentCallback();
		WiFastApp.current_order.sendToServer(this, uuid, c);
    }
    
    public void refresh(String result) {
		WiFastApp.current_order = new Order();
		payBt.setEnabled(true);
    }

	private class newItemCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			Double cost = WiFastApp.current_order.getTotalCost();

			priceTV.setText(new DecimalFormat("0.00 €").format(cost));
			return false;
		}
	}

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
			Toast.makeText(getApplicationContext(), "Order: " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
	        Intent intent = new Intent(getApplicationContext(), CashRegister.class);
	        startActivity(intent);
			finish();
			return false;
		}
	}
}
