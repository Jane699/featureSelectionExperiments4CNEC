package featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.positiveApproximationAccelerator;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.utils.MathUtils;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;
import featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.DefaultCombinationConditionEntropyCalculation;

public class CCECalculation4ACCOriginal 
	extends DefaultCombinationConditionEntropyCalculation 
	implements PositiveApproximationAcceleratorCalculation<Double>
{
	private double entropy;
	@Override
	public Double getResult() {
		return entropy;
	}
	
	public CCECalculation4ACCOriginal calculate(
			Collection<EquivalentClass> equClasses, int attributeLength, Object...args
	) {
		// Count the current calculation
		countCalculate(attributeLength);
		// Calculate
		entropy = equClasses==null || equClasses.isEmpty()? 
					0: complementEntropy(equClasses) / commonDenominator((int) args[0]);
		return this;
	}

	private double complementEntropy(Collection<EquivalentClass> equClasses) {
		// CCE = 0
		double entropy = 0;
		// Loop over E in U
		int dSum, tmp;
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
			// CCE = CCE+dSum* dSum*(dSum-1) /2
			entropy += dSum * MathUtils.combinatorialNumOf2(dSum);
			// for i=1 to |H|
			for (EquivalentClass decEqu : decEquClasses) {
				tmp = decEqu.getInstances().size();
				// CCE = CCE - d*d*(d -1)/2
				entropy = entropy - tmp * MathUtils.combinatorialNumOf2(tmp);
			}
		}
		return entropy;
	}
	
	private double commonDenominator(int universeSize) {
		return universeSize * MathUtils.combinatorialNumOf2(universeSize);
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof EquivalentClass;
	}
}
