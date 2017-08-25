package cop701.pastry;

import java.security.PublicKey;

import cop701.node.Address;

public class Message {
	
	private String senderId;
	private Address address;
	private String queryAccountId;
	private PublicKey pk;
	
	public Message(String senderId, Address address, String queryAccountId)
	{
		this.senderId = senderId;
		this.address = address;
		this.queryAccountId = queryAccountId;
		pk=null;
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
	
}
