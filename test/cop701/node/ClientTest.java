package cop701.node;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ClientTest {

	@Test
	public void test_valid_input_transaction() throws IOException {
		
		Client client = new Client("0");
		for(int i=1;i<=10;++i)
		{
			Transaction t = new Transaction();
			t.setTransactionId("T"+i);
			t.setAmount(10.0 * i);
			t.setSenderId(client.getAccount());
			t.setReceiverId(client.getAccount());
			t.setWitnessId(client.getAccount());
			t.setValid(true);
			
			client.getLedger().addTransaction(t);
		}
		
		Transaction t = new Transaction();
		t.setTransactionId("T"+11);
		t.setAmount(110.0);
		t.setSenderId(client.getAccount());
		t.setReceiverId(client.getAccount());
		t.setWitnessId(client.getAccount());
		t.setValid(true);
		
		boolean b = client.selectInputTransactions(t);
		
		assertEquals(true,b);
		int k=1;
		for (String str : t.getInputTransactions())
		{	
			assertEquals(str,"T"+k);
			k++;
		}
	}

public void test_invalid_input_transaction() throws IOException {
		
		Client client = new Client("0");
		for(int i=1;i<=10;++i)
		{
			Transaction t = new Transaction();
			t.setTransactionId("T"+i);
			t.setAmount(10.0 * i);
			t.setSenderId(client.getAccount());
			t.setReceiverId(client.getAccount());
			t.setWitnessId(client.getAccount());
			t.setValid(true);
			
			client.getLedger().addTransaction(t);
		}
		
		Transaction t = new Transaction();
		t.setTransactionId("T"+11);
		t.setAmount(560.0);
		t.setSenderId(client.getAccount());
		t.setReceiverId(client.getAccount());
		t.setWitnessId(client.getAccount());
		t.setValid(true);
		
		boolean b = client.selectInputTransactions(t);
		
		assertEquals(false,b);
	
	}
}
