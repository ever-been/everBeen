package cz.cuni.mff.d3s.been.cluster.action;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;

import com.hazelcast.core.IMap;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;

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
		String benchmarkId = request.getSelector();
		String serializedDescriptor = request.getValue();

		TaskContextDescriptor taskContextDescriptor;
		try {
			BindingParser<TaskContextDescriptor> bindingComposer = XSD.TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
			taskContextDescriptor = bindingComposer.parse(new ByteArrayInputStream(serializedDescriptor.getBytes()));
		} catch (ConvertorException | JAXBException | SAXException e) {
			return Replies.createErrorReply("Cannot deserialize task context descriptor.");
		}

		String taskContextEntryId = ctx.getTaskContexts().submit(taskContextDescriptor, benchmarkId);

		IMap<String,BenchmarkEntry> benchmarksMap = ctx.getBenchmarks().getBenchmarksMap();
		benchmarksMap.lock(benchmarkId);
		try {
			BenchmarkEntry entry = benchmarksMap.get(benchmarkId);
			entry.setGeneratedContextCount(entry.getGeneratedContextCount() + 1);
			benchmarksMap.put(benchmarkId, entry);
		} finally {
			benchmarksMap.unlock(benchmarkId);
		}

		return Replies.createOkReply(taskContextEntryId);
	}
}
