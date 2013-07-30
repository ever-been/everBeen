package cz.cuni.mff.d3s.been.api;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.MetaInf;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author donarus
 */
public class BpkStreamHolder {
    /**
     * Cached bpkIdentifier which is filled in first call of method {@link BpkStreamHolder#getBpkIdentifier()}
     */
    private BpkIdentifier bpkIdentifier = null;

    private InputStream inputStream;

    public BpkStreamHolder(InputStream inputStream) throws IOException, BpkConfigurationException {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

        IOUtils.copy(inputStream, tempStream);
        return new ByteArrayInputStream(tempStream.toByteArray());

    }

    public BpkIdentifier getBpkIdentifier() throws BpkConfigurationException, IOException {
        if (bpkIdentifier == null) {
            try (InputStream stream = getInputStream()) {
                MetaInf metaInf = BpkResolver.resolve(stream).getMetaInf();

                return new BpkIdentifier()
                        .withGroupId(metaInf.getGroupId())
                        .withBpkId(metaInf.getBpkId())
                        .withVersion(metaInf.getVersion());
            }
        }

        return bpkIdentifier;
    }

}
