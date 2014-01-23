package fr.eurecom.wifast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
				if (item.getInt("close") == 1) {
					HashMap<String, String> map = new HashMap<String, String>();
					String completeName = item.getString("name");
					String[] parts = completeName.split(" - ");
					
		            map.put("rowid", "" + i);
		            map.put("name", parts[0]);
		            map.put("pos", parts[1]);
		            map.put("completeName", completeName);
		            list.add(map);
				}
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
    protected void onResume() {
		super.onResume();
    	if (WiFastApp.shopManager.getShopName() != null) {
    		finish();
    	}
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_list, menu);
		return true;
	}
}
