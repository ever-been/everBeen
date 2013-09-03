/**
 * The BEEN API package is where the major control elements of the EverBEEN cluster reside. The most important interface, {@link cz.cuni.mff.d3s.been.api.BeenApi}, is the gateway to controlling EverBEEN.
 *
 * To get a {@link cz.cuni.mff.d3s.been.api.BeenApi} instance, you need to call {@link cz.cuni.mff.d3s.been.api.BeenApiImpl#BeenApiImpl(String, int, String, String)}, specifying:
 *
 * <ul>
 *     <li>hostname of an EverBEEN cluster node</li>
 *     <li>port that host is listening to cluster control protocol</li>
 *     <li>cluster group the targeted node is configured to listen on</li>
 *     <li>password to that cluster group</li>
 * </ul>
 *
 * After that, the {@link cz.cuni.mff.d3s.been.api.BeenApi} connects to the cluster and you can issue cluster-wide EverBEEN commands through it.
 */
package cz.cuni.mff.d3s.been.api;