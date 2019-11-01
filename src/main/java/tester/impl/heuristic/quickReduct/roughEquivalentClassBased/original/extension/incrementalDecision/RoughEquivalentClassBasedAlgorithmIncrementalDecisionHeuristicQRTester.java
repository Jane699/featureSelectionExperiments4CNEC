package tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.interf.Calculation;
import basic.procedure.ProcedureComponent;
import basic.procedure.ProcedureContainer;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.procedure.timer.TimeSum;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.roughEquivalentClassBased.extension.IncrementalDecisionRoughEquivalentClassBasedStrategy;
import featureSelection.repository.frame.support.reductMiningStrategy.heuristic.QuickReductHeuristicReductStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.roughEquivalentClassBased.RoughEquivalentClassBasedExtensionAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.EquivalentClass;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.RoughEquivalentClass;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.EquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original.Streamline4CompactedDecisionTable;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.SelectiveComponentsProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.SigLoopPreprocessProcedureContainer;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.SignificantAttributeSeekingLoopProcedureContainer;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.core.ClassicCoreProcedureContainer;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.core.ClassicImprovedCoreProcedureContainer;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.inspect.ClassicImprovedReductInspectionProcedureContainer;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.inspect.ClassicReductInspectionProcedureContainer;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Tester Procedure for <strong>Incremental Decision Rough Equivalent Classes based (ID-REC)</strong> 
 * Feature Selection.
 * <p>
 * <strong>Notice</strong>: 
 * The name "Incremental Decision Rough Equivalent Classes based (ID-REC)" has been changed into <strong>
 * "Classified Nested Equivalent Class (CNEC)"</strong>
 * <p>
 * This is a {@link SelectiveComponentsProcedureContainer}. Procedure contains 6 {@link ProcedureComponent}s, 
 * refer to steps: 
 * <li>
 * 	<strong>Get the Equivalent Class</strong>: 
 * 	<p>Get the global equivalent classes partitioned by C.
 * </li>
 * <li>
 * 	<strong>Initiate</strong>: 
 * 	<p>Initiate {@link Streamline4CompactedDecisionTable}, {@link Calculation} instances, and calculate the
 * 		Global significance.
 * </li>
 * <li>
 * 	<strong>Get Core</strong>: 
 * 	<p>Calculate Core. 
 * 	<p><code>ClassicCoreProcedureContainer</code>
 * 	<p><code>ClassicImprovedCoreProcedureContainer</code>
 * </li>
 * <li>
 * 	<strong>After Core / Init reduct list</strong>: 
 * 	<p>Calculate Core. 
 * 	<p><code>SigLoopPreprocessProcedureContainer</code>
 * </li>
 * <li>
 * 	<strong>Sig loop</strong>: 
 * 	<p>Loop and search for the most significant attribute and add as an attribute of the reduct until 
 * 		reaching exit condition.
 * 	<p><code>SignificantAttributeSeekingLoopProcedureContainer</code>
 * </li>
 * <li>
 * 	<strong>Reduct Inspection</strong>: 
 * 	<p>Inspect the reduct and remove redundant attributes.
 * 	<p><code>ClassicReductInspectionProcedureContainer</code>
 * 	<p><code>ClassicImprovedReductInspectionProcedureContainer</code>
 * </li>
 * 
 * @see {@link RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation}
 * @see {@link RoughEquivalentClassBasedExtensionAlgorithm.IncrementalDecision}
 * 
 * @see {@link ClassicCoreProcedureContainer}
 * @see {@link ClassicImprovedCoreProcedureContainer}
 * @see {@link SigLoopPreprocessProcedureContainer}
 * @see {@link SignificantAttributeSeekingLoopProcedureContainer}
 * @see {@link ClassicReductInspectionProcedureContainer}
 * @see {@link ClassicImprovedReductInspectionProcedureContainer}
 * 
 * @author Benjamin_L
 */
