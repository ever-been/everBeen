/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2010 Distributed Systems Research Group,
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
package cz.seznam.been.module.hintserver.generator;

import java.util.Comparator;
import java.util.UUID;

import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Class that stores information about one particular binary that is stored in
 * the ResultsRepository
 * 
 * @author Jiri Tauber
 */
public class BinaryData {

	//-----------------------------------------------------------------------//
	/**
	 * Comparator class for sorting BinaryData by version in ASC order.
	 * 
	 * @author Jiri Tauber
	 */
	public static class VersionComparator implements Comparator<BinaryData> {
		@Override
		public int compare(BinaryData o1, BinaryData o2) {
			if (o1.version < o2.version) return -1;
			if (o1.version > o2.version) return 1;
			return 0;
		}
	}

	/**
	 * Comparator class for sorting BinaryData by run ID in ASC order.
	 * 
	 * @author Jiri Tauber
	 */
	public static class RunComparator implements Comparator<BinaryData> {
		@Override
		public int compare(BinaryData o1, BinaryData o2) {
			if (o1.runCount < o2.runCount) return -1;
			if (o1.runCount > o2.runCount) return 1;
			return 0;
		}
	}

	//-----------------------------------------------------------------------//

	private int version;
	private UUID binaryId;
	private int runCount;

	public BinaryData(UUID binaryId, int version, int runCount) {
		super();
		this.version = version;
		this.binaryId = binaryId;
		this.runCount = runCount;
	}

	public BinaryData(DataHandleTuple tuple) throws DataHandleException {
		super();
		this.binaryId = tuple.get(HintserverGenerator.TAG_BINARY_BINARY).getValue(UUID.class);
		this.version = tuple.get(HintserverGenerator.TAG_BINARY_VERSION).getValue(Integer.class);
		this.runCount = tuple.get(HintserverGenerator.TAG_BINARY_RUN_COUNT).getValue(Integer.class);
	}

	//------------------------------------------------------------------------//
	public int getVersion() {
		return version;
	}

	public UUID getBinaryId() {
		return binaryId;
	}

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

}
