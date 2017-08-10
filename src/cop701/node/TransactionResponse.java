package cop701.node;

import java.io.Serializable;

public class TransactionResponse implements Serializable {
	
	private static final long serialVersionUID = 8758973589641525319L;
	String transactionId;
	boolean transactionCommitted;
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public boolean isTransactionCommitted() {
		return transactionCommitted;
	}
	
	public void setTransactionCommitted(boolean transactionCommitted) {
		this.transactionCommitted = transactionCommitted;
	}
	
}
