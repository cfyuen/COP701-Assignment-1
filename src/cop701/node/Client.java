package cop701.node;

import cop701.common.Util;
import cop701.node.ClientUI;
import cop701.pastry.Pastry;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyPair;
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
	
	public static final int LISTENER_PORT = 42000;
	
	private String accountId;
	private Address address;
	private ServerSocket serverSocket;
	private List<Transaction> inProgressTransactions;
	private Integer transactionCounter;
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
	public Client() throws IOException {
		this(null);
	}
	
	public Client(String id) throws IOException {
		this(id, null, LISTENER_PORT);
	}
	
	public Client(String id, String ip, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		
		if (ip == null) ip = Util.getIpAddress();
		if (id == null) this.accountId = Util.generateAccountId((int)Math.pow(2, Pastry.B), Pastry.L, ip);
		else this.accountId = id;
			
		this.address = new Address(ip, serverSocket.getLocalPort());
		nodesMap.put(accountId, address);
		inProgressTransactions = new ArrayList<Transaction>();
		transactionCounter = 0;
		ledger = new Ledger();
		initializeLedger();

		KeyPair kp = Util.generateKeyPairs();
		privateKey = kp.getPrivate();
		publicKey = kp.getPublic();
	
		pastry = new Pastry(accountId,nodesMap,publicKey);
		new Thread(new Runnable() {
			   public void run() {
			       try {
					pastry.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   }
			}).start();		
	}
	
	public static void main(String[] args) throws IOException {
		setup();
		
		Client client = new Client();
		client.start();
	}
	
	private static void setup() {
		Locale.setDefault(Locale.US);
		/*
		try {
			InputStream is = Client.class.getResourceAsStream("logging.properties");
			is = Client.class.getResourceAsStream("ClientUI.java");
			LogManager.getLogManager().readConfiguration(is);
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
		*/

	}
	
	public void start() throws IOException {
		//ClientUI cui=new ClientUI();
		//cui.clientUI(address.getPort(),this);
		
		clientWriter = new ClientWriter(this);
		
		ClientController clientController = new ClientController(this);
		new Thread(new Runnable() {
			   public void run() {
			       clientController.start();
			   }
			}).start();
		
		System.out.println("[" + accountId + "] Listening on " + address.toString());
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
		this.ledger.verifyTransaction(transaction);
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
	
	public void initiateTransaction(double amount, String receiverId, String witnessId) {
		String transactionId = "N" + accountId + "T" + String.valueOf(transactionCounter);
		transactionCounter += 2;
		initiateTransaction(amount, receiverId, witnessId, transactionId);
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

	public double getTotalAmountOf(String accountId) {
		return ledger.getTotalAmountOf(accountId);		
	}
	
	public String getLedgerHashCode() {
		return ledger.getHashCode();
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
	
	private void initializeLedger() {
		if (accountId.equals("0001")) {
			Transaction t = new Transaction();
			t.setTransactionId("N" + accountId + "T" + String.valueOf(transactionCounter));
			transactionCounter += 2;
			t.setAmount(10000.0);
			t.setSenderId(accountId);
			t.setReceiverId(accountId);
			t.setWitnessId(accountId);
			t.setValid(true);
			
			ledger.addTransaction(t);
		}
	}
	
	public void setBootstrapAddress(Address bootstrapAddress) {
		pastry.setBootstrapAddress(bootstrapAddress);
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
