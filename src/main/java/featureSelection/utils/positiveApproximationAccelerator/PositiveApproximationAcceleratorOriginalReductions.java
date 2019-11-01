package featureSelection.utils.positiveApproximationAccelerator;

import java.util.Collection;

import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import lombok.experimental.UtilityClass;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm;

/**
 * Utilities for {@link PositiveApproximationAcceleratorOriginalAlgorithm}.
 * 
 * @author Benjamin_L
 */
@UtilityClass
public class PositiveApproximationAcceleratorOriginalReductions {

	public static int universeSize(Collection<EquivalentClass> equClasses) {
		int size=0;
		for (EquivalentClass equ : equClasses)	size+= equ.getInstances().size();
		return size;
	}

}