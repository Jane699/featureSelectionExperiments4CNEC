package tester.impl.heuristic.quickReduct.semisupervisedRepresentative.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.FastMath;

import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.SemisupervisedRepresentativeCalculations4EntropyBased;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache.InformationEntropyCache;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevance1Params4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevanceParams4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.conditionalEntropy.semisupervisedRepresentative.ConditionalEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.mutualInformationEntropy.semisupervisedRepresentative.MutualInformationEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.relevance.semisupervisedRepresentative.FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.utils.TimerUtils;

/**
 * Relevant Feature Selection of <strong>Semi-supervised Representative Feature Selection</strong>. 
 * This procedure contains 3 ProcedureComponents: 
 * <li>
 * 	<strong>Feature loop controller</strong>
 * 	<p>Control loop over attributes of {@link UniverseInstance} to select relevant features judging by its 
 * 		F-Rel value and pre-set parameter "relevant feature threshold"(i.e. &alpha;).
 * 	<p>If pre-set parameter "relevant feature threshold"(i.e. &alpha;) is set to auto(=null), it is set
 * 		to the F_Rel value of the ceil(D/log<sub>2</sub>D)th ranked feature based on the original article
 * 		<a href="https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242">"An efficient 
 * 		semi-supervised representatives feature selection algorithm based on information theory"</a>
 * </li>
 * <li>
 * 	<strong>Calculate Feature Relevance: F_Rel(F[i], C)</strong>
 * 	<p>Calculate the F-Rel(F<sub>i</sub>, C) where F<sub>i</sub> is an attribute/a feature and C is the 
 * 		Decision attribute of {@link UniverseInstance}.
 * </li>
 * <li>
 * 	<strong>Calculate Feature 1 Relevance: F1_Rel(F[i], C)</strong>
 * 	<p>Calculate the F-Rel(F<sub>i</sub>, C) where F<sub>i</sub> is an attribute/a feature and C is the 
 * 		Decision attribute of {@link UniverseInstance}.
 * </li>
 * 
 * @author Benjamin_L
 */
