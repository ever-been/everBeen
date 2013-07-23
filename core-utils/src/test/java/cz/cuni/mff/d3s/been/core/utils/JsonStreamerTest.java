package cz.cuni.mff.d3s.been.core.utils;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.d3s.been.util.JsonException;
import cz.cuni.mff.d3s.been.util.JsonKeyHandler;
import cz.cuni.mff.d3s.been.util.JsonStreamer;

/**
 * @author Martin Sixta
 */
public class JsonStreamerTest extends Assert {
	private static final String json = "{\"created\":null,\"origin\":null,\"originContext\":null,\"name\":\"stdout\",\"level\":999,\"message\":\"ExampleTask\",\"errorTrace\":null,\"senderId\":\"d4a2d0ba-20c9-4b96-9d62-948035aa9d25\",\"contextId\":\"edabbcbd-d083-4414-9787-08da8a834d38\",\"threadName\":null,\"time\":1373118372583}";

	private static final String jsonWithArray = "{\n" + "\"employees\": [\n" + "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" },\n" + "{ \"firstName\":\"Anna\" , \"lastName\":\"Smith\" },\n" + "{ \"firstName\":\"Peter\" , \"lastName\":\"Jones\" }\n" + "]" + "}\n";

	private static final String jsonGarbage = "a45q4ew54e6tdt";

	static final String INNER_TEST = "{\"id\": \"1\", \"LogMessage\" : {\"contextId\":\"edabbcbd-d083-4414-9787-08da8a834d38\"}}";

	private static class OneTimeHandler implements JsonKeyHandler {

		private final String key;

		private String actualValue;
		private String actualKey;

		private int handlerCalled = 0;

		OneTimeHandler(String key) {
			this.key = key;
		}

		@Override
		public void handle(final String key, final String value, final String json) {

			this.actualValue = value;
			this.actualKey = key;

			++handlerCalled;
		}

		private void validate(final String expectedValue) {
			assertEquals(key, actualKey);
			assertEquals(1, handlerCalled);
			assertEquals(expectedValue, actualValue);
		}

		private String getActualValue() {
			return actualValue;
		}
	}

	@Test
	public void testKeyHandled() throws JsonException {
		final String key = "senderId";
		final String value = "d4a2d0ba-20c9-4b96-9d62-948035aa9d25";

		OneTimeHandler handler = new OneTimeHandler(key);

		JsonStreamer streamer = new JsonStreamer();
		streamer.addHandler(key, handler);
		streamer.process(json);

		handler.validate(value);
	}

	@Test
	public void testMultiplyKeyHandled() throws JsonException {

		final String key1 = "created";
		final String value1 = "null";

		final String key2 = "contextId";
		final String value2 = "edabbcbd-d083-4414-9787-08da8a834d38";

		OneTimeHandler handler1 = new OneTimeHandler(key1);

		OneTimeHandler handler2 = new OneTimeHandler(key2);

		JsonStreamer streamer = new JsonStreamer();
		streamer.addHandler(key1, handler1);
		streamer.addHandler(key2, handler2);
		streamer.process(json);

		handler1.validate(value1);
		handler2.validate(value2);
	}

	@Test
	public void testJsonArray() throws JsonException {
		// TODO

		//dummy
		final String key = "senderId";
		final String value = "d4a2d0ba-20c9-4b96-9d62-948035aa9d25";

		OneTimeHandler handler = new OneTimeHandler(key);

		JsonStreamer streamer = new JsonStreamer();
		streamer.addHandler(key, handler);
		streamer.process(jsonWithArray);

		//handler.validate(value);

	}

	@Test(expected = JsonException.class)
	public void testJsonGarbage() throws JsonException {
		final String key = "senderId";
		final String value = "d4a2d0ba-20c9-4b96-9d62-948035aa9d25";

		OneTimeHandler handler = new OneTimeHandler(key);

		JsonStreamer streamer = new JsonStreamer();
		streamer.addHandler(key, handler);
		streamer.process(jsonGarbage);

	}

	@Test
	public void testJsonInnerObject() throws JsonException {
		final String key = "contextId";
		final String value = "edabbcbd-d083-4414-9787-08da8a834d38";

		OneTimeHandler handler = new OneTimeHandler(key);

		JsonStreamer streamer = new JsonStreamer();
		streamer.addHandler(key, handler);
		streamer.process(INNER_TEST);

		handler.validate(value);

	}

}
