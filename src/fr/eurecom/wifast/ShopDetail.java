package fr.eurecom.wifast;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShopDetail extends Activity {
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detail);
		Intent intent = getIntent();
		this.name = intent.getStringExtra("NAME");
		String brand = this.name.split(" ")[0];
		
		TextView text = (TextView) findViewById(R.id.shop_text);
		text.setText(brand);
		
		Button openButton = (Button) findViewById(R.id.shop_open);
		
		int close = 0;
		for (int i = 0; i < WiFastApp.shops.length(); ++i) {
			try {
				JSONObject item = (JSONObject)WiFastApp.shops.get(i);
				if (this.name.equals(item.getString("name"))) {
					close = item.getInt("close");
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (close == 0) {
			openButton.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_detail, menu);
		return true;
	}

	public void closeButton(View view) {
		finish();
	}
	
	public void openButton(View view) {
		WiFastApp.shopManager.setShopName(this.name);
		finish();
	}
}
