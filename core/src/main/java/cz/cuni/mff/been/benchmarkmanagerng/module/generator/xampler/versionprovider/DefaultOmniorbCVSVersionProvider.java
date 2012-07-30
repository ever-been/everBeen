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
package cz.cuni.mff.been.benchmarkmanagerng.module.generator.xampler.versionprovider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.mff.been.benchmarkmanagerng.AnalysisException;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.SourceKey;
import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Cvs;

/**
 * Version provider for OmniORB and in CVS version control.
 * 
 * Supplies set of versions that are available in version control.
 * 
 * This class get the version list from Cvs.rlog task.
 * cvs rlog is invoked on update.log file (this file
 * is always changed with omniORB commit).
 *  
 * @author Jan Tattermusch
 *
 */
public class DefaultOmniorbCVSVersionProvider {
	
	
	/**
	 * on which file to invoke RLog
	 */
	private static final String UPDATE_LOG_FILE = "update.log";
	
	/**
	 * Returns set of versions based on omniorb's update log.
	 * 
	 * Update log is a file in omniorb's repository root that contains
	 * information about omniorb changes. Update log is analyzed 
	 * instead of CVS history because it's hard to extract a commit 
	 * log of a specific branch from CVS.
	 * 
	 * This is how method works:
	 * 1. omniorb's root is checked out (with non-recursive option)
	 * 2. update log file is loaded and analyzed
	 * 3. list of versions is returned
	 * 
	 * @param repository CVS repository URL
	 * @param password CVS repostory password
	 * @param cvsRsh CVS rsh 
	 * @param module CVS module
	 * @param tag CVS tag (branch)
	 * @param fromDate retrieve only versions newer than this date
	 * @return set of versions available in CVS
	 * @throws AnalysisException
	 */
	public Set<SourceKey> getAvailableVersions(String repository, String password, String cvsRsh, String module, String tag, Date fromDate) throws AnalysisException {
		
		String rlog = null;
		try {
			rlog = getCvsRLog(repository, password, cvsRsh, module + "/" + UPDATE_LOG_FILE, tag);
		} catch (AntTaskException e) {
			throw new AnalysisException("Version provider could not get CVS rlog.",e);
		} 
		
		try {
			Set<SourceKey> versions = findVersionTimestamps(getLogLines(rlog), fromDate);
			return versions;
		} catch(IOException e) {
			throw new AnalysisException("Version provider could not parse rlog.",e);
		}
		
	}
	
	/**
	 * Parses cvs log and returns list of versions
	 * @param log  cvs log contents
	 * @param dateFrom return only versions new than this date
	 * @return return set of source versions
	 * @throws AnalysisException
	 */
	private SortedSet<SourceKey> findVersionTimestamps(List<String> log, Date dateFrom) throws AnalysisException {
		SortedSet<SourceKey> versions = new TreeSet<SourceKey>();
		
		Pattern dateLine = Pattern.compile("^date: .*");
		
		DateFormat logDf = new SimpleDateFormat("'date:' yyyy/MM/dd HH:mm:ss");
		logDf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		for (String line : log) {
			Matcher m = dateLine.matcher(line);
			if (m.matches()) {
				String beforeSemicolon = line.split(";")[0];
				String versionString = null;
				try {
					Date parsedDate = logDf.parse(beforeSemicolon);
					
					if (dateFrom != null) {
						if (parsedDate.compareTo(dateFrom) >= 0) {
							versionString = df.format(parsedDate);
							versions.add(new SourceKey(versionString));
						}
					} else {
						versionString = df.format(parsedDate);
						versions.add(new SourceKey(versionString));
					}
				} catch (ParseException e) {
					throw new AnalysisException("Error parsing CVS log", e);
				}
				
			}
		}
		
		return versions;
	}

	/**
	 * Get CVS rlog. 
	 * @param repository CVS repository URL
	 * @param password CVS password
	 * @param cvsRsh CVS rsh
	 * @param module CVS module
	 * @param tag CVS tag (branch)
	 * @return CVS rlog content
	 * @throws AntTaskException
	 */
	private String getCvsRLog(String repository, String password, String cvsRsh, String module, String tag) throws AntTaskException {
		return Cvs.rlog(repository, password, cvsRsh, module, tag);
	}

	/**
	 * Splits log into list of lines.
	 * @param s log to split
	 * @return lines
	 * @throws IOException
	 */
	public static List<String> getLogLines(String s) throws IOException {
		List<String> lines = new ArrayList<String>();	
		String thisLine;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(s));
			while ((thisLine = br.readLine()) != null)
				lines.add(thisLine.trim());
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if(br!=null) try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}
}
