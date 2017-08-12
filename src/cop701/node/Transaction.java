package cop701.node;

import java.io.Serializable;
import java.util.List;


public class Transaction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8542256702717862972L;
	String transactionId;
	double amount;
	String senderId;
	String receiverId;
	String witnessId;
	List<String> inputTransactions;  
	boolean witnessCommitted;
	boolean receiverCommitted;
	boolean valid;

	
	public Transaction()
	{
		witnessCommitted=false;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getWitnessId() {
		return witnessId;
	}

	public void setWitnessId(String witnessId) {
		this.witnessId = witnessId;
	}

	public List<String> getInputTransactions() {
		return inputTransactions;
	}

	public void setInputTransactions(List<String> inputTransactions) {
		this.inputTransactions = inputTransactions;
	}

	public boolean isWitnessCommitted() {
		return witnessCommitted;
	}

	public boolean isReceiverCommitted() {
		return receiverCommitted;
	}
	
	public void setWitnessCommitted(boolean witnessCommitted) {
		this.witnessCommitted = witnessCommitted;
	}

	public void setReceiverCommitted(boolean receiverCommitted) {
		this.receiverCommitted = receiverCommitted;
	}
}
