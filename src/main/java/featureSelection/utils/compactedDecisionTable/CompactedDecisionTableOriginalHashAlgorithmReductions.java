package featureSelection.utils.compactedDecisionTable;

import java.util.Collection;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.experimental.UtilityClass;
import featureSelection.repository.implement.algroithm.algStrategyRepository.compactedDecisionTable.original.CompactedDecisionTableHashAlgorithm;

/**
 * Utilities for {@link CompactedDecisionTableHashAlgorithm}.
 * 
 * @author Benjamin_L
 */
@UtilityClass
public class CompactedDecisionTableOriginalHashAlgorithmReductions {
	
	public static int universeSizeOfCompactedTableRecords(Collection<? extends CompactedTableRecord<? extends DecisionNumber>> tableRecords){
		int sum = 0;
		IntegerIterator decisionValues;
		for (CompactedTableRecord<? extends DecisionNumber> record: tableRecords) {
			decisionValues = record.getDecisionNumbers()
									.numberValues()
									.reset();
			while (decisionValues.hasNext())	sum += decisionValues.next();
		}
		return sum;
	}

	public static <DN extends DecisionNumber> int compactedRecordSizeOfEquivalentClassRecords(
			Collection<EquivalentClassCompactedTableRecord<DN>> equClasses
	){
		int sum = 0;
		for (EquivalentClassCompactedTableRecord<? extends DecisionNumber> equClass: equClasses)
			sum += equClass.getEquivalentRecords().size();
		return sum;
	}
}