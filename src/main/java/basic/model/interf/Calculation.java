package basic.model.interf;

import featureSelection.repository.frame.support.Support;

/**
 * An interface for common calculations. Calculation execute times are stored and covered at each 
 * calculation.
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of calculation result.
 */
public interface Calculation<V> extends Support {
	/**
	 * The the result of calculation. 
	 * 
	 * @return calculation result value.
	 */
	V getResult();
	
	/**
	 * Get how many times the calculation had beed called for <strong>this instance</strong>.
	 * 
	 * @return Calculation times in {@link long}.
	 */
	long getCalculationTimes();
}