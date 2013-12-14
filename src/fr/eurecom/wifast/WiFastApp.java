package fr.eurecom.wifast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import fr.eurecom.wifast.library.Order;
import fr.eurecom.wifast.library.ShopListManager;

public class WiFastApp extends Application {
	public static final String PROPERTY_UUID = "uuid";
	public static JSONArray types, shops;
	public static HashMap<String, JSONObject> menu_map;
	public static Properties properties;
	public static Context context;
	public static ShopListManager shopManager;
	public static Order current_order;
	public static String id;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        properties = new Properties();
		try {
			Resources resources = context.getResources();
			InputStream rawResource = resources.openRawResource(R.raw.wifast_properties);
			properties.load(rawResource);
			rawResource.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    properties.list(System.out);

		current_order = new Order();
        shopManager = new ShopListManager(context);	// Start getting shop list
    }

	public static String getProperty(String propertyName){
		return properties.getProperty(propertyName);
	}

    public boolean checkLocationEnabled(){
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			return true;
		return false;
	}
	
	public boolean checkNetworkEnabled(){
		ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) 
			return true;
		else
			return false;
	}

}
