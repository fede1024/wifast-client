package fr.eurecom.wifast.library;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import fr.eurecom.wifast.MainActivity;

public class Order {
	private static Order current_order;
	
	Hashtable<String, Integer> items;
	
	private Order(){
		 items = new Hashtable<String,Integer>();
	}
	
	public static Order getCurrentOrder() {
		if (Order.current_order == null) {
			Order.current_order = new Order();
		}
		return Order.current_order;
	}
	
	public Integer get(String key){
		Integer n = items.get(key);
		
		if(n == null)
			return 0;
		return n;
	}
	
	public void addItem(String key){
		items.put(key, 1 + get(key));
	}
	
	public Set<String> getItems(){
		return items.keySet();
	}
	
	public int getTotalCost(){
		Integer total = 0;
		
		for (Integer value : items.values())
			total += value;
		
		return total;
	}
	
	public JSONArray getOrderArray() {
		JSONArray arr = new JSONArray();
		Enumeration<String> en = this.items.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			try {
				JSONObject obj = new JSONObject(MainActivity.menu_map.get(key).toString());
				obj.put("count", this.get(key));
				arr.put(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		return arr;
	}
	
	public void sendToServer() {
		System.out.println("sendToServer");
		JSONArray arr = new JSONArray();
		Enumeration<String> en = this.items.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			try {
				JSONObject obj = new JSONObject();
				obj.put("name", key);
				obj.put("count", this.get(key));
				arr.put(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (arr.length() > 0) {
			PostRequest p = new PostRequest(arr);
			p.execute();
		} else {
			System.out.println("Error creting jsonarray in sendToServer");
		}
		
	}

	public class PostRequest extends AsyncTask<String, Void, String> {
//		private Callback callback;
		private JSONArray array;

		public PostRequest(/*Callback callback, */JSONArray array) {
//			this.callback = callback;
			this.array = array;
		}

		@Override
		protected String doInBackground(String... urls) {
			System.out.println("in doInBackground");
			// params comes from the execute() call: params[0] is the url.
			try {
				URL url = new URL(MainActivity.prop.getProperty("send_order_url"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setRequestProperty("Content-Type", "application/json");
		        conn.setRequestProperty("Accept", "application/json");
		        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
		        osw.write(this.array.toString());
		        osw.flush();
		        osw.close();
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				System.out.println("response code: "+response);
				return "OK";
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			System.out.println("Order sent!");
			Order.current_order = null;
			Order.this.items = null;
		}
	}
}
