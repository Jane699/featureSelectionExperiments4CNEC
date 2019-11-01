package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StreamlineInput4RECIncrementalDecisionExtension<Sig extends Number> {
	private Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
	private int attributesInvolvedNumber;
}