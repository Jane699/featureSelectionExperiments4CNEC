package featureSelection.repository.implement.support.calculation.positiveRegion.classic;

import java.util.Collection;
import java.util.List;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionSequentialAlgorithm;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionSequentialAlgorithm.Basic;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.sequential.ClassicSequentialCalculation;
import featureSelection.repository.implement.support.calculation.positiveRegion.DefaultPositiveRegionCalculation;
import lombok.Getter;

public class PositiveRegionCalculation4ClassicSequential
	extends DefaultPositiveRegionCalculation
	implements ClassicSequentialCalculation<Integer>
{
	@Getter private Integer positive;	
	@Override
	public Integer getResult() {
		return positive;
	}

	public PositiveRegionCalculation4ClassicSequential calculate(Collection<UniverseInstance> instances, 
			IntegerIterator attributes, Collection<List<UniverseInstance>> decEClasses, Object...args
	) {
		// Count the current calculation
		countCalculate(attributes.size());
		// Calculate
		positive = instances==null || instances.isEmpty()? 0: 
					positiveRegion(instances, attributes, decEClasses);
		return this;
	}
	
	private static int positiveRegion(Collection<UniverseInstance> instances, IntegerIterator attributes, 
										Collection<List<UniverseInstance>> decEClasses
	) {
		Collection<List<UniverseInstance>> eClasses = Basic.equivalentClass(instances, attributes);
		// POS = 0
		int pos = 0;
		// for i=1 to number of |PEClass|
		for (List<UniverseInstance> e: eClasses) {
			if (ClassicAttributeReductionSequentialAlgorithm
					.Basic
					.isPositiveRegion(e, decEClasses, attributes)
			) {
				pos += e.size();
			}
		}
		return pos;
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}

	@Override
	public Integer difference(Integer v1, Integer v2) {
		return v1-v2;
	}
}