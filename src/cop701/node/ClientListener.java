
package cop701.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.logging.Logger;

import cop701.common.BaseMessage;

public class ClientListener extends Thread {
	
	private static final Logger logger = Logger.getLogger(ClientListener.class.getName()); 
	
	private Client client;
	private Socket socket; 
	
	public ClientListener(Client client, Socket socket) {
		this.client = client;
		this.socket = socket;
	}
	
	public void run() {
		logger.info("Client connected on " + socket.getLocalPort());
		
		Object verifiedObject = null;
		
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Object inObject = in.readObject();
			// This method will check digital signature
			verifiedObject = decodeAndVerify(inObject);
		} catch (IOException e) {
			System.out.println("Error in reading object from input stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (verifiedObject instanceof Transaction) {
			Transaction transaction = (Transaction)verifiedObject;
			if (transaction.isWitnessCommitted() && transaction.isReceiverCommitted()) {
				client.receiveBroadcast(transaction);
			}
			else
				client.listenTransaction(transaction);
		}
		
		else if(verifiedObject instanceof TransactionResponse) {
			client.handleTransactionResponse((TransactionResponse)verifiedObject);
		}
		
		else {
			System.out.println("Unknown object received");
		}
		
	}
	
	private Object decodeAndVerify(Object inObject) {
		String originAccountId = "";
		try {
			originAccountId = getInputAccountId(inObject);
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
		PublicKey publicKey = getPublicKeyFromPastry(originAccountId);
		Object verifiedObject = null;
		try {
			verifiedObject = verifySignature(inObject, publicKey);
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | ClassNotFoundException
				| IOException e) {
			e.printStackTrace();
		}
		return verifiedObject;
	}

	public String getInputAccountId(Object inObject) throws ClassNotFoundException, IOException {
		if (inObject instanceof SignedObject) {
			SignedObject so = (SignedObject)inObject;
			Object o = so.getObject();
			if (o instanceof BaseMessage) {
				return ((BaseMessage)o).getOriginAccountId();
			}
		}
		return "";
	}
	
	public Object verifySignature(Object inObject, PublicKey publicKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, ClassNotFoundException, IOException {
		Signature signature = null;
		signature = Signature.getInstance(publicKey.getAlgorithm());
		if (inObject instanceof SignedObject) {
			SignedObject so = (SignedObject)inObject;
			boolean validSign = so.verify(publicKey, signature);
			if (validSign) {
				return so.getObject();
			}
			else {
				throw new SignatureException("Wrong signature");
			}
		}
		else {
			throw new SignatureException("Object received is not signed");
		}
	}
	
	public PublicKey getPublicKeyFromPastry(String accountId) {
		return client.getPastry().get(accountId);
	}
	
}
