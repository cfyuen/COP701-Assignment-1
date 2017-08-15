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
		String sender=transaction.senderId;
		boolean flag = false;
		ListIterator<String> itr=transaction.inputTransactions.listIterator();
		while(itr.hasNext())
		{
			p=itr.next();
			ListIterator<Transaction> itr_ledger=ledger.listIterator();
			while(itr_ledger.hasNext())
			{
				flag=false;
				Transaction t=itr_ledger.next();
				if(t.transactionId.equals(p))
				{
					if(t.valid==true)
					{
						
							if(t.receiverId.equals(sender))
							{
								calculated_amount +=t.amount;
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
		if(calculated_amount > transaction.amount)
		{
			addTransaction(transaction);
			double difference=calculated_amount-transaction.amount;
			Transaction self= new Transaction();
			int id_length=transaction.transactionId.length();
			int transaction_number=Integer.parseInt(transaction.transactionId.substring(id_length-4));
			self.transactionId=transaction.transactionId.substring(0,id_length-4)+(transaction_number+1);
			self.amount=difference;
			self.senderId=transaction.senderId;
			self.receiverId=transaction.senderId;
			self.witnessId=transaction.witnessId;
			self.inputTransactions=null;
			self.valid=true;
			addTransaction(self);
		}
		else if(calculated_amount < transaction.amount)
		{
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
	}
	public void invalidateInputTransactions(Transaction transaction)
	{
		ListIterator<String> itr=transaction.inputTransactions.listIterator();
		String p;
		while(itr.hasNext())
		{
			p=itr.next();
			ListIterator<Transaction> itr_ledger=ledger.listIterator();
			while(itr_ledger.hasNext())
			{
				Transaction t=itr_ledger.next();
				if(t.transactionId.equals(p))
				{
					if(t.valid==true)
					{
						t.valid=false;
					}
				}
			}
		}
	}
	public List<Transaction> getLedger() {
		return ledger;
	}
	
}