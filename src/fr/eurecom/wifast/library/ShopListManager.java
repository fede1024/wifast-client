package fr.eurecom.wifast.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler.Callback;
import android.provider.Settings;
import android.util.Log;
import fr.eurecom.wifast.WiFastApp;

public class ShopListManager {
	private String serverURL;
	private Location lastLocation;
	private Context myContext;
	private String shopName;
	
	public ShopListManager(Context context) {
		this.myContext = context;			
		serverURL = WiFastApp.getProperty("get_shops_url");
	}

	public boolean getJSONShops(Callback c) {
		String searchURL  = "";
		if(lastLocation != null)
			searchURL = serverURL+"?lat="+lastLocation.getLatitude()+"&lon="+lastLocation.getLongitude();
		else {
			Log.d("ERROR", "Location null!!!");
			searchURL = serverURL;
		}
		System.out.println("Searching URL: "+searchURL);
		new JSONDownload(c).execute("GET", "JSONArray", searchURL);
		return false;
	}

	public Location updateLocation() {
		Location location = null;
		LocationManager locationManager = (LocationManager) this.myContext.getSystemService(Context.LOCATION_SERVICE);

		/* checking if settings to get position in the phone are enabled */
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if(location == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		lastLocation = location;
		
		if (location != null)
			System.out.println("Location from: " + location.getProvider());
		else
			System.out.println("Location from: none");
		
		return location;
	}
	
	public Location getLastLocation(){
		return this.lastLocation;
	}
	
	public boolean locationIsValid(){
		if (this.lastLocation == null) // Add timeout
			return false;
		else
			return true;
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
        
        alertDialog.setCancelable(false);
    
        if(type.equalsIgnoreCase("Network")) {
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
    
    public void setShopName(String name){
    	this.shopName = name;
    }
    
    public String getShopName(){
    	return this.shopName;
    }
}