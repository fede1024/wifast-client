package fr.eurecom.wifast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends FragmentActivity {
	private Callback updatePrices;

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

        WiFastApp app = (WiFastApp) getApplicationContext();
        if(app.doCheckinIfNeeded()){
        	this.finish();
        	return;
        }

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
        MenuItemArrayAdapter.cart_icon = (ImageButton) this.findViewById(R.id.cart_menu_button);
        MenuItemArrayAdapter.animationImage = (ImageView) this.findViewById(R.id.hidden_image);
        
        TextView priceTV = (TextView) this.findViewById(R.id.priceTV);
        
        updatePrices = new newItemCallback(priceTV);
        MenuItemArrayAdapter.newItemCallback = updatePrices;
        //return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

	@Override
    public void onResume(){
		super.onResume();
        updatePrices.handleMessage(null);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    public void cartButtonPressed(View view) {
    	Intent intent = new Intent(this, CartActivity.class);
    	startActivity(intent);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	this.finish();
                return true;
            case R.id.cart_menu_button:
            	Intent intent = new Intent(this, CartActivity.class);
            	startActivity(intent);
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
            	for (int i=0; i<WiFastApp.types.length(); i++) {
            		ArrayList<JSONObject> l = new ArrayList<JSONObject>();
            		JSONObject obj = WiFastApp.types.getJSONObject(i);
            		this.titles.add(obj.getString("title"));
            		String type = obj.getString("name");
            		Iterator<JSONObject> it = WiFastApp.menu_map.values().iterator();
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
    
    
	private class newItemCallback implements Callback {
		TextView priceTV;
		
		public newItemCallback(TextView priceTV){
			this.priceTV = priceTV;
		}
		
		@Override
		public boolean handleMessage(Message msg) {
			Double cost = WiFastApp.current_order.getTotalCost();

			priceTV.setText(new DecimalFormat("0.00 €").format(cost));
			return false;
		}
	}
}
