package cz.cuni.mff.d3s.been.benchmarkapi;

import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.benchmark.StorageItem;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.rep.ReplyType;
import cz.cuni.mff.d3s.been.mq.req.Request;
import cz.cuni.mff.d3s.been.mq.req.RequestType;
import cz.cuni.mff.d3s.been.taskapi.RequestException;
import cz.cuni.mff.d3s.been.taskapi.Requestor;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
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

	public static String storageToXml(Map<String, String> storage) {

		Storage s = new Storage();
		for (Map.Entry<String, String> entry : storage.entrySet()) {
			StorageItem i = new StorageItem();
			i.setKey(entry.getKey());
			i.setValue(entry.getValue());
			s.getStorageItem().add(i);
		}

		BindingComposer<Storage> composer = null;
		StringWriter writer = null;
		try {
			composer = XSD.STORAGE.createComposer(Storage.class);
			writer = new StringWriter();
			composer.compose(s, writer);
		} catch (SAXException | JAXBException e) {
			throw new IllegalArgumentException("Storage can't be converted to XML", e);
		}

		return writer.toString();
	}

	public static Map<String, String> storageFromXml(String xml) {
		Storage s;
		try {
			BindingParser<Storage> bindingComposer = XSD.STORAGE.createParser(Storage.class);
			s = bindingComposer.parse(new ByteArrayInputStream(xml.getBytes()));
		} catch (ConvertorException | JAXBException | SAXException e) {
			throw new IllegalArgumentException("Cannot parse Storage xml", e);
		}

		Map<String, String> map = new HashMap<>();

		for (StorageItem i : s.getStorageItem()) {
			map.put(i.getKey(), i.getValue());
		}

		return map;
	}

	private void assertValidReply(Reply reply, String value) throws TimeoutException {
		if (reply.getReplyType() == ReplyType.ERROR) {
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Request timed out."));
			} else {
				throw new RuntimeException(String.format("Request failed."));
			}
		}
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
		assertValidReply(reply, value);

		return value;
	}

	public void contextWait(String taskContextEntryId) throws TimeoutException {
		Request request = new Request(RequestType.CONTEXT_WAIT, "", taskContextEntryId);
		Reply reply = send(request);

		String value = reply.getValue();
		assertValidReply(reply, value);
	}

	public void storagePersist(String benchmarkId, Map<String, String> storage) throws TimeoutException {
		Request request = new Request(RequestType.STORAGE_PERSIST, benchmarkId, storageToXml(storage));
		Reply reply = send(request);

		String value = reply.getValue();
		assertValidReply(reply, value);
	}

	public Map<String, String> storageRetrieve(String benchmarkId) throws TimeoutException {
		Request request = new Request(RequestType.STORAGE_RETRIEVE, benchmarkId, "");
		Reply reply = send(request);

		String value = reply.getValue();
		assertValidReply(reply, value);

		return storageFromXml(value);
	}
}
