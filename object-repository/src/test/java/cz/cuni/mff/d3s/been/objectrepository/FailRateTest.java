package cz.cuni.mff.d3s.been.objectrepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link FailRate}
 *
 * @author darklight
 */
public class FailRateTest {
	/** Tolerance for the approximate equality of FPO results */
	private static final float FPO_TOLERANCE = 0.000001f;

	private FailRate failRate;

	@Before
	public void setUp() {
		this.failRate = new FailRate();
	}

	@After
	public void tearDown() {
		this.failRate = null;
	}

	@Test
	public void testNoRecords() {
		assertClose(0f, failRate.getFailRate());
	}

	@Test
	public void testSomeSuccesses() {
		failRate.success();
		failRate.success();
		failRate.success();
		assertClose(0f, failRate.getFailRate());
	}

	@Test
	public void testSomeFailures() {
		failRate.fail();
		failRate.fail();
		failRate.fail();
		assertClose(1f, failRate.getFailRate());
	}

	@Test
	public void testMixedSuccessesAndFailures() {
		failRate.success();
		failRate.fail();
		assertClose(.5f, failRate.getFailRate());
	}

	@Test
	public void testMoreThanMaxEvents() {
		for (int i = 0; i < 128; ++i) {
			failRate.success();
		}
		for (int j = 0; j < 16; ++j) {
			failRate.fail();
		}
		assertClose(.25f, failRate.getFailRate());
	}

	/**
	 * This helper method asserts that the actual value is close enough to the expected value to be considered the same.
	 *
	 * @param expected Expected value
	 * @param actual Actual value
	 */
	private void assertClose(float expected, float actual) {
		if (Math.abs(expected - actual) > FPO_TOLERANCE) {
			throw new AssertionError(String.format("actual %f is too far off from expected %f", actual, expected));
		}
	}
}
