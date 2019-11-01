package tester.impl.heuristic.quickReduct.positiveApproximationAccelerator.original.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.MostSignificantAttributeResult;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original.Streamline4CompactedDecisionTable;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original.StreamlineInput4CompactedDecisionTable;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.positiveApproximationAccelerator.original.Streamline4PositiveApproximationAccelerator;
import featureSelection.utils.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalReductions;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
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
 * Most significant attribute searching for <strong>Positive Approximation Accelerator (ACC)</strong> 
 * Feature Selection.
 * <p>
 * This procedure contains 5 ProcedureComponents: 
 * <li>
 * 	<strong>Check if sig(red)==sig(C), if does, break : true-continue, false-break</strong>
 * 	<p>Check if significance of current reduct equals to the global significance(i.e. <code>sig(red)==sig(C)
 * 		</code>). Return <code>true</code> if it does. 
 * </li>
 * <li>
 * 	<strong>filter positive/negative regions : CT => newU</strong>
 * 	<p>Filter positive regions and negative regions to streamline {@link UniverseInstance}s by calling  
 * 		{@link Streamline4CompactedDecisionTable#streamline(StreamlineInput4CompactedDecisionTable)}
 * </li>
 * <li>
 * 	<strong>Seek significant attribute</strong>
 * 	<p>Search for the attribute with the max. significance value in the rest of the attributes(i.e. 
 * 		attributes outside the reduct), and return as an attribute of the reduct.
 * </li>
 * <li>
 * 	<strong>Add significant attribut into red</strong>
 * 	<p>Add the most significant attribute into the reduct.
 * </li>
 * <li>
 * 	<strong>Update equivalent class</strong>
 * 	<p>Use the reduct to partition {@link UniverseInstance} {@link Collection} for update.
 * </li>
 * <p>
 * Loop is controlled by {@link #exec()}.
 * 
 * @see {@link Streamline4PositiveApproximationAccelerator}
 * 
 * @author Benjamin_L
 */
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
		super(parameters);
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
			// 1. Check if break the loop: true-break / false-continue.
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
						Sig globalSig = (Sig) parameters[0];
						Sig redSig = (Sig) parameters[1];
						Sig sigDeviation = (Sig) parameters[2];
						FeatureImportance<Sig> calculation = (FeatureImportance<Sig>) parameters[3];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// true-break / false-continue
						return !calculation.value1IsBetter(globalSig, redSig, sigDeviation);
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "globalSig = {} | redSig = {}"),
										getParameters().get("globalSig"),
										getParameters().get("redSig")
									);
							log.info(LoggerUtil.spaceFormat(2, "deviation = {}"), 
										getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION).toString()
									);
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
				}.setDescription("Check if break the loop."),
			// 2. Filter positive/negative regions.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 2/{}. {}."), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						Collection<EquivalentClass> equClasses = (Collection<EquivalentClass>) parameters[0];
						Streamline4PositiveApproximationAccelerator universeStreamline = (Streamline4PositiveApproximationAccelerator) parameters[1];
						/* ------------------------------------------------------------------------------ */
						int[] removeInfo = universeStreamline.streamline(equClasses);
						return new Object[] {
								equClasses,
								removeInfo[0],
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<EquivalentClass> equClasses = (Collection<EquivalentClass>) result[r++];
						int removeUniverse = (int) result[r++];
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("equClasses", equClasses);
						/* ------------------------------------------------------------------------------ */
						// Statistic
						//	[filterPositiveRegion()]
						countFilterPositiveRegion(1);
						//	[STATISTIC_UNIEVRSE_REMOVED]
						List<Integer> removedUniverse = (List<Integer>) statistics.get(Constants.STATISTIC_POS_UNIEVRSE_REMOVED);
						if (removedUniverse==null)	statistics.put(Constants.STATISTIC_POS_UNIEVRSE_REMOVED, removedUniverse = new LinkedList<>());
						removedUniverse.add(removeUniverse);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						Collection<Integer> red = getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						//	[DatasetRealTimeInfo]
						int currentUniverseSize = PositiveApproximationAcceleratorOriginalReductions
														.universeSize(equClasses);
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark, 
								currentUniverseSize, 
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
						//	[REPORT_UNIVERSE_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_UNIVERSE_POS_REMOVE_HISTORY, 
								removeUniverse
						);
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "- {} universe(s)"), removeUniverse);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Filter positive/negative regions"),
			// 3. Seek sig attribute.
			new TimeCountedProcedureComponent<MostSignificantAttributeResult<Sig>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 3/{}. {}."), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
							});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<EquivalentClass> equClasses = (Collection<EquivalentClass>) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						PositiveApproximationAcceleratorCalculation<Sig> calculation = 
								(PositiveApproximationAcceleratorCalculation<Sig>) 
								parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return PositiveApproximationAcceleratorOriginalAlgorithm
									.mostSignificantAttribute(
											equClasses, 
											red, 
											attributes, 
											universes.size(),
											calculation, 
											sigDeviation
									);
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						MostSignificantAttributeResult<Sig> sig = (MostSignificantAttributeResult<Sig>) result;
						getParameters().setNonRootParameter("sig", sig);
						getParameters().setNonRootParameter("redSig", sig.getMaxSig());
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "+ attribute {} | pos = {}"), 
									sig.getAttribute(), 
									sig.getMaxSig()
							);
						}
						/* ------------------------------------------------------------------------------ */
						// Statistic
						int[] attributes = getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES);
						Collection<Integer> red = getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						Collection<EquivalentClass> equClasses = getParameters().get("equClasses");
						//	[STATISTIC_SORTING]
						ProcedureUtils.Statistics.countInt(getStatistics(), Constants.STATISTIC_SORTING, equClasses.size()*(attributes.length-red.size()));
						//	[equivalentClass()]
						countEquivalentClass(equClasses.size()*(attributes.length-red.size()));
						//	[STATISTIC_POS_HISTORY]
						List<Sig> increment = (List<Sig>) statistics.get(Constants.STATISTIC_SIG_HISTORY);
						if (increment==null)	statistics.put(Constants.STATISTIC_SIG_HISTORY, increment = new LinkedList<>());
						increment.add(sig.getMaxSig());
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
						//	[REPORT_POSITIVE_INCREMENT_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_SIG_HISTORY, 
								sig.getMaxSig()
						);
						//	[REPORT_REDUCT_MAX_SIG_ATTRIBUTE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_REDUCT_MAX_SIG_ATTRIBUTE_HISTORY, 
								sig.getAttribute()
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}

					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Seek sig attribute"),
			// 4. Add sig attribute into red.
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 4/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
									getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
									getParameters().get("sig"),
							});
					},
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Collection<Integer> red = (Collection<Integer>) parameters[0];
						MostSignificantAttributeResult<Sig> sig = (MostSignificantAttributeResult<Sig>) parameters[1];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						if (sig.getAttribute()==-1)	
							throw new Exception("abnormal most significant attribute result : "+sig.getAttribute());
						else			
							red.add(sig.getAttribute());
						return red;
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
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
						return shortName()+" | 4. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Add sig attribute into red"),
			// 5. Update equivalent class.
			new TimeCountedProcedureComponent<Collection<EquivalentClass>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 5/{}. {}."), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("sig"),
							});
					},
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						MostSignificantAttributeResult<Sig> sig = (MostSignificantAttributeResult<Sig>) parameters[0];
						/* ------------------------------------------------------------------------------ */
						return sig.getEquClasses();
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter("equClasses", result);
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
						return shortName()+" | 5. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Update equivalent class"),
		};
	}

	@Override
	public Collection<Integer> exec() throws Exception {
		ProcedureComponent<?>[] comps = initComponents();
		for (ProcedureComponent<?> each : comps) {
			this.getComponents().add(each);
			//reportKeys.add(each.getDescription());
		}
		ComponentLoop:
		while (true) {
			loopCount++;
			for (int i=0; i<this.getComponents().size(); i++) {
				if (i==0) {
					if (((Boolean) comps[i].exec()))	break ComponentLoop;
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

	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
	
	private String reportMark() {
		return "Loop["+loopCount+"]";
	}
		
	private void countEquivalentClass(int countTime) {
		ProcedureUtils.Statistics.countInt(getStatistics(), "PositiveApproximationAcceleratorOriginalAlgorithm.Basic.equivalentClass()", countTime);
	}
	
	private void countFilterPositiveRegion(int countTime) {
		ProcedureUtils.Statistics.countInt(getStatistics(), "PositiveApproximationAcceleratorOriginalAlgorithm.Basic.filterPositiveRegion()", countTime);
	}
}