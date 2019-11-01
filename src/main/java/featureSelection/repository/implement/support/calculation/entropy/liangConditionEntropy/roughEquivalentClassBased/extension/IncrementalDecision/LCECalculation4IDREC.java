package featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.roughEquivalentClassBased.extension.IncrementalDecision;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap.DecisionInfoExtendedClassSet;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.DefaultLiangConditionEntropyCalculation;

public class LCECalculation4IDREC 
	extends DefaultLiangConditionEntropyCalculation
	implements RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Double> 
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public LCECalculation4IDREC calculate(DecisionInfoExtendedClassSet<?, ?> decisionInfo, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = decisionInfo==null? 0: liangEntropy(decisionInfo);
		return this;
	}
	public LCECalculation4IDREC calculate(Collection<? extends DecisionInfoExtendedClassSet<?, ?>> decisionInfos, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = 0;
		if (decisionInfos!=null) {
			for (DecisionInfoExtendedClassSet<?, ?> decisionInfo: decisionInfos) {
				entropy = this.plus(entropy, liangEntropy(decisionInfo));
			}
		}
		return this;
	}
	
	private double liangEntropy(DecisionInfoExtendedClassSet<?, ?> decisionInfo) {
		// LCE = 0
		double entropy = 0;
		// Loop over E in U
		Collection<Integer> dValues;
		// dSum = E.universeSize;
		dValues = decisionInfo.numberValues();
		// for i=1 to |E.dValue|
		for (int number: dValues) {
			// LCE = LCE + E.dValue[i].num * (dSum - c.dValue[i].num)
			entropy += number * ((long) decisionInfo.getUniverseSize() - (long) number);
		}
		return entropy;
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof DecisionInfoExtendedClassSet;
	}
}
