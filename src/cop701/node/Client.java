package cop701.node;

import cop701.node.ClientUI;
import cop701.pastry.Pastry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Client {
	
	private static final Logger logger = Logger.getLogger(Client.class.getName()); 
	
	private String accountId;
	private Address address;
	private ServerSocket serverSocket;
	private List<Transaction> inProgressTransactions;
	private ClientWriter clientWriter;
	private Ledger ledger;
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	private Pastry pastry;
	
	private Map<String, Address> nodesMap = new HashMap<String, Address>();
	
	/**
	 * This is the main program for client node
	 * @throws IOException 
	 */
	public Client(String id) throws IOException {
		serverSocket = new ServerSocket(0);
		this.accountId = id;
		this.address = new Address(InetAddress.getLocalHost(), serverSocket.getLocalPort());
		inProgressTransactions = new ArrayList<Transaction>();
		ledger = new Ledger();
		try {
			generateKeyPairs();
		} catch (NoSuchAlgorithmException e) {
			logger.warning("Error generating public / private keys");
			e.printStackTrace();
		}
		pastry = new Pastry(accountId);
	}
	
	public static void main(String[] args) throws IOException {
		setup();
		
		String id = args[0];
		Client client = new Client(id);
		client.start();
	}
	
	private static void setup() {
		Locale.setDefault(Locale.US);
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void start() throws IOException {
		//ClientUI cui=new ClientUI();
		//cui.clientUI(address.getPort(),this);
		
		clientWriter = new ClientWriter(this);
		
		System.out.println("[" + accountId + "] Listening on " + address.toString());
		while (true) {
			new ClientListener(this, serverSocket.accept()).run();
		}
	}
	
	public void stop() throws IOException {
		serverSocket.close();
	}
	
	public void generateKeyPairs() throws NoSuchAlgorithmException {
		KeyPairGenerator kg = KeyPairGenerator.getInstance("DSA");
		kg.initialize(1024);
		KeyPair kp = kg.generateKeyPair();
		privateKey = kp.getPrivate();
		publicKey = kp.getPublic();
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
		this.ledger.verify_transaction(transaction);
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
	
	public void addPublicKey(String accountId, PublicKey pk) {
		pastry.put(accountId, pk);
	}
	
	public void initiateTransaction(double amount, String receiverId, String witnessId , String transactionId)	
	{
		Transaction t = new Transaction();
		t.setTransactionId(transactionId);
		t.setAmount(amount);
		t.setSenderId(this.accountId);
		t.setReceiverId(receiverId);
		t.setWitnessId(witnessId);
		
		boolean check = selectInputTransactions(t);
		if(!check)
			System.out.println("Transaction could not be initiated due to insufficient balance");
		
		else
		{	
			inProgressTransactions.add(t);
	
			clientWriter.sendObject(t.getReceiverId(), t);
			clientWriter.sendObject(t.getWitnessId(), t);
		}	
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

	public Ledger getLedger() {
		return ledger;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public Pastry getPastry() {
		return pastry;
	}

	public boolean selectInputTransactions(Transaction transaction)
	{
		List<String> newList = new ArrayList<String>();
		int cal_amt=0;
		ListIterator<Transaction> itr = this.ledger.getLedger().listIterator();
		
		while(itr.hasNext() && cal_amt < transaction.getAmount())
		{
			Transaction t = itr.next();
			if(t.isValid() && t.getReceiverId()==this.accountId)
			{
				cal_amt+=t.getAmount();
				newList.add(t.getTransactionId());
			}
		}
		
		if(cal_amt < transaction.getAmount())
			return false;
		else
			transaction.setInputTransactions(newList);
		
		return true;
	}

}
