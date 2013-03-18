package cz.cuni.mff.d3s.been.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * This class is used to copy InputStream to OutputStream on the fly. It can be
 * used for example to redirect FileInputStream to SocketOutputStream - reason
 * is not surprising - !IT JUST SAVES MEMORY!.
 * 
 * @author donarus
 * 
 */
public class CopyStream {

	private static final int BUFFERSIZE = 10 * 1024;

	private final InputStream input;

	private final OutputStream output;

	private final boolean closeInputOnExit;

	private final boolean closeOutputOnExit;

	private final boolean flushOutputOnWrite;

	/**
	 * Create a stream copying tool.
	 * 
	 * @param input
	 *          Input stream to copy from
	 * @param closeInputOnExit
	 *          Whether the input stream should be closed once the copying is done
	 * @param output
	 *          Output stream to write to
	 * @param closeOutputOnExit
	 *          Whether the output stream should be closed once the copying is
	 *          done
	 * @param flushOutputOnWrite
	 *          Whether a flush should be forced after the copying is over
	 */
	public CopyStream(InputStream input, boolean closeInputOnExit, OutputStream output, boolean closeOutputOnExit, boolean flushOutputOnWrite) {
		this.input = input;
		this.closeInputOnExit = closeInputOnExit;
		this.output = output;
		this.closeOutputOnExit = closeOutputOnExit;
		this.flushOutputOnWrite = flushOutputOnWrite;
	}

	/**
	 * Perform the copy.
	 * 
	 * @throws IOException
	 *           Whenever reading the input or writing the output fails
	 */
	public void copy() throws IOException {
		try {
			byte[] buffer = new byte[BUFFERSIZE];
			for (int bytes = input.read(buffer); bytes >= 0; bytes = input.read(buffer)) {
				output.write(buffer, 0, bytes);
				if (flushOutputOnWrite) {
					output.flush();
				}
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (closeInputOnExit) {
				IOUtils.closeQuietly(input);
			}
			if (closeOutputOnExit) {
				IOUtils.closeQuietly(output);
			}
		}
	}

}
