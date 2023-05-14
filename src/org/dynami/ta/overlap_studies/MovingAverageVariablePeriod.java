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
package org.dynami.ta.overlap_studies;

import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.dynami.ta.TaLibIndicator;
import org.dynami.core.data.Series;
import org.dynami.core.utils.DUtils;
import com.tictactec.ta.lib.MAType;
/**
 * GENERATED CODE
 */
public class MovingAverageVariablePeriod extends TaLibIndicator {
	private int minimumPeriod = 2;
	private int maximumPeriod = 30;
	private MAType mAType = MAType.Sma;
	// output series
	private Series outReal = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for minimumPeriod = 2</br>
	 * default value for maximumPeriod = 30</br>
	 * default value for mAType = MAType.Sma</br>
	 */
	public MovingAverageVariablePeriod(){
		computePad( minimumPeriod,  maximumPeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param minimumPeriod default value 2
	 * @param maximumPeriod default value 30
	 * @param mAType default value MAType.Sma
	 */
	public MovingAverageVariablePeriod(int minimumPeriod, int maximumPeriod, MAType mAType){
		this.minimumPeriod = minimumPeriod;
		this.maximumPeriod = maximumPeriod;
		this.mAType = mAType;
		computePad( minimumPeriod,  maximumPeriod, PAD);
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
		return "Moving Average Variable Period";
	}

	@Override
	public String getDescription(){
		return "Moving Average Variable Period - Overlap Studies";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public MovingAverageVariablePeriod compute( final Series inReal, final Series inPeriods) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = inReal.size()-1;
		final double[] _tmpinReal = inReal.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpinPeriods = inPeriods.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outReal = new double[_tmpinReal.length];
		
		ready = DUtils.max(_tmpinReal.length, _tmpinPeriods.length) >= DUtils.max(minimumPeriod, maximumPeriod);
		// calculate output
		core.movingAverageVariablePeriod(0, _tmpinReal.length-1, _tmpinReal, _tmpinPeriods, this.minimumPeriod, this.maximumPeriod, this.mAType, outBegIdx, outNBElement, _outReal);
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
		return new String[]{"Moving Average Variable Period"};
	}
}

