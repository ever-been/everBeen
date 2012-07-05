package cz.cuni.mff.been.pluggablemodule.jaxb;

import java.io.StringReader;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import cz.cuni.mff.been.jaxb.pmc.ClassPathItems;
import cz.cuni.mff.been.jaxb.pmc.Dependencies;
import cz.cuni.mff.been.jaxb.pmc.Dependency;
import cz.cuni.mff.been.jaxb.pmc.Java;
import cz.cuni.mff.been.jaxb.pmc.PluggableModuleConfiguration;

public class SelfContainedParserTest {
	
	private static final String PMC =
	"<pluggableModuleConfiguration xmlns='http://been.mff.cuni.cz/pluggablemodule/config'>" +
		"<dependencies>" +
			"<dependency moduleName='A' moduleVersion='0'/>" +
			"<dependency moduleName='B' moduleVersion='1'/>" +
			"<dependency moduleName='C' moduleVersion='2'/>" +
			"<dependency moduleName='D' moduleVersion='3'/>" +
			"<dependency moduleName='E' moduleVersion='4'/>" +
			"<dependency moduleName='F' moduleVersion='5'/>" +
			"<dependency moduleName='G' moduleVersion='6'/>" +
			"<dependency moduleName='H' moduleVersion='7'/>" +
			"<dependency moduleName='I' moduleVersion='8'/>" +
			"<dependency moduleName='J' moduleVersion='9'/>" +
		"</dependencies>" +
		"<java mainClass='cz.cuni.mff.been.something.Great'>" +
			"<classpathItems>" +
				"<classpathItem>A0</classpathItem>" +
				"<classpathItem>B1</classpathItem>" +
				"<classpathItem>C2</classpathItem>" +
				"<classpathItem>D3</classpathItem>" +
				"<classpathItem>E4</classpathItem>" +
				"<classpathItem>F5</classpathItem>" +
				"<classpathItem>G6</classpathItem>" +
				"<classpathItem>H7</classpathItem>" +
				"<classpathItem>I8</classpathItem>" +
				"<classpathItem>J9</classpathItem>" +
			"</classpathItems>" +
		"</java>" +
	"</pluggableModuleConfiguration>";
	
	private StringReader reader;
	
	private SelfContainedParser parser;
	
	@Before
	public final void setUp() throws JAXBException {
		reader = new StringReader( PMC );
		parser = new SelfContainedParser();
	}
	
	@Test
	public final void testParseReader() throws JAXBException {
		PluggableModuleConfiguration conf;
		Dependencies deps;
		Java java;
		ClassPathItems cpis;
		int i;
		
		conf = parser.parse( reader );
		
		assertTrue( "Dependencies not set.", conf.isSetDependencies() );
		assertTrue( "Java not set.", conf.isSetJava() );
		java = conf.getJava();
		deps = conf.getDependencies();
		assertTrue( "Dependencies must not be empty.", deps.isSetDependency() );
		assertTrue( "mainClass can't be unset.", java.isSetMainClass() );
		assertTrue( "classPathItems must exist", java.isSetClasspathItems() );
		cpis = java.getClasspathItems();
		i = 0;
		for ( Dependency dep : deps.getDependency() ) {
			assertTrue( "Name missing???", dep.isSetModuleName() );
			assertTrue( "Version missing???", dep.isSetModuleVersion() );
			assertEquals( "Wrong module name.", String.valueOf( (char) ( 'A' + i ) ), dep.getModuleName() );
			assertEquals( "Wrong module version.", String.valueOf( i ), dep.getModuleVersion() );
			++i;
		}
		assertTrue( "classPathItems must not be empty.", cpis.isSetClasspathItem() );
		i = 0;
		for ( String cpi : cpis.getClasspathItem() ) {
			assertEquals( "Wrong classpath item.", String.valueOf( (char)( 'A' + i ) ) + String.valueOf( i ), cpi );
			++i;
		}
	}
	
	@After
	public final void tearDown() {
		parser = null;
		reader = null;
	}
}
