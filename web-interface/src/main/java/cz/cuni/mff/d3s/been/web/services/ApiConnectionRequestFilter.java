package cz.cuni.mff.d3s.been.web.services;

import java.io.IOException;

import org.apache.tapestry5.services.*;
import org.slf4j.Logger;

import cz.cuni.mff.d3s.been.api.ServiceUnavailableException;
import cz.cuni.mff.d3s.been.web.pages.Connect;

/**
 * User: donarus Date: 4/27/13 Time: 12:19 PM
 */
public class ApiConnectionRequestFilter implements RequestFilter {

	private final PageRenderLinkSource pageRenderLinkSource;
	private final BeenApiService beenApiService;
	private final Logger log;

	public ApiConnectionRequestFilter(
			final PageRenderLinkSource pageRenderLinkSource,
			final BeenApiService beenApiService,
			final Logger log) {
		this.pageRenderLinkSource = pageRenderLinkSource;
		this.beenApiService = beenApiService;
		this.log = log;
	}

	public boolean service(Request request, Response response,
			RequestHandler handler) throws IOException {
		try {
            log.error("ratata");
			return handler.service(request, response);
		} catch (ServiceUnavailableException e) {
			log.error(
					"Been Api is not connected (It seems that it has been disconnected unexpectedly).",
					e);
			response.sendRedirect(pageRenderLinkSource.createPageRenderLink(Connect.class));
		}
        return true;
	}
}
