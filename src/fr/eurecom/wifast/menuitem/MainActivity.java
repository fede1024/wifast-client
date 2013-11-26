package fr.eurecom.wifast.menuitem;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.eurecom.wifast.library.network.JSONDownload;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private TextView textView;
	private JSONArray contactsList;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		
		listView = (ListView) findViewById(R.id.listview);
		  
        /*if (savedInstanceState == null) {
            Fragment f1 = new menu_item();
            Bundle args1 = new Bundle();
            args1.putString("string", "Sono il fragment 1 :)");
            f1.setArguments(args1);
            
            Fragment f2 = new menu_item();
            Bundle args2 = new Bundle();
            args2.putString("string", "Sono il fragment 2 :'(");
            f2.setArguments(args2);

            //FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.add(R.id.menu_list, f1).commit();
            //FragmentTransaction ft2 = getFragmentManager().beginTransaction();
            //ft2.add(R.id.menu_list, f2).commit();
        }*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		String stringUrl = "http://wifast.herokuapp.com/getShops?lat=1&lon=2";
		//String stringUrl = "http://192.168.1.89:5000/getShops?lat=1&lon=2";
		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Callback c = new MyCallBack();
			new JSONDownload(c).execute("GET", "JSONArray", stringUrl);
		} else {
			textView.setText("No network connection available.");
		}
		return true;
	}
	
	protected void onStart() {
		super.onStart();
	}

	private class MyCallBack implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			contactsList = (JSONArray) msg.obj;
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

			try{
				//Get the element that holds the earthquakes ( JSONArray )

				//Loop the Array
				for(int i=0;i < contactsList.length();i++){						

					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject e = contactsList.getJSONObject(i);

					map.put("id",  String.valueOf(i));
					map.put("name", e.getString("name"));
					map.put("dist", "Dist: " +  e.getString("dist"));
					mylist.add(map);
				}
			}catch(JSONException e)        {
				Log.e("log_tag", "Error parsing data "+e.toString());
			}

			ListAdapter adapter = new MenuItemArrayAdapter(MainActivity.this, mylist);
			listView.setAdapter(adapter);

			listView.setTextFilterEnabled(true);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					final Object item = parent.getItemAtPosition(position);

					System.out.println("Ma che figo: " + id + item.toString());

				}
			});
			return false;
		}

	}

	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
