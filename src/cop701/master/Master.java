package cop701.master;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogManager;

import cop701.node.Address;
import cop701.node.Client;
import cop701.node.Transaction;

public class Master {

	private static final int CLIENT_COUNT = 7;
	private static List<Client> clients;
	
	/**
	 * This is the main entry point for the program
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */

	public static void main(String[] args) throws UnknownHostException, IOException {
	
		setup();
		
		clients = new ArrayList<Client>();
		
		for (int i=0; i<CLIENT_COUNT; i++) {
			try {
				int port = 0;
				if (i == 0) port = 42000;
				String ip = InetAddress.getLocalHost().getHostAddress();
				String id = Integer.toString(i+1, 4);
				while (id.length() < 4) id = "0" + id;
				Client client = new Client(id, ip, port);
				client.setBootstrapAddress(new Address(ip, 42000));
				clients.add(client);
				
				new Thread(new Runnable() {
					   public void run() {
					       try {
							client.start();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					   }
					}).start();
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			} catch (IOException e) {
				System.err.println("There is an exception of starting client");
				e.printStackTrace();
			}
		}
		
		//clients.get(0).hello();

		//addNodeIdentityMap();
		//initializeLedger();

		//clients.get(0).initiateTransaction(2.0,"0002","0003");
		

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
	
	private static void addNodeIdentityMap() {
		for (int i=0; i<CLIENT_COUNT; i++) {
			for (int j=0; j<CLIENT_COUNT; j++) {
				Client otherClient = clients.get(j);
				clients.get(i).addNodeIdentity(otherClient.getAccount(), otherClient.getAddress());
				clients.get(i).addPublicKey(otherClient.getAccount(), otherClient.getPublicKey());
			}
		}
	}
	
	private static void initializeLedger() {
		// Add transaction j to j 100.0 into node i's ledger
		for (int i=0; i<CLIENT_COUNT; i++) {
			for (int j=0; j<CLIENT_COUNT; j++) {
				Client client = clients.get(j);
				Transaction t = new Transaction();
				t.setTransactionId("N" + j + "T0");
				t.setAmount(100.0);
				t.setSenderId(client.getAccount());
				t.setReceiverId(client.getAccount());
				t.setWitnessId(client.getAccount());
				t.setValid(true);
				
				clients.get(i).getLedger().addTransaction(t);
			}
		}
	}
	
}
