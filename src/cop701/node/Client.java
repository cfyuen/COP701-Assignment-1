package cop701.node;

import cop701.node.ClientUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
	
	private String accountId;
	private Address address;
	private ServerSocket serverSocket;
	private List<Transaction> inProgressTransactions;
	private ClientWriter clientWriter;
	
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
		clientWriter = new ClientWriter(this);
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

		System.out.println("Node " + this.accountId + ":  Writing object " + response.getClass().getSimpleName() + " to node " + transaction.getSenderId() + " [id: " + response.getTransactionId() + "]");
		clientWriter.sendObject(transaction.getSenderId(), response);
	}
	
	public void receiveBroadcast(Transaction transaction) {
		// TODO verify transaction and add to ledger
	}
	
	public void broadcast(Transaction t)
	{
				
		for(String  key:nodesMap.keySet())
		{
			clientWriter.sendObject(key,t);
		}
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
	
		clientWriter.sendObject(t.getReceiverId(), t);
		clientWriter.sendObject(t.getWitnessId(), t);
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
				
				if(t.isWitnessCommitted() && t.isReceiverCommitted())
				{
					broadcast(t);
					inProgressTransactions.remove(t);
				}
		
				break;
			}
		}
		
	}
	
	public String getAccount() {
		return accountId;
	}
	
	public Address getAddress() {
		return address;
	}

	public Map<String, Address> getNodesMap() {
		return nodesMap;
	}
}
