package fr.eurecom.wifast;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class PaymentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payment, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void cashRegisterPayment(View view) {
		Toast.makeText(this, "Order finished. Sending to server.", Toast.LENGTH_SHORT).show();
		final SharedPreferences pref = getSharedPreferences("wifast", Context.MODE_PRIVATE);
		String uuid = pref.getString(WiFastApp.PROPERTY_UUID, "");

		Callback c = new orderSentCallback();
		WiFastApp.current_order.sendToServer(uuid, c);	
	}
	
	private class orderSentCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			if(msg == null || msg.obj == null){
				Toast.makeText(getApplicationContext(), "Order error, please try again.", Toast.LENGTH_SHORT).show();
				return true;
			}

            Intent intent = new Intent(getApplicationContext(), CashRegister.class);
            JSONObject obj = (JSONObject)msg.obj;
            try {
                String time = obj.getString("expected_time");
                intent.putExtra("EXP_TIME", time);
            } catch (JSONException e){
                Log.e("ORDER", "Error decoding expected time");
            }
            
            if (WiFastApp.promotion_id != -1) {
            	try {
    				JSONObject promo = WiFastApp.promotions.getJSONObject(WiFastApp.promotion_id);
    				int points = promo.getInt("points");
    				
    				WiFastApp.points -= points;
                	if (WiFastApp.points < 0) WiFastApp.points = 0;
                	
                	WiFastApp.promotion_id = -1;    				
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
            	
            }

	        startActivity(intent);
			finish();
			return false;
		}
	}

}
