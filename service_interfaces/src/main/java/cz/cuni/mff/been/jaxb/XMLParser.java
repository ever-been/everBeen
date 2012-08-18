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

package cz.cuni.mff.been.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

/**
 * A reference implementation of the {@link BindingParser} interface.
 * 
 * @author Andrej Podzimek
 */
class XMLParser< T extends AbstractSerializable > implements BindingParser< T > {
	
	/** The unmarshaller to parse XML data. */
	private final Unmarshaller unmarshaller;
	
	/**
	 * Initializes a new XML parser implementation with a JAXB context and a schema.
	 * 
	 * @param context The JAXB context from which an unmarshaller should be created.
	 * @param schema The XML Schema the parsed data must honor.
	 * @throws JAXBException When the unmarshaller cannot be created.
	 */
	public XMLParser( JAXBContext context, Schema schema ) throws JAXBException {
		this.unmarshaller = context.createUnmarshaller();
		this.unmarshaller.setSchema( schema );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T parse( InputStream stream ) throws JAXBException, ConvertorException {
		try {
			return (T) unmarshaller.unmarshal( stream );
		} catch ( ConvertorTransparentException exception ) {
			throw new ConvertorException( exception );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T parse( Reader reader ) throws JAXBException, ConvertorException {
		try {
			return (T) unmarshaller.unmarshal( reader );
		} catch ( ConvertorTransparentException exception ) {
			throw new ConvertorException( exception );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T parse( File file ) throws JAXBException, ConvertorException {
		try {
			return (T) unmarshaller.unmarshal( file );
		} catch ( ConvertorTransparentException exception ) {
			throw new ConvertorException( exception );
		}
	}
}
