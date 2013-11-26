package fr.eurecom.wifast;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.eurecom.wifast.library.network.JSONDownload;
import fr.eurecom.wifast.menuitem.MenuItemArrayAdapter;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by daniele on 24/11/13.
 */
public class MenuActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_collection_demo);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

        // Create an adapter that when requested, will return a fragment representing an object in the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use getSupportFragmentManager.
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(this.getSupportFragmentManager());

        // Set up action bar.
        final ActionBar actionBar = this.getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) this.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        //return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1); // Our object is just an integer :-P
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DemoObjectFragment extends Fragment {

        public static final String ARG_OBJECT = "object";
        public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    	private TextView textView;
    	private JSONArray contactsList;
    	private ListView listView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        	
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
//            Bundle args = getArguments();
//            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                    Integer.toString(args.getInt(ARG_OBJECT)));
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
    	}
    }
    
    

}
