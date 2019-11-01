package tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.inspect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.roughEquivalentClassBased.RoughEquivalentClassBasedExtensionAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.ClassSetType;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.EquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.core.ClassicCoreProcedureContainer;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Reduct inspection for <strong>Incremental Decision Rough Equivalent Class based(ID-REC)</strong> Feature 
 * Selection. <strong>Class improved version</strong>. (Check out the description of ProcedureComponent 2
 * and 3)
 * <p>
 * This procedure contains 3 ProcedureComponents: 
 * <li>
 * 	<strong>Inspection procedure controller</strong>
 * 	<p>Control to loop over attributes in reduct to inspect redundant.
 * </li>
 * <li>
 * 	<strong>Rough Equivalent Class partition</strong>
 * 	<p>Use the given feature subset to partition {@link EquivalentClass}es and get the correspondent 
 * 		Rough Equivalent Classes. Different from the normal version, it is {@link ClassSetType#BOUNDARY} 
 * 		sensitive and it returns <code>null</code> if encountering one. 
 * 	<p>In this case, using <code>C-{a}</code> to partition {@link EquivalentClass}es and get at least 1 
 * 		Rough Equivalent class which is {@link ClassSetType#BOUNDARY} means without the attribute 
 * 		<code>{a}</code> is indispensable and it is NOT redundant.
 * </li>
 * <li>
 * 	<strong>Inspect reduct redundancy</strong>
 * 	<p>Check the redundancy of an attribute by calculating its inner significance. Considered redundant if 
 * 		the inner significance is 0(i.e. sig(red)=sig(red-{a})). In the calculation, 
 * 		{@link EquivalentClassDecisionMapExtension#singleSigMark} is used to avoid the re-calculating 
 * 		entropy of rough equivalent class is {@link ClassSetType#NEGATIVE} and contains only 1 equivalent 
 * 		class. Which is an accelerating strategy to inspect reduct.
 * 	<p>Different from {@link ClassicCoreProcedureContainer}, a strategy bases on 
 * 		{@link ClassSetType#BOUNDARY} sensitive rough equivalent class partition strategy is used to 
 * 		accelerate the process of determining whether an attribute is redundant or not. That is, it is 
 * 		expecting to encounter {@link ClassSetType#BOUNDARY} in the partitioning by <code>Red-{a}</code> 
 * 		if {a} is not a redundant attribute.
 * </li>
 * 
 * 
 * @see {@link RoughEquivalentClassBasedExtensionAlgorithm.IncrementalDecision.Core.ClassicImproved}
 * @see {@link RoughEquivalentClassBasedExtensionAlgorithm.IncrementalDecision.InspectReduct#classicImproved(
 * 				int, Collection, Number, Collection, RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation, 
 * 				Number)}
 * 
 * @author Benjamin_L
 */
@Slf4j
public class ClassicImprovedReductInspectionProcedureContainer<Sig extends Number> 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;
	private int loopCount, boundarySkipCount;
	
	public ClassicImprovedReductInspectionProcedureContainer(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? null: log, paramaters);
		
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
		
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Reduct inspect(Classic.Imp)";
	}

	@Override
	public String staticsName() {
		return shortName();
	}

	@Override
	public String reportName() {
		return shortName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ProcedureComponent<?>[] initComponents() {
		return new ProcedureComponent<?>[] {
			// 1. Inspection procedure controller.
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component) -> {
						component.setLocalParameters(new Object[] {
								new HashSet<Integer>(getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)),
						});
					},
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						ProcedureComponent<Object[]> comp1 = (ProcedureComponent<Object[]>) getComponents().get(1);
						localParameters.put("red", red);
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 Go through a in R
						Integer[] redCopy = red.toArray(new Integer[red.size()]);
						for (int attr: redCopy) {
							TimerUtils.timePause((TimeCounted) component);

							loopCount++;
							localParameters.put("attr", attr);
							comp1.exec();
							
							TimerUtils.timeContinue((TimeCounted) component);
						}
						return red;
					}, 
					(component, red) -> {
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_AFTER_INSPECT]
						statistics.put(Constants.STATISTIC_RED_AFTER_INSPECT, red);
						/* ------------------------------------------------------------------------------ */
						// Report
						reportKeys.add(component.getDescription());
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								((Collection<?>) getParameters().get("equClasses")).size(), 
								red.size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						//	[REPORT_INSPECT_4_IDREC_0_REC_DIRECT]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_INSPECT_4_IDREC_0_REC_DIRECT, 
								boundarySkipCount
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspectation procedure controller"),
			// 2. Inspect reduct.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component) -> {
						component.setLocalParameters(new Object[] {
								getParameters().get("globalSig"),
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								localParameters.get("attr"),
								localParameters.get("red"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
					},
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						boolean attributeIsRedundant = true, boundaryDiscovered = false;
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Sig globalSig = (Sig) parameters[p++];
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses = 
								(Collection<EquivalentClassDecisionMapExtension<Sig>>) 
								parameters[p++];
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation = 
								(RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>) 
								parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						int attr = (int) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 2 calculate Sig(R-{a}).
						red.remove(attr);
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = 
								RoughEquivalentClassBasedExtensionAlgorithm
									.IncrementalDecision
									.Core
									.ClassicImproved
									.roughEquivalentClass(
											equClasses, 
											new IntegerCollectionIterator(red)
								);
						// * Doesn't contains 0-REC, otherwise, current attribute is not redundant
						//	 (for it can not be removed).
						if (roughClasses!=null) {
							calculation.calculate(roughClasses, red.size(), universes.size());
							Sig examSig = calculation.getResult();
							// 3 if (R-{a}.sig==C.sig)
							if (calculation.value1IsBetter(globalSig, examSig, sigDeviation)) {
								// 4 R = R-{a}
								red.add(attr);
								
								TimerUtils.timePause((TimeCounted) component);
								attributeIsRedundant = false;
								TimerUtils.timeContinue((TimeCounted) component);
							}
						}else {
							red.add(attr);
							
							TimerUtils.timePause((TimeCounted) component);
							boundaryDiscovered = true;
							boundarySkipCount++;
							attributeIsRedundant = false;
							TimerUtils.timeContinue((TimeCounted) component);
						}
						
						return new Object[] {
							attr,
							attributeIsRedundant,
							boundaryDiscovered, 
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						int attr = (int) result[r++];
						boolean attributeIsRedundant = (boolean) result[r++];
						boolean boundaryDiscovered = (boolean) result[r++];
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = "Loop["+loopCount+"] Attr["+attr+"]";
						reportKeys.add(reportMark);
						//	[DatasetRealTimeInfo]
						Collection<Integer> red = (Collection<Integer>) localParameters.get("red");
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark, 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								((Collection<?>) getParameters().get("equClasses")).size(), 
								red.size()
						);
						//	[REPORT_EXECUTION_TIME]
						Long lastComponentTime = (Long) localParameters.get("lastComponentTime");
						if (lastComponentTime==null)	lastComponentTime = 0L;
						long currentComponentTime = ((TimeCountedProcedureComponent<?>) component).getTime();
						ProcedureUtils.Report.ExecutionTime.save(report, reportMark, currentComponentTime - lastComponentTime);
						localParameters.put("lastComponentTime", currentComponentTime);
						//	[REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE, 
								attr
						);
						//	[REPORT_INSPECT_ATTRIBUTE_REDUNDANT]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_INSPECT_ATTRIBUTE_REDUNDANT, 
								attributeIsRedundant
						);
						//	[REPORT_INSPECT_4_IDREC_0_REC_DIRECT]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_INSPECT_4_IDREC_0_REC_DIRECT, 
								boundaryDiscovered
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
											
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspect reduct"),
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Integer> exec() throws Exception {
		ProcedureComponent<?>[] components = initComponents();
		for (ProcedureComponent<?> each : components)	this.getComponents().add(each);
		return (Collection<Integer>) components[0].exec();
	}

	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}