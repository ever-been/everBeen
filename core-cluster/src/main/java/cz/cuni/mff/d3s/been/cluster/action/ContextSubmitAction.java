package cz.cuni.mff.d3s.been.cluster.action;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Kuba Brecka
 */
public class ContextSubmitAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public ContextSubmitAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		TaskContextDescriptor taskContextDescriptor;
		try {
			String serializedDescriptor = request.getValue();
			BindingParser<TaskContextDescriptor> bindingComposer = XSD.TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
			taskContextDescriptor = bindingComposer.parse(new ByteArrayInputStream(serializedDescriptor.getBytes()));
		} catch (ConvertorException | JAXBException | SAXException e) {
			return Replies.createErrorReply("Cannot deserialize task context descriptor.");
		}

		TaskContextEntry taskContextEntry = ctx.getTaskContexts().submit(taskContextDescriptor);

		String taskContextEntryId = taskContextEntry.getId();

		return Replies.createOkReply(taskContextEntryId);
	}
}
