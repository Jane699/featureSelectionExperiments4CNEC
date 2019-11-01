package featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.positiveApproximationAccelerator;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;
import featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.DefaultLiangConditionEntropyCalculation;

public class LCECalculation4ACCOriginal 
	extends DefaultLiangConditionEntropyCalculation
	implements PositiveApproximationAcceleratorCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public LCECalculation4ACCOriginal calculate(Collection<EquivalentClass> equClasses, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = equClasses==null||equClasses.isEmpty()? 0: liangEntropy(equClasses);
		return this;
	}

	private double liangEntropy(Collection<EquivalentClass> equClasses) {
		// LCE = 0
		double entropy = 0;
		// Loop over E in U
		double dSum;
		Collection<EquivalentClass> decEquClasses;
		for (EquivalentClass equ: equClasses) {
			// H = equivalentClass(U, D)
			decEquClasses = PositiveApproximationAcceleratorOriginalAlgorithm
								.Basic
								.equivalentClass(equ.getInstances(), new IntegerArrayIterator(0));
			// dSum = 0
			dSum = 0;
			// for i=1 to |H|
			for (EquivalentClass decEqu : decEquClasses) {
				// dSum = dSum + d
				dSum += decEqu.getInstances().size();
			}
			// for i=1 to |H|
			for (EquivalentClass decEqu : decEquClasses) {
				// LCE = LCE + d*( (dSum -d )
				entropy += decEqu.getInstances().size() * (dSum - decEqu.getInstances().size());
			}
		}
		return entropy;
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof EquivalentClass;
	}
}
