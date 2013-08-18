package cz.cuni.mff.d3s.been.benchmarkapi;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistory;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.benchmark.StorageItem;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskContextStateInfo;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.NamedSockets;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReplyType;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequestType;
import cz.cuni.mff.d3s.been.taskapi.CheckpointController;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * This class serves as a communicator between the benchmark generator task and
 * the host runtime. It provides various requests that the generator needs.
 * 
 * @author Kuba Brecka
 */
public class BenchmarkRequestor {

	/** checkpoint request helper */
	private final CheckpointController checkpointController;

	/**
	 * Default constructor, creates a new instance with the specified checkpoint
	 * controller.
	 * 
	 * @param checkpointController
	 *          the checkpoint controller to use
	 */
	private BenchmarkRequestor(CheckpointController checkpointController) {
		this.checkpointController = checkpointController;
	}

	/**
	 * Create a benchmark checkpoint checkpointController instance
	 * 
	 * @return A new instance
	 * @throws MessagingException
	 *           When the checkpointController cannot be created
	 */
	public static BenchmarkRequestor create() throws MessagingException {
		final CheckpointController checkpointController = CheckpointController.create(NamedSockets.TASK_CHECKPOINT_0MQ.getConnection());
		return new BenchmarkRequestor(checkpointController);
	}

	/**
	 * Closes the current instance.
	 * 
	 * @throws MessagingException
	 *           when a messaging error occurrs
	 */
	public void close() throws MessagingException {
		checkpointController.close();
	}

	/**
	 * Serializes the passed task context descriptor into XML string
	 * representation.
	 * 
	 * @param entry
	 *          the descriptor to serialize
	 * @return serialized descriptor
	 * @throws IllegalArgumentException
	 *           when the descriptor is invalid or not serializable
	 */
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
	 * Serialized the passed key-value storage into XML representation.
	 * 
	 * @param storage
	 *          the storage to serialize
	 * @return serialized storage
	 */
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

	/**
	 * Deserializes the passed XML string into a key-value storage.
	 * 
	 * @param xml
	 *          the XML string to deserialize
	 * @return the deserialized key-value storage
	 */
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

	/**
	 * Deserializes the resubmit history collection from XML string
	 * representation.
	 * 
	 * @param xml
	 *          the XML string to deserialize
	 * @return deserialized resubmit history
	 */
	public Collection<ResubmitHistoryItem> resubmitHistoryFromXml(String xml) {
		try {
			BindingParser<ResubmitHistory> parser = XSD.STORAGE.createParser(ResubmitHistory.class);
			ResubmitHistory history = parser.parse(new ByteArrayInputStream(xml.getBytes()));
			return history.getResubmitHistoryItem();
		} catch (SAXException | JAXBException | ConvertorException e) {
			throw new IllegalArgumentException("Cannot parse resubmit history XML.", e);
		}
	}

	/**
	 * Checks whether the requestor reply is valid. If yes, it simply returns, if
	 * no, an exception is thrown.
	 * 
	 * @param reply
	 *          the reply to check
	 * @param value
	 *          the reply value to check
	 * @throws TimeoutException
	 *           when the request timed out
	 */
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
	 * Performs a request that will submit the specified task context within the
	 * specified benchmark.
	 * 
	 * @param taskContextDescriptor
	 *          the descriptor to submit
	 * @param benchmarkId
	 *          the benchmark ID under which the descriptor is to be submitted
	 * @return ID of the newly submitted task context
	 * @throws TimeoutException
	 *           when the request times out
	 */
	public String contextSubmit(TaskContextDescriptor taskContextDescriptor, String benchmarkId) throws TimeoutException {
		final CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.CONTEXT_SUBMIT, benchmarkId, taskContextToXml(taskContextDescriptor));
		final Reply reply = checkpointController.request(request);

		String value = reply.getValue();
		assertValidReply(reply, value);

		return value;
	}

	/**
	 * Performs a request that will wait until the specified context finishes.
	 * 
	 * @param taskContextEntryId
	 *          the ID of the context to wait for
	 * @return the final state of the context
	 * @throws TimeoutException
	 *           when the request times out
	 */
	public TaskContextState contextWait(String taskContextEntryId) throws TimeoutException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.CONTEXT_WAIT, "", taskContextEntryId);
		Reply reply = checkpointController.request(request);

		String value = reply.getValue();
		assertValidReply(reply, value);

		return TaskContextState.valueOf(value);
	}

	/**
	 * Performs a request that will persist the current key-value storage of the
	 * generator task.
	 * 
	 * @param benchmarkId
	 *          the benchmark ID of the current benchmark
	 * @param storage
	 *          the key-value storage to store
	 * @throws TimeoutException
	 *           when the request times out
	 */
	public void storagePersist(String benchmarkId, Map<String, String> storage) throws TimeoutException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.STORAGE_PERSIST, benchmarkId, storageToXml(storage));
		Reply reply = checkpointController.request(request);

		String value = reply.getValue();
		assertValidReply(reply, value);
	}

	/**
	 * Performs a request that will retrieve the current key-value storage of the
	 * generator task.
	 * 
	 * @param benchmarkId
	 *          the benchmark ID of the current benchmark
	 * @return the key-value storage of the benchmark
	 * @throws TimeoutException
	 *           when the request times out
	 */
	public Map<String, String> storageRetrieve(String benchmarkId) throws TimeoutException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.STORAGE_RETRIEVE, benchmarkId, "");
		Reply reply = checkpointController.request(request);

		String value = reply.getValue();
		assertValidReply(reply, value);

		return storageFromXml(value);
	}

	/**
	 * Performs a request that will retrieve the current resubmit history of the
	 * benchmark
	 * 
	 * @param benchmarkId
	 *          the ID of the benchmark of which the history should be retrieved
	 * @return the resubmit history of the benchmark
	 * @throws TimeoutException
	 *           when the request times out
	 */
	public Collection<ResubmitHistoryItem> resubmitHistoryRetrieve(String benchmarkId) throws TimeoutException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.RESUBMIT_HISTORY_RETRIEVE, benchmarkId, "");
		Reply reply = checkpointController.request(request);

		String value = reply.getValue();
		assertValidReply(reply, value);

		return resubmitHistoryFromXml(value);
	}

	/**
	 * Performs a request that will retrieve all currently contained task contexts
	 * within the benchmark.
	 * 
	 * @param benchmarkId
	 *          the ID of the benchmark
	 * @return the list of all contained task contexts and their states
	 * @throws TimeoutException
	 *           when the request times out
	 * @throws JsonException
	 *           when the response is invalid or not parsable
	 */
	public TaskContextStateInfo containedContextsRetrieve(String benchmarkId) throws TimeoutException, JsonException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.CONTAINED_CONTEXTS_RETRIEVE, benchmarkId, "");
		Reply reply = checkpointController.request(request);

		String value = reply.getValue();
		assertValidReply(reply, value);

		return JSONUtils.newInstance().deserialize(value, TaskContextStateInfo.class);
	}

}
