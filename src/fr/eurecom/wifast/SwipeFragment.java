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

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeFragment extends Fragment {
	public static final String ARG_TITLE = "title";
	public static final String ARG_LIST = "items_list";
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_swipe_collection, container, false);
        Bundle args = getArguments();
		ArrayList<JSONObject> items = (ArrayList<JSONObject>)args.getSerializable(SwipeFragment.ARG_LIST);
        
        listView = (ListView) rootView.findViewById(R.id.listview);
	
		Context context = getActivity();
		if (context != null) {
			ListAdapter adapter = new MenuItemArrayAdapter(getActivity(), items);
			listView.setAdapter(adapter);
	
			listView.setTextFilterEnabled(true);
		}

        return rootView;
    }
}