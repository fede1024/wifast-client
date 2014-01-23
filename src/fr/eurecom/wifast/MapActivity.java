package fr.eurecom.wifast;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        map = mapFrag.getMap();
        
        for (int i = 0; i < WiFastApp.shops.length(); ++i) {
			try {
				JSONObject item = (JSONObject)WiFastApp.shops.get(i);
				//double distance = item.getDouble("dist");
				MarkerOptions marker = new MarkerOptions().position(
						new LatLng(item.getDouble("lat"), item.getDouble("lon"))).title(item.getString("name"));
				map.addMarker(marker);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        myClickListener listener = new myClickListener();
		map.setOnInfoWindowClickListener(listener);

        // GREEN color icon
//        marker2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        
        this.centerMapOnMyLocation();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (WiFastApp.shopManager.getShopName() != null) {
    		finish();
    	}
    }
    
    private void centerMapOnMyLocation() {
    	Log.d("MAP", "centerMapOnLocation");
        map.setMyLocationEnabled(true);

        //Location location = map.getMyLocation();
        Location location = WiFastApp.shopManager.getLastLocation();
        if (location != null) {
        	Log.d("MAP", "location null");
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
        }
    }
    
    protected class myClickListener implements OnInfoWindowClickListener {

		@Override
		public void onInfoWindowClick(Marker arg0) {
			Intent intent = new Intent(MapActivity.this, ShopDetail.class);
			intent.putExtra("NAME", arg0.getTitle());
			MapActivity.this.startActivity(intent);
		}
    	
    }
}
