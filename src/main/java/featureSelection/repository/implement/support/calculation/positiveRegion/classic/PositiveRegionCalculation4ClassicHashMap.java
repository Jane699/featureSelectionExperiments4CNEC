package featureSelection.repository.implement.support.calculation.positiveRegion.classic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionHashMapAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.hash.ClassicHashMapCalculation;
import featureSelection.repository.implement.support.calculation.positiveRegion.DefaultPositiveRegionCalculation;
import lombok.Getter;

public class PositiveRegionCalculation4ClassicHashMap
	extends DefaultPositiveRegionCalculation
	implements ClassicHashMapCalculation<Integer>
{
	@Getter private Integer positive;
	@Override
	public Integer getResult() {
		return positive;
	}

	public PositiveRegionCalculation4ClassicHashMap calculate(Collection<UniverseInstance> instances, 
			IntegerIterator attributes, Map<Integer, Collection<UniverseInstance>> decEClasses, 
			Object...args
	) {
		// Count the current calculation
		countCalculate(attributes.size());
		// Calculate
		positive = instances==null || instances.isEmpty()? 
					0: positiveRegion(instances, attributes, decEClasses);
		return this;
	}
	
	private static int positiveRegion(Collection<UniverseInstance> instances, IntegerIterator attributes, 
									Map<Integer, Collection<UniverseInstance>> decEClasses
	) {
		if (attributes.size()!=0) {
			// pos=0
			int pos = 0;
			// Generate decEClass keys partitioned by P.
			Collection<Collection<IntArrayKey>> decEClassKeys = new ArrayList<>(decEClasses.size());
			for (Collection<UniverseInstance> decU: decEClasses.values()) {
				decEClassKeys.add(
					ClassicAttributeReductionHashMapAlgorithm
						.Basic
						.equivalentClass(decU, attributes)
						.keySet()
				);
			}
			// for j=1 to number of |PEClass|
			Map<IntArrayKey, Collection<UniverseInstance>> eClasses = ClassicAttributeReductionHashMapAlgorithm
																.Basic
																.equivalentClass(instances, attributes);
			for (Map.Entry<IntArrayKey, Collection<UniverseInstance>> e : eClasses.entrySet()) {
				// pos += conPOSCalForOneEquivalenceClass
				if (ClassicAttributeReductionHashMapAlgorithm
						.Basic
						.allUniversesAtTheSameDecEquClass(decEClassKeys, e.getKey())
				) {
					pos += e.getValue().size();
				}
			}
			return pos;
		}else {
			return 0;
		}
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}
}