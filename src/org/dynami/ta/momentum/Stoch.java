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
public class Stoch extends TaLibIndicator {
	private int fast_KPeriod = 5;
	private int slow_KPeriod = 3;
	private MAType slow_KMA = MAType.Sma;
	private int slow_DPeriod = 3;
	private MAType slow_DMA = MAType.Sma;
	// output series
	private Series outSlowK = new Series();
	private Series outSlowD = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for fast_KPeriod = 5</br>
	 * default value for slow_KPeriod = 3</br>
	 * default value for slow_KMA = MAType.Sma</br>
	 * default value for slow_DPeriod = 3</br>
	 * default value for slow_DMA = MAType.Sma</br>
	 */
	public Stoch(){
		computePad( fast_KPeriod,  slow_KPeriod,  slow_DPeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param fast_KPeriod default value 5
	 * @param slow_KPeriod default value 3
	 * @param slow_KMA default value MAType.Sma
	 * @param slow_DPeriod default value 3
	 * @param slow_DMA default value MAType.Sma
	 */
	public Stoch(int fast_KPeriod, int slow_KPeriod, MAType slow_KMA, int slow_DPeriod, MAType slow_DMA){
		this.fast_KPeriod = fast_KPeriod;
		this.slow_KPeriod = slow_KPeriod;
		this.slow_KMA = slow_KMA;
		this.slow_DPeriod = slow_DPeriod;
		this.slow_DMA = slow_DMA;
		computePad( fast_KPeriod,  slow_KPeriod,  slow_DPeriod, PAD);
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
		return "Stoch";
	}

	@Override
	public String getDescription(){
		return "Stoch - Momentum Indicators";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public void compute( final Series high, final Series low, final Series close) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = high.size()-1;
		final double[] _tmphigh = high.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmplow = low.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmpclose = close.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outSlowK = new double[_tmphigh.length];
		final double[] _outSlowD = new double[_tmphigh.length];
		
		ready = DUtils.max(_tmphigh.length, _tmplow.length, _tmpclose.length) >= DUtils.max(fast_KPeriod, slow_KPeriod, slow_DPeriod);
		// calculate output
		core.stoch(0, _tmphigh.length-1, _tmphigh, _tmplow, _tmpclose, this.fast_KPeriod, this.slow_KPeriod, this.slow_KMA, this.slow_DPeriod, this.slow_DMA, outBegIdx, outNBElement, _outSlowK, _outSlowD);
 		// shift data to end of array and set output fields
		DUtils.shift(_outSlowK, outBegIdx.value);
		DUtils.shift(_outSlowD, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outSlowK.append(_outSlowK[_outSlowK.length-j]);
			outSlowD.append(_outSlowD[_outSlowD.length-j]);
		}
	}

	public Series getSlowK(){
		return outSlowK;
	}
	public Series getSlowD(){
		return outSlowD;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getSlowK, this::getSlowD);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"SlowK", "SlowD"};
	}
}

