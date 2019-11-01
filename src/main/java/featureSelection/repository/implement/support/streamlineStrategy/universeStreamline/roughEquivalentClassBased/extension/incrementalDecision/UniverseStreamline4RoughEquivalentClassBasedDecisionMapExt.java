package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision;

import java.util.Iterator;

import featureSelection.repository.frame.support.streamlineStrategy.UniverseStreamline;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.ClassSetType;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import lombok.Setter;

public class UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig extends Number>
		implements UniverseStreamline<StreamlineInput4RECIncrementalDecisionExtension<Sig>, 
										RoughEquivalentClassDecisionMapExtension<Sig>, 
										StreamlineResult4RECIncrementalDecisionExtension<Sig>>
{
	@Setter private int universeSize;
	@Setter private RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation;
	
	@Override
	public StreamlineResult4RECIncrementalDecisionExtension<Sig> streamline(
			StreamlineInput4RECIncrementalDecisionExtension<Sig> in
	) throws Exception {
		int removedNegUniverse = 0, removeNegEquClass = 0;
		Sig removedSig = null;
		RoughEquivalentClassDecisionMapExtension<Sig> roughClass;
		Iterator<RoughEquivalentClassDecisionMapExtension<Sig>> iterator = in.getRoughClasses()
																			.iterator();
		while (iterator.hasNext()) {
			roughClass = iterator.next();
			if (removable(roughClass)) {
				iterator.remove();
				calculation.calculate(roughClass, in.getAttributesInvolvedNumber(), universeSize);
				if (ClassSetType.NEGATIVE.equals(roughClass.getType())) {
					removedSig = calculation.plus(removedSig, calculation.getResult());
					removedNegUniverse += roughClass.getUniverseSize();
					removeNegEquClass += roughClass.getItemSize();
				}
			}
		}
		return new StreamlineResult4RECIncrementalDecisionExtension<>(
					in.getRoughClasses(), 
					new int[] {removedNegUniverse, removeNegEquClass}, 
					removedSig
				);
	}

	@Override
	public boolean removable(RoughEquivalentClassDecisionMapExtension<Sig> item) {
		return ClassSetType.POSITIVE.equals(item.getType()) || 
				(ClassSetType.NEGATIVE.equals(item.getType()) && item.getItemSize()==1)	||
				item.getItemSize()==0;
	}
}
