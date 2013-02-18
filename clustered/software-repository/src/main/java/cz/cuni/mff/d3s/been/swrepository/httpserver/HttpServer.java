package cz.cuni.mff.d3s.been.swrepository.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple put/get http server used for file storage/retrieval
 * 
 * @author darklight
 * 
 */
public class HttpServer {

	/** Class logger */
	private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

	private final int port;
	private final HttpRequestHandlerRegistry handlerResolver;
	private final HttpParams params;

	private Thread listenerThread;

	/**
	 * Create an HTTP server on a host/port
	 * 
	 * @param port
	 *            Port on which the server listens
	 */
	public HttpServer(final int port) {
		this.port = port;
		this.handlerResolver = new HttpRequestHandlerRegistry();
		this.params = new BasicHttpParams();
	}

	/**
	 * Run the server (start listening and handling requests)
	 * 
	 * @throws HttpServerException
	 */
	public void start() throws HttpServerException {
		HttpProcessor httpProc = new BasicHttpProcessor();

		// TODO initialize the sub-services;
		HttpService httpService = new HttpService(httpProc,
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory(), handlerResolver, params);

		log.debug(String.format(
				"Running listener thread on port %d with params %s", port,
				params.toString()));
		listenerThread = new ListenerThread(httpService, port, params);
		listenerThread.start();
	}

	/**
	 * Stop the server (stop listeners)..
	 * 
	 * @throws HttpServerException
	 */
	public void stop() throws HttpServerException {
		// TODO find a way to close the serversocket
		listenerThread.interrupt();
	}

	/**
	 * Get the server's HTTP params
	 * 
	 * @return The params
	 */
	public HttpParams getParams() {
		return params;
	}

	/**
	 * Get the request handler resolver.
	 * 
	 * @return The resolver
	 */
	public HttpRequestHandlerRegistry getResolver() {
		return handlerResolver;
	}

	private static class ListenerThread extends Thread {

		private static final Logger log = LoggerFactory
				.getLogger(ListenerThread.class);

		private final HttpService service;
		private final HttpContext context;
		private final int port;
		private final HttpParams params;

		ListenerThread(HttpService service, int port, HttpParams params) {
			this.service = service;
			this.context = new BasicHttpContext();
			this.port = port;
			this.params = params;
		}

		@Override
		public void run() {
			super.run();

			ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				log.error(String.format(
						"Unable to bind server socket on port %d - %s", port,
						e.getMessage()));
				return;
			}

			log.info(String.format("Listener thread bound to socket %s",
					serverSocket.toString()));

			Socket socket;
			while (!Thread.interrupted()) {
				log.debug("Listener thread waiting for connection...");
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					log.error(String.format(
							"Failed to accept incoming socket connection - %s",
							e.getMessage()));
					continue;
				}

				DefaultHttpServerConnection connection = new DefaultHttpServerConnection();
				try {
					connection.bind(socket, params);
				} catch (IOException e) {
					log.error(String.format(
							"Failed to bind incoming connection %s - %s",
							connection.toString(), e.getMessage()));
					continue;
				}

				try {
					service.handleRequest(connection, context);
				} catch (HttpException e) {
					log.error("Could not process incoming connection %s - %s",
							connection.toString(), e.getMessage());
					continue;
				} catch (IOException e) {
					log.error(String.format(
							"I/O error when processing incoming connection %s - %s",
							connection.toString(), e.getMessage()));
					continue;
				}
				log.debug("Request processed.");
			}

			try {
				serverSocket.close();
				log.info(String.format("Socket %s released, listener is down.",
						serverSocket.toString()));
			} catch (IOException e) {
				log.error(String.format("Failed to close listener socket - %s"));
			}
		}
	}
}
