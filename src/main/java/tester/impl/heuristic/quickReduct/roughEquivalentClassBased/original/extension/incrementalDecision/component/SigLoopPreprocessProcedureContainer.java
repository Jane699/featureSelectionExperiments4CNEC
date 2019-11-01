package tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.roughEquivalentClassBased.RoughEquivalentClassBasedExtensionAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.EquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.roughEquivalentClassBased.RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.StreamlineInput4RECIncrementalDecisionExtension;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.StreamlineResult4RECIncrementalDecisionExtension;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.roughEquivalentClassBased.extension.incrementalDecision.UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt;
import featureSelection.utils.RoughEquivalentClassBasedReductions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

@Slf4j
public class SigLoopPreprocessProcedureContainer<Sig extends Number> 
	extends DefaultProcedureContainer<Collection<RoughEquivalentClassDecisionMapExtension<Sig>>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;

	public SigLoopPreprocessProcedureContainer(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? log: null, paramaters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new ArrayList<>(1);
		
		localParameters = new HashMap<>();
	}

	@Override
	public String staticsName() {
		return shortName();
	}

	@Override
	public String shortName() {
		return "Sig loop pre-process.";
	}

	@Override
	public String reportName() {
		return shortName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ProcedureComponent<?>[] initComponents() {
		return new ProcedureComponent<?>[] {
			// *. Seek significant attributes pre-process procedure controller.
			new ProcedureComponent<Collection<RoughEquivalentClassDecisionMapExtension<Sig>>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "*. "+component.getDescription()));
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Collection<Integer> red = (Collection<Integer>) getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						ProcedureComponent<Object[]> component1 = (ProcedureComponent<Object[]>) getComponents().get(1);
						ProcedureComponent<Object[]> component2 = (ProcedureComponent<Object[]>) getComponents().get(2);
						/* ------------------------------------------------------------------------------ */
						return red.isEmpty()? 
								(Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) 
								((Object[]) component2.exec())[0]:
								(Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) 
								((Object[]) component1.exec())[0];
					}, 
					(component, roughClasses) -> {
						this.getParameters().setNonRootParameter("roughClasses", roughClasses);
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | *. "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Seek significant attributes pre-process procedure controller"),
			// 1. Sort Rough Equivalent Class using core.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "1. "+component.getDescription()));
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
						localParameters.put(
								"lastUniverseSize", 
								((Collection<UniverseInstance>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size()
						);
						localParameters.put(
								"lastEquClassSize", 
								((Collection<EquivalentClassDecisionMapExtension<Sig>>) getParameters().get("equClasses")).size()
						);
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses = 
								(Collection<EquivalentClassDecisionMapExtension<Sig>>) 
								parameters[p++];
						UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig> universeStreamline = 
								(UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig>) 
								parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation =	
								(RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>) 
								parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						StreamlineResult4RECIncrementalDecisionExtension<Sig> streamlineResult;
						// 7 if Red != null
						// 8 newU = roughEquivelentClass(EC_Table, C)
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = 
								RoughEquivalentClassBasedExtensionAlgorithm
									.IncrementalDecision
									.Basic
									.roughEquivalentClass(equClasses, new IntegerCollectionIterator(red));
						// 9 newU, sig_static = streamline(newU, sig calculation)
						streamlineResult = universeStreamline.streamline(
												new StreamlineInput4RECIncrementalDecisionExtension<Sig>(
													roughClasses, red.size()
												)
											);
						roughClasses = streamlineResult.getRoughClasses();
						// 10 sig(Red)=sig calculation(newU)
						calculation.calculate(roughClasses, red.size(), universes.size());
						return new Object[] {
								roughClasses,
								streamlineResult.getRemovedUniverseSignificance(),
								calculation.plus(calculation.getResult(), streamlineResult.getRemovedUniverseSignificance()),
								calculation,
								streamlineResult.getRemovedNegativeSizeInfo(),
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = (Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) result[r++];
						Sig staticSig = (Sig) result[r++];
						Sig redSig = (Sig) result[r++];
						FeatureImportance<Sig> calculation = (FeatureImportance<Sig>) result[r++];
						int currentUniverseSize = RoughEquivalentClassBasedReductions.countUniverseSize(roughClasses);
						int currentEquClassSize = RoughEquivalentClassBasedReductions.countEquivalentClassSize(roughClasses);
						int[] removedNegInfo = (int[]) result[r++];
						int removedNegUniverse = removedNegInfo[0],
							removedNegEquClass = removedNegInfo[1];
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("roughClasses", result[0]);
						if (staticSig!=null)	this.getParameters().setNonRootParameter("staticSig", staticSig);
						this.getParameters().setNonRootParameter("redSig", redSig);
						this.getParameters().setNonRootParameter("calculation", calculation);
						this.getParameters().setNonRootParameter("lastUniverseSize", currentUniverseSize);
						this.getParameters().setNonRootParameter("lastEquClassSize", currentEquClassSize);
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "Static Sig = {} | Red sig = {}"), 
									result[1], result[2]);
						}
						/* ------------------------------------------------------------------------------ */
						// Statistics
						reportKeys.add(component.getDescription());
						//	[STATISTIC_SORTING]
						ProcedureUtils.Statistics.countInt(statistics, 
															Constants.STATISTIC_SORTING, 
															1
														);
						//	[roughEquivalentClass()]
						ProcedureUtils.Statistics.countInt(statistics, 
															"RoughEquivalentClassBasedExtensionAlgorithm.IncrementalDecision.Basic.roughEquivalentClass()", 
															1
														);
						//	[STATISTIC_POS_HISTORY]
						List<Sig> posHistory = (List<Sig>) statistics.get(Constants.STATISTIC_SIG_HISTORY);
						if (posHistory==null)	statistics.put(Constants.STATISTIC_SIG_HISTORY, posHistory=new LinkedList<>());
						posHistory.add(redSig);
						//	[calculation.plus]
						ProcedureUtils.Statistics.countInt(statistics, 
															"calculation.plus(sig_static, new_sig_static)", 
															1
														);
						//	[universeStreamline.streamline()]
						ProcedureUtils.Statistics.countInt(statistics, 
															"UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt.streamline()", 
															1
														);
						//	[STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED]
						List<Integer> universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						int lastEquClassSize = (int) localParameters.get("lastEquClassSize");
						universeRemoved.add(lastEquClassSize - currentEquClassSize - removedNegEquClass);
						//	[STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED]
						universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						universeRemoved.add(removedNegEquClass);
						//	[STATISTIC_POS_UNIEVRSE_REMOVED]
						universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_POS_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_POS_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						int lastUniverseSize = (int) localParameters.get("lastUniverseSize");
						universeRemoved.add(lastUniverseSize - currentUniverseSize - removedNegUniverse);
						//	[STATISTIC_NEG_UNIEVRSE_REMOVED]
						universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_NEG_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_NEG_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						universeRemoved.add(removedNegUniverse);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								currentUniverseSize, 
								currentEquClassSize, 
								((Collection<Integer>) getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)).size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						//	[REPORT_UNIVERSE_POS_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_UNIVERSE_POS_REMOVE_HISTORY, 
								lastUniverseSize - currentUniverseSize - removedNegUniverse
						);
						//	[REPORT_COMPACTED_UNIVERSE_POS_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_COMPACTED_UNIVERSE_POS_REMOVE_HISTORY, 
								lastEquClassSize - currentEquClassSize - removedNegEquClass
						);
						//	[REPORT_UNIVERSE_NEG_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_UNIVERSE_NEG_REMOVE_HISTORY, 
								removedNegUniverse
						);
						//	[REPORT_COMPACTED_UNIVERSE_NEG_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_COMPACTED_UNIVERSE_NEG_REMOVE_HISTORY, 
								removedNegEquClass
						);
						//	[REPORT_POSITIVE_INCREMENT_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_SIG_HISTORY, 
								redSig
						);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "- {} universe(s)"), lastUniverseSize - currentUniverseSize);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Sort Rough Equivalent Class using core"),
			// 2. Get Rough Equivalent Class of empty reduct.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "2. "+component.getDescription()));
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<EquivalentClassDecisionMapExtension<Sig>> equClasses = (Collection<EquivalentClassDecisionMapExtension<Sig>>) parameters[p++];
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation = 
								(RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>) 
								parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						RoughEquivalentClassDecisionMapExtension<Sig> roughClass = new RoughEquivalentClassDecisionMapExtension<>();
						for (EquivalentClassDecisionMapExtension<Sig> equ: equClasses)	roughClass.addClassItem(equ);
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = new HashSet<>();	
						roughClasses.add(roughClass);
						// 10 sig(Red)=sig calculation(newU)
						calculation.calculate(roughClasses, 0, universes.size());
						return new Object[] {
								roughClasses,
								calculation,
								calculation.getResult(),
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = (Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) result[r++];
						FeatureImportance<Sig> calculation = (FeatureImportance<Sig>) result[1];
						Sig redSig = (Sig) result[2];
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("roughClasses", roughClasses);
						this.getParameters().setNonRootParameter("calculation", calculation);
						this.getParameters().setNonRootParameter("redSig", redSig);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "redSig = {}"), redSig);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_SORTING]
						ProcedureUtils.Statistics.countInt(statistics, 
															Constants.STATISTIC_SORTING, 
															1
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						reportKeys.add(component.getDescription());
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								((Collection<?>) getParameters().get("equClasses")).size(), 
								((Collection<Integer>) getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)).size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						//	[REPORT_POSITIVE_INCREMENT_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, component.getDescription(), 
								Constants.REPORT_SIG_HISTORY, 
								redSig
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
				@Override public void init() {}
					
				@Override public String staticsName() {
					return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
				}
			}.setDescription("Get Rough Equivalent Class of empty reduct"),
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<RoughEquivalentClassDecisionMapExtension<Sig>> exec() throws Exception {
		ProcedureComponent<?>[] componentArray = initComponents();
		for (ProcedureComponent<?> each : componentArray)		this.getComponents().add(each);
		return (Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) componentArray[0].exec();
	}

	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}
