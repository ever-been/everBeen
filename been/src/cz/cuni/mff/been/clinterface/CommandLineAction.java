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
package cz.cuni.mff.been.clinterface;

import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Abstract utility class that represents 'function pointers' modules define for their actions.
 * 
 * @param <T> Type of the command line module containing the action.
 * @author Andrej Podzimek
 */
public abstract class CommandLineAction< T extends CommandLineModule > {
	
	/**
	 * The action handling 'function pointer'.
	 * 
	 * @param module The command line module instance handling this action.
	 * @param request The request
	 * @param response The response
	 * @throws ModuleSpecificException When something bad happens.
	 * @throws ModuleOutputException When output can't be written.
	 */
	public abstract void handle(
		T module,
		CommandLineRequest request,
		CommandLineResponse response
	)
	throws ModuleSpecificException, ModuleOutputException;
	
	/**
	 * Help string getter.
	 * 
	 * @return A string with a simple list of parameters and flags.
	 */
	public abstract String getHelpString();

	/**
	 * Verifies that only allowed parameters and flags are present.
	 * 
	 * @param request The Command Line Request to query.
	 * @param knownParameters Acceptable parameters.
	 * @param knownFlags Acceptable flags.
	 * @return A StringBuilder containing error messages or null of no errors were found.
	 */
	protected static StringBuilder verifyArguments(
		CommandLineRequest request,
		Set< String > knownParameters,
		Set< String > knownFlags
	) {
		ArrayList< Entry< String, String > > wrongParameters;
		ArrayList< String > wrongFlags;
		StringBuilder result;
		
		wrongParameters = request.ensureOnlyParameters( knownParameters );
		wrongFlags = request.ensureOnlyFlags( knownFlags );
		result = null;

		if ( null != wrongParameters ) {
			result = new StringBuilder();
			wrongParameters( result, wrongParameters );
		}
		
		if ( null != wrongFlags ) {
			if ( null == result ) {
				result = new StringBuilder();
			}
			wrongFlags( result, wrongFlags );
		}

		return result;
	}
	
	/**
	 * Verifies that only allowed parameters and flags are present and whether a blob is present.
	 * 
	 * @param request The Command Line Request to query.
	 * @param knownParameters Acceptable parameters.
	 * @param knownFlags Acceptable flags.
	 * @param blobExpected Whether a blob was expected.
	 * @return A StringBuilder containing error messages or null of no errors were found.
	 */
	protected static StringBuilder verifyArguments(
		CommandLineRequest request,
		Set< String > knownParameters,
		Set< String > knownFlags,
		boolean blobExpected
	) {
		StringBuilder result;
		
		result = verifyArguments( request, knownParameters, knownFlags );
		result = verifyBlob( request, blobExpected, result );
		return result;
	}
	
	/**
	 * Verifies that only allowed parameters and flags are present and that all the required
	 * parameters are present.
	 * 
	 * @param request The Command Line Request to query.
	 * @param knownParameters Acceptable parameters.
	 * @param knownFlags Acceptable flags.
	 * @param requiredParameters Mandatory parameters.
	 * @return A StringBuilder containing error messages or null of no errors were found.
	 */
	protected static StringBuilder verifyArguments(
		CommandLineRequest request,
		Set< String > knownParameters,
		Set< String > knownFlags,
		Set< String > requiredParameters
	) {
		ArrayList< String > missingParameters;
		StringBuilder result;
		
		missingParameters = request.ensureParametersExist( requiredParameters );
		result = verifyArguments( request, knownParameters, knownFlags );
		
		if ( null != missingParameters ) {
			if ( null == result ) {
				result = new StringBuilder();
			}
			missingParameters( result, missingParameters );
		}
		
		return result;
	}
	
	/**
	 * Verifies that only allowed parameters and flags are present and that all the required
	 * parameters are present and whether a blob is present.
	 * 
	 * @param request The Command Line Request to query.
	 * @param knownParameters Acceptable parameters.
	 * @param knownFlags Acceptable flags.
	 * @param requiredParameters Mandatory parameters.
	 * @param blobExpected Whether a blob was expected.
	 * @return A StringBuilder containing error messages or null of no errors were found.
	 */
	protected static StringBuilder verifyArguments(
		CommandLineRequest request,
		Set< String > knownParameters,
		Set< String > knownFlags,
		Set< String > requiredParameters,
		boolean blobExpected
	) {
		StringBuilder result;
		
		result = verifyArguments( request, knownParameters, knownFlags, requiredParameters );
		result = verifyBlob( request, blobExpected, result );
		return result;
	}
	
	/**
	 * Verifies that only allowed parameters and flags are present and that all the required
	 * parameters and required flags are present.
	 * Note that this method is useless. :-) There is absolutely no point in making a flag
	 * mandatory. It's just a binary value...
	 * 
	 * @param request The Command Line Request to query.
	 * @param knownParameters Acceptable parameters.
	 * @param knownFlags Acceptable flags.
	 * @param requiredParameters Mandatory parameters.
	 * @param requiredFlags Mandatory flags.
	 * @return A StringBuilder containing error messages or null of no errors were found.
	 */
	protected static StringBuilder verifyArguments(
		CommandLineRequest request,
		Set< String > knownParameters,
		Set< String > knownFlags,
		Set< String > requiredParameters,
		Set< String > requiredFlags
	) {
		ArrayList< String > missingFlags;
		StringBuilder result;
		
		missingFlags = request.ensureFlagsExist( requiredFlags );
		result = verifyArguments( request, knownParameters, knownFlags, requiredParameters );
		
		if ( null != missingFlags ) {
			if ( null == result ) {
				result = new StringBuilder();
			}
			missingFlags( result, missingFlags );
		}
		
		return result;
	}
	
