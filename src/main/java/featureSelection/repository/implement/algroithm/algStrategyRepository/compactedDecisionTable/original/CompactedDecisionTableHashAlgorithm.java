package featureSelection.repository.implement.algroithm.algStrategyRepository.compactedDecisionTable.original;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import basic.model.IntArrayKey;
import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.MostSignificantAttributeResult;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original.CompactedDecisionTableCalculation;

/**
 * Algorithm repository of CompactedDecisionTable(AttributeReduction), which bases on the paper 
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0950705115002312">
 * "Compacted decision tables based attribute reduction"</a> by Wei Wei, Junhong Wang, Jiye Liang, 
 * Xin Mi, Chuangyin Dang.
 * <p>
 * Original version.
 * 
 * @author Benjamin_L
 */
public class CompactedDecisionTableHashAlgorithm {
	
	public static class Basic {
		/**
		 * Initiate a decision table.
		 * 
		 * @param instances
		 * 		{@link UniverseInstance} {@link Collection}.
		 * @return A key value {@link Map} in the format of {Decision Value : number}.
		 */
		public static Map<Integer, Integer> initDecisionTable(Collection<UniverseInstance> instances){
			// Initiate dValue=empty, in the structure of key-value({value: num})
			Map<Integer, Integer> decisionTable = new HashMap<>();
			// Loop over universes and count decision values.
			int dValue;
			UniverseInstance instance;
			Iterator<UniverseInstance> iterator = instances.iterator();
			while (iterator.hasNext()) {
				instance = iterator.next();
				dValue = instance.getAttributeValue(0);
				if (!decisionTable.containsKey(dValue))	decisionTable.put(dValue, 0);
			}
			return decisionTable;
		}

		/**
		 * Check the consistency of the given <code>decisionTable</code>.
		 * 
		 * @param decisionTable
		 * 		Decision table in {@link Map}, in the format of {dValue: number}.
		 * @return -1 if it is <code>in consistent</code>. / size of <code>positive region</code> if it is consistent.
		 */
		public static int checkConsistency(DecisionNumber decisionTable) {
			boolean nonZero = false;
			int dSize = 0;
			// Loop over decision numbers.
			int num;
			IntegerIterator dValueIterator = decisionTable.numberValues().reset();
			while (dValueIterator.hasNext()) {
				num = dValueIterator.next();
				if (num != 0) {
					// if the number of decision values are greater than 2: in consistent.
					if (nonZero)	return -1;
					// mark for the checking of in consistency in future rounds.
					else			nonZero = true;
					// save the decision number.
					dSize = num;
				}
			}
			return dSize;
		}
		
		/**
		 * Generate a compacted table of <code>instance</code> by the given <code>attributes</code>.
		 * 
		 * @param <DN>
		 * 		{@link DecisionNumber} implemented type as Decision Number Info.
		 * @param instances
		 * 		{@link UniverseInstance} {@link Collection}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return {@link UniverseBasedCompactedTable} with {@link UniverseBasedCompactedTableRecord} {@link Map}
		 * 			as compacted table, positive & negative region.
		 * @throws IllegalAccessException if exceptions occur when creating instance of {@link DecisionNumber}.
		 * @throws InstantiationException if exceptions occur when creating instance of {@link DecisionNumber}.
		 */
		public static <DN extends DecisionNumber> Collection<UniverseBasedCompactedTableRecord<DN>>
			universeToCompactedTable(Class<DN> decisionNumberInfo, Collection<UniverseInstance> instances, 
			int...attributes
		) throws InstantiationException, IllegalAccessException {
			// Initiate a HashMap as compacted table: H
			Map<IntArrayKey, UniverseBasedCompactedTableRecord<DN>> h = new HashMap<>();
			// Loop over instances
			int dValue;
			int[] value;
			IntArrayKey key;
			DN decisionNumbers;
			UniverseBasedCompactedTableRecord<DN> tableRecord;
			for (UniverseInstance ins : instances) {
				value = new int[attributes.length];
				for (int i=0; i<attributes.length; i++)	value[i] = ins.getAttributeValue(attributes[i]);
				key = new IntArrayKey(value);

				tableRecord = h.get(key);
				dValue = ins.getAttributeValue(0);
				if (tableRecord==null) {
					// if H doesn't contain key.
					//	create h, h.v = x
					//	dvalue[key].num++
					decisionNumbers = decisionNumberInfo.newInstance();
					decisionNumbers.setDecisionNumber(dValue, 1);
					h.put(key, tableRecord = new UniverseBasedCompactedTableRecord<>(ins, decisionNumbers));
				}else {
					// if H contains key.
					//	h.dValue[key].num++
					tableRecord.getDecisionNumbers()
								.setDecisionNumber(dValue, 
													tableRecord.getDecisionNumbers()
																.getNumberOfDecision(dValue)
													+1
								);
				}
			}
			return h.values();
		}
		
