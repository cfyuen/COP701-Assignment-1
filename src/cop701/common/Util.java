package cop701.common;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Util {

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
	
	public static String generateAccountId(int b, int l, String ip) {
		String[] ipSplit = ip.split("\\.");
		Integer hash = Integer.valueOf(ipSplit[1])*256*256 + Integer.valueOf(ipSplit[2])*256 + Integer.valueOf(ipSplit[3]);
		String baseB = Integer.toString(hash, b);
		while (baseB.length() < l) {
			baseB = "0" + baseB;
		}
		return baseB.substring(baseB.length() - l); 
	}
	
}
