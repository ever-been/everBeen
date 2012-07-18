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
package cz.cuni.mff.been.resultsrepositoryng.data.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;

public class GroupTest {
	
	@Test
	public void testGroupBy1() throws Exception {
		
		GroupBy g = new GroupBy(getData1(), new String[] {"data1"});
		
		Set<DataHandleTuple> result = g.groups();
		
		Assert.assertEquals(2, result.size());
		{
			DataHandleTuple key = new DataHandleTuple();
			key.set("data1", DataHandle.create(DataType.STRING, "aaa"));
			Assert.assertTrue( result.contains(key));
			
			Collection<DataHandleTuple> c = g.getGroup(key);
			Assert.assertEquals(2, c.size());
		}
		{
			DataHandleTuple key = new DataHandleTuple();
			key.set("data1", DataHandle.create(DataType.STRING, "bbb"));
			Assert.assertTrue( result.contains(key));
			
			Collection<DataHandleTuple> c = g.getGroup(key);
			Assert.assertEquals(1, c.size());
		}
		
	}
	
	private List<DataHandleTuple> getData1() {
		List<DataHandleTuple> result = new ArrayList<DataHandleTuple>();
		
		{
			DataHandleTuple d = new DataHandleTuple();
			d.set("data1", DataHandle.create(DataType.STRING, "aaa"));
			d.set("data2", DataHandle.create(DataType.INT, 446));
			result.add(d);
		}
		
		{
			DataHandleTuple d = new DataHandleTuple();
			d.set("data1", DataHandle.create(DataType.STRING, "aaa"));
			d.set("data2", DataHandle.create(DataType.INT, 4446646));
			result.add(d);
		}
		
		{
			DataHandleTuple d = new DataHandleTuple();
			d.set("data1", DataHandle.create(DataType.STRING, "bbb"));
			d.set("data2", DataHandle.create(DataType.INT, 446));
			result.add(d);
		}
		
		return result;
	}

}
