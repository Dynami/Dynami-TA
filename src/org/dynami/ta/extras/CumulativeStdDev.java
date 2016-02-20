/*
 * Copyright 2015 Alessandro Atria - a.atria@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dynami.ta.extras;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.dynami.core.data.Series;
import org.dynami.ta.TaLibIndicator;
import org.dynami.ta.stat.StdDev;

public class CumulativeStdDev extends TaLibIndicator {
	private int timePeriod = 20;
	// output series
	private Series outReal = new Series();

	private StdDev stdDev;

	public CumulativeStdDev(){
		computePad( timePeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param timePeriod default value 14
	 */
	public CumulativeStdDev(int timePeriod){
		this.timePeriod = timePeriod;
		computePad( timePeriod, PAD);
		stdDev = new StdDev(timePeriod, 1);
	}

	public CumulativeStdDev(int timePeriod, double std){
		this.timePeriod = timePeriod;
		computePad( timePeriod, PAD);
		stdDev = new StdDev(timePeriod, std);
	}

	private void computePad(int...v){
		int max = Integer.MIN_VALUE;
		for(int d : v){
			if(d > max)
				max = d;
		}
		PAD = max;
	}

	@Override
	public String getName(){
		return "Cumulative Standard Deviation";
	}

	@Override
	public String getDescription(){
		return "Cumulative Standard Deviation - Statistic Function";
	}

	/**
	 * Compute indicator based on constructor class parameters
	 * and input Series.
	 */
	public void compute( final Series inReal) {
		stdDev.compute(inReal);
		Series diff = inReal.substract(1);
		outReal = diff.divide(stdDev.get());
	}

	public Series get(){
		return outReal;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::get);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"Linear Reg"};
	}

}
