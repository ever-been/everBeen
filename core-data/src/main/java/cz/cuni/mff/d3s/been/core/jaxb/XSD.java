/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.d3s.been.core.jaxb;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * A simple XSD files list to avoid magic String constants in the code.
 * 
 * @author Andrej Podzimek
 */
public enum XSD {

	RUNTIME("http://been.d3s.mff.cuni.cz/runtimeinfo",
			XSDFile.COMMON.FILE,
			XSDFile.HARDWARE_INFO.FILE,
			XSDFile.RUNTIME.FILE),

	TASKENTRY("http://been.d3s.mff.cuni.cz/taskentry",
			XSDFile.COMMON.FILE,
			XSDFile.TASK_DESCRIPTOR.FILE,
			XSDFile.TASKENTRY.FILE),

	TASK_DESCRIPTOR("http://been.d3s.mff.cuni.cz/taskmanager/td",
			XSDFile.COMMON.FILE,
			XSDFile.TASK_DESCRIPTOR.FILE),

	TASK_CONTEXT_DESCRIPTOR("http://been.d3s.mff.cuni.cz/taskmanager/tcd",
			XSDFile.COMMON.FILE,
			XSDFile.TASK_CONTEXT_DESCRIPTOR.FILE)

	;


	/**
	 * A simple class that holds a list of XSD files and can initialize the XML
	 * Schema on demand.
	 * 
	 * @author Andrej Podzimek
	 */
	private abstract class FilesContainer {

		/** Array of XSD files required by the Schema. */
		private File[] files;

		/**
		 * Initializes a new container with fies.
		 * 
		 * @param files
		 *          Array of XSD files that can be used to produce a Schema.
		 */
		FilesContainer(File[] files) {
			this.files = files;
		}

		/**
		 * Creates a new schema when necessary. As for thread safety, this is OK.
		 * The {@code createSchema()} method is (artificially) thread-safe. The
		 * worst thing that can happen are two consecutive assignments to schema. N
		 * 
		 * @throws org.xml.sax.SAXException
		 *           When the low level SAX parser fails.
		 */
		void initializeSchema() throws SAXException {
			synchronized (factory) { // Artificial thread safety.
				if (null == schema) {
					StreamSource[] sources;

					sources = new StreamSource[files.length];
					for (int i = 0; i < files.length; ++i) {
						InputStream input = XSD.class.getClassLoader().getResourceAsStream("xsd/" + files[i].getName());

						assert input != null;

						sources[i] = new StreamSource(input);
					}
					schema = factory.newSchema(sources);
					files = null; // Can be GCd despite 2nd FTXPF.
				}
			}
		}
	}

	/**
	 * A special factory class that handles the first invocation and initializes
	 * the enum member. Members can't be initialized statically, for that would
	 * require the XSD files to be omnipresent. With dynamic initialization, we
	 * can only include the files that are needed.
	 * 
	 * @author Andrej Podzimek
	 */
	private class FirstTimeXMLParserFactory extends FilesContainer implements XMLParserFactory {

		/**
		 * Initializes the new factory with the supplied bunch of XSD files.
		 * 
		 * @param files
		 *          XSD files that can be used to produce a XML Schema.
		 */
		public FirstTimeXMLParserFactory(File[] files) {
			super(files);
		}

		@Override
		public <T extends AbstractSerializable> BindingParser<T> internalCreateParser(
				Class<T> bindingClass) throws SAXException, JAXBException {
			synchronized (XSD.this) { // Concurrent first calls.
				if (null == parserContexts) { // Not initialized yet.
					initializeSchema();
					parserContexts = new HashMap<Class<? extends AbstractSerializable>, JAXBContext>();
					parserExecutor = new NextTimeXMLParserFactory();
				}
			}
			return createParser(bindingClass);
		}
	}

	/**
	 * A special factory instance that handles the first invocation and
	 * initializes the enum member. Members can't be initialized statically, for
	 * that would require the XSD files to be omnipresent. With dynamic
	 * initialization, we can only include the files that are needed.
	 * 
	 * @author Andrej Podzimek
	 */
	private class FirstTimeXMLComposerFactory extends FilesContainer implements XMLComposerFactory {

		/**
		 * Initializes the new factory with the supplied bunch of XSD files.
		 * 
		 * @param files
		 *          XSD files that can be used to produce a XML Schema.
		 */
		public FirstTimeXMLComposerFactory(File[] files) {
			super(files);
		}

		@Override
		public <T extends AbstractSerializable> BindingComposer<T> internalCreateComposer(
				Class<T> bindingClass) throws SAXException, JAXBException {
			initialize();
			return createComposer(bindingClass);
		}

