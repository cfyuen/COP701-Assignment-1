package cop701.node;

import java.util.Scanner;

public class ClientController {

	private Client client;
	private Scanner sc;
	
	public ClientController(Client client) {
		this.client = client;
		sc = new Scanner(System.in);
	}
	
	public void start() {
		while (true) {
			System.out.println("[Controller] Please enter command (type \"help\" for help)");
			String cmd = sc.nextLine();
			String[] token = cmd.split("\\s+");
			if (token.length > 0) {
				if (token[0].equals("help")) {
					help();
				}
				else if (token[0].equals("send")) {
					send(token);
				}
				else if (token[0].equals("query")) {
					query(token);
				}
				else {
					System.out.println("Unrecognized commands");
				}
			}
		}
	}
	
	public void help() {
		System.out.println("[Controller] Help");
		System.out.println("send [receiver account] [witness account] [amount] - Initiate transaction");
		System.out.println("query [account] - Query the amount of bitcoins for account");
		System.out.println();
	}
	
	public void send(String[] token) {
		try {
			Double amount = Double.valueOf(token[3]);
			client.initiateTransaction(amount, token[1], token[2]);
		} catch (Exception e) {
			System.out.println("Please follow the help example");
		}
	}
	
	public void query(String[] token) {
		try {
			Double amount = client.getTotalAmountOf(token[1]);
			System.out.println("Account " + token[1] + " has a total of " + String.valueOf(amount) + " bitcoins.");
		} catch (Exception e) {
			System.out.println("Please follow the help example");
		}
	}
	
}
