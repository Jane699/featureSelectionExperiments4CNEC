package featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.MostSignificantAttributeResult;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;

/**
 * Algorithm repository for ACC, which is based on the paper 
 * <a href="https://www.sciencedirect.com/science/article/pii/S0004370210000548">
 * "Positive approximation An accelerator for attribute reduction in rough set theory"
 * </a> by Yuhua Qian, Jiye Liang, etc..
 * 
 * @author Benjamin_L
 */
public class PositiveApproximationAcceleratorOriginalAlgorithm {
	public static class Basic {
		/**
		 * Generate {@link EquivalentClass} {@link Collection} by the given {@link UniverseInstance}s and 
		 * attributes.
		 * 
		 * @param instances
		 * 		{@link UniverseInstance} {@link Collection}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return An {@link EquivalentClass} {@link Collection}
		 */
		public static Collection<EquivalentClass> equivalentClass(
				Collection<UniverseInstance> instances, IntegerIterator attributes
		){
			// Create a HashMap for U/P: H
			Map<IntArrayKey, EquivalentClass> equClasses = new HashMap<>();
			// Loop over x in U
			int index;
			int[] keyArray;
			IntArrayKey key;
			EquivalentClass equClass;
			for (UniverseInstance universe : instances) {
				// key = P(e)
				keyArray = new int[attributes.size()];
				attributes.reset();
				while (attributes.hasNext()) {
					index = attributes.currentIndex();
					keyArray[index] = universe.getAttributeValue(attributes.next());
				}
				key = new IntArrayKey(keyArray);
				equClass = equClasses.get(key);
				if (equClass==null) {
					// if H doesn't contain key
					//	create h, h.addMember(x), h.dec = D(x)
					equClasses.put(key, equClass=new EquivalentClass());
					equClass.addUniverse(universe);
					equClass.setDecisionValue(universe.getAttributeValue(0));
				}else {
					// if H contains key
					//	h.addMember(x)
					equClass.addUniverse(universe);
					// if D(x)!=h.dec
					//	h.dec='/'
					if (Integer.compare(equClass.getDecisionValue(), universe.getAttributeValue(0))!=0)
						equClass.setDecisionValue(-1);
				}
			}
			return equClasses.values();
		}
		
		/**
		 * Filter {@link EquivalentClass} with '/'(value=-1) decision value, i.e. delete positive regions.
		 * 
		 * @param equClasses
		 * 		An {@link EquivalentClass} {@link Collection}.
		 * @return A {@link Collection} of {@link UniverseInstance}.
		 */
		public static Collection<UniverseInstance> filteredPositiveRegionUniverses(
				Collection<EquivalentClass> equClasses
		){
			Collection<UniverseInstance> instances = new LinkedList<>();
			for (EquivalentClass equClass: equClasses)
				if (equClass.getDecisionValue()==-1)
					instances.addAll(equClass.getInstances());
			return instances;
		}
	}

	/**
	 * Get the core.
	 * 
	 * @param <Sig>
	 * 		Type of feature significance.
	 * @param equClasses
	 * 		An {@link EquivalentClass} {@link Collection}.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}.
	 * @param calculationClass
	 * 		{@link Class} of {@link Calculation}.
	 * @return A {@link Collection} of {@link Integer} as core.
	 */
	public static <Sig extends Number> Collection<Integer> core(
			Collection<UniverseInstance> instances, int[] attributes, Sig globalSig, 
			PositiveApproximationAcceleratorCalculation<Sig> calculation, Sig sigDeviation
	) {
		// core = {}
		Collection<Integer> core = new HashSet<>();
		// Loop over a in C
		Sig sig;
		int[] examAttributes = new int[attributes.length-1];
		for (int i=0; i<examAttributes.length; i++)	examAttributes[i] = attributes[i+1];
		Collection<EquivalentClass> roughEquClasses;
		for (int i=0; i<attributes.length; i++) {
			// Calculate significance of C-{a}: a.innerSig.
			roughEquClasses = Basic.equivalentClass(instances, new IntegerArrayIterator(examAttributes));
			sig = calculation.calculate(roughEquClasses, examAttributes.length, instances.size())
							.getResult();
			// if a.innrSig!=C.sig
			if (calculation.value1IsBetter(globalSig, sig, sigDeviation))
				core.add(attributes[i]);

			if (i<examAttributes.length)	examAttributes[i] = attributes[i];
		}
		return core;
	}

