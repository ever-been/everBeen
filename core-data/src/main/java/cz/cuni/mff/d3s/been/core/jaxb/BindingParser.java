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
import java.io.Reader;
import java.io.Serializable;

import javax.xml.bind.JAXBException;

/**
 * JAXB-based XML parsers (unmarshallers) implement this interface.
 * 
 * @author Andrej Podzimek
 * 
 * @param <T>
 *          The binding class.
 */
public interface BindingParser<T extends Serializable> {

	/**
	 * Parses an input stream.
	 * 
	 * @param stream
	 *          The input stream to parse.
	 * @return A JAXB-based representation of the XML data.
	 * @throws javax.xml.bind.JAXBException
	 *           When unmarshalling fails.
	 */
	T parse(InputStream stream) throws JAXBException, ConvertorException;

	/**
	 * Parses a reader.
	 * 
	 * @param reader
	 *          The reader to parse.
	 * @return A JAXB-based representation of the XML data.
	 * @throws javax.xml.bind.JAXBException
	 *           When unmarshalling fails.
	 */
	T parse(Reader reader) throws JAXBException, ConvertorException;

	/**
	 * Parses a file.
	 * 
	 * @param file
	 *          Name of the file to parse.
	 * @return A JAXB-based representation of the XML data.
	 * @throws javax.xml.bind.JAXBException
	 *           When unmarshalling fails.
	 */
	T parse(File file) throws JAXBException, ConvertorException;
}
