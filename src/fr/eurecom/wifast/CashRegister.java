package fr.eurecom.wifast;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class CashRegister extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        WiFastApp app = (WiFastApp) getApplicationContext();
        if(app.doCheckinIfNeeded()){
        	this.finish();
        	return;
        }
        setContentView(R.layout.activity_cash_register);

        TextView exp_time = (TextView)this.findViewById(R.id.exp_time_order_val);
        String time;
        time = this.getIntent().getStringExtra("EXP_TIME");
        if (time == null) {
            time = "02:00";
        }
        exp_time.setText(time);
		
		TextView order = (TextView)findViewById(R.id.order_num);
        TextView time_label = (TextView)findViewById(R.id.exp_time_order);
		TextView info = (TextView)findViewById(R.id.order_info);
		
		Typeface typface=Typeface.createFromAsset(getAssets(),"fonts/OleoScriptSwashCaps-Regular.ttf");
        order.setTypeface(typface);
        info.setTypeface(typface);
        time_label.setTypeface(typface);
        exp_time.setTypeface(typface);
		
        if(WiFastApp.current_order.orderId == null){
        	order.setText("Order: error");
        	return;
        }
        
    	order.setText("Order: " + WiFastApp.current_order.orderId);
	}

	@Override
	public void onResume() {
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
