package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistory;
import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

/**
 * @author Kuba Brecka
 */
public class ResubmitHistoryRetrieve implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public ResubmitHistoryRetrieve(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String benchmarkId = this.request.getSelector();
		ResubmitHistory history = ctx.getBenchmarks().get(benchmarkId).getResubmitHistory();
		String s = resubmitHistoryToXml(history);
		return Replies.createOkReply(s);
	}

	public static String resubmitHistoryToXml(ResubmitHistory history) {
		try {
			BindingComposer<ResubmitHistory> composer = XSD.STORAGE.createComposer(ResubmitHistory.class);
			StringWriter writer = new StringWriter();
			composer.compose(history, writer);
			return writer.toString();
		} catch (SAXException | JAXBException e) {
			throw new IllegalArgumentException("Cannot serialize Storage to XML", e);
		}
	}
}
