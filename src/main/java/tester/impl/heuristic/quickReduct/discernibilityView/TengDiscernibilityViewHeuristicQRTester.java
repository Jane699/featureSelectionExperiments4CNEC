package tester.impl.heuristic.quickReduct.discernibilityView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.interf.Calculation;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.procedure.timer.TimeSum;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.algStrategy.TengDiscernibilityViewStrategy;
import featureSelection.repository.frame.support.streamlineStrategy.UniverseStreamline;
import featureSelection.repository.implement.algroithm.algStrategyRepository.discernibilityView.TengDiscernibilityViewAlgorithm;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.FeatureImportance4TengDiscernibilityView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.discernibilityView.component.SignificantAttributeSeekingLoopProcedureContainer;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Tester Procedure for <strong>Forward Attribute Reduction from the Discernibility View(FAR-DV)</strong>.
 * <p>
 * Original article: <a href="https://linkinghub.elsevier.com/retrieve/pii/S0020025515005605"> "Efficient
 * attribute reduction from the viewpoint of discernibility"</a> by Shu-Hua Teng, Min Lu, A-Feng Yang, 
 * Jun Zhang, Yongjian Nian, Mi He.
 * <p>
 * This is a {@link DefaultProcedureContainer}. Procedure contains 3 {@link ProcedureComponent}s, referring 
 * to steps: 
 * <li>
 * 	<strong>Compute Discernibility Degree of DIS(D/C)</strong>: 
 * 	<p>Compute the Relative Discernibility Degree of D relate to C where D is the decision attribute of 
 * 		{@link UniverseInstance} and C is all conditional attributes of {@link UniverseInstance}. Refers to the step 1 
 * 		in the original article.
 * </li>
 * <li>
 * 	<strong>Initializations</strong>: 
 * 	<p>Prepare for executions below, referring to the step 2 in the original article and some 
 * 		initializations for step 3 and step 5.
 * </li>
 * <li>
 * 	<strong>Sig loop</strong>: 
 * 	<p>Loop and search for the most significant attribute as an attribute of the final reduct until reaching
 * 		the exit criteria and return the reduct.
 * 	<p><code>SignificantAttributeSeekingLoopProcedureContainer</code>
 * </li>
 * 
 * @author Benjamin_L
 * 
 * @see {@link SignificantAttributeSeekingLoopProcedureContainer}
 *
 * @param <Sig>
 * 		Type of feature significance that implements {@link Number}.
 */
