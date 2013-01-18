package cz.cuni.mff.d3s.been.task;

/**
 * @author Martin Sixta
 */
public class Task {
	public static void main(String[] args) {
		String echoText = System.getProperty("ECHO_STRING", null);
		System.out.println(echoText);
	}
}
