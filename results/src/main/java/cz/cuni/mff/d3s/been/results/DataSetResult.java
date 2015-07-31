package cz.cuni.mff.d3s.been.results;

import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * An extension of {@link cz.cuni.mff.d3s.been.evaluators.EvaluatorResult}.
 * Used for storing datasets.
 * Adds type information.
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DataSetResult extends EvaluatorResult {

	@JsonProperty("preserializedResultMapping")
	private SerializableResultMapping preserializedResultMapping;

	@JsonCreator
	public static DataSetResult parseJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, DataSetResult.class);
	}

	@Override
	public String getMimeType() {
		return EvaluatorResult.MIME_TYPE_JSON;
	}

	public Collection<Map<String, Object>> getDataset() throws JsonException, PrimitiveTypeException {
		final ResultMapping resultMapping = ResultMapping.deserialize(preserializedResultMapping);
		final JSONUtils jsonUtils = JSONUtils.newInstance();
		return jsonUtils.deserialize(
				super.data,
				PrimitiveType.toClasses(PrimitiveType.toTypes(resultMapping.getTypeMapping())),
				resultMapping.getAliases(),
				false
		);
	}

	public SerializableResultMapping getPreserializedResultMapping() {
		return preserializedResultMapping;
	}

	public void setPreserializedResultMapping(SerializableResultMapping preserializedResultMapping) {
		this.preserializedResultMapping = preserializedResultMapping;
	}
}
