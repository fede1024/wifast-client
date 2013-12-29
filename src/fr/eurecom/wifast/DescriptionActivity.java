package fr.eurecom.wifast;

import java.text.DecimalFormat;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.eurecom.wifast.library.ImageManager;
import fr.eurecom.wifast.library.TextProgressBar;

public class DescriptionActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		
		final ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Product Description:");
		
		Bundle bundle = getIntent().getExtras(); 
		
		String name = bundle.getString("name");
		String image = bundle.getString("image");
		String desc = bundle.getString("description");
		String nutr = bundle.getString("nutrients");
		double desc_price = bundle.getDouble("price");
		
		TextView prod_name = (TextView) this.findViewById(R.id.prod_name);
		Typeface typface=Typeface.createFromAsset(getAssets(),"fonts/OleoScriptSwashCaps-Regular.ttf");
		prod_name.setTypeface(typface);
		prod_name.setText(name);
		
		String path = ImageManager.getCachedImage(image, this);
		
		ImageView img = (ImageView) this.findViewById(R.id.big_icon);
		if(path != null)
			setImage(path, img);
		else {
			hideImage();
			Callback c = new ImageCallBack(img);
			new ImageManager(c, this).execute(image);
		}
		
		TextView long_desc = (TextView) this.findViewById(R.id.long_desc);
		long_desc.setTypeface(typface);
		long_desc.setText(desc);
		
		String nutr_values[] = nutr.split(" ");
		
		TextView cal_val = (TextView) this.findViewById(R.id.cal_val);
		cal_val.setText(nutr_values[0]);
		
		TextProgressBar prot_bar = (TextProgressBar) this.findViewById(R.id.prot_progr);
		prot_bar.setText(nutr_values[1]);
		prot_bar.setMax(100);
		prot_bar.setProgress(Integer.parseInt(nutr_values[1]));
		
		TextProgressBar carb_bar = (TextProgressBar) this.findViewById(R.id.carb_progr);
		carb_bar.setText(nutr_values[2]);
		carb_bar.setMax(100);
		carb_bar.setProgress(Integer.parseInt(nutr_values[2]));

		TextProgressBar fat_bar = (TextProgressBar) this.findViewById(R.id.fat_progr);
		fat_bar.setText(nutr_values[3]);
		fat_bar.setMax(100);
		fat_bar.setProgress(Integer.parseInt(nutr_values[3]));
		
		TextView price = (TextView) this.findViewById(R.id.desc_price);
		price.setText("Price: "+new DecimalFormat("0.00 €").format(desc_price));
	}

	// Image loading callback
	private class ImageCallBack implements Callback {
		ImageView img;
		
		public ImageCallBack(ImageView img){
			this.img = img;
		}	
		@Override
		public boolean handleMessage(Message msg) {
			String path = (String) msg.obj;
			setImage(path, this.img);
			return false;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.description, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                /*Intent upIntent = new Intent(this, MenuActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();*/
                    this.finish();
                /*} else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }*/
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void hideImage(){
		RelativeLayout loading = (RelativeLayout) this.findViewById(R.id.desc_loadingPanel);
		loading.setVisibility(View.VISIBLE);
	}
	
	private void setImage(String path, ImageView img){
		RelativeLayout loading = (RelativeLayout) this.findViewById(R.id.desc_loadingPanel);
		img.setImageURI(Uri.parse("file://"+path));
		loading.setVisibility(View.INVISIBLE);
	}
}
