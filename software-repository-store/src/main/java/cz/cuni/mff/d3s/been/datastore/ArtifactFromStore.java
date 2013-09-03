package cz.cuni.mff.d3s.been.datastore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;

/**
 * An artifact retrieved from the {@link SoftwareStore}
 */
public class ArtifactFromStore implements Artifact {

	private static final Logger log = LoggerFactory.getLogger(ArtifactFromStore.class);

	private final ArtifactIdentifier identifier;
	private final String type;
	private final String scope;
	private final StoreReader reader;
	private String classifier;

	/**
	 * Create an artifact from the store
	 *
	 * @param identifier Unique identifier of the artifact
	 * @param reader Reader capable of loading the artifact's content from the store
	 */
	public ArtifactFromStore(ArtifactIdentifier identifier, StoreReader reader) {
		this.identifier = identifier;
		this.reader = reader;
		this.type = "jar";
		this.scope = "runtime";
	}

	@Override
	public int compareTo(Artifact o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getGroupId() {
		return identifier.getGroupId();
	}

	@Override
	public String getArtifactId() {
		return identifier.getArtifactId();
	}

	@Override
	public String getVersion() {
		return identifier.getVersion();
	}

	@Override
	public void setVersion(String version) {
		identifier.setVersion(version);
	}

	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getClassifier() {
		return classifier;
	}

	@Override
	public boolean hasClassifier() {
		return classifier != null;
	}

	@Override
	public File getFile() {
		File tmpFile;
		try {
			tmpFile = File.createTempFile("artifactFromStore", identifier.toString());
		} catch (IOException e) {
			log.error("Could not create buffer file for Artifact {} - {}", identifier.toString(), e.getMessage());
			return null;
		}
		FileOutputStream tempFileOs;
		try {
			tempFileOs = new FileOutputStream(tmpFile);
		} catch (IOException e) {
			log.error("Could not open file \"{}\" for writing - {}.", tmpFile.getAbsolutePath(), e.getMessage());
			return null;
		}
		InputStream contentIs;
		try {
			contentIs = reader.getContentStream();
		} catch (IOException e) {
			log.error("Failed to retrieve source stream for Artifact {} - {}", identifier.toString(), e.getMessage());
			IOUtils.closeQuietly(tempFileOs);
			return null;
		}
		try {
			IOUtils.copy(contentIs, tempFileOs);
		} catch (IOException e) {
			log.error("Can't create TMP file for Artifact {}", identifier.toString());
		}
		IOUtils.closeQuietly(tempFileOs);
		IOUtils.closeQuietly(contentIs);

		return tmpFile;
	}

	@Override
	public void setFile(File destination) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getBaseVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBaseVersion(String baseVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDependencyConflictId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ArtifactMetadata> getMetadataList() {
		throw new UnsupportedOperationException("Artifact metadata operations are not supported for this class.");
	}

	@Override
	public void setRepository(ArtifactRepository remoteRepository) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArtifactRepository getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateVersion(String version, ArtifactRepository localRepository) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDownloadUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDownloadUrl(String downloadUrl) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArtifactFilter getDependencyFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDependencyFilter(ArtifactFilter artifactFilter) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArtifactHandler getArtifactHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getDependencyTrail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDependencyTrail(List<String> dependencyTrail) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setScope(String scope) {
		// TODO Auto-generated method stub

	}

	@Override
	public VersionRange getVersionRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersionRange(VersionRange newRange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectVersion(String version) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroupId(String groupId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setArtifactId(String artifactId) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSnapshot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setResolved(boolean resolved) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setResolvedVersion(String version) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setArtifactHandler(ArtifactHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRelease() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRelease(boolean release) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ArtifactVersion> getAvailableVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAvailableVersions(List<ArtifactVersion> versions) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOptional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOptional(boolean optional) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addMetadata(ArtifactMetadata metadata) {
		// TODO Auto-generated method stub

	}

}
