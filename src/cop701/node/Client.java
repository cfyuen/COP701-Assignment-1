package cop701.node;

import cop701.node.ClientUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import cop701.master.Master;

public class Client {
	
	private int port;
	private ServerSocket serverSocket;
	
	/**
	 * This is the main program for client node
	 * @throws IOException 
	 */
	public Client(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		this.port = serverSocket.getLocalPort();
	}
	
	public void start() throws IOException {
		ClientUI cui=new ClientUI();
		cui.clientUI(port,this);
		while (true) {
			System.out.println("Listening on port " + port);
			new ClientListener(serverSocket.accept()).run();
			
		}
	}
	
	public void stop() throws IOException {
		serverSocket.close();
	}
	
	public void hello() {
		System.out.println("Hello World at " + port);
	}
	
	public void getNeighbor() {
		int nearbyPort = Master.getNearbyNode(port);
		System.out.println("My [" + port + "] nearby port is " + nearbyPort);
		try {
			connectTo("localhost", nearbyPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectTo(String ip, int neighborPort) throws UnknownHostException, IOException {
		Socket connectSocket = new Socket(ip, neighborPort);
		connectSocket.close();
	}
	
	public int getPort() {
		return port;
	}
}

class ClientListener extends Thread {
	
	private Socket socket; 
	
	public ClientListener(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		System.out.println("Client connected on port " + socket.getLocalPort());
	}
}