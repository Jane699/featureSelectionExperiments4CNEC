package featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.dependencyCalculation.IncrementalDependencyCalculationStrategy;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.incrementalDependencyCalculation.FeatureImportance4IncrementalDependencyCalculation;

/**
 * Algorithm repository of IDC, which based on the paper 
 * <a href="https://www.sciencedirect.com/science/article/pii/S0020025516000785">
 * "An incremental dependency calculation technique for feature selection using rough sets"</a> 
 * by Muhammad Summair Raza, Usman Qamar.
 * 
 * @author Benjamin_L
 */
@RoughSet
public class IncrementalDependencyCalculationAlgorithm 
	implements HashSearchStrategy,
				IncrementalDependencyCalculationStrategy
{
	/**
	 * Get the most significant current attribute.
	 * 
	 * @param <Sig>
	 * 		{@link Number} implemented type as the value of Significance.
	 * @param calculation
	 * 		Implemented {@link FeatureImportance4IncrementalDependencyCalculation}.
	 * @param sigDeviation
	 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
	 * 		difference between two sig is less than the given deviation value.
	 * @param instances
	 * 		A {@link Collection} of {@link UniverseInstance}.
	 * @param red
	 * 		Reduct attributes.
	 * @param dependency
	 * 		Reduct dependency value.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}. (Starts from 1)
	 * @return A {@link int} value as the most significant attribute's index.
	 */
	public static <Sig extends Number> int mostSignificantAttribute(
			FeatureImportance4IncrementalDependencyCalculation<Sig> calculation, Sig sigDeviation, 
			Collection<UniverseInstance> instances, Collection<Integer> red, Sig redDependency, 
			int[] attributes
	){
		// Initiate
		int sigAttr = -1;
		Sig max = null, sig, subDependency;
		// Loop over potential attributes
		int i=0;	int[] attribute = new int[red.size()+1];	for ( int r : red) attribute[i++] = r;
		for (int attr : attributes) {
			if (!red.contains(attr)) {
				// Calculate dep(P U {a})
				attribute[attribute.length-1] = attr;
				
				subDependency = calculation.calculate(instances, new IntegerArrayIterator(attribute))
											.getResult();
				// Sig(a) = dep(P U {a}) - dep(P)
				sig = calculation.difference(subDependency, redDependency);
				// If Sig(a)>max, update max and the most significant attribute
				if (max==null||calculation.value1IsBetter(sig, max, sigDeviation)) {
					max = sig;
					sigAttr = attr;
				}
			}
		}
		return sigAttr;
	}
	
	/**
	 * Check if meets the algorithm stop criteria.
	 * 
	 * @param <Sig>
	 * 		{@link Number} implemented type as the value of Significance.
	 * @param calculation
	 * 		Implemented {@link FeatureImportance}.
	 * @param sigDeviation
	 * 		Acceptable deviation when calculating significance of attribuets. Consider equal when the 
	 * 		difference between two sig is less than the given deviation value.
	 * @param globalSig
	 * 		The global dependency value.
	 * @param redSig
	 * 		Reduct dependency value.
	 * @return <code>true</code> if meets the stopping criteria.
	 */
	public static <Sig extends Number> boolean continueLoopQuickReduct(
			FeatureImportance<Sig> calculation, Sig sigDeviation, Sig globalSig, Sig redSig
	) {
		return !calculation.value1IsBetter(globalSig, redSig, sigDeviation);
	}
}