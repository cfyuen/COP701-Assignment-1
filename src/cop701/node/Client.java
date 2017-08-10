package cop701.node;

import cop701.node.ClientUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Client {
	
	private String accountId;
	private Address address;
	private ServerSocket serverSocket;
	
	private Map<String, Address> nodesMap = new HashMap<String, Address>();
	
	/**
	 * This is the main program for client node
	 * @throws IOException 
	 */
	public Client(int id, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		this.accountId = String.valueOf(id);
		this.address = new Address("localhost", serverSocket.getLocalPort());
	}
	
	public void start() throws IOException {
		ClientUI cui=new ClientUI();
		cui.clientUI(address.getPort(),this);
		System.out.println("Listening on port " + address.getPort());
		while (true) {
			new ClientListener(serverSocket.accept()).run();
			
		}
	}
	
	public void stop() throws IOException {
		serverSocket.close();
	}
	
	public void hello() {
		System.out.println("Hello World at " + address.getPort());
	}
	
	public void connectTo(String ip, int neighborPort) throws UnknownHostException, IOException {
		Socket connectSocket = new Socket(ip, neighborPort);
		connectSocket.close();
	}
	
	public String getAccount() {
		return accountId;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void addNodeIdentity(String account, Address address) {
		nodesMap.put(account, address);
	}
}
