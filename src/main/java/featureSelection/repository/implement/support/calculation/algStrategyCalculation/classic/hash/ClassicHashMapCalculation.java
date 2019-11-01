package featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.hash;

import java.util.Collection;
import java.util.Map;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.ClassicReductionCalculation;

/**
 * An interface for Classic Feature Importance calculation using set intersect calculations. 
 * Calculations are implemented using {@link HashMap}.
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
@RoughSet
public interface ClassicHashMapCalculation<V extends Number> 
	extends ClassicReductionCalculation<V> 
{
	public ClassicHashMapCalculation<V> calculate(Collection<UniverseInstance> universes, 
			IntegerIterator attributes, Map<Integer, Collection<UniverseInstance>> decEClasses, 
			Object...args);
}