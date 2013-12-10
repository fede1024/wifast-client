package fr.eurecom.wifast;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import fr.eurecom.wifast.library.Order;

public class MenuItemArrayAdapter extends ArrayAdapter<JSONObject> {
	private final Context context;
	private boolean ready[];
	private ImageButton cart_icon;
	private ImageView animationImage;
	
	public MenuItemArrayAdapter(Context context, ArrayList<JSONObject> values) {
		super(context, R.layout.list_item, values);
		this.context = context;
		ready = new boolean[getCount()];
		for(int i = 0; i < getCount(); i++) ready[i] = false;
	}

	public void setCartIcon(ImageButton cart_icon) {
		this.cart_icon = cart_icon;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item, parent, false);
		}
		
		JSONObject curr = getItem(position);
		TextView flv = (TextView) convertView.findViewById(R.id.firstLine);
		TextView slv = (TextView) convertView.findViewById(R.id.secondLine);
		ImageButton add = (ImageButton) convertView.findViewById(R.id.addToCartButton);
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		
		RelativeLayout item = (RelativeLayout) convertView.findViewById(R.id.list_item);
		
		String name = "";
		String image = "";
		String descr = "";
		try {
			name = curr.getString("name");
			image = curr.getString("image");
			descr = curr.getString("description");
			flv.setText(name);
			if(descr.length() > 60)
				descr = descr.substring(0, 57) + "...";
			slv.setText(descr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			int count = Integer.parseInt(curr.get("count").toString());
			//count is present --> order view 
			TextView cnt = (TextView) convertView.findViewById(R.id.countCartLabel);
			cnt.setText(Integer.toString(count));
			cnt.setVisibility(View.VISIBLE);
			slv.setVisibility(View.INVISIBLE);
			add.setVisibility(View.INVISIBLE);
		} catch (JSONException e1) {
			//count is not present --> menu view
			add.setOnClickListener(new AddCartOnClickListener(name, icon));
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
			
			//TODO perform animation
			int animPos[] = new int[2];
			int iconPos[] = new int[2];
			int cartPos[] = new int[2];
			animationImage.getLocationOnScreen(animPos);
			icon.getLocationOnScreen(iconPos);
			cart_icon.getLocationOnScreen(cartPos);
			
			
			AnimationSet anim = new AnimationSet(true);
			//TranslateAnimation translateAnim = new TranslateAnimation(iconPos[0] - animPos[0], cartPos[0]-animPos[0], iconPos[1]-animPos[1], cartPos[1]-animPos[1]);
			TranslateAnimation translateAnim = new TranslateAnimation(iconPos[0] - animPos[0], 400, iconPos[1]-animPos[1], -200);
			translateAnim.setDuration(5000);
			anim.addAnimation(translateAnim);
			ScaleAnimation scaleAnim1 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f);
			scaleAnim1.setDuration(2500);
			/*ScaleAnimation scaleAnim2 = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f);
			scaleAnim2.setDuration(2500);
			scaleAnim2.setStartTime(0);
			scaleAnim2.setStartOffset(2500);
			*/
			anim.addAnimation(scaleAnim1);
			//anim.addAnimation(scaleAnim2);
			anim.setZAdjustment(AnimationSet.ZORDER_TOP);
			
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					animationImage.setVisibility(ImageView.VISIBLE);				
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					animationImage.setVisibility(ImageView.INVISIBLE);
				}
			});
			/* Copying the image */
			animationImage.setImageBitmap(((BitmapDrawable)icon.getDrawable()).getBitmap());
			/* Adjusting the size */
			animationImage.setAdjustViewBounds(true);
			animationImage.setMaxHeight(icon.getHeight());
			animationImage.setMaxWidth(icon.getWidth());
			/* Setting visibility and putting the image in front of all */
			animationImage.bringToFront();
			animationImage.startAnimation(anim);
			Order.getCurrentOrder().addItem(this.id);
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

	public void setAnimationImage(ImageView animationImage) {
		this.animationImage = animationImage;
	}
}