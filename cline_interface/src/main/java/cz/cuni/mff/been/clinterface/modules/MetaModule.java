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
package cz.cuni.mff.been.clinterface.modules;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.TreeMap;

import cz.cuni.mff.been.clinterface.CommandLineAction;
import cz.cuni.mff.been.clinterface.CommandLineInterface;
import cz.cuni.mff.been.clinterface.CommandLineModule;
import cz.cuni.mff.been.clinterface.CommandLineRequest;
import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.clinterface.CommandLineService;
import cz.cuni.mff.been.clinterface.ModuleOutputException;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.ref.ServiceReference;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.services.Names;

/**
 * This Module sets some tunable values inside the Command Line Interface. The debugging level
 * is an example of such a value. More tunable values might be implemented later.
 * 
 * @author Andrej Podzimek
 */
public final class MetaModule extends CommandLineModule {
	
	public enum Errors implements Message {
		
		/** Unknown action name. */
		UNKN_ACTION( "Unknown action." ),
		
		/** Action not implemented yet. */
		IMPL_ACTION( "Action not implemented yet. Volunteers?" ),
		
		/** When the Task Manager is not responding (RemoteException, IOException). */
		CONN_CLI( "Could not contact the Command Line Service, which is weird." ),
		
		/** When no flag and no 'pool' parameters is specified. */
		REQD_POOL( "No pool name specified. Use the 'all' flag to flush all." ),
		
		/** When both the 'pool' parameters and the 'all' flag are specified. */
		EXCL_ALL_POOL( "'pool' and 'all' are mutually exclusive." ),
		
		/** When both the 'debug' and 'normal' flags are specified. */
		EXCL_DEBUG_NORM( "'debug' and 'normal' are mutually exclusive." ),
		
		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS( "" );
		
		/** The message the enum item will convey. */
		private final String message;
		
		/**
		 * Initializes the enum member with a human-readable error message.
		 * 
		 * @param message The error message this enum member will contain.
		 */
		private Errors( String message ) {
			this.message = message;
		}
		
		@Override
		public final String getMessage() {
			return message;
		}
	}
	/** The publically available name of this module. */
	public static final String MODULE_NAME = "meta";

	/** A map of actions provided by this module. */
	private static final TreeMap< String, CommandLineAction< MetaModule > > actions;
	
	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;
	
	static {
		actions = new TreeMap< String, CommandLineAction< MetaModule > >();
		
		actions.put(
			"counter",
			new CommandLineAction< MetaModule >() {
				private final Set< String > parameters = stringSet();
				private final Set< String > flags = stringSet();
				private final String help = constructHelp( parameters, flags, false );

				@Override
				public void handle(
					MetaModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					try {
						response.sendOut(
							String.valueOf( module.cliReference.get().getConnectionCounter() ) +
							'\n'
						);
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_CLI, exception );
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_CLI, exception );
					} catch ( IOException exception ) {
						throw new ModuleOutputException( exception );
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"flush",
			new CommandLineAction< MetaModule >() {
				private final Set< String > parameters = stringSet( "pool" );
				private final Set< String > flags = stringSet( "all" );
				private final String help = constructHelp( parameters, flags, false );
				
				@Override
				public void handle(
					MetaModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					String pool;
					
					pool = request.getParameter( "pool" );
					try {
						if ( request.getFlag( "all" ) ) {
							if ( null == pool ) {
								module.cliReference.get().flushAllPools();							// Could be done directly, but...
							} else {
								throw new ModuleSpecificException( Errors.EXCL_ALL_POOL );
							}
						} else {
							if ( null == pool ) {
								throw new ModuleSpecificException( Errors.REQD_POOL );
							} else {
								module.cliReference.get().flushPool( pool );
							}
						}
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_CLI, exception );
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_CLI, exception );
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"output",
			new CommandLineAction< MetaModule >() {
				private final Set< String > parameters = stringSet();
				private final Set< String > flags = stringSet( "debug", "normal" );
				private final String help = constructHelp( parameters, flags, false );
				
				@Override
				public void handle(
					MetaModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					try {
						if ( request.getFlag( "debug" ) ) {
							if ( request.getFlag( "normal" ) ) {
								throw new ModuleSpecificException( Errors.EXCL_DEBUG_NORM );
							} else {
								module.cliReference.get().switchToDebugOutput();
								response.sendOut( "Switched to verbose debugging output.\n" );
							}
						} else {
							module.cliReference.get().switchToNormalOutput();
							response.sendOut( "Switched to normal output.\n" );
						}
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_CLI, exception );
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_CLI, exception );
					} catch ( IOException exception ) {
						throw new ModuleOutputException( exception );
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		StringBuilder builder = new StringBuilder();
		for ( String action : actions.keySet() ) {
			builder.append( action ).append( '\n' );
		}
		ACTIONS_LIST = builder.toString();
		
		actions.put(
			"help",
			new CommandLineAction< MetaModule >() {
				
				@Override
				public void handle(
					MetaModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					try {
						response.sendOut( ACTIONS_LIST );
					} catch ( IOException exception ) {
						throw new ModuleOutputException( exception );
					}
				}
				
				@Override
				public String getHelpString() {
					return "";
				}
			}
		);
	}
	
	/** A special reference to the ResultsRepository. */
	private final ServiceReference< CommandLineInterface > cliReference;
	
	/**
	 * Initializes a new module to the default state ready for use.
	 */
	public MetaModule() {
		TaskManagerReference taskManagerReference;
		
		taskManagerReference = new TaskManagerReference();
		this.cliReference = new ServiceReference< CommandLineInterface >(
			taskManagerReference,
			Names.COMMAND_LINE_SERVICE_NAME,
			CommandLineService.RMI_MAIN_IFACE,
			Names.COMMAND_LINE_SERVICE_HUMAN_NAME
		);		
	}
	
	@Override
	protected String getName() {
		return MODULE_NAME;
	}

	@Override
	protected void handleAction(
		String action,
		CommandLineRequest request,
		CommandLineResponse response
	) throws ModuleSpecificException, ModuleOutputException {
		CommandLineAction< MetaModule > act;

		act = actions.get( action );
		if ( null == act ) {
			throw new ModuleSpecificException( Errors.UNKN_ACTION, " (" + action + ')' );
		}
		if ( request.getFlag( "help" ) ) {
			try {
				response.sendOut( act.getHelpString() );
			} catch ( IOException exception ) {
				throw new ModuleOutputException( exception );
			}
		} else {
			act.handle( this, request, response );
		}
	}

	@Override
	protected void restoreState() {
		cliReference.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
