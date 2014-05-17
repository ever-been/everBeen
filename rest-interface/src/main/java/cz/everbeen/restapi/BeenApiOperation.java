package cz.everbeen.restapi;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;

/**
 * An operation on the {@link cz.cuni.mff.d3s.been.api.BeenApi}
 * @author darklight
 */
public interface BeenApiOperation<T> {

	/**
	 * @return The name of this operation
	 */
	String name();

	/**
	 * Perform the operation, and yield an outcome
	 * @param beenApi The API to operate on
	 * @return The outcome of the operation
	 * @throws cz.cuni.mff.d3s.been.api.BeenApiException When the operation on the cluster fails
	 */
	T perform(BeenApi beenApi) throws BeenApiException;

	/**
	 * The return value provided to user in case the operation fails
	 * @param error The reason why the fallback value was requested
	 * @return The fallback value
	 */
	T fallbackValue(Throwable error);
}