		/**
		 * Get the positive region and negative region of global compacted table.
		 * 
		 * @param <DN>
		 * 		{@link DecisionNumber} implemented type as Decision Number Info.
		 * @param records
		 * 		{@link UniverseBasedCompactedTable} as global compacted table.
		 * @return An {@link UniverseInstance} {@link Set} <code>array</code> with <code>element[0]</code>:
		 * 		Positive region and <code>element[1]</code> Negative region.
		 */
		@SuppressWarnings("unchecked")
		public static <DN extends DecisionNumber> Set<UniverseBasedCompactedTableRecord<DN>>[] 
			globalPositiveNNegativeRegion(Collection<UniverseBasedCompactedTableRecord<DN>> records
		){
			Set<UniverseBasedCompactedTableRecord<DN>> pos = new HashSet<>(records.size()), 
														neg = new HashSet<>(records.size());
			for (UniverseBasedCompactedTableRecord<DN> record : records ) {
				if (checkConsistency(record.getDecisionNumbers())>= 0) {
					// positive region
					pos.add(record);
				}else {
					// negative region
					neg.add(record);
				}
			}
			return new Set[] {pos, neg};
		}
		
		/**
		 * Merge two Decision Compacted Tables in {@link Map} : { dValue: number }.
		 * 
		 * @param <DN>
		 * 		{@link DecisionNumber} implemented type as Decision Number Info.
		 * @param mainDecisionTable
		 * 		The decision compacted table to absorb and contain both values from 2 records.
		 * @param toBeMergedDecisionTable
		 * 		A decision compacted table provides value to be merged and absorbed.
		 */
		public static <DN extends DecisionNumber> void mergeCompactedTableRecord(
				EquivalentClassCompactedTableRecord<DN> mainDecisionInfo, 
				UniverseBasedCompactedTableRecord<DN> toBeMergedRecord
		){
			Integer number, key;
			IntegerIterator toBeMergedValues = toBeMergedRecord.getDecisionNumbers()
																.decisionValues()
																.reset();
			while (toBeMergedValues.hasNext()) {
				key = toBeMergedValues.next();
				number = mainDecisionInfo.getDecisionNumbers()
										.getNumberOfDecision(key);
				number += toBeMergedRecord.getDecisionNumbers()
										.getNumberOfDecision(key);
				mainDecisionInfo.getDecisionNumbers()
								.setDecisionNumber(key, number);
			}
			// merge table records
			mainDecisionInfo.getEquivalentRecords()
							.add(toBeMergedRecord);
		}

		/**
		 * Get equivalent class compacted table by the given <code>attributes</code> and <code>CompactedTable</code>.
		 * 
		 * @param <DN>
		 * 		{@link DecisionNumber} implemented type as Decision Number Info.
		 * @param universeCompactedTable
		 * 		An {@link Map} required by {@link UniverseBasedCompactedTable#getRecords()}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @return A {@link Map} with attribute value {@link IntArrayKey} keys and {@link UniverseBasedCompactedTableRecord} values.
		 */
		@SuppressWarnings("unchecked")
		public static <DN extends DecisionNumber> Collection<EquivalentClassCompactedTableRecord<DN>>
			equivalentClassOfCompactedTable(
					Collection<UniverseBasedCompactedTableRecord<DN>> compactedTableRecords, 
					IntegerIterator attributes
		){
			if (attributes==null || attributes.size()==0) {
				UniverseBasedCompactedTableRecord<DN> compactedTableRecord;
				Set<UniverseBasedCompactedTableRecord<DN>> compactedTableRecordSet = new HashSet<>();
				Iterator<UniverseBasedCompactedTableRecord<DN>> recordsIterator = compactedTableRecords.iterator();
				compactedTableRecord = recordsIterator.next();
				compactedTableRecordSet.add(compactedTableRecord);
				EquivalentClassCompactedTableRecord<DN> equRecord = 
						new EquivalentClassCompactedTableRecord<>(
								(DN) compactedTableRecord.getDecisionNumbers().clone(), 
								compactedTableRecordSet
							);
				while (recordsIterator.hasNext())	mergeCompactedTableRecord(equRecord, recordsIterator.next());
				
				Collection<EquivalentClassCompactedTableRecord<DN>> result = new HashSet<>();
				result.add(equRecord);
				return result;
			}else {
				// Initiate a Hash as equivalent class table: H
				Map<IntArrayKey, EquivalentClassCompactedTableRecord<DN>> equClassTable = new HashMap<>();
				// Loop over compactedTableRecords
				int[] value;
				IntArrayKey key;
				EquivalentClassCompactedTableRecord<DN> decisionInfo;
				Collection<UniverseBasedCompactedTableRecord<DN>> equivalentRecords;
				for (UniverseBasedCompactedTableRecord<DN> record : compactedTableRecords) {
					// key = P(x)
					value = new int[attributes.size()];
					attributes.reset();
					for (int i=0; i<value.length; i++) {
						value[i] = record.getUniverseRepresentitive()
										.getAttributeValue(attributes.next());
					}
					key = new IntArrayKey(value);
					
					decisionInfo = equClassTable.get(key);
					if (decisionInfo==null) {
						// if H doesn't contain key
						//	create h, key(h)=P(x)
						//	create dvalue for h with the same structure of dvalue.
						equivalentRecords = new HashSet<>();
						equivalentRecords.add(record);
						equClassTable.put(key, 
											new EquivalentClassCompactedTableRecord<>(
												(DN) record.getDecisionNumbers().clone(), 
												equivalentRecords
											)
										);
					}else {
						//	else
						//		merge(x, h)
						mergeCompactedTableRecord(decisionInfo, record);
					}
				}
				return equClassTable.values();
			}
		}
	}

