package tester.impl.heuristic.quickReduct.dependencyCalculation.component;

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
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.incrementalDependencyCalculation.FeatureImportance4IncrementalDependencyCalculation;
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
 * Reduct inspection for <strong>Incremental Dependency Calculation (IDC)</strong> Feature Selection.
 * This procedure contains 2 ProcedureComponents: 
 * <li>
 * 	<strong>Inspection procedure controller</strong>
 * 	<p>Control loop over attributes of reduct, and calculate their inner significance. If removing the 
 * 		attribute from the feature subset doesn't have any effect on the significance(i.e 
 * 		<code>dep(Red-{a})==dep(Red)</code>), it is REDUNDANT. 
 * </li>
 * <li>
 * 	<strong>Inspect reduct redundancy</strong>
 * 	<p>Calculate the inner significance of the attribute by removing it and determine if it is redundant.
 * </li>
 * 
 * @author Benjamin_L
 */
@Slf4j
public class ReductInspectionProcedureContainer4IDC<Sig>
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	
	private Map<String, Object> localParameters;
	private int loopCount;
	
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private List<String> reportKeys;
	
	public ReductInspectionProcedureContainer4IDC(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		
		localParameters = new HashMap<>();
		
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
	}

	@Override
	public String shortName() {
		return "Inspect reduct";
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
			// 1. Inspection procedure controller
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "1/{}. {}"), getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								new HashSet<>(getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						FeatureImportance4IncrementalDependencyCalculation<Sig> calculation = (FeatureImportance4IncrementalDependencyCalculation<Sig>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						ProcedureComponent<Object[]> comp1 = (ProcedureComponent<Object[]>) getComponents().get(1);
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 Go through a in R
						Integer[] redCopy = red.toArray(new Integer[red.size()]);
						Sig globalDependency = calculation.calculate(universes, new IntegerCollectionIterator(red))
															.getResult();
						for (int attr: redCopy) {
							TimerUtils.timePause((TimeCounted) component);
							
							loopCount++;
							localParameters.put("globalDependency", globalDependency);
							localParameters.put("examAttr", attr);
							localParameters.put("red", red);
							comp1.exec();
							red = (Collection<Integer>) localParameters.get("red");
							
							TimerUtils.timeContinue((TimeCounted) component);
						}
						return red;
					}, 
					(component, red) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST_AFTER_INSPECTATION, red);
						/* ------------------------------------------------------------------------------ */
						// Statistics
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
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspection procedure controller"),
			// 2. Inspect reduct redundancy
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component)->{
						//if (logOn)	log.info(LoggerUtil.spaceFormat(1, "2/{}. {}"), getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								localParameters.get("red"),
								localParameters.get("globalDependency"),
								localParameters.get("examAttr"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						boolean attributeIsRedundant = false;
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						Sig globalDependency = (Sig) parameters[p++];
						int examAttr = (int) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						FeatureImportance4IncrementalDependencyCalculation<Sig> calculation = (FeatureImportance4IncrementalDependencyCalculation<Sig>) parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						red.remove(examAttr);
						Sig examDependency = calculation.calculate(universes, new IntegerCollectionIterator(red))
															.getResult();
						if (calculation.value1IsBetter(globalDependency, examDependency, sigDeviation)) {
							red.add(examAttr);
							
							TimerUtils.timePause((TimeCounted) component);
							attributeIsRedundant = false;
							TimerUtils.timeContinue((TimeCounted) component);
						}
						return new Object[] {
							red,
							examAttr, 
							attributeIsRedundant
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<Integer> red = (Collection<Integer>) result[r++];
						int examAttr = (int) result[r++];
						boolean attributeIsRedundant = (boolean) result[r++];
						/* ------------------------------------------------------------------------------ */
						localParameters.put("red", red);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = "Loop["+loopCount+"] Attr["+examAttr+"]";
						reportKeys.add(reportMark);
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark, 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
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
						//	[REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE]
						ProcedureUtils.Report.saveItem(
								report, 
								reportMark, 
								Constants.REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE,
								examAttr
						);
						//	[REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE]
						ProcedureUtils.Report.saveItem(
								report, 
								reportMark, 
								Constants.REPORT_INSPECT_ATTRIBUTE_REDUNDANT,
								attributeIsRedundant
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspect reduct redundancy"),
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Integer> exec() throws Exception {
		ProcedureComponent<?>[] comps = initComponents();
		for (ProcedureComponent<?> each : comps) {
			this.getComponents().add(each);
			reportKeys.add(each.getDescription());
		}
		return (Collection<Integer>) comps[0].exec();
	}
	
	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}