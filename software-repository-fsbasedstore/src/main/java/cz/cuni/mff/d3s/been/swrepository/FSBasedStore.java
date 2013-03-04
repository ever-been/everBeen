/**
 * 
 */
package cz.cuni.mff.d3s.been.swrepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * @author darklight
 * 
 */
public final class FSBasedStore implements DataStore {

	private static final Logger log = LoggerFactory.getLogger(FSBasedStore.class);

	private static final String FS_ROOT_NAME = ".persistence";
	private static final String ARTIFACTS_ROOT_NAME = "artifacts";
	private static final String BPKS_ROOT_NAME = "bpks";

	private final File fsRoot;
	private final File artifactFSRoot;
	private final File bpkFSRoot;

	/**
	 * Create the data store.
	 */
	public FSBasedStore() {
		fsRoot = new File(FS_ROOT_NAME);
		artifactFSRoot = new File(fsRoot, ARTIFACTS_ROOT_NAME);
		bpkFSRoot = new File(fsRoot, BPKS_ROOT_NAME);
	}

	/**
	 * Creates the data store over a pre-defined filesystem root.
	 * 
	 * @param persistenceRootDir
	 *          Root of the filesystem storage.
	 */
	public FSBasedStore(File persistenceRootDir) {
		fsRoot = persistenceRootDir;
		artifactFSRoot = new File(fsRoot, ARTIFACTS_ROOT_NAME);
		bpkFSRoot = new File(fsRoot, BPKS_ROOT_NAME);
	}

	/**
	 * Initialize the FS store in the app's run directory.
	 */
	public void init() {
		if (!fsRoot.exists()) {
			fsRoot.mkdir();
		}
		if (!artifactFSRoot.exists()) {
			artifactFSRoot.mkdir();
		}
		if (!bpkFSRoot.exists()) {
			bpkFSRoot.mkdir();
		}
	}

	@Override
	public InputStream getArtifactReaderStream(String groupId, String artifactId,
			String version) throws IOException {
		File item = getItemPath(artifactFSRoot, groupId, artifactId, version);
		if (!item.exists()) {
			return null;
		}
		return new FileInputStream(item);
	}

	@Override
	public StoreReader getBpkReader(BpkIdentifier bpkIdentifier) throws IOException {
		File item = getBpkItem(bpkIdentifier);
		if (item == null || !item.exists()) {
			return null;
		}
		return new FSBasedStoreReader(item);
	}

	@Override
	public OutputStream getArtifactPersister(String groupId, String artifactId,
			String version) throws IOException {
		return new FileOutputStream(getArtifactItem(getItemPath(artifactFSRoot, groupId, artifactId, version), artifactId, version));
	}

	@Override
	public StorePersister getBpkPersister(BpkIdentifier bpkIdentifier) throws IOException {
		File item = getBpkItem(bpkIdentifier);
		if (item == null) {
			return null;
		}
		return new FSBasedStorePersister(bpkIdentifier.toString(), item);
	}

	/**
	 * Generically synthesize a stored file's directory in the persistence tree.
	 * 
	 * @param itemRoot
	 *          Root for the item's specific item type
	 * @param pathItems
	 *          Identifiers for the item
	 * 
	 * @return The file, may or may not exist
	 */
	public File getItemPath(File itemRoot, String... pathItems) {
		Path itemPath = FileSystems.getDefault().getPath(itemRoot.getPath(), pathItems);
		return itemPath.toFile();
	}

	/**
	 * Get a BPK file's path in the persistence tree.
	 * 
	 * @param bpkIdentifier
	 *          The BPK's identifier
	 * 
	 * @return The path to the BPK
	 */
	public File getBpkItem(BpkIdentifier bpkIdentifier) {
		if (bpkIdentifier.getGroupId() == null || bpkIdentifier.getBpkId() == null || bpkIdentifier.getVersion() == null) {
			return null;
		}
		final File itemPath = getItemPath(bpkFSRoot, bpkIdentifier.getGroupId(), bpkIdentifier.getBpkId(), bpkIdentifier.getVersion());
		final String bpkFileName = String.format("%s-%s.bpk", bpkIdentifier.getBpkId(), bpkIdentifier.getVersion());
		return new File(itemPath, bpkFileName);
	}

	/**
	 * Get an Artifact's path in the persistence tree
	 * 
	 * @param itemPath
	 *          The parent directory of the artifact
	 * @param artifactId
	 *          The <code>artifactId</code> of the artifact
	 * @param versionId
	 *          The <code>version</code> of the artifact
	 * 
	 * @return The path to the Artifact file
	 */
	public File getArtifactItem(File itemPath, String artifactId, String versionId) {
		final String artifactFileName = String.format("%s-%s.jar", artifactId, versionId);
		return new File(itemPath, artifactFileName);
	}
}
