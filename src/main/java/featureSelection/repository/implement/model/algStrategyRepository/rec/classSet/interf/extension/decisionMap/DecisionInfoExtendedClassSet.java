package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.ClassSet;

public interface DecisionInfoExtendedClassSet<Item, Decision> extends ClassSet<Item> {
	
	Decision getDecisionInfo();
	
	Collection<Integer> decisionValues();
	Collection<Integer> numberValues();
}
