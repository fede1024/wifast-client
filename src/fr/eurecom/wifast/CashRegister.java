package fr.eurecom.wifast;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.TextView;

public class CashRegister extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cash_register);
		
		TextView order = (TextView)findViewById(R.id.order_num);
		TextView info = (TextView)findViewById(R.id.order_info);
		
		Typeface typface=Typeface.createFromAsset(getAssets(),"fonts/OleoScriptSwashCaps-Regular.ttf");
        order.setTypeface(typface);
        info.setTypeface(typface);
		
        if(WiFastApp.current_order.orderId == null){
        	order.setText("Order: error");
        	return;
        }
        
    	order.setText("Order: " + WiFastApp.current_order.orderId);
	}

	@Override
	protected void onResume() {
	    super.onResume();

		TextView info = (TextView)findViewById(R.id.order_info);
		
        if (WiFastApp.current_order.ready){
	        info.setText("Your order is ready!");
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cash_register, menu);
		return true;
	}

}
