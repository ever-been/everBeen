package cz.cuni.mff.d3s.been.api;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.MetaInf;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author donarus
 */
public class BpkStreamHolder implements BpkHolder {

    private File bpkFile;

    private BpkIdentifier bpkIdentifier;

    public BpkStreamHolder(InputStream inputStream) throws IOException, BpkConfigurationException {
        if (bpkFile == null) {
            bpkFile = File.createTempFile("tmp", "bpk");
            FileOutputStream fos = new FileOutputStream(bpkFile);
            IOUtils.copy(inputStream, fos);
            bpkFile.deleteOnExit();
        }
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(bpkFile);
    }

    public BpkIdentifier getBpkIdentifier() throws BpkConfigurationException, IOException {
        if (bpkIdentifier == null) {
            try (InputStream stream = getInputStream()) {
                MetaInf metaInf = BpkResolver.resolve(stream).getMetaInf();

                bpkIdentifier = new BpkIdentifier()
                        .withGroupId(metaInf.getGroupId())
                        .withBpkId(metaInf.getBpkId())
                        .withVersion(metaInf.getVersion());
            }
        }

        return bpkIdentifier;
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteQuietly(bpkFile);
    }

}