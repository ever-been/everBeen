package cz.cuni.mff.d3s.been.bpkplugin;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Tadeas Palusga
 * 
 */
public class RuntimeTypeTest extends Assert {

	@Test
	public void testDetermine() throws Exception {
		assertNull(RuntimeType.determine(null));

		assertSame(RuntimeType.JAVA, RuntimeType.determine(RuntimeType.JAVA.name().toLowerCase()));
		assertSame(RuntimeType.NATIVE, RuntimeType.determine(RuntimeType.NATIVE.name().toUpperCase()));
		assertSame(RuntimeType.NATIVE, RuntimeType.determine("  " + RuntimeType.NATIVE.name() + " \t \n"));
	}

}
