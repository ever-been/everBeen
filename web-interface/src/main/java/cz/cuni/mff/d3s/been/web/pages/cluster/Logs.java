package cz.cuni.mff.d3s.been.web.pages.cluster;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.CLUSTER_LOGS)
public class Logs extends Page {

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	@Inject
	private ComponentResources resources;

	public Link getDownloadEventBaseLink() {
		return resources.createEventLink("downloadLogs");
	}

	public Object onDownloadLogs(String dateString) throws Exception {
		Date date;
		try {
			date = new SimpleDateFormat(DATE_FORMAT).parse(dateString);
		} catch (ParseException e) {
			throw new Exception(String.format(
					"Format of the given date is invalid. Expected format '%s' given value '%s'",
					DATE_FORMAT,
					dateString));
		}

		Collection<ServiceLogMessage> serviceLogs = this.api.getApi().getServiceLogsByDate(date);
		StringBuilder sb = new StringBuilder();
		for (ServiceLogMessage serviceLog : serviceLogs) {
			sb.append(serviceLog.toDownloadableString());
			sb.append("\n");
		}

		final String filename = "service-logs-" + dateString;
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
