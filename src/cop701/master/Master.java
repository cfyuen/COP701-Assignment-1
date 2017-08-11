package cop701.master;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cop701.node.Client;

public class Master {

	private static final int CLIENT_COUNT = 5;
	private static List<Client> clients;
	
	/**
	 * This is the main entry point for the program
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		clients = new ArrayList<Client>();
		
		for (int i=0; i<CLIENT_COUNT; i++) {
			try {
				Client client = new Client(i, 0);
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
				
			} catch (IOException e) {
				System.err.println("There is an exception of starting client");
				e.printStackTrace();
			}
		}
		
		clients.get(0).hello();
		
		for (int i=0; i<CLIENT_COUNT; i++) {
			for (int j=0; j<CLIENT_COUNT; j++) {
				Client otherClient = clients.get(j);
				clients.get(i).addNodeIdentity(otherClient.getAccount(), otherClient.getAddress());
			}
		}
		
		clients.get(0).initiateTransaction(2.0,"2","3","10");
	}
	
}
