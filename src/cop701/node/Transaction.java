package cop701.node;

import java.util.List;

import cop701.common.BaseMessage;


public class Transaction extends BaseMessage {
	
	/**
	 * This is the transaction object used in Socket transfer
	 */
	private static final long serialVersionUID = 8542256702717862972L;
	private String transactionId;
	private double amount;
	private String senderId;
	private String receiverId;
	private String witnessId;
	private List<String> inputTransactions;  
	private boolean witnessCommitted;
	private boolean receiverCommitted;
	private boolean valid;

	
	public Transaction()
	{
		witnessCommitted=false;
		receiverCommitted=false;
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