		/**
		 * Initializes all the basic fields of the enclosing enum member.
		 * 
		 * @throws org.xml.sax.SAXException
		 *           When the low level SAX parser fails.
		 */
		private void initialize() throws SAXException {
			synchronized (XSD.this) { // Concurrent first calls.
				if (null == composerContexts) { // Not initialized yet.
					initializeSchema();
					composerContexts = new HashMap<Class<? extends AbstractSerializable>, JAXBContext>();
					composerExecutor = new NextTimeXMLComposerFactory();
				}
			}
		}
	}

	/**
	 * A factory class that handles all the invocations except the first one.
	 * 
	 * @author Andrej Podzimek
	 */
	private class NextTimeXMLParserFactory implements XMLParserFactory {

		@Override
		public <T extends AbstractSerializable> BindingParser<T> internalCreateParser(
				Class<T> bindingClass) throws JAXBException {
			JAXBContext context;

			synchronized (parserContexts) { // Concurrent requests.
				context = parserContexts.get(bindingClass);
				if (null == context) {
					context = JAXBContext.newInstance(bindingClass);
					parserContexts.put(bindingClass, context);
				}
			}
			return new XMLParser<T>(context, schema);
		}
	}

	/**
	 * A factory class that handles all the invocations except the first one.
	 * 
	 * @author Andrej Podzimek
	 */
	private class NextTimeXMLComposerFactory implements XMLComposerFactory {

		@Override
		public <T extends AbstractSerializable> BindingComposer<T> internalCreateComposer(
				Class<T> bindingClass) throws JAXBException {
			JAXBContext context;

			synchronized (composerContexts) { // Concurrent requests.
				context = composerContexts.get(bindingClass);
				if (null == context) {
					context = JAXBContext.newInstance(bindingClass);
					composerContexts.put(bindingClass, context);
				}
			}
			return new XMLComposer<T>(context, schema);
		}
	}

	/** A factory that can produce XML schemas. */
	private static final SchemaFactory factory;

	static {
		factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); // OK, this is serialized.
	}

	/** The namespace URI. */
	public final String URI;

	/** The XML schema to use for marshaller/unmarshaller initialization. */
	private Schema schema;

	/** The contexts used to create unmarshallers. */
	private HashMap<Class<? extends AbstractSerializable>, JAXBContext> parserContexts;

	/** The contexts used to create marshallers. */
	private HashMap<Class<? extends AbstractSerializable>, JAXBContext> composerContexts;

	/**
	 * The instance that actually answers factory requests. First request handled
	 * differently.
	 */
	private XMLParserFactory parserExecutor;

	/**
	 * The instance that actually answers factory requests. First request handled
	 * differently.
	 */
	private XMLComposerFactory composerExecutor;

	/**
	 * Thi senum member initializer sets the {@code URI} and {@code PREFIX}
	 * constants, initializes the list of XSD files and creates special parser
	 * executors that will finish the initialization dynamically at first
	 * invocation.
	 * 
	 * @param uri
	 *          URI of the namespace to initialize.
	 * @param files
	 *          An array of files that for a XML Schema together.
	 */
	private XSD(String uri, File... files) {
		this.URI = uri;
		this.parserExecutor = new FirstTimeXMLParserFactory(files);
		this.composerExecutor = new FirstTimeXMLComposerFactory(files);
	}

	/**
	 * Creates an XML composer.
	 * 
	 * @param <T>
	 *          Type of the binding class.
	 * @param bindingClass
	 *          The binding class.
	 * @return An XML composer bound to the requested class.
	 * @throws org.xml.sax.SAXException
	 *           When a low-level SAX parser failure occurs.
	 * @throws javax.xml.bind.JAXBException
	 *           When the JAXB class is refused.
	 */
	public <T extends AbstractSerializable> BindingComposer<T> createComposer(
			Class<T> bindingClass) throws SAXException, JAXBException {
		return composerExecutor.internalCreateComposer(bindingClass);
	}

	/**
	 * Creates an XML parser.
	 * 
	 * @param <T>
	 *          Type of the binding class.
	 * @param bindingClass
	 *          The binding class.
	 * @return An XML parser bound to the requested class.
	 * @throws org.xml.sax.SAXException
	 *           When a low-level SAX parser failure occurs.
	 * @throws javax.xml.bind.JAXBException
	 *           When the JAXB class is refused.
	 */
	public <T extends AbstractSerializable> BindingParser<T> createParser(
			Class<T> bindingClass) throws SAXException, JAXBException {
		return parserExecutor.internalCreateParser(bindingClass);
	}

	/**
	 * A utility method for creating a Schema from a source unknown to the XSD
	 * enum.
	 * 
	 * @param sources
	 *          The XSD schema data.
	 * @return A new XML schema.
	 * @throws org.xml.sax.SAXException
	 *           When a low-level SAX parser failure occurs.
	 */
	public static Schema createSchema(Source... sources) throws SAXException {
		synchronized (factory) {
			return factory.newSchema(sources);
		}
	}
}
