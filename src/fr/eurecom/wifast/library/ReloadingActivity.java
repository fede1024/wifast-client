package fr.eurecom.wifast.library;

import fr.eurecom.wifast.SplashScreen;
import fr.eurecom.wifast.WiFastApp;
import android.app.Activity;
import android.content.Intent;

public class ReloadingActivity extends Activity {

	@Override
    public void onResume(){
		if(WiFastApp.initialized != "ok"){ // App status data is not initialized, reboot
	        Intent splash = new Intent(getApplicationContext(), SplashScreen.class);
	        startActivity(splash);
	        finish();
		}
    	super.onResume();
    }
}
