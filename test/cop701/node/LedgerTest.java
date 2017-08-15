package cop701.node;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class LedgerTest {

	@Test
	public void test_add_transaction_to_ledger() {
		Ledger ledger = new Ledger();
		
		Transaction self = new Transaction();
		self.transactionId="123";
		self.amount = 100.0;
		self.senderId="a";
		self.receiverId="b";
		self.witnessId="c";
		self.inputTransactions=null;
		self.valid=true;
		
		ledger.addTransaction(self);
		
		List<Transaction> led = ledger.getLedger();
		
		assertEquals(led.size(), 1);
		
	}

}
