package cop701.pastry;

import java.io.IOException;
import java.io.ObjectInputStream;
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
		
		Object inObject = null;
		
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			inObject = in.readObject();
			
		} catch (IOException e) {
			System.out.println("Error in reading object from input stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (inObject instanceof Message) {
			Message m = (Message)inObject;
			logger.info("Pastry message received");
			this.pastry.get(m.getSenderId(),m.getQueryAccountId());
		}
		
	}

}
