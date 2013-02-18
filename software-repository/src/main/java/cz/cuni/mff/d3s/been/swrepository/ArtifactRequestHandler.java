package cz.cuni.mff.d3s.been.swrepository;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import cz.cuni.mff.d3s.been.swrepository.ArtifactStore;
import cz.cuni.mff.d3s.been.swrepository.httpserver.SkeletalRequestHandler;

/**
 * A request handler that deals with artifact requests.
 * 
 * @author darklight
 *
 */
public class ArtifactRequestHandler extends SkeletalRequestHandler {

	/** Persistence layer used to retrieve artifacts */
	private final ArtifactStore store;
	
	/**
	 * Create the handler.
	 * 
	 * @param store Persistence to use
	 */
	public ArtifactRequestHandler(ArtifactStore store) {
		this.store = store;
	}
	
	@Override
	public void handleGet(HttpRequest request, HttpResponse response) {
		// TODO copy to response
	}
	
	@Override
	protected void handlePut(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		
	}
}
