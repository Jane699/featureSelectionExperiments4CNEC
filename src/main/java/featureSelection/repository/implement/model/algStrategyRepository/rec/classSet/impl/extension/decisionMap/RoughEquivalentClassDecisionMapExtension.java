package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap;

import java.util.Map;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.RoughEquivalentClass;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap.DecisionInfoExtendedClassSet;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;

/**
 * An extension model of {@link RoughEquivalentClass}. 
 * <p>{@link DecisionInfoExtendedClassSet} implemented, using a {@link Map} for decision values info: 
 * <p>{ key-decision value({@link Integer}), value-number-({@link Integer}) }.
 * 
 * @see {@link EquivalentClassDecisionMapExtension}.
 * 
 * @author Benjamin_L
 */
public class RoughEquivalentClassDecisionMapExtension<Sig extends Number> 
	extends RoughEquivalentClass<EquivalentClassDecisionMapExtension<Sig>>
	implements DecisionInfoExtendedClassSet<EquivalentClassDecisionMapExtension<Sig>, Map<Integer, Integer>>
{
	@Getter private Map<Integer, Integer> decisionInfo;
		
	public RoughEquivalentClassDecisionMapExtension() {	
		super();
		decisionInfo = new HashMap<>();
	}
	
	@Override
	public void addClassItem(EquivalentClassDecisionMapExtension<Sig> item) {
		super.addClassItem(item);
		for (Map.Entry<Integer, Integer> entry: item.getDecisionInfo().entrySet()) {
			decisionInfo.put(entry.getKey(), 
							decisionInfo.containsKey(entry.getKey())?
							entry.getValue()+decisionInfo.get(entry.getKey()):
							entry.getValue()
			);
		}
	}

	@Override
	public Collection<Integer> decisionValues() {
		return decisionInfo.keySet();
	}

	@Override
	public Collection<Integer> numberValues() {
		return decisionInfo.values();
	}
}