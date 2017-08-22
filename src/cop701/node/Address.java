package cop701.node;

import java.net.InetAddress;

public class Address {

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
	
}