	/**
	 * Verifies that only allowed parameters and flags are present and that all the required
	 * parameters and required flags are present.
	 * Note that this method is useless. :-) There is absolutely no point in making a flag
	 * mandatory. It's just a binary value...
	 * 
	 * @param request The Command Line Request to query.
	 * @param knownParameters Acceptable parameters.
	 * @param knownFlags Acceptable flags.
	 * @param requiredParameters Mandatory parameters.
	 * @param requiredFlags Mandatory flags.
	 * @param blobExpected Whether a blob was expected.
	 * @return A StringBuilder containing error messages or null of no errors were found.
	 */
	protected static StringBuilder verifyArguments(
		CommandLineRequest request,
		Set< String > knownParameters,
		Set< String > knownFlags,
		Set< String > requiredParameters,
		Set< String > requiredFlags,
		boolean blobExpected
	) {
		StringBuilder result;
		
		result = verifyArguments(
			request,
			knownParameters,
			knownFlags,
			requiredParameters,
			requiredFlags
		);
		result = verifyBlob( request, blobExpected, result );
		return result;
	}
	
	/**
	 * Constructs a help string for actions without mandatory parameters.
	 * 
	 * @param parameters Action parameters.
	 * @param flags Action flags.
	 * @return A string with pretty-printed parameters and flags.
	 */
	protected static String constructHelp(
		Iterable< String > parameters,
		Iterable< String > flags,
		Boolean blob
	) {
		StringBuilder builder;
		
		builder = new StringBuilder();
		if ( null == blob ) {
			builder.append( "blob\n" );
		} else if ( blob ) {
			builder.append( "BLOB\n" );
		}
		for ( String parameter : parameters ) {
			builder.append( "p\t" ).append( parameter ).append( '\n' );
		}
		for ( String flag : flags ) {
			builder.append( "f\t" ).append( flag ).append( '\n' );
		}
		return builder.toString();
	}
	
	/**
	 * Constructs a help string for actions with mandatory parameters.
	 * 
	 * @param allParameters All action parameters.
	 * @param flags Action flags.
	 * @param requiredParameters Mandatory action parameters.
	 * @return A string with pretty-printed parameters and flags.
	 */
	protected static String constructHelp(
		Iterable< String > allParameters,
		Iterable< String > flags,
		Set< String > requiredParameters,
		Boolean blob
	) {
		StringBuilder builder;
		
		builder = new StringBuilder();
		if ( null == blob ) {
			builder.append( "blob\n" );
		} else if ( blob ) {
			builder.append( "BLOB\n" );
		}
		for ( String parameter : requiredParameters ) {
			builder.append( "P\t" ).append( parameter ).append( '\n' );
		}
		for ( String parameter : allParameters ) {
			if ( !requiredParameters.contains( parameter ) ) {
				builder.append( "p\t" ).append( parameter ).append( '\n' );
			}
		}
		for ( String flag : flags ) {
			builder.append( "f\t" ).append( flag ).append( '\n' );
		}
		return builder.toString();
	}
	
	/**
	 * Appends wrong parameters and their values to the error message.
	 * 
	 * @param builder The builder to write to.
	 * @param wrongParameters A collection of wrong parameters and their values.
	 */
	private static void wrongParameters(
		StringBuilder builder,
		Iterable< Entry< String, String > > wrongParameters
	) {
		for ( Entry< String, String > entry : wrongParameters ) {
			builder
			.append( '\n' )
			.append( "Unknown parameter: " )
			.append( entry.getKey() ).append( '=' ).append( entry.getValue() );
		}
	}
	
	/**
	 * Appends wrong flags to the error message.
	 * 
	 * @param builder The builder to write to.
	 * @param wrongFlags A collection of wrong flags.
	 */
	private static void wrongFlags( StringBuilder builder, Iterable< String > wrongFlags ) {
		for ( String flag : wrongFlags ) {
			builder
			.append( '\n' )
			.append( "Unknown flag: " )
			.append( flag );
		}		
	}
	
	/**
	 * Appends parameters that are missing to the error mesage.
	 * 
	 * @param builder The builder to write to.
	 * @param missingParameters A collection of missing parameters.
	 */
	private static void missingParameters(
		StringBuilder builder,
		Iterable< String > missingParameters
	) {
		for ( String parameter : missingParameters ) {
			builder
			.append( '\n' )
			.append( "Missing parameter: " )
			.append( parameter );
		}
	}
	
	/**
	 * Appends flags that are missing to the error message. 
	 * 
	 * @param builder The builder to write to.
	 * @param missingFlags A collection of missing flags.
	 */
	private static void missingFlags( StringBuilder builder, Iterable< String > missingFlags ) {
		for ( String flag : missingFlags ) {
			builder
			.append( '\n' )
			.append( "Missing flag: " )
			.append( flag );
		}		
	}
	
	/**
	 * Vefifies whether a blob is present or not.
	 * 
	 * @param request The Command Line Request to query.
	 * @param blobExpected Whether a blob is expected.
	 * @param builder The builder to write to or null.
	 * @return A builder with error messages or unchanged {@code builder}.
	 */
	private static StringBuilder verifyBlob(
		CommandLineRequest request,
		boolean blobExpected,
		StringBuilder builder
	) {
		if ( blobExpected != request.hasBlob() ) {
			if ( null == builder ) {
				builder = new StringBuilder();
			}
			builder.append( '\n' );
			if ( blobExpected ) {
				builder.append( "Expected a blob." );
			} else {
				builder.append( "Unexpected blob." );
			}
		}
		
		return builder;
	}
}
