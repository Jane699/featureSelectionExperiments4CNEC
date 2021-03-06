package featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.heuristicDependencyCalculation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;
import featureSelection.repository.implement.model.algStrategyRepository.heuristicDependencyCalculation.GridRecord;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.heuristicDependencyCalculation.FeatureImportance4HeuristicDependencyCalculation;
import featureSelection.repository.implement.support.calculation.dependency.DefaultDependencyCalculation;

public class DependencyCalculation4HDCSequential
	extends DefaultDependencyCalculation 
	implements SequentialSearchStrategy,
				FeatureImportance4HeuristicDependencyCalculation<Double>
{
	private double dependency;
	@Override
	public Double getResult() {
		return dependency;
	}
	
	@Override
	public FeatureImportance4HeuristicDependencyCalculation<Double> calculate(
			Collection<UniverseInstance> instances, Collection<Integer> desisionValues, 
			IntegerIterator attribute, Object... args
	) {
		// Count the current calculation
		countCalculate(attribute.size());
		// Calculate
		dependency = instances==null || instances.isEmpty()? 0: dependency(instances, desisionValues, attribute);
		return this;
	}
	
	private double dependency(Collection<UniverseInstance> instances, Collection<Integer> desisionValues,
								IntegerIterator attributes
	) {
		// TotalCRecords = 0
		int totalCRecords = 0;
		// Loop over DecisionClasses
		for (int dValue : desisionValues)
			// TotalCRecords += calculateConsistentRecords(DecisionClassValue)
			totalCRecords += calculateConsistentRecords(instances, attributes, dValue);
		return totalCRecords / (double) instances.size();
	}
	
	/**
	 * Calculate the consistent records.
	 * 
	 * @param instances
	 * 		A {@link Collection} of {@link UniverseInstance}.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}. (Starts from 1)
	 * @param dValue
	 * 		Decision value the {@link UniverseInstance}.
	 * @return The number of consistent records.
	 */
	public static int calculateConsistentRecords(
			Collection<UniverseInstance> instances, IntegerIterator attributes, int dValue
	) {
		// RecordsCount = 0
		int recordsCount = 0;
		// Initiate a grid
		List<GridRecord> grid = new LinkedList<>();
		// Loop over xj in U
		int[] attrValues;
		GridRecord gridRecord;
		for (UniverseInstance xj : instances) {
			// if DecisionClass(xj)==dValue
			if (Integer.compare(xj.getAttributeValue(0), dValue)==0) {
				// insertInGrid.
				attrValues = new int[attributes.size()];
				attributes.reset();
				for (int i=0; i<attributes.size(); i++)	attrValues[i] = xj.getAttributeValue(attributes.next());
				gridRecord = new GridRecord(attrValues, recordsCount);
				grid.add(gridRecord);
			}
		}
		// Loop over xj in U
		for (UniverseInstance xj : instances) {
			// if D(xi)!=DValue
			if (Integer.compare(xj.getAttributeValue(0), dValue) !=0) {
				// recordExistsIngrid
				GridLoop:
				for (GridRecord each : grid) {
					attrValues = each.getConditionalAttributes();
					attributes.reset();
					for (int i=0; i<attributes.size(); i++) {
						if (Integer.compare(attrValues[i], xj.getAttributeValue(attributes.next()))!=0)
							continue GridLoop;
					}
					each.setClassStatus(false);
				}
			}
		}
		// Loop over hi in H
		for (GridRecord each : grid) {
			// if hi.cnst = true, RecoredsCount++
			if (each.cnst())	recordsCount++;
		}
		return recordsCount;
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}

	@Override
	public Double difference(Double v1, Double v2) {
		return (v1==null?0:v1) - (v2==null?0:v2);
	}
}