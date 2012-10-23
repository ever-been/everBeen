package cz.cuni.mff.been.core.utils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.been.core.utils.IOUtils;
import static org.mockito.Mockito.*;

/**
 * @author Tadeáš Palusga
 */
public class IOUtilsTest extends Assert {

	@Test
	public void testCloseResourceClosesResource() throws Exception {
		Closeable resource = mock(Closeable.class);
		IOUtils.closeCloseableQuitely(resource);
		verify(resource).close();
	}

	@Test
	public void testCloseResourceDoesNotRethrowsExceptions() throws Exception {
		Closeable resource = mock(Closeable.class);
		doThrow(IOException.class).when(resource).close();
		IOUtils.closeCloseableQuitely(resource);
		verify(resource).close();
	}

	@Test
	public void testCloseAlreadyClosedResourceDoesNotThrowsException() throws Exception {
		InputStream is = new ByteArrayInputStream(new byte[50]);
		is.close();

		IOUtils.closeCloseableQuitely(is);
	}

	@Test
	public void testCloseNullResourceDoesNotThrowsException() throws Exception {
		IOUtils.closeCloseableQuitely(null);
	}

}
