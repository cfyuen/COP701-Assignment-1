package cop701.node;

import static org.junit.Assert.*;

import java.util.ArrayList;
//import java.util.List;

import org.junit.Test;

public class LedgerTest {

	@Test
	public void test_add_transaction_to_ledger() {
		Ledger ledger = new Ledger();
		
		Transaction self = new Transaction();

		self.setTransactionId("N1T123");
		self.setAmount(100.0);
		self.setSenderId("a");
		self.setReceiverId("b");
		self.setWitnessId("c");
		self.setValid(true);

		
		ledger.addTransaction(self);
		
		Transaction self1 = new Transaction();
		self1.setTransactionId("N3T125");
		self1.setAmount(100.0);
		self1.setSenderId("c");
		self1.setReceiverId("b");
		self1.setWitnessId("a");
		self1.setInputTransactions(null);
		self1.setValid(true);
		
		ledger.addTransaction(self1);
		
		Transaction self2 = new Transaction();
		self2.setTransactionId("N3T129");
		self2.setAmount(100.0);
		self2.setSenderId("c");
		self2.setReceiverId("a");
		self2.setWitnessId("b");
		self2.setInputTransactions(null);
		self2.setValid(true);
		
		ledger.addTransaction(self2);
		
		ArrayList<String> list=new ArrayList<String>();
		  list.add("N1T123"); 
		  list.add("N3T125");  
		  //list.add("N3T129");
		Transaction verify = new Transaction();
		verify.setTransactionId("N2T127");
		verify.setAmount(350.0);
		verify.setSenderId("b");
		verify.setReceiverId("a");
		verify.setWitnessId("c");
		verify.setInputTransactions(list);
		verify.setValid(true);
		
		boolean verified=ledger.verify_transaction(verify);
		
		assertEquals(false, verified);
		Transaction verify1 = new Transaction();
		verify1.setTransactionId("N2T129");
		verify1.setAmount(150.0);
		verify1.setSenderId("b");
		verify1.setReceiverId("a");
		verify1.setWitnessId("c");
		verify1.setInputTransactions(list);
		verify1.setValid(true);
		
		boolean verified1=ledger.verify_transaction(verify1);
		assertEquals(true, verified1);
		//List<Transaction> led = ledger.getLedger();
		//assertEquals(4,led.size());
		//assertEquals("N2T128",led.get(3).getTransactionId());
	}

}
