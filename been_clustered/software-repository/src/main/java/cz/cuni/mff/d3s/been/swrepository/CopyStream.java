package cz.cuni.mff.d3s.been.swrepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	private InputStream input;

	private OutputStream output;

	private boolean closeInputOnExit;

	private boolean closeOutputOnExit;

	private boolean flushOutputOnWrite;

	public CopyStream(InputStream input, boolean closeInputOnExit, OutputStream
			output, boolean closeOutputOnExit,
			boolean flushOutputOnWrite) {
		this.input = input;
		this.closeInputOnExit = closeInputOnExit;
		this.output = output;
		this.closeOutputOnExit = closeOutputOnExit;
		this.flushOutputOnWrite = flushOutputOnWrite;
	}

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
				// FIXME -> use ioutils, quiet close
				try {
					input.close();
				} catch (IOException ex) {
					// ignore
				}
			}
			if (closeOutputOnExit) {
				// FIXME -> use ioutils, quiet close
				try {
					output.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}
	}

}
