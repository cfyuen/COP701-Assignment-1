package cop701.node;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.logging.Logger;

import cop701.common.BaseMessage;

public class ClientWriter {

	private static final Logger logger = Logger.getLogger(ClientWriter.class.getName()); 
	
	private Client client;
	
	public ClientWriter(Client client) {
		this.client = client;
	}
	
	public void sendObject(String recipient, BaseMessage obj) {
		Socket recipientSocket;
		ObjectOutputStream outputStream;
		try {
			Address address = client.getNodesMap().get(recipient);
			recipientSocket = new Socket(address.getIp(),address.getPort());
			outputStream = new ObjectOutputStream(recipientSocket.getOutputStream());
			obj.setOriginAccountId(client.getAccount());
			logger.info("Node " + client.getAccount() + " is sending " + obj.getClass().getSimpleName() + " to node " + recipient);
			SignedObject so = sign(obj);
			outputStream.writeObject(so);
			recipientSocket.close();
		}
		catch (IOException | InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
			logger.warning("ClientWriter has some issues while writing to socket");
			e.printStackTrace();
		}
	}
	
	public SignedObject sign(Serializable obj) throws InvalidKeyException, SignatureException, IOException, NoSuchAlgorithmException {
		logger.fine("Signing object with private key");
		Signature signature = null;
		signature = Signature.getInstance(client.getPrivateKey().getAlgorithm());
		return new SignedObject(obj, client.getPrivateKey(), signature);
	}
	
}
