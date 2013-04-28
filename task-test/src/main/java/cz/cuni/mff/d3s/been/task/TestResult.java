package cz.cuni.mff.d3s.been.task;

import java.util.List;

import cz.cuni.mff.d3s.been.results.Result;

public class TestResult extends Result {

	String field;
	List<Integer> values;

	class InnerClass {
		int a;
		int b;
	};

	InnerClass i = new InnerClass();
}
