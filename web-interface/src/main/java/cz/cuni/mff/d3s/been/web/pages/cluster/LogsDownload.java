package cz.cuni.mff.d3s.been.web.pages.cluster;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.ClusterConnectionUnavailableException;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Kuba Brecka
 */
public class LogsDownload extends Page {

	Object onActivate(String d) throws ParseException, BeenApiException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(d);

		Collection<ServiceLogMessage> serviceLogs = this.api.getApi().getServiceLogsByDate(date);
		StringBuilder sb = new StringBuilder();
		for (ServiceLogMessage serviceLog : serviceLogs) {
			sb.append(serviceLog.toDownloadableString());
			sb.append("\n");
		}

		final String filename = "service-logs-" + d;
		final ByteArrayInputStream contentStream = new ByteArrayInputStream(sb.toString().getBytes());

		return new StreamResponse() {
			@Override
			public String getContentType() {
				return "text/plain";
			}

			@Override
			public InputStream getStream() throws IOException {
				return contentStream;
			}

			@Override
			public void prepareResponse(Response response) {
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			}
		};
	}

}
