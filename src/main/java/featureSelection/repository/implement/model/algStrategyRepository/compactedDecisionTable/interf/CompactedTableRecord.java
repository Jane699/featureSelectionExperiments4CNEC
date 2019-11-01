package featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf;

public interface CompactedTableRecord<DN extends DecisionNumber> {
	DN getDecisionNumbers();
}