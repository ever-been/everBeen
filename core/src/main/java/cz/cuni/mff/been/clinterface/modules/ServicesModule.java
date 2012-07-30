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
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cz.cuni.mff.been.clinterface.CommandLineAction;
import cz.cuni.mff.been.clinterface.CommandLineModule;
import cz.cuni.mff.been.clinterface.CommandLineRequest;
import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.clinterface.ModuleOutputException;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.adapters.ServiceHandle;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.clinterface.writers.LogRecordWriter;
import cz.cuni.mff.been.clinterface.writers.ServiceHandleWriter;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.IterableWrapper;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper.BootTask;

/**
 * Management of core BEEN services from the command line interface.
 * 
 * @author Andrej Podzimek
 */
public final class ServicesModule extends CommandLineModule {
	
	/**
	 * Error messages reported by this module.
	 * 
	 * @author Andrej Podzimek
	 */
	public enum Errors implements Message {
		
		/** Unknown action name. */
		UNKN_ACTION( "Unknown action." ),
		
		/** Action not implemented yet. */
		IMPL_ACTION( "Action not implemented yet. Volunteers?" ),
		
		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS( "" ),
		
		/** Invalid regular expression used. */
		INVD_REGEXP( "Illegal regular expression." ),
		
		/** An unknown RMI problem. */
		CONN_RMI( "Unknown RMI failure. Cannot contact the registry." ),
		
		/** When the Task Manager is not responding (RemoteException, IOException). */
		CONN_TM( "Could not contact the Task Manager." ),
		
		/** A weird timeout when waiting for the TM. */
		CONN_TM_TIMEOUT( "Timed out waiting for the service startup." ),
		
		/** Serivice lookup failed (which is an error outside CLI). */
		CONN_TM_LOOKUP( "Service lookup failed." ),
		
		/** Service is not registered (which is an error outside CLI). */
		CONN_TM_REG( "Service not registered." ),
		
		/** A failure occured when trying to stop service. */
		CONN_STOP_FAIL( "Failed to stop the service." ),
		
		/** A failure occured when trying to retrieve logs for task. */
		CONN_LOG_LOG( "Failed to retrieve logs." ),
		
		/** When reading stdin or stderr fails. */
		CONN_LOG_OUT( "Failed to retrieve task output." ),
		
		/** Invalid service name requested, not a boot service. */
		INVD_SERVICE( "Unknown service name." ),
		
		/** When 'from' can't be read as Long. */
		MALF_FROM( "The 'from' parameter is malformed." ),

		/** When 'to' can't be read as Long. */
		MALF_TO( "The 'to' parameter is malformed." ),

		/** A second attempt to start an already running service. */
		STAT_RUNNING( "The service is already running." ),
		
		/** An attempt to stop a service that is not running. */
		STAT_STOPPED( "The service is not running." ),
		
		/** An attempt to retrieve logs for task that has not run yet. */
		STAT_NOTYET( "The service has not run yet." );
		
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

	/** Name of this module. */
	public static final String MODULE_NAME = "csvcs";

	/** A map of actions provided by this module. */
	private static final TreeMap< String, CommandLineAction< ServicesModule > > actions;
	
	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;
	
	/** A list of BEEN core services. */
	private static final TreeMap< String, ServiceHandle > services;
	
	/** A list of BEEN core services in the order they are started/stopped */
	private static final LinkedList< ServiceHandle > orderedServices;
	
	/** Number of log lines to read at once. */
	private static final int LINES_AT_ONCE = 128;
	
