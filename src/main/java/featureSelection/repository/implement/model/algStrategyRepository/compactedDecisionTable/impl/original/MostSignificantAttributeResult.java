package featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MostSignificantAttributeResult<Sig extends Number, DN extends DecisionNumber> {
	private Sig significance;
	private int attribute;
	private Collection<EquivalentClassCompactedTableRecord<DN>> equClasses;
}
