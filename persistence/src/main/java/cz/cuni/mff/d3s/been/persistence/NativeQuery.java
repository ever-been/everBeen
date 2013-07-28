package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Kuba Brecka
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class NativeQuery extends SkeletalQuery implements Serializable {

	private String jsFunction;

	public NativeQuery(String jsFunction) {
		super(new EntityID(), new HashMap<String, String>());
		this.jsFunction = jsFunction;
	}

	@Override
	public QueryType getType() {
		return QueryType.NATIVE;
	}

	public String getJsFunction() {
		return jsFunction;
	}

	public void setJsFunction(String jsFunction) {
		this.jsFunction = jsFunction;
	}
}
