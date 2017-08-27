package cop701.node;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import cop701.node.Transaction;

public class Ledger {
	private List<Transaction> ledger;
	
	public Ledger()
	{
		ledger= new ArrayList<Transaction>();
	}
	
	public boolean verifyTransaction(Transaction transaction){
		//TODO: verify if input transaction does not contain more than 1 transaction with same transaction id 
		String p;
		double calculated_amount=0;
		String sender=transaction.getSenderId();
		boolean flag = false;
		ListIterator<String> itr=transaction.getInputTransactions().listIterator();
		while(itr.hasNext())
		{
			p=itr.next();
			ListIterator<Transaction> itr_ledger=ledger.listIterator();
			while(itr_ledger.hasNext())
			{
				flag=false;
				Transaction t=itr_ledger.next();
				if(t.getTransactionId().equals(p))
				{
					if(t.isValid())
					{
						
							if(t.getReceiverId().equals(sender))
							{
								calculated_amount +=t.getAmount();
								flag=true;
								break;
							}
							else
							{
								System.out.println(transaction.getTransactionId()+":Input transaction contains a useless Transaction");
								return false; //useless transaction	
							}
					}
					else
						{
							System.out.println(transaction.getTransactionId()+"Input transaction contains an invalid transaction");
							return false; //transaction is invalid
						}
				}
			}
			if(!flag)
			{
				System.out.println(transaction.getTransactionId()+" Transaction wasn't found in the ledger");
				return false; //transaction not found
			}	
		}

		if(calculated_amount > transaction.getAmount())
		{
			addTransaction(transaction);
			double difference=calculated_amount-transaction.getAmount();
			Transaction self= new Transaction();
			
			String transaction_parts[]=transaction.getTransactionId().split("T");
			int transaction_number=Integer.parseInt(transaction_parts[1]);
			transaction_number++;
			self.setTransactionId(transaction_parts[0]+"T"+transaction_number);
			//int transaction_number=Integer.parseInt(transaction.getTransactionId().substring(id_length-4));
			//self.setTransactionId(transaction.getTransactionId().substring(0,id_length-4)+(transaction_number+1));
			self.setAmount(difference);
			self.setSenderId(transaction.getSenderId());
			self.setReceiverId(transaction.getSenderId());
			self.setWitnessId(transaction.getWitnessId());
			self.setValid(true);
			addTransaction(self);
		}
		else if(calculated_amount < transaction.getAmount())
		{
			System.out.println(transaction.getTransactionId()+"Total amount insufficient for the transaction");
			return false;
		}
		else
		{
			addTransaction(transaction);
		}
		invalidateInputTransactions(transaction);
		return true;
	}
	
	public void addTransaction(Transaction transaction){
		ledger.add(transaction);
		System.out.println(transaction.getTransactionId()+"Successfully Added");
	}
	
	public void invalidateInputTransactions(Transaction transaction)
	{
		ListIterator<String> itr=transaction.getInputTransactions().listIterator();
		String p;
		while(itr.hasNext())
		{
			p=itr.next();
			ListIterator<Transaction> itr_ledger=ledger.listIterator();
			while(itr_ledger.hasNext())
			{
				Transaction t=itr_ledger.next();
				if(t.getTransactionId().equals(p))
				{
					if(t.isValid())
					{
						t.setValid(false);;
					}
				}
			}
		}
	}
	
	public double getTotalAmountOf(String accountId) {
		Double amount = 0.0;
		ListIterator<Transaction> itr_ledger=ledger.listIterator();
		while(itr_ledger.hasNext()) {
			Transaction t=itr_ledger.next();
			if(t.isValid() && t.getReceiverId().equals(accountId))
				amount +=t.getAmount();
		}
		return amount;
	}
	
	public String getHashCode() {
		// Hashing using SHA 256
		String hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			ByteArrayOutputStream byteos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(byteos);
			oos.writeObject(this.ledger);
			oos.close();
			
			md.update(byteos.toByteArray());
			byte[] bytes = md.digest();
			
			StringBuilder sbtohex = new StringBuilder();
		    for(byte b : bytes) {
		    	sbtohex.append(String.format("%02x", b));
		    }
		    hash = sbtohex.toString();
			
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		return hash;
		
	}
	
	public List<Transaction> getLedger() {
		return ledger;
	}
	
}