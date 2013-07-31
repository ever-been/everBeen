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
public class BpkFileHolder implements BpkHolder {

    private File bpkFile;

    private BpkIdentifier bpkIdentifier;

    public BpkFileHolder(File bpkFile) throws IOException, BpkConfigurationException {
       this.bpkFile = bpkFile;
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
        // ignore - this class is not owner of given file
    }

}
