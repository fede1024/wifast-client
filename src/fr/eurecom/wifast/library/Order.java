package fr.eurecom.wifast.library;

import java.util.Hashtable;
import java.util.Set;

public class Order {
	Hashtable<String, Integer> items;
	
	public Order(){
		 items = new Hashtable<String,Integer>();
	}
	
	public Integer get(String key){
		Integer n = items.get(key);
		
		if(n == null)
			return 0;
		return n;
	}
	
	public void add(String key){
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
