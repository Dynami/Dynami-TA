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
package org.dynami.ta.pattern;

import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.dynami.ta.TaLibIndicator;
import org.dynami.core.ITechnicalIndicator;
import org.dynami.core.data.Series;
import org.dynami.core.utils.DUtils;
/**
 * GENERATED CODE
 */
public class Cdl3BlackCrows extends TaLibIndicator implements ITechnicalIndicator {
	private int lastLength = 0;
	private int PAD = 4;
	private boolean ready = false;
	// output series
	private Series outInteger = new Series();
	
	/**
	 * Default constructor with custom input parameters:
	 */
	public Cdl3BlackCrows(){
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
		return "Cdl3 Black Crows";
	}

	@Override
	public String getDescription(){
		return "Cdl3 Black Crows - Pattern Recognition";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public void compute( final Series open, final Series high, final Series low, final Series close) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = open.size();
		final double[] _tmpopen = open.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmphigh = high.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmplow = low.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpclose = close.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final int[] _outInteger = new int[_tmpopen.length];
		
		ready = DUtils.max(_tmpopen.length, _tmphigh.length, _tmplow.length, _tmpclose.length) >= DUtils.max();
		// calculate output
		core.cdl3BlackCrows(0, _tmpopen.length-1, _tmpopen, _tmphigh, _tmplow, _tmpclose, outBegIdx, outNBElement, _outInteger);
 		// shift data to end of array and set output fields
		DUtils.shift(_outInteger, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outInteger.append(_outInteger[_outInteger.length-j]);
		}
	}

	public Series get(){
		return outInteger;
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
		return new String[]{"Cdl3 Black Crows"};
	}
}

