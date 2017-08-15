package cop701.node;
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
	
	public boolean verify_transaction(Transaction transaction){
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
								return false; //useless transaction	
							}
					}
					else return false; //transaction is invalid
				}
			}
			if(!flag)
				return false; //transaction not found
		}
		if(calculated_amount >= transaction.getAmount())
		{
			addTransaction(transaction);
			double difference=calculated_amount-transaction.getAmount();
			Transaction self= new Transaction();
			int id_length=transaction.getTransactionId().length();
			int transaction_number=Integer.parseInt(transaction.getTransactionId().substring(id_length-4));
			self.setTransactionId(transaction.getTransactionId().substring(0,id_length-4)+(transaction_number+1));
			self.setAmount(difference);
			self.setSenderId(transaction.getSenderId());
			self.setReceiverId(transaction.getSenderId());
			self.setWitnessId(transaction.getWitnessId());
			self.setValid(true);
			addTransaction(self);
		}
		invalidateInputTransactions(transaction);
		return true;
	}
	public void addTransaction(Transaction transaction){
		ledger.add(transaction);
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
	public List<Transaction> getLedger() {
		return ledger;
	}
	
}