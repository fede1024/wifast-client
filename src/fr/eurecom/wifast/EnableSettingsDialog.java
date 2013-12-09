package fr.eurecom.wifast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class EnableSettingsDialog extends Activity {
	public static String MISSING_ARG_NAME = "MISSING";
	public static int MISSING_LOCATION = 0;
	public static int MISSING_NETWORK = 1;
	private int missing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();

		setContentView(R.layout.enable_settings_dialog);
		setFinishOnTouchOutside(false);

		if (extras != null) {
		    missing = extras.getInt(MISSING_ARG_NAME);
		}
		TextView description = (TextView)this.findViewById(R.id.enable_setting_description);
		
		if (missing == MISSING_LOCATION){
			setTitle("Location disabled");
			description.setText(R.string.location_disabled);
		}
		else if (missing == MISSING_NETWORK){
			setTitle("Network disabled");
			description.setText(R.string.network_disabled);
		}
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
		Intent intent;
		if (missing == MISSING_LOCATION)
	        intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		else if (missing == MISSING_NETWORK)
	        intent = new Intent(Settings.ACTION_SETTINGS);
		else
			return;

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
}
