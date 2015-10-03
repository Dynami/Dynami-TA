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
public class Bbands extends TaLibIndicator {
	private int timePeriod = 5;
	private double deviationsUp = 2.0;
	private double deviationsDown = 2.0;
	private MAType mAType = MAType.Sma;
	// output series
	private Series outRealUpperBand = new Series();
	private Series outRealMiddleBand = new Series();
	private Series outRealLowerBand = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for timePeriod = 5</br>
	 * default value for deviationsUp = 2.0</br>
	 * default value for deviationsDown = 2.0</br>
	 * default value for mAType = MAType.Sma</br>
	 */
	public Bbands(){
		computePad( timePeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param timePeriod default value 5
	 * @param deviationsUp default value 2.0
	 * @param deviationsDown default value 2.0
	 * @param mAType default value MAType.Sma
	 */
	public Bbands(int timePeriod, double deviationsUp, double deviationsDown, MAType mAType){
		this.timePeriod = timePeriod;
		this.deviationsUp = deviationsUp;
		this.deviationsDown = deviationsDown;
		this.mAType = mAType;
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
		return "Bbands";
	}

	@Override
	public String getDescription(){
		return "Bbands - Overlap Studies";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public void compute( final Series inReal) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = inReal.size();
		final double[] _tmpinReal = inReal.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outRealUpperBand = new double[_tmpinReal.length];
		final double[] _outRealMiddleBand = new double[_tmpinReal.length];
		final double[] _outRealLowerBand = new double[_tmpinReal.length];
		
		ready = DUtils.max(_tmpinReal.length) >= DUtils.max(timePeriod);
		// calculate output
		core.bbands(0, _tmpinReal.length-1, _tmpinReal, this.timePeriod, this.deviationsUp, this.deviationsDown, this.mAType, outBegIdx, outNBElement, _outRealUpperBand, _outRealMiddleBand, _outRealLowerBand);
 		// shift data to end of array and set output fields
		DUtils.shift(_outRealUpperBand, outBegIdx.value);
		DUtils.shift(_outRealMiddleBand, outBegIdx.value);
		DUtils.shift(_outRealLowerBand, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outRealUpperBand.append(_outRealUpperBand[_outRealUpperBand.length-j]);
			outRealMiddleBand.append(_outRealMiddleBand[_outRealMiddleBand.length-j]);
			outRealLowerBand.append(_outRealLowerBand[_outRealLowerBand.length-j]);
		}
	}

	public Series getRealUpperBand(){
		return outRealUpperBand;
	}
	public Series getRealMiddleBand(){
		return outRealMiddleBand;
	}
	public Series getRealLowerBand(){
		return outRealLowerBand;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getRealUpperBand, this::getRealMiddleBand, this::getRealLowerBand);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"RealUpperBand", "RealMiddleBand", "RealLowerBand"};
	}
}

