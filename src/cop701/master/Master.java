package cop701.master;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cop701.node.Client;

public class Master {

	private static final int CLIENT_COUNT = 5;
	private static final int INF = 80000;
	private static List<Client> clients;
	
	/**
	 * This is the main entry point for the program
	 * @param args
	 */
	public static void main(String[] args) {
		clients = new ArrayList<Client>();
		
		for (int i=0; i<CLIENT_COUNT; i++) {
			try {
				Client client = new Client(0);
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
		clients.get(2).getNeighbor();

	}
	
	public static int getNearbyNode(int port) {
		int minDiff = INF;
		int nearbyPort = 0;
		for (int i=0; i<clients.size(); i++) {
			int otherPort = clients.get(i).getPort();
			if (otherPort != port && minDiff > Math.abs(port - otherPort)) {
				minDiff = Math.abs(port - otherPort);
				nearbyPort = otherPort;
			}
		}
		return nearbyPort;
	}

}
