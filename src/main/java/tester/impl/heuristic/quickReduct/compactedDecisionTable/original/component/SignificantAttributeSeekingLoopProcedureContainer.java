package tester.impl.heuristic.quickReduct.compactedDecisionTable.original.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.compactedDecisionTable.original.CompactedDecisionTableHashAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.MostSignificantAttributeResult;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.compactedTable.UniverseBasedCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.equivalentClass.EquivalentClassCompactedTableRecord;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.interf.DecisionNumber;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.compactedDecisionTable.original.CompactedDecisionTableCalculation;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original.Streamline4CompactedDecisionTable;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.compactedDecisionTable.original.StreamlineInput4CompactedDecisionTable;
import featureSelection.utils.compactedDecisionTable.CompactedDecisionTableOriginalHashAlgorithmReductions;
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
 * Most significant attribute searching for <strong>Compacted Decision Table</strong> Feature Selection. 
 * This procedure contains 4 ProcedureComponents: 
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
 * <p>
 * Loop is controlled by {@link #exec()}.
 * 
 * @see {@link Streamline4CompactedDecisionTable}
 * @see {@link StreamlineInput4CompactedDecisionTable}
 * @see {@link CompactedDecisionTableHashAlgorithm.Basic#equivalentClassOfCompactedTable(Collection, IntegerIterator)}
 * 
 * @author Benjamin_L
 */
@Slf4j
public class SignificantAttributeSeekingLoopProcedureContainer<Sig extends Number, DN extends DecisionNumber> 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated, 
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	
	private int loopCount = 0;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private List<String> reportOrder;
	
	private Map<String, Object> localParameters;
	
	public SignificantAttributeSeekingLoopProcedureContainer(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();		
		reportOrder = new LinkedList<>();

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
			// 1. Check if sig(red)==sig(C), if does, break : true-continue, false-break.
			new TimeCountedProcedureComponent<Boolean>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(1, "Loop {} | 1/{}. {}."), loopCount, getComponents().size(), component.getDescription());
							log.info(LoggerUtil.spaceFormat(2, "sig(C) = {} | sig(red) = {}"), 
										String.format(getParameters().get("globalSig") instanceof Integer? "%d":"%.20f", (Number) getParameters().get("globalSig")), 
										String.format(getParameters().get("redSig") instanceof Integer? "%d":"%.20f", (Number) getParameters().get("redSig"))
									);
							log.info(LoggerUtil.spaceFormat(2, "deviation = {}"), getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION).toString());
						}
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
						CompactedDecisionTableCalculation<Sig> calculation = 
								(CompactedDecisionTableCalculation<Sig>) 
								parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return calculation.value1IsBetter(globalSig, redSig, sigDeviation);
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						if (result!=null && !(Boolean) result && logOn)	log.info(LoggerUtil.spaceFormat(2, "break!"));
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						reportOrder.add(reportMark);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(
										localParameters, 
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
				}.setDescription("Check if sig(red)==sig(C)"),
			// 2. filter positive/negative regions : CT => newU.
			new TimeCountedProcedureComponent<Collection<EquivalentClassCompactedTableRecord<DN>>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 2/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE),
								getParameters().get("equClasses"),
								getParameters().get("positiveNNegative"),
						});
						if (localParameters.get("lastUniverseSize")==null) {
							localParameters.put("lastUniverseSize", 
												CompactedDecisionTableOriginalHashAlgorithmReductions
													.universeSizeOfCompactedTableRecords(
															getParameters().get("equClasses")
													)
							);
						}
						if (localParameters.get("lastCompactedSize")==null) {
							localParameters.put("lastCompactedSize", 
												CompactedDecisionTableOriginalHashAlgorithmReductions
													.compactedRecordSizeOfEquivalentClassRecords(
															getParameters().get("equClasses")
													)
							);
						}
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Streamline4CompactedDecisionTable<DN> universeStreamline = (Streamline4CompactedDecisionTable<DN>) parameters[0];
						Collection<EquivalentClassCompactedTableRecord<DN>> equClasses = (Collection<EquivalentClassCompactedTableRecord<DN>>) parameters[1];
						Set<UniverseBasedCompactedTableRecord<DN>>[] positiveNNegative = (Set<UniverseBasedCompactedTableRecord<DN>>[]) parameters[2];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						universeStreamline.streamline(
								new StreamlineInput4CompactedDecisionTable<DN>(
										equClasses, 
										true, positiveNNegative[0], 
										false, positiveNNegative[1]
								)
						);
						return equClasses;
					}, 
					(component, equClasses)->{
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("equClasses", equClasses);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						int lastCompactedSize = (int) localParameters.get("lastCompactedSize");
						int currentEquClassSize = CompactedDecisionTableOriginalHashAlgorithmReductions
														.compactedRecordSizeOfEquivalentClassRecords(
															(Collection<EquivalentClassCompactedTableRecord<DN>>)
															equClasses
														);
						int currentUniverseSize = CompactedDecisionTableOriginalHashAlgorithmReductions
														.universeSizeOfCompactedTableRecords(equClasses);
						//	[STATISTIC_UNIEVRSE_REMOVED]+[STATISTIC_COMPACTED_UNIEVRSE_REMOVED]
						int[] removeInfo = saveStatisticsRemoveUniverseInfo(equClasses, 
													lastCompactedSize, 
													currentEquClassSize, 
													currentUniverseSize
												);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						Collection<Integer> red = getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, 
								reportMark, 
								currentUniverseSize, 
								currentEquClassSize, 
								red.size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(
										localParameters, 
										report, 
										reportMark, 
										(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_UNIVERSE_POS_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, 
								reportMark, 
								Constants.REPORT_UNIVERSE_POS_REMOVE_HISTORY, 
								removeInfo[0]
						);
						//	[REPORT_COMPACTED_UNIVERSE_POS_REMOVE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, 
								reportMark, 
								Constants.REPORT_COMPACTED_UNIVERSE_POS_REMOVE_HISTORY, 
								removeInfo[1]
						);
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "- {} universe(s) | - {} equ class(s)"), 
									removeInfo[0], removeInfo[1]
							);
						}
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("filter positive/negative regions"),
			// 3. Seek significant attribute.
			new TimeCountedProcedureComponent<MostSignificantAttributeResult<Sig, DN>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 3/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("equClasses"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
						localParameters.put("currentEquClassSize", ((Collection<?>) getParameters().get("equClasses")).size());
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<EquivalentClassCompactedTableRecord<DN>> equClasses = 
								(Collection<EquivalentClassCompactedTableRecord<DN>>) 
								parameters[p++];
						CompactedDecisionTableCalculation<Sig> calculation = 
								(CompactedDecisionTableCalculation<Sig>) 
								parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						MostSignificantAttributeResult<Sig, DN> sigResult = 
								CompactedDecisionTableHashAlgorithm
									.mostSignificantAttribute(
											equClasses,
											universes.size(),
											calculation, 
											sigDeviation,
											red, 
											new IntegerArrayIterator(attributes)
									);
						if (sigResult.getAttribute()<0)	throw new Exception("abnormal sig attribute : "+sigResult.getAttribute());
						return sigResult;
					}, 
					(component, result)->{
						/* ------------------------------------------------------------------------------ */
						localParameters.put("sigResult", result);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						int[] attributes = getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES);
						Collection<Integer> currentRed = getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST);
						int candidateSize = attributes.length - currentRed.size();
						int currentEquClassSize = (int) localParameters.get("currentEquClassSize");
						//	[STATISTIC_SORTING]
						ProcedureUtils.Statistics.countInt(statistics, 
															Constants.STATISTIC_SORTING, 
															candidateSize * currentEquClassSize
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(
										localParameters, 
										report, 
										reportMark, 
										(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_REDUCT_MAX_SIG_ATTRIBUTE_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, 
								reportMark(), 
								Constants.REPORT_REDUCT_MAX_SIG_ATTRIBUTE_HISTORY, 
								result.getAttribute()
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Seek significant attribute"),
			// 4. Add significant attribut into red.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Loop {} | 4/{}. {}"), loopCount, getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								localParameters.get("sigResult"),
								getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST),
								
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						MostSignificantAttributeResult<Sig, DN> sigResult = (MostSignificantAttributeResult<Sig, DN>) parameters[0];
						Collection<Integer> red = (Collection<Integer>) parameters[1];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						red.add(sigResult.getAttribute());
						return new Object[] {
								sigResult.getSignificance(), 
								red, 
								sigResult.getEquClasses(),
							};
					}, 
					(component, result)->{
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("redSig", result[0]);
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result[1]);
						getParameters().setNonRootParameter("equClasses", result[2]);
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(2, "+ attribute {} | redSig = {}"), 
										((MostSignificantAttributeResult<Sig, DN>) localParameters.get("sigResult")).getAttribute(), 
										result[0]
									);
						}
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_POS_HISTORY]
						List<Sig> posIncrement = (List<Sig>) statistics.get(Constants.STATISTIC_SIG_HISTORY);
						if (posIncrement==null)	statistics.put(Constants.STATISTIC_SIG_HISTORY, posIncrement = new LinkedList<>());
						posIncrement.add((Sig) result[0]);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = reportMark();
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(
										localParameters, 
										report, 
										reportMark, 
										(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_POSITIVE_INCREMENT_HISTORY]
						ProcedureUtils.Report.saveItem(
								report, 
								reportMark(), 
								Constants.REPORT_SIG_HISTORY, 
								(Sig) result[0]
						);
						/* ------------------------------------------------------------------------------ */
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
		for (ProcedureComponent<?> each : comps) {
			getComponents().add(each);
		}

		SigLoop:
		while (true){
			loopCount++;
			for (int i=0; i<comps.length; i++) {
				if (i==0) {
					if (!(boolean) comps[i].exec())	break SigLoop;
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
		return reportOrder.toArray(new String[reportOrder.size()]);
	}
	

	private String reportMark() {
		return "Loop["+loopCount+"]";
	}
	
	/**
	 * Save Statistics of [STATISTIC_COMPACTED_UNIEVRSE_REMOVED]+[STATISTIC_UNIEVRSE_REMOVED].
	 * <p>Update {@link #localParameters}: {<code>lastCompactedSize</code>: <code>currentEquClassSize</code>}
	 * <p>Update {@link #localParameters}: {<code>lastUniverseSize</code>: <code>currentUniverseSize</code>}
	 * 
	 * @param equClasses
	 * 		An {@link EquivalentClassCompactedTableRecord} {@link Collection}.
	 * @param lastCompactedSize
	 * 		Size of compacted universe at last round.
	 * @param currentEquClassSize
	 * 		Size of compacted universe at current round.
	 * @param currentUniverseSize
	 * 		Size of universe at current round.
	 * @return <code>lastUniverseSize - currentUniverseSize</code> as size of universe removed 
	 * 		at the current round.
	 */
	@SuppressWarnings("unchecked")
	private int[] saveStatisticsRemoveUniverseInfo(
			Collection<EquivalentClassCompactedTableRecord<DN>> equClasses,
			int lastCompactedSize, int currentEquClassSize, int currentUniverseSize
	) {
		//	[STATISTIC_COMPACTED_UNIEVRSE_REMOVED]
		List<Integer> universeRemove = (List<Integer>) statistics.get(Constants.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED);
		if (universeRemove==null)	statistics.put(Constants.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED, universeRemove = new LinkedList<>());
		universeRemove.add(lastCompactedSize - currentEquClassSize);
		localParameters.put("lastCompactedSize", currentEquClassSize);
		
		//	[STATISTIC_UNIEVRSE_REMOVED]
		int lastUniverseSize = (int) localParameters.get("lastUniverseSize");
		universeRemove = (List<Integer>) statistics.get(Constants.STATISTIC_POS_UNIEVRSE_REMOVED);
		if (universeRemove==null)	statistics.put(Constants.STATISTIC_POS_UNIEVRSE_REMOVED, universeRemove = new LinkedList<>());
		universeRemove.add(lastUniverseSize - currentUniverseSize);
		localParameters.put("lastUniverseSize", currentUniverseSize);
		return new int[] {
				lastUniverseSize - currentUniverseSize,
				lastCompactedSize - currentEquClassSize
		};
	}
}