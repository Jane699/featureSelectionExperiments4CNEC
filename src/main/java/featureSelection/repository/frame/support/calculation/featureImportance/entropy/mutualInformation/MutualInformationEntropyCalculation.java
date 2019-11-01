package featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation;

import featureSelection.repository.frame.support.calculation.featureImportance.entropy.EntropyCalculation;

/**
 * Mutual Information Entropy:
 * <p>
 * <strong>I(A;B)</strong> = H(A) - H(A|B) = <strong>I(B;A)</strong> = H(B) - H(B|A)
 * <p>
 * <strong>I(F<sub>i</sub>;F<sub>j</sub>)</strong> = H(F<sub>i</sub>) - H(F<sub>i</sub>|F<sub>j</sub>) = 
 * <strong>I(F<sub>j</sub>;F<sub>i</sub>)</strong> = H(F<sub>j</sub>) - H(F<sub>j</sub>|F<sub>i</sub>)
 * 
 * @see {@link InformationEntropyCalculation}
 * 
 * @author Benjamin_L
 */
public interface MutualInformationEntropyCalculation extends EntropyCalculation {
	public static final String CALCULATION_NAME = "MIE";
}