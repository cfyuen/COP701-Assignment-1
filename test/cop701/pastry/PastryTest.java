package cop701.pastry;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cop701.common.Util;
import cop701.node.Address;

public class PastryTest {

	@Test
	public void testLeafSetCase() throws IOException {
		String accountId = "0002";
		Map<String, Address> nodesMap = new HashMap<String, Address>();
		Pastry pastry = new Pastry(accountId, nodesMap);
		
		Map<String, PublicKey> pkMap = pastry.getPkMap();
		putPkMap(pkMap, "0001");
		putPkMap(pkMap, "0002");
		putPkMap(pkMap, "0003");
		pastry.setPkMap(pkMap);
		
		List<String> leftls = new ArrayList<String>();
		leftls.add("0001");
		List<String> rightls = new ArrayList<String>();
		rightls.add("0003");
		
		pastry.setLeftLeafSet(leftls);
		pastry.setRightLeafSet(rightls);
		
		PublicKey pk;
		pk = pastry.get(accountId, "0001");
		assertEquals(pkMap.get("0001"), pk);
		
		pk = pastry.get(accountId, "0002");
		assertEquals(pkMap.get("0002"), pk);
		
		pk = pastry.get(accountId, "0003");
		assertEquals(pkMap.get("0003"), pk);
		
	}
	
	@Test
	public void testRoutingTableCase() throws IOException {
		String accountId = "0002";
		Map<String, Address> nodesMap = new HashMap<String, Address>();
		nodesMap.put("1023", new Address("0.0.0.0", 12345));
		
		Pastry pastry = new Pastry(accountId, nodesMap);
		pastry.start();
		
		Map<String, PublicKey> pkMap = pastry.getPkMap();
		putPkMap(pkMap, "0001");
		putPkMap(pkMap, "0002");
		putPkMap(pkMap, "0003");
		pastry.setPkMap(pkMap);
		
		List<String> leftls = new ArrayList<String>();
		leftls.add("0001");
		List<String> rightls = new ArrayList<String>();
		rightls.add("0003");
		
		pastry.setLeftLeafSet(leftls);
		pastry.setRightLeafSet(rightls);
		
		String[][] rt = pastry.getRoutingTable();
		rt[0][1] = "1023";
		pastry.setRoutingTable(rt);
		
		PublicKey pk;
		pk = pastry.get(accountId, "1023");
		assertNull(pk);
		
		pk = pastry.get(accountId, "1233");
		assertNull(pk);
	}

	private void putPkMap(Map<String, PublicKey> pkMap, String account) {
		KeyPair kp = Util.generateKeyPairs();
		PublicKey pk = kp.getPublic();
		pkMap.put(account, pk);
	}
	
}
