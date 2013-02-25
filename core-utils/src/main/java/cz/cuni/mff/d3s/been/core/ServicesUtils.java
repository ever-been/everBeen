package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo.SoftwareRepositoryNodeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;

/**
 * Purpose of this class is to associate methods for cluster-wide services like
 * Software Repository or Results Repository.
 * 
 * @author Tadeáš Palusga
 * 
 */
public class ServicesUtils {

	/**
	 * @return {@link SWRepositoryInfo} of registered
	 *         {@link SoftwareRepositoryNodeInfo} Repository or null if Software
	 *         repository has not been registered yes.
	 */
	public SWRepositoryInfo getSWRepositoryInfo() {
		Object swRepObject = getServicesMap().get(Names.SWREPOSITORY_SERVICES_MAP_KEY);
		try {
			return (SWRepositoryInfo) swRepObject;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("Object in servicesMap under the key '%s' is instance of type '%s' but it should be '%s'", Names.SWREPOSITORY_SERVICES_MAP_KEY, swRepObject.getClass().getName(), SWRepositoryInfo.class.getName()), e);
		}
	}

	public void storeSWRepositoryInfo(SWRepositoryInfo swRepositoryInfo) {
		// FIXME Radek Macha:
		// getServicesMap().put(Names.SWREPOSITORY_SERVICES_MAP_KEY, swRepositoryInfo)
	}

	public void removeSWRepositoryInfo() {
		// FIXME Radek Macha:
		// something like
		// getServicesMap().remove(Names.SWREPOSITORY_SERVICES_MAP_KEY)
	}

	/**
	 * @return modifiable map of all registered Services.
	 */
	private IMap<String, Object> getServicesMap() {
		return MapUtils.getMap(Names.SERVICES_MAP_NAME);
	}

}
