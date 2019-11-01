package featureSelection.repository.implement.support.calculation.markovBlanket.approximate.symmetricalUncertainty.mutualInformationEntropy;

import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.InformationEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.MutualInformationEntropyCalculation;
import featureSelection.repository.frame.support.calculation.markovBlanket.approximate.SymmetricalUncertaintyCalculation;
import lombok.Getter;

/**
 * Symmetrical Uncertainty (SU) based on Mutual Information Entropy calculation.
 * <p>
 * <strong>SU(F<sub>i</sub>, F<sub>j</sub>)</strong> = 2 * [ I(F<sub>i</sub>; F<sub>j</sub>) / 
 * (H(F<sub>i</sub>) + H(F<sub>j</sub>))]
 * 
 * @see {@link MutualInformationEntropyCalculation}
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of Symmetrical Uncertainty value.
 */
public class MutualInformationEntropyBasedSymmetricalUncertaintyCalculation
	implements SymmetricalUncertaintyCalculation<Double> 
{
	@Getter protected long calculationTimes = 0;
	@Getter private Double result;

	/**
	 * Calculate the Symmetrical Uncertainty of 2 attributes bases on Mutual Information Entropy value
	 * and Information Entropy values.
	 * 
	 * @see {@link InformationEntropyCalculation}
	 * @see {@link MutualInformationEntropyCalculation}
	 * 
	 * @param mutualInfoEntropy
	 * 		The mutual information entropy value of the 2 attributes: <strong>I(F<sub>i</sub>; F<sub>j</sub>)</strong>.
	 * @param infoEntropyOfAttribute1
	 * 		The information entropy value of an attribute: <strong>H(F<sub>i</sub>)</strong>.
	 * @param infoEntropyOfAttribute2
	 * 		The information entropy value of another attribute: <strong>H(F<sub>j</sub>)</strong>.
	 * @return <code>this</code> instance.
	 */
	public MutualInformationEntropyBasedSymmetricalUncertaintyCalculation calculate(
			double mutualInfoEntropy, double infoEntropyOfAttribute1, double infoEntropyOfAttribute2
	) {
		// Count calculation
		calculationTimes++;
		// Calculate.
		result = 2 * ( mutualInfoEntropy / (infoEntropyOfAttribute1+infoEntropyOfAttribute2) );
		return this;
	}
}