package fr.eurecom.wifast;

import org.json.JSONArray;
import org.json.JSONException;

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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
    	private JSONArray keys;

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
            try {
				keys = MainActivity.menu_json.getJSONArray("_keys");
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }

        @Override
        public Fragment getItem(int i) {
        	System.out.println("--- getItem: "+i);
        	System.out.println("--- key: "+this.getKeyAtIndex(i));
            Fragment fragment = new SwipeFragment();
            Bundle args = new Bundle();
    		args.putString(SwipeFragment.ARG_TITLE, this.getKeyAtIndex(i));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
        	int num;
        	if (keys == null) {
        		num = MainActivity.menu_json.length() - 1;
        	} else {
				num = keys.length();
			}
            return num;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	String key = this.getKeyAtIndex(position);
        	if (key.equals(""))
        		return "Error";
        	return key.substring(0, 1).toUpperCase() + key.substring(1);
        }
        
        private String getKeyAtIndex(int position) {
        	String key = "";
        	try {
				key = keys.getString(position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
            return key;
        }
    }
    

}
