package featureSelection.repository.implement.algroithm.algStrategyRepository.roughEquivalentClassBased;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import basic.model.IntArrayKey;
import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.model.interf.Calculation;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.ClassSetType;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.EquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.ClassSet;
import featureSelection.repository.implement.model.algStrategyRepository.rec.extension.incrementalDecision.MostSignificantAttributeResult;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.StreamlineInput4RECIncrementalDecisionExtension;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.StreamlineResult4RECIncrementalDecisionExtension;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt;

/**
 * Algorithm repository of RoughEquivalentClass based extension algorithms(REC) using Rough Equivalent 
 * Class, including:
 * <li><strong>ID-REC</strong>:
 * 		<p>Incremental Decision Rough Equivalent Class based. Using {@link EquivalentClassDecisionMapExtension}
 * 			and {@link RoughEquivalentClassDecisionMapExtension} for conveniences in calculations of 
 * 			entropies and to accelerate.
 * </li>
 * <p><strong>Notice</strong>: Later at the designing of Rough Equivalent Class based extension algorithms, 
 * 		the name of Rough Equivalent Class was changed into <strong>Nested Equivalent Class</strong> because
 * 		of academic and other reasons. However, implemented algorithms here are not affected in executions.
 * 
 * @author Benjamin_L
 */