	static {
		orderedServices = new LinkedList< ServiceHandle >();
		orderedServices.add( new ServiceHandle( BootTask.COMMAND_LINE_INTERFACE ) );
		orderedServices.add( new ServiceHandle( BootTask.HOST_MANAGER ) );
		orderedServices.add( new ServiceHandle( BootTask.SOFTWARE_REPOSITORY ) );
		orderedServices.add( new ServiceHandle( BootTask.RESULTS_REPOSITORY_NG ) );
		orderedServices.add( new ServiceHandle( BootTask.BENCHMARK_MANAGER_NG ) );
	
		services = new TreeMap< String, ServiceHandle >();
		for ( ServiceHandle handle : orderedServices ) {
			services.put( handle.getName(), handle );
		}

		actions = new TreeMap< String, CommandLineAction< ServicesModule > >();
		
		actions.put(
			"list",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > parameters = stringSet( "pattern" );
				private final Set< String > flags = stringSet( "all" );
				private final String help = constructHelp( parameters, flags, false );

				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
										
					TaskManagerInterface taskManager;
					ServiceHandleWriter writer;
					String pattern;
					Pattern cpattern;

					pattern = request.getParameter( "pattern" );
					writer = new ServiceHandleWriter( response );
					
					try {
						taskManager = module.taskManagerReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}
					
					for ( ServiceHandle handle : services.values() ) {
						handle.acquireData( taskManager );
					}
					
					try {
						if ( null == pattern ) {
							if ( request.getFlag( "all" ) ) {
								for ( ServiceHandle handle : services.values() ) {
									writer.sendLine( handle );
								}
							} else {
								for ( ServiceHandle handle : services.values() ) {
									if ( handle.getStatus() != null ) {
										writer.sendLine( handle );
									}
								}
							}
						} else {
							cpattern = Pattern.compile( pattern );
							if ( request.getFlag( "all" ) ) {
								for ( ServiceHandle handle : services.values() ) {
									if ( cpattern.matcher( handle.getName() ).matches() ) {
										writer.sendLine( handle );
									}
								}
							} else {
								for ( ServiceHandle handle : services.values() ) {
									if (
										handle.getStatus() != null &&
										cpattern.matcher( handle.getName() ).matches()
									) {
										writer.sendLine( handle );
									}
								}
							}
						}
					} catch ( PatternSyntaxException exception ) {
						throw new ModuleSpecificException(
							Errors.INVD_REGEXP,
							" /" + pattern + '/',
							exception
						);
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
			"start",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > parameters = stringSet( "service", "host" );
				private final Set< String > flags = stringSet( "debug" );
				private final String help = constructHelp( parameters, flags, parameters, false );

				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
										
					ServiceHandle handle;
					String service;

					service = request.getParameter( "service" );
					handle = services.get( service );
					if ( null == handle ) {
						throw new ModuleSpecificException(
							Errors.INVD_SERVICE,
							" (" + service + ')'
						);
					}

					try {
						handle.start(
							module.taskManagerReference.get(),
							request.getParameter( "host" ),
							request.getFlag( "debug" ) 
						);
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"stop",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > parameters = stringSet( "service" );
				private final Set< String > flags = stringSet();
				private final String help = constructHelp( parameters, flags, parameters, false );
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					ServiceHandle handle;
					String service;

					service = request.getParameter( "service" );
					handle = services.get( service );
					if ( null == handle ) {
						throw new ModuleSpecificException(
							Errors.INVD_SERVICE,
							" (" + service + ')'
						);
					}

					try {
						handle.stop( module.taskManagerReference.get() );
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}

				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"restart",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > parameters = stringSet( "service" );
				private final Set< String > flags = stringSet( "debug" );
				private final String help = constructHelp( parameters, flags, parameters, false );
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					ServiceHandle handle;
					String service;

					service = request.getParameter( "service" );
					handle = services.get( service );
					if ( null == handle ) {
						throw new ModuleSpecificException(
							Errors.INVD_SERVICE,
							" (" + service + ')'
						);
					}

					try {
						handle.restart(
							module.taskManagerReference.get(),
							request.getFlag( "debug" )
						);
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}

				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"start-all",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > parameters = stringSet( "host" );
				private final Set< String > flags = stringSet( "debug" );
				private final String help = constructHelp( parameters, flags, parameters, false );
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					TaskManagerInterface taskManager;
					
					try {
						taskManager = module.taskManagerReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}
					
					for ( ServiceHandle handle : orderedServices ) {
						handle.acquireData( taskManager );
						if ( null == handle.getStatus() ) {
							handle.start(
								taskManager,
								request.getParameter( "host" ),
								request.getFlag( "debug" )
							);
						}
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"stop-all",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > parameters = stringSet();
				private final Set< String > flags = stringSet();
				private final String help = constructHelp( parameters, flags, false );
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					TaskManagerInterface taskManager;
					
					try {
						taskManager = module.taskManagerReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}
					
					for (
						ServiceHandle handle :
						new IterableWrapper< ServiceHandle >( orderedServices.descendingIterator() )
					) {
						handle.acquireData( taskManager );
						if ( null != handle.getStatus() ) {
							handle.stop( taskManager );
						}
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"logs",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > allParameters = stringSet( "service", "from", "to" );
				private final Set< String > requiredParameters = stringSet( "service" );
				private final Set< String > flags = stringSet( "numbers" );
				private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false
				);
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;
					
					errors = verifyArguments(
						request,
						allParameters,
						flags,
						requiredParameters,
						false
					);
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					ServiceHandle handle;
					String service;
					String from, to;
					long lfrom, lto;
					TaskManagerInterface taskManager;
					LogRecordWriter writer;

					from = request.getParameter( "from" );
					try {
						lfrom = null == from ? 0 : Long.valueOf( from );
					} catch ( NumberFormatException exception ) {
						throw new ModuleSpecificException(
							Errors.MALF_FROM,
							" (" + from + ')',
							exception
						);
					}
					
					to = request.getParameter( "to" );
					try {
						lto = null == to ? Long.MAX_VALUE : Long.valueOf( to );
					} catch ( NumberFormatException exception ) {
						throw new ModuleSpecificException(
							Errors.MALF_TO,
							" (" + to + ')',
							exception
						);
					}
					
					service = request.getParameter( "service" );
					handle = services.get( service );
					if ( null == handle ) {
						throw new ModuleSpecificException(
							Errors.INVD_SERVICE,
							" (" + service + ')'
						);
					}

					try {
						taskManager = module.taskManagerReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}

					writer = new LogRecordWriter( response );
					try {
						if ( request.getFlag( "numbers" ) ) {
							long l = lfrom;
							for ( LogRecord record : handle.getLogs( taskManager, lfrom, lto ) ) {
								writer.sendLine( l++, record );
							}
						} else {
							for ( LogRecord record : handle.getLogs( taskManager, lfrom, lto ) ) {
								writer.sendLine( record );
							}
						}
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
			"stdout",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > allParameters = stringSet( "service", "from", "to" );
				private final Set< String > requiredParameters = stringSet( "service" );
				private final Set< String > flags = stringSet( "numbers" );
				private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false
				);
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;
					
					errors = verifyArguments(
						request,
						allParameters,
						flags,
						requiredParameters,
						false
					);
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					ServiceHandle handle;
					String service;
					String from, to;
					long lfrom, lto, lread;
					int iread;
					OutputHandle oHandle;
					TaskManagerInterface taskManager;
					String[] lines;
					
					from = request.getParameter( "from" );
					try {
						lfrom = null == from ? 0 : Long.valueOf( from );
					} catch ( NumberFormatException exception ) {
						throw new ModuleSpecificException(
							Errors.MALF_FROM,
							" (" + from + ')',
							exception
						);
					}
					
					to = request.getParameter( "to" );
					try {
						lto = null == to ? Long.MAX_VALUE - 1 : Long.valueOf( to );					// - 1 avoids later overflow.
					} catch ( NumberFormatException exception ) {
						throw new ModuleSpecificException(
							Errors.MALF_TO,
							" (" + to + ')',
							exception
						);
					}
					
					service = request.getParameter( "service" );
					handle = services.get( service );
					if ( null == handle ) {
						throw new ModuleSpecificException(
							Errors.INVD_SERVICE,
							" (" + service + ')'
						);
					}

					if ( lfrom > lto ) {
						lto = lfrom - 1;
					}
					
					try {
						taskManager = module.taskManagerReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}
					
					oHandle = handle.getStandardOutput( taskManager );

					try {
						oHandle.skipLines( lfrom );
					} catch ( IOException exception ) {
						throw new ModuleSpecificException( Errors.CONN_LOG_OUT, exception );
					}
					try {
						if ( request.getFlag( "numbers" ) ) {
							for ( ; ; ) {
								lread = lto - lfrom + 1;
								iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE : (int) lread;
								try {
									lines = oHandle.getNextLines( iread );
								} catch ( IOException exception ) {
									throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception
									);
								}
								for ( String line : lines ) {
									response.sendOut( lfrom++ + " " + line + '\n' );
								}
								if ( lines.length < LINES_AT_ONCE ) {								// 'to' can be huge.
									break;
								}
							}
						} else {
							for ( ; ; ) {
								lread = lto - lfrom + 1;
								iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE : (int) lread;
								try {
									lines = oHandle.getNextLines( iread );
								} catch ( IOException exception ) {
									throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception
									);
								}
								for ( String line : lines ) {
									response.sendOut( line + '\n' );
								}
								if ( lines.length < LINES_AT_ONCE ) {								// 'to' can be huge.
									break;
								}
								lfrom += lines.length;
							}
						}
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
			"stderr",
			new CommandLineAction< ServicesModule >() {
				private final Set< String > allParameters = stringSet( "service", "from", "to" );
				private final Set< String > requiredParameters = stringSet( "service" );
				private final Set< String > flags = stringSet( "numbers" );
				private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false
				);
				
				@Override
				public void handle(
					ServicesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;

					errors = verifyArguments(
						request,
						allParameters,
						flags,
						requiredParameters,
						false
					);
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}

					ServiceHandle handle;
					String service;
					String from, to;
					long lfrom, lto, lread;
					int iread;
					OutputHandle oHandle;
					TaskManagerInterface taskManager;
					String[] lines;

					from = request.getParameter( "from" );
					try {
						lfrom = null == from ? 0 : Long.valueOf( from );
					} catch ( NumberFormatException exception ) {
						throw new ModuleSpecificException(
							Errors.MALF_FROM,
							" (" + from + ')',
							exception
						);
					}

					to = request.getParameter( "to" );
					try {
						lto = null == to ? Long.MAX_VALUE - 1 : Long.valueOf( to );					// - 1 avoids later overflow.
					} catch ( NumberFormatException exception ) {
						throw new ModuleSpecificException(
							Errors.MALF_TO,
							" (" + to + ')',
							exception
						);
					}

					service = request.getParameter( "service" );
					handle = services.get( service );
					if ( null == handle ) {
						throw new ModuleSpecificException(
							Errors.INVD_SERVICE,
							" (" + service + ')'
						);
					}

					if ( lfrom > lto ) {
						lto = lfrom - 1;
					}

					try {
						taskManager = module.taskManagerReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_TM, exception );
					}

					oHandle = handle.getErrorOutput( taskManager );

					try {
						oHandle.skipLines( lfrom );
					} catch ( IOException exception ) {
						throw new ModuleSpecificException( Errors.CONN_LOG_OUT, exception );
					}
					try {
						if ( request.getFlag( "numbers" ) ) {
							for ( ; ; ) {
								lread = lto - lfrom + 1;
								iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE : (int) lread;
								try {
									lines = oHandle.getNextLines( iread );
								} catch ( IOException exception ) {
									throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception
									);
								}
								for ( String line : lines ) {
									response.sendOut( lfrom++ + " " + line + '\n' );
								}
								if ( lines.length < LINES_AT_ONCE ) {								// 'to' can be huge.
									break;
								}
							}
						} else {
							for ( ; ; ) {
								lread = lto - lfrom + 1;
								iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE : (int) lread;
								try {
									lines = oHandle.getNextLines( iread );
								} catch ( IOException exception ) {
									throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception
									);
								}
								for ( String line : lines ) {
									response.sendOut( line + '\n' );
								}
								if ( lines.length < LINES_AT_ONCE ) {								// 'to' can be huge.
									break;
								}
								lfrom += lines.length;
							}
						}
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
			new CommandLineAction< ServicesModule >() {
				
				@Override
				public void handle(
					ServicesModule module,
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
	
	/** A reference to the Task Manager. */
	private final TaskManagerReference taskManagerReference;

	/**
	 * Initializes a new module to the default state ready for use.
	 */
	public ServicesModule() {
		this.taskManagerReference = new TaskManagerReference();
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
		CommandLineAction< ServicesModule > act;

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
		for ( ServiceHandle handle : services.values() ) {
			handle.invalidate();
		}
		taskManagerReference.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
