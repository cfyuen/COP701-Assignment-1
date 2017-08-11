package cop701.node;

import cop701.node.ClientUI;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
	
	private String accountId;
	private Address address;
	private ServerSocket serverSocket;
	private List<Transaction> inProgressTransactions;
	
	private Map<String, Address> nodesMap = new HashMap<String, Address>();
	
	/**
	 * This is the main program for client node
	 * @throws IOException 
	 */
	public Client(int id, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		this.accountId = String.valueOf(id);
		this.address = new Address("localhost", serverSocket.getLocalPort());
		inProgressTransactions = new ArrayList<Transaction>();
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

		response.setTransactionValid(true);

		Socket sender;
		ObjectOutputStream outputStream;
		try {
			sender = new Socket("localhost",nodesMap.get(transaction.getSenderId()).getPort());
			outputStream = new ObjectOutputStream(sender.getOutputStream());
			System.out.println("Node " + this.accountId + ":  Writing object " + response.getClass().getSimpleName() + " to " + sender.getPort() + " [id: " + response.getTransactionId() + "]");
			outputStream.writeObject(response);
			sender.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receiveBroadcast(Transaction transaction) {
		// TODO verify transaction and add to ledger
	}
	
	public void broadcast(Transaction t)
	{
		// TODO broadcast transaction to all the live nodes
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
	
	public void initiateTransaction(double amount, String receiverId, String witnessId , String transactionId)	
	{
		Transaction t = new Transaction();
		t.setTransactionId(transactionId);
		t.setAmount(amount);
		t.setSenderId(this.accountId);
		t.setReceiverId(receiverId);
		t.setWitnessId(witnessId);
		
		inProgressTransactions.add(t);
		
		ObjectOutputStream outputStream1 = null;
		ObjectOutputStream outputStream2 = null;
		
		
		Socket withReceiver;

		try {
			withReceiver = new Socket("localhost",nodesMap.get(t.getReceiverId()).getPort());
			outputStream1 = new ObjectOutputStream(withReceiver.getOutputStream());
			outputStream1.writeObject(t);
			
			withReceiver.close();
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
		
		}
		
		
		Socket withWitness;
		
		try {
			withWitness = new Socket("localhost",nodesMap.get(t.getWitnessId()).getPort());
			outputStream2 = new ObjectOutputStream(withWitness.getOutputStream());
			outputStream2.writeObject(t);
			withWitness.close();
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
		
		}
		
			// transaction Invalidated by either receiver or witness
	}
	
	public void handleTransactionResponse(TransactionResponse tr)
	{
		for(Transaction t:inProgressTransactions)
		{
			if(t.getTransactionId().equals(tr.getTransactionId()))
			{
				if(!t.isWitnessCommitted() && !t.isReceiverCommitted())
					t.setReceiverCommitted(tr.isTransactionValid());
				else
					t.setWitnessCommitted(tr.isTransactionValid());
				break;
			}
		}
		
		for(Transaction t:inProgressTransactions)
		{
			if(t.isReceiverCommitted() && t.isWitnessCommitted())
			{
				broadcast(t);
				inProgressTransactions.remove(t);
			}
		}
	}
}
