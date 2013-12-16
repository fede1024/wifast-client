package fr.eurecom.wifast.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler.Callback;
import android.os.Message;
import fr.eurecom.wifast.CartActivity;
import fr.eurecom.wifast.WiFastApp;

public class Order {
	private Hashtable<String, Integer> items;
	private CartActivity caller;
	public Integer orderId;
	
	public Order(){
		 items = new Hashtable<String,Integer>();
		 this.caller = null;
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
	
	public Double getTotalCost(){
		Double total = 0.0;
		
		if(items == null)
			return total;
		
		for (Entry<String, Integer> entry : items.entrySet()) {
			JSONObject item = WiFastApp.menu_map.get(entry.getKey());
			Double price = 0.0;
			if (item == null) {
				System.out.println("ERROR: item doee not exists.");
				continue;
			}
			try {
				price = item.getDouble("price");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total += price * entry.getValue();
		}
		
		return total;
	}
	
	public JSONArray getOrderArray() {
		JSONArray arr = new JSONArray();
		
		if(items == null)
			return arr;

		Enumeration<String> en = this.items.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			try {
				JSONObject obj = new JSONObject(WiFastApp.menu_map.get(key).toString());
				obj.put("count", this.get(key));
				arr.put(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		return arr;
	}
	
	public void sendToServer(CartActivity caller, String uuid, Callback done) {
		this.caller = caller;
		System.out.println("sendToServer");
		try {
			JSONObject order = new JSONObject();
			order.put("uuid", uuid);
			JSONArray arr = new JSONArray();
			Enumeration<String> en = this.items.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				JSONObject obj = new JSONObject();
				obj.put("name", key);
				obj.put("count", this.get(key));
				arr.put(obj);
			}
			if (arr.length() > 0) {
				order.put("items", arr);
				System.out.println("in sendOrder");
		
				String url = WiFastApp.getProperty("server_url")+"/api/newOrder";
		
				Callback c = new OrderSentCallback(done);
				new JSONDownload(c).execute("POST", "JSONObject", url, order.toString());
		
			} else {
				System.out.println("Error creting jsonarray in sendToServer");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Order sent callback
	private class OrderSentCallback implements Callback {
		Callback done;
		
		public OrderSentCallback(Callback done){
			this.done = done;
		}
		
		@Override
		public boolean handleMessage(Message msg) {
			if(msg == null || msg.obj == null){
				System.out.println("SENDING ORDER ERROR");
				this.done.handleMessage(null);
				return true;
			}

			System.out.println("Order sent! > " + msg.obj.toString());
			JSONObject obj = (JSONObject)msg.obj;
			
			try {
				orderId = obj.getInt("order_id");
			} catch (JSONException e) {
				System.out.println("MISSING ORDER ID");
				this.done.handleMessage(null);
				return true;
			}
			
			items = new Hashtable<String,Integer>();
			Message m = new Message();
			m.obj = orderId.toString();
			this.done.handleMessage(m);
			return false;
		}
	}
}
