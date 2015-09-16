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
public class MacdFix extends TaLibIndicator implements ITechnicalIndicator {
	private int signalPeriod = 9;
	private int lastLength = 0;
	private int PAD = 4;
	private boolean ready = false;
	// output series
	private Series outMACD = new Series();
	private Series outMACDSignal = new Series();
	private Series outMACDHist = new Series();
	
	/**
	 * Default constructor with standard input parameters</br>
	 * default value for signalPeriod = 9</br>
	 */
	public MacdFix(){
		computePad( signalPeriod, PAD);
	}

	/**
	 * Default constructor with custom input parameters:
	 * @param signalPeriod default value 9
	 */
	public MacdFix(int signalPeriod){
		this.signalPeriod = signalPeriod;
		computePad( signalPeriod, PAD);
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
		return "Macd Fix";
	}

	@Override
	public String getDescription(){
		return "Macd Fix - Momentum Indicators";
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
		final double[] _outMACD = new double[_tmpinReal.length];
		final double[] _outMACDSignal = new double[_tmpinReal.length];
		final double[] _outMACDHist = new double[_tmpinReal.length];
		
		ready = DUtils.max(_tmpinReal.length) >= DUtils.max(signalPeriod);
		// calculate output
		core.macdFix(0, _tmpinReal.length-1, _tmpinReal, this.signalPeriod, outBegIdx, outNBElement, _outMACD, _outMACDSignal, _outMACDHist);
 		// shift data to end of array and set output fields
		DUtils.shift(_outMACD, outBegIdx.value);
		DUtils.shift(_outMACDSignal, outBegIdx.value);
		DUtils.shift(_outMACDHist, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outMACD.append(_outMACD[_outMACD.length-j]);
			outMACDSignal.append(_outMACDSignal[_outMACDSignal.length-j]);
			outMACDHist.append(_outMACDHist[_outMACDHist.length-j]);
		}
	}

	public Series getMACD(){
		return outMACD;
	}
	public Series getMACDSignal(){
		return outMACDSignal;
	}
	public Series getMACDHist(){
		return outMACDHist;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getMACD, this::getMACDSignal, this::getMACDHist);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"MACD", "MACDSignal", "MACDHist"};
	}
}

