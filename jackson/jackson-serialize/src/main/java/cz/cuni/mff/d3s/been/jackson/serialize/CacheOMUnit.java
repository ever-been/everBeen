package cz.cuni.mff.d3s.been.jackson.serialize;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class CacheOMUnit implements SerializationUnit {

	private final ObjectMapper om = new ObjectMapper();

	@Override
	public long doMeasure(Object unit) throws JsonGenerationException, JsonMappingException, IOException {
		long before, after;

		before = System.nanoTime();
		final String value = om.writeValueAsString(unit);
		after = System.nanoTime();

		return after - before;
	}

}
