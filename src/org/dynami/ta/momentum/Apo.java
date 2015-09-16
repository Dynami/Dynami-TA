package org.dynami.ta.momentum;

import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.dynami.ta.TaLibIndicator;
import org.dynami.core.ITechnicalIndicator;
import org.dynami.core.data.Series;
import org.dynami.core.utils.DUtils;
import com.tictactec.ta.lib.MAType;
/**
 * GENERATED CODE
 */
public class Apo extends TaLibIndicator implements ITechnicalIndicator {
	private int fastPeriod = 12;
	private int slowPeriod = 26;
	private MAType mAType = MAType.Sma;
	private int lastLength = 0;
	private int PAD = 4;
	private boolean ready = false;
	// output series
	private Series outReal = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for fastPeriod = 12</br>
	 * default value for slowPeriod = 26</br>
	 * default value for mAType = MAType.Sma</br>
	 */
	public Apo(){
		computePad( fastPeriod,  slowPeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param fastPeriod default value 12
	 * @param slowPeriod default value 26
	 * @param mAType default value MAType.Sma
	 */
	public Apo(int fastPeriod, int slowPeriod, MAType mAType){
		this.fastPeriod = fastPeriod;
		this.slowPeriod = slowPeriod;
		this.mAType = mAType;
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
		return "Apo";
	}

	@Override
	public String getDescription(){
		return "Apo - Momentum Indicators";
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
		final double[] _outReal = new double[_tmpinReal.length];
		
		ready = DUtils.max(_tmpinReal.length) >= DUtils.max(fastPeriod, slowPeriod);
		// calculate output
		core.apo(0, _tmpinReal.length-1, _tmpinReal, this.fastPeriod, this.slowPeriod, this.mAType, outBegIdx, outNBElement, _outReal);
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
		return new String[]{"Apo"};
	}
}

