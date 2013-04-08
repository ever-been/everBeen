package cz.cuni.mff.d3s.been.bpkplugin;

import static org.mockito.Mockito.mock;

import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GeneratorDriverTest extends Assert {

	@Mock
	private Log log;

	//tested class
	private GeneratorDriver driver;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		driver = new GeneratorDriver(log);
	}

	@Test(expected = ConfigurationException.class)
	public void testExceptionIsThrownOnInvalidRuntimeSpecification() throws Exception {
		Configuration configuration = mock(Configuration.class);

		driver.generate("invalid runtime specification", configuration);
	}

	@Test(expected = ConfigurationException.class)
	public void testGeneratorSelectionOnInvalidRuntimeSpecification() throws Exception {
		driver.selectCorrectGenerator("invalid runtime specification");
	}

	@Test
	public void testGeneratorSelection() throws Exception {
		assertSame(JavaGenerator.class, driver.selectCorrectGenerator(RuntimeType.JAVA.name()).getClass());
		assertSame(NativeGenerator.class, driver.selectCorrectGenerator(RuntimeType.NATIVE.name()).getClass());
	}
}
