package featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased;

import java.util.Collection;

import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.ClassSet;

@RoughSet
public interface RoughEquivalentClassBasedCalculation<V, CSet extends ClassSet<?>> 
	extends FeatureImportance4RoughEquivalentClassBased<V> 
{
	public RoughEquivalentClassBasedCalculation<V, CSet> calculate(
			Collection<CSet> classSet, int universeSize, Object...args);
}
