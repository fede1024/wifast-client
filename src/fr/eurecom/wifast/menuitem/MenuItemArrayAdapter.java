package fr.eurecom.wifast.menuitem;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.net.Uri;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.eurecom.wifast.R;
import fr.eurecom.wifi.library.image.ImageManager;

public class MenuItemArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
	private final Context context;
	private final ArrayList<HashMap<String, String>> values;
	private boolean ready[];

	public MenuItemArrayAdapter(Context context, ArrayList<HashMap<String, String>> values) {
		super(context, R.layout.list_item, values);
		this.context = context;
		this.values = values;
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

		HashMap<String, String> curr = getItem(position);
		String name = curr.get("name");
		flv.setText(name);
		slv.setText(curr.get("dist"));

		String img = "" + position%10 + ".jpg";
		String path = ImageManager.getCachedImage(img, context);
		
		if(path != null)
			setImage(path, convertView);
		else {
			hideImage(convertView);
			Callback c = new ImageCallBack(convertView, name);
			new ImageManager(c, context).execute(img);
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
}