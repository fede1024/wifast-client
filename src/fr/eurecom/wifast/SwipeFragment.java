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
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeFragment extends Fragment {
	public static final String ARG_TITLE = "object";
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_swipe_collection, container, false);
        Bundle args = getArguments();
        String title = args.getString(SwipeFragment.ARG_TITLE);
		try {
			ArrayList<JSONObject> myList = new ArrayList<JSONObject>();
			Iterator<JSONObject> it = MainActivity.menu_map.values().iterator();
			while (it.hasNext()) {
				
			}
			
			listArray = MainActivity.menu_json.getJSONArray(title);
        
	        listView = (ListView) rootView.findViewById(R.id.listview);
	        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
			//Get the element that holds the earthquakes ( JSONArray )

			//Loop the Array
			for(int i=0;i < listArray.length();i++){

				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject e = listArray.getJSONObject(i);

				map.put("id",  String.valueOf(i));
				map.put("name", e.getString("name"));
				map.put("dist", "Dist: " +  e.getString("desc"));
				mylist.add(map);
			}
			Context context = getActivity();
			if (context != null) {
				ListAdapter adapter = new MenuItemArrayAdapter(getActivity(), mylist);
				listView.setAdapter(adapter);
		
				listView.setTextFilterEnabled(true);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
						final Object item = parent.getItemAtPosition(position);
		
						System.out.println("Ma che figo: " + id + item.toString());
		
					}
				});
//				TextView v = (TextView)rootView.findViewById(R.id.listTextView);
//				v.setVisibility(View.GONE);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        return rootView;
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
