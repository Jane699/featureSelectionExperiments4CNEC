package tester.impl.heuristic.quickReduct.dependencyCalculation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeSum;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.annotation.theory.RoughSet;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.dependencyCalculation.HeuristicDependencyCalculationStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.frame.support.searchStrategy.SequentialSearchStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation.HeuristicDependencyCalculationAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.heuristicDependencyCalculation.FeatureImportance4HeuristicDependencyCalculation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.dependencyCalculation.component.CoreProcedureContainer4HDC;
import tester.impl.heuristic.quickReduct.dependencyCalculation.component.ReductInspectionProcedureContainer4HDC;
import tester.impl.heuristic.quickReduct.dependencyCalculation.component.SignificantAttributeSeekingLoopProcedureContainer4HDC;
import tester.utils.ProcedureUtils;

/**
 * Tester Procedure for <strong>Heuristic Dependency Calculation (HDC)</strong> Feature Selection.
 * <p>
 * Original article: <a href="https://www.sciencedirect.com/science/article/abs/pii/S0031320318301432">
 * "A heuristic based dependency calculation technique for rough set theory"</a> by Muhammad Summair Raza, 
 * Usman Qamar.
 * <p>
 * This is a {@link DefaultProcedureContainer}. Procedure contains 4 {@link ProcedureComponent}s, refer to 
 * steps: 
 * <li>
 * 	<strong>Get dep(C)</strong>: 
 * 	<p>Calculate the global dependency by C: dep(C).
 * </li>
 * <li>
 * 	<strong>Get Core</strong>: 
 * 	<p>Calculate Core. 
 * 	<p><code>CoreProcedureContainer4HDC</code>
 * </li>
 * <li>
 * 	<strong>Sig loop</strong>: 
 * 	<p>Loop and search for the most significant attribute and add as an attribute of the reduct until 
 * 		reaching exit condition.
 * 	<p><code>SignificantAttributeSeekingLoopProcedureContainer4HDC</code>
 * </li>
 * <li>
 * 	<strong>Inspection</strong>: 
 * 	<p>Inspect the reduct and remove redundant attributes.
 * 	<p><code>ReductInspectionProcedureContainer4HDC</code>
 * </li>
 * 
 * @see {@link FeatureImportance4HeuristicDependencyCalculation}
 * @see {@link HeuristicDependencyCalculationAlgorithm}
 * 
 * @see {@link CoreProcedureContainer4HDC}
 * @see {@link SignificantAttributeSeekingLoopProcedureContainer4HDC}
 * @see {@link ReductInspectionProcedureContainer4HDC}
 * 
 * @author Benjamin_L
 */
@Slf4j
@RoughSet
public class HeuristicDependencyCalculationAlgorithmHeuristicQRTester<Sig extends Number>
	extends DefaultProcedureContainer<Collection<Integer>>
	implements TimeSum, 
				SequentialSearchStrategy, HashSearchStrategy,
				StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>,
				HeuristicDependencyCalculationStrategy
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	
	public HeuristicDependencyCalculationAlgorithmHeuristicQRTester(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? log: null, paramaters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
	}
	
	@Override
	public String shortName() {
		String searchStrategy;
		Class<?> calculationClass = getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS);
		if (HashSearchStrategy.class.isAssignableFrom(calculationClass))
			searchStrategy = "(Hash)";
		else if (SequentialSearchStrategy.class.isAssignableFrom(calculationClass))
			searchStrategy = "(Seq.)";
		else
			searchStrategy = null;
		return "QR-HDC"+(searchStrategy==null?"": searchStrategy)+
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
			// 1. Count decision values.
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("1. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
						});
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						List<UniverseInstance> universes = (List<UniverseInstance>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						return HeuristicDependencyCalculationAlgorithm
								.Basic
								.decisionValues(universes);
					}, 
					(component, decisionValues) -> {
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "|Decision values|={}"), decisionValues.size());
						this.getParameters().setNonRootParameter("decisionValues", decisionValues);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								0
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(getReport(), (TimeCountedProcedureComponent<?>) component);
					}
				){
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Count decision values"),
			// 2. Get dep(C)
			new TimeCountedProcedureComponent<Sig>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("2. "+component.getDescription());
						FeatureImportance4HeuristicDependencyCalculation<Sig> calculation;
						Class<? extends FeatureImportance4HeuristicDependencyCalculation<Sig>> calculationClass = 
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS);
						getParameters().setNonRootParameter(
								ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE, 
								calculation = calculationClass.newInstance()
						);
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								calculation,
								getParameters().get("decisionValues"),
						});
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						List<UniverseInstance> universes = (List<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						FeatureImportance4HeuristicDependencyCalculation<Sig> calculation = 
								(FeatureImportance4HeuristicDependencyCalculation<Sig>) 
								parameters[p++];
						Collection<Integer> decisionValues = (Collection<Integer>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						return calculation.calculate(universes, decisionValues, new IntegerArrayIterator(attributes), parameters)
											.getResult();
					}, 
					(component, globalSig) -> {
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Sig(global)={}"), globalSig);
						this.getParameters().setNonRootParameter("globalDependency", globalSig);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								0
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(getReport(), (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Get dep(C)"),
			// 3. Get Core
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("3. "+component.getDescription());
					}, 
					(component, parameters) -> {
						Boolean byCore = getParameters().get(ParameterConstants.PARAMETER_QR_BY_CORE);
						return byCore!=null && byCore?  
								(Collection<Integer>) component.getSubProcedureContainers()
																.values()
																.iterator()
																.next()
																.exec():
								new HashSet<>();
					}, 
					(component, core) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, core);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								core.size()
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
					new CoreProcedureContainer4HDC<Sig>(getParameters(), logOn)
				),
			// 4. Sig loop
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("3. "+component.getDescription());
					}, 
					(component, parameters) -> {
						return (Collection<Integer>) component.getSubProcedureContainers().values().iterator().next().exec();
					}, 
					(component, result) -> {
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, result);
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|Reduct Candidate| = {}"), result.size());
						statistics.put(Constants.STATISTIC_RED_BEFORE_INSPECT, result);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
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
			}.setDescription("Sig loop")
				.setSubProcedureContainer(
					"significantAttributeSeekingLoopProcedureContainer", 
					new SignificantAttributeSeekingLoopProcedureContainer4HDC<Sig>(getParameters(), logOn)
				),
			// 5. Inspection
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("4. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
						});
					}, 
					(component, parameters) -> {
						return (Collection<Integer>) component.getSubProcedureContainers().values().iterator().next().exec();
					}, 
					(component, red) -> {
						if (logOn)	log.info(LoggerUtil.spaceFormat(2, "|Reduct Finally| = {}"), red.size());
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST_AFTER_INSPECTATION, red);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						statistics.put(Constants.STATISTIC_RED_AFTER_INSPECT, red);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								red.size()
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
			}.setDescription("Inspection")
				.setSubProcedureContainer(
					"ReductInspectionProcedureContainer", 
					new ReductInspectionProcedureContainer4HDC<Sig>(getParameters(), logOn)
				),
		};
	}

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