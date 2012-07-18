/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.database;

import java.io.File;

/**
 * This program will build default alias definition file to use with Host Manager. It will create
 * output file in the data directory in the been home.
 * 
 * Note: this is only internal tool, do not use it...
 *
 * @author Branislav Repcek
 */
@SuppressWarnings("unused")
public class AliasDefinitionBuilder {

	private AliasDefinitionBuilder() {
	}
	
	/**
	 * Output directory. Currently directory from which files are packed into bpk package.
	 */
	private static final String OUTPUT_DIRECTORY = 
		System.getenv("BEEN_HOME") + "/resources/packages/hostmanager/";
	
	/**
	 * Name of the output file.
	 */
	private static final String OUTPUT_FILE_NAME = "alias-definitions";
	
	/**
	 * Restriction which will match any Windows version.
	 */
	private static final RSLRestriction restrictionWindows = 
		new RSLRestriction("family == \"Windows\"");
	
	/**
	 * Restriction which will match any Linux distribution.
	 */
	private static final RSLRestriction restrictionLinux = 
		//new ObjectRestriction("", "family", new ValueString("Linux"));
		new RSLRestriction("family == \"Linux\"");
	
	/**
	 * Restriction which will match any Solaris version.
	 */
	private static final RSLRestriction restrictionSolaris = 
		//new ObjectRestriction("", "family", new ValueString("Solaris"));
		new RSLRestriction("family == \"Solaris\"");
	
	/**
	 * Place all alias defintions into this array.
	 */
	private static final SoftwareAliasDefinition []definitions = {
		newDefinition("Microsoft Visual Studio", "${name}", "${vendor}", "${version}",
				restrictionWindows, "name =~ /.*microsoft.*visual.*studio.*/i"),
				
		newDefinition("Microsoft .NET Framework", "${name}", "${vendor}", "${version}",
				restrictionWindows, "name =~ /.*\\.net framework.*/i && vendor =~ /.*Microsoft.*/"),
				
		newDefinition("Apache Tomcat", "${name}", "${vendor}", "${version}",
				restrictionWindows, "name =~ /.*apache tomcat.*/i"),
				
		newDefinition("Java Runtime Environment", "${name}", "${vendor}", "${version}",
				restrictionWindows, "name =~ /.*J2SE Runtime Environment.*/i"),
				
		newDefinition("Java JDK", "${name}", "${vendor}", "${version}",
				restrictionWindows, "name =~ /.*J2SE Development Kit.*/i")
	};
	
	/**
	 * @param args Command-line arguments (ignored). 
	 */
	public static void main(String[] args) {

		// create output directory if it does not exists
		{
			System.out.println(OUTPUT_DIRECTORY);
			File od = new File(OUTPUT_DIRECTORY);
		
			od.mkdirs();
		}
		
		SoftwareAliasList aliasList = new SoftwareAliasList();
		
		for (SoftwareAliasDefinition def: definitions) {
			aliasList.add(def);
		}
		
		System.out.println("Alias definition table size: " + definitions.length + " definitions");
		System.out.print("Writing output file...");
		try {
			aliasList.save(OUTPUT_DIRECTORY + OUTPUT_FILE_NAME);
		} catch (Exception e) {
			System.out.println("FAILED");
			e.printStackTrace();
			return;
		}
		System.out.println("OK");
	}
	
	/*
	 * Small hack to be able to create static array.
	 */
	private static SoftwareAliasDefinition newDefinition(String aliasName, String resultName, 
			String resultVendor, String resultVersion, RSLRestriction os, String appRestriction) {
			
			try {
				return new SoftwareAliasDefinition(aliasName, resultName, resultVendor, resultVersion, 
						os, new RSLRestriction(appRestriction));
			} catch (Exception e) {
				System.err.println("Alias error \"" + aliasName + "\", msg: " + e.getMessage());
				return null;
			}
		}
}
