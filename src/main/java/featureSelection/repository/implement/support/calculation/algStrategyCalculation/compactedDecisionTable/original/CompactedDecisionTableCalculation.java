package featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original;

import java.util.Collection;

import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.CompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;

/**
 * An interface for Feature Importance calculation using Rough Set Theory dependency(Positive region based) 
 * calculation based on Compacted Decision table.
 * <p>
 * Implementations should base on the original paper
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0950705115002312">
 * "Compacted decision tables based attribute reduction"</a> by Wei Wei, Junhong Wang, Jiye Liang, 
 * Xin Mi, Chuangyin Dang.
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of feature importance.
 */
@RoughSet
public interface CompactedDecisionTableCalculation<V> extends FeatureImportance<V> {
	public CompactedDecisionTableCalculation<V> calculate(
			Collection<? extends CompactedTableRecord<? extends DecisionNumber>> records, 
			int attributeLength, Object...args
	);
}