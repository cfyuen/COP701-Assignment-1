package cop701.node;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class LedgerTest {

	@Test
	public void test_add_transaction_to_ledger() {
		Ledger ledger = new Ledger();
		
		Transaction self = new Transaction();
		self.setTransactionId("123");
		self.setAmount(100.0);
		self.setSenderId("a");
		self.setReceiverId("b");
		self.setWitnessId("c");
		self.setValid(true);
		
		ledger.addTransaction(self);
		
		List<Transaction> led = ledger.getLedger();
		
		assertEquals(led.size(), 1);
		
	}

}
