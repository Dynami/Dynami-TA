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
import org.dynami.core.ITechnicalIndicator;
import org.dynami.core.data.Series;
import org.dynami.core.utils.DUtils;
/**
 * GENERATED CODE
 */
public class Aroon extends TaLibIndicator implements ITechnicalIndicator {
	private int timePeriod = 14;
	private int lastLength = 0;
	private int PAD = 4;
	private boolean ready = false;
	// output series
	private Series outAroonDown = new Series();
	private Series outAroonUp = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for timePeriod = 14</br>
	 */
	public Aroon(){
		computePad( timePeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param timePeriod default value 14
	 */
	public Aroon(int timePeriod){
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
		return "Aroon";
	}

	@Override
	public String getDescription(){
		return "Aroon - Momentum Indicators";
	}

	/**
	 * Compute indicator based on constructor class parameters 
	 * and input Series.
	 */
	public void compute( final Series high, final Series low) {
		final MInteger outBegIdx = new MInteger();
		final MInteger outNBElement = new MInteger();
		// define strict necessary input parameters
		final int currentLength = high.size();
		final double[] _tmphigh = high.toArray(Math.max(lastLength-PAD, 0), currentLength);
		final double[] _tmplow = low.toArray(Math.max(lastLength-PAD, 0), currentLength);
		// define output parameters
		final double[] _outAroonDown = new double[_tmphigh.length];
		final double[] _outAroonUp = new double[_tmphigh.length];
		
		ready = DUtils.max(_tmphigh.length, _tmplow.length) >= DUtils.max(timePeriod);
		// calculate output
		core.aroon(0, _tmphigh.length-1, _tmphigh, _tmplow, this.timePeriod, outBegIdx, outNBElement, _outAroonDown, _outAroonUp);
 		// shift data to end of array and set output fields
		DUtils.shift(_outAroonDown, outBegIdx.value);
		DUtils.shift(_outAroonUp, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outAroonDown.append(_outAroonDown[_outAroonDown.length-j]);
			outAroonUp.append(_outAroonUp[_outAroonUp.length-j]);
		}
	}

	public Series getAroonDown(){
		return outAroonDown;
	}
	public Series getAroonUp(){
		return outAroonUp;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getAroonDown, this::getAroonUp);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"AroonDown", "AroonUp"};
	}
}

