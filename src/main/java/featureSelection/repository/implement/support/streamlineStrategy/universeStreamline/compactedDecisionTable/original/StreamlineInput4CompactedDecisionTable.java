package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original;

import java.util.Collection;
import java.util.Set;

import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamlineInput4CompactedDecisionTable<DN extends DecisionNumber> {
	private Collection<EquivalentClassCompactedTableRecord<DN>> decisionTableRecords;

	private boolean filterPositiveRegion;
	private Set<UniverseBasedCompactedTableRecord<DN>> globalPositiveRegion;
	private boolean filterNegativeRegion;
	private Set<UniverseBasedCompactedTableRecord<DN>> globalNegativeRegion;
}
