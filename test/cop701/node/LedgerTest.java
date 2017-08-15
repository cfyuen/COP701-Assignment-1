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
		ArrayList<String> list=new ArrayList<String>();
		  list.add("N1T123"); 
		  list.add("N3T125");  
		Transaction verify = new Transaction();
		verify.setTransactionId("N2T127");
		verify.setAmount(150.0);
		verify.setSenderId("b");
		verify.setReceiverId("a");
		verify.setWitnessId("c");
		verify.setInputTransactions(list);
		verify.setValid(true);
		
		boolean verified=ledger.verify_transaction(verify);
		
		assertEquals(true, verified);
		
		List<Transaction> led = ledger.getLedger();
		assertEquals(4,led.size());
		assertEquals("N2T128",led.get(3).getTransactionId());
	}

}
