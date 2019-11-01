package tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.roughEquivalentClassBased.RoughEquivalentClassBasedExtensionAlgorithm.IncrementalDecision.Basic;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl.extension.decisionMap.RoughEquivalentClassDecisionMapExtension;
import featureSelection.repository.implement.model.algStrategyRepository.rec.extension.incrementalDecision.MostSignificantAttributeResult;
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
public class SignificantAttributeSeekingLoopProcedureContainer<Sig extends Number> 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	
	private int loopCount = 0;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;
	
	public SignificantAttributeSeekingLoopProcedureContainer(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
	
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Sig seeking loop";
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
			// 1. Check if break loop : true-continue, false-break.
			new TimeCountedProcedureComponent<Boolean>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 1/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("globalSig"),
								getParameters().get("redSig"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Sig globalSig = (Sig) parameters[p++];
						Sig redSig = (Sig) parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						FeatureImportance<Sig> calculation = (FeatureImportance<Sig>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return calculation.value1IsBetter(globalSig, redSig, sigDeviation);
					}, 
					(component, result)->{
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "globalSig = {} | redSig = {}"),
										String.format((getParameters().get("globalSig") instanceof Double?"%.20f":"%d"), ((Number) getParameters().get("globalSig"))),
										String.format((getParameters().get("redSig") instanceof Double?"%.20f":"%d"), ((Number) getParameters().get("redSig")))
									);
							if (result==null || !result.booleanValue())
								log.info(LoggerUtil.spaceFormat(2, "break!"));
						}
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						reportKeys.add(reportMark);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters, 
											report, 
											reportMark, 
											(TimeCountedProcedureComponent<?>) component
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
							
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Check if break loop"),
			// 2. Seek the most significant current attribute.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 2/{}. {}."), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("roughClasses"),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
							});
						if (localParameters.get("lastUniverseSize")==null) {
							localParameters.put(
								"lastUniverseSize", 
								RoughEquivalentClassBasedReductions.countUniverseSize(getParameters().get("roughClasses"))
							);
						}
						if (localParameters.get("lastEquClassSize")==null) {
							localParameters.put(
								"lastEquClassSize", 
								RoughEquivalentClassBasedReductions.countEquivalentClassSize(getParameters().get("roughClasses"))
							);
						}
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int removeNegUniverse = 0, removeNegEquClass = 0;
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> roughClasses = (Collection<RoughEquivalentClassDecisionMapExtension<Sig>>) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation = 
								(RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>) 
								parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig> universeStreamline = 
							(UniverseStreamline4RoughEquivalentClassBasedDecisionMapExt<Sig>) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 sig=0
						// 2 a*=0
						int sigAttr=-1;
						// 3 global_sig_static=0
						Sig redSig, staticSig, sigSum;
						Sig maxRedSig = null, maxStaticSig = null, maxSigSum = null;
						// 4 S = null
						// 5 Go through a in C-Red
						StreamlineResult4RECIncrementalDecisionExtension<Sig> streamlineResult;
						Collection<RoughEquivalentClassDecisionMapExtension<Sig>> sigRoughClasses = null, 
																					incrementalRoughClasses;
						for (int attr : attributes) {
							if (red.contains(attr))	continue;
							// 6 a_U = EC_Table
							// 7 a_U = roughEquivalentClass(a_U, a)
							incrementalRoughClasses = Basic.incrementalPartitionRoughEquivalentClass(
															roughClasses, 
															new IntegerArrayIterator(attr)
														);
							// 8 a_U, sig_static = streamline(a_U, sig calculation)
							streamlineResult = universeStreamline.streamline(
													new StreamlineInput4RECIncrementalDecisionExtension<Sig>(
															incrementalRoughClasses, 1
													)
												);
							incrementalRoughClasses = streamlineResult.getRoughClasses();
							staticSig = streamlineResult.getRemovedUniverseSignificance();//*/
							// 9 a.outerSig = sig calculation(a_U)
							calculation.calculate(incrementalRoughClasses, 1, universes.size());
							redSig = calculation.getResult();
							// 10 if (a.outerSig > sig)
							sigSum = calculation.plus(redSig, staticSig);
							if (maxSigSum==null || 
								calculation.value1IsBetter(sigSum, maxSigSum, sigDeviation)
							) {
								//	11 a*=a
								sigAttr = attr;
								//	12 sig=a.outerSig
								maxRedSig = redSig;
								//	13 global_sig_static = sig_static
								maxStaticSig = staticSig;
								maxSigSum = sigSum;
								//	14 S = a_U
								sigRoughClasses = incrementalRoughClasses;
								
								removeNegUniverse = streamlineResult.getRemovedNegativeSizeInfo()[0];
								removeNegEquClass = streamlineResult.getRemovedNegativeSizeInfo()[1];
							}
						}
						return new Object[] {
								new MostSignificantAttributeResult<>(maxRedSig, maxStaticSig, sigAttr, sigRoughClasses),
								removeNegUniverse,
								removeNegEquClass,
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r = 0;
						MostSignificantAttributeResult<Sig> sig = (MostSignificantAttributeResult<Sig>) result[r++];
						int removeNegUniverse = (int) result[r++];
						int removeNegEquClass = (int) result[r++];
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("sig", sig);
						this.getParameters().setNonRootParameter("roughClasses", sig.getRoughClasses());
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "+ attribute = {} | significance = {}"), 
									sig.getAttribute(), sig.getSignificance());
						}
						/* ------------------------------------------------------------------------------ */
						// Statistic
						int lastUniverseSize = (int) localParameters.get("lastUniverseSize");
						int lastEquClassSize = (int) localParameters.get("lastEquClassSize");
						//	[STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED]
						int currentEquClassSize = RoughEquivalentClassBasedReductions.countEquivalentClassSize(sig.getRoughClasses());
						List<Integer> universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						universeRemoved.add(lastEquClassSize - currentEquClassSize - removeNegEquClass);
						//	[STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED]
						universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						universeRemoved.add(removeNegEquClass);
						localParameters.put("lastEquClassSize", currentEquClassSize);
						//	[STATISTIC_POS_UNIEVRSE_REMOVED]
						int currentUniverseSize = RoughEquivalentClassBasedReductions.countUniverseSize(sig.getRoughClasses());
						universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_POS_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_POS_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						universeRemoved.add(lastUniverseSize - currentUniverseSize - removeNegUniverse);
						//	[STATISTIC_NEG_UNIEVRSE_REMOVED]
						universeRemoved = (List<Integer>) statistics.get(Constants.STATISTIC_NEG_UNIEVRSE_REMOVED);
						if (universeRemoved==null) {
							statistics.put(Constants.STATISTIC_NEG_UNIEVRSE_REMOVED, 
											universeRemoved = new LinkedList<>()
										);
						}
						universeRemoved.add(removeNegUniverse);
						localParameters.put("lastUniverseSize", currentUniverseSize);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark, 
								currentUniverseSize, 
								currentEquClassSize, 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)).size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters,
											report, 
											reportMark, 
											(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_REDUCT_MAX_SIG_ATTRIBUTE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_REDUCT_MAX_SIG_ATTRIBUTE_HISTORY, 
								sig.getAttribute()
						);
						//	[REPORT_UNIVERSE_POS_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_UNIVERSE_POS_REMOVE_HISTORY, 
								lastUniverseSize - currentUniverseSize - removeNegUniverse
						);
						//	[REPORT_UNIVERSE_NEG_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_UNIVERSE_NEG_REMOVE_HISTORY, 
								removeNegUniverse
						);
						//	[REPORT_COMPACTED_UNIVERSE_POS_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_COMPACTED_UNIVERSE_POS_REMOVE_HISTORY, 
								lastEquClassSize - currentEquClassSize - removeNegEquClass
						);
						//	[REPORT_COMPACTED_UNIVERSE_NEG_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_COMPACTED_UNIVERSE_NEG_REMOVE_HISTORY, 
								removeNegEquClass
						);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "- {} universe(s)"), lastUniverseSize - currentUniverseSize);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}

					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Seek the most significant current attribute"),
			// 3. Update reduct and significance.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 3/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("sig"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get("staticSig"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						MostSignificantAttributeResult<Sig> sig = (MostSignificantAttributeResult<Sig>) parameters[p++];
						RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig> calculation = 
								(RoughEquivalentClassBasedIncrementalDecisionExtensionCalculation<Sig>)
								parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						Sig staticSig = (Sig) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						red.add(sig.getAttribute());
						staticSig = calculation.plus(staticSig, sig.getGlobalStaticSiginificance());
						return new Object[] {
								red,
								staticSig,
								calculation.plus(sig.getSignificance(), staticSig),
						};
					}, 
					(component, result)->{
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<Integer> red = (Collection<Integer>) result[r++];
						Sig staticSig = (Sig) result[r++];
						Sig redSig = (Sig) result[r++];
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, red);
						if (result[1]!=null)	getParameters().setNonRootParameter("staticSig", staticSig);
						getParameters().setNonRootParameter("redSig", redSig);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "staticSig = {}| sig = {}"), result[1], result[2]);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_SIG_HISTORY]
						List<Sig> posHistory = (List<Sig>) statistics.get(Constants.STATISTIC_SIG_HISTORY);
						if (posHistory==null)	statistics.put(Constants.STATISTIC_SIG_HISTORY, posHistory = new LinkedList<>());
						posHistory.add(redSig);
						//	[calculation.plus]
						ProcedureUtils.Statistics.countInt(statistics, 
															"calculation.plus(sig_static, new_sig_static)", 
															1
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters, 
											report, 
											reportMark, 
											(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_SIG_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_SIG_HISTORY, 
								redSig
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
							
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Update reduct and significance"),
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Integer> exec() throws Exception {
		localParameters.put("lastUniverseSize", getParameters().get("lastUniverseSize"));
		localParameters.put("lastEquClassSize", getParameters().get("lastEquClassSize"));
		
		if (Integer.compare(
			((Collection<Integer>) getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)).size(), 
			((int[]) getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES)).length
			)!=0
		) {
			ProcedureComponent<?>[] comps = initComponents();
			for (ProcedureComponent<?> each : comps)	this.getComponents().add(each);
			
			SigLoop:
			while (true) {
				loopCount++;
				for (int i=0; i<comps.length; i++) {
					if (i==0) {
						if (!(Boolean) comps[i].exec())	break SigLoop;
					}/*else if (i==3) {
						if ((Boolean) comps[i].exec())	break SigLoop;
					}*/else {
						comps[i].exec();
					}
				}
			}
			
			// Statistics
			//	[Sig loop times]
			statistics.put(Constants.STATISTIC_SIG_LOOP_TIMES, loopCount);
		}
		return this.getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
	}

	public String reportMark() {
		return "Loop["+loopCount+"]";
	}
	
	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}