package cop701.node;

import java.io.Serializable;
import java.net.InetAddress;

public class Address implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7635698097272647888L;
	private String ip;
	private Integer port;
	
	public Address(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}
	
	public Address(InetAddress localHost, Integer port) {
		this.ip = localHost.getHostAddress();
		this.port = port;
	}

	public String getIp() {
		return ip;
	}
	public Integer getPort() {
		return port;
	}
	
	public String toString() {
		return ip + ":" + port.toString();
	}
	
	public boolean equals(Address a) {
		return (a.ip.equals(this.ip)) && (a.port.equals(this.port));
	}
	
}
