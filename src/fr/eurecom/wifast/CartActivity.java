package fr.eurecom.wifast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.eurecom.wifast.library.Order;

public class CartActivity extends FragmentActivity {
	private ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_cart);

        // Create an adapter that when requested, will return a fragment representing an object in the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use getSupportFragmentManager.

        // Set up action bar.
        final ActionBar actionBar = this.getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
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
		ListAdapter adapter = new MenuItemArrayAdapter(this, items);
		listView.setAdapter(adapter);

		listView.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order, menu);
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
            case R.id.finish_order_menu_button:
            	System.out.println("Finish order menu button");
        		Toast.makeText(this, "Order finished. Sending to server.", Toast.LENGTH_SHORT).show();
        		item.setEnabled(false);
        		WiFastApp.current_order.sendToServer();
        		final Handler handler = new Handler();
        		handler.postDelayed(new Runnable() {
        			@Override
        		  	public void run() {
        				finish();
              			startActivity(getIntent());
        			}
        		}, 1500);
        		
        }
        return super.onOptionsItemSelected(item);
    }
}
