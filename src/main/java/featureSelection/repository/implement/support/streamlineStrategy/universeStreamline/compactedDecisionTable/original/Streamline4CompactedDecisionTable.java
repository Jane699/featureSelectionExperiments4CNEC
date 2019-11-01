package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original;

import java.util.Collection;
import java.util.Iterator;

import featureSelection.repository.frame.support.streamlineStrategy.UniverseStreamline;
import featureSelection.repository.implement.algroithm.algStrategyRepository.compactedDecisionTable.original.CompactedDecisionTableHashAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;

public class Streamline4CompactedDecisionTable<DN extends DecisionNumber>
	implements UniverseStreamline<StreamlineInput4CompactedDecisionTable<DN>, StreamlineRemoveableCriteria4CompactedDecisionTable<DN>, int[]> 
{
	/**
	 * @return Positive/Negative region removed universe number: [Pos(U/C), Neg(U/C)].
	 */
	@Override
	public int[] streamline(StreamlineInput4CompactedDecisionTable<DN> in) throws Exception {
		int consistentStatus, removePos=0, removeNeg=0;
		boolean containsAllUniverses;
		EquivalentClassCompactedTableRecord<DN> equClassRecord;
		Collection<UniverseBasedCompactedTableRecord<DN>> globalCompactedTableRecords;
		Iterator<EquivalentClassCompactedTableRecord<DN>> equequClassIterator = in.getDecisionTableRecords()
																					.iterator();
		while (equequClassIterator.hasNext()) {
			equClassRecord = equequClassIterator.next();
			consistentStatus = CompactedDecisionTableHashAlgorithm
									.Basic
									.checkConsistency(equClassRecord.getDecisionNumbers());
			if (in.isFilterPositiveRegion()) {
				// if dValue of h is single. (1 criteria of positive region)
				if (consistentStatus>0) {
					// if h in global positive region. (1 criteria of positive region)
					containsAllUniverses = false;
					globalCompactedTableRecords = equClassRecord.getEquivalentRecords();
					for (UniverseBasedCompactedTableRecord<? extends DecisionNumber> record : globalCompactedTableRecords) {
						containsAllUniverses = in.getGlobalPositiveRegion().contains(record);
						if (!containsAllUniverses)	break;
					}
					if (containsAllUniverses) {
						equequClassIterator.remove();
						removePos++;
						continue;
					}
				}
			}
			if (in.isFilterNegativeRegion()) {
				// if dValue of h is single. (1 criteria of negative region)
				if (consistentStatus<0) {
					// if h in global positive region. (1 criteria of negative region)
					containsAllUniverses = false;
					globalCompactedTableRecords = equClassRecord.getEquivalentRecords();
					for (UniverseBasedCompactedTableRecord<? extends DecisionNumber> record : globalCompactedTableRecords) {
						containsAllUniverses = in.getGlobalNegativeRegion().contains(record);
						if (!containsAllUniverses)	break;
					}
					if (containsAllUniverses) {
						equequClassIterator.remove();
						removeNeg++;
						continue;
					}
				}
			}
		}
		return new int[] {removePos, removeNeg};
	}

	@Override
	public boolean removable(StreamlineRemoveableCriteria4CompactedDecisionTable<DN> item) {
		if (item.getConsistentStatus()>0) {
			// if dValue of h is single. (1 criteria of positive region)
			if (item.isFilterPositiveRegion()) {
				// if h in global positive region. (1 criteria of positive region)
				boolean containsAllUniverses;
				Collection<UniverseBasedCompactedTableRecord<DN>> globalCompactedTableRecords = 
						item.getItem().getEquivalentRecords();
				for (UniverseBasedCompactedTableRecord<? extends DecisionNumber> record : globalCompactedTableRecords) {
					containsAllUniverses = item.getGlobalPositiveRegion().contains(record);
					if (!containsAllUniverses)	return false;
				}
				return true;
			}else {
				return false;
			}
		}else if (item.getConsistentStatus()<0) {
			// if dValue of h is single. (1 criteria of negative region)
			if (item.isFilterNegativeRegion()) {
				// if h in global positive region. (1 criteria of negative region)
				boolean containsAllUniverses;
				Collection<UniverseBasedCompactedTableRecord<DN>> globalCompactedTableRecords = 
						item.getItem().getEquivalentRecords();
				for (UniverseBasedCompactedTableRecord<? extends DecisionNumber> record : globalCompactedTableRecords) {
					containsAllUniverses = item.getGlobalNegativeRegion().contains(record);
					if (!containsAllUniverses)	return false;
				}
				return true;
			}else {
				return false;
			}
		}else {
			return true;
		}
	}
}
