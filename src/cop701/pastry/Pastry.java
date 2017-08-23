package cop701.pastry;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cop701.node.Address;


public class Pastry {

	private static final Logger logger = Logger.getLogger(Pastry.class.getName()); 
	
	/**
	 * Assuming there is no more than 256 nodes
	 * Example node id = "0231", "0012"
	 */
	private final int B = 2; // bits every digit in node id
	private final int L = 4; // Contains 2 nodes to the left and 2 nodes to the right in leaf set
	private final int M = 8; // number of nodes in neighborhood set
	
	private Map<String, PublicKey> pkMap;
	
	private String accountId;
	private Map<String,Address> nodesMap;
	private ServerSocket serverSocket;
	
	private String[][] routingTable;
	private List<String> neighborhoodSet;
	private List<String> leftLeafSet;
	private List<String> rightLeafSet;
	
	private PastryWriter pastryWriter;
	
	public Pastry(String accountId, Map<String, Address> nodesMap) throws IOException {
		pkMap = new HashMap<String, PublicKey>();
		pastryWriter=new PastryWriter(this);
		
		this.accountId = accountId;
		this.nodesMap = nodesMap;
		serverSocket = new ServerSocket(0);
		
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
			// FIXME implement according to specifications
			logger.warning("Going into the rare case");
			for (int i=l; i<L; ++i) {
				 for(int j=Integer.valueOf(key.charAt(l))+1; j<Math.pow(2, B); ++j)
					 if(routingTable[i][j]!=null)
					 {
						 //return .get(key);
						 String nextAccountId = routingTable[i][j];
						 pastryWriter.sendKey(nodesMap.get(nextAccountId),key);
					 }
						 
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

	public void start() throws IOException {
		
		pastryWriter = new PastryWriter(this);
		
		System.out.println("Listening on port " + nodesMap.get(accountId).getPort());
		while (true) {
			new PastryListener(this, serverSocket.accept()).run();
		} 
		
	}
	
}
