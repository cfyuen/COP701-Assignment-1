package cop701.node;

import cop701.common.BaseMessage;

public class TransactionResponse extends BaseMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6262397620814220298L;
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
