package featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.roughEquivalentClassBased.extension.IncrementalDecision;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap.DecisionInfoExtendedClassSet;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.DefaultShannonConditionEnpropyCalculation;

public class SCECalculation4IDREC 
	extends DefaultShannonConditionEnpropyCalculation
	implements RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Double>
{
	private double entropy = 0;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public SCECalculation4IDREC calculate(DecisionInfoExtendedClassSet<?, ?> decisionInfo, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = decisionInfo==null? 0: shannonEntropy(decisionInfo);
		return this;
	}
	public SCECalculation4IDREC calculate(Collection<? extends DecisionInfoExtendedClassSet<?, ?>> decisionInfos, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = 0;
		if (decisionInfos!=null) {
			for (DecisionInfoExtendedClassSet<?, ?> decisionInfo : decisionInfos)
				entropy = this.plus(entropy, shannonEntropy(decisionInfo));
		}
		return this;
	}
	
	private double shannonEntropy(DecisionInfoExtendedClassSet<?, ?> decisionInfo) {
		// SCE = 0
		double entropy = 0;
		// Go through E in U
		double tmp;
		Collection<Integer> dValue;
		// dSum = E.universeSize
		dValue = decisionInfo.numberValues();
		// for i=1 to |E.dValue|
		for (int number: dValue) {
			tmp = number / (double) decisionInfo.getUniverseSize();
			// SCE = SCE + (dSum)*( (E.dValue[i].num/dSum) * log(E.dValue[i].num/dSum) ) 
			entropy -= decisionInfo.getUniverseSize() * (tmp*Math.log(tmp));
		}
		return entropy;
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof DecisionInfoExtendedClassSet;
	}
}
