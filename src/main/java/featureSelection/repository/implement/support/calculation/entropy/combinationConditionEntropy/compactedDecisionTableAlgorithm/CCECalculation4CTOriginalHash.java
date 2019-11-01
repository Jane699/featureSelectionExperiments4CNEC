package featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.compactedDecisionTableAlgorithm;

import java.util.Collection;

import basic.model.interf.IntegerIterator;
import basic.utils.MathUtils;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original.CompactedDecisionTableCalculation;
import featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.DefaultCombinationConditionEntropyCalculation;

public class CCECalculation4CTOriginalHash 
	extends DefaultCombinationConditionEntropyCalculation
	implements CompactedDecisionTableCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public CCECalculation4CTOriginalHash calculate(
			Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records, 
			int attributeLength, Object...args
	) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = records==null || records.isEmpty()? 
					0.0: 
					complementEntropy(records) / commonDenominator((int) args[0]);
		return this;
	}

	private static double complementEntropy(Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records) {
		// CCE = 0
		double entropy = 0;
		// Loop over c in CTHash
		int number;
		double dSum;
		IntegerIterator dValue;
		for (CompactedTableRecord<? extends DecisionNumber> record : records) {
			// dSum=0
			dSum = 0;
			// c.dValue
			dValue = record.getDecisionNumbers()
							.numberValues();
			// for i=1 to |c.dValue|
			dValue.reset();
			while (dValue.hasNext()) {
				number = dValue.next();
				// dSum = dSum + c.dValue[i].number;
				dSum += number;
			}
			// CCE = CCE+dSum* dSum*(dSum-1) /2
			entropy += Math.pow(dSum, 2) * (dSum-1) / 2.0;
			// for i=1 to |c.dValue|
			dValue.reset();
			while (dValue.hasNext()) {
				number = dValue.next();
				// CCE = CCE - c.dValue[i] * c.dValue[i]*( c.dValue[i]-1)/2
				entropy = entropy - number * MathUtils.combinatorialNumOf2(number);
			}
		}
		// return CCE/ (|U|^2*|U|(|U|-1)/2)
		return entropy; // / commonDenominator(records.size());
	}
	
	private double commonDenominator(int universeSize) {
		return universeSize * MathUtils.combinatorialNumOf2(universeSize);
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof CompactedTableRecord;
	}
}