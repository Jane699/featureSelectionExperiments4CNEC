package tester.impl.heuristic.quickReduct.dependencyCalculation.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation.HeuristicDependencyCalculationAlgorithm;
import featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation.IncrementalDependencyCalculationAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.heuristicDependencyCalculation.FeatureImportance4HeuristicDependencyCalculation;
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
 * Significant attributes seeking loops for <strong>Heuristic Dependency Calculation (HDC)</strong> Feature 
 * Selection. This procedure contains 4 ProcedureComponents: 
 * <li>
 * 	<strong>Calculate current red dependency</strong>
 * 	<p>Calculate current reduct's dependency value: dep(Red).
 * </li>
 * <li>
 * 	<strong>Check if dep(red)==dep(C), if does, break : true-continue, false-break.</strong>
 * 	<p>Check if current reduct dependency equals to global dependency. If it does, finished searching and 
 * 		break the loop. Otherwise, continue to seek.
 * </li>
 * <li>
 * 	<strong>Seek significant attribute</strong>
 * 	<p>Loop over attributes outside current reduct and seek for the most significant attribute that has the
 * 		max. outer significance.
 * </li>
 * <li>
 * 	<strong>Add significant attribute into red</strong>
 * 	<p>Add the most significant attribute into reduct.
 * </li>
 * <p>
 * Loop is controlled by {@link #exec()}.
 * 
 * @author Benjamin_L
 */
@Slf4j
public class SignificantAttributeSeekingLoopProcedureContainer4HDC<Sig extends Number>
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
	
	public SignificantAttributeSeekingLoopProcedureContainer4HDC(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "QR-HDC - Sig seeking loop";
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
			// 1. Calculate current red dependency.
			new TimeCountedProcedureComponent<Sig>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 1/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get("decisionValues"),
							});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						FeatureImportance4HeuristicDependencyCalculation<Sig> calculation = 
								(FeatureImportance4HeuristicDependencyCalculation<Sig>) 
								parameters[p++];
						Collection<Integer> decisionValues = (Collection<Integer>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return calculation.calculate(universes, decisionValues, new IntegerCollectionIterator(red))
										.getResult();
					}, 
					(component, redDependency)->{
						/* ------------------------------------------------------------------------------ */
						localParameters.put("redDependency", redDependency);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_SIG_HISTORY]
						Collection<Sig> sigHistory = (Collection<Sig>) statistics.get(Constants.STATISTIC_SIG_HISTORY);
						if (sigHistory==null) {
							statistics.put(Constants.STATISTIC_SIG_HISTORY, 
											sigHistory = new LinkedList<>()
										);
						}
						sigHistory.add(redDependency);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						reportKeys.add(reportMark);
						Collection<Integer> red = getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark, 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								red.size()
						);
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
				}.setDescription("Calculate current red dependency"),
			// 2. Check if dep(red)==dep(C), if does, break : true-continue, false-break.
			new TimeCountedProcedureComponent<Boolean>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(1, "Loop {} | 2/{}. {}"), loopCount, getComponents().size(), component.getDescription());
							log.info(LoggerUtil.spaceFormat(2, "deviation = {}"), getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION).toString());
							log.info(LoggerUtil.spaceFormat(2, "dep(C) = {}"), getParameters().get("globalDependency").toString());
							log.info(LoggerUtil.spaceFormat(2, "dep(red) = {}"), localParameters.get("redDependency").toString());
						}
						component.setLocalParameters(new Object[] {
								getParameters().get("globalDependency"),
								localParameters.get("redDependency"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Sig globalSig = (Sig) parameters[p++];
						Sig redSig = (Sig) parameters[p++];
						FeatureImportance4HeuristicDependencyCalculation<Sig> calculation = 
								(FeatureImportance4HeuristicDependencyCalculation<Sig>) 
								parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return !IncrementalDependencyCalculationAlgorithm
								.continueLoopQuickReduct(
									calculation, 
									sigDeviation,
									globalSig, 
									redSig
								);
					}, 
					(component, result)->{
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[continueLoopQuickReduct()]
						ProcedureUtils.Statistics.countInt(getStatistics(), "IncrementalDependencyCalculationAlgorithm.continueLoopQuickReduct()", 1);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters, 
											report, 
											reportMark(), 
											(TimeCountedProcedureComponent<?>) component
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
							
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Check if dep(red)==dep(C), if does, break"),
			// 3. Seek significant attribute.
			new TimeCountedProcedureComponent<Integer>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 3/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								localParameters.get("redDependency"),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								getParameters().get("decisionValues"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						Sig redDependency = (Sig) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						FeatureImportance4HeuristicDependencyCalculation<Sig> calculation = 
								(FeatureImportance4HeuristicDependencyCalculation<Sig>) 
								parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						Collection<Integer> decisionValues = (Collection<Integer>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						int sig = HeuristicDependencyCalculationAlgorithm
									.mostSignificantAttribute(calculation, 
																sigDeviation,
																universes, 
																red, 
																redDependency, 
																attributes,
																decisionValues
									);
						if (sig==-1) throw new RuntimeException("abnormal most significant attribute result : "+sig);
						return sig;
					}, 
					(component, sig)->{
						/* ------------------------------------------------------------------------------ */
						localParameters.put("sig", sig);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						int[] attributes = getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES);
						Collection<Integer> currentRed = getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						int candidateSize = attributes.length-currentRed.size();
						//	[mostSignificantAttribute()]
						ProcedureUtils.Statistics.countInt(getStatistics(), "HeuristicDependencyCalculationAlgorithm.mostSignificantAttribute()", candidateSize);
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
						ProcedureUtils.Report.saveItem(report, reportMark, Constants.REPORT_SIG_HISTORY, sig);
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Seek significant attribute"),
			// 4. Add significant attribut into red.
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 4/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								localParameters.get("sig"),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
						});
					}, 
					(component, parameters) -> {
						int sig = (int) parameters[0];
						Collection<Integer> red = (Collection<Integer>) parameters[1];
						red.add(sig);
						return red;
					}, 
					(component, red)->{
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|red| = {}"), ((Collection<Integer>) red).size());
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters, 
											report, 
											reportMark(), 
											(TimeCountedProcedureComponent<?>) component
						);
					}
				){
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 4. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Add significant attribut into red"),
		};	
	}

	@Override
	public Collection<Integer> exec() throws Exception {
		ProcedureComponent<?>[] comps = initComponents();
		for (ProcedureComponent<?> each : comps)	this.getComponents().add(each);
		
		SigLoop:
		while (true){
			loopCount++;
			for (int i=0; i<comps.length; i++) {
				if (i==1) {
					if ( ! (boolean) comps[i].exec())	break SigLoop;
				}else {
					comps[i].exec();
				}
			}
		}
		// Statistics
		//	[Sig loop times]
		statistics.put(Constants.STATISTIC_SIG_LOOP_TIMES, loopCount);
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