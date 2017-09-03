package cop701.pastry;

import java.security.PublicKey;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cop701.node.Address;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8179684450336870390L;
	private String senderId;
	private Address address;
	private String queryAccountId;

	private Map<String, PublicKey> senderPkMap;
	private PublicKey senderPublicKey;
	private PublicKey pk;
	
	private int messageType;/*1:when a sender sends a request to ask for nodesMap
	 						  2:when a nodes returns the nodesMap
	 						  3:for routing a message to a particular accountId
	 						  4:broadcast message for adding new node to nodesMap
	 						  5:finding correct location of a new node
	 						  6:msg for getting public key
	 						  7:msg informing a node about its current location
	 						  8:msg telling the node on the left of new node to send its details */
	private Map<String,Address> nodesMap;
	private String[][] routingTable;
	private List<String> neighborhoodSet;
	private List<String> leftLeafSet;
	private List<String> rightLeafSet;
	
	public Message(String senderId, Address address, String queryAccountId)
	{
		this.senderId = senderId;
		this.address = address;
		this.queryAccountId = queryAccountId;
		pk=null;
		this.senderPublicKey = null;
		this.senderPkMap = new HashMap<String, PublicKey>();
	}
	
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	
	public String[][] getRoutingTable() {
		return routingTable;
	}
	public void setAddress(Address address) {
		this.address = address;
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
	
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public void setNodesMap(Map<String, Address> nodesMap) {
		this.nodesMap = nodesMap;
	}

	public String getSenderId() {
		return senderId;
	}

	public Address getAddress() {
		return address;
	}

	public String getQueryAccountId() {
		return queryAccountId;
	}
	
	public void setQueryAccountId(String queryAccountId) {
		this.queryAccountId = queryAccountId;
	}

	public PublicKey getPk() {
		return pk;
	}

	public void setPk(PublicKey pk) {
		this.pk = pk;
	}
	
	public int getMessageType() {
		return messageType;
	}
	
	public Map<String,Address> getNodesMap() {
		return nodesMap;
	}

	public PublicKey getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(PublicKey senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public Map<String, PublicKey> getSenderPkMap() {
		return senderPkMap;
	}

	public void setSenderPkMap(Map<String, PublicKey> senderPkMap) {
		this.senderPkMap = senderPkMap;
	}
}
