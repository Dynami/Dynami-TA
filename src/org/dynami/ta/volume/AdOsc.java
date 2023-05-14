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
package org.dynami.ta.volume;

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
public class AdOsc extends TaLibIndicator {
	private int fastPeriod = 3;
	private int slowPeriod = 10;
	// output series
	private Series outReal = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for fastPeriod = 3</br>
	 * default value for slowPeriod = 10</br>
	 */
	public AdOsc(){
		computePad( fastPeriod,  slowPeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param fastPeriod default value 3
	 * @param slowPeriod default value 10
	 */
	public AdOsc(int fastPeriod, int slowPeriod){
		this.fastPeriod = fastPeriod;
		this.slowPeriod = slowPeriod;
		computePad( fastPeriod,  slowPeriod, PAD);
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
		return "Ad Osc";
	}

	@Override
	public String getDescription(){
		return "Ad Osc - Volume Indicators";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public AdOsc compute( final Series high, final Series low, final Series close, final Series volume) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = high.size()-1;
		final double[] _tmphigh = high.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmplow = low.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpclose = close.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpvolume = volume.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outReal = new double[_tmphigh.length];
		
		ready = DUtils.max(_tmphigh.length, _tmplow.length, _tmpclose.length, _tmpvolume.length) >= DUtils.max(fastPeriod, slowPeriod);
		// calculate output
		core.adOsc(0, _tmphigh.length-1, _tmphigh, _tmplow, _tmpclose, _tmpvolume, this.fastPeriod, this.slowPeriod, outBegIdx, outNBElement, _outReal);
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
		return new String[]{"Ad Osc"};
	}
}

