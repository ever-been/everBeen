package cz.cuni.mff.d3s.been.swrepository.httpserver;

import java.net.InetAddress;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
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
	static final int MAX_CONNECTIONS = 50;

	private final int port;
	private final InetAddress inetAddr;
	private final HttpRequestHandlerRegistry handlerResolver;
	private final HttpParams params;

	private HttpListener listenerThread;

	/**
	 * Create an HTTP server on a host/port
	 * 
	 * @param port
	 *          Port on which the server listens
	 */
	public HttpServer(InetAddress inetAddr, int port) {
		this.inetAddr = inetAddr;
		this.port = port;
		this.handlerResolver = new HttpRequestHandlerRegistry();
		this.params = new BasicHttpParams();
	}

	/**
	 * Get the port registered by this server.
	 * 
	 * @return The port no
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Get the host registered by this server.
	 * 
	 * @return The registered {@link InetAddress}
	 */
	public InetAddress getHost() {
		return inetAddr;
	}

	/**
	 * Run the server (start listening and handling requests)
	 * 
	 * @throws HttpServerException
	 */
	public void start() throws HttpServerException {
		HttpProcessor httpProc = new BasicHttpProcessor();

		HttpService httpService = new HttpService(httpProc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), handlerResolver, params);

		log.debug(String.format(
				"Running listener thread on port %d with params %s",
				port,
				params.toString()));
		listenerThread = new HttpListener(httpService, inetAddr, port, params);
		listenerThread.bind();
		listenerThread.start();
	}

	/**
	 * Stop the server (stop listeners)..
	 * 
	 * @throws HttpServerException
	 */
	public void stop() {
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
}
