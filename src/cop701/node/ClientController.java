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
				else if (token[0].equals("hash")) {
					hash(token);
				}
				else if (token[0].equals("print")) {
					print(token);
				}
				else {
					System.out.println("Unrecognized commands");
				}
			}
		}
	}
	
	public void help() {
		System.out.println("[Controller] Help for " + client.getAccount());
		System.out.println("send [receiver account] [witness account] [amount] - Initiate transaction");
		System.out.println("query [account] - Query the amount of bitcoins for account");
		System.out.println("hash - Print the hash code of the ledger of the current node");
		System.out.println("print [ledger(lg)|routingtable(rt)|leafset(ls)|nodemap(nm)|publickey(pk)] - Print the ledger / routing table / leaf set of the current node");
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
	
	public void hash(String[] token) {
		try {
			String hash = client.getLedgerHashCode();
			System.out.println("Account " + client.getAccount() + " ledger's hash code = " + hash);
		} catch (Exception e) {
			System.out.println("Please follow the help example");
		}
	}
	
	public void print(String[] token) {
		try {
			if (token[1].equals("ledger") || token[1].equals("lg")) {
				client.printLedger();
			}
			else if (token[1].equals("routingtable") || token[1].equals("rt")) {
				client.printRoutingTable();
			}
			else if (token[1].equals("leafset") || token[1].equals("ls")) {
				client.printLeafSet();
			}
			else if (token[1].equals("nodemap") || token[1].equals("nm")) {
				client.printNodesMap();
			}
			else if (token[1].equals("publickey") || token[1].equals("pk")) {
				client.printPublicKeyMap();
			}
			else {
				System.out.println("Unknown print command");
			}
		} catch (Exception e) {
			System.out.println("Please follow the help example");
		}
	}
	
}
