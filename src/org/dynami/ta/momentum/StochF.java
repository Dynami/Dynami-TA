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
package org.dynami.ta.momentum;

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
public class StochF extends TaLibIndicator {
	private int fast_KPeriod = 5;
	private int fast_DPeriod = 3;
	private MAType fast_DMA = MAType.Sma;
	// output series
	private Series outFastK = new Series();
	private Series outFastD = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for fast_KPeriod = 5</br>
	 * default value for fast_DPeriod = 3</br>
	 * default value for fast_DMA = MAType.Sma</br>
	 */
	public StochF(){
		computePad( fast_KPeriod,  fast_DPeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param fast_KPeriod default value 5
	 * @param fast_DPeriod default value 3
	 * @param fast_DMA default value MAType.Sma
	 */
	public StochF(int fast_KPeriod, int fast_DPeriod, MAType fast_DMA){
		this.fast_KPeriod = fast_KPeriod;
		this.fast_DPeriod = fast_DPeriod;
		this.fast_DMA = fast_DMA;
		computePad( fast_KPeriod,  fast_DPeriod, PAD);
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
		return "Stoch F";
	}

	@Override
	public String getDescription(){
		return "Stoch F - Momentum Indicators";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public void compute( final Series high, final Series low, final Series close) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = high.size();
		final double[] _tmphigh = high.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmplow = low.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpclose = close.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outFastK = new double[_tmphigh.length];
		final double[] _outFastD = new double[_tmphigh.length];
		
		ready = DUtils.max(_tmphigh.length, _tmplow.length, _tmpclose.length) >= DUtils.max(fast_KPeriod, fast_DPeriod);
		// calculate output
		core.stochF(0, _tmphigh.length-1, _tmphigh, _tmplow, _tmpclose, this.fast_KPeriod, this.fast_DPeriod, this.fast_DMA, outBegIdx, outNBElement, _outFastK, _outFastD);
 		// shift data to end of array and set output fields
		DUtils.shift(_outFastK, outBegIdx.value);
		DUtils.shift(_outFastD, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outFastK.append(_outFastK[_outFastK.length-j]);
			outFastD.append(_outFastD[_outFastD.length-j]);
		}
	}

	public Series getFastK(){
		return outFastK;
	}
	public Series getFastD(){
		return outFastD;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getFastK, this::getFastD);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"FastK", "FastD"};
	}
}

