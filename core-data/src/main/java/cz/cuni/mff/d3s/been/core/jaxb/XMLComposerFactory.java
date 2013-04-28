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

import java.io.Serializable;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

/**
 * This interface is implemented by JAXB composer factories in the XSD enum
 * 
 * @author Andrej Podzimek
 */
interface XMLComposerFactory {

	/**
	 * Creates a new Composer.
	 * 
	 * @param <T>
	 *          Type of the binding and input class.
	 * @param bindingClass
	 *          The binding and input class.
	 * @return A Composer based on the binding and input class.
	 * @throws org.xml.sax.SAXException
	 *           When some low-level XSD problems occur on initialization.
	 * @throws javax.xml.bind.JAXBException
	 *           When the binding class is refused.
	 */
	<T extends Serializable> BindingComposer<T> internalCreateComposer(
			Class<T> bindingClass) throws SAXException, JAXBException;
}
