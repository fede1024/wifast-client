package fr.eurecom.wifast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.eurecom.wifast.library.ShopListManager;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class WiFastApp extends Application {
	public static JSONArray types, shops;
	public static HashMap<String, JSONObject> menu_map;
	public static Properties properties;
	public static Context context;
	public static ShopListManager shopManager;
	public static String id;

    @Override public void onCreate() {
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

        shopManager = new ShopListManager(context);	// Start getting shop list
    }

	public static String getProperty(String propertyName){
		return properties.getProperty(propertyName);
	}
}
