package featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation;

import featureSelection.repository.frame.support.calculation.featureImportance.entropy.EntropyCalculation;

/**
 * Conditional Entropy Calculation:
 * <p>
 * H(A|B) = - &Sigma;<sub>f<sub>j</sub> in B</sub> &Sigma;<sub>f<sub>i</sub> &isin; 
 * A</sub> ( p(f<sub>i</sub>, f<sub>j</sub>) * log p(f<sub>i</sub> | f<sub>j</sub>) )
 * 
 * @see {@link InformationEntropyCalculation}
 * 
 * @author Benjamin_L
 */
public interface ConditionalEntropyCalculation extends EntropyCalculation {
	public static final String CALCULATION_NAME = "CE";
}
