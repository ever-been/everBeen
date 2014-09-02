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

	private static final char LEFT_DELIM = '(';
	private static final char RIGHT_DELIM = ')';
	private static final char PORT_SEPARATOR = ':';
	private static final String LIST_SEPARATOR = ",";

	/**
	 * Create a string representation of a socket address
	 *
	 * @param sockAddr The socket address
	 *
	 * @return The string representation of that socket address
	 */
	public static String sockAddrToString(InetSocketAddress sockAddr) {
		final String hostName = stripAddrScope(sockAddr.getHostName());
		return new StringBuilder()
				.append(LEFT_DELIM).append(hostName).append(RIGHT_DELIM)
				.append(PORT_SEPARATOR)
				.append(sockAddr.getPort()).toString();
	}

	/**
	 * Strip the scope from a hostname (ipv6)
	 *
	 * @param addr Address whose scope should be stripped
	 *
	 * @return The address, scope stripped
	 */
	private static String stripAddrScope(String addr) {
		int scopeSignPos = addr.indexOf('%');
		return (scopeSignPos > 0) ? addr.substring(0, scopeSignPos) : addr;
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
			if (!first) addrs.append(LIST_SEPARATOR);
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
		final int lbr = sockAddrString.indexOf(LEFT_DELIM);
		final int rbr = sockAddrString.lastIndexOf(RIGHT_DELIM);
		if (lbr < 0 || rbr < 0) throw new UnknownHostException(String.format("Unparseable socket addr string: '%s'", sockAddrString));
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
		StringTokenizer addrTok = new StringTokenizer(sockAddrs, LIST_SEPARATOR);
		while(addrTok.hasMoreTokens()) {
			final InetSocketAddress sockAddr = parseReachableSockAddr(addrTok.nextToken().trim(), reachTimeout);
			if (sockAddr != null) return sockAddr;
		}
		return null;
	}
}