@Slf4j
public class TengDiscernibilityViewHeuristicQRTester<Sig extends Number>
		extends DefaultProcedureContainer<Collection<Integer>>
		implements TimeSum, 
					StatisticsCalculated, 
					ReportMapGenerated<String, Map<String, Object>>, 
					TengDiscernibilityViewStrategy 
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	
	public TengDiscernibilityViewHeuristicQRTester(ProcedureParameters paramaters, boolean logOn) {
		super(logOn? log: null, paramaters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
	}
	
	@Override
	public String shortName() {
		return "QR-Discernibility View"+
				"("+ProcedureUtils.ShortName.calculation(getParameters())+")";
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
			// 1. Compute Discernibility Degree of DIS(D/C)
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("1. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_CLASS),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						Class<? extends FeatureImportance4TengDiscernibilityView<Sig>> calculationClass =
								(Class<? extends FeatureImportance4TengDiscernibilityView<Sig>>) 
								parameters[p++];
						Class<? extends UniverseStreamline<?, ?, ?>> streamlineClass = 
								(Class<? extends UniverseStreamline<?, ?, ?>>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						FeatureImportance4TengDiscernibilityView<Sig> calculation = calculationClass.newInstance();
						//	U/C
						Collection<Collection<UniverseInstance>> globalEquClasses = 
								TengDiscernibilityViewAlgorithm
									.Basic
									.equivalentClass(universes, new IntegerArrayIterator(attributes))
									.values();
						//	(U/C)/D
						Collection<Collection<UniverseInstance>> gainGlobalEquClasses = 
								TengDiscernibilityViewAlgorithm
									.Basic
									.gainEquivalentClass(globalEquClasses, new IntegerArrayIterator(0));
						//	|DIS(D/C)|
						Sig disOfDRelate2C = calculation.calculate(globalEquClasses, gainGlobalEquClasses)
														.getResult();
						return new Object[] {
								calculation,
								streamlineClass.newInstance(),
								globalEquClasses,
								disOfDRelate2C,
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Calculation<Sig> calculation = (Calculation<Sig>) result[r++];
						UniverseStreamline<?, ?, ?> streamline = (UniverseStreamline<?, ?, ?>) result[r++];
						Collection<Collection<UniverseInstance>> globalEquClasses = (Collection<Collection<UniverseInstance>>) result[r++];
						Sig disOfDRelate2C = (Sig) result[r++];
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE, calculation);
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_INSTANCE, streamline);
						getParameters().setNonRootParameter("globalEquClasses", globalEquClasses);
						getParameters().setNonRootParameter("disOfDRelate2C", disOfDRelate2C);
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(1, "|DIS(D/C)| = {}"), disOfDRelate2C);
						}
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
				}.setDescription("Compute Discernibility Degree of DIS(D/C)"),
			// 2. Initializations.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("2. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// Step 2: j=1, U[j] = U, U' = {}, A[j] = C, A' = {}, red = {}
						Collection<Integer> red = new LinkedList<>();
						Collection<Integer> removedAttributes = new HashSet<>(attributes.length);

						// * Initiate for step 3.
						//		U/red = { U }, for red = {}
						Collection<Collection<UniverseInstance>> redEquClasses = new ArrayList<>(1);
						redEquClasses.add(universes);
						
						// * Initiate for step 5.
						//		U/D
						Collection<Collection<UniverseInstance>> decEquClasses = 
								TengDiscernibilityViewAlgorithm
									.Basic
									.equivalentClass(universes, new IntegerArrayIterator(0))
									.values();
						
						return new Object[] {
								red,
								removedAttributes,
								redEquClasses,
								decEquClasses
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<Integer> red = (Collection<Integer>) result[r++];
						Collection<Integer> removedAttributes = (Collection<Integer>) result[r++];
						Collection<Collection<UniverseInstance>> redEquClasses = (Collection<Collection<UniverseInstance>>) result[r++];
						Collection<Collection<UniverseInstance>> decEquClasses = (Collection<Collection<UniverseInstance>>) result[r++];
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST, red);
						getParameters().setNonRootParameter("removedAttributes", removedAttributes);
						getParameters().setNonRootParameter("redEquClasses", redEquClasses);
						getParameters().setNonRootParameter("decEquClasses", decEquClasses);
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
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Initializations"),
			// 3. Sig loop.
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("3. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
						});
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						return (Collection<Integer>) 
								component.getSubProcedureContainers()
										.values()
										.iterator()
										.next()
										.exec();
					}, 
					(component, reduct) -> {
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						statistics.put(Constants.STATISTIC_RED_BEFORE_INSPECT, reduct);
						//	[STATISTIC_RED_AFTER_INSPECT]
						statistics.put(Constants.STATISTIC_RED_AFTER_INSPECT, reduct);
						/* ------------------------------------------------------------------------------ */
						// Report
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>) getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								reduct.size()
						);
						//	[REPORT_EXECUTION_TIME]
						long componentTime = ProcedureUtils.Time.sumProcedureComponentTimes(component);
						ProcedureUtils.Report.ExecutionTime.save(report, component.getDescription(), componentTime);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
					
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Sig loop")
				.setSubProcedureContainer(
					"SignificantAttributeSeekingLoopProcedureContainer", 
					new SignificantAttributeSeekingLoopProcedureContainer<>(getParameters(), logOn)
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