	/**
	 * Get the most significant attributes in current attributes out of reduct.
	 * 
	 * @param <Sig>
	 * 		{@link Number} implemented type as the value of Significance.
	 * @param equClasses
	 * 		{@link EquivalentClass} {@link Collection}.
	 * @param red
	 * 		Reduct {@link Collection}.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}.
	 * @param calculationClass
	 * 		{@link Class} of {@link Calculation}.
	 * @param sigDeviation
	 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
	 * 		difference between two sig is less than the given deviation value.
	 * @return An int value as the most significant attribute.
	 */
	public static <Sig extends Number> MostSignificantAttributeResult<Sig> mostSignificantAttribute(
			Collection<EquivalentClass> equClasses, Collection<Integer> red, int[] attributes, 
			int universeSize, PositiveApproximationAcceleratorCalculation<Sig> calculation, Sig sigDeviation
	) {
		// sig = 0; a*=0
		Sig maxSig=null;
		int sigAttr=-1;
		// newU = U
		// Loop over a in C-red
		Sig sig;
		Collection<EquivalentClass> subEquClasses, sigEquClasses = null;
		for (int attr : attributes) {
			if (red.contains(attr))	continue;
			// newU = equivalentClass(newU, Red U {a})
			red.add(attr);
			subEquClasses = new HashSet<>();
			for (EquivalentClass equ: equClasses) {
				subEquClasses.addAll(
						Basic.equivalentClass(
								equ.getInstances(), 
								new IntegerCollectionIterator(red)
						)
				);
			}
			red.remove(attr);
			// newU = filteredPositiveRegions(newU)
			// Calculate sig : a.outerSig
			sig = calculation.calculate(subEquClasses, red.size(), universeSize)
							.getResult();
			// if a.outerSig>sig, update max sig.
			if (maxSig==null || calculation.value1IsBetter(sig, maxSig, sigDeviation)) {
				sigAttr = attr;
				maxSig = sig;
				sigEquClasses = subEquClasses;
			}
		}
		return new MostSignificantAttributeResult<Sig>(sigAttr, maxSig, sigEquClasses);
	}

	/**
	 * Inspect the given reduct and remove redundant ones.
	 * 
	 * @param <Sig>
	 * 		{@link Number} implemented type as the value of Significance.
	 * @param red
	 * 		Reduct {@link Collection}.
	 * @param globalSig
	 * 		The global positive region number.
	 * @param instances
	 * 		{@link UniverseInstance} {@link Collection}.
	 * @param calculationClass
	 * 		{@link Class} of {@link Calculation}.
	 * @param sigDeviation
	 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
	 * 		difference between two sig is less than the given deviation value.
	 */
	public static <Sig extends Number> void inspection(
			Collection<Integer> red, Sig globalSig, Collection<UniverseInstance> instances,
			PositiveApproximationAcceleratorCalculation<Sig> calculation, Sig sigDeviation
	) {
		// Loop over a in R
		Sig examSig;
		Collection<EquivalentClass> equClasses;
		Integer[] redCopy = red.toArray(new Integer[red.size()]);
		for (int attr: redCopy) {
			// calculate Sig(R-{a}).
			red.remove(attr);
			equClasses = Basic.equivalentClass(instances, new IntegerCollectionIterator(red));
			examSig = calculation.calculate(equClasses, red.size(), instances.size()).getResult();
			// if (R-{a}.sig==C.sig)
			if (calculation.value1IsBetter(globalSig, examSig, sigDeviation)) {
				// R = R-{a}
				red.add(attr);
			}
		}
	}
}