@Slf4j
public class RelevantFeatureSelectProcedureContainer 
	extends DefaultProcedureContainer<Map<Integer, Double>>
	implements StatisticsCalculated, 
				ReportMapGenerated<String, Map<String, Object>> 
{
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;
	
	public RelevantFeatureSelectProcedureContainer(ProcedureParameters parameters, boolean logOn) {
		super(logOn? log: null, parameters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();

		reportKeys = new LinkedList<>();
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Relevant Feature Select(SRFS)";
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
			// 1. Feature loop controller
			new TimeCountedProcedureComponent<Map<Integer, Double>>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
//						if (logOn)	log.info("1. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								getParameters().get("featureRelevanceThreshold"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						int[] attributes = (int[]) parameters[p++];
						Double featureRelevanceThreshold = (Double) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						ProcedureComponent<?> comp1 = getComponents().get(1);
						ProcedureComponent<?> comp2 = getComponents().get(2);
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1: S = NULL
						Map<Integer, Double> relevantFeatureF1Rels = new HashMap<>(attributes.length);
						TimerUtils.timePause((TimeCounted) component);
						getParameters().setNonRootParameter("relevantFeatureF1Rels", relevantFeatureF1Rels);
						TimerUtils.timeContinue((TimeCounted) component);
						// 2: for i=1:D do	// D = dimension
						double featureRelevance;
						Map<Integer, Double> featureRelevances = new HashMap<>(attributes.length);
						for (int i=0; i<attributes.length; i++) {
							// 3: F_Rel(F[i], C) = B * H(F[i]) + (1-B) * I(F[i], C).
							TimerUtils.timePause((TimeCounted) component);
							localParameters.put("i", i);
							comp1.exec();
							featureRelevance = (double) localParameters.get("featureRelevance");
							featureRelevances.put(i, featureRelevance);
							TimerUtils.timeContinue((TimeCounted) component);
						}
						// If auto-set &alpha; (featureRelevanceThreshold)
						if (featureRelevanceThreshold==null) {
							// &alpha; is the F_Rel value of the ceil(D/log<sub>2</sub>D)th ranked feature.
							int attrLength = attributes.length;
							int index = (int) FastMath.ceil(attrLength / FastMath.log(2, attrLength));
							int thresholdIndex = 
								featureRelevances.keySet().stream().sorted(
									(i1, i2)->Double.compare(featureRelevances.get(i1), featureRelevances.get(i2))
								).collect(Collectors.toList())
								.get(index).intValue();
							featureRelevanceThreshold = featureRelevances.get(thresholdIndex);
							
							TimerUtils.timePause((TimeCounted) component);
							getParameters().setNonRootParameter("featureRelevanceThreshold", featureRelevanceThreshold);
							TimerUtils.timeContinue((TimeCounted) component);
						}
						
						for (int i=0; i<attributes.length; i++) {
							// 4: If F_Rel(F[i], C) > &alpha;
							if (Double.compare(featureRelevances.get(i), featureRelevanceThreshold)>0) {
								// 5: Add F[i] to the relevant feature subset
								// 6: F1_Rel(F[i], C) = &beta; * UI(F[i])/H(F[i]) + (1-&beta;) * SU(F[i], C)
								TimerUtils.timePause((TimeCounted) component);
								localParameters.put("i", i);
								comp2.exec();
								TimerUtils.timeContinue((TimeCounted) component);
							}
						}
						/* ------------------------------------------------------------------------------ */
						return relevantFeatureF1Rels;
					}, 
					(component, relevantFeatureF1Rels) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("relevantFeatureF1Rels", relevantFeatureF1Rels);
						/* ------------------------------------------------------------------------------ */
						if (logOn) {
							log.info(LoggerUtil.spaceFormat(1, "featureRelevanceThreshold (α) = {}"), 
									String.format("%.6f", (double) getParameters().get("featureRelevanceThreshold"))
							);
							log.info(LoggerUtil.spaceFormat(1, "|relevant features| = {}"), relevantFeatureF1Rels.size());
						}
						/* ------------------------------------------------------------------------------ */
						// Statistics
						//	[featureRelevanceThreshold (α)]
						statistics.put("featureRelevanceThreshold", getParameters().get("featureRelevanceThreshold"));
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Feature loop controller"),
			// 2. Calculate Feature Relevance: F_Rel(F[i], C)
			new TimeCountedProcedureComponent<Double>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
//						if (logOn)	log.info("2. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_LABELED_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								localParameters.get("i"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get("decisionInfoEntropy"),
								getParameters().get("tradeOff"),
								getParameters().get("infoEntropyCache"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> allUniverses = (Collection<UniverseInstance>) parameters[p++];
						Collection<UniverseInstance> labeledUniverses = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						int i = (int) parameters[p++];
						SemisupervisedRepresentativeCalculations4EntropyBased calculation = 
								(SemisupervisedRepresentativeCalculations4EntropyBased) 
								parameters[p++];
						double decisionInfoEntropy = (double) parameters[p++];
						Double tradeOff = (Double) parameters[p++];
						InformationEntropyCache infoEntropyCache = (InformationEntropyCache) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						FeatureRelevanceParams4SemisupervisedRepresentative<
								Collection<Collection<UniverseInstance>>,
								ConditionalEntropyCalculation4SemisupervisedRepresentative,
								MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
							param4FRel = 
								FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased
									.ParameterLoader
									.loadFeatureRelevanceCalculationParamters(
										tradeOff, 
										labeledUniverses, allUniverses, 
										attributes[i], 
										decisionInfoEntropy, 
										calculation,
										infoEntropyCache
									);
						double featureRelevance = calculation.getRelevanceCalculation()
															.calculateFRel(param4FRel)
															.getResult().doubleValue();
						/* ------------------------------------------------------------------------------ */
						return featureRelevance;
					}, 
					(component, featureRelevance) -> {
						/* ------------------------------------------------------------------------------ */
						localParameters.put("featureRelevance", featureRelevance);
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
				}.setDescription("Calculate Feature Relevance: F_Rel(F[i], C)"),
			// 3. Calculate Feature 1 Relevance: F1_Rel(F[i], C)
			new TimeCountedProcedureComponent<Boolean>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
//						if (logOn)	log.info("3. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_LABELED_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
								localParameters.get("i"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get("decisionInfoEntropy"),
								getParameters().get("tradeOff"),
								getParameters().get("relevantFeatureF1Rels"),
								getParameters().get("infoEntropyCache"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Collection<UniverseInstance> allUniverses = (Collection<UniverseInstance>) parameters[p++];
						Collection<UniverseInstance> labeledUniverses = (Collection<UniverseInstance>) parameters[p++];
						int[] attributes = (int[]) parameters[p++];
						int i = (int) parameters[p++];
						SemisupervisedRepresentativeCalculations4EntropyBased calculation = 
								(SemisupervisedRepresentativeCalculations4EntropyBased) 
								parameters[p++];
						double decisionInfoEntropy = (double) parameters[p++];
						double tradeOff = (double) parameters[p++];
						Map<Integer, Double> relevantFeatureF1Rels = (Map<Integer, Double>) parameters[p++];
						InformationEntropyCache infoEntropyCache = (InformationEntropyCache) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 5: Add F[i] to the relevant feature subset
						// 6: F1_Rel(F[i], C) = &beta; * UI(F[i])/H(F[i]) +
						//						(1-&beta;) * SU(F[i], C)
						FeatureRelevance1Params4SemisupervisedRepresentative<
								Collection<Collection<UniverseInstance>>,
								ConditionalEntropyCalculation4SemisupervisedRepresentative,
								MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
							param4F1Rel = 
								FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased
									.ParameterLoader
									.loadFeature1RelevanceCalculationParamters(
										tradeOff, 
										labeledUniverses, allUniverses, 
										attributes, i, 
										decisionInfoEntropy, 
										calculation,
										infoEntropyCache
									);
						relevantFeatureF1Rels.put(
							attributes[i], 
							calculation.getRelevanceCalculation()
										.calculateF1Rel(param4F1Rel)
										.getResult().doubleValue()
						);
						/* ------------------------------------------------------------------------------ */
						return null;
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						/* ------------------------------------------------------------------------------ */
					}
				) {
					@Override public void init() {}
								
					@Override public String staticsName() {
						return shortName()+" | 3. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Calculate Feature 1 Relevance: F1_Rel(F[i], C)"),
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, Double> exec() throws Exception {
		ProcedureComponent<?>[] comps = initComponents();
		for (ProcedureComponent<?> each: comps)	this.getComponents().add(each);
		return (Map<Integer, Double>) comps[0].exec();
	}

	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}