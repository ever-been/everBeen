package cz.cuni.mff.d3s.been.cluster;

/**
 *
 * Type of a node.
 *
 * There are several ways a node can connect to the cluster. The types are listed here.
 *
 * @author Martin Sixta
 */
public enum NodeType {
	/**
	 * The node is full member of a cluster.
	 *
	 * The node participate in data distribution among nodes. On such a node
	 * Task Manager can (and in fact must) run
	 */
	DATA,

	/**
	 * The node is a member of a cluster, but does not participate in data distribution.
	 *
	 * Such a node does not "own" any data, but incurs the overhead of membership handling.
	 *
	 * TODO: currenly there is not clear use case for such a node
	 */
	LITE,

	/**
	 * The node is a client of a cluster.
	 *
	 * The node is not member of a cluster. There is no auto discovery so a cluster member
	 * (or list of members) must be supplied to such a node to connect to.
	 *
	 * Host Runtimes can be implemented as native clients.
	 */
	NATIVE
}
