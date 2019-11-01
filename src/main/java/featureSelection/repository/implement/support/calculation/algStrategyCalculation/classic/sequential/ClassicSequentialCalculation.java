package featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.sequential;

import java.util.Collection;
import java.util.List;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.ClassicReductionCalculation;

/**
 * An interface for Classic Feature Importance calculation using set intersect calculations. 
 * Calculations are implemented using sequential Structure {@link Collection}.
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
public interface ClassicSequentialCalculation<V extends Number> 
	extends ClassicReductionCalculation<V> 
{
	public ClassicSequentialCalculation<V> calculate(Collection<UniverseInstance> universes, 
			IntegerIterator attributes, Collection<List<UniverseInstance>> decEClasses, 
			Object...args);
	
	public V difference(V v1, V v2);
}