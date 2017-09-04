
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import cop701.common.BaseMessage;
import cop701.pastry.Message;

public class ClientListener extends Thread {
	
	private static final Logger logger = Logger.getLogger(ClientListener.class.getName()); 
	
	private Client client;
	private Socket socket; 
	
	public ClientListener(Client client, Socket socket) {
		this.client = client;
		this.socket = socket;
	}
	
	public void run() {
		//logger.info("Client connected on " + socket.getLocalPort());
		
		Object inObject = null;
		
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			inObject = in.readObject();
			if (inObject instanceof SignedObject) 
				processSignedObject((SignedObject)inObject);
			
			else if (inObject instanceof Message)
				processMessage((Message)inObject);
			
			else
				logger.warning("Unknown object received");
			
		} catch (IOException e) {
			System.out.println("Error in reading object from input stream");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void processSignedObject(SignedObject inObject) throws ClassNotFoundException, IOException {
		client.getInProgressMessage().add(inObject);
		getPublicKeyFromPastry(this.client.getAccount(),this.client.getPublicKey(),getInputAccountId(inObject));
	}

	private void processMessage(Message inObject) {
		if(inObject.getPk()!=null)
		{
			PublicKey publicKey = inObject.getPk();
			String accountId = inObject.getQueryAccountId();
			try {
				List<Object> verifiedObjects = checkKey(publicKey,accountId);
				processVerifiedObjects(verifiedObjects);
			} catch (InvalidKeyException | ClassNotFoundException | SignatureException | NoSuchAlgorithmException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else
			this.client.getPastry().getPastryListener().doStuff(inObject);
	}

	public void getPublicKeyFromPastry(String senderId, PublicKey senderPublicKey, String accountId) {
		client.getPastry().get(senderId,accountId);
	}
	
	public List<Object> checkKey(PublicKey publicKey, String accountId) throws ClassNotFoundException, IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException
	{
		List<Object> verifiedObjects = new ArrayList<Object>();
		for(Iterator<SignedObject> it = client.getInProgressMessage().iterator(); it.hasNext();)
		{
			SignedObject so = it.next();
			if((getInputAccountId(so).equals(accountId)))
			{
				Object verifiedObject = verifySignature(so,publicKey);
				verifiedObjects.add(verifiedObject);
				it.remove();
			}
		}
		return verifiedObjects;
	}

	private void processVerifiedObjects(List<Object> verifiedObjects) {
		for (Object verifiedObject : verifiedObjects) {
		
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
		
	}
	
	public Object verifySignature(SignedObject inObject, PublicKey publicKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, ClassNotFoundException, IOException {
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

	public String getInputAccountId(SignedObject so) throws ClassNotFoundException, IOException {
		Object o = so.getObject();
		if (o instanceof BaseMessage) {
			return ((BaseMessage)o).getOriginAccountId();
		}
		return "";
	}

}
