package featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original;

import java.util.Collection;

import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;

/**
 * An interface for Feature Importance calculation using Rough Set Theory dependency(Positive region based) 
 * calculation using <strong>equivalent class based calculations</strong>:
 * <pre>
 * C_(x) = {x∈U, [x]<sub>R</sub>⊆X}
 * Pos<sub>C</sub>(D) = ∪<sub>x∈U/D</sub> C_(x)
 * </pre>
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
@RoughSet
public interface PositiveApproximationAcceleratorCalculation<V> extends FeatureImportance<V>  {
	public PositiveApproximationAcceleratorCalculation<V> calculate(
			Collection<EquivalentClass> equClasses, int attributeLength, Object...args
	);
}
