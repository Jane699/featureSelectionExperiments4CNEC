package featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation;

import featureSelection.repository.frame.support.calculation.featureImportance.entropy.EntropyCalculation;

/**
 * Information Entropy Calculation:
 * <p>
 * H(A) = - &Sigma;<sub>f<sub>i</sub> &isin; A</sub> ( p(f<sub>i</sub>) * log p(f<sub>i</sub>) )
 * 
 * @author Benjamin_L
 */
public interface InformationEntropyCalculation extends EntropyCalculation {
	public static final String CALCULATION_NAME = "IE";
}
