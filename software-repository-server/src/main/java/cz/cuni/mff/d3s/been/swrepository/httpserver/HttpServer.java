package cz.cuni.mff.d3s.been.swrepository.httpserver;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import cz.cuni.mff.d3s.been.util.SocketAddrUtils;
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

	private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

	private final Set<InetSocketAddress> sockAddrs;
	private final HttpRequestHandlerRegistry handlerResolver;
	private final HttpParams params;

	private Collection<HttpListener> listenerThreads;

	/**
	 * Create an HTTP server on a host/port
	 * 
	 * @param sockAddrs
	 *          Sockets on which the server listens
	 */
	public HttpServer(Set<InetSocketAddress> sockAddrs) {
		this.sockAddrs = Collections.unmodifiableSet(sockAddrs);
		this.handlerResolver = new HttpRequestHandlerRegistry();
		this.params = new BasicHttpParams();
	}

	/**
	 * Get the host registered by this server.
	 * 
	 * @return The set of registered {@link java.net.InetSocketAddress}es
	 */
	public Set<InetSocketAddress> getHosts() {
		return sockAddrs;
	}

	/**
	 * Run the server (start listening and handling requests)
	 * 
	 * @throws HttpServerException On HTTP transport failure
	 */
	public void start() throws HttpServerException {
		HttpProcessor httpProc = new BasicHttpProcessor();

		HttpService httpService = new HttpService(httpProc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), handlerResolver, params);

		listenerThreads = new ArrayList<HttpListener>(sockAddrs.size());
		for (InetSocketAddress sockAddr: sockAddrs) {
			log.debug(String.format(
					"Running listener thread on socket %s with params %s",
					sockAddr.toString(),
					params.toString()));
			final HttpListener listenerThread = new HttpListener(httpService, sockAddr, params);
			listenerThread.setName(String.format("SR_http_listener[%s]", SocketAddrUtils.sockAddrToString(sockAddr)));
			listenerThreads.add(listenerThread);
			listenerThread.bind();
			listenerThread.start();
		}
	}

	/**
	 * Stop the server (stop listeners)..
	 */
	public void stop() {
		for (HttpListener listenerThread: listenerThreads) {
			log.debug("Stopping thread {}", listenerThread.getName());
			listenerThread.interrupt();
		}
		for (HttpListener listenerThread: listenerThreads) {
			try {
				listenerThread.join();
			} catch (InterruptedException e) {
				log.error("Listener {} interrupted, exiting dirty", listenerThread.getName());
			}
		}
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
