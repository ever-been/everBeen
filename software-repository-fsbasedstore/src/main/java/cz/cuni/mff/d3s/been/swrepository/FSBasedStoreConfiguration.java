package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration for the file-system based software store.
 * 
 * @author darklight
 */
public class FSBasedStoreConfiguration implements BeenServiceConfiguration {

	/** Property name for software caching directory (host-runtime) */
	public static final String CACHE_FS_ROOT = "hostruntime.swcache.folder";
	/** Default caching directory for downloaded software (host-runtime) */
	public static final String DEFAULT_CACHE_FS_ROOT = ".swcache";

	/** Property name for max software cache size (host-runtime) in MBytes */
	public static final String SWCACHE_MAX_SIZE = "hostruntime.swcache.maxSize";
	/** Default software cache size (host-runtime) in MBytes */
	public static final Long DEFAULT_SWCACHE_MAX_SIZE = 1024l;

	/** Property name for storage directory for Software Repository server */
	public static final String SERVER_FS_ROOT = "swrepository.persistence.folder";
	/** Default storage directory for Software Repository server */
	public static final String DEFAULT_SERVER_FS_ROOT = ".swrepository";

}
