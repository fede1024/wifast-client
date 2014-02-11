package fr.eurecom.wifast;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PromotionActivityDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promotion);
		Integer points = WiFastApp.points;
		
		TextView points_view = (TextView) findViewById(R.id.points_view);
		points_view.setText("Points: "+points);
		
		TextView t;
		LinearLayout l;
		if (points < 10) {
			t = (TextView) findViewById(R.id.points10_1);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));
			t = (TextView) findViewById(R.id.points10_2);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));	
		} else {
			l = (LinearLayout) findViewById(R.id.points10_layout);
			l.setOnClickListener(new MyClickListener(0));
		}

		if (points < 40) {
			t = (TextView) findViewById(R.id.points40_1);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));
			t = (TextView) findViewById(R.id.points40_2);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));	
		} else {
			l = (LinearLayout) findViewById(R.id.points40_layout);
			l.setOnClickListener(new MyClickListener(1));
		}
		
		if (points < 70) {
			t = (TextView) findViewById(R.id.points70_1);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));
			t = (TextView) findViewById(R.id.points70_2);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));	
		} else {
			l = (LinearLayout) findViewById(R.id.points70_layout);
			l.setOnClickListener(new MyClickListener(2));
		}
		
		if (points < 100) {
			t = (TextView) findViewById(R.id.points100_1);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));
			t = (TextView) findViewById(R.id.points100_2);
			t.setTextColor(getResources().getColor(android.R.color.darker_gray));	
		} else {
			l = (LinearLayout) findViewById(R.id.points100_layout);
			l.setOnClickListener(new MyClickListener(3));
		}
		
		l = (LinearLayout) findViewById(R.id.points0_layout);
		l.setOnClickListener(new MyClickListener(-1));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.promotion, menu);
		return true;
	}
	
	class MyClickListener implements OnClickListener {
		private int index;
		
		public MyClickListener(int index) {
			this.index = index;
		}
		
		@Override
		public void onClick(View v) {
			WiFastApp.promotion_id = this.index;
			finish();
		}
		
	}

}
