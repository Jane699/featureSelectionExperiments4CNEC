package featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.directDependencyCalculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;
import featureSelection.repository.implement.model.algStrategyRepository.directDependencyCalculation.GridRecord;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.directDependencyCalculation.FeatureImportance4DirectDependencyCalculation;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.DefaultDependencyCalculation;

public class PositiveRegion4DDCSequential
	extends DefaultDependencyCalculation 
	implements SequentialSearchStrategy, 
				FeatureImportance4DirectDependencyCalculation<Integer>
{
	private int dependency;
	@Override
	public Integer getResult() {
		return dependency;
	}
	
	@Override
	public FeatureImportance4DirectDependencyCalculation<Integer> calculate(
			Collection<UniverseInstance> universes,  IntegerIterator attribute, Object... args
	) {
		// Count the current calculation
		countCalculate(attribute.size());
		// Calculate
		dependency = universes==null || universes.isEmpty()? 0: dependency(universes, attribute);
		return this;
	}
	
	private int dependency(Collection<UniverseInstance> universes, IntegerIterator attributes) {
		Collection<GridRecord> grid = Support.updateGrid(universes, attributes);
		// 1 dep=0
		int dep = 0;
		// 2 for i=1 to totalRecoredsInGrid
		for (GridRecord gridRecord : grid) {
			// 3 if grid(i, classStatus)=0
			if (gridRecord.unique()) {
				// 4 dep = dep + grid(i,count)
				dep += gridRecord.getInstanceCount();
			}
		}
		// 5 return dep / TotalRecoreds
		return dep;
	}
	
	public static class Support {
		/**
		 * Generate a {@link List} of {@link GridRecord} based on the given {@link UniverseInstance} 
		 * {@link List}
		 * 
		 * @param universes
		 * 		A {@link List} of {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return A {@link List} of {@link GridRecord} 
		 */
		public static Collection<GridRecord> updateGrid(Collection<UniverseInstance> universes, IntegerIterator attributes) {
			List<GridRecord> grid = new ArrayList<>();
			// InsertGrid(X1)
			Iterator<UniverseInstance> universeIterator = universes.iterator();
			insertInGrid(grid, universeIterator.next(), attributes);
			// for i=2 to totalUniverseSize
			int index;
			UniverseInstance uPointer;
			while (universeIterator.hasNext()) {
				uPointer = universeIterator.next();
				// ifAlreadyExistsInGrid(Xi)
				if (alreadyExistInGrid(grid, uPointer, attributes)) {
					// index = FindIndexInGrid(Xi)
					index = findIndexInGrid(grid, uPointer, attributes);
					// grid(index, count) +=1
					grid.get(index)
						.addInstanceCount();
					// if decisionClassMatched(index, i)=false
					if (!decisionClassMatched(grid, index, uPointer))
						updateUniquenessStatus(grid, index);
				// else
				}else {
					// InsertInGrid(Xi)
					insertInGrid(grid, uPointer, attributes);
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
		 * @param universe
		 * 		A {@link List} of {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return <code>true</code> if the given {@link UniverseInstance} exists by given <code>attributes</code>.
		 */
		public static boolean alreadyExistInGrid(Collection<GridRecord> grid, UniverseInstance universe, IntegerIterator attributes) {
			// for i=1 to totalRecordsInGrid
			GridLoop:
			for (GridRecord gridRecord : grid) {
				// for j=1 to totalAttributeInX
				attributes.reset();
				for (int j=0; j<attributes.size(); j++) {
					// if grid(i, j) <> X[j], i.e. grid(i, j) != universe.attributes.value[j]
					if (Integer.compare(gridRecord.getConditionalAttributes().key()[j], 
										universe.getAttributeValue(attributes.next())) != 0
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
		 * @param universe
		 * 		An {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return An {@link int} value as index in grid. (Starts from 0, -1 as no matched)
		 */
		public static int findIndexInGrid(Collection<GridRecord> grid, UniverseInstance universe, IntegerIterator attributes) {
			int[] attrValue;
			// for i=1 to totalRecordsInGrid. 
			int i=0;
			GridLoop:
			for (GridRecord gridRecord: grid) {
				// init recordMatched. 
				// for j=1 to totalAttributeInX
				attrValue = gridRecord.getConditionalAttributes().key();
				attributes.reset();
				for (int j=0; j<attrValue.length; j++) {
					// if grid(i, j) != Xi[j]
					if (Integer.compare(attrValue[j], universe.getAttributeValue(attributes.next()))!=0) {
						// recordMatched = false
						i++;
						continue GridLoop;
					}
				}
				// 7~8 if recordMatched is true. Return i.
				return i;
			}
			// 12 return true.
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
		 * @param universe
		 * 		An {@link UniverseInstance}.
		 * @return true if the 2 values matches.
		 */
		public static boolean decisionClassMatched(List<GridRecord> grid, int index, UniverseInstance universe) {
			return Integer.compare(grid.get(index).getDecisionClass(), universe.getAttributeValue(0))==0;
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
		 * @param universe
		 * 		A {@link UniverseInstance} to be inserted.
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as <code>grid</code>.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 */
		public static void insertInGrid(Collection<GridRecord> grid, UniverseInstance universe, IntegerIterator attributes) {
			// for j=1 to totalAttributeInX, i.e. C
			int[] attributeValues = new int[attributes.size()];
			attributes.reset();
			for (int j=0; j<attributes.size(); j++) {
				//	Grid(NextRow,j) = Xi(j)
				attributeValues[j] = universe.getAttributeValue(attributes.next());
			}
			// Grid(NextRow, D)=Di; Grid(NextRow, count)=1; Grid(NextRow, cons)=0; 
			grid.add(new GridRecord(attributeValues, universe.getAttributeValue(0)));
		}
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}
}