package featureSelection.repository.implement.algroithm.algStrategyRepository.discernibilityView;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionHashMapAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.FeatureImportance4TengDiscernibilityView;

/**
 * Algorithm repository of Teng-Forward Attribute Reduction from the Discernibility View(FAR-DV), which 
 * bases on the paper <a href="https://linkinghub.elsevier.com/retrieve/pii/S0020025515005605"> "
 * Efficient attribute reduction from the viewpoint of discernibility"</a> by Shu-Hua Teng, Min Lu, A-Feng 
 * Yang, Jun Zhang, Yongjian Nian, Mi He.
 * 
 * @author Benjamin_L
 */
public class TengDiscernibilityViewAlgorithm {

	public static class Basic {
		
		/**
		 * Get equivalent classes partitioned by the given attributes: <strong>U/P</strong>.
		 * 
		 * @see {@link ClassicAttributeReductionHashMapAlgorithm.Basic#equivalentClass(Collection, IntegerIterator)}
		 * 
		 * @param instances
		 * 		An {@link UniverseInstance} {@link Collection} to be partitioned.
		 * @param attributes
		 * 		Attributes used in the partition.
		 * @return A {@link Map} with equivalent values as keys and {@link UniverseInstance} {@link Collection} as 
		 * 		equivalent class as values.
		 */
		public static Map<IntArrayKey, Collection<UniverseInstance>> equivalentClass(
				Collection<UniverseInstance> instances, IntegerIterator attributes
		){
			return ClassicAttributeReductionHashMapAlgorithm
					.Basic
					.equivalentClass(instances, attributes);
		}
		
		/**
		 * Get equivalent classes further partitioned by the given attributes: <strong>(U/P)/Q</strong>.
		 * 
		 * @param equClasses
		 * 		A {@link Collection} of {@link UniverseInstance} {@link Collection} as equivalent classes to be 
		 * 		further partitioned.
		 * @param attributes
		 * 		Attributes used in the partition.
		 * @return A {@link Collection} of {@link UniverseInstance} {@link Collection} as equivalent classes.
		 */
		public static Collection<Collection<UniverseInstance>> gainEquivalentClass(
				Collection<Collection<UniverseInstance>> equClasses, IntegerIterator attributes
		){
			Collection<Collection<UniverseInstance>> newEquClasses = new LinkedList<>();
			// Loop over equivalent class in the given equivalent classes.
			for (Collection<UniverseInstance> equClass: equClasses) {
				// Use the given attributes to partition universes in it and collect results.
				newEquClasses.addAll(equivalentClass(equClass, attributes).values());
			}
			return newEquClasses;
		}
		
	}
	
	/**
	 * Check if the given Equivalent Class is a part of the given Equivalent Classes: with every 
	 * {@link UniverseInstance} in <code>equClass</code> all in the same equivalent class of 
	 * <code>equClasses</code>. 
	 * 
	 * @param equClasses
	 * 		A {@link Collection} of {@link UniverseInstance} {@link Collection} as equivalent classes which
	 * 		contains a lot of {@link UniverseInstance} {@link Collection}s which are considered as an equivalent 
	 * 		class each.
	 * @param equClass
	 * 		An {@link UniverseInstance} {@link Collection} as an equivalent class.
	 * @return <code>true</code> if <code>equClass</code> is a sub-element of <code>equClasses</code>.
	 */
	public static boolean isSubEquivalentClassOf(
			Collection<Collection<UniverseInstance>> equClasses, Collection<UniverseInstance> equClass
	) {
		for (Collection<UniverseInstance> e: equClasses) {	
			if (e.containsAll(equClass)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Select the most significant attribute bases on (relative) discernibility degree based outer 
	 * significance calculation: 
	 * <li>select the one with max. outer significance;</li>
	 * <li> if outer significance are the same, select the one with the smallest discernibility degree.</li>
	 * 
	 * @param <Sig>
	 * 		Type of attribute significance.
	 * @param instanceSize
	 * 		The size of {@link UniverseInstance} in <code>redEquClasses</code>.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}
	 * @param redundantAttributes
	 * 		Redundant attributes.
	 * @param redEquClasses
	 * 		Equivalent Classes partitioned by reduct.
	 * @param redRelativeDisDegree
	 * 		The relative discernibility degree of the reduct.
	 * @param calculation
	 * 		{@link FeatureImportance4TengDiscernibilityView} instance.
	 * @return the selected most significant attribute.
	 */
	public static <Sig extends Number> int mostSignificantAttribute(
			int instanceSize, int[] attributes, 
			Collection<Integer> redundantAttributes,
			Collection<Collection<UniverseInstance>> redEquClasses, Sig redRelativeDisDegree, 
			FeatureImportance4TengDiscernibilityView<Sig> calculation
	) {
		// Initiate.
		int sigAttr = -1, sigValue = 0, disValue = -1;
		
		int outerSigOfAttr;
		IntegerIterator gainedAttribute = new IntegerArrayIterator(0);
		for (int attr: attributes) {
			// skip attribute in A'(i.e. attribute not in A[j])
			if (redundantAttributes.contains(attr))	continue;
			// Calculate SIG<sup>outer</sup><sub>dis</sub>(a[t], red, D).
			outerSigOfAttr = calculation.calculateOuterSignificance(
								// U/red
								redEquClasses, 
								// |DIS(D/red)|
								redRelativeDisDegree, 
								// a[t]
								new IntegerArrayIterator(attr), 
								// D
								gainedAttribute
							).getResult().intValue();
			// Update sig.
			if (sigAttr==-1) {
				sigAttr = attr;
				sigValue = outerSigOfAttr;
				disValue = calculation.calculate(instanceSize, redEquClasses).getResult().intValue();
			}else {
				int cmp = Integer.compare(outerSigOfAttr, sigValue);
				if (cmp<0) {
					// do nothing ...
				}else {
					int attrDis = calculation.calculate(instanceSize, redEquClasses).getResult().intValue();
					if (cmp>0 || Integer.compare(attrDis, disValue)<0) {
						sigAttr = attr;
						sigValue = outerSigOfAttr;
						disValue = attrDis;
					}
				}
			}
		}
		return sigAttr;
	}
}