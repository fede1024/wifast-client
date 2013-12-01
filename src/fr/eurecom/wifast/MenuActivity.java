package fr.eurecom.wifast;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.eurecom.wifast.library.Order;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
        this.setContentView(R.layout.activity_menu);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;
            case R.id.cart_menu_button:
            	System.out.println("Cart menu button");
            	Order.getCurrentOrder().sendToServer();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
    	private ArrayList<ArrayList<JSONObject>> list;
    	private ArrayList<String> titles;
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
            try {
            	this.list = new ArrayList<ArrayList<JSONObject>>();
            	this.titles = new ArrayList<String>();
            	for (int i=0; i<MainActivity.types.length(); i++) {
            		ArrayList<JSONObject> l = new ArrayList<JSONObject>();
            		JSONObject obj = MainActivity.types.getJSONObject(i);
            		this.titles.add(obj.getString("title"));
            		String type = obj.getString("name");
            		Iterator<JSONObject> it = MainActivity.menu_map.values().iterator();
            		while (it.hasNext()) {
            			JSONObject tmp = (JSONObject)it.next();
            			if (tmp.getString("type").equals(type))
            				l.add(tmp);
            		}
            		this.list.add(l);
            	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }

        @Override
        public Fragment getItem(int i) {
        	JSONArray array;
        	System.out.println("--- getItem: "+i);
        	System.out.println("--- key: "+titles.get(i));
            Fragment fragment = new SwipeFragment();
            Bundle args = new Bundle();
    		args.putString(SwipeFragment.ARG_TITLE, titles.get(i));
    		//args.putSerializable(SwipeFragment.ARG_LIST, list.get(i));
    		array = new JSONArray(list.get(i));
    		args.putString(SwipeFragment.ARG_LIST, array.toString());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
    		return titles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	return titles.get(position);
        }
    }
    

}
