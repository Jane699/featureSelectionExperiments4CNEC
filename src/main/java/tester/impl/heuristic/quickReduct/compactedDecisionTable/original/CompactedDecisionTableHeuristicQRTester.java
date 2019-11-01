package tester.impl.heuristic.quickReduct.compactedDecisionTable.original;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.model.interf.Calculation;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.procedure.timer.TimeSum;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.CompactedDesicionTableStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.compactedDecisionTable.original.CompactedDecisionTableHashAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original.CompactedDecisionTableCalculation;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original.Streamline4CompactedDecisionTable;
import featureSelection.utils.compactedDecisionTable.CompactedDecisionTableOriginalHashAlgorithmReductions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.compactedDecisionTable.original.component.CoreProcedureContainer;
import tester.impl.heuristic.quickReduct.compactedDecisionTable.original.component.ReductInspectionProcedureContainer;
import tester.impl.heuristic.quickReduct.compactedDecisionTable.original.component.SignificantAttributeSeekingLoopProcedureContainer;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Tester Procedure for <strong>Compacted Decision Table</strong> Feature Selection.
 * <p>
 * Original article: <a href="https://www.sciencedirect.com/science/article/abs/pii/S0950705115002312">
 * "Compacted decision tables based attribute reduction"</a> by Wei Wei, Junhong Wang, Jiye Liang, 
 * Xin Mi, Chuangyin Dang.
 * <p>
 * This is a {@link DefaultProcedureContainer}. Procedure contains 8 {@link ProcedureComponent}s, refer to 
 * steps: 
 * <li>
 * 	<strong>Initiate</strong>: 
 * 	<p>Initiate {@link Streamline4CompactedDecisionTable} and {@link Calculation} instances.
 * </li>
 * <li>
 * 	<strong>Compact universe and get a Compacted Table</strong>: 
 * 	<p>Compact universes into a Compacted Decision Table by calling the method 
 * 		{@link CompactedDecisionTableHashAlgorithm.Basic#equivalentClassOfCompactedTable(Collection, 
 * 		IntegerIterator)}
 * </li>
 * <li>
 * 	<strong>Get global positive region and negative region</strong>: 
 * 	<p>Calculate global positive region and negative region (Item {@link Collection}s) bases on the previous
 * 		Compacted Table by C.
 * </li>
 * <li>
 * 	<strong>Call sig to calculate Sig(C)</strong>: 
 * 	<p>Calculate the global significance bases on the previous Compacted Table by C and its Positive/Negative
 * 		region.
 * </li>
 * <li>
 * 	<strong>Get Core</strong>: 
 * 	<p>Calculate Core.
 * 	<p><code>CoreProcedureContainer</code>
 * </li>
 * <li>
 * 	<strong>Calculate red.sig</strong>: 
 * 	<p>Calculate the significance of the reduct(i.e. Core).
 * </li>
 * <li>
 * 	<strong>Sig loop</strong>: 
 * 	<p>Loop and search for the most significant attribute and add as an attribute of the reduct until 
 * 		reaching exit condition.
 * 	<p><code>SignificantAttributeSeekingLoopProcedureContainer</code>
 * </li>
 * <li>
 * 	<strong>Inspection</strong>: 
 * 	<p>Inspect the reduct and remove redundant attributes.
 * 	<p><code>ReductInspectionProcedureContainer</code>
 * </li>
 * 
 * @see {@link CompactedDecisionTableCalculation}
 * @see {@link CompactedDecisionTableHashAlgorithm}
 * 
 * @see {@link CoreProcedureContainer}
 * @see {@link SignificantAttributeSeekingLoopProcedureContainer}
 * @see {@link ReductInspectionProcedureContainer}
 * 
 * @author Benjamin_L
 */
