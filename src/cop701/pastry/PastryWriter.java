package cop701.pastry;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import cop701.node.Address;
import cop701.node.ClientWriter;

public class PastryWriter {
	
	private static final Logger logger = Logger.getLogger(ClientWriter.class.getName());
	
	private Pastry pastry;
	
	public PastryWriter(Pastry pastry)
	{
		this.pastry = pastry;
	}

	public void sendKey(Address address, String key) {
		Socket recipientSocket;
		PrintWriter out;
		
		try {
			recipientSocket = new Socket(address.getIp(),address.getPort());
			out = new PrintWriter(recipientSocket.getOutputStream(),true);
			out.println(key);
			recipientSocket.close();
		}
		catch (IOException e) {
			logger.warning("PastryWriter has some issues while writing to socket");
			e.printStackTrace();
		}
	}
}
