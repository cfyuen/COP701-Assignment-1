package cop701.node;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ClientWriter {

	private static final Logger logger = Logger.getLogger(ClientWriter.class.getName()); 
	
	private Client client;
	private Map<String, Address> nodesMap = new HashMap<String, Address>();
	
	public ClientWriter(Client client) {
		this.client = client;
	}
	
	public void sendObject(String recipient, Object obj) {
		Socket recipientSocket;
		ObjectOutputStream outputStream;
		try {
			Address address = nodesMap.get(recipient);
			recipientSocket = new Socket(address.getIp(),address.getPort());
			outputStream = new ObjectOutputStream(recipientSocket.getOutputStream());
			logger.info("Node " + client.getAccount() + " is sending " + obj.getClass().getSimpleName() + " to node " + recipient);
			outputStream.writeObject(obj);
			recipientSocket.close();
		}
		catch (IOException e) {
			logger.warning("ClientWriter has some issues while writing to socket");
			e.printStackTrace();
		}
	}
	
	public void addNodeIdentity(String accountId, Address address) {
		nodesMap.put(accountId, address);
	}
	
}
