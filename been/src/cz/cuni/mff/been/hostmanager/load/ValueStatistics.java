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

package cz.cuni.mff.been.hostmanager.load;

import java.io.Serializable;

/**
 * Statistical data about values read from the event file.
 *
 * @author Branislav Repcek
 */
public class ValueStatistics implements Serializable {
	
	private static final long	serialVersionUID	= -4695685895544000486L;

	/**
	 * Minimum value from the dataset.
	 */
	private long min;
	
	/**
	 * Maximum value from the dataset.
	 */
	private long max;
	
	/**
	 * Average value.
	 */
	private float average;
	
	/**
	 * Sum of all values.
	 */
	private long sum;
	
	/**
	 * Name (description) of the data.
	 */
	private String name;
	
	/**
	 * Global minimum for this value.
	 */
	private Long limitMin;
	
	/**
	 * Global maximum for this value.
	 */
	private Long limitMax;
	
	/**
	 * Number of values.
	 */
	private long count;

	/**
	 * Create new data storage for value with given name.
	 * 
	 * @param name Name of the value.
	 * @param limitMin Minimum value. Use <tt>null</tt> for negative infinity.
	 * @param limitMax Maximum value. use <tt>null</tt> for positive infinity.
	 */
	ValueStatistics(String name, Long limitMin, Long limitMax) {
		
		this.name = name;
		min = Long.MAX_VALUE;
		max = Long.MIN_VALUE;
		this.limitMax = limitMax;
		this.limitMin = limitMin;
	}

	/**
	 * Add new sample value.
	 * 
	 * @param value Value to add.
	 */
	public void addSamplePoint(long value) {
		
		if (value < min) {
			min = value;
		}

		if (value > max) {
			max = value;
		}

		++count;
		sum += value;
		average = (float) sum / (float) count;
	}
	
	/**
	 * @return Average value of all samples. If no data is stored in this instance 0.0 is returned.
	 */
	public float getAverage() {
		
		if (count == 0) {
			return 0;
		} else {
			return average;
		}
	}

	/**
	 * @return Number of values.
	 */
	public long getCount() {

		return count;
	}

	/**
	 * @return Maximum value. If no data is stored in this instance 0 is returned.
	 */
	public long getMax() {
		
		if (count == 0) {
			return 0;
		} else {
			return max;
		}
	}

	/**
	 * @return Minimum value. If no data is stored in this instance 0 is returned.
	 */
	public long getMin() {
		
		if (count == 0) {
			return 0;
		} else {
			return min;
		}
	}

	/**
	 * @return Name of the value.
	 */
	public String getName() {
		
		return name;
	}

	/**
	 * @return Global maximum this value can reach. If value does not have upper bound 
	 *         <tt>null</tt> is returned.
	 */
	public Long getLimitMax() {
		
		return limitMax;
	}

	/**
	 * @return Global minimum this value can reach. If value does not have lower bound
	 *         <tt>null</tt> is returned.
	 */
	public Long getLimitMin() {
		
		return limitMin;
	}
}
