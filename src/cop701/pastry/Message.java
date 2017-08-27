package cop701.pastry;

import java.security.PublicKey;
import java.io.Serializable;
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
	private PublicKey pk;
	private int messageType;/*1:when a sender sends a request to ask for nodesMap
	 						  2:when a nodes returns the nodesMap
	 						  3:for routing a message to a particular accountId
	 						  4:broadcast message for adding new node to nodesMap
	 						  6:msg for getting public key*/
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
}
