package cz.cuni.mff.been.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;

public class TaskDescriptorParserTest {
	
	private BindingParser< TaskDescriptor > parser;
	private BindingComposer< TaskDescriptor > composer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty( "been.directory.jaxb", System.getenv( "BEEN_HOME" ) + "/src/cz/cuni/mff/been/jaxb" );
	}
	
	@Before
	public void setUp() throws Exception {
		parser = XSD.TD.createParser( TaskDescriptor.class );
		composer = XSD.TD.createComposer( TaskDescriptor.class );
	}
	
	@Test
	public void testParseCompose() throws ConvertorException, JAXBException, IOException {
		final File dir = new File( System.getenv( "BEEN_HOME" ) + "/resources/task-descriptors" );
		final String[] names = dir.list(
			new FilenameFilter() {
				@Override
				public boolean accept( File dir, String name ) {
					return name.endsWith( ".td" );
				}
			}
		);
		final File[] files = new File[ names.length ];
		
		for ( int i = 0; i < names.length; ++i ) {
			files[ i ] = new File( dir, names[ i ] );
		}
		
		Assert.assertFalse( "Wrong directory or other problem. No files found.", 0 == files.length );
		
		for ( File file : files ) {
			TaskDescriptor descriptor = parser.parse( file );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			composer.compose( descriptor, outputStream );
			outputStream.close();
			byte[] firstOutput = outputStream.toByteArray();
			
			descriptor = parser.parse( new ByteArrayInputStream( firstOutput ) );
			outputStream = new ByteArrayOutputStream();
			composer.compose( descriptor, outputStream );
			outputStream.close();
			byte[] secondOutput = outputStream.toByteArray();
			
			Assert.assertTrue( "First output and second output are different.", Arrays.equals( firstOutput, secondOutput ) );
			composer.compose( descriptor, System.out );
		}
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
		composer = null;
	}
}
