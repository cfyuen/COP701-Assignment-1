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
		System.out.println("abcd");
		JFrame f= new JFrame("client"+port);
		JButton b1= new JButton("Say Hello");
		JButton b2= new JButton("Get Neighbour");
		JButton b3= new JButton("Send Transaction");
		JTextField t1= new JTextField("");
		JTextField t2= new JTextField("");
		JTextField t3= new JTextField("");
		JLabel l1= new JLabel("Receiver Account");
		JLabel l2= new JLabel("Amount");
		JLabel l3= new JLabel("Witness");
		b1.setBounds(50,50,95,30);
		b2.setBounds(200,50,95,30);
		l1.setBounds(10, 100, 100, 30);
		t1.setBounds(210,100,100,30);
		l2.setBounds(10, 150, 100, 30);
		t2.setBounds(210,150,100,30);
		l3.setBounds(10, 200, 100, 30);
		t3.setBounds(210,200,100,30);
		b3.setBounds(200,250,95,30);
		f.add(b1);
		f.add(b2);
		f.add(b3);
		f.add(t1);
		f.add(t2);
		f.add(t3);
		f.add(l1);
		f.add(l2);
		f.add(l3);
		f.setSize(500, 500);
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