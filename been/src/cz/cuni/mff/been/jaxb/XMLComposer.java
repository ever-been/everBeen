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
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;

/**
 * A reference implementation of the {@link BindingComposer} interface.
 * 
 * @author Andrej Podzimek
 */
class XMLComposer< T extends AbstractSerializable > implements BindingComposer< T > {

	/** The marshaller to compose XML documents. */
	private final Marshaller marshaller;
	
	/**
	 * Initializes a new XML composer implementation with a JAXB context and schema.
	 * 
	 * @param context The JAXB context from which a marshaller should be created.
	 * @param schema The XML schema the composed data must honor.
	 * @throws JAXBException When the marshaller cannot be created.
	 */
	public XMLComposer( JAXBContext context, Schema schema ) throws JAXBException {
		this.marshaller = context.createMarshaller();
		this.marshaller.setSchema( schema );
		this.marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
	}
	
	@Override
	public void compose( T xml, OutputStream stream ) throws JAXBException {
		marshaller.marshal( xml, stream );
	}

	@Override
	public void compose( T xml, Writer writer ) throws JAXBException {
		marshaller.marshal( xml, writer );
	}

	@Override
	public void compose( T xml, File file ) throws JAXBException {
		marshaller.marshal( xml, file );
	}
}
