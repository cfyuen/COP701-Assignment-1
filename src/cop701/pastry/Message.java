package cop701.pastry;

import java.util.List;
import java.util.Map;

import cop701.node.Address;

public class Message {
	
	private String senderId;
	private Address address;
	public void setAddress(Address address) {
		this.address = address;
	}

	private String queryAccountId;
	private int messageType;/*1:when a sender sends a request to ask for nodesMap
	 						  2:when a nodes returns the nodesMap
	 						  3:for routing a message to a particular accountId
	 						  4:broadcast message for adding new node to nodesMap
	 						  5:finding correct location of a new node*/
	private Map<String,Address> nodesMap;
	private String[][] routingTable;
	private List<String> neighborhoodSet;
	private List<String> leftLeafSet;
	
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

	private List<String> rightLeafSet;
	
	public Message(String senderId, Address address, String queryAccountId)
	{
		this.senderId = senderId;
		this.address = address;
		this.queryAccountId = queryAccountId;
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
	
	public int getMessageType() {
		return messageType;
	}
	
	public Map<String,Address> getNodesMap() {
		return nodesMap;
	}
}
