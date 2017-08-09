package cop701.node;

public class Address {

	private String ip;
	private Integer port;
	
	public Address(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}
	public Integer getPort() {
		return port;
	}
	
}
