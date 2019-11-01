package featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache;

import java.util.Collection;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An entity for Cache result of Information entropy calculations, with the following fields:
 * <li><strong>entropyValue</strong>: double
 * 	<p>The entropy value calculated.
 * </li>
 * <li><strong>equivalentClass</strong>: {@link Collection} of {@link UniverseInstance} {@link Collection}
 * 	<p>Correspondent Equivalent Classes partitioned by the attribute(s) used in the entropy calculation.
 * </li>
 * 
 * @author Benjamin_L
 */
@Data
@AllArgsConstructor
public class InformationEntropyCacheResult {
	private double entropyValue;
	private Collection<Collection<UniverseInstance>> equivalentClass;
}
