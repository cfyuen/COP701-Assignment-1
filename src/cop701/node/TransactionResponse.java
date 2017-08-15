package cop701.node;

import java.io.Serializable;

public class TransactionResponse implements Serializable {
	
	private static final long serialVersionUID = 8758973589641525319L;
	private String transactionId;
	private boolean transactionValid;
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public boolean isTransactionValid() {
		return transactionValid;
	}
	
	public void setTransactionValid(boolean transactionValid) {
		this.transactionValid = transactionValid;
	}
	
}
