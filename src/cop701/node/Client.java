package cop701.node;

import cop701.node.ClientUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Client {
	
	private String accountId;
	private Address address;
	private ServerSocket serverSocket;
	private ObjectInputStream inputStream1 = null;
	private ObjectOutputStream outputStream1 = null;
	private ObjectInputStream inputStream2 = null;
	private ObjectOutputStream outputStream2 = null;
	
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
		//ClientUI cui=new ClientUI();
		//cui.clientUI(address.getPort(),this);
		System.out.println("Listening on port " + address.getPort());
		while (true) {
			new ClientListener(this, serverSocket.accept()).run();
		}
	}
	
	public void stop() throws IOException {
		serverSocket.close();
	}
	
	public void hello() {
		System.out.println("Hello World at " + address.getPort());
	}
	
	public void listenTransaction(Transaction transaction) {
		TransactionResponse response = new TransactionResponse();
		response.setTransactionId(transaction.getTransactionId());
		response.setTransactionCommitted(true);
		
		Socket sender;
		ObjectOutputStream outputStream;
		try {
			sender = new Socket("localhost",nodesMap.get(transaction.getSenderId()).getPort());
			outputStream = new ObjectOutputStream(sender.getOutputStream());
			System.out.println("Node " + this.accountId + ":  Writing object " + response.getClass().getSimpleName() + " to " + sender.getPort() + " [id: " + response.getTransactionId() + "]");
			outputStream.writeObject(response);
			sender.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void receiveBroadcast(Transaction transaction) {
		// TODO verify transaction and add to ledger
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
	
	public void addNodeIdentity(String accountId, Address address) {
		nodesMap.put(accountId, address);
	}
	
	public void initiateTransaction() throws UnknownHostException, IOException	
	{
		Transaction t = new Transaction();
		t.setTransactionId("1");
		t.setAmount(2);
		t.setSenderId("0");
		t.setReceiverId("3");
		t.setWitnessId("4");
		
		Socket withReceiver = new Socket("localhost",nodesMap.get(t.getReceiverId()).getPort());
		outputStream1 = new ObjectOutputStream(withReceiver.getOutputStream());
		outputStream1.writeObject(t);
		withReceiver.close();
		
		Socket withWitness = new Socket("localhost",nodesMap.get(t.getWitnessId()).getPort());
		outputStream2 = new ObjectOutputStream(withWitness.getOutputStream());
		outputStream2.writeObject(t);
		withWitness.close();
		
	}
	
}
