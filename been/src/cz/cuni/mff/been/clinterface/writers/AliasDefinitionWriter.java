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
package cz.cuni.mff.been.clinterface.writers;

import java.io.IOException;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.hostmanager.database.RSLRestriction;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;

/**
 * A Writer that outputs data from SoftwareAliasDefinition instances.
 * 
 * @author Andrej Podzimek
 */
public final class AliasDefinitionWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public AliasDefinitionWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Short output without restriction specifications.
	 * 
	 * @param definition The alias definition to write.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlain( SoftwareAliasDefinition definition ) throws IOException {
		sendCommon( definition );
		sendOut();
	}
	
	/**
	 * Long output with details about application and OS restrictions.
	 * 
	 * @param definition The alias definition to write.
	 * @throws IOException When it rains.
	 */
	public void sendLineXtend( SoftwareAliasDefinition definition ) throws IOException {
		RestrictionInterface appRestriction;
		RestrictionInterface osRestriction;
		
		appRestriction = definition.getAppRestriction();
		osRestriction = definition.getOsRestriction();
		
		sendCommon( definition );
		builder()
		.append(
			literal(
				appRestriction instanceof RSLRestriction ?
					( (RSLRestriction) appRestriction ).getRSLString() :
					appRestriction.toString()
			)
		)
		.append( '\n' )
		.append(
			literal(
				osRestriction instanceof RSLRestriction ?
					( (RSLRestriction) osRestriction ).getRSLString() :
					osRestriction.toString()
			)
		)
		.append( '\n' );
		sendOut();
	}
	
	/**
	 * Writes common parts of the software alias definition.
	 * 
	 * @param definition The alias definition to write.
	 */
	private void sendCommon( SoftwareAliasDefinition definition ) {
		builder()
		.append( quotedLiteral( definition.getAliasName() ) ).append( ' ' )
		.append( quotedLiteral( definition.getResultName() ) ).append( ' ' )
		.append( quotedLiteral( definition.getResultVendor() ) ).append( ' ' )
		.append( quotedLiteral( definition.getResultVersion() ) ).append( '\n' );
	}
}
