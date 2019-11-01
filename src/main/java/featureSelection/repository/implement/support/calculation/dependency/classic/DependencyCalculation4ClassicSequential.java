package featureSelection.repository.implement.support.calculation.dependency.classic;

import java.util.Collection;
import java.util.List;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionSequentialAlgorithm;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionSequentialAlgorithm.Basic;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.sequential.ClassicSequentialCalculation;
import featureSelection.repository.implement.support.calculation.dependency.DefaultDependencyCalculation;
import lombok.Getter;

public class DependencyCalculation4ClassicSequential
	extends DefaultDependencyCalculation
	implements ClassicSequentialCalculation<Double>
{
	@Getter private Double positive;	
	@Override
	public Double getResult() {
		return positive;
	}

	public DependencyCalculation4ClassicSequential calculate(Collection<UniverseInstance> instances, 
			IntegerIterator attributes, Collection<List<UniverseInstance>> decEClasses, Object...args
	) {
		// Count the current calculation
		countCalculate(attributes.size());
		// Calculate
		positive = (Double) new Double(instances==null || instances.isEmpty()? 0.0: 
								positiveRegion(instances, attributes, decEClasses) / (instances.size()+0.0)
					);
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
	public Double difference(Double v1, Double v2) {
		return v1-v2;
	}
}