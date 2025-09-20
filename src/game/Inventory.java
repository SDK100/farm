package game;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
	
	private final Map<String, Integer> counts = new HashMap<>();
	
	public void add(String item, int n) {counts.put(item, count(item)+n);}
	public boolean remove(String item, int n) {
		int c = count(item);
		if (c < n) return false;
		counts.put(item, c -n);
		return true;
	}

	private int count(String item) {return counts.getOrDefault(item, 0);}
	public Map<String,Integer> snapshot(){return new HashMap<>(counts);}

}
