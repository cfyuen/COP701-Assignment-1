package cop701.pastry;

import cop701.node.Address;

public class Message {
	
	private String senderId;
	private Address address;
	private String queryAccountId;
	
	public Message(String senderId, Address address, String queryAccountId)
	{
		this.senderId = senderId;
		this.address = address;
		this.queryAccountId = queryAccountId;
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
	
}
