package cop701.node;

import java.io.Serializable;

public abstract class BaseTransaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 402098800324088733L;
	private String originAccountId;

	public String getOriginAccountId() {
		return originAccountId;
	}

	public void setOriginAccountId(String originAccountId) {
		this.originAccountId = originAccountId;
	}
	
}
