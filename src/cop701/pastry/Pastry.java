package cop701.pastry;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Pastry {

	Map<String, PublicKey> pkMap;
	
	public Pastry() {
		pkMap = new HashMap<String, PublicKey>();
	}
	
	public PublicKey get(String key) {
		return pkMap.get(key);
	}
	
	public void put(String key, PublicKey value) {
		pkMap.put(key, value);
	}
	
}
