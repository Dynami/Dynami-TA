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
/**
 * GENERATED CODE
 */
public class SarExt extends TaLibIndicator {
	private double startValue = 0.0;
	private double offsetOnReverse = 0.0;
	private double aFInitLong = 0.02;
	private double aFLong = 0.02;
	private double aFMaxLong = 0.2;
	private double aFInitShort = 0.02;
	private double aFShort = 0.02;
	private double aFMaxShort = 0.2;
	// output series
	private Series outReal = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for startValue = 0.0</br>
	 * default value for offsetOnReverse = 0.0</br>
	 * default value for aFInitLong = 0.02</br>
	 * default value for aFLong = 0.02</br>
	 * default value for aFMaxLong = 0.2</br>
	 * default value for aFInitShort = 0.02</br>
	 * default value for aFShort = 0.02</br>
	 * default value for aFMaxShort = 0.2</br>
	 */
	public SarExt(){
		computePad(PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param startValue default value 0.0
	 * @param offsetOnReverse default value 0.0
	 * @param aFInitLong default value 0.02
	 * @param aFLong default value 0.02
	 * @param aFMaxLong default value 0.2
	 * @param aFInitShort default value 0.02
	 * @param aFShort default value 0.02
	 * @param aFMaxShort default value 0.2
	 */
	public SarExt(double startValue, double offsetOnReverse, double aFInitLong, double aFLong, double aFMaxLong, double aFInitShort, double aFShort, double aFMaxShort){
		this.startValue = startValue;
		this.offsetOnReverse = offsetOnReverse;
		this.aFInitLong = aFInitLong;
		this.aFLong = aFLong;
		this.aFMaxLong = aFMaxLong;
		this.aFInitShort = aFInitShort;
		this.aFShort = aFShort;
		this.aFMaxShort = aFMaxShort;
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
		return "Sar Ext";
	}

	@Override
	public String getDescription(){
		return "Sar Ext - Overlap Studies";
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
		final double[] _outReal = new double[_tmphigh.length];
		
		ready = DUtils.max(_tmphigh.length, _tmplow.length) >= DUtils.max();
		// calculate output
		core.sarExt(0, _tmphigh.length-1, _tmphigh, _tmplow, this.startValue, this.offsetOnReverse, this.aFInitLong, this.aFLong, this.aFMaxLong, this.aFInitShort, this.aFShort, this.aFMaxShort, outBegIdx, outNBElement, _outReal);
 		// shift data to end of array and set output fields
		DUtils.shift(_outReal, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outReal.append(_outReal[_outReal.length-j]);
		}
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
		return new String[]{"Sar Ext"};
	}
}

