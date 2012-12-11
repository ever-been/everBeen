/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.hostruntime;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.task.TaskException;

/**
 * Stores package configuration of a task, which is read from the "config.xml"
 * file in the package.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public class PackageConfiguration {

	/**
	 * Specifies in which language is given task implemented.
	 * 
	 * @author Jan Tattermusch
	 */
	enum TaskLanguage {
		JAVA, JYTHON, SHELL
	}

	/** Language of this task */
	private final TaskLanguage taskLanguage;

	/** List of directories or JAR files to append to the Java's class path. */
	private String javaClassPath;

	/** Name of the class to execute. */
	private String javaMainClass;

	/** List of directories or JAR files to append to the Jython's class path. */
	private String jythonClassPath;

	/** Name of Jython script to be executed */
	private String jythonScriptFile;

	/** Name of shell script to be executed */
	private String shellScriptFile;

	/**
	 * Returns language of task's implementation. According to returned language,
	 * different task execution detail fields should be read.
	 * 
	 * @return language of task's implementation.
	 */
	public TaskLanguage getTaskLanguage() {
		return taskLanguage;
	}

	/**
	 * Returns the list of directories or JAR files to append to the Java's class
	 * path. If called with taskLanguage != JAVA, should return null.
	 * 
	 * @return list of directories or JAR files to append to the Java's class path
	 */
	public String getJavaClassPath() {
		return javaClassPath;
	}

	/**
	 * Returns the name of the class to execute. If called with taskLanguage !=
	 * JAVA, should return null.
	 * 
	 * @return name of the class to execute.
	 */
	public String getJavaMainClass() {
		return javaMainClass;
	}

	/**
	 * 
	 * @return name of shell script file to be executed or null if taskLanguage !=
	 *         SHELL
	 */
	public String getShellScriptFile() {
		return shellScriptFile;
	}

	/**
	 * 
	 * @return name of shell script file to be executed or null if taskLanguage !=
	 *         JYTHON
	 */
	public String getJythonScriptFile() {
		return jythonScriptFile;
	}

	/**
	 * 
	 * @return name of shell file to be executed or null if taskLanguage != JYTHON
	 */
	public String getJythonClassPath() {
		return jythonClassPath;
	}

	/**
	 * Custom entity resolver required to load the package configuration DTD.
	 * 
	 * @author Antonin Tomecek
	 * @author David Majda
	 */
	private static class PackageConfigurationEntityResolver implements EntityResolver {

		/**
		 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
		 *      java.lang.String)
		 */
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (systemId.endsWith("package-configuration.dtd")) {
				return new InputSource(getClass().getResourceAsStream("package-configuration.dtd"));
			} else {
				return null;
			}
		}
	}

	/**
	 * Parses the package configuration XML file and builds a DOM document. Sets
	 * taskLanguage property according to document's element structure.
	 * 
	 * @param configFile
	 *          package configuration file
	 * @return document built form the parsed package configuration file
	 * @throws TaskException
	 *           if the package configuration file parsing fails
	 */
	private Document parseConfigFile(String configFile) throws TaskException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new TaskException(e);
		}
		builder.setEntityResolver(new PackageConfigurationEntityResolver());

		Document document;
		try {
			document = builder.parse(configFile);
		} catch (SAXException e) {
			throw new TaskException(e);
		} catch (IOException e) {
			throw new TaskException(e);
		}

		return document;
	}

	private final static String CONFIG_FILE_NAME = "config.xml";

	public static TaskLanguage validate(Document document) throws TaskException {
		TaskLanguage language = null;

		String configFile = CONFIG_FILE_NAME;
		/* Check that document is not null. If it is, it means it isn't valid XML
		 * file, so report error.
		 */
		if (document == null) {
			throw new TaskException(configFile + ": not valid XML file.");
		}

		Element documentElement = document.getDocumentElement();

		/* Check that root element is <packageConfiguration>. */
		if (!documentElement.getNodeName().equals("packageConfiguration")) {
			throw new TaskException(configFile + ": Root element must be <packageConfiguration>.");
		}

		/* Check presence of the <java>, <jython> or <shell> element. */
		NodeList javaElements = documentElement.getElementsByTagName("java");
		NodeList jythonElements = documentElement.getElementsByTagName("jython");
		NodeList shellElements = documentElement.getElementsByTagName("shell");

		Element javaElement = null;
		Element jythonElement = null;
		Element shellElement = null;

		if (javaElements.getLength() >= 1) {
			javaElement = (Element) javaElements.item(0);
		} else {
			if (javaElements.getLength() > 1)
				throw new TaskException(configFile + ": There must be maximum one <java> element present.");
		}

		if (jythonElements.getLength() >= 1) {
			jythonElement = (Element) jythonElements.item(0);
		} else {
			if (jythonElements.getLength() > 1)
				throw new TaskException(configFile + ": There must be maximum one <jython> element present.");
		}

		if (shellElements.getLength() >= 1) {
			shellElement = (Element) shellElements.item(0);
		} else {
			if (shellElements.getLength() > 1)
				throw new TaskException(configFile + ": There must be maximum one <shell> element present.");
		}

		if (javaElement != null) {
			if (jythonElement != null || shellElement != null) {
				throw new TaskException(configFile + ": Only one of <java>, <jython> and <shell> elements allowed.");
			}
			/* Check presence of the <java> element's attributes. */
			if (!javaElement.hasAttribute("classPath")) {
				throw new TaskException(configFile + ": Missing \"classPath\" attribute of the <java> element.");
			}
			if (!javaElement.hasAttribute("mainClass")) {
				throw new TaskException(configFile + ": Missing \"mainClass\" attribute of the <java> element.");
			}
			language = TaskLanguage.JAVA;
		} else if (jythonElement != null) {
			if (/* javaElement != null ||*/shellElement != null) {
				throw new TaskException(configFile + ": Only one of <java>, <jython> and <shell> elements allowed.");
			}
			/* Check presence of the <jython> element's attributes. */
			if (!jythonElement.hasAttribute("classPath")) {
				throw new TaskException(configFile + ": Missing \"classPath\" attribute of the <jython> element.");
			}
			if (!jythonElement.hasAttribute("scriptFile")) {
				throw new TaskException(configFile + ": Missing \"scriptFile\" attribute of the <jython> element.");
			}
			language = TaskLanguage.JYTHON;
		} else if (shellElement != null) {
			//if (javaElement != null || jythonElement != null) {
			//	throw new TaskException(configFile + ": Only one of <java>, <jython> and <shell> elements allowed.");
			//}
			/* Check presence of the <shell> element's attributes. */
			if (!shellElement.hasAttribute("scriptFile")) {
				throw new TaskException(configFile + ": Missing \"scriptFile\" attribute of the <shell> element.");
			}
			language = TaskLanguage.SHELL;
		}
		/* We've passed all the tests now. */
		return language;
	}

	/**
	 * Reads the configuration data from DOM document of the XML configuration
	 * file.
	 */
	private void readData(Document document) {
		Element packageElement = document.getDocumentElement();

		if (taskLanguage.equals(TaskLanguage.JAVA)) {
			NodeList javaElements = packageElement.getElementsByTagName("java");
			assert javaElements.getLength() == 1 : "There should be only one \"java\" " + "element in the configuration file.";
			Element javaElement = (Element) javaElements.item(0);

			if (File.pathSeparatorChar == ';') {
				javaClassPath = javaElement.getAttribute("classPath").replace(':', File.pathSeparatorChar);
			} else {
				javaClassPath = javaElement.getAttribute("classPath").replace(';', File.pathSeparatorChar);
			}
			javaMainClass = javaElement.getAttribute("mainClass");

			jythonClassPath = null;
			jythonScriptFile = null;
			shellScriptFile = null;
		} else if (taskLanguage.equals(TaskLanguage.JYTHON)) {
			NodeList jythonElements = packageElement.getElementsByTagName("jython");
			assert jythonElements.getLength() == 1 : "There should be only one \"jython\" " + "element in the configuration file.";
			Element jythonElement = (Element) jythonElements.item(0);
			if (File.pathSeparatorChar == ';') {
				jythonClassPath = jythonElement.getAttribute("classPath").replace(':', File.pathSeparatorChar);
			} else {
				jythonClassPath = jythonElement.getAttribute("classPath").replace(';', File.pathSeparatorChar);
			}
			jythonScriptFile = jythonElement.getAttribute("scriptFile");

			javaClassPath = null;
			javaMainClass = null;
			shellScriptFile = null;
		} else if (taskLanguage.equals(TaskLanguage.SHELL)) {
			NodeList shellElements = packageElement.getElementsByTagName("shell");
			assert shellElements.getLength() == 1 : "There should be only one \"shell\" " + "element in the configuration file.";
			Element shellElement = (Element) shellElements.item(0);
			shellScriptFile = shellElement.getAttribute("scriptFile");

			jythonClassPath = null;
			jythonScriptFile = null;
			javaClassPath = null;
			javaMainClass = null;
		}
	}

	/**
	 * Allocates a new <code>PackageConfiguration</code> object. The data in the
	 * object is read from specified XML file.
	 * 
	 * @param configFile
	 *          package configuration file
	 * @throws TaskException
	 *           if the package configuration file parsing fails
	 * 
	 */
	public PackageConfiguration(String configFile) throws TaskException {
		Document document = parseConfigFile(configFile);
		taskLanguage = validate(document);
		readData(document);
	}

}
