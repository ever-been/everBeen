package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.results.Result;

/**
 * Result containing a variety of fields usable for integration testing
 *
 * @author darklight
 */
public class ExampleTestableResult extends Result {

	private String testString;
	private Integer testInt;
	private Float testFloat;

	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}

	public Integer getTestInt() {
		return testInt;
	}

	public void setTestInt(Integer testInt) {
		this.testInt = testInt;
	}

	public Float getTestFloat() {
		return testFloat;
	}

	public void setTestFloat(Float testFloat) {
		this.testFloat = testFloat;
	}

	public void init(String testString, Integer testInt, Float testFloat) {
		setTestString(testString);
		setTestInt(testInt);
		setTestFloat(testFloat);
	}
}
