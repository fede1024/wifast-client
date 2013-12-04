package fr.eurecom.wifast;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ShopsListDialog extends DialogFragment {

	private ArrayList<String> possibleShops;
	private String correctShop;
	
	public static ShopsListDialog newInstance(int title) {
		ShopsListDialog dialog = new ShopsListDialog();
		Bundle args = new Bundle();
		args.putInt("title", title);
		dialog.setArguments(args);
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title");
	    // Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    CharSequence[] shops = possibleShops.toArray(new CharSequence[possibleShops.size()]);
	    
	    builder.setTitle(title)
	    	   .setItems(shops, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// which contains the index position of the selected items
						correctShop = possibleShops.get(which);
					}
	    	   });
	    // Create the AlertDialog object and return it
	    return builder.create();
	}

	public void addShop(String shop) {
		if(possibleShops == null)
			possibleShops = new ArrayList<String>();
		
		possibleShops.add(shop);
	}

	public String getCorrectShop() {
		return correctShop;
	}
}
