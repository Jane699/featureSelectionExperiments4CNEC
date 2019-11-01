package featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.dependencyCalculation.HeuristicDependencyCalculationStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.heuristicDependencyCalculation.FeatureImportance4HeuristicDependencyCalculation;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.incrementalDependencyCalculation.FeatureImportance4IncrementalDependencyCalculation;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.heuristicDependencyCalculation.DependencyCalculation4HDCHash;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.heuristicDependencyCalculation.DependencyCalculation4HDCSequential;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.heuristicDependencyCalculation.PositiveRegion4HDCHash;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.heuristicDependencyCalculation.PositiveRegion4HDCSequential;

/**
 * Algorithm repository of HDC, which based on the Article 
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0031320318301432">
 * "A heuristic based dependency calculation technique for rough set theory"</a> 
 * by Muhammad Summair Raza, Usman Qamar.
 * <p>
 * For dependency calculation, use {@link DependencyCalculation4HDCHash} for <code>Hash</code> search
 * strategy and {@link DependencyCalculation4HDCSequential} for <code>Sequential</code> search strategy.
 * 
 * @see {@link DependencyCalculation4HDCHash}
 * @see {@link DependencyCalculation4HDCSequential}
 * @see {@link PositiveRegion4HDCHash}
 * @see {@link PositiveRegion4HDCSequential}
 * 
 * @author Benjamin_L
 */
@RoughSet
public class HeuristicDependencyCalculationAlgorithm
	implements HashSearchStrategy, SequentialSearchStrategy,
				HeuristicDependencyCalculationStrategy
{
	public static class Basic {
		/**
		 * Get the decision values of the given {@link UniverseInstance} {@link Collection}.
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}.
		 * @return A {@link Collection} of {@link Integer} values as decision values.
		 */
		public static Collection<Integer> decisionValues(Collection<UniverseInstance> instances){
			Set<Integer> decisionValues = new HashSet<>();
			for (UniverseInstance ins: instances) {
				if (!decisionValues.contains(ins.getAttributeValue(0)))
					decisionValues.add(ins.getAttributeValue(0));
			}
			return decisionValues;
		}
	}
	
	/**
	 * Get the most significant current attribute
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
	 * 		Reduct attributes. (Starts from 1)
	 * @param globalSig
	 * 		The current dependency value.
	 * @param attributes
	 * 		All attributes. (Starts from 1)
	 * @return A {@link List} of {@link Integer} value as attribute index (Starts from 1).
	 * @throws Exception if the result is -1
	 */
	public static <Sig extends Number> int mostSignificantAttribute(
			FeatureImportance4HeuristicDependencyCalculation<Sig> calculation, Sig sigDeviation,
			Collection<UniverseInstance> instances, Collection<Integer> red, Sig globalSig, 
			int[] attributes, Collection<Integer> decisionValues
	){
		// Initiate
		int sigAttr = -1;
		Sig max = null, sig, subSig;
		// Loop over potential attributes
		int i=0;	int[] attribute = new int[red.size()+1];	for (int r : red) attribute[i++] = r;
		for (int attr : attributes) {
			if (!red.contains(attr)) {
				// Calculate dep(P ∪ {a})
				attribute[attribute.length-1] = attr;
				subSig = calculation.calculate(instances, decisionValues, new IntegerArrayIterator(attribute))
									.getResult();
				// Sig(a) = dep(P ∪ {a}) - dep(core)
				sig = calculation.difference(subSig, globalSig);
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