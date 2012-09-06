/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task.download.svn;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import cz.cuni.mff.been.task.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Tar;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;

/**
 * <p>Performs a checkout from a Subversion repository. If no revision is
 * specified, then it will checkout the HEAD.</p>
 * 
 * <b>Task properties:</b><br>
 * <ul>
 * <li>{@value #URL}: <br>
 * 		- URL of the SVN repository <br>
 * 		- mandatory <br>
 * <li>{@value #REVISION}: <br>
 * 		- if this task property is set, then the revision number in its value
 * 		will be checked out from the SVN repository<br>
 * <li>{@value #TIMESTAMP}: <br>
 * 		- if this task property is set, then the revision from the time specified
 * 		in its value will be checked out from the SVN repository. The time
 * 		must be specified in milliseconds from epoch. <br>
 * </ul>
 * 
 * @author Jaroslav Urban
 */
public class SVNCheckout extends Job {
	/** Task property name for the URL of the SVN repository. */
	public static final String URL = "url";
	/** Task property name for revision number. */
	public static final String REVISION = "revision";
	/** Task property name for revision date (java.lang.Long timestamp). */
	public static final String TIMESTAMP = "timestamp";
	/** Name of analysis of target dataset */
	public static final String RR_ANALYSIS_NAME = "rr.analysis";
    /** Target dataset to which file reference will be saved */
	public static final String RR_DATASET_NAME = "rr.dataset";
		
	
	/* name of archive to which downloaded files will be zipped and tarred */
	private static final String SOURCE_ARCHIVE_NAME = "source-code.tar.gz";
	
	/* dataset's field for storing timestamp */
    private static final String TIMESTAMP_FIELD_NAME = "timestamp";
    
    /* dataset's field for storing revision number */
    private static final String REVISION_FIELD_NAME = "revision";
    
    /* dataset's field for storing file id */
    private static final String FILE_ID_FIELD_NAME = "fileid";
    
    /** If set, the task will be run in dry mode (not downloading anything) */
    public static final String SIMULATE = "simulate";


	/** URL to checkout. */
	private String url;
	/** <tt>true</tt> if HEAD revision should be checked out.*/
	private boolean revisionHead = false;
	
	/** Revision number to checkout. */
	private Long revision;
	/** Revision date to checkout. */
	private Long timestamp;

	/** analysis name where to upload source */ 
	private String analysisName; 
	/** dataset name where to upload source */
	private String datasetName;
	/** file agent pluggable module */
	private FileAgentPluggableModule fileAgentModule;
	private boolean simulate;
	
	/**
	 * Allocates a new <code>SVNCheckout</code> object.
	 * 
	 * @throws cz.cuni.mff.been.task.TaskInitializationException
	 */
	public SVNCheckout() throws TaskInitializationException {
		super();
	}


	@Override
	protected void checkRequiredProperties() throws TaskException {
		
		analysisName = getTaskProperty(RR_ANALYSIS_NAME);
		datasetName = getTaskProperty(RR_DATASET_NAME);
		
		if ((url = getTaskProperty(URL)) == null) {
			//throw new TaskException(URL + " task property not set");
			logInfo("Using default URL");
			url="http://shiva.ms.mff.cuni.cz/svn/xampler/trunk";
		}
		logInfo("URL: " + url);
		
		String tstamp = getTaskProperty(TIMESTAMP);
		String rev = getTaskProperty(REVISION);
		
		if (tstamp != null) {
			try {
				timestamp = Long.valueOf(tstamp);
			} catch(NumberFormatException e) {
				throw new TaskException("Error parsing \""+TIMESTAMP+"\" property");
			}
		}
		
		if (rev != null) {
			try {
				revision = Long.valueOf(rev);
			} catch(NumberFormatException e) {
				throw new TaskException("Error parsing \""+REVISION+"\" property");
			}
		}
		
		if ((timestamp != null) && (revision != null)) {
			throw new TaskException("Both \""+REVISION+"\" and \""+TIMESTAMP+"\" properties are specified at a time.");
		}
		
		if ((timestamp == null) && (revision == null)) {
			throw new TaskException("None of \""+REVISION+"\" and \""+TIMESTAMP+"\" properties specified.");
		}
		
		//if ((date == null) && (number == null)) {
		//	revisionHead = true;
		//  logInfo("Using HEAD revision");
		//}
		
		simulate = ( getTaskProperty(SIMULATE) != null);
	}

	@Override
	protected void run() throws TaskException {
		/* load pluggable modules */
		loadPluggableModules();
		
		/* files */
		File srcArchive = new File(getWorkingDirectory(),SOURCE_ARCHIVE_NAME);
		
		/* do checkout */
		logInfo("Checking out from SVN");
		// initialise the JavaSVN library to work with http:// and https:// 
		DAVRepositoryFactory.setup();
		// initialise the JavaSVN library to work with svn:// and svn+ssh://
		SVNRepositoryFactoryImpl.setup();
		
		SVNClientManager clientManager = SVNClientManager.newInstance();
		SVNUpdateClient client = clientManager.getUpdateClient();
		
		SVNRevision rev = null;
		if (revisionHead) {
			rev = SVNRevision.HEAD;
		}
		if (timestamp != null) {
			rev = SVNRevision.create(new Date(timestamp));
		}
		if (revision != null) {
			rev = SVNRevision.create(revision.longValue());
		}
		
		if (!simulate) {
			try {
				client.doCheckout(SVNURL.parseURIEncoded(url), 
						new File(getTempDirectory()), 
						rev, 
						rev, 
						true);
			} catch (SVNException e) {
				throw new TaskException("Cannot checkout: " + e.getMessage(), e);
			}
		}
		
		logInfo("Tarring sources.");
		
		/* tar downloaded files */
		
		try {
			Tar.tar(getTempDirectory(),srcArchive.getAbsolutePath(),"gzip");
		} catch (AntTaskException e) {
			throw new TaskException("Can't create source archive",e);
		}
		
		logInfo("Saving sources to RR.");
		
		/* save tarred sources to RR */
		try {			
			
			FileAgent agent = fileAgentModule.createRRFileAgent(analysisName, datasetName, FILE_ID_FIELD_NAME);
			
			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			if (revision != null) {
				tags.put(REVISION_FIELD_NAME, revision);
			}
			if (timestamp != null) {
				// TODO A temporary hack to work around timestamp definition mismatch.
				// While the checkout task expects the timestamp to be a long integer,
				// the surrounding generator structure defines the timestamp as a
				// string. These should be unified (certainly some unnecessary
				// conversions and loss of precision occur now).
				
				DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone (TimeZone.getTimeZone ("GMT"));
				String dateString = dateFormat.format (new Date (timestamp));

				tags.put(TIMESTAMP_FIELD_NAME, dateString);
			}
			
			agent.storeFile(srcArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error storing sources",e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error storing sources",e);
		}   
		
		logInfo("Sources stored to RR.");
	}
	
	private void loadPluggableModules() throws TaskException {
		logInfo("Loading pluggable modules...");
		
		try {
			fileAgentModule = CurrentTaskSingleton.getTaskHandle().getPluggableModule(
					FileAgentPluggableModule.class, "fileagent", "1.0");
			
		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading pluggable module", e);
		} 
	}
}