	/**
	 * Get the most significant attribute in {@link UniverseInstance} attributes beside red.
	 * 
	 * @param <Sig>
	 * 		{@link Number} implemented type as the value of Significance.
	 * @param <DN>
	 * 		{@link DecisionNumber} implemented type as Decision Number Info.
	 * @param tableRecords
	 * 		An {@link UniverseBasedCompactedTableRecord} {@link Collection}.
	 * @param instanceSize
	 * 		The number of {@link UniverseInstance} in the original decision table.
	 * @param calculation
	 * 		Implemented {@link CompactedDecisionTableCalculation}.
	 * @param deviation
	 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
	 * 		difference between two sig is less than the given deviation value.
	 * @param red
	 * 		Current reduction.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}.
	 * @return An int value as the most significant attribute.
	 * @throws InstantiationException if exceptions occur when constructing {@link Calculation}.
	 * @throws IllegalAccessException if exceptions occur when constructing {@link Calculation}.
	 * @throws IllegalArgumentException if exceptions occur when constructing {@link Calculation}.
	 * @throws InvocationTargetException if exceptions occur when constructing {@link Calculation}.
	 * @throws NoSuchMethodException if exceptions occur when constructing {@link Calculation}.
	 * @throws SecurityException if exceptions occur when constructing {@link Calculation}.
	 * @throws CloneNotSupportedException if exceptions occur when cloning {@link UniverseBasedCompactedTable}.
	 */
	public static <Sig extends Number, DN extends DecisionNumber> MostSignificantAttributeResult<Sig, DN>
		mostSignificantAttribute(
				Collection<EquivalentClassCompactedTableRecord<DN>> tableRecords,
				int instanceSize, CompactedDecisionTableCalculation<Sig> calculation, Sig deviation, 
				Collection<Integer> red, IntegerIterator attributes
	) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, CloneNotSupportedException 
	{
		// Initiate
		Sig maxSig = null, sig;
		int maxSigAttr = -1;
		// Loop over attributes in C-red.(i.e. attributes that are not in reduct)
		int attr;
		IntegerCollectionIterator examAttrIterator;
		Collection<EquivalentClassCompactedTableRecord<DN>> equClassTable, sigEquClassTable = null;
		while (attributes.hasNext()) {
			attr = attributes.next();
			if (red.contains(attr))	continue;
			// Initiate the compacted table of U/(red U {a}).
			red.add(attr);
			equClassTable = new HashSet<>();
			examAttrIterator = new IntegerCollectionIterator(red);
			for (EquivalentClassCompactedTableRecord<DN> equRecords: tableRecords) {
				equClassTable.addAll(
					Basic.equivalentClassOfCompactedTable(
							equRecords.getEquivalentRecords(), 
							examAttrIterator.reset()
					)
				);
			}
			red.remove(attr);
			// Call significance calculation method to calculate Sig(red U {a}) to get a.outerSig
			sig = calculation.calculate(equClassTable, examAttrIterator.size(), instanceSize)
							.getResult();
			// if a.outerSig > sig: update the maxSig, maxSigAttr, sigEquClassTable.
			if (maxSig==null || calculation.value1IsBetter(sig, maxSig, deviation)) {
				maxSig = sig;
				maxSigAttr = attr;
				sigEquClassTable = equClassTable;
			}
		}
		return new MostSignificantAttributeResult<Sig, DN>(maxSig, maxSigAttr, sigEquClassTable);
	}
}