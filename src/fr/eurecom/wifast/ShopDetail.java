package fr.eurecom.wifast;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopDetail extends Activity {
	static private String TAG = "SHOP_DETAIL"; 
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detail);
		Intent intent = getIntent();
		this.name = intent.getStringExtra("NAME");
		String brand = this.name.split(" ")[0];
		JSONObject shop = this.getShopFromName(this.name);
		setTitle(brand);
		
		ImageView picture = (ImageView) findViewById(R.id.shop_image);
		if (brand.equals("Subway"))
			picture.setImageResource(R.drawable.subway);
		else if (brand.equals("Quick"))
			picture.setImageResource(R.drawable.quick);
		
		String address = "";
		try{
			address = shop.getString("address").replace("\\n","\n");
		} catch (JSONException e){
			Log.e(TAG, "Shop "+this.name+" doesn't contain the address argument. Fallback to 1 Place Guynemer.");
			address = "1 Place Guynemer,\nAntibes\n06600\n\nTel:0494533347";
		}
		
		TextView addressView = (TextView) findViewById(R.id.shop_address);
		addressView.setText(address);
		
		Button openButton = (Button) findViewById(R.id.shop_open);
		
		int close = 0;
		
		try{
			close = shop.getInt("close");
		} catch (JSONException e){
			Log.e(TAG, "Shop "+this.name+" doesn't contain the close argument. Fallback to not close shop.");
		}
		
		if (close == 0) {
			openButton.setVisibility(View.GONE);
		}
	}
	
	private JSONObject getShopFromName(String name) {
		for (int i = 0; i < WiFastApp.shops.length(); ++i) {
			try {
				JSONObject item = (JSONObject)WiFastApp.shops.get(i);
				if (name.equals(item.getString("name"))) {
					return item;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//The menu.xml is now empty!
		//TODelete??
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
