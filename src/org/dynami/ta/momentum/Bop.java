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
package org.dynami.ta.momentum;

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
public class Bop extends TaLibIndicator {
	// output series
	private Series outReal = new Series();
	
	/**
	 * Default constructor with custom input parameters:
	 */
	public Bop(){
		computePad(PAD);
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
		return "Bop";
	}

	@Override
	public String getDescription(){
		return "Bop - Momentum Indicators";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public Bop compute( final Series open, final Series high, final Series low, final Series close) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = open.size()-1;
		final double[] _tmpopen = open.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmphigh = high.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmplow = low.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpclose = close.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outReal = new double[_tmpopen.length];
		
		ready = DUtils.max(_tmpopen.length, _tmphigh.length, _tmplow.length, _tmpclose.length) >= DUtils.max();
		// calculate output
		core.bop(0, _tmpopen.length-1, _tmpopen, _tmphigh, _tmplow, _tmpclose, outBegIdx, outNBElement, _outReal);
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
		return new String[]{"Bop"};
	}
}