@Slf4j
@RoughSet
public class CompactedDecisionTableHeuristicQRTester<Sig extends Number, DN extends DecisionNumber> 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements TimeSum, 
				ReportMapGenerated<String, Map<String, Object>>,
				HashSearchStrategy,
				StatisticsCalculated,
				CompactedDesicionTableStrategy
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	
	public CompactedDecisionTableHeuristicQRTester(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? log: null, paramaters);
		this.logOn = logOn;
		
		statistics = new HashMap<>();
		report = new HashMap<>();
	}
	
	@Override
	public String shortName() {
		return "QR-CT"+
				"("+ProcedureUtils.ShortName.calculation(getParameters())+")"+
				"("+ProcedureUtils.ShortName.byCore(getParameters())+")";
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
			// 1. Initiate
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("1. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS),
						});
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Class<? extends CompactedDecisionTableCalculation<Sig>> calculationClass = 
								(Class<? extends CompactedDecisionTableCalculation<Sig>>) 
								parameters[p++];
						/* ------------------------------------------------------------------------------ */
						return new Object[] {
							new Streamline4CompactedDecisionTable<>(),
							(CompactedDecisionTableCalculation<Sig>) calculationClass.newInstance(),
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Streamline4CompactedDecisionTable<DN> universeStreamline = 
								(Streamline4CompactedDecisionTable<DN>) 
								result[r++];
						CompactedDecisionTableCalculation<Sig> calculation = 
								(CompactedDecisionTableCalculation<Sig>) 
								result[r++];
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE, universeStreamline);
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE, calculation);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								0
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Initiate"),
			// 2. Compact universe and get a Compacted Table
			new TimeCountedProcedureComponent<Collection<UniverseBasedCompactedTableRecord<DN>>> (
					ComponentTags.TAG_COMPACT,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("2. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_DECISION_NUMBER_CLASS),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Class<DN> decisionNumberClass = (Class<DN>) parameters[0];
						List<UniverseInstance> universes = (List<UniverseInstance>) parameters[1];
						int[] attributes = (int[]) parameters[2];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return CompactedDecisionTableHashAlgorithm
									.Basic
									.universeToCompactedTable(
										decisionNumberClass, 
										universes, 
										attributes
									);
					}, 
					(component, compactedTable) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("compactedTable", compactedTable);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_COMPACTED_SIZE]
						ProcedureUtils.Statistics.countInt(getStatistics(), 
															Constants.STATISTIC_COMPACTED_SIZE, 
															compactedTable.size()
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								compactedTable.size(), 
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
				}.setDescription("Compact universe and get a Compacted Table"),
			// 3. Get global positive region and negative region
			new TimeCountedProcedureComponent<Set<UniverseBasedCompactedTableRecord<DN>>[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("3. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("compactedTable"),
							});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Collection<UniverseBasedCompactedTableRecord<DN>> compactedTable = (Collection<UniverseBasedCompactedTableRecord<DN>>) parameters[0];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return CompactedDecisionTableHashAlgorithm
								.Basic
								.globalPositiveNNegativeRegion(compactedTable);
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("positiveNNegative", result);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						Collection<?> compactedTable = getParameters().get("compactedTable");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								compactedTable.size(), 
								0
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
			}.setDescription("Get global positive region and negative region"),
			// 4. Call sig to calculate Sig(C)
			new TimeCountedProcedureComponent<Sig>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("4. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get("compactedTable"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						CompactedDecisionTableCalculation<Sig> calculation = 
								(CompactedDecisionTableCalculation<Sig>) 
								parameters[p++];
						Collection<UniverseBasedCompactedTableRecord<DN>> compactedTable = 
								(Collection<UniverseBasedCompactedTableRecord<DN>>) 
								parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return (Sig) calculation.calculate(compactedTable, attributes.length, universes.size())
												.getResult();
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("globalSig", result);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "globalSig = {}"), result);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						Collection<?> compactedTable = getParameters().get("compactedTable");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								compactedTable.size(), 
								0
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 4. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Call sig to calculate Sig(C)"),
			// 5. Get Core
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("5. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_QR_BY_CORE),
						});
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						if (parameters[0]!=null && (Boolean) parameters[0]) {
							return (Collection<Integer>) component.getSubProcedureContainers()
																.values()
																.iterator()
																.next()
																.exec();
						}else {
							return new HashSet<>();
						}
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
						/* ------------------------------------------------------------------------------ */
						// Report
						Collection<?> compactedTable = getParameters().get("compactedTable");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								compactedTable.size(), 
								result.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long time = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), time);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 5. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Get Core")
				.setSubProcedureContainer(
					"CoreProcedureContainer", 
					new CoreProcedureContainer<>(getParameters(), logOn)
				),
			// 6. Calculate red.sig.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("6. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get("compactedTable"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						Collection<UniverseBasedCompactedTableRecord<DN>> compactedTable = 
								(Collection<UniverseBasedCompactedTableRecord<DN>>) 
								parameters[p++];
						CompactedDecisionTableCalculation<Sig> calculation = 
								(CompactedDecisionTableCalculation<Sig>) 
								parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 6 newU = equivalentClassCompactedTable(U’, gPOS, gNEG, Red) 
						Collection<EquivalentClassCompactedTableRecord<DN>> equClasses = 
							CompactedDecisionTableHashAlgorithm
								.Basic
								.equivalentClassOfCompactedTable(
										compactedTable, 
										red.isEmpty()? null: new IntegerCollectionIterator(red)
								);
						// * Calculate red.sig.
						return new Object[] {
							(Sig) calculation.calculate(equClasses, red.size(), universes.size())
											.getResult(),
							equClasses
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Sig redSig = (Sig) result[r++];
						Collection<EquivalentClassCompactedTableRecord<DN>> equClasses = 
								(Collection<EquivalentClassCompactedTableRecord<DN>>) result[r++];
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("redSig", redSig);
						getParameters().setNonRootParameter("equClasses", equClasses);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "sig(red) = {}"), result[0]);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_POS_HISTORY]
						List<Sig> posIncrement = (List<Sig>) statistics.get(Constants.STATISTIC_SIG_HISTORY);
						if (posIncrement==null)	statistics.put(Constants.STATISTIC_SIG_HISTORY, posIncrement = new LinkedList<>());
						posIncrement.add(redSig);
						/* ------------------------------------------------------------------------------ */
						// Report
						Collection<?> compactedTable = getParameters().get("compactedTable");
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								compactedTable.size(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)).size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 6. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Calculate red.sig"),
			// 7. Sig loop.
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("7. "+component.getDescription());
					}, 
					(component, parameters) -> {
						return (Collection<Integer>) component.getSubProcedureContainers().values().iterator().next().exec();
					}, 
					(component, red) -> {
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, red);
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|Reduct Candidate| = {}"), red.size());
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						statistics.put(Constants.STATISTIC_RED_BEFORE_INSPECT, red);
						/* ------------------------------------------------------------------------------ */
						// Report
						Collection<EquivalentClassCompactedTableRecord<DN>> equClasses = getParameters().get("equClasses");
						int currentEquClassSize = CompactedDecisionTableOriginalHashAlgorithmReductions
													.compactedRecordSizeOfEquivalentClassRecords(
															(Collection<EquivalentClassCompactedTableRecord<DN>>)
															equClasses
														);
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								currentEquClassSize, 
								red.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long time = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), time);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 7. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Sig loop")
				.setSubProcedureContainer(
					"SignificantAttributeSeekingLoopProcedureContainer", 
					new SignificantAttributeSeekingLoopProcedureContainer<Sig, DN>(getParameters(), logOn)
				),
			// 8. Inspection.
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("8. "+component.getDescription());
					}, 
					(component, parameters) -> {
						return (Collection<Integer>) component.getSubProcedureContainers().values().iterator().next().exec();
					}, 
					(component, red) -> {
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|Reduct Finally| = {}"), red.size());
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						statistics.put(Constants.STATISTIC_RED_AFTER_INSPECT, red);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								red.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long time = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), time);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 8. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspection")
				.setSubProcedureContainer(
					"ReductInspectationProcedureContainer", 
					new ReductInspectionProcedureContainer<Sig, DN>(getParameters(), logOn)
				),
		};
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

	@Override
	public String[] getReportMapKeyOrder() {
		return getComponents().stream().map(ProcedureComponent::getDescription).toArray(String[]::new);
	}
}