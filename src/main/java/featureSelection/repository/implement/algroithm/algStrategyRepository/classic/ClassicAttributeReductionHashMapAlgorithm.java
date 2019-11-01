package featureSelection.repository.implement.algroithm.algStrategyRepository.classic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.ClassicStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;

/**
 * An implementation of {@link ClassicAttributeReductionSequentialIDAlgorithm} with HashMap search strategy.
 * 
 * @author Benjamin_L
 */
public class ClassicAttributeReductionHashMapAlgorithm 
	implements ClassicStrategy, 
				HashSearchStrategy
{
	public static class Basic {
		/**
		 * Calculate the Equivalent Classes by given attributes: U/P, where P is a set of attributes.
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}. (starts from 1, 0 as decision attribute)
		 * @return A {@link Map} of Equivalence Classes with {@link UniverseInstance} in {@link List}.
		 */
		public static Map<IntArrayKey, Collection<UniverseInstance>> equivalentClass(
				Collection<UniverseInstance> instances, IntegerIterator attributes
		){
			// Initiate a Hash map for equivalent classes: H.
			Map<IntArrayKey, Collection<UniverseInstance>> equivalentClass = new HashMap<>();
			// Loop over universes and group into equivalent classes bases on attributes values.
			int[] attrValue;
			IntArrayKey key;
			Collection<UniverseInstance> equList;
			for (UniverseInstance instance: instances) {
				// get the correspondent attribute of universe.
				attrValue = new int[attributes.size()];
				attributes.reset();
				for (int i=0; i<attributes.size(); i++)
					attrValue[i] = instance.getAttributeValue(attributes.next());
				key = new IntArrayKey(attrValue);
				// Update H (the equivalent classes)
				equList = equivalentClass.get(key);
				if (equList==null)	equivalentClass.put(key, equList = new HashSet<>());
				equList.add(instance);
			}
			// return H
			return equivalentClass;
		}
		
		/**
		 * Calculate the Equivalent Class of Decision Attribute: U/D
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}
		 * @return A {@link Collection} of Equivalence Class in {@link UniverseInstance} {@link List}
		 */
		public static Map<Integer, Collection<UniverseInstance>> equivalentClassOfDecisionAttribute(Collection<UniverseInstance> instances){
			// Initiate a Hash map for equivalent classes: H.
			Map<Integer, Collection<UniverseInstance>> equivalentClass = new HashMap<>();
			// For u, key = P(x[j]), execute:
			Collection<UniverseInstance> equSet;
			for (UniverseInstance instance: instances) {
				// Locate and update the correspondent equivalent class with the current universe.
				equSet = equivalentClass.get(instance.getAttributeValue(0));
				if (equSet==null)	equivalentClass.put(instance.getAttributeValue(0), equSet = new HashSet<>());
				equSet.add(instance);
			}
			// return H
			return equivalentClass;
		}
		
		/**
		 * Check if all {@link UniverseInstance} at the same decision equivalent class.
		 * 
		 * @param decEClasses
		 * 		A {@link Map} of {@link UniverseInstance} {@link List} as equivalent classes of decision value.
		 * @param equClassKey
		 * 		The key of an equivalent class.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return <code>true</code> if all {@link UniverseInstance} at the same decision equivalent class.
		 */
		public static boolean allUniversesAtTheSameDecEquClass(
				Collection<Collection<IntArrayKey>> decEClassKeys, IntArrayKey equClassKey
		) {
			// Loop over U/D
			int classNum = 0;
			for (Collection<IntArrayKey> decKeys : decEClassKeys) {
				// Mark the 1st/2nd decision value
				if (decKeys.contains(equClassKey))	classNum++;
				// If decision value number is more than 1, not all universes at the same U/D.
				if (classNum>1)						return false;
			}
			return true;
		}
	}
}