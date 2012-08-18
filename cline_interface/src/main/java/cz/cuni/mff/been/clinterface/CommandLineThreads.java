/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.clinterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import cz.cuni.mff.been.clinterface.Constants.ConnError;
import cz.cuni.mff.been.clinterface.Constants.DataError;
import cz.cuni.mff.been.clinterface.Constants.IntegrityError;
import cz.cuni.mff.been.task.CurrentTaskSingleton;

/**
 * This class accepts network connections and launches a bunch of threads, one
 * for each request. Unlike the module instances, these threads are not pooled.
 * Implementing a thread pool is somewhat difficult, so it might be better to
 * use a standard library implementation if such a feature is ever required.
 * 
 * @author Andrej Podzimek
 */
public class CommandLineThreads extends Thread {

	/**
	 * An abstract base class for error output classes. There can be multiple of
	 * them for various debugging levels.
	 * 
	 * @author Andrej Podzimek
	 */
	private static abstract class ErrorOutput {

		/**
		 * Lists error messages from the whole exception chain up to the root
		 * cause.
		 * 
		 * @param throwable
		 *            The exception to start at (exclusive).
		 * @param response
		 *            Where to write.
		 * @throws IOException
		 *             When it rains.
		 */
		abstract void outputCause(
				Throwable throwable,
				CommandLineResponse response) throws IOException;

		/**
		 * Prefixes lines of output with 'remote:' and indents them.
		 * 
		 * @param message
		 *            The message to output.
		 * @return Indented message.
		 */
		protected static String makeOutput(String message) {
			if (null == message) {
				message = "Null exception message.";
			}
			return "remote:\t" + message.replaceAll("\n", "\nremote:\t") + '\n';
		}
	}

	/**
	 * Error output instance for standard basic output with exception messages
	 * only.
	 */
	private static final ErrorOutput NORMAL_OUTPUT = new ErrorOutput() {

		@Override
		public void outputCause(
				Throwable throwable,
				CommandLineResponse response) throws IOException {
			for (Throwable t = throwable; null != t; t = t.getCause()) {
				response.sendErr(makeOutput(t.getMessage()));
			}
		}
	};

	/**
	 * Error output instance for verbose output with exception messages and
	 * stack traces.
	 */
	private static final ErrorOutput DEBUG_OUTPUT = new ErrorOutput() {

		@Override
		public void outputCause(
				Throwable throwable,
				CommandLineResponse response) throws IOException {
			PrintWriter stream;

			response.flush();
			stream = new PrintWriter(response.getErrOutputStream(), false);
			for (Throwable t = throwable; null != t; t = t.getCause()) {
				stream.append('\n').append(makeOutput(t.getMessage()))
						.append('\n');
				t.printStackTrace(stream);
			}
			stream.close(); // NECESSARY! (Tripple buffering.)
		}
	};

	/**
	 * This class represents one single TCP connection. It parses the input
	 * stream and calls a {@code handleAction()} method of an appropriate module
	 * instance.
	 * 
	 * @author Andrej Podzimek
	 */
	private final class ConnectionThread extends Thread {

		/** The default charset to use for network communication. */
		private static final String CHARSET = "UTF-8"; // Should be dynamic...

		/**
		 * The network socket representing a connection to the command line
		 * client.
		 */
		private final Socket socket;

		/**
		 * Creates and starts a new connection thread.
		 * 
		 * @param socket
		 *            The socket connected to the native command line client.
		 */
		public ConnectionThread(Socket socket) {
			this.socket = socket;
			start();
		}

