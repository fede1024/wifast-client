package fr.eurecom.wifast.library;

import java.util.Hashtable;
import java.util.Set;

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
	
	//What is it???
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
}
