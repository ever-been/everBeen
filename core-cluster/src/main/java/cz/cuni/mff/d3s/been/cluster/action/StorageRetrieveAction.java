package cz.cuni.mff.d3s.been.cluster.action;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;

/**
 * An {@link Action} that handles a request for retrieving the benchmark
 * key-value storage.
 * 
 * @author Kuba Brecka
 */
public class StorageRetrieveAction implements Action {

	/** the request to handle */
	private final Request request;

	/** BEEN cluster instance */
	private final ClusterContext ctx;

	/**
	 * Default constructor, creates the action with the specified request and
	 * cluster context.
	 * 
	 * @param request
	 *          the request to handle
	 * @param ctx
	 *          the cluster context
	 */
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

	/**
	 * Serializes the key-value storage into a XML string.
	 * 
	 * @param storage
	 *          the storage to serialize
	 * @return serialized storage
	 */
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
