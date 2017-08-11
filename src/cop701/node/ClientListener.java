
package cop701.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientListener extends Thread {
	
	private static final Logger logger = Logger.getLogger(ClientListener.class.getName()); 
	
	private Client client;
	private Socket socket; 
	
	public ClientListener(Client client, Socket socket) {
		this.client = client;
		this.socket = socket;
	}
	
	public void run() {
		logger.info("Client connected on port " + socket.getLocalPort());
		
		Object inObject = null;
		
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			inObject = in.readObject();
		} catch (IOException e) {
			System.out.println("Error in reading object from input stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (inObject instanceof Transaction) {
			Transaction transaction = (Transaction)inObject;
			if (transaction.isTransactionCommitted()) {
				client.receiveBroadcast(transaction);
			}
			else {
				client.listenTransaction(transaction);
			}
		}
		else {
			System.out.println("Unknown object received");
		}
		
	}
}
