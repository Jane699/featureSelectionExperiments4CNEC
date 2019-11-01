package featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.semisupervisedRepresentative.SemisupervisedRepresentativeAlgorithm;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.informationEntropy.semisupervisedRepresentative.InformationEntropyCalculation4SemisupervisedRepresentative;
import lombok.Getter;

/**
 * A cache entity for <code>Information Entropy</code> calculated values of attributes of {@link Universe}
 * using {@link HashMap} to store corresponding {@link InformationEntropyCacheResult}.
 * 
 * 
 * @author Benjamin_L
 */
@Getter
public class InformationEntropyCache {
	private Map<Integer, InformationEntropyCacheResult> infoEntropyByAllInstances;
	private Map<Integer, InformationEntropyCacheResult> infoEntropyByLabeledInstances;
	
	public InformationEntropyCache(int attributeSize) {
		infoEntropyByAllInstances = new HashMap<>(attributeSize);
		infoEntropyByLabeledInstances = new HashMap<>(attributeSize);
	}
	
	/**
	 * Calculate H(D/{a}), where D is the decision attribute and {a} is an conditional attribute, using 
	 * labeled + unlabeled universe instances.
	 * <p>
	 * <strong>Notice</strong>: If calculation is needed, <strong>U/{a}</strong> is calculated in order to
	 * calculate H(D/{a}).
	 * 
	 * @param attribute
	 * 		Attribute to be calculated: <strong>a</strong>
	 * @param calculation
	 * 		{@link InformationEntropyCalculation4SemisupervisedRepresentative} instance.
	 * @param allInstances
	 * 		A {@link Collection} of labeled and unlabeled universes.
	 * @return Conditional Information entropy of D related to {a}: <strong>H(D/{a})</strong>
	 */
	public InformationEntropyCacheResult calculateByAllUniverses(
			int attribute, InformationEntropyCalculation4SemisupervisedRepresentative calculation, 
			Collection<UniverseInstance> allInstances
	) {
		return calculate(attribute, calculation, allInstances, infoEntropyByAllInstances);
	}

	/**
	 * Calculate H(D/{a}), where D is the decision attribute and {a} is an conditional attribute, using 
	 * labeled universe instances.
	 * <p>
	 * <strong>Notice</strong>: If calculation is needed, <strong>(labeled U)/{a}</strong> is calculated in 
	 * order to calculate H(D/{a}).
	 * 
	 * @param attribute
	 * 		Attribute to be calculated: <strong>a</strong>
	 * @param calculation
	 * 		{@link InformationEntropyCalculation4SemisupervisedRepresentative} instance.
	 * @param labeledInstances
	 * 		A {@link Collection} of unlabeled universe instances.
	 * @return Conditional Information entropy of D related to {a}: <strong>H(D/{a})</strong>
	 */
	public InformationEntropyCacheResult calculateByLabeledUniverses(
			int attribute, InformationEntropyCalculation4SemisupervisedRepresentative calculation, 
			Collection<UniverseInstance> labeledInstances
	) {
		return calculate(attribute, calculation, labeledInstances, infoEntropyByLabeledInstances);
	}
	
	/**
	 * Calculate H(D/{a}), where D is the decision attribute and {a} is an conditional attribute, using 
	 * given universe instances.
	 * <p>
	 * <strong>Notice</strong>: If calculation is needed, <strong>U/{a}</strong> is calculated in 
	 * order to calculate H(D/{a}).
	 * 
	 * @param attribute
	 * 		Attribute to be calculated: <strong>a</strong>
	 * @param calculation
	 * 		{@link InformationEntropyCalculation4SemisupervisedRepresentative} instance.
	 * @param instances
	 * 		A {@link Collection} of labeled and unlabeled universe instances.
	 * @param infoEntropyCache
	 * 		A {@link Map} contains info. entropy value history whose keys are attributes of {@link UniverseInstance}
	 * 		and values are info. entropy values.
	 * @param cache
	 * 		The {@link Map} to store {@link InformationEntropyCacheResult} for cache.
	 * @return Conditional Information entropy of D related to {a}: <strong>H(D/{a})</strong>
	 */
	private InformationEntropyCacheResult calculate(
			int attribute, InformationEntropyCalculation4SemisupervisedRepresentative calculation, 
			Collection<UniverseInstance> instances, Map<Integer, InformationEntropyCacheResult> cache
	) {
		InformationEntropyCacheResult cacheValue = cache.get(attribute);
		//	if exists in cache, use cache value.
		if (cacheValue!=null) {
			// do nothing.
		//	if doesn't, calculate
		}else {
			// U/F[i]
			Collection<Collection<UniverseInstance>> equClassesOfLabeledU = 
				SemisupervisedRepresentativeAlgorithm
					.Basic
					.equivalentClass(instances, new IntegerArrayIterator(attribute))
					.values();
			// H(F[i]), bases on U/F[i]
			infoEntropyByLabeledInstances.put(
				attribute,
				cacheValue = new InformationEntropyCacheResult(
								calculation.calculate(equClassesOfLabeledU, instances.size())
														.getResult().doubleValue(),
								equClassesOfLabeledU
							)
			);
		}
		return cacheValue;
	}
}