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
package cz.cuni.mff.been.common.anttasks;

import org.junit.Test;

/**
 *  
 *
 *  @author: Jan Tattermusch
 */
public class CvsClientTest {
	
	@Test
	public void dummyTest() {
		/* this test is here just to not break ant test target */
	}
	
//	/**
//	 * Tests whether checkout failure
//	 * throws an exception
//	 * @throws Exception
//	 */
//	@Test(expected=AntTaskException.class)
//	public void testCVSCheckout() throws Exception {
//
//		Cvs.checkout(":pserver:anonymous@nonexistentserver.cz:/cvsroot/omniorb", null, null, "somemodule", "sometag", "2009-11-17 15:33", "tmp/cvsclienttest");
//	}
//	*/
	
	/*@Test//(expected=AntTaskException.class)
	public void testCVSCheckout2() throws Exception {

	Cvs.checkout(":pserver:anonymous@omniorb.cvs.sourceforge.net:/cvsroot/omniorb",
			null,null,"omni","omni4_0_develop","2 Jan 2005","/tmp/data");
	}*/
}