@Slf4j
@RoughSet
public class RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<Sig extends Number> 
	extends SelectiveComponentsProcedureContainer<Collection<Integer>>
	implements TimeSum, 
				StatisticsCalculated, 
				ReportMapGenerated<String, Map<String, Object>>,
				HashSearchStrategy, 
				IncrementalDecisionRoughEquivalentClassBasedStrategy, 
				QuickReductHeuristicReductStrategy 
{
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	
	private String[] componentExecOrder;
	
	public RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester(ProcedureParameters paramaters, boolean logOn) {
		super(logOn, paramaters);
		statistics = new HashMap<>();
		report = new HashMap<>();
	}

	@Override
	public String shortName() {
		String corer;
		Collection<ProcedureContainer<?>> subCons = getComponentMap().get("Get Core").getSubProcedureContainers().values();
		if (subCons!=null)	corer = subCons.iterator().next().shortName();
		else				corer = "UNKNOWN";

		String inspector;
		if (this.getComponentMap().containsKey("Reduct Inspection"))
			subCons = getComponentMap().get("Reduct Inspection").getSubProcedureContainers().values();
		if (subCons!=null)	inspector = subCons.iterator().next().shortName();
		else				inspector = "UNKNOWN";
		
		return "QR-REC(Ext.ID)"+
				"("+ProcedureUtils.ShortName.calculation(getParameters())+")"+
				"("+ProcedureUtils.ShortName.byCore(getParameters())+")"+
				"("+corer+")"+
				"("+inspector+")";
	}
	
	@Override
	public String staticsName() {
		return shortName();
	}

	@Override
	public String reportName() {
		return shortName();
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public void initDefaultComponents(boolean logOn) {
		ProcedureComponent<?>[] componentArray = new ProcedureComponent<?>[] {
			// 1. Get the Equivalent Class.
			new TimeCountedProcedureComponent<Collection<EquivalentClassDecisionMapExtension<Sig>>>(
					ComponentTags.TAG_COMPACT,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("1. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES)
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[0];
						int[] attributes = (int[]) parameters[1];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return RoughEquivalentClassBasedExtensionAlgorithm
								.IncrementalDecision
								.Basic
								.equivalentClass(universes, new IntegerArrayIterator(attributes));
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("equClasses", result);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_COMPACTED_SIZE]
						ProcedureUtils.Statistics.countInt(statistics, 
															Constants.STATISTIC_COMPACTED_SIZE, 
															result.size()
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								result.size(), 
								0
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
				}.setDescription("Get the Equivalent Class"),
			// 2. Initiate.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("2. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses = 
								(Collection<EquivalentClassDecisionMapExtension<Sig>>) 
								parameters[p++];
						Class<? extends RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>> calculationClass = 
								(Class<? extends RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>>) 
								parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation = 
								calculationClass.newInstance();
						Sig globalSig = RoughEquivalentClassBasedExtensionAlgorithm
											.IncrementalDecision
											.Basic
											.globalSignificance(
												equClasses, 
												universes.size(), 
												attributes.length,
												calculation
											);
						UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig> universeStreamline = 
								new UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<>();
						universeStreamline.setCalculation(calculation);
						universeStreamline.setUniverseSize(universes.size());
						return new Object[] {
								globalSig, 
								universeStreamline,
								calculation
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						getParameters().setNonRootParameter("globalSig", result[r++]);
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE, result[r++]);
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE, result[r++]);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "global Sig = {}"), result[0]);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						Collection<EquivalentClass> equClasses = getParameters().get("equClasses");
						//	[globalSignificance()]
						ProcedureUtils.Statistics.countInt(getStatistics(), 
															"RoughEquivalentClassBasedExtensionAlgorithm.IncrementalDecision.Basic.globalSignificance()", 
															1
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								equClasses.size(), 
								0
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Initiate"),
			// 3. Get Core.
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("3. "+component.getDescription());
						component.setLocalParameters(new Object[] {
							getParameters().get(ParameterConstants.PARAMETER_QR_BY_CORE),
						});
					},
					(component, parameters) -> {
						return parameters[0]!=null && ((Boolean) parameters[0])?
								(Collection<Integer>) component.getSubProcedureContainers().values().iterator().next().exec():
								new LinkedList<>();
					}, 
					(component, result) -> {
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
						// Report
						Collection<EquivalentClass> equClasses = getParameters().get("equClasses");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								equClasses.size(), 
								result.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long componentTime = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), componentTime);
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Get Core")
				.setSubProcedureContainer(
					"CoreProcedureContainer", 
					new ClassicCoreProcedureContainer<Sig>(getParameters(), logOn)
				),
			// 4. After Core / Init reduct list.
			new ProcedureComponent<Collection<RoughEquivalentClass<EquivalentClass>>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("4. "+component.getDescription());
					},
					(component, parameters) -> {
						return (Collection<RoughEquivalentClass<EquivalentClass>>) 
								component.getSubProcedureContainers()
										.values()
										.iterator()
										.next()
										.exec();
					}, 
					(component, result) -> {
						this.getParameters().setNonRootParameter("roughClasses", result);
						// Report
						Collection<EquivalentClass> equClasses = getParameters().get("equClasses");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								equClasses.size(), 
								result.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long componentTime = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), componentTime);
					}
				){
					@Override public void init() {}
							
					@Override public String staticsName() {
						return shortName()+" | 4. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setSubProcedureContainer(
					"SigLoopPreprocessProcedureContainer", 
					new SigLoopPreprocessProcedureContainer<Sig>(getParameters(), logOn)
				).setDescription("After Core / Init reduct list"),
			// 5. Sig loop.
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					getParameters(), 
					(component)->{
						if (logOn)	log.info("5. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								component.getSubProcedureContainers().values().iterator().next()
						});
					}, 
					(component, parameters)->{
						return (Collection<Integer>) ((ProcedureContainer<?>) parameters[0]).exec();
					}, 
					(component, result)->{
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|Reduct Candidate| = {}"), result.size());
						statistics.put(Constants.STATISTIC_RED_BEFORE_INSPECT, result);
						// Report
						Collection<EquivalentClass> equClasses = getParameters().get("equClasses");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								equClasses.size(), 
								result.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long componentTime = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), componentTime);
					}
				) {
					@Override public void init() {}
						
					@Override public String staticsName() {
						return shortName()+" | 5. of "+getComponents().size()+".";
					}
				}.setSubProcedureContainer(
					"SignificantAttributeSeekingLoopProcedureContainer", 
					new SignificantAttributeSeekingLoopProcedureContainer<Sig>(getParameters(), logOn)
				).setDescription("Sig loop"),
			// 6. Reduct Inspection.
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CHECK,
					getParameters(), 
					(component)->{
						if (logOn)	log.info("6. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								component.getSubProcedureContainers().values().iterator().next()
						});
					}, 
					(component, parameters)->{
						return (Collection<Integer>) ((ProcedureContainer<?>) parameters[0]).exec();
					}, 
					(component, result)->{
						// Statistics
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST_AFTER_INSPECTATION, result);
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|Reduct Finally| = {}"), result.size());
						statistics.put(Constants.STATISTIC_RED_AFTER_INSPECT, result);
						// Report
						Collection<EquivalentClass> equClasses = getParameters().get("equClasses");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								equClasses.size(), 
								result.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long componentTime = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), componentTime);
					}
				) {
					@Override public void init() {}
						
					@Override public String staticsName() {
						return shortName()+" | 6. of "+getComponents().size()+".";
					}
				}.setSubProcedureContainer(
					"ReductInspectionProcedureContainer", 
					new ClassicReductInspectionProcedureContainer<Sig>(getParameters(), logOn)
				).setDescription("Reduct Inspection"),
		};
		//	Component order.
		componentExecOrder = new String[componentArray.length];
		for (int i=0; i<componentArray.length; i++) {
			this.setComponent(componentArray[i].getDescription(), componentArray[i]);
			componentExecOrder[i] = componentArray[i].getDescription();
		}
	}

	@Override
	public String[] componentsExecOrder() {
		return componentExecOrder;
	}
	
	@Override
	public String[] getReportMapKeyOrder() {
		return componentsExecOrder();
	}
	
	@Override
	public long getTime() {
		return getComponents().stream()
				.map(comp->ProcedureUtils.Time.sumProcedureComponentTimes(comp))
				.reduce(Long::sum).orElse(0L);
	}

	@Override
	public Map<String, Long> getTimeDetailByTags() {
		return ProcedureUtils.Time.sumProcedureComponentsTimesByTags(this);
	}
}