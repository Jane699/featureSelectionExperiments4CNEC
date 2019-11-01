package featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.sequential;

import java.util.Collection;
import java.util.List;

import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.classic.ClassicReductionCalculation;

/**
 * An interface for Classic Feature Importance calculation using set intersect calculations. 
 * Calculations are implemented using sequential Structure {@link Collection} based on 
 * {@link UniverseInstance#getNum()}.
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
@RoughSet
public interface ClassicSequentialIDCalculation<V extends Number> 
	extends ClassicReductionCalculation<V> 
{
	/**
	 * Calculate the feature importance by <strong>Classic reduction</strong> using <code>{@link UniverseInstance} 
	 * ID based Sequential Search</code> when searching is involved.
	 * 
	 * @param eClasses
	 * 		Global Equivalent Class {@link Collection} of {@link UniverseInstance}s, sorting by particular 
	 * 		<code>condition attribute values</code>.
	 * @param decEClasses
	 * 		Global Equivalent Class {@link Collection} of {@link UniverseInstance}s, sorting by <code>decision 
	 * 		attribute value</code>.
	 * @param attributeLength
	 * 		The length of attributes involved in sorting <code>eClasses</code>.
	 * @param args
	 * 		Extra arguments.
	 * @return <code>this</code>.
	 */
	public ClassicSequentialIDCalculation<V> calculate(Collection<List<UniverseInstance>> eClasses, 
			Collection<List<UniverseInstance>> decEClasses, int attributeLength, Object...args);
	
	public V difference(V v1, V v2);
}