package cop701.pastry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
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
	public static final int B = 2; // bits every digit in node id
	public static final int L = 4; // Contains 2 nodes to the left and 2 nodes to the right in leaf set
	public static final int M = 8; // number of nodes in neighborhood set
	
	private Map<String, PublicKey> pkMap;
	
	private String accountId;
	private Map<String,Address> nodesMap;
	
	private String[][] routingTable;
	private List<String> neighborhoodSet;
	private List<String> leftLeafSet;
	private List<String> rightLeafSet;
	private PastryWriter pastryWriter;
	
	public Pastry(String accountId,Map<String, Address> nodesMap) throws IOException {
		pkMap = new HashMap<String, PublicKey>();
		this.accountId = accountId;
		this.nodesMap = nodesMap;
		routingTable = new String[L][(int)Math.pow(2, B)];
		neighborhoodSet = new ArrayList<String>();
		leftLeafSet = new ArrayList<String>();
		rightLeafSet = new ArrayList<String>();
	}
	
	public PublicKey get(String senderId, String queryAccountId) {
		// 0. Check if key is the node itself
		if (queryAccountId.equals(accountId)) {
			return pkMap.get(queryAccountId);
		}
		// 1. Iterate leaf set
		for (String leaf : leftLeafSet) {
			if (queryAccountId.equals(leaf)) {
				return pkMap.get(leaf);
			}
		}
		for (String leaf : rightLeafSet) {
			if (queryAccountId.equals(leaf)) {
				return pkMap.get(leaf);
			}
		}
		// 2. Find in routing table
		int l = longestPrefix(queryAccountId,accountId);
		String route = routingTable[l][Integer.valueOf(queryAccountId.charAt(l)-'0')];
		if (!(route == null))
			getRemote(senderId,route,queryAccountId);
		
		else {
			// FIXME implement according to specifications
			logger.warning("Going into the rare case");
			for (int i=l; i<L; ++i) {
				 for(int j=Integer.valueOf(queryAccountId.charAt(l)-'0')+1; j<Math.pow(2, B); ++j)
					 if(routingTable[i][j]!=null)
						 getRemote(senderId,routingTable[i][j],queryAccountId);
						 
			}
		}
		logger.warning("Should not reach here");
		return null;
	}
	
	public void getRemote(String senderId, String nextAccountId, String queryAccountId) {
		
		 Message m = new Message(senderId,nodesMap.get(nextAccountId),queryAccountId);
		 pastryWriter.sendKey(m);
	}
	
	// Legacy method
	public PublicKey getOld(String queryAccountId) {
		return pkMap.get(queryAccountId);
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
		pastryWriter = new PastryWriter();
	}
	
	public void nodeInitialization() throws UnknownHostException, SocketException
	{
		Address address=nodesMap.get(accountId);
		if(!(address.getIp().equals("10.10.15.1")))
		{
			//pastryWriter.write
			
			
			
		}
		else
		{
			//TODO getPastryObject
			
			
			
			
			
		}
	}
	public void routeToNextNode(String accountId,int i)
	{
		int nextMatch=accountId.charAt(i+1)-48;
		if(routingTable[i+1][nextMatch]!=null)
		{
			
		}
	}

	public Map<String, PublicKey> getPkMap() {
		return pkMap;
	}

	public void setPkMap(Map<String, PublicKey> pkMap) {
		this.pkMap = pkMap;
	}

	public String[][] getRoutingTable() {
		return routingTable;
	}

	public void setRoutingTable(String[][] routingTable) {
		this.routingTable = routingTable;
	}

	public List<String> getNeighborhoodSet() {
		return neighborhoodSet;
	}

	public void setNeighborhoodSet(List<String> neighborhoodSet) {
		this.neighborhoodSet = neighborhoodSet;
	}

	public List<String> getLeftLeafSet() {
		return leftLeafSet;
	}

	public void setLeftLeafSet(List<String> leftLeafSet) {
		this.leftLeafSet = leftLeafSet;
	}

	public List<String> getRightLeafSet() {
		return rightLeafSet;
	}

	public void setRightLeafSet(List<String> rightLeafSet) {
		this.rightLeafSet = rightLeafSet;
	}
	
}

