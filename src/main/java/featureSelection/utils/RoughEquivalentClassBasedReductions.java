package featureSelection.utils;

import java.util.Collection;

import featureSelection.repository.implement.algroithm.algStrategyRepository.roughEquivalentClassBased.RoughEquivalentClassBasedAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.EquivalentClass;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.RoughEquivalentClass;
import lombok.experimental.UtilityClass;

/**
 * Utilities for {@link RoughEquivalentClassBasedAlgorithm}.
 * 
 * @author Benjamin_L
 */
@UtilityClass
public class RoughEquivalentClassBasedReductions {
	
	public static <Equ extends EquivalentClass, Rough extends RoughEquivalentClass<Equ>> int 
		countUniverseSize(Collection<Rough> roughClasses
	) {
		if (roughClasses==null)	return 0;
		int count = 0;	for (RoughEquivalentClass<Equ> rough : roughClasses)	count += rough.getUniverseSize();
		return count;
	}
	
	public static <Equ extends EquivalentClass, Rough extends RoughEquivalentClass<Equ>> int 
		countEquivalentClassSize(Collection<Rough> roughClasses
	) {
		if (roughClasses==null)	return 0;
		int count = 0;	for (RoughEquivalentClass<Equ> rough : roughClasses)	count += rough.getItemSize();
		return count;
	}
}