package featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.dependencyCalculation.DirectDependencyCalculationStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.directDependencyCalculation.FeatureImportance4DirectDependencyCalculation;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.directDependencyCalculation.DependencyCalculation4DDCHash;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.directDependencyCalculation.DependencyCalculation4DDCSequential;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.directDependencyCalculation.PositiveRegion4DDCHash;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.directDependencyCalculation.PositiveRegion4DDCSequential;

/**
 * Algorithm implemented based on the paper 
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0888613X17300178">
 * "Feature selection using rough set-based direct dependency calculation by avoiding the positive region"</a> 
 * by Muhammad Summair Raza, Usman Qamar.
 * <p>
 * For dependency calculation, use {@link PositiveRegion4DDCHash} or {@link DependencyCalculation4DDCHash} 
 * for <code>Hash</code> search strategy and {@link PositiveRegion4DDCSequential} or 
 * {@link DependencyCalculation4DDCSequential} for <code>Sequential</code> search strategy.
 * 
 * @see {@link PositiveRegion4DDCHash}
 * @see {@link DependencyCalculation4DDCHash}
 * @see {@link PositiveRegion4DDCSequential}
 * @see {@link DependencyCalculation4DDCSequential}
 * 
 * @author Benjamin_L
 */
@RoughSet
public class DirectDependencyCalculationAlgorithm
	implements HashSearchStrategy, SequentialSearchStrategy,
				DirectDependencyCalculationStrategy
{
	/**
	 * Get the most significant current attribute.
	 * 
	 * @param <Sig>
	 * 		{@link Number} implemented type as the value of Significance.
	 * @param calculation
	 * 		Implemented {@link FeatureImportance4DirectDependencyCalculation}.
	 * @param sigDeviation
	 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
	 * 		difference between two sig is less than the given deviation value.
	 * @param instances
	 * 		A {@link Collection} of {@link UniverseInstance}.
	 * @param red
	 * 		Reduct attributes.
	 * @param globalSig
	 * 		The current dependency value.
	 * @param attributes
	 * 		All attributes. (Starts from 1)
	 * @return A integer value.
	 */
	public static <Sig extends Number> int mostSignificantAttribute(
			FeatureImportance4DirectDependencyCalculation<Sig> calculation, Sig sigDeviation,
			Collection<UniverseInstance> instances, Collection<Integer> red, Sig globalSig, int[] attributes
	){
		// Initiate
		int sigAttr = -1;
		Sig max = null, sig, subDependency;
		// Loop over potential attributes
		int i=0;	int[] attribute = new int[red.size()+1];	for (int r : red) attribute[i++] = r;
		for (int attr : attributes) {
			if (!red.contains(attr)) {
				// Calculate dep(P U {a})
				attribute[attribute.length-1] = attr;
				subDependency = calculation.calculate(instances, new IntegerArrayIterator(attribute))
											.getResult();
				// Sig(a) = dep(P U {a}) - dep(core)
				sig=calculation.difference(subDependency, globalSig);
				// If Sig(a)>max, update max and the most significant attribute
				if (max==null || calculation.value1IsBetter(sig, max, sigDeviation)) {
					max = sig;
					sigAttr = attr;
				}
			}
		}
		return sigAttr;
	}
}