package cop701.node;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LedgerTest {

	@Test
	public void test_add_transaction_to_ledger() {
		Ledger ledger = new Ledger();
		
		Transaction self = new Transaction();
		self.transactionId="N1T0123";
		self.amount = 100.0;
		self.senderId="a";
		self.receiverId="b";
		self.witnessId="c";
		self.inputTransactions=null;
		self.valid=true;
		
		ledger.addTransaction(self);
		
		Transaction self1 = new Transaction();
		self1.transactionId="N3T0125";
		self1.amount = 100.0;
		self1.senderId="c";
		self1.receiverId="b";
		self1.witnessId="a";
		self1.inputTransactions=null;
		self1.valid=true;
		
		ledger.addTransaction(self1);
		ArrayList<String> list=new ArrayList<String>();
		  list.add("N1T0123"); 
		  list.add("N3T0125");  
		Transaction verify = new Transaction();
		verify.transactionId="N2T0127";
		verify.amount = 150.0;
		verify.senderId="b";
		verify.receiverId="a";
		verify.witnessId="c";
		verify.inputTransactions=list;
		verify.valid=true;
		
		boolean verified=ledger.verify_transaction(verify);
		
		assertEquals(true, verified);
		
		List<Transaction> led = ledger.getLedger();
		assertEquals(4,led.size());
		assertEquals("N2T0128",led.get(3).getTransactionId());
	}

}
