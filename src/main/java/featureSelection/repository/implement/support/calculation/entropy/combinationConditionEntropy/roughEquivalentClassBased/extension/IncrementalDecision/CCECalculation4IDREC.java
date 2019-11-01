package featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.roughEquivalentClassBased.extension.IncrementalDecision;

import java.util.Collection;

import basic.utils.MathUtils;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap.DecisionInfoExtendedClassSet;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.DefaultCombinationConditionEntropyCalculation;

public class CCECalculation4IDREC 
	extends DefaultCombinationConditionEntropyCalculation
	implements RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public CCECalculation4IDREC calculate(DecisionInfoExtendedClassSet<?, ?> decisionInfo, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		if (decisionInfo!=null) {
			entropy = complementEntropy(decisionInfo) / commonDenominator((int) args[0]);
		}
		return this;
	}
	public CCECalculation4IDREC calculate(Collection<? extends DecisionInfoExtendedClassSet<?, ?>> decisionInfos, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = 0;
		if (decisionInfos!=null) {
			for (DecisionInfoExtendedClassSet<?, ?> decisionInfo: decisionInfos)
				entropy = plus(entropy, complementEntropy(decisionInfo) / commonDenominator((int) args[0]));
		}
		return this;
	}
	
	private double complementEntropy(DecisionInfoExtendedClassSet<?, ?> decisionInfo) {
		// CCE = 0
		double entropy = 0;
		// Loop over E in U
		Collection<Integer> dValue;
		// dSum = U.size;
		dValue = decisionInfo.numberValues();
		// CCE = CCE+dSum*dSum*(dSum-1)/2
		entropy = entropy + decisionInfo.getUniverseSize() * MathUtils.combinatorialNumOf2(decisionInfo.getUniverseSize());
		// for i=1 to |E.dValue|
		for (int number: dValue) {
			// CCE = CCE - E.dValue[i].num * c.dValue[i].num*( c.dValue[i].num-1)/2
			entropy = entropy - number * MathUtils.combinatorialNumOf2(number);
		}
		return entropy;
	}
	
	private double commonDenominator(int universeSize) {
		return universeSize * MathUtils.combinatorialNumOf2(universeSize);
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof DecisionInfoExtendedClassSet;
	}
}