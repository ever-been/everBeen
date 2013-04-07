package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.results.ResultContainerId;

public class TestContainerId implements ResultContainerId {

	public TestContainerId() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDatabaseName() {
		return "results";
	}

	@Override
	public String getContainerName() {
		return "test";
	}

	@Override
	public String getEntityName() {
		return "hello";
	}

}
