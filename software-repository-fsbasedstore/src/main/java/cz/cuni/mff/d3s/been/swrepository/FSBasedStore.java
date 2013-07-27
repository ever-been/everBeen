package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkNames;
import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.StorePersister;
import cz.cuni.mff.d3s.been.datastore.StoreReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static cz.cuni.mff.d3s.been.swrepository.FSBasedStoreConfiguration.*;

/**
 * @author darklight
 */
final class FSBasedStore implements SoftwareStore {

    private static final Logger log = LoggerFactory.getLogger(FSBasedStore.class);

    private static final String ARTIFACTS_ROOT_NAME = "artifacts";
    private static final String BPKS_ROOT_NAME = "bpks";

    private final File fsRoot;
    private final File artifactFSRoot;
    private final File bpkFSRoot;


    /**
     * Create the data store.
     */
    private FSBasedStore(String fsRootName) {
        fsRoot = new File(fsRootName);
        artifactFSRoot = new File(fsRoot, ARTIFACTS_ROOT_NAME);
        bpkFSRoot = new File(fsRoot, BPKS_ROOT_NAME);
    }

    static FSBasedStore createCache(Properties properties) {
        final PropertyReader propReader = PropertyReader.on(properties);

        final String rootDir = propReader.getString(CACHE_FS_ROOT, DEFAULT_CACHE_FS_ROOT);
        final Long cacheSizeString = propReader.getLong(SWCACHE_MAX_SIZE, DEFAULT_SWCACHE_MAX_SIZE);

        // TODO infer a cache size control mechanism
        return new FSBasedStore(rootDir);
    }

    static FSBasedStore createServer(Properties properties) {
        final String rootDir = properties.getProperty(SERVER_FS_ROOT, DEFAULT_SERVER_FS_ROOT);

        return new FSBasedStore(rootDir);
    }

    /**
     * Initialize the FS store in the app's run directory.
     */
    @Override
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
    public boolean exists(BpkIdentifier bpkIdentifier) {
        File item = getBpkItem(bpkIdentifier);
        if (item == null || !item.exists()) {
            return false;
        } else {
            return true;
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
    public StorePersister getArtifactPersister(ArtifactIdentifier artifactIdentifier) {
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
     * @param itemRoot  Root for the item's specific item type
     * @param pathItems Identifiers for the item
     * @return The file, may or may not exist
     */
    public File getItemPath(File itemRoot, String... pathItems) {
        Path itemPath = FileSystems.getDefault().getPath(itemRoot.getPath(), pathItems);
        return itemPath.toFile();
    }

    /**
     * Get a BPK file's path in the persistence tree.
     *
     * @param bpkIdentifier The BPK's identifier
     * @return The path to the BPK
     */
    public File getBpkItem(BpkIdentifier bpkIdentifier) {
        if (bpkIdentifier == null || bpkIdentifier.getGroupId() == null || bpkIdentifier
                .getBpkId() == null || bpkIdentifier.getVersion() == null) {
            log.error("Null or incomplete BPK identifier {}", bpkIdentifier);
            return null;
        }
        final List<String> pathItems = new LinkedList<String>();
        for (String grpIdPart : bpkIdentifier.getGroupId().split("\\.")) {
            pathItems.add(grpIdPart);
        }
        ;
        pathItems.add(bpkIdentifier.getBpkId());
        pathItems.add(bpkIdentifier.getVersion());

        final File itemPath = getItemPath(bpkFSRoot, pathItems.toArray(new String[pathItems.size()]));

        final String bpkFileName = String.format("%s-%s.bpk", bpkIdentifier.getBpkId(), bpkIdentifier.getVersion());

        return new File(itemPath, bpkFileName);
    }

    /**
     * Get an Artifact's path in the persistence tree
     *
     * @param artifactIdentifier A fully qualified identifier of the Maven artifact
     * @return The path to the Artifact file
     */
    public File getArtifactItem(ArtifactIdentifier artifactIdentifier) {
        if (artifactIdentifier == null || artifactIdentifier.getGroupId() == null || artifactIdentifier
                .getArtifactId() == null || artifactIdentifier.getVersion() == null) {
            log.error("Null or incomplete Artifact identifier {}", artifactIdentifier);
            return null;
        }
        ;
        final List<String> pathItems = new ArrayList<>(Arrays.asList(artifactIdentifier.getGroupId().split("\\.")));
        pathItems.add(artifactIdentifier.getArtifactId());
        pathItems.add(artifactIdentifier.getVersion());
        final String[] newPathItems = new String[pathItems.size()];
        pathItems.toArray(newPathItems);

        final File itemPath = getItemPath(artifactFSRoot, newPathItems);

        final String artifactFileName = String
                .format("%s-%s.jar", artifactIdentifier.getArtifactId(), artifactIdentifier.getVersion());

        return new File(itemPath, artifactFileName);
    }

    @Override
    public List<BpkIdentifier> listBpks() {


        String sep = Pattern.quote(File.separator);
        String regexp = Pattern.quote(bpkFSRoot
                .getPath()) + // base
                sep + //
                "([a-zA-Z0-9" + sep + "]+)" + // groupId
                sep + //
                "([a-zA-Z0-9-._]+)" + // artifactId
                sep + //
                "([a-zA-Z0-9-._]+)" + // version
                 sep + //
                 "([a-zA-Z0-9-._]+)\\.bpk"; // file name

        Pattern pattern = Pattern.compile(regexp);
        List<BpkIdentifier> result = new ArrayList<BpkIdentifier>();

        for (File f : FileUtils.listFiles(bpkFSRoot, new String[]{"bpk"}, true)) {
            String path = f.getPath();
            Matcher m = pattern.matcher(path);
            if (m.find()) {
                BpkIdentifier bpkIdentifier = new BpkIdentifier();
                bpkIdentifier.setGroupId(m.group(1).replace(File.separator, "."));
                bpkIdentifier.setBpkId(m.group(2));
                bpkIdentifier.setVersion(m.group(3));

                result.add(bpkIdentifier);
            }
        }

        return result;
    }

    @Override
    public Map<String, String> getTaskDescriptors(BpkIdentifier bpkIdentifier) throws IOException, JAXBException,
            SAXException, ConvertorException {
        File bpkFile = getBpkItem(bpkIdentifier);

        Map<String, String> descriptors = new HashMap<>();

        ZipFile zipFile = new ZipFile(bpkFile);
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry element = (ZipEntry) zipEntries.nextElement();
            String fileName = element.getName();
            if (!element.isDirectory() && fileName.startsWith(BpkNames.TASK_DESCRIPTORS_DIR + "/")) {
                InputStream inputStream = zipFile.getInputStream(element);
                String xml = IOUtils.toString(inputStream);
                descriptors.put(fileName, xml);
            }
        }
        return descriptors;
    }

    @Override
    public Map<String, String> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) throws IOException, JAXBException,
            SAXException, ConvertorException {
        File bpkFile = getBpkItem(bpkIdentifier);

        Map<String, String> descriptors = new HashMap<>();

        ZipFile zipFile = new ZipFile(bpkFile);
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry element = (ZipEntry) zipEntries.nextElement();
            String fileName = element.getName();
            if (!element.isDirectory() && fileName.startsWith(BpkNames.TASK_CONTEXT_DESCRIPTORS_DIR + "/")) {
                InputStream inputStream = zipFile.getInputStream(element);
                String xml = IOUtils.toString(inputStream);
                descriptors.put(fileName, xml);
            }
        }
        return descriptors;
    }
}
