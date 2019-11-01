package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap;

import java.util.Map;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.EquivalentClass;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.extension.decisionMap.DecisionInfoExtendedClassSet;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;

/**
 * An extension model of {@link EquivalentClass}. 
 * <p>{@link DecisionInfoExtendedClassSet} implemented, using a {@link Map} for decision values info: 
 * <p>{ key-decision value({@link Integer}), value-number-({@link Integer}) }.
 * 
 * @see {@link RoughEquivalentClassDecisionMapExtension}.
 * 
 * @author Benjamin_L
 */
public class EquivalentClassDecisionMapExtension<Sig extends Number> 
	extends EquivalentClass 
	implements DecisionInfoExtendedClassSet<UniverseInstance, Map<Integer, Integer>> 
{
	@Getter private Map<Integer,Integer> decisionInfo;
	@Setter @Getter private Sig singleSigMark;
	
	public EquivalentClassDecisionMapExtension(UniverseInstance universe) {
		super(universe);
		decisionInfo = new HashMap<>();
		updateDecisionInfo4AddingClassItem(universe);
	}
		
	/**
	 * Add a universe, and update dValueMap.
	 * 
	 * @param universe
	 * 		The universe instance to be added. / false if couldn't add the universe.
	 * @return true if added successfully
	 */
	@Override
	public void addClassItem(UniverseInstance universe) {
		super.addClassItem(universe);
		updateDecisionInfo4AddingClassItem(universe);
	}
	
	private void updateDecisionInfo4AddingClassItem(UniverseInstance universe) {
		Integer dValue = universe.getAttributeValue(0);
		Integer number = decisionInfo.get(dValue);
		if (number==null)	number=0;
		decisionInfo.put(dValue, number+1);
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