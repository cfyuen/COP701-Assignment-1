package cop701.common;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.logging.Logger;

public class Util {
	
	private static final Logger logger = Logger.getLogger(Util.class.getName()); 
	
	public static KeyPair generateKeyPairs() {
		KeyPairGenerator kg = null;
		try {
			kg = KeyPairGenerator.getInstance("DSA");
			kg.initialize(1024);
			KeyPair kp = kg.generateKeyPair();
			return kp;
		} catch (NoSuchAlgorithmException e) {
			logger.warning("Error generating public / private keys");
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getIpAddress() throws UnknownHostException, SocketException {
		// Get IP address (linux)
		String ipAddr = null;
		Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
		while (niEnum.hasMoreElements()) {
			NetworkInterface ni = niEnum.nextElement();
			if (!ni.getName().equals("lo")) {
				Enumeration<InetAddress> iaEnum = ni.getInetAddresses();
				while (iaEnum.hasMoreElements()) {
					InetAddress ia = iaEnum.nextElement();
					if (ia instanceof Inet4Address) {
						System.out.println(ni.getName() + " " + ia.getHostAddress());
						ipAddr = ia.getHostAddress();
						break;
					}
				}
			}
		}
		if (ipAddr == null) {
			// Windows fallback
			ipAddr = InetAddress.getLocalHost().getHostAddress();
		}
		return ipAddr;
	}
	
}
