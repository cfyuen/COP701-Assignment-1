package cop701.pastry;

import java.io.IOException;
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
	private PastryListener pastryListener;
	
	private Address bootstrapAddress = new Address("10.0.0.1",42000);
	
	public Pastry(String accountId,Map<String, Address> nodesMap) throws IOException {
		pkMap = new HashMap<String, PublicKey>();
		this.accountId = accountId;
		this.nodesMap = nodesMap;
		System.out.println("[" + accountId + "] NodesMap " + nodesMap.size());
		System.out.println(nodesMap.keySet().toString());
		routingTable = new String[L][(int)Math.pow(2, B)];
		neighborhoodSet = new ArrayList<String>();
		leftLeafSet = new ArrayList<String>();
		rightLeafSet = new ArrayList<String>();
		
		
		pastryListener = new PastryListener(this);

	}
	
	public void start() throws IOException {
		pastryWriter = new PastryWriter();
		
		nodeInitialization();
	}
	
	public void get(String senderId, String queryAccountId) {
		// 0. Check if key is the node itself
		if (queryAccountId.equals(accountId)) {
			sendKey(senderId,pkMap.get(accountId),queryAccountId);
		}
		// 1. Iterate leaf set
		for (String leaf : leftLeafSet) {
			if (queryAccountId.equals(leaf)) {
				sendKey(senderId,pkMap.get(leaf),queryAccountId);
			}
		}
		for (String leaf : rightLeafSet) {
			if (queryAccountId.equals(leaf)) {
				sendKey(senderId,pkMap.get(leaf),queryAccountId);
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
	}
	
	public void getRemote(String senderId, String nextAccountId, String queryAccountId) {
		
		 Message m = new Message(senderId,nodesMap.get(nextAccountId),queryAccountId);
		 pastryWriter.forwardMessage(m);
	}
	
	public void sendKey(String senderId, PublicKey pk, String queryAccountId)
	{
		Message m = new Message(senderId,nodesMap.get(senderId),queryAccountId);
		m.setPk(pk);
		pastryWriter.forwardMessage(m);
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

	
	public void nodeInitialization() throws UnknownHostException, SocketException
	{
		Address address=nodesMap.get(accountId);
		System.out.println(address.toString());
		if(!(address.equals(bootstrapAddress)))
		{
			Message message=new Message(accountId,bootstrapAddress,null);
			message.setMessageType(1);
			message.setNodesMap(nodesMap);
			pastryWriter.forwardMessage(message);
		}
	}
	public void addNodesMap(Message message)
	{
		nodesMap.putAll(message.getNodesMap());
		System.out.println("AddNodesMap [" + accountId + "]" + nodesMap);
		message.setMessageType(4);
		broadcast(message);
	}
	public void sendNodesMap(Message message)
	{
		String sender=message.getSenderId();
		Address senderAddress=message.getNodesMap().get(sender);
		Message responseMessage= new Message(accountId,senderAddress,null);
		responseMessage.setMessageType(2);
		responseMessage.setNodesMap(nodesMap);
		nodesMap.putAll(message.getNodesMap());
		pastryWriter.forwardMessage(responseMessage);	
	}
	public void broadcast(Message message)
	{
		if(message.getMessageType()==4)
		{
			for(String  key:nodesMap.keySet())
			{
				Message msg=new Message(accountId,nodesMap.get(key),null);
				msg.setMessageType(4);
				Map<String,Address> newNodeInfo=new HashMap<String,Address>();
				newNodeInfo.put(accountId,nodesMap.get(accountId));
				msg.setNodesMap(newNodeInfo);
				pastryWriter.forwardMessage(msg);
			}
		}
	}

	public PastryListener getPastryListener() {
		return pastryListener;
	}	

	public void addBroadcastNodesMap(Message message)
	{
		nodesMap.putAll(message.getNodesMap());
		System.out.println("AddBroadcastNodesMap [" + accountId + "]" + nodesMap);
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
	
	public void setBootstrapAddress(Address bootstrapAddress) {
		this.bootstrapAddress = bootstrapAddress;
	}
	
}

