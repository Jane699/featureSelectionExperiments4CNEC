package featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UniverseBasedCompactedTableRecord<DN extends DecisionNumber> 
												implements CompactedTableRecord<DN>, 
															Cloneable 
{
	private UniverseInstance universeRepresentitive;
	private DN decisionNumbers;
	
	@SuppressWarnings("unchecked")
	@Override
	public UniverseBasedCompactedTableRecord<DN> clone() {
		return new UniverseBasedCompactedTableRecord<DN>(universeRepresentitive, (DN) decisionNumbers.clone());
	}
}