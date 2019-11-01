package featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.compactedDecisionTableAlgorithm;

import java.util.Collection;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original.CompactedDecisionTableCalculation;
import featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.DefaultLiangConditionEntropyCalculation;

public class LCECalculation4CTOriginalHash 
	extends DefaultLiangConditionEntropyCalculation 
	implements CompactedDecisionTableCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public LCECalculation4CTOriginalHash calculate(
			Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records, 
			int attributeLength,
			Object...args
	) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = records==null || records.isEmpty()? 0.0: liangEntropy(records);
		return this;
	}

	public static double liangEntropy(
			Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records
	) {
		// LCE = 0
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
			// for i=1 to |c.dValue|
			dValue.reset();
			while (dValue.hasNext()) {
				number = dValue.next();
				// LCE = LCE + c.dValue[i] * (dSum-c.dValue[i] )
				entropy += number * (dSum-number);
			}
		}
		// return LCE / |U|^2
		return entropy; /// commonDenominator(records.size());
	}
	
	@Override
	public boolean value1IsBetter(Double v1, Double v2, Double deviation) {
		double value = v2-v1;
		return Double.compare(value, deviation) > 0;
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof CompactedTableRecord;
	}
}
