package cz.cuni.mff.d3s.been.swrepository;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import cz.cuni.mff.d3s.been.core.protocol.api.AbstractNode;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;

public class SoftwareRepository extends AbstractNode {

	private final HttpServer httpServer;

	public SoftwareRepository(Messaging messaging, DataPersistence dataPersistence, HttpServer httpServer) {
		super(messaging, dataPersistence);
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

	void handle(Request request, Response response) {
		// FIXME process request and response
	}

}
