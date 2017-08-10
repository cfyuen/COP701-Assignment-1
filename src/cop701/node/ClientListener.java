package cop701.node;

import java.net.Socket;

public class ClientListener extends Thread {
	
	private Socket socket; 
	
	public ClientListener(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		System.out.println("Client connected on port " + socket.getLocalPort());
	}
}
