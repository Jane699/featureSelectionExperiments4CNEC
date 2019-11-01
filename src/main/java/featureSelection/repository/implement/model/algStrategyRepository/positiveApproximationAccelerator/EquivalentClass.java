package featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator;

import java.util.Collection;
import java.util.HashSet;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import lombok.Data;

@Data
public class EquivalentClass {
	private Collection<UniverseInstance> instances;
	private int decisionValue;
	
	public EquivalentClass() {
		instances = new HashSet<>();
	}
	
	public void addUniverse(UniverseInstance u) {
		instances.add(u);
	}
}
