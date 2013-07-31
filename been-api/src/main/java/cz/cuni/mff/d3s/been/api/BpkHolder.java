package cz.cuni.mff.d3s.been.api;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: donarus
 * Date: 7/31/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BpkHolder extends AutoCloseable {
    BpkIdentifier getBpkIdentifier() throws BpkConfigurationException, IOException;

    InputStream getInputStream() throws IOException;
}
