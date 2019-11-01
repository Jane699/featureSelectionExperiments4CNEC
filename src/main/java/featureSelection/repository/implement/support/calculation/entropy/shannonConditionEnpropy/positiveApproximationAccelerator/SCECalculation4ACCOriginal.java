package featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.positiveApproximationAccelerator;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;
import featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.DefaultShannonConditionEnpropyCalculation;

public class SCECalculation4ACCOriginal 
	extends DefaultShannonConditionEnpropyCalculation 
	implements PositiveApproximationAcceleratorCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public SCECalculation4ACCOriginal calculate(Collection<EquivalentClass> equClasses, int attributeLength, Object...args) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = equClasses==null||equClasses.isEmpty()? 0: shannonEntropy(equClasses);
		return this;
	}

	private double shannonEntropy(Collection<EquivalentClass> equClasses) {
		// SCE = 0
		double entropy = 0;
		// Loop over E in U
		double dSum, tmp;
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
				tmp = decEqu.getInstances().size() / dSum;
				// SCE = SCE + dSum * ( (d/dSum) * log(d/dSum) )
				entropy -= dSum * tmp * Math.log(tmp);
			}
		}
		return entropy;
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof EquivalentClass;
	}
}