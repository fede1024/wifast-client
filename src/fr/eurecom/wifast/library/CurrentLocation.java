package fr.eurecom.wifast.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler.Callback;
import android.provider.Settings;
import fr.eurecom.wifast.MainActivity;

public class CurrentLocation {
	
	private String serverURL;
	private Location lastLocation;
	private Context myContext;
	
	public CurrentLocation(Context context) {
		this.myContext = context;			
		serverURL = MainActivity.prop.getProperty("server_url")+"getShops";
	}

	public void getJSONShops(Callback c) {
		
		Location currentLocation = getLocation();
		this.lastLocation = currentLocation;
		
		ConnectivityManager connMgr = (ConnectivityManager)this.myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String searchURL = serverURL+"?lat="+currentLocation.getLatitude()+"&lon="+currentLocation.getLongitude();
			System.out.println("Searching URL: "+searchURL);
			new JSONDownload(c).execute("GET", "JSONArray", searchURL);
		} else
			showSettingsAlert("Network");
		
	}

	private Location getLocation() {
		LocationManager locationManger;
		Location location;
		locationManger = (LocationManager) this.myContext.getSystemService(Context.LOCATION_SERVICE);

		location = null;

		/* checking if settings to get position in the phone are enabled */
		if(locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER))
			location = locationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		else if(locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			location = locationManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		else
			showSettingsAlert("GPS");
		
		lastLocation = location;
		
		return location;
	}

	/* Methods for getting coordinates of the 
	 * last location are used for debugging 
	 * purposes only
	 */
	public double getLastLongitude() {
		if(lastLocation == null)
			return -999999999.0;
		return this.lastLocation.getLongitude();
	}
	
	public double getLastLatitude() {
		if(lastLocation == null)
			return -999999999.0;
		return this.lastLocation.getLatitude();
	}
	
	/**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */
    public void showSettingsAlert(String type){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
    
        if(type.equalsIgnoreCase("GPS")) {
	        alertDialog.setTitle("GPS Settings")
	        		   .setMessage("GPS is not enabled. Do you want to go to settings menu?")
	                   .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog,int which) {
				                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				                myContext.startActivity(intent);
				            }
				       })
				       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    	   public void onClick(DialogInterface dialog, int which) {
				    		   dialog.cancel();
				    	   }
			       });
        } else if(type.equalsIgnoreCase("Network")) {
        	alertDialog.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
	        		   .setTitle("Unable to connect")
				       .setCancelable(false)
				       .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
				    	   public void onClick(DialogInterface dialog, int id) {
				    		   Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				    		   myContext.startActivity(i);
				    	   }
				       })
					   .setNegativeButton("Cancel",
					       new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					               dialog.cancel();
					           }
					       });
        }
        // Showing Alert Message
        alertDialog.show();
    }
}