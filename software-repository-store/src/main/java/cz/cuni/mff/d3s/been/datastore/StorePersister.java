package cz.cuni.mff.d3s.been.datastore;

import java.io.IOException;
import java.io.InputStream;

/**
 * A persister class that enables the user to dump the content of an
 * {@link InputStream} to a persistent entity.
 * 
 * @author darklight
 * 
 */
public interface StorePersister {

	/**
	 * Dump the content of an {@link InputStream} to this persister's target,
	 * overriding previous content.
	 * 
	 * @param content
	 *          The stream with the content that should be stored
	 * 
	 * @return <code>true</code> whenever the operation results in a success,
	 *         <code>false</code> on failure
	 * 
	 * @throws IOException
	 *           When R/W operation fails
	 */
	boolean dump(InputStream content) throws IOException;
}