		/**
		 * Parses the output and sends parameters to a module. Reports errors
		 * when exceptions occur.
		 */
		public void theRun() throws IOException {
			final InputStream stream;
			final StreamTokenizer tokenizer;
			final String moduleName;
			final String actionName;
			Charset charset = null; // Grrr. Buggy compiler!
			CommandLineResponse response = null; // Grrr, Buggy compiler!
			CommandLineModule module = null;

			try {
				if ((charset = Charset.forName(CHARSET)) == null) { // TODO: Get
																	// it
																	// dynamically!!!
					response = new CommandLineResponse( // We need it for
														// logging.
							socket.getOutputStream(),
							Charset.forName("UTF-8"));
					throw new IllegalInputException(DataError.UNKNOWN_CHARSET);
				}
				response = new CommandLineResponse(
						socket.getOutputStream(),
						charset); // Normal initialization.

				stream = socket.getInputStream();
				tokenizer = new StreamTokenizer(new InputStreamReader(
						new FirstPhaseInputStream(stream),
						charset));
				tokenizer.resetSyntax(); // All characters ordinary.
				tokenizer.eolIsSignificant(false); // End of lines mean nothing.
				tokenizer.lowerCaseMode(false); // Nope, case-preserving.
				tokenizer.slashStarComments(false); // TODO Might be suitable.
				tokenizer.slashSlashComments(false); // No, we're not
														// line-oriented.
				tokenizer.wordChars('\1', Character.MAX_CODE_POINT); // Set all
																		// chars
																		// to
																		// word.
				tokenizer.quoteChar('\''); // This is a string quote char.
				tokenizer.quoteChar('"'); // And this too.
				tokenizer.whitespaceChars(' ', ' '); // Spaces are white.
				tokenizer.whitespaceChars('\n', '\n'); // Newlines too.
				tokenizer.whitespaceChars('\t', '\t'); // Tabs as well.
				tokenizer.ordinaryChar('\0'); // Separates arguments from blob.
				tokenizer.ordinaryChar('='); // This separates names and values.

				switch (tokenizer.nextToken()) {
					case StreamTokenizer.TT_WORD:
						moduleName = tokenizer.sval;
						break;
					case StreamTokenizer.TT_EOF:
						throw new IllegalInputException(
								DataError.NO_MODULE,
								CommandLineModule.MODULE_LIST);
					case '"':
					case '\'':
						moduleName = tokenizer.sval;
						break;
					case '=':
						throw new IllegalInputException(DataError.UNEXP_EQ);
					case '\0':
						throw new IllegalInputException(DataError.UNEXP_ZERO);
					default:
						throw new CommandLineException(
								IntegrityError.INT_ERR.MSG);
				}

				if ("help".equals(moduleName)) { // WOW! What a great hack!
					response.sendOut(CommandLineModule.MODULE_LIST);
				} else {
					module = CommandLineModule.forName(moduleName);
					switch (tokenizer.nextToken()) {
						case StreamTokenizer.TT_WORD:
							actionName = tokenizer.sval;
							break;
						case StreamTokenizer.TT_EOF:
							throw new IllegalInputException(
									DataError.NO_ACTION,
									module.getActionsList());
						case '"':
						case '\'':
							actionName = tokenizer.sval;
							break;
						case '=':
							throw new IllegalInputException(DataError.UNEXP_EQ);
						case '\0':
							throw new IllegalInputException(
									DataError.UNEXP_ZERO);
						default:
							throw new CommandLineException(
									IntegrityError.INT_ERR.MSG);
					}
					module.handleAction(actionName, new CommandLineRequest(
							tokenizer,
							stream), response);
				}
				response.flush();
			} catch (IllegalInputException exception) {
				socket.shutdownInput();
				if (null != response) {
					response.flush();
					response.sendRawErr(
							ErrorOutput.makeOutput(exception.getMessage())
									.getBytes(charset),
							exception.getError());
				}
				CurrentTaskSingleton.getTaskHandle().logError(
						exception.getMessage().replace('\n', ' '));
			} catch (ModuleSpecificException exception) {
				socket.shutdownInput();
				if (null != response) {
					if (exception.getMessage().isEmpty()) {
						output.outputCause(exception.getCause(), response);
					} else {
						output.outputCause(exception, response);
					}
					response.flush();
					response.sendRawErr((byte) 0, (byte) (exception.getError()
							.ordinal() + Constants.SPECIFIC_CODE_BASE));
				}
			} catch (ModuleOutputException exception) { // Don't attempt to
														// write...
				CurrentTaskSingleton.getTaskHandle().logError(
						ConnError.MODULE_IOE.MSG); // ...coz that would just
													// cause...
				logExceptionChain(exception); // ...another IOException.
				socket.shutdownInput(); // Will almost certainly throw.
			} catch (CommandLineException exception) {
				socket.shutdownInput();
				if (null != response) {
					output.outputCause(exception, response);
					response.sendErr("remote: Integrity error. Terminating CLI.\n");
					response.flush();
				}
				CurrentTaskSingleton
						.getTaskHandle()
						.logFatal(
								exception.getMessage()
										+ "\nremote: Integrity error. Terminating CLI.");
				logExceptionChain(exception);
				System.exit(-1);
			} catch (Exception exception) {
				socket.shutdownInput();
				if (null != response) {
					output.outputCause(exception, response);
					response.sendErr("remote: Unknown exception. Terminating CLI. "
							+ '(' + exception.getClass().getName() + ")\n");
					response.flush();
				}
				CurrentTaskSingleton.getTaskHandle().logFatal(
						exception.getMessage()
								+ "\nUnknown exception. Terminating CLI.");
				logExceptionChain(exception);
				System.exit(-1);
			} finally {
				try {
					socket.close();
				} catch (IOException exception) {
					CurrentTaskSingleton.getTaskHandle().logError(
							ConnError.CLOSE_SOCK.MSG);
				}
				if (null != module) {
					module.recycle();
				}
			}
		}

