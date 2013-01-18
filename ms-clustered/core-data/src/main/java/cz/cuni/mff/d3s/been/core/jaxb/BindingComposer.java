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

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

/**
 * JAXB-based XML composers (marshallers) implement this interface.
 * 
 * @author Andrej Podzimek
 *
 * @param <T> The binding class.
 */
public interface BindingComposer< T extends AbstractSerializable > {

	/**
	 * Composes an XML document into an output stream.
	 * 
	 * @param xml A JAXB-based XML data representation.
	 * @param stream An output stream to write the XML data.
	 * @throws javax.xml.bind.JAXBException When data marshalling fails.
	 */
	void compose(T xml, OutputStream stream) throws JAXBException;

	/**
	 * Composes an XML document into a writer.
	 *
	 * @param xml A JAXB-based XML data representation.
	 * @param writer A writer to write the XML data.
	 * @throws javax.xml.bind.JAXBException When data marshalling fails.
	 */
	void compose(T xml, Writer writer) throws JAXBException;

	/**
	 * Composes an XML document into a file.
	 *
	 * @param xml A JAXB-based XML data representation.
	 * @param file A file to write the XML data. An existing file will be overwritten.
	 * @throws javax.xml.bind.JAXBException When data marshalling fails.
	 */
	void compose(T xml, File file) throws JAXBException;
}
