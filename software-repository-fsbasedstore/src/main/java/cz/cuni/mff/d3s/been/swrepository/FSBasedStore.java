/**
 * 
 */
package cz.cuni.mff.d3s.been.swrepository;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.datastore.DataStore;
import cz.cuni.mff.d3s.been.datastore.StorePersister;
import cz.cuni.mff.d3s.been.datastore.StoreReader;

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
	public StoreReader getArtifactReader(ArtifactIdentifier artifactIdentifier) {
		File item = getArtifactItem(artifactIdentifier);
		if (item == null || !item.exists()) {
			return null;
		} else {
			return new FSBasedStoreReader(item);
		}
	}

	@Override
	public StoreReader getBpkReader(BpkIdentifier bpkIdentifier) {
		File item = getBpkItem(bpkIdentifier);
		if (item == null || !item.exists()) {
			return null;
		}
		return new FSBasedStoreReader(item);
	}

	@Override
	public StorePersister getArtifactPersister(
			ArtifactIdentifier artifactIdentifier) {
		File item = getArtifactItem(artifactIdentifier);
		if (item == null) {
			return null;
		}
		return new FSBasedStorePersister(artifactIdentifier.toString(), item);
	}

	@Override
	public StorePersister getBpkPersister(BpkIdentifier bpkIdentifier) {
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
		Path itemPath = FileSystems.getDefault().getPath(
				itemRoot.getPath(),
				pathItems);
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
		if (bpkIdentifier == null || bpkIdentifier.getGroupId() == null || bpkIdentifier.getBpkId() == null || bpkIdentifier.getVersion() == null) {
			log.error("Null or incomplete BPK identifier {}", bpkIdentifier);
			return null;
		}
		final List<String> pathItems = new LinkedList<String>();
		for (String grpIdPart : bpkIdentifier.getGroupId().split("\\.")) {
			pathItems.add(grpIdPart);
		};
		pathItems.add(bpkIdentifier.getBpkId());
		pathItems.add(bpkIdentifier.getVersion());

		final File itemPath = getItemPath(
				bpkFSRoot,
				pathItems.toArray(new String[pathItems.size()]));

		final String bpkFileName = String.format(
				"%s-%s.bpk",
				bpkIdentifier.getBpkId(),
				bpkIdentifier.getVersion());

		return new File(itemPath, bpkFileName);
	}

	/**
	 * Get an Artifact's path in the persistence tree
	 * 
	 * @param artifactIdentifier
	 *          A fully qualified identifier of the Maven artifact
	 * 
	 * @return The path to the Artifact file
	 */
	public File getArtifactItem(ArtifactIdentifier artifactIdentifier) {
		if (artifactIdentifier == null || artifactIdentifier.getGroupId() == null || artifactIdentifier.getArtifactId() == null || artifactIdentifier.getVersion() == null) {
			log.error("Null or incomplete Artifact identifier {}", artifactIdentifier);
			return null;
		};
		final List<String> pathItems = new ArrayList<>(Arrays.asList(artifactIdentifier.getGroupId().split(
				"\\.")));
		pathItems.add(artifactIdentifier.getArtifactId());
		pathItems.add(artifactIdentifier.getVersion());
		final String[] newPathItems = new String[pathItems.size()];
		pathItems.toArray(newPathItems);

		final File itemPath = getItemPath(artifactFSRoot, newPathItems);

		final String artifactFileName = String.format(
				"%s-%s.jar",
				artifactIdentifier.getArtifactId(),
				artifactIdentifier.getVersion());

		return new File(itemPath, artifactFileName);
	}
}
