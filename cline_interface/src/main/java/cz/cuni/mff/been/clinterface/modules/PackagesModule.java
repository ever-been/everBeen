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
import java.util.ArrayList;
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
import cz.cuni.mff.been.clinterface.adapters.InputStreamEndMonitor;
import cz.cuni.mff.been.clinterface.adapters.OutputStreamEndMonitor;
import cz.cuni.mff.been.clinterface.ref.ServiceReference;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.clinterface.writers.PackageMetadataWriter;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.DownloadHandle;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.common.UploadHandle;
import cz.cuni.mff.been.softwarerepository.InputStreamTransporter;
import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.OutputStreamTransporter;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.task.Task;

/**
 * A command line interface component that corresponds to the packages listing screen.
 * 
 * @author Andrej Podzimek
 */
public final class PackagesModule extends CommandLineModule {

	/**
	 * Error messages reported by this module.
	 * 
	 * @author Andrej Podzimek
	 */
	private enum Errors implements Message {
		
		/** Unknown action name. */
		UNKN_ACTION( "Unknown action." ),
		
		/** Could not connet to the Software Repository. */
		CONN_SR( "Failed to contact the Software Repository." ),
				
		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS( "" ),
		
		/** Invalid regular expression used. */
		INVD_REGEXP( "Illegal regular expression." ),
		
		/** Invalid package file name, package not found. */
		INVD_FILE( "Package file not found." ),
		
		/** Package download has failed. */
		FAIL_DOWNLOAD( "Package download failed." ),
		
		/** Package upload has failed. */
		FAIL_UPLOAD( "Package upload failed." ),
		
		/** The package file has been rejected. */
		FAIL_REJECTED( "Package has been rejected." ),
		
		/** Something failed in the callback interface, which should not happen. */
		FAIL_MATCH( "Package matching has failed. This is a bug." );
		
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
	public static final String MODULE_NAME = "packages";
	
	/** A map of actions provided by this module. */
	private static final TreeMap< String, CommandLineAction< PackagesModule > > actions;
	
	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;
	
	/** Active waiting delay. Active waiting is needed to overcome design flaws of SW Repository. */
	private static final int DELAY = 100;
	
	/**
	 * A matcher that matches based on a regular expression.
	 * 
	 * @author Andrej Podzimek
	 */
	private static final class MatchRegex implements PackageQueryCallbackInterface {
		
		private static final long	serialVersionUID	= -1766515231022319405L;
		
		/** The pattern to use for matching. */
		private final Pattern pattern;

		/** 
		 * Initializes a new regexp-based matcher.
		 * 
		 * @param pattern The pattern to use for matching.
		 */
		MatchRegex( Pattern pattern ) {
			this.pattern = pattern;
		}
		
		@Override
		public boolean match( PackageMetadata metadata ) throws MatchException {
			return pattern.matcher( metadata.getName() ).matches();
		}
	}
	
	/**
	 * A matcher that matches everything.
	 * 
	 * @author Andrej Podzimek
	 */
	private static final class MatchEverything implements PackageQueryCallbackInterface {
		
		private static final long	serialVersionUID	= 6426026460455689181L;

		@Override
		public boolean match( PackageMetadata metadata ) throws MatchException {
			return true;
		}
	}

