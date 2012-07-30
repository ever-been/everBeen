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

package cz.cuni.mff.been.pluggablemodule.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.mapping.PersistentClass;

/**
 * Hibernate pluggable module interface
 * @author Jan Tattermusch
 *
 */
public interface HibernatePluggableModule {
		/**
		 * Creates Hibernate session factory which supplies sessions  
		 * for derby with given jdbcUrl
		 * @param jdbcUrl url of derby instance
		 * @param annotatedClasses array of annotated persistence classes
		 * @return session factory
		 */
		SessionFactory createSessionFactory(String jdbcUrl, String[] annotatedClasses);

		
		/**
		 * Creates Hibernate session factory which supplies sessions  
		 * for derby with given jdbcUrl
		 * @param jdbcUrl URL of derby instance
		 * @param annotatedClasses array of annotated persistence classes
		 * @return session factory
		 */
		SessionFactory createSessionFactory(String jdbcUrl,	Class<?>[] annotatedClasses);


		/**
		 * Creates Hibernate session factory which supplies sessions  
		 * for derby with given jdbcUrl
		 * @param jdbcUrl URL of derby instance
		 * @param persistentClasses array of hibernate persistence class mapping definitions
		 * @return session factory
		 */
		SessionFactory createSessionFactory(String jdbcUrl,
				PersistentClass[] persistentClasses);
	
}
