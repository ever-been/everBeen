package cz.cuni.mff.d3s.been.taskapi.results;

import cz.cuni.mff.d3s.been.taskapi.mq.Messaging;

public final class ResultFacadeFactory {

	private ResultFacadeFactory() {}

	public static ResultFacade createResultFacade(Messaging messaging) {
		return new JSONResultFacade(messaging);
	}

}
