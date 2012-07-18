/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

package cz.cuni.mff.been.pluggablemodule.hibernate.implementation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.PersistentClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;

/**
 * Implementation of hibernate pluggable module.
 * @author Jan Tattermusch
 */
public class HibernatePluggableModuleImpl extends PluggableModule implements HibernatePluggableModule {
	
	/** 
	 * Creates new instance of class.
	 * @param manager pluggable module manager.
	 */
	public HibernatePluggableModuleImpl(PluggableModuleManager manager) {
		super(manager);
	}

    

	/**
	 * Creates Hibernate session factory which supplies sessions  
	 * for derby with given jdbcUrl
	 * @param jdbcUrl URL of derby instance
	 * @param annotatedClasses array of annotated persistence classes
	 * @return session factory
	 */
	@Override
	public SessionFactory createSessionFactory(String jdbcUrl, String[] annotatedClasses) {
		AnnotationConfiguration ac = new AnnotationConfiguration();
		ac.configure( getConfiguration(jdbcUrl, false, annotatedClasses) );
		SessionFactory factory = ac.buildSessionFactory();
		return factory;
	}
	
	/**
	 * Creates Hibernate session factory which supplies sessions  
	 * for derby with given jdbcUrl
	 * @param jdbcUrl URL of derby instance
	 * @param annotatedClasses array of annotated persistence classes
	 * @return session factory
	 */
	@Override
	public SessionFactory createSessionFactory(String jdbcUrl, Class<?>[] annotatedClasses) {
		AnnotationConfiguration ac = new AnnotationConfiguration();
		String[] classNames = new String[annotatedClasses.length];
		for (int i = 0; i < annotatedClasses.length; i++) {
			classNames[i] = annotatedClasses[i].getCanonicalName();
		}
		ac.configure( getConfiguration(jdbcUrl, false, classNames) );	
		
		SessionFactory factory = ac.buildSessionFactory();
		return factory;
	}
	
	/**
	 * Creates Hibernate session factory which supplies sessions  
	 * for derby with given jdbcUrl
	 * @param jdbcUrl URL of derby instance
	 * @param persistentClasses array of hibernate persistence class mapping definitions
	 * @return session factory
	 */
	@Override
	public SessionFactory createSessionFactory(String jdbcUrl, PersistentClass[] persistentClasses) {
		Configuration conf = new Configuration();
		
		
		conf.configure( getConfiguration(jdbcUrl, false, new String[] {}) );
		Mappings mappings = conf.createMappings();
		for(PersistentClass persistentClass : persistentClasses) {
			mappings.addClass(persistentClass);
		}
		
		
		SessionFactory factory = conf.buildSessionFactory();
		return factory;
	}
	
	/**
	 * Creates hibernate configuration DOM object
	 * @param jdbcUrl derby instance URL
	 * @param showSql whether to show SQL or not (debug option)
	 * @param annotatedClasses array of annotated persistence classes
	 * @return hibernate configuration document
	 */
	private Document getConfiguration(String jdbcUrl, boolean showSql, String[] annotatedClasses) {
		StringBuilder mappingStringBuilder = new StringBuilder();
		for(String className : annotatedClasses) {
			mappingStringBuilder.append("<mapping class=\"");
			mappingStringBuilder.append(className);
			mappingStringBuilder.append("\"/>\n");
		}		
		
		String result =  
		"<?xml version='1.0' encoding='UTF-8'?>" +
		"<hibernate-configuration>" +
		"<session-factory>" +
			"<property name=\"connection.url\">" +
			jdbcUrl +
			"</property>" +
			
			"<property name=\"connection.driver_class\">org.apache.derby.jdbc.EmbeddedDriver</property>" +
			"<property name=\"dialect\">org.hibernate.dialect.DerbyDialect</property>" +
			"<property name=\"transaction.factory_class\">org.hibernate.transaction.JDBCTransactionFactory</property>" +
		    "<property name=\"hibernate.show_sql\">" +
		    (showSql ? "true" : "false") +
		    "</property>" +
		    // SERIALIZABLE isolation level
		    "<property name=\"hibernate.connection.isolation\">8</property>" +
		    "<property name=\"hibernate.connection.autocommit\">false</property>" +
		    "<property name=\"hibernate.hbm2ddl.auto\">update</property>" +
		
            mappingStringBuilder.toString() +

		"</session-factory>" +
		"</hibernate-configuration>";
		
		return stringToDocument(result);
	}
	
	private Document stringToDocument(String text) {
		try {
			InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder loader = factory.newDocumentBuilder();
	    	Document document = loader.parse(is);
	    	return document;
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
		} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
		} catch (SAXException e) {
				e.printStackTrace();
				return null;
		} catch (IOException e) {
				e.printStackTrace();
				return null;
		}		
	}
}
