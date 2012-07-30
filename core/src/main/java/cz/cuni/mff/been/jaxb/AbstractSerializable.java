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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

/**
 * At the time of this writing (December 2009), the XMC compiler has a stupid bug that causes
 * nondeterministic behaviour. Classes are expected to inherit from Serializable, but the template
 * parameter in collection varies from run to run. It's either <?> or <? extends Serializable>.
 * List<?> and List<? extends Serializable> are not assignment-compatible, which causes problems.
 * This class should let XJC know about the common superinterface.
 * 
 * @author Andrej Podzimek
 */
@XmlTransient
public abstract class AbstractSerializable implements Serializable {

	private static final long serialVersionUID = -7014579707835188844L;
}
