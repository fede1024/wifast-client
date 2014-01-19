package fr.eurecom.wifast;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class Favorites extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);

        TextView title = (TextView) this.findViewById(R.id.textView1);
		Typeface typface=Typeface.createFromAsset(getAssets(),"fonts/OleoScriptSwashCaps-Regular.ttf");
        title.setTypeface(typface);

    	final SharedPreferences prefs = getSharedPreferences("wifast", Context.MODE_PRIVATE);
        String favorites[] = prefs.getString("FAVORITES:"+WiFastApp.shopManager.getShopBrand(), "").split(";");

        // Set up the ViewPager, attaching the adapter.
        // Transform a JSONArray into a ArrayList<JSONObject>
		ArrayList<JSONObject> items = new ArrayList<JSONObject>();
		for (String name : favorites){
			if (name.isEmpty()) continue;
			JSONObject item = WiFastApp.menu_map.get(name);
			if (item != null)
				items.add(item);
		}
        
        ListView listView = (ListView) this.findViewById(R.id.favorites);
		MenuItemArrayAdapter adapter = new MenuItemArrayAdapter(this, items, false);
		adapter.setBuyButtonDisabled();
		listView.setAdapter(adapter);

		listView.setTextFilterEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorites, menu);
		return true;
	}

}
