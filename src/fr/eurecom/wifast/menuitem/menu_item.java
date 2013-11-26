package fr.eurecom.wifast.menuitem;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class menu_item extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String myString = getArguments().getString("string");
		View rootView = inflater.inflate(R.layout.menu_item, container, false);
		TextView tv = (TextView) rootView.findViewById(R.id.textView);
		tv.setText(myString);
		return rootView;
	}
			
}
