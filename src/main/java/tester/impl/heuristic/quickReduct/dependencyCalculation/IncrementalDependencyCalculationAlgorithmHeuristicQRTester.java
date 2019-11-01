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
import featureSelection.repository.frame.support.algStrategy.dependencyCalculation.IncrementalDependencyCalculationStrategy;
import featureSelection.repository.frame.support.searchStrategy.HashSearchStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.dependencyCalculation.IncrementalDependencyCalculationAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.dependencyCalculation.incrementalDependencyCalculation.FeatureImportance4IncrementalDependencyCalculation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.dependencyCalculation.component.CoreProcedureContainer4IDC;
import tester.impl.heuristic.quickReduct.dependencyCalculation.component.ReductInspectionProcedureContainer4IDC;
import tester.impl.heuristic.quickReduct.dependencyCalculation.component.SignificantAttributeSeekingLoopProcedureContainer4IDC;
import tester.utils.ProcedureUtils;

/**
 * Tester Procedure for <strong>Incremental Dependency Calculation (IDC)</strong> Feature Selection.
 * <p>
 * Original article: <a href="https://www.sciencedirect.com/science/article/pii/S0020025516000785">
 * "An incremental dependency calculation technique for feature selection using rough sets"</a> by Muhammad 
 * Summair Raza, Usman Qamar.
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
 * 	<p><code>CoreProcedureContainer4IDC</code>
 * </li>
 * <li>
 * 	<strong>Sig loop</strong>: 
 * 	<p>Loop and search for the most significant attribute and add as an attribute of the reduct until 
 * 		reaching exit condition.
 * 	<p><code>SignificantAttributeSeekingLoopProcedureContainer4IDC</code>
 * </li>
 * <li>
 * 	<strong>Inspection</strong>: 
 * 	<p>Inspect the reduct and remove redundant attributes.
 * 	<p><code>ReductInspectionProcedureContainer4IDC</code>
 * </li>
 * 
 * @see {@link FeatureImportance4IncrementalDependencyCalculation}
 * @see {@link IncrementalDependencyCalculationAlgorithm}
 * 
 * @see {@link CoreProcedureContainer4IDC}
 * @see {@link SignificantAttributeSeekingLoopProcedureContainer4IDC}
 * @see {@link ReductInspectionProcedureContainer4IDC}
 * 
 * @author Benjamin_L
 */
@Slf4j
@RoughSet
public class IncrementalDependencyCalculationAlgorithmHeuristicQRTester<Sig extends Number>
	extends DefaultProcedureContainer<Collection<Integer>>
	implements TimeSum, 
				HashSearchStrategy,
				StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>,
				IncrementalDependencyCalculationStrategy
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	
	public IncrementalDependencyCalculationAlgorithmHeuristicQRTester(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? log: null, paramaters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
	}
	
	@Override
	public String shortName() {
		return "QR-IDC"+
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
			// 1. Get dep(C)
			new TimeCountedProcedureComponent<Sig>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("1. "+component.getDescription());
						FeatureImportance4IncrementalDependencyCalculation<Sig> calculation;
						Class<? extends FeatureImportance4IncrementalDependencyCalculation<Sig>> calculationClass = 
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS);
						getParameters().setNonRootParameter(
								ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE, 
								calculation = calculationClass.newInstance()
						);
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								calculation,
						});
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						List<UniverseInstance> universes = (List<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						FeatureImportance4IncrementalDependencyCalculation<Sig> calculation = (FeatureImportance4IncrementalDependencyCalculation<Sig>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						return calculation.calculate(universes, new IntegerArrayIterator(attributes))
											.getResult();
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "Sig(global)={}"), result);
						this.getParameters().setNonRootParameter("globalDependency", result);
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
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
			}.setDescription("Get dep(C)"),
			// 2. Get Core
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CORE,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("2. "+component.getDescription());
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
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Get Core")
				.setSubProcedureContainer(
					"CoreProcedureContainer", 
					new CoreProcedureContainer4IDC<Sig>(getParameters(), logOn)
				),
			// 3. Sig loop
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
					"SignificantAttributeSeekingLoopProcedureContainer", 
					new SignificantAttributeSeekingLoopProcedureContainer4IDC<Sig>(getParameters(), logOn)
				),
			// 4. Inspection
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
					new ReductInspectionProcedureContainer4IDC<Sig>(getParameters(), logOn)
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