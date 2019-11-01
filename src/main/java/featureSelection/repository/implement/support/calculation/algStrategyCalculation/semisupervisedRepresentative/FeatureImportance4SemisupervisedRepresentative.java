package featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative;

import java.util.Collection;
import java.util.Map;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.frame.support.calculation.FeatureImportance;

/**
 * An interface for Feature Importance calculation for Semi-supervised Representative Feature Selection.
 * <p>
 * Implementations should base on the original paper 
 * <a href="https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242">
 * "An efficient semi-supervised representatives feature selection algorithm based on information theory"</a> 
 * by Yintong Wang, Jiandong Wang, Hao Liao, Haiyan Chen.
 * 
 * @see {@link SemisupervisedRepresentativeStrategy}
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
public interface FeatureImportance4SemisupervisedRepresentative<V> 
	extends FeatureImportance<V>, 
			SemisupervisedRepresentativeStrategy
{
	/**
	 * Calculate Info. entropy of Equivalent Classes(<code>equClasses</code>).
	 * 
	 * @param equClasses
	 * 		A {@link Map} contains Equivalent Classes whose keys are distinct equivalent keys and values
	 * 		are {@link UniverseInstance} {@link Collection}s as equivalent classes.
	 * @param args
	 * 		Extra arguments required in calculation.
	 * @return <code>this</code> instance.
	 */
	public FeatureImportance4SemisupervisedRepresentative<V> calculate(
			Collection<Collection<UniverseInstance>> equClasses, Object...args
	);
}