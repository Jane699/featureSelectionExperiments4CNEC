package featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.directDependencyCalculation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.model.algStrategyRepository.directDependencyCalculation.GridRecord;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.directDependencyCalculation.FeatureImportance4DirectDependencyCalculation;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.DefaultDependencyCalculation;

public class PositiveRegion4DDCHash
	extends DefaultDependencyCalculation 
	implements HashSearchStrategy, 
				FeatureImportance4DirectDependencyCalculation<Integer>
{
	private int dependency;
	@Override
	public Integer getResult() {
		return dependency;
	}
	
	@Override
	public FeatureImportance4DirectDependencyCalculation<Integer> calculate(
			Collection<UniverseInstance> instances,  IntegerIterator attribute, Object... args
	) {
		// Count the current calculation
		countCalculate(attribute.size());
		// Calculate
		dependency = instances==null || instances.isEmpty()? 0: dependency(instances, attribute);
		return this;
	}
	
	private int dependency(Collection<UniverseInstance> instances, IntegerIterator attributes) {
		Collection<GridRecord> grid = Support.updateGrid(instances, attributes)
											.values();
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
		return dep;
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
		public static Map<IntArrayKey, GridRecord> updateGrid(
				Collection<UniverseInstance> instances, IntegerIterator attributes
		) {
			Map<IntArrayKey, GridRecord> grid = new HashMap<>();
			Iterator<UniverseInstance> iterator = instances.iterator();
			// InsertGrid(X1)
			insertInGrid(grid, iterator.next(), attributes);
			// for i=2 to totalUniverseSize
			IntArrayKey key;
			UniverseInstance insPointer;
			while (iterator.hasNext()) {
				// uPointer = universes.get(i);
				insPointer = iterator.next();
				// ifAlreadyExistsInGrid(Xi)
				if (alreadyExistInGrid(grid, insPointer, attributes)) {
					// index = FindIndexInGrid(Xi)
					key = findIndexInGrid(grid, insPointer, attributes);
					// grid(index, count) +=1
					grid.get(key).addInstanceCount();
					// if decisionClassMatched(index, i)=false
					if (!decisionClassMatched(grid, key, insPointer))
						updateUniquenessStatus(grid, key);
				// else
				}else {
					// InsertInGrid(Xi)
					insertInGrid(grid, insPointer, attributes);
				}
			}
			return grid;
		}
		
		/**
		 * Insert a new record into <code>grid</code>.
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as <code>grid</code>.
		 * @param instance
		 * 		A {@link UniverseInstance} to be inserted.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 */
		public static void insertInGrid(Map<IntArrayKey, GridRecord> grid, UniverseInstance instance, IntegerIterator attributes) {
			// for j=1 to totalAttributeInX, i.e. C
			int[] attributeValues = new int[attributes.size()];
			attributes.reset();
			for (int j=0; j<attributeValues.length; j++) {
				//	Grid(NextRow,j) = Xi(j)
				attributeValues[j] = instance.getAttributeValue(attributes.next());
			}
			// Grid(NextRow, D)=Di; Grid(NextRow, count)=1; Grid(NextRow, cons)=0; 
			grid.put(new IntArrayKey(attributeValues), new GridRecord(null, instance.getAttributeValue(0)));
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
		public static boolean alreadyExistInGrid(Map<IntArrayKey, GridRecord> grid, UniverseInstance instance, 
												IntegerIterator attributes
		) {
			int[] attrValues = new int[attributes.size()];
			attributes.reset();
			for (int i=0; i<attrValues.length; i++)	attrValues[i] = instance.getAttributeValue(attributes.next());
			return grid.containsKey(new IntArrayKey(attrValues));
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
		public static IntArrayKey findIndexInGrid(
				Map<IntArrayKey, GridRecord> grid, UniverseInstance instance, IntegerIterator attributes
		) {
			int[] attrValue = new int[attributes.size()];
			attributes.reset();
			for (int a=0; a<attrValue.length; a++)	attrValue[a] = instance.getAttributeValue(attributes.next());
			return new IntArrayKey(attrValue);
		}
		
		/**
		 * Get if {@link UniverseInstance}'s decision value equals to the one in <code>grid</code>[{@link #index}].
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as grid.
		 * @param index
		 * 		The index of {@link GridRecord}.
		 * @param instance
		 * 		An {@link UniverseInstance}.
		 * @return true if the 2 values matches.
		 */
		public static boolean decisionClassMatched(Map<IntArrayKey, GridRecord> grid, IntArrayKey key, UniverseInstance instance) {
			return Integer.compare(grid.get(key).getDecisionClass(), instance.getAttributeValue(0))==0;
		}
		
		/**
		 * Update the unique status of <code>grid</code>[{@link #index}] from unique to non-unique.
		 * 
		 * @param grid
		 * 		A {@link List} of {@link GridRecord} as grid.
		 * @param index
		 * 		The index of {@link GridRecord}.
		 */
		public static void updateUniquenessStatus(Map<IntArrayKey, GridRecord> grid, IntArrayKey key) {
			grid.get(key).setClassStatus(false);
		}
	}

	@Override
	public <Item> boolean calculateAble(Item item) {
		return item instanceof Collection;
	}
}