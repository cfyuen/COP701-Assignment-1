package cop701.pastry;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Pastry {

	private static final Logger logger = Logger.getLogger(Pastry.class.getName()); 
	
	/**
	 * Assuming there is no more than 256 nodes
	 * Example node id = "0231", "0012"
	 */
	public static final int B = 2; // bits every digit in node id
	public static final int L = 4; // Contains 2 nodes to the left and 2 nodes to the right in leaf set
	public static final int M = 8; // number of nodes in neighborhood set
	
	private Map<String, PublicKey> pkMap;
	
	private String accountId;
	
	private String[][] routingTable;
	private List<String> neighborhoodSet;
	private List<String> leftLeafSet;
	private List<String> rightLeafSet;
	
	public Pastry(String accountId) {
		pkMap = new HashMap<String, PublicKey>();
		
		this.accountId = accountId;
		routingTable = new String[L][(int)Math.pow(2, B)];
		neighborhoodSet = new ArrayList<String>();
		leftLeafSet = new ArrayList<String>();
		rightLeafSet = new ArrayList<String>();
	}
	
	public PublicKey get(String key) {
		// 0. Check if key is the node itself
		if (key.equals(accountId)) {
			return pkMap.get(key);
		}
		// 1. Iterate leaf set
		// TODO need checking
		for (String leaf : leftLeafSet) {
			if (key.equals(leaf)) {
				return getRemote(leaf, key);
			}
		}
		for (String leaf : rightLeafSet) {
			if (key.equals(leaf)) {
				return getRemote(leaf, key);
			}
		}
		// 2. Find in routing table
		int l = longestPrefix(key, accountId);
		String route = routingTable[l][Integer.valueOf(key.charAt(l))];
		if (!(route == null)) {
			return getRemote(route, key);
		}
		else {
			// FIXME implement according to spec
			logger.warning("Going into the rare case");
			for (String neighbor : neighborhoodSet) {
				return getRemote(neighbor,key);
			}
		}
		logger.warning("Should not reach here");
		return null;
	}
	
	public PublicKey getRemote(String dest, String key) {
		// TODO route to remote host for public key
		return null;
	}
	
	// Legacy method
	public PublicKey getOld(String key) {
		return pkMap.get(key);
	}
	
	public void put(String key, PublicKey value) {
		pkMap.put(key, value);
	}
	
	public int longestPrefix(String a, String b) {
		if (a.length() != b.length()) return 0;
		for (int i=0; i<a.length(); i++)
			if (a.charAt(i) != b.charAt(i))
				return i;
		return a.length();
	}
	
}
