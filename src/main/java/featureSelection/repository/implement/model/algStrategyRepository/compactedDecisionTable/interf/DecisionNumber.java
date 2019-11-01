package featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf;

import basic.model.interf.IntegerIterator;

public interface DecisionNumber extends Cloneable {
	int getNumberOfDecision(int decision);
	void setDecisionNumber(int decision, int number);
	
	IntegerIterator decisionValues();
	IntegerIterator numberValues();
	DecisionNumber clone();
}