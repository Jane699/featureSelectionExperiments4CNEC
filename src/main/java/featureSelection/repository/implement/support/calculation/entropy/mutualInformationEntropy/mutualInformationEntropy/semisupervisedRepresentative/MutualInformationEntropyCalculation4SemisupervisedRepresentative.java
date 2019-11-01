package featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.mutualInformationEntropy.semisupervisedRepresentative;

import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.mutualInformationEntropy.DefaultMutualInformationEntropy;

/**
 * Mutual Information Entropy.
 * <p>
 * <strong>I(F, C)</strong> = <strong>H(C) - H(C|F)</strong> = H(F) - H(F|C).
 * 
 * @author Benjamin_L
 */
public class MutualInformationEntropyCalculation4SemisupervisedRepresentative 
	extends DefaultMutualInformationEntropy
	implements SemisupervisedRepresentativeStrategy 
{
	@Override
	public Double plus(Double v1, Double v2) throws Exception {
		throw new RuntimeException("Unimplemented method");
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		throw new RuntimeException("Unimplemented method");
	}
}