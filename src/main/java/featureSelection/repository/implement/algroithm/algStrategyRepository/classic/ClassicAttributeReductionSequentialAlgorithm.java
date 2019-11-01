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
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.ClassicStrategy;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;

/**
 * Algorithm repository of Classic Attribute Reduction.
 * 
 * @author Benjamin_L
 */
public class ClassicAttributeReductionSequentialAlgorithm 
	implements ClassicStrategy, 
				SequentialSearchStrategy
{
	public static class Basic {
		/**
		 * Get the Equivalent Classes partitioned by given attributes: U/P, where P is a set of attributes.
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}. (starts from 1, 0 as decision attribute)
		 * @return A {@link Collection} of Equivalence Class in {@link UniverseInstance} {@link List}
		 */
		public static Collection<List<UniverseInstance>> equivalentClass(Collection<UniverseInstance> instances, IntegerIterator attributes){
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
		 * Calculate the Equivalent Class of Decision Attribute: U/D.
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}
		 * @return A {@link Collection} of Equivalence Class in {@link UniverseInstance} {@link List}
		 */
		public static Collection<List<UniverseInstance>> equivalentClassOfDecisionAttribute(Collection<UniverseInstance> instances){
			return equivalentClass(instances, new IntegerArrayIterator(0));
		}
	
		/**
		 * Check if {@link UniverseInstance} {@link List} is a part of positive region.
		 * 
		 * @param equClass
		 * 		{@link UniverseInstance} {@link List} as an equivalent class.
		 * @param decEClasses
		 * 		A {@link Collection} of {@link UniverseInstance} {@link List} as decision attribute equivalent classes.
		 * @param attributes
		 * 		Attribute of {@link UniverseInstance} used to partition <code>equClass</code>.
		 * @return <code>true</code> if it is a part of positive region.
		 */
		public static boolean isPositiveRegion(
				List<UniverseInstance> equClass, Collection<List<UniverseInstance>> decEClasses, 
				IntegerIterator attributes
		) {
			// Get the 1st Universe in Equivalent Class.
			UniverseInstance x = equClass.get(0);
			boolean found = false;
			// Loop over U/D
			for (List<UniverseInstance> decEquClass: decEClasses) {
				// classNum = classNum + belong(x, records in U/D[j])
				if (!found) {
					found = inUniverseCollection(x, decEquClass, attributes);
				}else if (inUniverseCollection(x, decEquClass, attributes)) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Check if {@link UniverseInstance} in the {@link #decU} Collection.
		 * 
		 * @param instance
		 * 		An {@link UniverseInstance}.
		 * @param instanceCollection
		 * 		A {@link Collection} of {@link UniverseInstance} as an equivalent class.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance} for equivalent classes.
		 * @return <code>true</code> if it is.
		 */
		public static boolean inUniverseCollection(
				UniverseInstance instance, Collection<UniverseInstance> instanceCollection, 
				IntegerIterator attributes
		) {
			// Loop over universe in universe Collection.
			int attr;
			UniverseCollectionItemLoop:
			for (UniverseInstance insInCollection : instanceCollection) {
				// Loop over attribute values in universe and uInCollection
				attributes.reset();
				while (attributes.hasNext()) {
					attr = attributes.next();
					// Check if attribute values are equal between: uInCollection, universe
					if (Integer.compare(insInCollection.getAttributeValue(attr), instance.getAttributeValue(attr))!=0)
						// Check the next universe in universeCollection
						continue UniverseCollectionItemLoop;
				}
				// return true.
				return true;
			}
			// return false.
			return false;
		}
	}
}