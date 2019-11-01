package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original;

import java.util.Set;

import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamlineRemoveableCriteria4CompactedDecisionTable<DN extends DecisionNumber> {
	private int consistentStatus;
	private EquivalentClassCompactedTableRecord<DN> item;
	
	private boolean filterPositiveRegion;
	private Set<UniverseBasedCompactedTableRecord<DN>> globalPositiveRegion;
	private boolean filterNegativeRegion;
	private Set<UniverseBasedCompactedTableRecord<DN>> globalNegativeRegion;
}
