package tester.impl.heuristic.quickReduct.dependencyCalculation.component;

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
 * Core searching for <strong>Incremental Dependency Calculation (IDC)</strong> Feature Selection.
 * This procedure contains 2 ProcedureComponents: 
 * <li>
 * 	<strong>Core procedure controller</strong>
 * 	<p>Control loop over attributes that are not in reduct, and calculate their inner significance. If 
 * 		removing the attribute from the feature subset doesn't have any effect on the significance(i.e 
 * 		<code>dep(C-{a})==dep(C)</code>), it is NOT a core attribute. 
 * </li>
 * <li>
 * 	<strong>Core</strong>
 * 	<p>Calculate the inner significance of the attribute and determine if it is a Core attribute.
 * </li>
 * 
 * @author Benjamin_L
 */
@Slf4j
public class CoreProcedureContainer4IDC<Sig extends Number>
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;
	
	public CoreProcedureContainer4IDC(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
		
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Core";
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
			// 1 Core procedure controller
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component)->{
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int[] attributes = (int[]) parameters[0];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 core = {}
						Collection<Integer> core = new HashSet<>(attributes.length);
						TimerUtils.timePause((TimeCounted) component);
						localParameters.put("core", core);
						TimerUtils.timeContinue((TimeCounted) component);
						
						// 2 Go through a in C
						int[] examAttributes = new int[attributes.length-1];
						for (int i=0; i<examAttributes.length; i++)	examAttributes[i] = attributes[i+1];
						for (int i=0; i<attributes.length; i++) {
							TimerUtils.timePause((TimeCounted) component);
							
							localParameters.put("i", i);
							localParameters.put("examAttributes", examAttributes);
							
							this.getComponents().get(1).exec();
							
							examAttributes = (int[]) localParameters.get("examAttributes");
							core = (Collection<Integer>) localParameters.get("core");
							
							TimerUtils.timeContinue((TimeCounted) component);
						}
						return core;
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "core={}"), StringUtils.numberToString(result, 50, 0));
						/* ------------------------------------------------------------------------------ */
						// Statistics
						int[] attributes = getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES);
						//	[STATISTIC_CORE_LIST]
						component.setStatistics(Constants.STATISTIC_CORE_LIST, 
												((Collection<Integer>) result).toArray(new Integer[((Collection<Integer>) result).size()])
											);
						// [STATISTIC_CORE_ATTRIBUTE_EXAMED_LENGTH]
						ProcedureUtils.Statistics.countInt(getStatistics(), 
															Constants.STATISTIC_CORE_ATTRIBUTE_EXAMED_LENGTH, 
															attributes.length
														);
						/* ------------------------------------------------------------------------------ */
						// Report
						reportKeys.add(component.getDescription());
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								result.size()
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
			// 2 Core
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component)->{
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								localParameters.get("examAttributes"),
								getParameters().get("globalDependency"),
								localParameters.get("i"),
								localParameters.get("core"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						boolean attributeIsCore = false;
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						int[] examAttributes = (int[]) parameters[p++];
						Sig globalDependency = (Sig) parameters[p++];
						int i = (int) parameters[p++];
						Collection<Integer> core = (Collection<Integer>) parameters[p++];
						FeatureImportance4IncrementalDependencyCalculation<Sig> calculation = (FeatureImportance4IncrementalDependencyCalculation<Sig>) parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// Calculate significance of C-{a}: a.innerSig.
						//2.1 Use 【Algorithm 32】 to get the sub dependency.
						Sig subDependency = calculation.calculate(universes, new IntegerArrayIterator(examAttributes))
														.getResult();
						//2.2 If dep(C)-dep(C-{a}) > 0, add the current attribute into core.
						if (calculation.value1IsBetter(globalDependency, subDependency, sigDeviation)) {
							core.add(attributes[i]);
							
							TimerUtils.timePause((TimeCounted) component);
							attributeIsCore = true;
							TimerUtils.timeContinue((TimeCounted) component);
						}
						//Update extracted attributes
						if (i<examAttributes.length)	examAttributes[i] = attributes[i];
						
						return new Object[] {
							core,
							examAttributes, 
							attributeIsCore,
							attributes[i],
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<Integer> core = (Collection<Integer>) result[r++];
						int[] examAttributes = (int[]) result[r++];
						boolean attributeIsCore = (boolean) result[r++];
						int currentAttribute = (int) result[r++];
						/* ------------------------------------------------------------------------------ */
						localParameters.put("core", core);
						localParameters.put("examAttributes", examAttributes);
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = "Loop["+(((int)localParameters.get("i"))+1)+"] "+
											"Attr["+result[3]+"]";
						reportKeys.add(reportMark);
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, reportMark, 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								((Collection<?>) result[0]).size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters, 
											report, 
											reportMark, 
											(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_CORE_CURRENT_ATTRIBUTE]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_CORE_CURRENT_ATTRIBUTE, 
								currentAttribute
						);
						//	[REPORT_CORE_INDIVIDUAL_RESULT]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_CORE_INDIVIDUAL_RESULT, 
								attributeIsCore
						);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}

					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Core"),
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