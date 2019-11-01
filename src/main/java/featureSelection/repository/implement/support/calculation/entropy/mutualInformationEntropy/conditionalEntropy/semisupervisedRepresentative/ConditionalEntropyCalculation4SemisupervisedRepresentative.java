package featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.conditionalEntropy.semisupervisedRepresentative;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.semisupervisedRepresentative.SemisupervisedRepresentativeAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.FeatureImportance4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.conditionalEntropy.DefaultConditionalEntropyCalculation;

/**
 * Conditional Entropy Calculation:
 * <p>
 * H(A|B) = - &Sigma;<sub>f<sub>j</sub> in B</sub> &Sigma;<sub>f<sub>i</sub> &isin; 
 * A</sub> ( p(f<sub>i</sub>, f<sub>j</sub>) * log p(f<sub>i</sub> | f<sub>j</sub>) )
 * 
 * @author Benjamin_L
 */
public class ConditionalEntropyCalculation4SemisupervisedRepresentative
	extends DefaultConditionalEntropyCalculation
	implements SemisupervisedRepresentativeStrategy,
				FeatureImportance4SemisupervisedRepresentative<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	/**
	 * H(A|B) = - &Sigma;<sub>f<sub>j</sub> in B</sub> &Sigma;<sub>f<sub>i</sub> &isin; 
	 * A</sub> ( p(f<sub>i</sub>, f<sub>j</sub>) * log p(f<sub>i</sub> | f<sub>j</sub>) )
	 * 
	 * @param equClasses
	 * 		Equivalent classes partitioned by B: <strong>U/B</strong>
	 * @param args
	 * 		Extra arguments including: 
	 * 		<li>Conditional partitioned attributes in {@link IntegerIterator}: 
	 * 			<strong>A</strong>.
	 * 		</li>
	 * 		<li>The size of total {@link UniverseInstance} in <code>equClasses</code>: 
	 * 			<strong>|U|</strong>
	 * 		</li>
	 */
	@Override
	public FeatureImportance4SemisupervisedRepresentative<Double> calculate(
			Collection<Collection<UniverseInstance>> equClasses, Object... args
	) {
		IntegerIterator conditionalPartition = (IntegerIterator) args[0];
		int universeSize = (int) args[1];
		
		entropy = 0;
		// Loop h in U/B: equClasses
		int condEquUniverseSize, equivalentClassUniverseSize;
		Collection<Collection<UniverseInstance>> conditionalEquClasses;
		for (Collection<UniverseInstance> equClass: equClasses) {
			// E = h/Y: conditionalEquClasses
			conditionalEquClasses = 
				SemisupervisedRepresentativeAlgorithm
					.Basic
					.equivalentClass(equClass, conditionalPartition)
					.values();
			// |E| = |h|
			equivalentClassUniverseSize = equClass.size();
			// Loop y in E
			for (Collection<UniverseInstance> condEquClass: conditionalEquClasses) {
				condEquUniverseSize = condEquClass.size();
				// H -= |y| / |U| * log(|y| / |E|)
				entropy -= condEquUniverseSize / (double) universeSize * 
							FastMath.log(condEquUniverseSize / (double) equivalentClassUniverseSize);
			}
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