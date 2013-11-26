/*
 * Copyright 2012 The Android Open Source Project
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

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.eurecom.wifast.library.JSONDownload;

public class SwipeFragment extends Fragment {
	public static final String ARG_OBJECT = "object";
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private TextView textView;
	private JSONArray contactsList;
	private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
//        Bundle args = getArguments();
//        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                Integer.toString(args.getInt(ARG_OBJECT)));
        listView = (ListView) rootView.findViewById(R.id.listview);
        System.out.println("---- listView: "+listView.toString());
        String stringUrl = "http://wifast.herokuapp.com/getShops?lat=1&lon=2";
		//String stringUrl = "http://192.168.1.89:5000/getShops?lat=1&lon=2";
		ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Callback c = new MyCallBack();
			new JSONDownload(c).execute("GET", "JSONArray", stringUrl);
		} else {
			textView.setText("No network connection available.");
		}
        return rootView;
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
			System.out.println("---- mylst: "+mylist.toString());
			ListAdapter adapter = new MenuItemArrayAdapter(getActivity(), mylist);
			System.out.println("---- adapter: "+adapter.toString());
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
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}}
