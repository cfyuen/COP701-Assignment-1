package cop701.node;
import cop701.node.Client;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JFrame;
public class ClientUI
{ 

	public void clientUI(int port,Client client)
		{
		JFrame f= new JFrame("client UI");
		JButton b1= new JButton("Say Hello");
		JButton b2= new JButton("Get Neighbour");
		b1.setBounds(50,100,95,30);
		b2.setBounds(200,100,95,30);
		f.add(b1);
		f.add(b2);
		f.setSize(400, 400);
		f.setLayout(null);
		f.setVisible(true);
		 b1.addActionListener(new ActionListener(){  
			 public void actionPerformed(ActionEvent e){  
			             client.hello(); 
			         }  
			     });
		 b2.addActionListener(new ActionListener(){  
			 public void actionPerformed(ActionEvent e){  
			             //client.getNeighbor(); 
			         }  
			     });
		}
}