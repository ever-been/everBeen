package cz.cuni.mff.d3s.been.taskapi;

import static cz.cuni.mff.d3s.been.core.StatusCode.EX_USAGE;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 * @author Martin Sixta
 */
public class TaskRunnerTest extends Assert {

	private static final String ARG0 = "ARG0";

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	/** Dummy exception to catch */
	static class DummyException extends RuntimeException {

	}

	/** Dummy task, throws DummyException in doMain to ensure it was called. */
	static class DummyTask extends Task {
		@Override
		public void doMain(String[] args) {
			throw new DummyException();
		}

		@Override
		public void run() {

		}
	}

	/** Dummy task class which is private -> cannot be created */
	private static class PrivateDummyTask extends Task {
		@Override
		public void doMain(String[] args) {

		}

		@Override
		public void run() {

		}
	}

	/** Dummy task which checks that doMain was called with no arguments */
	static class DummyTaskArgs0 extends Task {
		@Override
		public void doMain(String[] args) {
			assertEquals(0, args.length);
		}

		@Override
		public void run() {

		}
	}

	/** Dummy task which checks that doMain was called with one arguments */
	static class DummyTaskArgs1 extends Task {
		@Override
		public void doMain(String[] args) {
			assertEquals(1, args.length);
			assertEquals(ARG0, args[0]);
		}

		@Override
		public void run() {

		}
	}

	@Test(expected = DummyException.class)
	public void testLoadClass() {
		TaskRunner.main(new String[] { DummyTask.class.getName() });
	}

	@Test
	public void testLoadClassNoClass() {
		exit.expectSystemExitWithStatus(EX_USAGE.getCode());
		TaskRunner.main(new String[0]);
	}

	@Test
	public void testLoadClassFail() {
		exit.expectSystemExitWithStatus(EX_USAGE.getCode());
		TaskRunner.main(new String[] { "does.not.exists.Class" });
	}

	@Test
	public void testLoadClassFailOnPrivate() {
		exit.expectSystemExitWithStatus(EX_USAGE.getCode());
		TaskRunner.main(new String[] { PrivateDummyTask.class.getName() });
	}

	@Test
	public void testLoadClassPassedArgsToDoMain0() {
		TaskRunner.main(new String[] { DummyTaskArgs0.class.getName() });
	}

	@Test
	public void testLoadClassPassedArgsToDoMain1() {

		TaskRunner.main(new String[] { DummyTaskArgs1.class.getName(), ARG0 });
	}

}
