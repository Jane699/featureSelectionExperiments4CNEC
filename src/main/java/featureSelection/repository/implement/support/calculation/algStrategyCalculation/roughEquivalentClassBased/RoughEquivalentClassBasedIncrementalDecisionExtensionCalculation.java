package featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap.DecisionInfoExtendedClassSet;

public interface RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<V> 
	extends FeatureImportance4RoughEquivalentClassBased<V>
{
	public RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<V> calculate(
			Collection<? extends DecisionInfoExtendedClassSet<?, ?>> decisionInfos, 
			int attributeLength, 
			Object...args
	);
	public RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<V> calculate(
			DecisionInfoExtendedClassSet<?, ?> decisionInfo, 
			int attributeLength, 
			Object...args
	);
}