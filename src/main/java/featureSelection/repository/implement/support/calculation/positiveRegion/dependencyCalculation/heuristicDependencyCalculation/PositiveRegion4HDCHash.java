package featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.heuristicDependencyCalculation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.model.algStrategyRepository.heuristicDependencyCalculation.HashMapValue;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.heuristicDependencyCalculation.FeatureImportance4HeuristicDependencyCalculation;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.DefaultDependencyCalculation;

public class PositiveRegion4HDCHash
	extends DefaultDependencyCalculation 
	implements HashSearchStrategy, 
				FeatureImportance4HeuristicDependencyCalculation<Integer>
{
	private int dependency;
	@Override
	public Integer getResult() {
		return dependency;
	}
	
	@Override
	public FeatureImportance4HeuristicDependencyCalculation<Integer> calculate(
			Collection<UniverseInstance> instances, Collection<Integer> desisionValues, 
			IntegerIterator attribute, Object... args
	) {
		// Count the current calculation
		countCalculate(attribute.size());
		// Calculate
		dependency = instances==null || instances.isEmpty()? 0: dependency(instances, desisionValues, attribute);
		return this;
	}
	
	private int dependency(Collection<UniverseInstance> universes, Collection<Integer> desisionValues,
								IntegerIterator attributes
	) {
		// TotalCRecords = 0
		int totalCRecords = 0;
		// Loop over DecisionClasses
		for (int dValue : desisionValues)
			// TotalCRecords += calculateConsistentRecords(DecisionClassValue)
			totalCRecords += calculateConsistentRecords(universes, attributes, dValue);
		return totalCRecords;
	}
	
	/**
	 * Calculate the consistent records.
	 * 
	 * @param instances
	 * 		A {@link Collection} of {@link UniverseInstance}.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}. (Starts from 1)
	 * @param dValue
	 * 		Decision value the {@link UniverseInstance}.
	 * @return The number of consistent records.
	 */
	public static int calculateConsistentRecords(
			Collection<UniverseInstance> instances, IntegerIterator attributes, int dValue
	) {
		// RecordsCount = 0
		int recordsCount = 0;
		// Initiate a HashMap
		Map<IntArrayKey, HashMapValue> H = new HashMap<>();
		// Loop over xj in U
		int[] attrValue;
		IntArrayKey key;
		HashMapValue hashMapValue;
		for (UniverseInstance xj : instances) {
			// if DecisionClass(xj)==dValue
			if (Integer.compare(xj.getAttributeValue(0), dValue)==0) {
				// key = P(xj)
				attributes.reset();
				attrValue = new int[attributes.size()];
				for (int i=0; i<attrValue.length; i++)	attrValue[i] = xj.getAttributeValue(attributes.next());
				key = new IntArrayKey(attrValue);
						
				hashMapValue = H.get(key);
				if (hashMapValue==null) {
					// if no such key, create hk, hk.count=1, hk.cnst=true
					hashMapValue = new HashMapValue(true);
					H.put(key, hashMapValue);
				}else {
					// if key exists in H, hk.count++
					hashMapValue.add();
				}
			}
		}
		// Loop over xj in U
		for (UniverseInstance xj : instances) {
			// if D(xi)!=DValue
			if (Integer.compare(xj.getAttributeValue(0), dValue)!=0) {
				// key = P(xj)
				attrValue = new int[attributes.size()];
				attributes.reset();
				for (int i=0; i<attrValue.length; i++)	attrValue[i] = xj.getAttributeValue(attributes.next());
				key = new IntArrayKey(attrValue);
				// if key exists in H as hk
				hashMapValue = H.get(key);
				if (hashMapValue!=null) {
					// hk.cnst = false
					hashMapValue.setCnst(false);
				}
			}
		}
		// Loop over hi in H
		for (HashMapValue hi : H.values())
			// if hi.cnst = true, RecoredsCount++
			if (hi.cnst())	recordsCount+=hi.count();
		return recordsCount;
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}
}