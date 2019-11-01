package featureSelection.repository.frame.support.calculation.featureImportance;

import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.support.calculation.FeatureImportance;

/**
 * An interface for <strong>Dependency</strong> calculation.
 * 
 * @author Benjamin_L
 *
 * @param <Sig>
 * 		Type of Feature Importance.
 */
@RoughSet
public interface DependencyCalculation 
	extends FeatureImportance<Double>
{
	public static final String CALCULATION_NAME = "Dep.";
}
