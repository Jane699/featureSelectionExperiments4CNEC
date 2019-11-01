package tester.impl.heuristic.quickReduct.semisupervisedRepresentative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.FastMath;

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
import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.frame.support.reductMiningStrategy.heuristic.QuickReductHeuristicReductStrategy;
import featureSelection.repository.implement.algroithm.algStrategyRepository.semisupervisedRepresentative.SemisupervisedRepresentativeAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.SemisupervisedRepresentativeCalculations4EntropyBased;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache.InformationEntropyCache;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.graph.DirectedAcyclicGraghWeightedEdge;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.impl.heuristic.quickReduct.semisupervisedRepresentative.component.DirectedAcyclicGraphConstruction;
import tester.impl.heuristic.quickReduct.semisupervisedRepresentative.component.RelevantFeatureSelectProcedureContainer;
import tester.impl.heuristic.quickReduct.semisupervisedRepresentative.component.RepresentativeFeatureSelection;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Tester Procedure for <strong>Semi-supervised Representative Feature Selection</strong> Heuristic Quick 
 * Reduct Feature Selection.
 * <p>
 * Original article: <a href="https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242">"An efficient 
 * semi-supervised representatives feature selection algorithm based on information theory"</a> by Yintong 
 * Wang, Jiandong Wang, Hao Liao, Haiyan Chen.
 * <p>
 * This is a {@link DefaultProcedureContainer}. Procedure contains 6 {@link ProcedureComponent}s, refer to 
 * steps: 
 * <li>
 * 	<strong>Pre-initialization</strong>: 
 * 	<p>Some pre-initializations for Feature Selection. Creating instance of {@link Calculation} and 
 * 		wrap labeled {@link UniverseInstance}s, unlabeled {@link UniverseInstance}s into an Universe collection.
 * </li>
 * <li>
 * 	<strong>Decision entropy calculation</strong>: 
 * 	<p>Calculate H(D) for global usage later.
 * </li>
 * <li>
 * 	<strong>Irrelevant Feature Removal</strong>: 
 * 	<p>Correspondent to the original article, this step remove irrelevant features(i.e. select relevance 
 * 		features) bases on the parameter "feature relevance threshold"(i.e. &alpha; in the article).
 * 	<p><code>RelevantFeatureSelectProcedureContainer</code>,
 * </li>
 * <li>
 * 	<strong>F1-Relevance based feature descending sorting</strong>:
 * 	<p>Sort relevant features by F1-Relevance calculated in descending order.
 * </li>
 * <li>
 * 	<strong>Directed Acyclic Graph Construction and Partition</strong>:
 * 	<p>Construct a Directed Acyclic Graph to cluster relevant features into groups.
 * 	<p><code>DirectedAcyclicGraphConstruction</code>
 * </li>
 * <li>
 * 	<strong>Representative Feature Selection</strong>:
 * 	<p>Select the feature with max. F1-Rel value as the representative of the group it is in and as an
 * 		attribute of the final reduct.
 * 	<p><code>RepresentativeFeatureSelection</code>
 * </li>
 * <p>
 * <strong>Extra parameters</strong> beside standard parameters(e.g. attributes, etc.).
 * <li><strong>labeledUniverses</strong>: {@link UniverseInstance} {@link Collection}
 * 		<p>Labeled {@link UniverseInstance}s.
 * </li>
 * <li><strong>unlabeledUniverses</strong>: {@link UniverseInstance} {@link Collection}
 * 		<p>Unlabeled {@link UniverseInstance}s.
 * </li>
 * <li><strong>featureRelevanceThreshold</strong>: {@link double}, [0, 1], selective
 * 		<p>A threshold for feature relevance, marked as <strong>&alpha;</strong>. Features with F-Rel value
 * 			greater than &alpha; are considered relevant and reserved, otherwise are removed.
 * </li>
 * <li><strong>tradeOff</strong>: {@link double}, [0, 1]
 * 		<p>Superivsed/Semi-supervised/Un-supervised trade-off, marked as <strong>&beta;</strong>.
 * 		<p><strong>supervise</strong> only:    &beta; = <strong>0</strong>; 
 * 		<p><strong>un-supervise</strong> only: &beta; = <strong>1</strong>; 
 * 		<p><strong>semi-supervise</strong>:    &beta; = <strong>(0, 1)</strong>.
 * </li>
 * 
 * @see {@link RelevantFeatureSelectProcedureContainer}
 * @see {@link DirectedAcyclicGraphConstruction}
 * @see {@link RepresentativeFeatureSelection}
 * 
 * @author Benjamin_L
 */
