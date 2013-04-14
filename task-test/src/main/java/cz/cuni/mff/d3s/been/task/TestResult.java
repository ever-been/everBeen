package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.results.Result;

import java.util.Arrays;
import java.util.List;

public class TestResult extends Result {

	String field;
	List<Integer> values;

	class InnerClass {
		int a;
		int b;
	};

	InnerClass i = new InnerClass();
}
