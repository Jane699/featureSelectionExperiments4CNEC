package featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.directDependencyCalculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;
import featureSelection.repository.implement.model.algStrategyRepository.directDependencyCalculation.GridRecord;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.directDependencyCalculation.FeatureImportance4DirectDependencyCalculation;
import featureSelection.repository.implement.support.calculation.dependency.DefaultDependencyCalculation;

public class DependencyCalculation4DDCSequential
	extends DefaultDependencyCalculation 
	implements SequentialSearchStrategy, 
				FeatureImportance4DirectDependencyCalculation<Double>
{
	private double dependency;
	@Override
	public Double getResult() {
		return dependency;
	}
	
	@Override
	public FeatureImportance4DirectDependencyCalculation<Double> calculate(
			Collection<UniverseInstance> instanes,  IntegerIterator attribute, Object... args
	) {
		// Count the current calculation
		countCalculate(attribute.size());
		// Calculate
		dependency = instanes==null || instanes.isEmpty()? 0: dependency(instanes, attribute);
		return this;
	}
	
	private double dependency(Collection<UniverseInstance> instances, IntegerIterator attributes) {
		Collection<GridRecord> grid = Support.updateGrid(instances, attributes);
		// dep=0
		int dep = 0;
		// for i=1 to totalRecoredsInGrid
		for (GridRecord gridRecord : grid) {
			// if grid(i, classStatus)=0
			if (gridRecord.unique()) {
				// dep = dep + grid(i,count)
				dep += gridRecord.getInstanceCount();
			}
		}
		// return dep / TotalRecoreds
		return (double) dep / instances.size();
	}
	
	public static class Support {
		/**
		 * Generate a {@link List} of {@link GridRecord} based on the given {@link UniverseInstance} 
		 * {@link List}
		 * 
		 * @param instances
		 * 		A {@link List} of {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return A {@link List} of {@link GridRecord} 
		 */
		public static Collection<GridRecord> updateGrid(Collection<UniverseInstance> instances, IntegerIterator attributes) {
			List<GridRecord> grid = new ArrayList<>();
			// InsertGrid(X1)
			Iterator<UniverseInstance> instanceIterator = instances.iterator();
			insertInGrid(grid, instanceIterator.next(), attributes);
			// for i=2 to totalUniverseSize
			int index;
			UniverseInstance insPointer;
			while (instanceIterator.hasNext()) {
				insPointer = instanceIterator.next();
				// ifAlreadyExistsInGrid(Xi)
				if (alreadyExistInGrid(grid, insPointer, attributes)) {
					// index = FindIndexInGrid(Xi)
					index = findIndexInGrid(grid, insPointer, attributes);
					// grid(index, count) +=1
					grid.get(index)
						.addInstanceCount();
					// if decisionClassMatched(index, i)=false
					if (!decisionClassMatched(grid, index, insPointer))
						updateUniquenessStatus(grid, index);
				// else
				}else {
					// InsertInGrid(Xi)
					insertInGrid(grid, insPointer, attributes);
				}
			}
			return grid;
		}
		
		/**
		 * Get if the given {@link UniverseInstance} already exists in the {@link GridRecord} {@link List}, 
		 * i.e. <code>grid</code>.
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as <code>grid</code>.
		 * @param instance
		 * 		A {@link List} of {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return <code>true</code> if the given {@link UniverseInstance} exists by given <code>attributes</code>.
		 */
		public static boolean alreadyExistInGrid(Collection<GridRecord> grid, UniverseInstance instance, IntegerIterator attributes) {
			// for i=1 to totalRecordsInGrid
			GridLoop:
			for (GridRecord gridRecord : grid) {
				// for j=1 to totalAttributeInX
				attributes.reset();
				for (int j=0; j<attributes.size(); j++) {
					// if grid(i, j) <> X[j], i.e. grid(i, j) != instance.attributes.value[j]
					if (Integer.compare(gridRecord.getConditionalAttributes().key()[j], 
										instance.getAttributeValue(attributes.next())) != 0
					) {
						// continue to check next grid record.
						continue GridLoop;
					}
				}
				return true;
			}
			return false;
		}
		
		/**
		 * Find the {@link UniverseInstance} index that attribute values matched in <code>grid</code>.
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord}.
		 * @param instance
		 * 		An {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return An {@link int} value as index in grid. (Starts from 0, -1 as no matched)
		 */
		public static int findIndexInGrid(Collection<GridRecord> grid, UniverseInstance instance, IntegerIterator attributes) {
			int[] attrValue;
			// for i=1 to totalRecordsInGrid. 
			int i=0;
			GridLoop:
			for (GridRecord gridRecord: grid) {
				// initiate recordMatched. 
				// for j=1 to totalAttributeInX
				attrValue = gridRecord.getConditionalAttributes().key();
				attributes.reset();
				for (int j=0; j<attrValue.length; j++) {
					// if grid(i, j) != Xi[j]
					if (Integer.compare(attrValue[j], instance.getAttributeValue(attributes.next()))!=0) {
						// recordMatched = false
						i++;
						continue GridLoop;
					}
				}
				// if recordMatched is true. Return i.
				return i;
			}
			return -1;
		}

		/**
		 * Get if {@link UniverseInstance}'s decision value equals to the one in <code>grid</code>
		 * [{@link #index}].
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as grid.
		 * @param index
		 * 		The index of {@link GridRecord}.
		 * @param instance
		 * 		An {@link UniverseInstance}.
		 * @return true if the 2 values matches.
		 */
		public static boolean decisionClassMatched(List<GridRecord> grid, int index, UniverseInstance instance) {
			return Integer.compare(grid.get(index).getDecisionClass(), instance.getAttributeValue(0))==0;
		}

		/**
		 * Update the unique status of <code>grid</code>[{@link #index}] from unique to non-unique.
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as grid.
		 * @param index
		 * 		The index of {@link GridRecord}.
		 */
		public static void updateUniquenessStatus(List<GridRecord> grid, int index) {
			grid.get(index).setClassStatus(false);
		}
		
		/**
		 * Insert a new record into <code>grid</code>.
		 * 
		 * @param instance
		 * 		A {@link UniverseInstance} to be inserted.
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as <code>grid</code>.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 */
		public static void insertInGrid(Collection<GridRecord> grid, UniverseInstance instance, IntegerIterator attributes) {
			// for j=1 to totalAttributeInX, i.e. C
			int[] attributeValues = new int[attributes.size()];
			attributes.reset();
			for (int j=0; j<attributes.size(); j++) {
				//	Grid(NextRow,j) = Xi(j)
				attributeValues[j] = instance.getAttributeValue(attributes.next());
			}
			// Grid(NextRow, D)=Di; Grid(NextRow, count)=1; Grid(NextRow, cons)=0; 
			grid.add(new GridRecord(attributeValues, instance.getAttributeValue(0)));
		}
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