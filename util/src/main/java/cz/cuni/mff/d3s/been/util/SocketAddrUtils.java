package cz.cuni.mff.d3s.been.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Utilities to work with socket addresses
 *
 * @author darklight
 * @since 7/26/14.
 */
public class SocketAddrUtils {

	/**
	 * Create a string representation of a socket address
	 *
	 * @param sockAddr The socket address
	 *
	 * @return The string representation of that socket address
	 */
	public static String sockAddrToString(InetSocketAddress sockAddr) {
		return new StringBuilder()
				.append('[').append(sockAddr.getHostName()).append(']')
				.append(':')
				.append(sockAddr.getPort()).toString();
	}

	/**
	 * Create a string representation of multiple socket addresses
	 *
	 * @param sockAddrs Socket addresses to include
	 *
	 * @return A comma-separated list of socket addresses
	 */
	public static String sockAddrsToString(Collection<InetSocketAddress> sockAddrs) {
		final StringBuilder addrs = new StringBuilder();
		boolean first = true;
		for (InetSocketAddress sockAddr: sockAddrs) {
			if (!first) addrs.append(',');
			first = false;
			addrs.append(sockAddrToString(sockAddr));
		}
		return addrs.toString();
	}

	/**
	 * Parse the string representation of a socket address into a socket address.
	 * Verify that the address is reachable. If not, return <code>null</code>.
	 *
	 * @param sockAddrString The string representation of the socket address
	 * @param reachTimeout The timeout (in milliseconds) that is applied before the address is declared as unreachable
	 *
	 * @return A reachable socket address
	 *
	 * @throws java.net.UnknownHostException When the network host cannot be parsed correctly
	 * @throws java.lang.NumberFormatException When the port is not an integer
	 */
	public static InetSocketAddress parseReachableSockAddr(String sockAddrString, int reachTimeout) throws UnknownHostException {
		final int lbr = sockAddrString.indexOf('[');
		final int rbr = sockAddrString.indexOf(']');
		final String host = sockAddrString.substring(lbr + 1, rbr);
		final InetAddress inetAddr = InetAddress.getByName(host);
		try {
			if (!inetAddr.isReachable(reachTimeout)) return null;
		} catch (IOException e) {
			return null;
		}
		final int port = Integer.parseInt(sockAddrString.substring(rbr + 2)); // also skip the double dot
		return new InetSocketAddress(inetAddr, port);
	}

	/**
	 * Parse the comma-separated list of socket address string representations.
	 * Verify each address for reachability. Return the first reachable address.
	 *
	 * @param sockAddrs Comma-separated list of socket address string representations
	 * @param reachTimeout The timeout (in milliseconds) that is applied before the address is declared as unreachable
	 *
	 * @return The first reachable socket address from the list, or <code>null</code> if no address is provided/reachable
	 */
	public static InetSocketAddress getFirstReachableAddress(String sockAddrs, int reachTimeout) throws UnknownHostException {
		StringTokenizer addrTok = new StringTokenizer(sockAddrs, ",");
		while(addrTok.hasMoreTokens()) {
			final InetSocketAddress sockAddr = parseReachableSockAddr(addrTok.nextToken().trim(), reachTimeout);
			if (sockAddr != null) return sockAddr;
		}
		return null;
	}
}
