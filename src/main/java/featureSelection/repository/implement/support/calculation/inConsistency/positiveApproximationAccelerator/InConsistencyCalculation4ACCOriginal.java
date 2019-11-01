package featureSelection.repository.implement.support.calculation.inConsistency.positiveApproximationAccelerator;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;
import featureSelection.repository.implement.support.calculation.inConsistency.DefaultInConsistencyCalculation;
import lombok.Getter;

public class InConsistencyCalculation4ACCOriginal 
	extends DefaultInConsistencyCalculation 
	implements PositiveApproximationAcceleratorCalculation<Integer>
{
	@Getter private int inConsistency;
	@Override
	public Integer getResult() {
		return inConsistency;
	}
	
	public InConsistencyCalculation4ACCOriginal calculate(Collection<EquivalentClass> records, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		inConsistency = records==null || records.isEmpty() ? 0: inConsistency(records);
		return this;
	}
	
	private static int inConsistency(Collection<EquivalentClass> records) {
		// incon = 0
		int inCon = 0;
		// Loop over e in U
		int dSum, maxNum = 0;
		Collection<EquivalentClass> decEquClasses;
		for (EquivalentClass equClass : records) {
			// H = equivalentClass(equClass, D)
			decEquClasses = PositiveApproximationAcceleratorOriginalAlgorithm
								.Basic
								.equivalentClass(equClass.getInstances(), new IntegerArrayIterator(0));
			// dSum = 0
			dSum = 0;
			// Loop over d in H
			maxNum = 0;
			for (EquivalentClass decEquClass: decEquClasses) {
				dSum += decEquClass.getInstances().size();
				if (decEquClass.getInstances().size()>maxNum)
					maxNum = decEquClass.getInstances().size();
			}
			inCon += dSum - maxNum;
		}
		return inCon;
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof EquivalentClass;
	}
}