		@Override
		public void run() {
			try {
				theRun();
			} catch (IOException exception) {
				CurrentTaskSingleton.getTaskHandle().logError(
						ConnError.UNKNOWN_IOE.MSG);
				logExceptionChain(exception);
			}
		}
	}

	/** The socket to listen on. */
	private final ServerSocket serverSocket;

	/** Counter of accepted connections. */
	private long counter;

	/** Current error output sink. */
	volatile private ErrorOutput output;

	/**
	 * Creates and starts a thread that listens on the standard BEEN port and
	 * accepts connections from native command line clients.
	 * 
	 * @throws IOException
	 *             Mostly when the port is already occupied or something weird
	 *             happens.
	 */
	CommandLineThreads() throws IOException {
		this.serverSocket = new ServerSocket(Constants.BEEN_PORT);
		this.counter = 0;
		this.output = NORMAL_OUTPUT;
		start();
	}

	/**
	 * Accepts connections and launches a {@code ConnectionThread} for each of
	 * them. Exits when someone closes the {@code ServerSocket} from another
	 * thread.
	 */
	@Override
	public void run() {
		for (;;) {
			try {
				new ConnectionThread(serverSocket.accept());
				++counter;
			} catch (SocketException exception) {
				CurrentTaskSingleton.getTaskHandle().logInfo(
						"Socket closed. Accepted " + counter + " connections.");
				break;
			} catch (IOException exception) {
				CurrentTaskSingleton.getTaskHandle().logError(
						"Connection error on accept().");
				continue;
			}
		}
	}

	/**
	 * Closes the server socket, which causes this thread to exit. This is not a
	 * graceful exit, but implementing this properly would involve listing all
	 * accepted sockets, closing them one by one etc., which seems to be an
	 * overkill. If someone stops the command line service using the web
	 * interface, they should make sure no command line clients are running.
	 * 
	 * @throws IOException
	 *             When a pretty weird error occurs.
	 */
	void shutdown() throws IOException {
		serverSocket.close();
	}

	/**
	 * This getter does not have any reasonable purpose. ;-)
	 * 
	 * @return A rough estimate of the number of accepted connections. (Not
	 *         synchronized!)
	 */
	long getCounter() {
		return counter;
	}

	/**
	 * Switches the output to basic output. Only exception messages will be
	 * printed out.
	 */
	void setNormalOutput() {
		output = NORMAL_OUTPUT;
	}

	/**
	 * Switches the output to debug output. Both exception messages and full
	 * exception stack traces will be printed out.
	 */
	void setDebugOutput() {
		output = DEBUG_OUTPUT;
	}

	/**
	 * Logs a chain of exceptions to error output with stack traces. Useful for
	 * debugging and when something really bad (IOException) happens.
	 * 
	 * @param t
	 *            The Throwable linked list to log.
	 */
	private static void logExceptionChain(Throwable t) {
		for (; null != t; t = t.getCause()) {
			System.err.println();
			System.err.println(t.getMessage());
			t.printStackTrace(System.err);
		}
	}
}