@Slf4j
public class SemisupervisedRepresentativesFeatureSelectionHeuristicQRTester 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements TimeSum, 
				ReportMapGenerated<String, Map<String, Object>>, 
				StatisticsCalculated,
				QuickReductHeuristicReductStrategy,
				SemisupervisedRepresentativeStrategy
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	
	public SemisupervisedRepresentativesFeatureSelectionHeuristicQRTester(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
	}

	@Override
	public String shortName() {
//		Double featureRelevanceThreshold = getParameters().get("featureRelevanceThreshold");
//		Double tradeOff = getParameters().get("tradeOff");
//		return "QR-SRFS("+
//					(featureRelevanceThreshold!=null? String.format("α=%.6f", featureRelevanceThreshold): "") +
//					(featureRelevanceThreshold!=null && tradeOff!=null? ", ":"") +
//					(tradeOff!=null?"β="+String.format("%.3f", tradeOff): "") +
//				")";
		return "QR-SRFS";
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
			// 1. Pre-initialization
			new ProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("1. "+component.getDescription());
					}, 
					(component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Collection<UniverseInstance> labeledUniverses = getParameters().get(ParameterConstants.PARAMETER_LABELED_UNIVERSE);
						Collection<UniverseInstance> unlabeledUniverses = getParameters().get(ParameterConstants.PARAMETER_UNLABELED_UNIVERSE);
						
						Collection<UniverseInstance> allUniverses = new ArrayList<>(labeledUniverses.size()+unlabeledUniverses.size());
						allUniverses.addAll(labeledUniverses);
						allUniverses.addAll(unlabeledUniverses);
						
						// Set &beta;
						if (getParameters().get("tradeOff")==null) {
							// auto-set trade-off(&beta;) = 1 - (N/(N+M))^1/2, where N=|labeled U|, M=|unlabeled U|
							double tradeOff = 1 - FastMath.sqrt(labeledUniverses.size() / (double) allUniverses.size());
							getParameters().setNonRootParameter("tradeOff", tradeOff);
						}
						
						Class<? extends Calculation<?>> calculationClass = getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS);
						/* ------------------------------------------------------------------------------ */
						return new Object[] {
								allUniverses,
								calculationClass.newInstance()
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						Collection<UniverseInstance> allUniverses = (Collection<UniverseInstance>) result[r++];
						Calculation<?> calculation = (Calculation<?>) result[r++];
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, allUniverses);
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE, calculation);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Pre-initialization"),
			// 2. Decision entropy calculation and cache initialization.
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("2. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_LABELED_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> labeledUniverses = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						SemisupervisedRepresentativeCalculations4EntropyBased calculation = 
								(SemisupervisedRepresentativeCalculations4EntropyBased) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						Collection<Collection<UniverseInstance>> decEquClasses = 
								SemisupervisedRepresentativeAlgorithm
									.Basic
									.equivalentClass(labeledUniverses, new IntegerArrayIterator(0))
									.values();
						double decisionInfoEntropy = 
								calculation.getInfoEntropyCalculation()
											.calculate(decEquClasses, labeledUniverses.size())
											.getResult().doubleValue();	
						/* ------------------------------------------------------------------------------ */
						return new Object[] {
								decisionInfoEntropy,
								new InformationEntropyCache(attributes.length)
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						double decisionInfoEntropy = (double) result[r++];
						InformationEntropyCache infoEntropyCache = (InformationEntropyCache) result[r++];
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("decisionInfoEntropy", decisionInfoEntropy);
						getParameters().setNonRootParameter("infoEntropyCache", infoEntropyCache);
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(1, "H(D) = {}"), String.format("%.8f", decisionInfoEntropy));
						}
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Decision entropy calculation and cache initialization"),
			// >> Part 1: Irrelevant Feature Removal.
			// 3. Irrelevant Feature Removal
			new ProcedureComponent<Map<Integer, Double>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("3. "+component.getDescription());
					}, 
					(component, parameters) -> {
						return (Map<Integer, Double>)
								component.getSubProcedureContainers()
										.values()
										.iterator()
										.next()
										.exec();
					}, 
					(component, relevantFeatureF1Rels) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("relevantFeatureF1Rels", relevantFeatureF1Rels);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Irrelevant Feature Removal")
				.setSubProcedureContainer(
					"RelevantFeatureSelectProcedureContainer", 
					new RelevantFeatureSelectProcedureContainer(getParameters(), logOn)
				),
			// 4. F1-Relevance based feature descending sorting
			new TimeCountedProcedureComponent<Integer[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("4. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("relevantFeatureF1Rels"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Map<Integer, Double> relevantFeatureF1Rels = (Map<Integer, Double>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						return SemisupervisedRepresentativeAlgorithm
								.descendingSortRelevanceFeatureByF1Relevance(
										relevantFeatureF1Rels
									);
					}, 
					(component, relevantFeatures) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("relevantFeatures", relevantFeatures);
//						if (logOn) {
//							log.info(LoggerUtil.spaceFormat(1, "Sorted Relevant Features: {}"), 
//									Arrays.toString(relevantFeatures)
//							);
//							Map<Integer, Double> relevantFeatureF1Rels = getParameters().get("relevantFeatureF1Rels");
//							log.info(LoggerUtil.spaceFormat(1, "Sorted Relevant Features: {}"), 
//									Arrays.stream(relevantFeatures).map(f->{
//										return f+":"+String.format("%.4f", relevantFeatureF1Rels.get(f));
//									}).collect(Collectors.toList())
//							);
//						}
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_BEFORE_INSPECT]
						statistics.put(Constants.STATISTIC_RED_BEFORE_INSPECT, Arrays.asList(relevantFeatures));
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 4. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("F1-Relevance based feature descending sorting"),
			// >> Part 2: Directed Acyclic Graph Construction and Partition
			// 5. Directed Acyclic Graph Construction and Partition
			new ProcedureComponent<Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("5. "+component.getDescription());
					}, 
					(component, parameters) -> {
						return (Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[]) 
								component.getSubProcedureContainers()
										.values()
										.iterator()
										.next()
										.exec();
					}, 
					(component, relevanceFeatures) -> {
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
											
					@Override public String staticsName() {
						return shortName()+" | 5. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Directed Acyclic Graph Construction and Partition")
				.setSubProcedureContainer(
					"DirectedAcyclicGraphConstruction", 
					new DirectedAcyclicGraphConstruction(getParameters(), logOn)
				),
			// >> Part 3: Representative Feature Selection
			// 6. Representative Feature Selection
			new ProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
						if (logOn)	log.info("6. "+component.getDescription());
					}, 
					(component, parameters) -> {
						int[] representatives = 
								(int[]) component.getSubProcedureContainers()
												.values()
												.iterator()
												.next()
												.exec();
						return Arrays.stream(representatives).boxed().collect(Collectors.toList());
					}, 
					(component, reduct) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST_AFTER_INSPECTATION, reduct);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[STATISTIC_RED_AFTER_INSPECT]
						statistics.put(Constants.STATISTIC_RED_AFTER_INSPECT, reduct);
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
											
					@Override public String staticsName() {
						return shortName()+" | 6. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Representative Feature Selection")
				.setSubProcedureContainer(
					"RepresentativeFeatureSelection", 
					new RepresentativeFeatureSelection(getParameters(), logOn)
				),
		};
	}
	
	@Override
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