	static {
		actions = new TreeMap< String, CommandLineAction< PackagesModule > >();
		
		actions.put(
			"list",
			new CommandLineAction< PackagesModule >() {
				private final MatchEverything MATCH_EVERYTHING = new MatchEverything();
				private final Set< String > parameters = stringSet( "pattern" );
				private final Set< String > flags = stringSet( "desc" );
				private final Set< String > filteringFlags = stringSet();
				{
					for ( PackageType type : PackageType.values() ) {
						flags.add( type.toString() );
						filteringFlags.add( type.toString() );
					}
				}				
				private final String help = constructHelp( parameters, flags, false );
				
				@Override
				public void handle(
					PackagesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException, ModuleOutputException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					SoftwareRepositoryInterface softwareRepository;
					PackageMetadataWriter writer;
					String pattern;
					Pattern cpattern;
					boolean[] types;
					ArrayList< String > typeNames;

					typeNames = request.getFlagsThatExist( filteringFlags );
					if ( null == typeNames ) {
						types = null;
					} else {
						types = new boolean[ PackageType.values().length ];							// Initialized to false.
						for ( String typeName : typeNames ) {
							types[ PackageType.realValueOf( typeName ).ordinal() ] = true;
						}
					}

					pattern = request.getParameter( "pattern" );
					writer = new PackageMetadataWriter( response );
					
					try {
						softwareRepository = module.softwareRepositoryReference.get();
						if ( null == pattern ) {
							if ( null == types ) {
								if ( request.getFlag( "desc" ) ) {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages( MATCH_EVERYTHING )
									) {
										writer.sendLineXtend( metadata );
									}
								} else {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages( MATCH_EVERYTHING )
									) {
										writer.sendLinePlain( metadata );
									}
								}
							} else {
								if ( request.getFlag( "desc" ) ) {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages( MATCH_EVERYTHING )
									) {
										if ( types[ metadata.getType().ordinal() ] ) {
											writer.sendLineXtend( metadata );
										}
									}
								} else {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages( MATCH_EVERYTHING )
									) {
										if ( types[ metadata.getType().ordinal() ] ) {
											writer.sendLinePlain( metadata );
										}
									}
								}
							}
						} else {
							cpattern = Pattern.compile( pattern );
							if ( null == types ) {
								if ( request.getFlag( "desc" ) ) {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages(
											new MatchRegex( cpattern )
										)
									) {
										writer.sendLineXtend( metadata );
									}
								} else {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages(
											new MatchRegex( cpattern )
										)
									) {
										writer.sendLinePlain( metadata );
									}
								}
							} else {
								if ( request.getFlag( "desc" ) ) {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages(
											new MatchRegex( cpattern )
										)
									) {
										if ( types[ metadata.getType().ordinal() ] ) {
											writer.sendLineXtend( metadata );
										}
									}
								} else {
									for (
										PackageMetadata metadata
										: softwareRepository.queryPackages(
											new MatchRegex( cpattern )
										)
									) {
										if ( types[ metadata.getType().ordinal() ] ) {
											writer.sendLinePlain( metadata );
										}
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
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					} catch ( MatchException exception ) {
						throw new ModuleSpecificException( Errors.FAIL_MATCH, exception );
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
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
			"upload",
			new CommandLineAction< PackagesModule >() {
				private final Set< String > parameters = stringSet();
				private final Set< String > flags = stringSet();
				private final String help = constructHelp( parameters, flags, parameters, true );
				
				@Override
				public void handle(
					PackagesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, true );		// WOW! We want a blob!
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					SoftwareRepositoryInterface softwareRepository;
					InputStreamTransporter transporter;
					InputStreamEndMonitor monitor;
					UploadHandle handle;

					monitor = new InputStreamEndMonitor();
					try {
						transporter = new InputStreamTransporter(
							request.getBlobStream(),
							monitor
						);
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					}

					try {
						softwareRepository = module.softwareRepositoryReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );							
					}

					try {
						synchronized ( monitor ) {
							handle = softwareRepository.beginPackageUpload( transporter );
							for ( ; ; ) {
								try {
									monitor.wait();
									break;
								} catch ( InterruptedException e ) {}
							}
						}

						for ( boolean sleepOn = true; sleepOn; ) {
							switch ( softwareRepository.getPackageUploadStatus( handle ) ) {
								case ACCEPTED:
									sleepOn = false;
									break;
								case ERROR:
									throw new ModuleSpecificException( Errors.FAIL_UPLOAD );
								case REJECTED:
									errors = new StringBuilder();
									for (
										String message
										: softwareRepository.getUploadErrorMessages( handle )
									) {
										errors
										.append( '\n' )												// Initial '\n' will be discarded.
										.append( message );
									}
									throw new ModuleSpecificException(
										Errors.FAIL_REJECTED,
										errors
									);
								default:
									try {
										Thread.sleep( DELAY );
									} catch ( InterruptedException exception ) {}
									Task.getTaskHandle().logInfo( "Had to wait for upload." );
							}
						}
						softwareRepository.endPackageUpload( handle );
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"download",
			new CommandLineAction< PackagesModule >() {
				private final Set< String > parameters = stringSet( "filename" );
				private final Set< String > flags = stringSet();
				private final String help = constructHelp( parameters, flags, parameters, false );
				
				@Override
				public void handle(
					PackagesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}

					SoftwareRepositoryInterface softwareRepository;
					OutputStreamTransporter transporter;
					OutputStreamEndMonitor monitor;
					DownloadHandle handle;
					String fileName;

					monitor = new OutputStreamEndMonitor();
					try {
						transporter = new OutputStreamTransporter(
							response.getStandardOutputStream(),
							monitor
						);
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					}

					try {
						softwareRepository = module.softwareRepositoryReference.get();
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					}

					fileName = request.getParameter( "filename" );
					try {
						synchronized ( monitor ) {													// Condition Variable semantics...
							try {
								handle = softwareRepository.beginPackageDownload(
									fileName,
									transporter
								);
							} catch ( IllegalArgumentException exception ) {
								throw new ModuleSpecificException(
									Errors.INVD_FILE,
									" (" + fileName + ')'
								);
							}
							for ( ; ; ) {
								try {
									monitor.wait();													// Notified when the stream ends.
									break;
								} catch ( InterruptedException exception ) {}																	
							}
						}

						for ( boolean sleepOn = true; sleepOn; ) {
							switch ( softwareRepository.getPackageDownloadStatus( handle ) ) {
								case SUCCEEDED:
									sleepOn = false;
									break;
								case ERROR:
									throw new ModuleSpecificException(
										Errors.FAIL_DOWNLOAD,
										fileName
									);
								default:
									try {
										Thread.sleep( DELAY );
									} catch ( InterruptedException e ) {}
									Task.getTaskHandle().logInfo( "Had to wait for download." );
							}
						}
						softwareRepository.endPackageDownload( handle );							// Release the resources.
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					}
				}

				@Override
				public String getHelpString() {
					return help;
				}
			}
		);
		
		actions.put(
			"delete",
			new CommandLineAction< PackagesModule >() {				
				private final Set< String > parameters = stringSet( "filename" );
				private final Set< String > flags = stringSet();
				private final String help = constructHelp( parameters, flags, parameters, false );
				
				@Override
				public void handle(
					PackagesModule module,
					CommandLineRequest request,
					CommandLineResponse response
				) throws ModuleSpecificException {
					StringBuilder errors;
					
					errors = verifyArguments( request, parameters, flags, parameters, false );
					if ( null != errors ) {
						throw new ModuleSpecificException( Errors.INVD_PARAMS, errors );
					}
					
					String fileName;

					fileName = request.getParameter( "filename" );
					
					try {
						if ( !module.softwareRepositoryReference.get().deletePackage( fileName ) ) {
							throw new ModuleSpecificException(
								Errors.INVD_FILE,
								" (" + fileName + ')'
							);
						}
					} catch ( ComponentInitializationException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
					} catch ( RemoteException exception ) {
						throw new ModuleSpecificException( Errors.CONN_SR, exception );
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
			new CommandLineAction< PackagesModule >() {
				
				@Override
				public void handle(
					PackagesModule module,
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
	
	/** A reference to the Software Repository. */
	private final ServiceReference< SoftwareRepositoryInterface >
	softwareRepositoryReference;
	
	/**
	 * Initializes a new module to the default state ready for use.
	 */
	public PackagesModule() {
		this.taskManagerReference = new TaskManagerReference();
		this.softwareRepositoryReference = new ServiceReference< SoftwareRepositoryInterface >(
			taskManagerReference,
			SoftwareRepositoryService.SERVICE_NAME,
			SoftwareRepositoryService.RMI_MAIN_IFACE,
			SoftwareRepositoryService.SERVICE_HUMAN_NAME
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
		CommandLineAction< PackagesModule > act;
		
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
		taskManagerReference.drop();
		softwareRepositoryReference.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
