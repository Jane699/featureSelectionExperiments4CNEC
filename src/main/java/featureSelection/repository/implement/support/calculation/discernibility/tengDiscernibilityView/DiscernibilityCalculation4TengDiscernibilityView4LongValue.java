package featureSelection.repository.implement.support.calculation.discernibility.tengDiscernibilityView;

import java.util.Collection;

import org.apache.commons.math3.util.FastMath;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.TengDiscernibilityViewStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.discernibilityView.TengDiscernibilityViewAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.FeatureImportance4TengDiscernibilityView;
import lombok.Getter;

public class DiscernibilityCalculation4TengDiscernibilityView4LongValue 
	implements FeatureImportance4TengDiscernibilityView<Long>,
				TengDiscernibilityViewStrategy 
{
	@Getter private long calculationAttributeLength;
	@Getter private long calculationTimes;
	
	@Getter private Long result;

	@Override
	public DiscernibilityCalculation4TengDiscernibilityView4LongValue calculate(
			int universeSize, Collection<Collection<UniverseInstance>> equClasses, Object... args
	) {
		// |DIS(P)| = |U|^2 - &Sigma;<sub>t=1:m</sub>|P<sub>t</sub>|^2
		long sum = 0;
		for (Collection<UniverseInstance> equClass: equClasses)	sum += FastMath.pow(equClass.size(), 2);
		result = (long) (FastMath.pow(universeSize, 2) - sum);
		return this;
	}
	
	@Override
	public DiscernibilityCalculation4TengDiscernibilityView4LongValue calculate(
			Collection<Collection<UniverseInstance>> condEquClasses,
			Collection<Collection<UniverseInstance>> gainedEquClasses, Object... args
			
	) {
		// initiate DIS(Q/P): sum
		long dis = 0;
		// Go through p in U/P
		for (Collection<UniverseInstance> instances: condEquClasses) {
			// sum += |p|*|p|
			dis += FastMath.pow(instances.size(), 2);
		}
		// Go through m in U/(P&cup;Q)
		for (Collection<UniverseInstance> instances: gainedEquClasses) {
			// sum -= |m|*|m|
			dis -= FastMath.pow(instances.size(), 2);
		}
		result = dis;
		return this;
	}
	
	
	@Override
	public DiscernibilityCalculation4TengDiscernibilityView4LongValue calculateOuterSignificance(
			Collection<UniverseInstance> instances, 
			IntegerIterator conditionalAttributes, IntegerIterator outerAttributes, 
			IntegerIterator gainedAttributes,
			Object... args
	) {
		// SIG<sub>dis</sub><sup>outer</sup>(a, P, Q) = |DIS(Q/P)| - |DIS(Q/P∪{a})|
		//	U/P
		Collection<Collection<UniverseInstance>> condEquClasses = 
				TengDiscernibilityViewAlgorithm
					.Basic
					.equivalentClass(instances, conditionalAttributes)
					.values();
		//	(U/P)/D
		Collection<Collection<UniverseInstance>> gainedEquClasses = 
				TengDiscernibilityViewAlgorithm
					.Basic
					.gainEquivalentClass(condEquClasses, gainedAttributes);
		//	DIS(D/P)
		long disB4Gain = calculate(condEquClasses, gainedEquClasses).getResult().intValue();
		calculateOuterSignificance(condEquClasses, disB4Gain, outerAttributes, gainedAttributes, args);
		return this;
	}
	
	@Override
	public DiscernibilityCalculation4TengDiscernibilityView4LongValue calculateOuterSignificance(
			Collection<Collection<UniverseInstance>> condEquClasses, Long disB4Gain, 
			IntegerIterator outerAttributes, IntegerIterator gainedAttributes, 
			Object... args
	) {
		//	U/(P∪{a}), based on U/P
		Collection<Collection<UniverseInstance>> condEquClasses4Outer = 
				TengDiscernibilityViewAlgorithm
					.Basic
					.gainEquivalentClass(condEquClasses, outerAttributes);
		//	(U/(P∪{a}))/Q
		Collection<Collection<UniverseInstance>> gainedEquClasses4Outer = 
				TengDiscernibilityViewAlgorithm
					.Basic
					.gainEquivalentClass(condEquClasses4Outer, gainedAttributes);
		//	DIS(Q/P∪{a})
		long dis4Gained = calculate(condEquClasses4Outer, gainedEquClasses4Outer).getResult();
		//	SIG<sub>dis</sub><sup>outer</sup>(a, P, Q) = |DIS(Q/P)| - |DIS(Q/P∪{a})|
		result = disB4Gain - dis4Gained;
		return this;
	}

	
	@Override
	public boolean value1IsBetter(Long v1, Long v2, Long deviation) {
		throw new RuntimeException("Unimplemented method!");
	}

	@Override
	public Long plus(Long v1, Long v2) throws Exception {
		throw new Exception("Unimplemented method!");
	}
	
	@Override
	public <Item> boolean calculateAble(Item item) {
		if (item instanceof Collection) {
			Collection<?> collection = (Collection<?>) item;
			return collection.isEmpty() || collection.iterator().next() instanceof UniverseInstance;
		}else {
			return false;
		}
	}
}