package fr.eurecom.wifast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShopListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_list);
		
		final ListView listview = (ListView) findViewById(R.id.shopListView);

		final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		for (int i = 0; i < WiFastApp.shops.length(); ++i) {
			try {
				JSONObject item = (JSONObject)WiFastApp.shops.get(i);
				//double distance = item.getDouble("dist");
				HashMap<String, String> map = new HashMap<String, String>();
				String completeName = item.getString("name");
				String[] parts = completeName.split(" - ");
				
	            map.put("rowid", "" + i);
	            map.put("name", parts[0]);
	            map.put("pos", parts[1]);
	            map.put("completeName", completeName);
	            list.add(map);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ListAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
				new String[] { "name", "pos" },
				new int[] { android.R.id.text1 , android.R.id.text2 });
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
				WiFastApp.shopManager.setShopName(item.get("completeName"));
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_list, menu);
		return true;
	}
}
