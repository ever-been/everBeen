package cz.cuni.mff.d3s.been.swrepository;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface HttpRequestHandler {

	public void handle(Request request, Response response);

}
