package fr.eurecom.wifast;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.eurecom.wifast.library.ImageManager;

public class MenuItemArrayAdapter extends ArrayAdapter<JSONObject> {
	private final Context context;
	private boolean ready[];

	public MenuItemArrayAdapter(Context context, ArrayList<JSONObject> values) {
		super(context, R.layout.list_item, values);
		this.context = context;
		ready = new boolean[getCount()];
		for(int i = 0; i < getCount(); i++) ready[i] = false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item, parent, false);
		}
		
		TextView flv = (TextView) convertView.findViewById(R.id.firstLine);
		TextView slv = (TextView) convertView.findViewById(R.id.secondLine);
		ImageButton add = (ImageButton) convertView.findViewById(R.id.addToCartButton);
		RelativeLayout item = (RelativeLayout) convertView.findViewById(R.id.list_item);
		
		JSONObject curr = getItem(position);
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
		
		add.setOnClickListener(new AddCartOnClickListener(name));
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

		public AddCartOnClickListener(String id) {
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			System.out.println("Add this " + id);
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