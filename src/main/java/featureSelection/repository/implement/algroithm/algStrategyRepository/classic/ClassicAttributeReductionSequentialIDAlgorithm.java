package featureSelection.repository.implement.algroithm.algStrategyRepository.classic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;

/**
 * Algorithm repository of <strong>Classic Attribute Reduction</strong>.
 * 
 * @author Benjamin_L
 */
@RoughSet
public class ClassicAttributeReductionSequentialIDAlgorithm {
	public static class Basic {
		/**
		 * Calculate the Equivalent Classes by given attributes: U/P, where P is a set of attributes.
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}. (starts from 1, 0 as decision attribute)
		 * @return A {@link Collection} of Equivalence Class in {@link UniverseInstance} {@link List}
		 */
		public static Collection<List<UniverseInstance>> equivalentClass(
				Collection<UniverseInstance> instances, IntegerIterator attributes
		){
			UniverseInstance insPointer;
			List<UniverseInstance> equClass;
			Set<List<UniverseInstance>> equClasses = new HashSet<>();
			Iterator<UniverseInstance> iterator = instances.iterator();
			
			// U/P = {} ∪ {U[0]}
			insPointer = iterator.next();
			equClass = new ArrayList<>();
			equClass.add(insPointer);
			equClasses.add(equClass);
			
			// Loop over the rest of the record in U and partition.
			int attr;
			boolean newClass;
			UniverseInstance equRepresentitive;
			while (iterator.hasNext()) {
				// next universe.
				insPointer = iterator.next();
				// Loop over equivalent classes
				newClass = true;
				equClassesLoop:
				for (List<UniverseInstance> e: equClasses) {
					// Get the 1st universe in e as representative.
					equRepresentitive = e.get(0);
					// Loop over attributes and check if attribute values are equal between: uPointer, equRepresentitive.
					attributes.reset();
					while (attributes.hasNext()) {
						attr = attributes.next();
						// if attribute values differ, universe is not the member of the current equivalent class.
						if (insPointer.getAttributeValue(attr) != equRepresentitive.getAttributeValue(attr)) {
							// Check the next equivalent class
							continue equClassesLoop;
						}
					}
					// Add uPointer into e(inside equivalent class because all attribute values are equal)
					e.add(insPointer);
					newClass = false;
				}
				// If no existing equivalent class matches, uPointer belongs to a new equivalent class. 
				if (newClass) {
					// Create a new equivalent class and add into equClasses.
					equClass = new LinkedList<>();
					equClass.add(insPointer);
					equClasses.add(equClass);
				}
			}
			return equClasses;
		}
		
		/**
		 * Calculate the Equivalent Class of Decision Attribute: U/D
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}
		 * @return A {@link Collection} of Equivalence Class in {@link UniverseInstance} {@link List}
		 */
		public static Collection<List<UniverseInstance>> equivalentClassOfDecisionAttribute(
				Collection<UniverseInstance> instances
		){
			return equivalentClass(instances, new IntegerArrayIterator(0));
		}
	}
}