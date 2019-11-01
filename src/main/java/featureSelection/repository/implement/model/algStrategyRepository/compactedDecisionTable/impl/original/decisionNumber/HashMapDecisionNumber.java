package featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.decisionNumber;

import java.util.HashMap;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.model.interf.IntegerIterator;
import basic.utils.CollectionUtils;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import lombok.Getter;

@Getter
public class HashMapDecisionNumber implements DecisionNumber {
	private Map<Integer, Integer> decisionNumberMap;
	
	public HashMapDecisionNumber() {
		decisionNumberMap = new HashMap<>();
	}
	public HashMapDecisionNumber(Map<Integer, Integer> decisionNumberMap) {
		this.decisionNumberMap = decisionNumberMap;
	}
	
	@Override
	public int getNumberOfDecision(int decision) {
		Integer number = decisionNumberMap.get(decision);
		return number==null? 0: number;
	}

	@Override
	public void setDecisionNumber(int decision, int number) {
		decisionNumberMap.put(decision, number);
	}

	@Override
	public IntegerIterator decisionValues() {
		return new IntegerCollectionIterator(decisionNumberMap.keySet());
	}
	
	@Override
	public IntegerIterator numberValues() {
		return new IntegerCollectionIterator(decisionNumberMap.values());
	}
	
	@Override
	public HashMapDecisionNumber clone() {
		return new HashMapDecisionNumber(CollectionUtils.copyHashMap(decisionNumberMap));
	}
	@Override
	public String toString() {
		return decisionNumberMap.toString();
	}
}