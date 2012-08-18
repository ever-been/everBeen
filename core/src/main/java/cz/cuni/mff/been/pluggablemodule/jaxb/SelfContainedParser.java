/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.pluggablemodule.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.pmc.PluggableModuleConfiguration;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * A JAXB-based parser that doesn't require any external text files.
 * 
 * @author Andrej Podzimek
 */
public final class SelfContainedParser implements
		BindingParser<PluggableModuleConfiguration> {

	/** The XML schema for document validation. */
	private static final Schema schema;

	/** The JAXB context from which unmarshallers can be obtained. */
	private static final JAXBContext context;

	/** The unmarshaller that does all the work. */
	private final Unmarshaller unmarshaller;

	/**
	 * Createse a new JAXB-based parser bound to the pluggable module
	 * configuration schema.
	 * 
	 * @throws JAXBException
	 *             When unmarshaller initialization fails.
	 */
	public SelfContainedParser() throws JAXBException {
		this.unmarshaller = context.createUnmarshaller();
		this.unmarshaller.setSchema(schema);
	}

	@Override
	public PluggableModuleConfiguration parse(InputStream stream)
			throws JAXBException {
		return (PluggableModuleConfiguration) unmarshaller.unmarshal(stream);
	}

	@Override
	public PluggableModuleConfiguration parse(File file) throws JAXBException {
		return (PluggableModuleConfiguration) unmarshaller.unmarshal(file);
	}

	@Override
	public PluggableModuleConfiguration parse(Reader reader)
			throws JAXBException {
		return (PluggableModuleConfiguration) unmarshaller.unmarshal(reader);
	}

	static {
		final String xsd =

		"<?xml version='1.0' encoding='UTF-8'?>"
				+

				"<xs:schema "
				+ "targetNamespace='http://been.mff.cuni.cz/pluggablemodule/config' "
				+ "elementFormDefault='qualified' "
				+ "xmlns:xs='http://www.w3.org/2001/XMLSchema' "
				+ "xmlns:jaxb='http://java.sun.com/xml/ns/jaxb' "
				+ "xmlns:pmc='http://been.mff.cuni.cz/pluggablemodule/config' "
				+ "xmlns:xjc='http://java.sun.com/xml/ns/jaxb/xjc' "
				+ "jaxb:extensionBindingPrefixes='xjc' "
				+ "jaxb:version='2.0'>"
				+

				"<xs:simpleType name='IDStringRelaxed'>"
				+ "<xs:restriction base='xs:string'>"
				+ "<xs:pattern value='[a-zA-Z0-9_\\.-]+'/>"
				+ "</xs:restriction>"
				+ "</xs:simpleType>"
				+

				"<xs:simpleType name='IDStringClass'>"
				+ "<xs:restriction base='xs:string'>"
				+ "<xs:pattern value='[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*'/>"
				+ "</xs:restriction>"
				+ "</xs:simpleType>"
				+

				"<xs:element name='classpathItem' type='xs:string'/>"
				+

				"<xs:complexType name='ClassPathItems'>"
				+ "<xs:sequence>"
				+ "<xs:element ref='pmc:classpathItem' minOccurs='0' maxOccurs='unbounded'/>"
				+ "</xs:sequence>"
				+ "</xs:complexType>"
				+

				"<xs:element name='classpathItems' type='pmc:ClassPathItems'/>"
				+

				"<xs:attributeGroup name='javaAttrGroup'>"
				+ "<xs:attribute name='mainClass' type='pmc:IDStringClass' use='required'/>"
				+ "</xs:attributeGroup>"
				+

				"<xs:complexType name='Java'>"
				+ "<xs:all>"
				+ "<xs:element ref='pmc:classpathItems' minOccurs='0' maxOccurs='1'/>"
				+ "</xs:all>"
				+ "<xs:attributeGroup ref='pmc:javaAttrGroup'/>"
				+ "</xs:complexType>"
				+

				"<xs:element name='java' type='pmc:Java'/>"
				+

				"<xs:attributeGroup name='dependencyAttrGroup'>"
				+ "<xs:attribute name='moduleName' type='pmc:IDStringRelaxed' use='required'/>"
				+ "<xs:attribute name='moduleVersion' type='xs:string' use='required'/>"
				+ "</xs:attributeGroup>"
				+

				"<xs:complexType name='Dependency'>"
				+ "<xs:attributeGroup ref='pmc:dependencyAttrGroup'/>"
				+ "</xs:complexType>"
				+

				"<xs:element name='dependency' type='pmc:Dependency'/>"
				+

				"<xs:complexType name='Dependencies'>"
				+ "<xs:sequence>"
				+ "<xs:element ref='pmc:dependency' minOccurs='0' maxOccurs='unbounded'/>"
				+ "</xs:sequence>"
				+ "</xs:complexType>"
				+

				"<xs:element name='dependencies' type='pmc:Dependencies'/>"
				+

				"<xs:element name='pluggableModuleConfiguration'>"
				+ "<xs:complexType>"
				+ "<xs:all>"
				+ "<xs:element ref='pmc:java' minOccurs='1' maxOccurs='1'/>"
				+ "<xs:element ref='pmc:dependencies' minOccurs='0' maxOccurs='1'/>"
				+ "</xs:all>" + "</xs:complexType>" + "</xs:element>"
				+ "</xs:schema>";

		Throwable t = null;

		try {
			context = JAXBContext
					.newInstance(PluggableModuleConfiguration.class);
			schema = XSD.createSchema(new StreamSource(new StringReader(xsd)));
		} catch (SAXException exception) {
			t = exception;
			Task task = CurrentTaskSingleton.getTaskHandle();
			if (null != task) {
				task.logError("JAXB parser could not find, read or parse schema files.");
			}
			throw new ExceptionInInitializerError(exception); // Dirty, but
																// needed.
		} catch (JAXBException exception) {
			t = exception;
			Task task = CurrentTaskSingleton.getTaskHandle();
			if (null != task) {
				task.logError("JAXB parser refused or could not load the binding class.");
			}
			throw new ExceptionInInitializerError(exception); // Dirty, but
																// needed.
		} finally {
			for (; null != t; t = t.getCause()) {
				System.err.println();
				System.err.println(t.getMessage());
				t.printStackTrace(System.err);
			}
		}
	}
}
