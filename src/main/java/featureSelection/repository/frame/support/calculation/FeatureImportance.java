package featureSelection.repository.frame.support.calculation;

import basic.model.interf.Calculation;
import featureSelection.repository.frame.annotation.theory.RoughSet;

/**
 * Calculate the importance of feature (subset).
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
@RoughSet
public interface FeatureImportance<V> 
	extends Calculation<V> 
{
	/**
	 * Get how many attributes involved in calculations by adding up attributes' length of each calculation.
	 * 
	 * @return The sum of attribute length in {@link long}.
	 */
	long getCalculationAttributeLength();
	
	/**
	 * Evaluate whether <code>v1</code> is better than <code>v2</code> in the view of <strong>feature 
	 * importance</strong>. Difference between the two values is ignored if it is less than 
	 * <code>deviation</code>.
	 * 
	 * @param v1
	 * 		Value 1 to be evaluated.
	 * @param v2
	 * 		Value 2 to be evaluated.
	 * @param deviation
	 * 		Acceptable difference between <code>v1</code> and <code>v2</code>.
	 * @return true if <code>v1</code> is better than <code>v2</code>.
	 */
	boolean value1IsBetter(V v1, V v2, V deviation);
	/**
	 * Calculate the value of v1 + v2.
	 * 
	 * @param v1
	 * 		Value 1 to be calculated.
	 * @param v2
	 * 		Value 2 to be calculated.
	 * @return the result.
	 */
	V plus(V v1, V v2) throws Exception;
	/**
	 * Check if the <code>item</code> is calculateable.
	 * 
	 * @param <Item>
	 * 		Type of item involved in calculation.
	 * @param item
	 * 		An {@link Item} to be checked.
	 * @return true if it is calculateable.
	 */
	<Item> boolean calculateAble(Item item);
}
