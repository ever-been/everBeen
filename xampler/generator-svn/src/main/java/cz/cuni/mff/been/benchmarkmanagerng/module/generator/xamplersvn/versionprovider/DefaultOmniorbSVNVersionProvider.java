/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.benchmarkmanagerng.module.generator.xamplersvn.versionprovider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import cz.cuni.mff.been.benchmarkmanagerng.AnalysisException;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.SourceKey;

/**
 * Version provider for OmniORB and in SVN version control.
 * 
 * Supplies set of versions that are available in version control.
 * 
 * @author Jan Tattermusch
 *
 */
public class DefaultOmniorbSVNVersionProvider {
	
	/**
	 * Returns set of versions based on repository commit log.
	 * 
	 * @param repository SVN repository URL
	 * @param fromDate retrieve only versions newer than this date
	 * @return set of versions available in SVN
	 * @throws AnalysisException
	 */
	public Set<SourceKey> getAvailableVersions(String repository, Date fromDate) throws AnalysisException {

		// Initialize the JavaSVN library to work with http:// and https:// ... 
		DAVRepositoryFactory.setup();
		// Initialize the JavaSVN library to work with svn:// and svn+ssh:// ...
		SVNRepositoryFactoryImpl.setup();

		SVNClientManager clientManager = SVNClientManager.newInstance ();
		SVNLogClient clientLog = clientManager.getLogClient ();

		class LogEntryHandler implements ISVNLogEntryHandler
		{
			public Set<SourceKey> keys = new TreeSet<SourceKey> ();

			public long lastRevision;
			
			private DateFormat dateFormat;
			
			public LogEntryHandler ()
			{
				dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone (TimeZone.getTimeZone ("GMT"));
			}
			
			@Override
			public void handleLogEntry (SVNLogEntry entry) throws SVNException
			{
				// Remember the last revision in case we need to resume.
				lastRevision = entry.getRevision ();

				// Add the date of the entry into the set of versions.
				// The date is rounded up to the nearest second to
				// prevent conversions from YYYY-MM-DD HH:mm:ss
				// to milliseconds and back from ending just
				// before instead of just after a commit.
				Date dateObject = entry.getDate ();
				dateObject.setTime (dateObject.getTime () + 999);
				String dateString = dateFormat.format (dateObject);
				keys.add (new SourceKey (dateString));
			}
		}
		
		LogEntryHandler handler = new LogEntryHandler ();
		
		String [] paths = {""};
		
		// Getting the history is somewhat tricky when the repository has been copied around.
		// To tackle the common case of a directory being overwritten with another copy,
		// we try to resume one version before copy ...

		SVNRevision revision = SVNRevision.HEAD;
		boolean complete = false;
		boolean initial = true;
		
		while (!complete)
		{
			try
			{
				clientLog.doLog (
					SVNURL.parseURIEncoded (repository), paths,
					revision, revision, SVNRevision.create (fromDate),
					true, false, 0, handler);
				
				if (handler.lastRevision > 0)
				{
					revision = SVNRevision.create (handler.lastRevision - 1);
				}
				else
				{
					complete = true;
				}

				initial = false;
			}
			catch (SVNException e)
			{
				// An exception on the initial attempt to get the log is reported.
				if (initial) throw new AnalysisException ("Cannot get SVN log. " + e.getMessage (), e);

				// An exception later on probably means we just run beyond the first revision.
				complete = true;
			}
		}
		
		return (handler.keys);
	}
}
