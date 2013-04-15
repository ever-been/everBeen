package cz.cuni.mff.d3s.been.jackson.serialize;

import cz.cuni.mff.d3s.been.results.Result;

public class TimeResult extends Result {

	TimeResult(Long timeTaken) {
		this.timeTaken = timeTaken;
	}

	private final Long timeTaken;
}
