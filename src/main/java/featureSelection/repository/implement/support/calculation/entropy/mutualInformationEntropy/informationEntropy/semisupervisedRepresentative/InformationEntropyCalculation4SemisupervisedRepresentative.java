package featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.informationEntropy.semisupervisedRepresentative;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.FeatureImportance4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.informationEntropy.DefaultInformationEntropyCalculation;

public class InformationEntropyCalculation4SemisupervisedRepresentative
	extends DefaultInformationEntropyCalculation
	implements SemisupervisedRepresentativeStrategy,
				FeatureImportance4SemisupervisedRepresentative<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	/**
	 * Calculate Info. entropy of Equivalent Classes(<code>equClasses</code>).
	 * <p>
	 * No extra arguments required in <code>args</code>.
	 */
	@Override
	public FeatureImportance4SemisupervisedRepresentative<Double> calculate(
			Collection<Collection<UniverseInstance>> equClasses, Object...args
	) {
		// Initiate
		int universeSize = (int) args[0];
		
		entropy = 0;
		double p;
		for (Collection<UniverseInstance> equClass: equClasses) {
			// H -= |u| / log(1/|u|)
			// p = |u| / |U|
			// H -= p * log(p)
			p = equClass.size() / (double) universeSize;
			entropy -= p * FastMath.log(p);
		}
		return this;
	}
	
	@Override
	public Double plus(Double v1, Double v2) throws Exception {
		throw new RuntimeException("Unimplemented method!");
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		if (item instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) item;
			if (map.values() instanceof Collection)
				return map.isEmpty()? true: map.values().iterator().next() instanceof UniverseInstance;
			else
				return false;
		}else {
			return false;
		}
	}
}