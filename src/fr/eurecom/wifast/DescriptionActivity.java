package fr.eurecom.wifast;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class DescriptionActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		
		ImageView img = (ImageView) this.findViewById(R.id.big_icon);
		// img.setImage
		
		TextView desc = (TextView) this.findViewById(R.id.long_desc);
		// desc.setText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.description, menu);
		return true;
	}

}
