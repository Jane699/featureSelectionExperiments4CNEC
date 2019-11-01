package featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.heuristicDependencyCalculation;

import java.util.Collection;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.FeatureImportance4DependencyCalculation;

/**
 * An interface for Feature Importance calculation using Rough Set Theory dependency(Positive region based) 
 * calculation based on <strong>Raza's heuristic dependency calculation methods</strong>.
 * <p>
 * Implementations should base on the original paper 
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0031320318301432">
 * "A heuristic based dependency calculation technique for rough set theory"</a>
 * by Muhammad Summair Raza, Usman Qamar.
 * 
 * @see {@link FeatureImportance4DependencyCalculation}
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
@RoughSet
public interface FeatureImportance4HeuristicDependencyCalculation<V> 
	extends FeatureImportance4DependencyCalculation<V> 
{
	public FeatureImportance4HeuristicDependencyCalculation<V> calculate(
			Collection<UniverseInstance> universes, Collection<Integer> decisionValues, 
			IntegerIterator attribute, Object...args
	);
	
	/**
	 * Difference between <code>v1</code> and <code>v2</code>. Usually calculated by v1-v2.
	 * 
	 * @param v1
	 * 		Value 1.
	 * @param v2
	 * 		Value 2.
	 * @return difference in V.
	 */
	public V difference(V v1, V v2);
}