@RoughSet
public class RoughEquivalentClassBasedExtensionAlgorithm 
	extends RoughEquivalentClassBasedAlgorithm 
{
	/**
	 * ID-REC for short.
	 * <p>
	 * (CNEC)
	 * 
	 * @author Benjamin_L
	 */
	public static class IncrementalDecision {
		/**
		 * Basic algorithms for ID-REC.
		 * 
		 * @see {@link RoughEquivalentClassBasedAlgorithm.Basic}
		 * 
		 * @author Benjamin_L
		 */
		public static class Basic{
			/**
			 * Get an {@link EquivalentClassDecisionMapExtension} {@link Collection} generated based on the 
			 * given {@link UniverseInstance} {@link Collection}.
			 * 
			 * @param instances
			 * 		An {@link UniverseInstance} {@link Collection}.
			 * @param attributes
			 * 		Attributes of {@link UniverseInstance} .
			 * @return {@link EquivalentClassDecisionMapExtension} {@link Collection}.
			 */
			public static <Sig extends Number> Collection<EquivalentClassDecisionMapExtension<Sig>> 
				equivalentClass(Collection<UniverseInstance> instances, IntegerIterator attributes
			) {
				// Initiate a Hash for Equivalent Classes.
				Map<IntArrayKey, EquivalentClassDecisionMapExtension<Sig>> equClasses = new HashMap<>();
				// Loop over universe instances.
				IntArrayKey key;
				int[] valueArray;
				EquivalentClassDecisionMapExtension<Sig> equClass;
				for (UniverseInstance ins : instances) {
					// key = C(x)
					attributes.reset();
					valueArray = new int[attributes.size()];
					for (int i=0; i<valueArray.length; i++)	valueArray[i] = ins.getAttributeValue(attributes.next());
					key = new IntArrayKey(valueArray);
					
					equClass = equClasses.get(key);
					if (equClass==null) {
						// if H doesn't contain key.
						//	create h, h.count=1, h.cons=true, h.dec=D(x)
						equClasses.put(key, equClass = new EquivalentClassDecisionMapExtension<>(ins));
						//	create a Hash dv
						//	create a sub item d, d.key=h.dec, d.value=1
						//	Add d into dv, h.dv = dv
					}else {
						// else H contains key
						//	h.count++
						//	if h.cons==true and D(x)!=h.dec
						//		h.cons=false
						//	d.key=h.dec
						//	if h.dv doesn't contain key
						//		create a new sub item d(h.dec, 1)
						//		add d into h.dv
						//	else h.dv contains key
						//		get the number of d: d.value
						//		d.value++
						equClass.addClassItem(ins);
						if (equClass.sortable() && 
							Integer.compare(equClass.getDecisionValue(), ins.getAttributeValue(0))!=0
						) {
							equClass.setUnsortable();
						}
					}
				}
				return equClasses.values();
			}
		
			/**
			 * Generate {@link RoughEquivalentClassDecisionMapExtension} {@link Collection} based on the 
			 * given {@link EquivalentClassDecisionMapExtension} {@link Collection} and the given attributes.
			 * 
			 * @param equClasses
			 * 		{@link EquivalentClassDecisionMapExtension} {@link Collection}.
			 * @param attributes
			 * 		Attributes of {@link UniverseInstance}.
			 * @return {@link RoughEquivalentClassDecisionMapExtension} {@link Collection}.
			 */
			public static <Sig extends Number> Collection<RoughEquivalentClassDecisionMapExtension<Sig>> 
				roughEquivalentClass(
					Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, 
					IntegerIterator attributes
			){
				// Create a Hash for E as rough equivalent classes: HE
				Map<IntArrayKey, RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = new HashMap<>();
				// Loop over e in E
				int[] keyArray;
				IntArrayKey key;
				RoughEquivalentClassDecisionMapExtension<Sig> roughClass;
				for (EquivalentClassDecisionMapExtension<Sig> equClass: equClasses) {
					// key = P(e)
					keyArray = new int[attributes.size()];
					attributes.reset();
					for (int i=0; i<keyArray.length; i++)
						keyArray[i] = equClass.getAttributeValueAt(attributes.next()-1);
					key = new IntArrayKey(keyArray);
					roughClass = roughClasses.get(key);
					if (roughClass==null) {
						// if (HE doesn't contain key)
						//	create h
						roughClasses.put(key, roughClass=new RoughEquivalentClassDecisionMapExtension<>());
						//	h.addMember(e)
						//	h.dv=e.dv
						//	if (e.cons=true)
						//		h.cons=1
						//	else
						//		h.cons=-1
					}
					// if HE contains key
					//	if h.cons=1
					//		h.addMember(e)
					//		h.dv=combine(h.dv, e.dv)
					//		if h.cons==1 && e.cons==true && h.dec=e.dec
					//			continue;
					//		else if h.cons=-1 && e.cons==false
					//			continue;
					//		else 
					//			h.cons=0
					//			h.dec='/'
					roughClass.addClassItem(equClass);
				}
				return roughClasses.values();
			}
		
			/**
			 * Further partition of {@link RoughEquivalentClassDecisionMapExtension} {@link Collection} based
			 * on the given {@link RoughEquivalentClassDecisionMapExtension} {@link Collection} and the given 
			 * attributes.
			 * 
			 * @param roughClasses
			 * 		{@link RoughEquivalentClassDecisionMapExtension} {@link Collection}.
			 * @param attributes
			 * 		Attributes of {@link UniverseInstance}.
			 * @return further partition {@link RoughEquivalentClassDecisionMapExtension} {@link Collection}.
			 */
			public static <Sig extends Number> Set<RoughEquivalentClassDecisionMapExtension<Sig>> 
				incrementalPartitionRoughEquivalentClass(
					Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses, 
					IntegerIterator attributes
			){
				Set<RoughEquivalentClassDecisionMapExtension<Sig>> incrementalRoughClasses = new HashSet<>();
				for (RoughEquivalentClassDecisionMapExtension<Sig> roughClass: roughClasses) {
					if (!ClassSetType.POSITIVE.equals(roughClass.getType())) {
						incrementalRoughClasses.addAll(
							roughEquivalentClass(roughClass.getItemSet(), attributes)
						);
					}else {
						incrementalRoughClasses.add(roughClass);
					}
				}
				return incrementalRoughClasses;
			}
		
			/**
			 * Calculate the negative significance of the given {@link ClassSet} {@link Collection}, using
			 * {@link Calculation}.
			 * 
			 * @param <Sig>
			 * 		Significance Type.
			 * @param equClasses
			 * 		Implemented {@link ClassSet} instance.
			 * @param instanceSize
			 * 		The number of {@link UniverseInstance}.
			 * @param attributeLength
			 * 		The length of conditional attributes.
			 * @param calculation
			 * 		Implemented {@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} 
			 * 		instance.
			 * @return {@link Sig} value as significance. / <code>null</code> if significance is 0.
			 * @throws Exception if exceptions occur when {@link Calculation} calling plus().
			 */
			public static <Sig extends Number> Sig globalSignificance(
					Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, 
					int instanceSize, int attributeLength, 
					RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation
			) throws Exception {
				// sig = 0
				Sig sigSum = null;
				// Loop over e in EC_Table
				for (EquivalentClassDecisionMapExtension<Sig> equClass: equClasses) {
					calculation.calculate(equClass, attributeLength, instanceSize);
					if (ClassSetType.NEGATIVE.equals(equClass.getType()))
						equClass.setSingleSigMark(calculation.getResult());
					sigSum = calculation.plus(sigSum, calculation.getResult());
				}
				return sigSum;
			}
		}
		
		/**
		 * Core algorithms for ID-REC, Including: Classic, Classic Improved (0-REC), Redundancy Mining.
		 * 
		 * @author Benjamin_L
		 */
		public static class Core{
			/**
			 * Get core of the given {@link EquivalentClassDecisionMapExtension} {@link Collection} and 
			 * attributes.
			 * 
			 * <p>Using classic core strategy.
			 * 
			 * @param <Sig>
			 * 		Type of feature significance.
			 * @param instanceSize
			 * 		The number of {@link UniverseInstance}.
			 * @param calculation
			 * 		{@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} implemented 
			 * 		instance.
			 * @param globalSig
			 * 		Global positive region value.
			 * @param sigDeviation
			 * 		Acceptable deviation when calculating significance of attributes. Consider equal when 
			 * 		the difference between two sig is less than the given deviation value.
			 * @param equClasses
			 * 		An {@link EquivalentClassDecisionMapExtension} {@link Collection}.
			 * @param attributes
			 * 		Attributes of {@link UniverseInstance}.
			 * @return An {@link Integer} {@link Collection}.
			 * @throws Exception if exceptions occur when {@link Calculation} calling plus().
			 */
			public static <Sig extends Number> Collection<Integer> classic(
					int instanceSize,
					RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
					Sig globalSig, Sig sigDeviation, 
					Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, int[] attributes
			) throws Exception {
				// core={}
				Collection<Integer> core = new HashSet<>();
				// attrCheck=C, S=null, sig=0
				int[] examAttr = new int[attributes.length-1];
				for (int i=0; i<examAttr.length; i++)	examAttr[i] = attributes[i+1];
				// Loop over a in attrCheck
				Sig sig = null;
				Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
				for (int i=0; i<attributes.length; i++) {
					// S = roughEquivalentClass(EC_Table, C-{a})
					roughClasses = Basic.roughEquivalentClass(
										equClasses, 
										new IntegerArrayIterator(examAttr)
									);
					// Loop over E in S
					sig = null;
					for (RoughEquivalentClassDecisionMapExtension<Sig> roughClass: roughClasses) {
						if (!ClassSetType.POSITIVE.equals(roughClass.getType())) {
							if (ClassSetType.NEGATIVE.equals(roughClass.getType()) && 
								roughClass.getItemSize()==1
							) {
								// sig = sig + E.sig
								for (EquivalentClassDecisionMapExtension<Sig> equClass: roughClass.getItemSet())
									sig = calculation.plus(sig, equClass.getSingleSigMark());
							}else {
								// sig = sig + sig calculation(E)
								calculation = calculation.calculate(roughClass, examAttr.length, instanceSize);
								sig = calculation.plus(sig, calculation.getResult());
							}
						}
					}
					if (calculation.value1IsBetter(globalSig, sig, sigDeviation))
						core.add(attributes[i]);
					if (i<examAttr.length)	examAttr[i] = attributes[i];
				}
				return core;
			}
			
			/**
			 * Get core of the given {@link EquivalentClassDecisionMapExtension} {@link Collection} and 
			 * attributes.
			 * 
			 * @author Benjamin_L
			 */
			public static class ClassicImproved {
				/**
				 * Execute and get core.
				 * 
				 * @param <Sig>
				 * 		Type of feature significance.
				 * @param universeSize
				 * 		The number of {@link UniverseInstance}.
				 * @param calculation
				 * 		{@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} implemented
				 * 		instance.
				 * @param globalSig
				 * 		Global positive region value.
				 * @param sigDeviation
				 * 		Acceptable deviation when calculating significance of attributes. Consider equal when
				 * 		the difference between two sig is less than the given deviation value.
				 * @param equClasses
				 * 		An {@link EquivalentClassDecisionMapExtension} {@link Collection}.
				 * @param attributes
				 * 		Attributes of {@link UniverseInstance}.
				 * @return An {@link Integer} {@link Collection}.
				 * @throws Exception if exceptions occur when {@link Calculation} calling plus().
				 */
				public static <Sig extends Number> Collection<Integer> exec(
						int universeSize,
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
						Sig globalSig, Sig sigDeviation,
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, int[] attributes
				) throws Exception {
					// core={}
					Collection<Integer> core = new HashSet<>();
					// attrCheck=C, S=null, sig=0
					int[] examAttr = new int[attributes.length-1];
					for (int i=0; i<examAttr.length; i++)	examAttr[i] = attributes[i+1];
					// Loop over a in attrCheck
					Sig sig = null;
					Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
					for (int i=0; i<attributes.length; i++) {
						// S = roughEquivalentClass(EC_Table, C-{a})
						roughClasses = roughEquivalentClass(
											equClasses, 
											new IntegerArrayIterator(examAttr)
										);
						// Loop over E in S
						sig = null;
						if (roughClasses!=null) {
							for (RoughEquivalentClassDecisionMapExtension<Sig> roughClass: roughClasses) {
								if (ClassSetType.NEGATIVE.equals(roughClass.getType())) {
									if (roughClass.getItemSize()==1) {
										// sig = sig + E.sig
										for (EquivalentClassDecisionMapExtension<Sig> equClass: roughClass.getItemSet())
											sig = calculation.plus(sig, equClass.getSingleSigMark());
									}else {
										// sig = sig + sig calculation(E)
										calculation.calculate(roughClass, examAttr.length, universeSize);
										sig = calculation.plus(sig, calculation.getResult());
									}
								}
							}
							if (calculation.value1IsBetter(globalSig, sig, sigDeviation))
								core.add(attributes[i]);
						}else {
							core.add(attributes[i]);
						}
						if (i<examAttr.length)	examAttr[i] = attributes[i];
					}
					return core;
				}
			
				/**
				 * Generate {@link RoughEquivalentClassDecisionMapExtension} {@link Collection} based on the
				 * given {@link EquivalentClassDecisionMapExtension} {@link Collection} and the given 
				 * attributes.
				 * 
				 * @see {@link Basic#roughEquivalentClass(Collection, IntegerIterator)}
				 * 
				 * @param <Sig>
				 * 		Type of feature significance.
				 * @param equClasses
				 * 		{@link EquivalentClassDecisionMapExtension} {@link Collection}.
				 * @param attributes
				 * 		Attributes of {@link UniverseInstance}.
				 * @return {@link RoughEquivalentClassDecisionMapExtension} {@link Collection}. / 
				 * 			<code>null</code> if <code>RoughEquivalentClassDecisionMapExtension</code>'s
				 * 			type equals {@link ClassSetType#BOUNDARY}.
				 */
				public static <Sig extends Number> Collection<RoughEquivalentClassDecisionMapExtension<Sig>> 
					roughEquivalentClass(
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, 
						IntegerIterator attributes
				){
					// Create a Hash for E as Rough equivalent classes: HE
					Map<IntArrayKey, RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = new HashMap<>();
					// Loop over e in E
					int[] keyArray;
					IntArrayKey key;
					RoughEquivalentClassDecisionMapExtension<Sig> roughClass;
					for (EquivalentClassDecisionMapExtension<Sig> equClass: equClasses) {
						// key = P(e)
						keyArray = new int[attributes.size()];
						attributes.reset();
						for (int i=0; i<keyArray.length; i++)
							keyArray[i] = equClass.getAttributeValueAt(attributes.next()-1);
						key = new IntArrayKey(keyArray);
						roughClass = roughClasses.get(key);
						if (roughClass==null) {
							// if (HE doesn't contain key)
							//	create h
							roughClasses.put(key, roughClass=new RoughEquivalentClassDecisionMapExtension<>());
							//	h.addMember(e)
							//	h.dv=e.dv
							//	if (e.cons=true)
							//		h.cons=1
							//	else
							//		h.cons=-1
							roughClass.addClassItem(equClass);
						}else {
							// if HE contains key
							//	if h.cons=1
							//		h.addMember(e)
							//		h.dv=combine(h.dv, e.dv)
							//		if h.cons==1 && e.cons==true && h.dec=e.dec
							//			continue;
							//		else if h.cons=-1 && e.cons==false
							//			continue;
							//		else 
							//			return false, the current attribute is not core.
							roughClass.addClassItem(equClass);
							if (ClassSetType.BOUNDARY.equals(roughClass.getType()))	return null;
						}
					}
					return roughClasses.values();
				}
			}
			
			/** 
			 * Get core of the given {@link EquivalentClassDecisionMapExtension} {@link Collection} and 
			 * attributes.
			 * 
			 * <p>Using incremental redundancy mining core strategy.
			 * 
			 * @param <Sig>
			 * 		Type of feature significance.
			 * @param instanceSize
			 * 		The number of {@link UniverseInstance}.
			 * @param universeStreamline
			 * 		{@link UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt} instance.
			 * @param calculationClass
			 * 		{@link Class} of {@link Calculation}.
			 * @param globalSig
			 * 		Global positive region value.
			 * @param sigDeviation
			 * 		Acceptable deviation when calculating significance of attributes. Consider equal when 
			 * 		the difference between two sig is less than the given deviation value.
			 * @param negativeSignificance
			 * 		Significances of Negative region in {@link Map}.
			 * @param equClasses
			 * 		An {@link EquivalentClassDecisionMapExtension} {@link Collection}.
			 * @param attributes
			 * 		Attributes of {@link UniverseInstance}.
			 * @return An {@link Integer} {@link Collection}.
			 * @throws Exception if exceptions occur when {@link UniverseStreamline} streamlining or
			 * 			when {@link Calculation} calling plus().
			 */
			public static <Sig extends Number> Collection<Integer> redundancyMining(
					int instanceSize, 
					UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig> universeStreamline,
					RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
					Sig globalSig, Sig sigDeviation,
					Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, int[] attributes
			) throws Exception {
				// core = {}
				Collection<Integer> core = new HashSet<>();
				// attrCheck = C
				int limit = attributes.length;
				// S = U
				// Loop over a in attrCheck
				Sig sig;
				boolean redundant;
				Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
				for (int i=0; i<limit; i++) {
					roughClasses = null;
					redundant = false;
					// Loop over b[i] in C-{a}
					for (int j=0; j<attributes.length; j++) {
						if (i==j)	continue;
						// S = roughEquivalentClass(S, b[i])
						if (roughClasses==null) {
							roughClasses = Basic.roughEquivalentClass(
												equClasses, 
												new IntegerArrayIterator(attributes[j])
											);
						}else {
							roughClasses = Basic.incrementalPartitionRoughEquivalentClass(
												roughClasses, 
												new IntegerArrayIterator(attributes[j])
											);
						}
						roughClasses = universeStreamline.streamline(
											new StreamlineInput4RECIncrementalDecisionExtension<Sig>(
													roughClasses, 1
											)
										).getRoughClasses();
						if (roughClasses.isEmpty()) {
							redundant = true;
							limit = j+1;
							break;
						}else {
							sig = null;
							for (RoughEquivalentClassDecisionMapExtension<Sig> roughClass: roughClasses) {
								if (calculation.calculateAble(roughClass)) {
									if (roughClass.getItemSize()!=1) {
										// sig = sig + sig calculation(E)
										calculation.calculate(roughClass, 1, instanceSize );
										sig = calculation.plus(sig, calculation.getResult());
									}else {
										// sig = sig + E.sig
										for (EquivalentClassDecisionMapExtension<Sig> equClass: roughClass.getItemSet())
											sig = calculation.plus(sig, equClass.getSingleSigMark());
									}
								}
							}
							// if sig(S) + E.sig, update
							if (calculation.value1IsBetter(globalSig, sig, sigDeviation)) {
								redundant = true;
								limit = j+1;
								break;
							}
						}
					}
					if (!redundant)	core.add(attributes[i]);
				}
				return core;
			}
		}
		
		/**
		 * Get the least significant attribute in attributes out of reducts.
		 * 
		 * @param <Sig>
		 * 		Type of feature significance.
		 * @param equClasses
		 * 		A {@link EquivalentClassDecisionMapExtension} {@link Collection}.
		 * @param red
		 * 		Current reduct.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @param instanceSize
		 * 		The number of {@link UniverseInstance}.
		 * @param calculation
		 * 		{@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} instance.
		 * @param sigDeviation
		 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
		 * 		difference between two sig is less than the given deviation value.
		 * @return the most significant attribute in {@link int}.
		 */
		public static <Sig extends Number> int leastSignificantAttribute(
				Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses, 
				Collection<Integer> red, int[] attributes, int instanceSize, 
				RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
				Sig sigDeviation
		) {
			// sig=0, a*=0
			int sigAttr=-1;
			Sig redSig, minRedSig = null;
			// S = null
			// Loop over a in C-Red
			Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
			for (int attr : attributes) {
				if (red.contains(attr))	continue;
				// a_U = EC_Table
				// a_U = roughEquivalentClass(a_U, a)
				roughClasses = Basic.roughEquivalentClass(
									equClasses, 
									new IntegerArrayIterator(attr)
								);
				// a.outerSig = sig calculation(a_U)
				calculation.calculate(roughClasses, 1, instanceSize);
				redSig = calculation.getResult();
				// if (a.outerSig > sig)
				if (minRedSig==null || calculation.value1IsBetter(minRedSig, redSig, sigDeviation)) {
					//	a*=a
					sigAttr = attr;
					//	sig=a.outerSig
					minRedSig = redSig;
				}
			}
			return sigAttr;
		}
	
		/**
		 * Get the most significant attribute in attributes out of reducts based on the given 
		 * {@link RoughEquivalentClassDecisionMapExtension} {@link Collection}.
		 * 
		 * @param <Sig>
		 * 		Type of feature significance.
		 * @param roughClasses
		 * 		A {@link RoughEquivalentClassDecisionMapExtension} {@link Collection}.
		 * @param red
		 * 		Current reduct.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @param instanceSize
		 * 		The number of {@link UniverseInstance}.
		 * @param calculation
		 * 		{@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} instance.
		 * @param deviation
		 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
		 * 		difference between two sig is less than the given deviation value.
		 * @return {@link MostSignificantAttributeResult} with the most significant attribute and the 
		 * 			correspondent {@link RoughEquivalentClassDecisionMapExtension} {@link Collection}.
		 * @throws Exception if exceptions occur when {@link UniverseStreamline} streamlining or when
		 * 			{@link Calculation} calling plus().
		 */
		public static <Sig extends Number> MostSignificantAttributeResult<Sig> 
			mostSignificantAttribute(
				Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses, 
				Collection<Integer> red, int[] attributes, int instanceSize, 
				RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
				Sig deviation,
				UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig> universeStreamline
		) throws Exception {
			// sig=0
			// a*=0
			int sigAttr=-1;
			// global_sig_static=0
			Sig redSig, staticSig, sigSum;
			Sig maxRedSig = null, maxStaticSig = null, maxSigSum = null;
			// S = null
			// Loop over a in C-Red
			StreamlineResult4RECIncrementalDecisionExtension<Sig> streamlineResult;
			Collection<RoughEquivalentClassDecisionMapExtension<Sig>> sigRoughClasses = null, 
																		incrementalRoughClasses;
			for (int attr: attributes) {
				if (red.contains(attr))	continue;
				// a_U = EC_Table
				// a_U = roughEquivalentClass(a_U, a)
				incrementalRoughClasses = Basic.incrementalPartitionRoughEquivalentClass(
												roughClasses, 
												new IntegerArrayIterator(attr)
											);
				// a_U, sig_static = streamline(a_U, sig calculation)
				streamlineResult = universeStreamline.streamline(
									new StreamlineInput4RECIncrementalDecisionExtension<Sig>(
											incrementalRoughClasses, 1
									)
								);
				incrementalRoughClasses = streamlineResult.getRoughClasses();
				staticSig = streamlineResult.getRemovedUniverseSignificance();//*/
				// a.outerSig = sig calculation(a_U)
				calculation.calculate(incrementalRoughClasses, 1);
				redSig = calculation.getResult();
				// if (a.outerSig > sig)
				sigSum = calculation.plus(redSig, staticSig);
				if (maxSigSum==null || calculation.value1IsBetter(sigSum, maxSigSum, deviation)) {
					//	a*=a
					sigAttr = attr;
					//	sig=a.outerSig
					maxRedSig = redSig;
					//	global_sig_static = sig_static
					maxStaticSig = staticSig;
					maxSigSum = sigSum;
					//	S = a_U
					sigRoughClasses = incrementalRoughClasses;
				}
			}
			return new MostSignificantAttributeResult<>(maxRedSig, maxStaticSig, sigAttr, sigRoughClasses);
		}
	
		/**
		 * Reduct inspection algorithms for ID-REC.
		 * 
		 * @author Benjamin_L
		 */
		public static class InspectReduct{
			
			/**
			 * Inspect the given reduct and remove redundant attributes.
			 * 
			 * @param <Sig>
			 * 		Type of feature significance.
			 * @param instanceSize
			 * 		The number of {@link UniverseInstance}.
			 * @param red
			 * 		Current reduct.
			 * @param globalSig
			 * 		Global positive region value.
			 * @param equClasses
			 * 		An {@link EquivalentClassDecisionMapExtension} {@link Collection}.
			 * @param calculation
			 * 		{@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} instance.
			 * @param sigDeviation
			 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
			 * 		difference between two sig is less than the given deviation value.
			 * @throws InstantiationException if exceptions occur when creating {@link Calculation} instance.
			 * @throws IllegalAccessException if exceptions occur when creating {@link Calculation} instance.
			 * @throws IllegalArgumentException if exceptions occur when creating {@link Calculation} instance.
			 * @throws InvocationTargetException if exceptions occur when creating {@link Calculation} instance.
			 * @throws NoSuchMethodException if exceptions occur when creating {@link Calculation} instance.
			 * @throws SecurityException if exceptions occur when creating {@link Calculation} instance.
			 */
			public static <Sig extends Number> void classic(
					int instanceSize,
					Collection<Integer> red, Sig globalSig, 
					Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses,
					RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
					Sig sigDeviation
			) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
					NoSuchMethodException, SecurityException
			{
				// Loop over a in R
				Sig examSig;
				Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
				Integer[] redCopy = red.toArray(new Integer[red.size()]);
				for (int attr: redCopy) {
					// calculate Sig(R-{a}).
					red.remove(attr);
					roughClasses = Basic.roughEquivalentClass(equClasses, new IntegerCollectionIterator(red));
					calculation = calculation.calculate(roughClasses, red.size(), instanceSize);
					examSig = calculation.getResult();
					// if (R-{a}.sig==C.sig)
					if (calculation.value1IsBetter(globalSig, examSig, sigDeviation)) {
						// R = R-{a}
						red.add(attr);
					}
				}
			}
			
			/**
			 * Inspect the given reduct and remove redundant attributes.
			 * 
			 * @param <Sig>
			 * 		Type of feature significance.
			 * @param instanceSize
			 * 		The number of {@link UniverseInstance}.
			 * @param red
			 * 		Current reduct.
			 * @param globalSig
			 * 		Global positive region value.
			 * @param equClasses
			 * 		An {@link EquivalentClassDecisionMapExtension} {@link Collection}.
			 * @param calculation
			 * 		{@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation} instance.
			 * @param sigDeviation
			 * 		Acceptable deviation when calculating significance of attributes. Consider equal when the 
			 * 		difference between two sig is less than the given deviation value.
			 */
			public static <Sig extends Number> void classicImproved(
					int instanceSize,
					Collection<Integer> red, Sig globalSig, 
					Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses,
					RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation, 
					Sig sigDeviation
			) {
				// Loop over a in R
				Sig examSig;
				Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses;
				Integer[] redCopy = red.toArray(new Integer[red.size()]);
				for (int attr: redCopy) {
					// calculate Sig(R-{a}).
					red.remove(attr);
					roughClasses = Core.ClassicImproved.roughEquivalentClass(equClasses, new IntegerCollectionIterator(red));
					// * Doesn't contains 0-REC, otherwise, current attribute is not redundant
					//	 (for it can not be removed).
					if (roughClasses!=null) {
						calculation.calculate(roughClasses, red.size(), instanceSize);
						examSig = calculation.getResult();
						// if (R-{a}.sig==C.sig)
						if (calculation.value1IsBetter(globalSig, examSig, sigDeviation)) {
							// R = R-{a}
							red.add(attr);
						}
					}else {
						red.add(attr);
					}
				}
			}
		}
	}
}