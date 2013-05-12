package cz.cuni.mff.d3s.been.benchmarkapi;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.rep.ReplyType;
import cz.cuni.mff.d3s.been.mq.req.Request;
import cz.cuni.mff.d3s.been.mq.req.RequestType;
import cz.cuni.mff.d3s.been.taskapi.RequestException;
import cz.cuni.mff.d3s.been.taskapi.Requestor;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.concurrent.TimeoutException;

/**
 * @author Kuba Brecka
 */
public class BenchmarkRequestor extends Requestor {

	public static String taskContextToXml(TaskContextDescriptor entry) throws IllegalArgumentException {
		BindingComposer<TaskContextDescriptor> composer = null;
		StringWriter writer = null;
		try {
			composer = XSD.TASK_CONTEXT_DESCRIPTOR.createComposer(TaskContextDescriptor.class);
			writer = new StringWriter();
			composer.compose(entry, writer);
		} catch (SAXException | JAXBException e) {
			throw new IllegalArgumentException("TaskContextDescriptor can't be converted to XML", e);
		}

		return writer.toString();
	}

	/**
	 *
	 * @param taskContextDescriptor
	 * @return task context entry id
	 * @throws RequestException
	 * @throws TimeoutException
	 */
	public String contextSubmit(TaskContextDescriptor taskContextDescriptor, String benchmarkId) throws TimeoutException {
		Request request = null;
		request = new Request(RequestType.CONTEXT_SUBMIT, benchmarkId, taskContextToXml(taskContextDescriptor));
		Reply reply = send(request);

		String value = reply.getValue();
		if (reply.getReplyType() == ReplyType.ERROR) {
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Request timed out."));
			} else {
				throw new RuntimeException(String.format("Request failed."));
			}
		}

		return value;
	}

	public void contextWait(String taskContextEntryId) throws TimeoutException {
		Request request = new Request(RequestType.CONTEXT_WAIT, "", taskContextEntryId);
		Reply reply = send(request);

		if (reply.getReplyType() == ReplyType.ERROR) {
			String value = reply.getValue();
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Request timed out."));
			} else {
				throw new RuntimeException(String.format("Request failed."));
			}
		}
	}

}
