package cz.cuni.mff.d3s.been.taskapi;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 11.03.13 Time: 10:50 To change
 * this template use File | Settings | File Templates.
 */
public abstract class Task {
	public abstract void run();

	public void doMain(String[] args) {
		System.out.println("Task is started");
		run();
		System.out.println("Task is finished");
	}
}
