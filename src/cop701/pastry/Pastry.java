package cop701.pastry;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
	
	public Pastry(String accountId,Map<String, Address> nodesMap, PublicKey publicKey) throws IOException {
		pkMap = new HashMap<String, PublicKey>();
		pkMap.put(accountId, publicKey);
		
		this.accountId = accountId;
		this.nodesMap = nodesMap;
		printNodesMap();
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
			return;
		}
		// 1. Iterate leaf set
		for (String leaf : leftLeafSet) {
			if (queryAccountId.equals(leaf)) {
				getRemote(senderId,leaf,queryAccountId);
				//sendKey(senderId,pkMap.get(leaf),queryAccountId);
				return;
			}
		}
		for (String leaf : rightLeafSet) {
			if (queryAccountId.equals(leaf)) {
				getRemote(senderId,leaf,queryAccountId);
				//sendKey(senderId,pkMap.get(leaf),queryAccountId);
				return;
			}
		}
		// 2. Find in routing table
		int l = longestPrefix(queryAccountId,accountId);
		String route = routingTable[l][Integer.valueOf(queryAccountId.charAt(l)-'0')];
		if (!(route == null))
		{
			if (getRemote(senderId,route,queryAccountId)) return;
			else routingTable[l][Integer.valueOf(queryAccountId.charAt(l)-'0')] = null;
		}
		
		// 3. Going into the rare case
		logger.info("Going into the rare case");
		List<String> allRoutes = new ArrayList<String>();
		for (int i=l; i<L; ++i) {
			 for(int j=0; j<Math.pow(2, B); ++j) 
				 if(routingTable[i][j]!=null)
				 {
					 allRoutes.add(routingTable[i][j]);
				 }
		}
		allRoutes.addAll(leftLeafSet);
		allRoutes.addAll(rightLeafSet);
		
		boolean routeSuccess = false;
		while (!routeSuccess && !allRoutes.isEmpty()) {
			String bestMatch = null;
			for (String r : allRoutes) {
				int rl = longestPrefix(queryAccountId, r);
				if (rl > l) {
					l = rl;
					bestMatch = r;
				}
				else if (rl == l) {
					if (bestMatch == null) 
						bestMatch = r;
					else {
						int bestDiff = Math.abs(Integer.valueOf(queryAccountId)-Integer.valueOf(bestMatch));
						int rDiff = Math.abs(Integer.valueOf(queryAccountId)-Integer.valueOf(r));
						if (rDiff < bestDiff)
							bestMatch = r;
					}
				}
			}
			routeSuccess = getRemote(senderId,bestMatch,queryAccountId);
			if (!routeSuccess) {
				allRoutes.remove(bestMatch);
			}
		}
		if (routeSuccess) return;
		logger.warning("Not going anywhere");
	}
	
	public boolean getRemote(String senderId, String nextAccountId, String queryAccountId) {
		
		 Message m = new Message(senderId,nodesMap.get(nextAccountId),queryAccountId);
		 System.out.println("[Pastry] Routing " + senderId + "->...->" + accountId + "->" + nextAccountId + "->...->" + queryAccountId);
		 m.setMessageType(6);
		 return pastryWriter.forwardMessage(m);
	}
	
	public void sendKey(String senderId, PublicKey pk, String queryAccountId)
	{
		Message m = new Message(senderId,nodesMap.get(senderId),queryAccountId);
		m.setPk(pk);
		pastryWriter.forwardMessage(m);
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
		if(!(address.equals(bootstrapAddress)))
		{
			Message message=new Message(accountId,bootstrapAddress,null);
			message.setMessageType(1);
			message.setNodesMap(nodesMap);
			leftLeafSet.add("0001");
			rightLeafSet.add("0001");
			addToRoutingTable("0001");
			pastryWriter.forwardMessage(message);
		}
	}
	public void sendNodesMap(Message message)
	{
		nodesMap.putAll(message.getNodesMap());
		
		String sender=message.getSenderId();
		Address senderAddress=message.getNodesMap().get(sender);
		Message responseMessage= new Message(accountId,senderAddress,null);
		responseMessage.setSenderPublicKey(pkMap.get(accountId));
		responseMessage.setMessageType(2);
		responseMessage.setNodesMap(nodesMap);
		pastryWriter.forwardMessage(responseMessage);
		
		Message getNewNodeLocation= new Message(accountId,null,sender);
		getNewNodeLocation.setMessageType(5);
		getNewNodeLocation.setNodesMap(message.getNodesMap());
		routeToNode(getNewNodeLocation);	
	}
	public void routeToNode(Message msg)
	{
		
		String destination = msg.getQueryAccountId();
		System.out.println("["+accountId+"] routing to "+destination+" with "+leftLeafSet);
		if (destination.equals(accountId)) {
			System.out.println("Boomerang");
			return;
		}
		
		int l = longestPrefix(destination,accountId);
		String nextHop = routingTable[l][Integer.valueOf(destination.charAt(l)-'0')];
		boolean routeSuccess = false;
		if (!(nextHop == null))
		{
			// Case 1: Routing table forward
			System.out.println("nextHop found l:" + l);
			routeSuccess = forwardRequest(nextHop,msg,destination);
			if (routeSuccess) return;
			else routingTable[l][Integer.valueOf(destination.charAt(l)-'0')] = null;
		}
		
		if (leftLeafSet.isEmpty()) {
			// Case 2: Special case for node 0001
			Message newMsg=new Message(accountId,msg.getNodesMap().get(destination),destination);
			newMsg.setRoutingTable(routingTable);
			newMsg.setRightLeafSet(rightLeafSet);
			newMsg.setLeftLeafSet(leftLeafSet);
			newMsg.setSenderPkMap(pkMap);
			newMsg.setMessageType(7);
			pastryWriter.forwardMessage(newMsg);
			return;
		}
		
		if(leftLeafSet.get(0).compareTo(destination)>0||leftLeafSet.get(0).equals("0001"))
		{
			// Case 3: Destination reached
			int i = 0;
			while (i < leftLeafSet.size() && !routeSuccess) {
				Message newMsg = new Message(accountId,nodesMap.get(leftLeafSet.get(i)),destination);
				newMsg.setMessageType(8);
				newMsg.setNodesMap(msg.getNodesMap());
				routeSuccess = pastryWriter.forwardMessage(newMsg);
			}
			
			Message destMsg = new Message(accountId,msg.getNodesMap().get(destination),destination);
			destMsg.setRightLeafSet(rightLeafSet);
			destMsg.setSenderPkMap(pkMap);
			destMsg.setRoutingTable(routingTable);
			destMsg.setMessageType(7);
			pastryWriter.forwardMessage(destMsg);
			/*TODO: 1)send location nd tell next node also to do that
			 * 		2)also cope up with what happens if the initial bradcast has still no reached the next leaf node*/
			return;
		}

		// Case 4: Routing table not up to date - just go to next node
		System.out.println("nextHop NOT found " + leftLeafSet);
		int i = 0;
		while (i < leftLeafSet.size() && !routeSuccess) {
			routeSuccess = forwardRequest(leftLeafSet.get(i),msg,destination);
		}
			
	}
	
	public boolean forwardRequest(String nextHop,Message msg,String destination)
	{
		System.out.println("["+accountId+"] sending msg 5 to " + nextHop + " with destination " + destination);
		msg.setSenderId(accountId);
		msg.setAddress(nodesMap.get(nextHop));
		msg.setMessageType(5);
		msg.setQueryAccountId(destination);
		return pastryWriter.forwardMessage(msg);
	}
	
	public void addDetails(Message msg)
	{
		System.out.println("["+ accountId +"] Add details called by:"+msg.getSenderId());
		if(!msg.getSenderId().equals("0001"))
		{
			if(msg.getSenderId().compareTo(accountId)<0)
			{
				System.out.println("["+ accountId +"] message received from" + msg.getSenderId());
				//routingTable=msg.getRoutingTable();
				rightLeafSet = msg.getRightLeafSet();
				rightLeafSet.add(msg.getSenderId());
				
				if (rightLeafSet.size() > L/2)
					rightLeafSet.remove(0);
				
				for(int i=0;i<rightLeafSet.size();++i)
				{
					if(msg.getSenderPkMap().get(rightLeafSet.get(i))!=null)
						pkMap.put(rightLeafSet.get(i),msg.getSenderPkMap().get(rightLeafSet.get(i)));
				}
					
			}
			else if(msg.getSenderId().compareTo(accountId)>0)
			{
				leftLeafSet = msg.getLeftLeafSet();
				leftLeafSet.add(0, msg.getSenderId());
				
				if (leftLeafSet.size() > L/2)
					leftLeafSet.remove(leftLeafSet.size()-1);
				
				for(int i=0;i<leftLeafSet.size();++i)
				{	
					if(msg.getSenderPkMap().get(leftLeafSet.get(i))!=null)
						pkMap.put(leftLeafSet.get(i),msg.getSenderPkMap().get(leftLeafSet.get(i)));
				}
			}	
		}
		else
		{
			if(!msg.getLeftLeafSet().isEmpty())
			{
				System.out.println("["+ accountId +"] message received from" + msg.getSenderId());
				leftLeafSet.add(msg.getLeftLeafSet().get(0));
				for(int i=0;i<leftLeafSet.size();++i)
				{
					if(msg.getSenderPkMap().get(leftLeafSet.get(i))!=null)
						pkMap.put(leftLeafSet.get(i),msg.getSenderPkMap().get(leftLeafSet.get(i)));
				}
			}
		}

		addToRoutingTable(msg.getSenderId());
		for(int i=0;i <L;i++)
		{
			for(int y=0;y<Math.pow(2,B);y++)
			{
				if(msg.getRoutingTable()[i][y]!=null)
				addToRoutingTable(msg.getRoutingTable()[i][y]);
			}
		}
		msg.setSenderId(accountId);
		msg.setNodesMap(nodesMap);
		msg.setMessageType(4);
		broadcast(msg);
		
		printLeafSet();
		printRoutingTable();
		printPkMap();
	}
	
	public void addBroadcastNodesMap(Message message)
	{
		System.out.println("["+ accountId +"] Receive broadcast from "+message.getSenderId());
		nodesMap.putAll(message.getNodesMap());
		
		addToRightLeafSet(message.getSenderId());
		pkMap.put(message.getSenderId(), message.getSenderPublicKey());
		System.out.println("["+ accountId +"] [Added right] leaf set = L:" + leftLeafSet + "  R:" + rightLeafSet);
		
		addToLeftLeafSet(message.getSenderId());
		pkMap.put(message.getSenderId(), message.getSenderPublicKey());
		System.out.println("["+ accountId +"] [Added left] leaf set = L:" + leftLeafSet + "  R:" + rightLeafSet);
		
		addToRoutingTable(message.getSenderId());
	}
	
	private List<String> sortLeafSet(String accountId, List<String> leafSet) {
		List<String> smallerLeaf = new ArrayList<String>();
		List<String> largerLeaf = new ArrayList<String>();
		for (String leaf : leafSet) {
			if (leaf.compareTo(accountId)>0) largerLeaf.add(leaf);
			else smallerLeaf.add(leaf);
		}
		Collections.sort(smallerLeaf);
		Collections.sort(largerLeaf);
		leafSet = largerLeaf;
		leafSet.addAll(smallerLeaf);
		return leafSet;
	}

	public void addNodesMap(Message message)
	{
		nodesMap.putAll(message.getNodesMap());
		
		for(int i=0;i<leftLeafSet.size();++i)
		{
			if(message.getSenderPkMap().get(leftLeafSet.get(i))!=null)
				pkMap.put(leftLeafSet.get(i),message.getSenderPkMap().get(leftLeafSet.get(i)));
		}
		
		for(int i=0;i<rightLeafSet.size();++i)
		{
			if(message.getSenderPkMap().get(rightLeafSet.get(i))!=null)
				pkMap.put(rightLeafSet.get(i),message.getSenderPkMap().get(rightLeafSet.get(i)));
		}
	}
	
	public void addToRoutingTable(String id)
	{
		int l=longestPrefix(id,accountId);
		if(accountId.equals(id))
			return;
		if(routingTable[l][Integer.valueOf(id.charAt(l)-'0')]==null)
		{
			routingTable[l][Integer.valueOf(id.charAt(l)-'0')]=id;
			printRoutingTable();
		}
	}
	public void addToLeftLeafSet(String newNodeId)
	{
		if (leftLeafSet.contains(newNodeId) || newNodeId.equals(accountId)) return;
		leftLeafSet.add(newNodeId);
		leftLeafSet = sortLeafSet(accountId, leftLeafSet);
		if (leftLeafSet.size() > L/2)
			leftLeafSet.remove(leftLeafSet.size() - 1);
	}
	public void addToRightLeafSet(String newNodeId)
	{
		if (rightLeafSet.contains(newNodeId) || newNodeId.equals(accountId)) return;
		rightLeafSet.add(newNodeId);
		rightLeafSet = sortLeafSet(accountId, rightLeafSet);
		if (rightLeafSet.size() > L/2)
			rightLeafSet.remove(0);
	}
	
	public void broadcast(Message message)
	{
		if(message.getMessageType()==4)
		{
			for(String  key:nodesMap.keySet())
			{
				Message msg=new Message(accountId,nodesMap.get(key),null);
				msg.setSenderPublicKey(pkMap.get(accountId));
				msg.setMessageType(4);
				Map<String,Address> newNodeInfo=new HashMap<String,Address>();
				newNodeInfo.put(accountId,nodesMap.get(accountId));
				msg.setNodesMap(newNodeInfo);
				pastryWriter.forwardMessage(msg);
			}
		}
	}

	public void sendLeftLeafSet(Message msg)
	{
		msg.setMessageType(7);
		msg.setSenderId(accountId);
		msg.setRoutingTable(routingTable);
		msg.setLeftLeafSet(leftLeafSet);
		msg.setSenderPkMap(pkMap);
		msg.setAddress(msg.getNodesMap().get(msg.getQueryAccountId()));
		pastryWriter.forwardMessage(msg);
	}
	
	public void printRoutingTable() {
		System.out.println("["+ accountId +"] Routing Table");
		for (int i=0; i<L; ++i) {
			 for(int j=0; j<Math.pow(2, B); ++j)
				 System.out.print(routingTable[i][j] + " ");
			 System.out.println();
		}
	}
	
	public void printLeafSet() {
		System.out.println("["+ accountId +"] Leaf set = L:" + leftLeafSet + "  R:" + rightLeafSet);
	}
	
	public void printNodesMap() {
		System.out.println("[" + accountId + "] NodesMap (size: " + nodesMap.size() + ")");
		System.out.println(nodesMap);
	}
	
	public void printPkMap() {
		System.out.println("[" + accountId + "] Printing Public keys");
		Iterator<String> it = pkMap.keySet().iterator();
		while(it.hasNext())
		{
			String key = it.next();
			PublicKey value = pkMap.get(key);
			System.out.println(key+" : "+value);
		}			
	}

	public PastryListener getPastryListener() {
		return pastryListener;
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

