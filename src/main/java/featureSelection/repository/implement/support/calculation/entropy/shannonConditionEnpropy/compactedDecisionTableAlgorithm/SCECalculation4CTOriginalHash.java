package featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.compactedDecisionTableAlgorithm;

import java.util.Collection;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original.CompactedDecisionTableCalculation;
import featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.DefaultShannonConditionEnpropyCalculation;

public class SCECalculation4CTOriginalHash 
	extends DefaultShannonConditionEnpropyCalculation 
	implements CompactedDecisionTableCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public SCECalculation4CTOriginalHash calculate(Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = records==null || records.isEmpty()? 0.0: complementEntropy(records);
		return this;
	}

	private static double complementEntropy(Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records) {
		// SCE = 0
		double entropy = 0;
		// Loop over c in CTHash
		int number;
		double dSum, tmp;
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
				// SCE = SCE + (dSum)*( (c.dValue[i]/dSum) * log(c.dValue[i]/dSum) )
				tmp = number / dSum;
				entropy -= dSum * tmp * Math.log(tmp);
			}
		}
		// return SCE / |U|
		return entropy;
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof CompactedTableRecord;
	}
}