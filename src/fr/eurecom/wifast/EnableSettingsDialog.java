package fr.eurecom.wifast;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

// TODO: use this dialog also for network connection
public class EnableSettingsDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enable_settings_dialog);
		setFinishOnTouchOutside(false);
		setTitle("Location disabled");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_permissions, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		// Do nothing
	}
	
	public void showSettings(View v) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivity(intent);
	}

	public void doneButton(View v) {
		this.finish();
	}
	
	public void exitAppButton(View v) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

    public void showNoLocationAlertAndExit(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Localization missing")
		    .setMessage("WiFast needs localization.")
            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                //myContext.startActivity(intent);
		            }
		       });
        alertDialog.show();
    }
}
