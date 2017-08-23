package cop701.pastry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;



public class PastryListener {
	
	private static final Logger logger = Logger.getLogger(PastryListener.class.getName()); 
	
	private Pastry pastry;
	private Socket socket;
	
	public PastryListener(Pastry pastry, Socket socket) {
		this.pastry = pastry;
		this.socket = socket;
	}

	public void run() {
		
		String s = null;
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			s = in.readLine();	
		} 
		catch (IOException e) {
			System.out.println("Error in reading object from input stream");
			e.printStackTrace();
		}
		
		System.out.println(s);
		
	}

}
