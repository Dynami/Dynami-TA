package org.dynami.ta.cycle;

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
public class HtPhasor extends TaLibIndicator implements ITechnicalIndicator {
	private int lastLength = 0;
	private int PAD = 4;
	private boolean ready = false;
	// output series
	private Series outInPhase = new Series();
	private Series outQuadrature = new Series();
	
	/**
	 * Default constructor with custom input parameters:
	 */
	public HtPhasor(){
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
		return "Ht Phasor";
	}

	@Override
	public String getDescription(){
		return "Ht Phasor - Cycle Indicators";
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
		final double[] _outInPhase = new double[_tmpinReal.length];
		final double[] _outQuadrature = new double[_tmpinReal.length];
		
		ready = DUtils.max(_tmpinReal.length) >= DUtils.max();
		// calculate output
		core.htPhasor(0, _tmpinReal.length-1, _tmpinReal, outBegIdx, outNBElement, _outInPhase, _outQuadrature);
 		// shift data to end of array and set output fields
		DUtils.shift(_outInPhase, outBegIdx.value);
		DUtils.shift(_outQuadrature, outBegIdx.value);
		for(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){
			outInPhase.append(_outInPhase[_outInPhase.length-j]);
			outQuadrature.append(_outQuadrature[_outQuadrature.length-j]);
		}
	}

	public Series getInPhase(){
		return outInPhase;
	}
	public Series getQuadrature(){
		return outQuadrature;
	}

	@Override
	public List<Supplier<Series>> series() {
		return Arrays.asList(this::getInPhase, this::getQuadrature);
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public String[] seriesNames() {
		return new String[]{"InPhase", "Quadrature"};
	}
}

