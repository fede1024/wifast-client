package fr.eurecom.wifast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.eurecom.wifast.library.ImageManager;

public class MenuItemArrayAdapter extends ArrayAdapter<JSONObject> {
	private final Context context;
	private boolean ready[];
	public static ImageButton cart_icon;
	public static ImageView animationImage;
	public static Callback newItemCallback;
	private boolean buttonEnabled;
	
	public MenuItemArrayAdapter(Context context, ArrayList<JSONObject> values) {
		super(context, R.layout.menu_list_item, values);
		this.context = context;
		ready = new boolean[getCount()];
		for(int i = 0; i < getCount(); i++) ready[i] = false;
		buttonEnabled = true;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		JSONObject obj = getItem(position);
		boolean cart;
		RelativeLayout item;
		
		cart = obj.has("count");

		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (cart)
				convertView = inflater.inflate(R.layout.cart_list_item, parent, false);
			else
				convertView = inflater.inflate(R.layout.menu_list_item, parent, false);
		}
		
		TextView flv = (TextView) convertView.findViewById(R.id.firstLine);
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		TextView pr = (TextView) convertView.findViewById(R.id.price);
		
		if(cart)
			item = (RelativeLayout) convertView.findViewById(R.id.cart_list_item);
		else
			item = (RelativeLayout) convertView.findViewById(R.id.menu_list_item);
		
		String name = "";
		String image = "";
		String descr = "";
		int count = 0;
		double price = 0;
		
		try {
			name = obj.getString("name");
			image = obj.getString("image");
			descr = obj.getString("description");
			price = obj.getDouble("price");
			flv.setText(name);
			pr.setText(new DecimalFormat("0.00 €").format(price));
			count = 0;
			
			if(cart)
				count = obj.getInt("count");
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		if(cart){
			//count is present --> order view 
			TextView cnt = (TextView) convertView.findViewById(R.id.countCartLabel);
			cnt.setText("Qt: " + Integer.toString(count));
		}
		else {
			ImageButton add = (ImageButton) convertView.findViewById(R.id.addToCartButton);
			TextView slv = (TextView) convertView.findViewById(R.id.secondLine);

			add.setOnClickListener(new AddCartOnClickListener(name, icon));
			slv.setText(descr);
		}
		
		item.setOnClickListener(new MoreInfoOnClickListener(name));
		
		String path = ImageManager.getCachedImage(image, context);
		
		if(path != null)
			setImage(path, convertView);
		else {
			hideImage(convertView);
			Callback c = new ImageCallBack(convertView, name);
			new ImageManager(c, context).execute(image);
		}
		return convertView;
	}
	
	private void hideImage(View rowView){
		//ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		RelativeLayout loading = (RelativeLayout) rowView.findViewById(R.id.loadingPanel);
		//imageView.setImageURI(Uri.parse("file://"+path));
		loading.setVisibility(View.VISIBLE);
	}
	
	private void setImage(String path, View rowView){
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		RelativeLayout loading = (RelativeLayout) rowView.findViewById(R.id.loadingPanel);
		imageView.setImageURI(Uri.parse("file://"+path));
		loading.setVisibility(View.INVISIBLE);
	}
	
	// Image loading callback
	private class ImageCallBack implements Callback {
		View rowView;
		String name;
		
		public ImageCallBack(View rv, String name){
			rowView = rv;
			this.name = name;
		}
		
		@Override
		public boolean handleMessage(Message msg) {
			TextView flv = (TextView) rowView.findViewById(R.id.firstLine);
			String newName = (String)flv.getText();
			if(newName == name)
		        setImage((String)msg.obj, rowView);
			return false;
		}
	}

	private class AddCartOnClickListener implements OnClickListener {
		String id;
		ImageView icon;
		
		public AddCartOnClickListener(String id, ImageView icon) {
			this.id = id;
			this.icon = icon;
		}

		@Override
		public void onClick(View v) {
			System.out.println("Add this " + id);
			
			if(buttonEnabled == false)
				return;
			
			//Perform animation
			/* Copying the image */
			animationImage.setImageBitmap(((BitmapDrawable)icon.getDrawable()).getBitmap());
			/* Adjusting the size */
			animationImage.setAdjustViewBounds(true);
			animationImage.setMaxHeight(icon.getHeight());
			animationImage.setMaxWidth(icon.getWidth());
			
			int animPos[] = new int[2];
			int iconPos[] = new int[2];
			int cartPos[] = new int[2];
			animationImage.getLocationOnScreen(animPos);
			icon.getLocationOnScreen(iconPos);
			cart_icon.getLocationOnScreen(cartPos);
			
			TranslateAnimation translateAnim1 = new TranslateAnimation(iconPos[0]-animPos[0],
																	   (cartPos[0]-animPos[0])*2,	// (deltaX)/0.5 0.5 = scaling factor
																	   iconPos[1]-animPos[1],
																	   (cartPos[1]-animPos[1])*2);	// (deltaY)/0.5
			translateAnim1.setInterpolator(new AccelerateInterpolator());
			translateAnim1.setDuration(500);
			ScaleAnimation scaleAnim1 = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
															Animation.RELATIVE_TO_SELF, 0.5f,
															Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnim1.setDuration(500);
			/*ScaleAnimation scaleAnim2 = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
															Animation.RELATIVE_TO_SELF, 0.5f,
															Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnim2.setDuration(1000);
			scaleAnim2.setStartOffset(1000);*/
			
			/* Combine animations */
			AnimationSet anim = new AnimationSet(false);
			anim.addAnimation(translateAnim1);
			anim.addAnimation(scaleAnim1);
			//anim.addAnimation(scaleAnim2);
			anim.setZAdjustment(AnimationSet.ZORDER_TOP);	
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					animationImage.setVisibility(ImageView.VISIBLE);
					buttonEnabled = false;
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					animationImage.setVisibility(ImageView.INVISIBLE);
					buttonEnabled = true;
					final Handler handler = new Handler(); // Execute price update with delay
					handler.postDelayed(new Runnable() {	// to prevent android animation flick
						@Override
						public void run() {
							newItemCallback.handleMessage(null);
						}
					}, 50);
				}
			});
			/* Putting the image in front of all */
			animationImage.bringToFront();
			animationImage.startAnimation(anim);
			/* Add order to cart */
			WiFastApp.current_order.addItem(this.id);
		}
	}

	private class MoreInfoOnClickListener implements OnClickListener {
		String id;

		public MoreInfoOnClickListener(String id) {
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			System.out.println("More info on this " + id);
		}
	  }
}