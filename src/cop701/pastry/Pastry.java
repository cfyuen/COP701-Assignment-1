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

import cop701.common.Util;
import cop701.node.Address;
import cop701.node.Client;

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
	
	public Pastry(String accountId,Map<String, Address> nodesMap) throws IOException {
		pkMap = new HashMap<String, PublicKey>();
		this.accountId = accountId;
		this.nodesMap = nodesMap;
		serverSocket = new ServerSocket(0);
		routingTable = new String[L][(int)Math.pow(2, B)];
		neighborhoodSet = new ArrayList<String>();
		leftLeafSet = new ArrayList<String>();
		rightLeafSet = new ArrayList<String>();
		nodeInitialization(accountId,nodesMap);
	}
	
	public PublicKey get(String senderId, String queryAccountId) {
		// 0. Check if key is the node itself
		if (queryAccountId.equals(accountId)) {
			return pkMap.get(queryAccountId);
		}
		// 1. Iterate leaf set
		// TODO need checking
		for (String leaf : leftLeafSet) {
			if (queryAccountId.equals(leaf)) {
				return getRemote(leaf,queryAccountId);
			}
		}
		for (String leaf : rightLeafSet) {
			if (queryAccountId.equals(leaf)) {
				return getRemote(leaf,queryAccountId);
			}
		}
		// 2. Find in routing table
		int l = longestPrefix(queryAccountId,accountId);
		String route = routingTable[l][Integer.valueOf(queryAccountId.charAt(l))];
		if (!(route == null)) {
			return getRemote(route, queryAccountId);
		}
		else {
			// FIXME implement according to specifications
			logger.warning("Going into the rare case");
			for (int i=l; i<L; ++i) {
				 for(int j=Integer.valueOf(queryAccountId.charAt(l))+1; j<Math.pow(2, B); ++j)
					 if(routingTable[i][j]!=null)
					 {
						 //return .get(key);
						 String nextAccountId = routingTable[i][j];
						 Message m = new Message(senderId,nodesMap.get(nextAccountId),queryAccountId);
						 pastryWriter.sendKey(m);
					 }
						 
			}
		}
		logger.warning("Should not reach here");
		return null;
	}
	
	public PublicKey getRemote(String dest, String queryAccountId) {
		// TODO route to remote host for public key
		return null;
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
		
		System.out.println("Listening on port " + nodesMap.get(accountId).getPort());
		while (true) {
			new PastryListener(this, serverSocket.accept()).run();
		} 
		
	}
	public void nodeInitialization(String accountId,Map<String,Address> nodesMap) throws UnknownHostException, SocketException
	{
		Address address=nodesMap.get(accountId);
		if(!(address.getIp().equals("10.0.0.1")))
		{
			Address bootstrapAddress=new Address("10.0.0.1",42000);
			Message message=new Message(accountId,bootstrapAddress,null);
			message.setMessageType(1);
			message.setNodesMap(nodesMap);
			pastryWriter.sendKey(message);
		}
	}
	public void addNodesMap(Message message)
	{
		nodesMap.putAll(message.getNodesMap());
		System.out.println(nodesMap);
	}
	public void sendNodesMap(Message message)
	{
		String sender=message.getSenderId();
		Address senderAddress=message.getNodesMap().get(sender);
		Message responseMessage= new Message(accountId,senderAddress,null);
		responseMessage.setMessageType(2);
		responseMessage.setNodesMap(nodesMap);
		pastryWriter.sendKey(responseMessage);	
	}
	public void broadcast(Message message)
	{
		if(message.getMessageType()==4)
		{
			for(String  key:nodesMap.keySet())
			{
				if(key!="0001" && key!=accountId)
				{
					Message msg=new Message(accountId,nodesMap.get(key),null);
					msg.setMessageType(4);
					Map<String,Address> newNodeInfo=new HashMap<String,Address>();
					newNodeInfo.put("accountId",nodesMap.get(accountId));
					msg.setNodesMap(newNodeInfo);
					pastryWriter.sendKey(msg);
				}
			}
		}
	}
	public void addBroadcastNodesMap(Message message)
	{
		nodesMap.putAll(message.getNodesMap());
		System.out.println(nodesMap);
	}
	/*public void routeToNextNode(String accountId,int i)
	{
		int nextMatch=accountId.charAt(i+1)-48;
		if(routingTable[i+1][nextMatch]!=null)
		{
			
		}
	}*/
}

