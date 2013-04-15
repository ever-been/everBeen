package cz.cuni.mff.d3s.been.jackson.serialize;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public interface SerializationUnit {
	public long doMeasure(Object unit) throws JsonGenerationException, JsonMappingException, IOException;
}
