package org.iotcity.iot.framework.core.util.helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;

/**
 * System information util.
 * @author Ardon
 */
public final class SystemHelper {

	// --------------------------- Public fields ----------------------------

	/**
	 * Milliseconds per second.
	 */
	public final static long SECOND_MS = 1000;
	/**
	 * Milliseconds per minute.
	 */
	public final static long MINUTE_MS = 60 * SECOND_MS;
	/**
	 * Milliseconds per hour.
	 */
	public final static long HOUR_MS = 60 * MINUTE_MS;
	/**
	 * Milliseconds per day.
	 */
	public final static long DAY_MS = 24 * HOUR_MS;
	/**
	 * Milliseconds per month (31 days).
	 */
	public final static long MONTH_MS = 31 * DAY_MS;

	// --------------------------- Private fields ----------------------------

	/**
	 * Local address.
	 */
	private static InetAddress localAddress = null;
	/**
	 * The lock for IP.
	 */
	private static Object lock = new Object();

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the operating system default language key.
	 * @return String Language key (e.g. "en_US").
	 */
	public final static String getSystemLang() {
		Locale local = Locale.getDefault();
		return local.getLanguage() + "_" + local.getCountry();
	}

	/**
	 * Test whether the string is local address like "127.0.0.1", "localhost", "0.0.0.0", "0.0.0.0.0.0" (return true if IP is null).
	 * @param ip IP address string.
	 * @return boolean Whether is the local IP address.
	 */
	public final static boolean isLocalhostIP(String ip) {
		if (ip == null || ip.length() == 0) return true;
		if (ip.trim().replaceAll("[0\\.]+", "").length() == 0 || "127.0.0.1".equals(ip) || "localhost".equals(ip)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the local IP address from OS (returns null if an error is encountered.).
	 * @param force Whether force re acquisition.
	 * @return String The local IP address.
	 */
	public final static String getLocalIP(boolean force) {
		if (localAddress == null || force) {
			synchronized (lock) {
				if (localAddress == null || force) {
					try {
						localAddress = getLocalAddress();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return localAddress == null ? null : localAddress.getHostAddress();
	}

	/**
	 * Gets the local MAC from OS (returns null if an error is encountered.).
	 * @param force Whether force re acquisition.
	 * @return String The local MAC.
	 */
	public final static String getLocalMac(boolean force) {
		if (localAddress == null || force) {
			synchronized (lock) {
				if (localAddress == null || force) {
					try {
						localAddress = getLocalAddress();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (localAddress == null) return null;
		try {
			// Gets MAC bytes.
			byte[] mac = NetworkInterface.getByInetAddress(localAddress).getHardwareAddress();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) sb.append("-");
				String s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? "0" + s : s);
			}
			// Return MAC string as: D0-27-88-1F-89-51
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the local IP address at the current time.
	 * @return InetAddress IP address object.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public final static InetAddress getLocalAddress() throws Exception {
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
