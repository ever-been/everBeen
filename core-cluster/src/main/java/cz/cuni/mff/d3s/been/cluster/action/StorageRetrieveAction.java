package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

/**
 * @author Kuba Brecka
 */
public class StorageRetrieveAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public StorageRetrieveAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String benchmarkId = this.request.getSelector();
		Storage storage = ctx.getBenchmarks().get(benchmarkId).getStorage();
		String serializedStorage = storageToXml(storage);
		return Replies.createOkReply(serializedStorage);
	}

	public static String storageToXml(Storage storage) {
		BindingComposer<Storage> composer = null;
		StringWriter writer = null;
		try {
			composer = XSD.STORAGE.createComposer(Storage.class);
			writer = new StringWriter();
			composer.compose(storage, writer);
		} catch (SAXException | JAXBException e) {
			throw new IllegalArgumentException("Cannot serialize Storage to XML", e);
		}

		return writer.toString();
	}
}
