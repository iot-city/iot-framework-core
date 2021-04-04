package org.iotcity.iot.framework.core.util.helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * System information util
 * @author Ardon
 */
public class SystemHelper {

	/**
	 * Local IP address
	 */
	private static String localIP = null;
	/**
	 * The lock for IP
	 */
	private static Object lock = new Object();

	/**
	 * Test whether the string is local address (return true if IP is null)
	 * @param ip IP address string
	 * @return boolean Whether is the local IP address
	 */
	public static boolean isLocalhostIP(String ip) {
		if (ip == null || ip.length() == 0) return true;
		if (ip.trim().replaceAll("[0\\.]+", "").length() == 0 || "127.0.0.1".equals(ip) || "localhost".equals(ip)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the local IP address from OS
	 * @param force Whether force re acquisition
	 * @return String The local IP address
	 */
	public static String getLocalIP(boolean force) {
		if (localIP == null || force) {
			synchronized (lock) {
				if (localIP == null || force) {
					InetAddress ip = getLocalAddress();
					if (ip != null) localIP = ip.getHostAddress();
				}
			}
		}
		return localIP;
	}

	/**
	 * Get the local IP address at the current time
	 * @return InetAddress IP address object
	 */
	public static InetAddress getLocalAddress() {
		try {
			InetAddress iadd = null;
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (inetAddr.isLoopbackAddress()) continue;
					if (inetAddr.isSiteLocalAddress()) {
						return inetAddr;
					} else if (iadd == null) {
						iadd = inetAddr;
					}
				}
			}
			if (iadd != null) return iadd;
			return InetAddress.getLocalHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
