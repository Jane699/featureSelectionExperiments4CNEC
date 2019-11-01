package featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.incrementalDependencyCalculation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.model.algStrategyRepository.incrementalDependencyCalculation.HashMapValue;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.incrementalDependencyCalculation.FeatureImportance4IncrementalDependencyCalculation;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.DefaultDependencyCalculation;

public class PositiveRegion4IDC 
	extends DefaultDependencyCalculation
	implements HashSearchStrategy, 
				FeatureImportance4IncrementalDependencyCalculation<Integer>
{
	private int dependency;
	
	@Override
	public Integer getResult() {
		return dependency;
	}

	public PositiveRegion4IDC calculate(Collection<UniverseInstance> instances, IntegerIterator attributes, 
										Object...args
	) {
		// Count the current calculation
		countCalculate(attributes.size());
		// Calculate
		dependency = instances==null || instances.isEmpty()? 0: dependency(instances, attributes);
		return this;
	}
	
	private static int dependency(Collection<UniverseInstance> instances, IntegerIterator attributes) {
		if (instances.size()==0)	return 0;
		
		// New a HashMap(H)
		Map<IntArrayKey, HashMapValue> map = new HashMap<>();
		// Initiate
		int udv=0;
		// Loop over X
		int index;
		IntArrayKey key; 
		int[] attrValues;
		HashMapValue mapValue;
		for (UniverseInstance instance : instances) {
			// Count the universe(x)
			// Set the key with the given attributes' values : P(x)
			attrValues = new int[attributes.size()];
			attributes.reset();
			while (attributes.hasNext()) {
				index = attributes.currentIndex();
				attrValues[index] = instance.getAttributeValue(attributes.next());
			}
			key = new IntArrayKey(attrValues);
			// Search for the key in map.
			// If no such key
			if ( !map.containsKey(key) ) {
				// create a sub item(h), h.cons=true, h.dec=x.dec, h.count=1
				map.put(key, new HashMapValue(true, instance.getAttributeValue(0)));
				udv++;
			// else
			}else {
				// Get the correspondent sub item(h)
				mapValue = map.get(key);
				// h.count++
				mapValue.add();
				if (mapValue.cons()) {
					// If h.cons is true
					if (mapValue.decisionValue()==instance.getAttributeValue(0)) {
						// If h.dec equals x.dec, udv++
						udv++;
					}else {
						// Else h.cons=false, UDV=UNV-h.count+1
						mapValue.setCons(false);
						udv = udv-mapValue.count()+1;
					}
				}
				map.replace(key, mapValue);
			}
		}
		return udv;
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}
}