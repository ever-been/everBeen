package cz.cuni.mff.d3s.been.taskapi.results;

import cz.cuni.mff.d3s.been.mq.IMessageSender;

public final class ResultFacadeFactory {

	private ResultFacadeFactory() {}

	public static ResultFacade createResultFacade(IMessageSender<String> messaging) {
		return new JSONResultFacade(messaging);
	}

}
