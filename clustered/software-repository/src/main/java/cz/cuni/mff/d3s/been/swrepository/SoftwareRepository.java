package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;


public class SoftwareRepository implements IClusterService {

	private final HttpServer httpServer;

	public SoftwareRepository(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	@Override
	public void start() {
		httpServer.setRequestHandler(new HttpRequestHandler() {
			@Override
			public void handle(Request request, Response response) {
				SoftwareRepository.this.handle(request, response);
			}
		});
		httpServer.start();
	}

	@Override
	public void stop() {
		httpServer.stop();
	}

	void handle(Request request, Response response) {
		// FIXME process request and response
	}

}
