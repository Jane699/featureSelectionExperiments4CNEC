package tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import basic.utils.StringUtils;
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
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Core searching for <strong>Incremental Decision Rough Equivalent Class based(ID-REC)</strong> Feature 
 * Selection. This procedure contains 3 ProcedureComponents: 
 * <li>
 * 	<strong>Core procedure controller</strong>
 * 	<p>Control loop over attributes that are not in reduct, and calculate their inner significance. If 
 * 		removing the attribute from the feature subset doesn't have any effect on the significance(i.e 
 * 		<code>sig(C-{a})==sig(C)</code>), it is NOT a core attribute. 
 * </li>
 * <li>
 * 	<strong>Rough Equivalent Class partition</strong>
 * 	<p>Use the given feature subset to partition {@link EquivalentClass}es and get the correspondent 
 * 		Rough Equivalent Classes.
 * </li>
 * <li>
 * 	<strong>Core</strong>
 * 	<p>Calculate the inner significance of the attribute and determine if it is a Core attribute. In 
 * 		the calculation, {@link EquivalentClassDecisionMapExtension#singleSigMark} is used to avoid 
 * 		the re-calculating entropy of rough equivalent class is {@link ClassSetType#NEGATIVE} and 
 * 		contains only 1 equivalent class. Which is an accelerating strategy to calculate Core.
 * </li>
 * 
 * @author Benjamin_L
 */
@Slf4j
public class ClassicCoreProcedureContainer<Sig extends Number> 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;
	
	public ClassicCoreProcedureContainer(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? null: log, paramaters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
		
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Core(Classic)";
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
			// 1. Core procedure controller.
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					},
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						int[] attributes = (int[]) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						ProcedureComponent<?> comp1 = getComponents().get(1);
						ProcedureComponent<?> comp2 = getComponents().get(2);
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 core={}
						Collection<Integer> core = new HashSet<>(attributes.length);
						TimerUtils.timePause((TimeCounted) component);
						localParameters.put("core", core);
						TimerUtils.timeContinue((TimeCounted) component);
						// 2~4 attrCheck=C, S=null, sig=0
						int[] examAttr = new int[attributes.length-1];
						for (int i=0; i<examAttr.length; i++)	examAttr[i] = attributes[i+1];
						TimerUtils.timePause((TimeCounted) component);
						localParameters.put("examAttr", examAttr);
						TimerUtils.timeContinue((TimeCounted) component);
						// 5 Go through a in attrCheck
						for (int i=0; i<attributes.length; i++) {
							TimerUtils.timePause((TimeCounted) component);
							localParameters.put("i", i);
							comp1.exec();
							comp2.exec();
							
							examAttr = (int[]) localParameters.get("examAttr");
							
							TimerUtils.timeContinue((TimeCounted) component);
						}
						return core;
					}, 
					(component, core) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, core);
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(1, "|core|={}, core={}"), 
									core.size(), 
									StringUtils.numberToString(core, 50, 0)
								);
						}
						/* ------------------------------------------------------------------------------ */
						// Statistics
						int calculatePlusSingleMark = (int) localParameters.get("calculatePlusSingleMark");
						int[] attributes = getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES);
						//	[calculatePlusSingleMark]
						ProcedureUtils.Statistics.countInt(statistics, 
															"Core.calculatePlusSingleMark", 
															calculatePlusSingleMark
														);
						//	[STATISTIC_CORE_LIST]
						statistics.put(Constants.STATISTIC_CORE_LIST, core==null? null: core.toArray(new Integer[core.size()]));
						//	[STATISTIC_CORE_ATTRIBUTE_EXAMED_LENGTH]
						statistics.put(Constants.STATISTIC_CORE_ATTRIBUTE_EXAMED_LENGTH, attributes.length);
						/* ------------------------------------------------------------------------------ */
						// Report
						reportKeys.add(component.getDescription());
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								((Collection<?>) getParameters().get("equClasses")).size(), 
								core.size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Core procedure controller"),
			// 2. Rough Equivalent Class partition.
			new TimeCountedProcedureComponent<Collection<RoughEquivalentClassDecisionMapExtension<Sig>>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								localParameters.get("examAttr"),
						});
					},
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p = 0;
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses = (Collection<EquivalentClassDecisionMapExtension<Sig>>) parameters[p++];
						int[] examAttr = (int[]) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 6 S = roughEquivalentClass(EC_Table, C-{a})
						return RoughEquivalentClassBasedExtensionAlgorithm
								.IncrementalDecision
								.Basic
								.roughEquivalentClass(
									equClasses, 
									new IntegerArrayIterator(examAttr)
								);
					}, 
					(component, roughClasses) -> {
						/* ------------------------------------------------------------------------------ */
						localParameters.put("roughClasses", roughClasses);
						/* ------------------------------------------------------------------------------ */
						// Report
						reportKeys.add(reportMark());
						//	[REPORT_EXECUTION_TIME]
						saveReportExecutedTime((TimeCountedProcedureComponent<?>) component);
					}
				){
					@Override public void init() {}
											
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Rough Equivalent Class partition"),
			// 3. Get Core.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get("globalSig"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								localParameters.get("i"),
								localParameters.get("examAttr"),
								localParameters.get("core"),
								localParameters.get("roughClasses"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
					},
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						boolean attributesIsCore = false;
						int calculatePlusSingleMark = 0, calculateGetResult = 0;
						/* ------------------------------------------------------------------------------ */
						int p = 0;
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation =
								(RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>) 
								parameters[p++];
						Sig globalSig = (Sig) parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						int i = (int) parameters[p++];
						int[] examAttr = (int[]) parameters[p++];
						Collection<Integer> core = (Collection<Integer>) parameters[p++];
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = 
								(Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) 
								parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 7 Go through E in S
						Sig sig = null;
						for (RoughEquivalentClassDecisionMapExtension<Sig> roughClass: roughClasses) {
							if (!ClassSetType.POSITIVE.equals(roughClass.getType())) {
								if (ClassSetType.NEGATIVE.equals(roughClass.getType()) && 
									roughClass.getItemSize()==1
								) {
									// sig = sig + E.sig
									for (EquivalentClassDecisionMapExtension<Sig> equClass: roughClass.getItemSet()) {
										sig = calculation.plus(sig, equClass.getSingleSigMark());
									}
									
									TimerUtils.timePause((TimeCounted) component);
									calculatePlusSingleMark++;
									TimerUtils.timeContinue((TimeCounted) component);
								}else {
									// sig = sig + sig calculation(E)
									calculation.calculate(roughClass, 1, universes.size());
									sig = calculation.plus(sig, calculation.getResult());
									
									TimerUtils.timePause((TimeCounted) component);
									calculateGetResult++;
									TimerUtils.timeContinue((TimeCounted) component);
								}
							}
						}//*/
						if (calculation.value1IsBetter(globalSig, sig, sigDeviation)) {
							core.add(attributes[i]);
							TimerUtils.timePause((TimeCounted) component);
							attributesIsCore = true;
							TimerUtils.timeContinue((TimeCounted) component);
						}
						if (i<examAttr.length)	examAttr[i] = attributes[i];
						return new Object[] {
								calculatePlusSingleMark,
								calculateGetResult,
								attributes[i],
								attributesIsCore, 
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						int calculatePlusSingleMark = (int) result[r++];
						int calculateGetResult = (int) result[r++];
						int attributes_i = (int) result[r++];
						boolean attributeIsCore = (boolean) result[r++];
						/* ------------------------------------------------------------------------------ */
						Integer p_calculatePlusSingleMark = (Integer) localParameters.get("calculatePlusSingleMark");
						if (p_calculatePlusSingleMark==null)	p_calculatePlusSingleMark = 0;
						Integer p_calculateGetResult = (Integer) localParameters.get("calculateGetResult");
						if (p_calculateGetResult==null)			p_calculateGetResult = 0;
						localParameters.put("calculatePlusSingleMark", calculatePlusSingleMark + p_calculatePlusSingleMark);
						localParameters.put("calculateGetResult", calculateGetResult + p_calculateGetResult);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						Collection<Integer> core = (Collection<Integer>) localParameters.get("core");
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								((Collection<?>) getParameters().get("equClasses")).size(), 
								core.size()
						);
						//	[REPORT_EXECUTION_TIME]
						saveReportExecutedTime((TimeCountedProcedureComponent<?>) component);
						//	[REPORT_CORE_CURRENT_ATTRIBUTE]
						ProcedureUtils.Report.saveItem(
								report, reportMark(), 
								Constants.REPORT_CORE_CURRENT_ATTRIBUTE, 
								attributes_i
						);
						//	[REPORT_CORE_CURRENT_ATTRIBUTE]
						ProcedureUtils.Report.saveItem(
								report, reportMark(), 
								Constants.REPORT_CORE_INDIVIDUAL_RESULT, 
								attributeIsCore
						);
						//	[REPORT_CORE_4_IDREC_CURRENT_STATIC_CALCULATE_TIMES]
						ProcedureUtils.Report.saveItem(
								report, reportMark(), 
								Constants.REPORT_CORE_4_IDREC_CURRENT_STATIC_CALCULATE_TIMES, 
								calculatePlusSingleMark
						);
						//	[REPORT_CORE_4_IDREC_CURRENT_COMMON_CALCULATE_TIMES]
						ProcedureUtils.Report.saveItem(
								report, reportMark(), 
								Constants.REPORT_CORE_4_IDREC_CURRENT_COMMON_CALCULATE_TIMES, 
								calculateGetResult
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
														
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Get Core"),
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

	public String reportMark() {
		return "Loop["+localParameters.get("i")+"]";
	}
	
	/**
	 * Save executed time of {@link Component} at the current round.
	 * 
	 * @see {@link ProcedureUtils.Report.ExecutionTime#save(Map, String, long...)}.
	 * 
	 * @param component
	 * 		{@link TimeCountedProcedureComponent} to be saved.
	 */
	private void saveReportExecutedTime(TimeCountedProcedureComponent<?> component) {
		@SuppressWarnings("unchecked")
		Map<String, Long> executedTime = (Map<String, Long>) localParameters.get("executedTime");
		if (executedTime==null)	localParameters.put("executedTime", executedTime = new HashMap<>());
		Long  historyTime = executedTime.get(component.getDescription());
		if (historyTime==null)	historyTime = 0L;
		ProcedureUtils.Report.ExecutionTime.save(report, reportMark(), component.getTime() - historyTime);
		executedTime.put(component.getDescription(), component.getTime());
	}
}