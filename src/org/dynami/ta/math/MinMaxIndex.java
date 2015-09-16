package org.dynami.ta.math;

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
public class MinMaxIndex extends TaLibIndicator implements ITechnicalIndicator {
	private int timePeriod = 30;
	private int lastLength = 0;
	private int PAD = 4;
	private boolean ready = false;
	// output series
	private Series outMinIdx = new Series();
	private Series outMaxIdx = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for timePeriod = 30</br>
	 */
	public MinMaxIndex(){
		computePad( timePeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param timePeriod default value 30
	 */
	public MinMaxIndex(int timePeriod){
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
		return "Min Max Index";
	}

	@Override
	public String getDescription(){
		return "Min Max Index - Math Operators";
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
		final int[] _outMinIdx = new int[_tmpinReal.length];
		final int[] _outMaxIdx = new int[_tmpinReal.length];
		
		ready = DUtils.max(_tmpinReal.length) >= DUtils.max(timePeriod);
		// calculate output
		core.minMaxIndex(0, _tmpinReal.length-1, _tmpinReal, this.timePeriod, outBegIdx, outNBElement, _outMinIdx, _outMaxIdx);
 		// shift data to end of array and set output fields
		DUtils.shift(_outMinIdx, outBegIdx.value);
		DUtils.shift(_outMaxIdx, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outMinIdx.append(_outMinIdx[_outMinIdx.length-j]);
			outMaxIdx.append(_outMaxIdx[_outMaxIdx.length-j]);
		}
	}

	public Series getMinIdx(){
		return outMinIdx;
	}
	public Series getMaxIdx(){
		return outMaxIdx;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getMinIdx, this::getMaxIdx);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"MinIdx", "MaxIdx"};
	}
}

