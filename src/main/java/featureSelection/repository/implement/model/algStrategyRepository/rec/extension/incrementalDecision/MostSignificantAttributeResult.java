package featureSelection.repository.implement.model.algStrategyRepository.rec.extension.incrementalDecision;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MostSignificantAttributeResult<Sig extends Number> {
	private Sig significance;
	private Sig globalStaticSiginificance;
	private int attribute;
	private Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
}
