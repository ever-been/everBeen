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
package cz.cuni.mff.been.resultsrepositoryng.implementation;

import java.util.UUID;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;

/**
 * Interface for scheduling evaluator tasks. It's used by results repository.
 * @author Jan Tattermusch
 *
 */
public interface EvaluatorScheduler {
	
	/**
	 * Launches evaluator task with parameters triggerId and lastProcessedSerial
	 * @param task evaluator to run
	 * @param triggerId trigger id
	 * @param lastProcessedSerial last processed data serial number
	 * @throws ResultsRepositoryException 
	 */
	void scheduleEvaluator(TaskDescriptor task,UUID triggerId, long lastProcessedSerial) throws ResultsRepositoryException;

}
