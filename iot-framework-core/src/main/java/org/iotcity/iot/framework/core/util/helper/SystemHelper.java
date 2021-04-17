package org.iotcity.iot.framework.core.util.helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;

/**
 * System information util
 * @author Ardon
 */
public final class SystemHelper {

	// --------------------------- Public fields ----------------------------

	/**
	 * Milliseconds per second
	 */
	public static final long SECOND_MS = 1000;
	/**
	 * Milliseconds per minute
	 */
	public static final long MINUTE_MS = 60 * SECOND_MS;
	/**
	 * Milliseconds per hour
	 */
	public static final long HOUR_MS = 60 * MINUTE_MS;
	/**
	 * Milliseconds per day
	 */
	public static final long DAY_MS = 24 * HOUR_MS;
	/**
	 * Milliseconds per month (31 days)
	 */
	public static final long MONTH_MS = 31 * DAY_MS;

	// --------------------------- Private fields ----------------------------

	/**
	 * Local IP address
	 */
	private static String localIP = null;
	/**
	 * The lock for IP
	 */
	private static Object lock = new Object();

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the operating system default language key
	 * @return String Language key (e.g. "en_US")
	 */
	public static String getSystemLang() {
		Locale local = Locale.getDefault();
		return local.getLanguage() + "_" + local.getCountry();
	}

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
	 * @throws Exception Throw an exception when an error is encountered
	 */
	public static String getLocalIP(boolean force) throws Exception {
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
	 * @throws Exception Throw an exception when an error is encountered
	 */
	public static InetAddress getLocalAddress() throws Exception {
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
	}

}
