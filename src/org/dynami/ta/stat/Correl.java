/*
 * Copyright 2023 Alessandro Atria - a.atria@gmail.com
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
package org.dynami.ta.stat;

import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.dynami.ta.TaLibIndicator;
import org.dynami.core.data.Series;
import org.dynami.core.utils.DUtils;
/**
 * GENERATED CODE
 */
public class Correl extends TaLibIndicator {
	private int timePeriod = 30;
	// output series
	private Series outReal = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for timePeriod = 30</br>
	 */
	public Correl(){
		computePad( timePeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param timePeriod default value 30
	 */
	public Correl(int timePeriod){
		this.timePeriod = timePeriod;
		computePad( timePeriod, PAD);
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
		return "Correl";
	}

	@Override
	public String getDescription(){
		return "Correl - Statistic Functions";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public Correl compute( final Series inReal0, final Series inReal1) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = inReal0.size()-1;
		final double[] _tmpinReal0 = inReal0.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpinReal1 = inReal1.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outReal = new double[_tmpinReal0.length];
		
		ready = DUtils.max(_tmpinReal0.length, _tmpinReal1.length) >= DUtils.max(timePeriod);
		// calculate output
		core.correl(0, _tmpinReal0.length-1, _tmpinReal0, _tmpinReal1, this.timePeriod, outBegIdx, outNBElement, _outReal);
 		// shift data to end of array and set output fields
		DUtils.shift(_outReal, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outReal.append(_outReal[_outReal.length-j]);
		}
		return this;
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
		return new String[]{"Correl"};
	}
}

