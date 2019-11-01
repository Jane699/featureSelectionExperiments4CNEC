package featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EquivalentClassCompactedTableRecord<DN extends DecisionNumber> implements CompactedTableRecord<DN> {
	private DN decisionNumbers;
	private Collection<UniverseBasedCompactedTableRecord<DN>> equivalentRecords;
}