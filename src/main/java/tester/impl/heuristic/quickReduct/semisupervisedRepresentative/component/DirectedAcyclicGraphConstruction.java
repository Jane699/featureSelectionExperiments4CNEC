package tester.impl.heuristic.quickReduct.semisupervisedRepresentative.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.SemisupervisedRepresentativeCalculations4EntropyBased;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache.InformationEntropyCache;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.graph.DirectedAcyclicGraghWeightedEdge;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevance2Params4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.conditionalEntropy.semisupervisedRepresentative.ConditionalEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.mutualInformationEntropy.semisupervisedRepresentative.MutualInformationEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.relevance.semisupervisedRepresentative.FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased;
import lombok.Getter;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.utils.TimerUtils;

/**
 * Directed Acyclic Graph construction for <strong>Semi-supervised Representative Feature Selection</strong>. 
 * This procedure contains 2 ProcedureComponents: 
 * <li>
 * 	<strong>Relevance feature loop controller</strong>
 * 	<p>Control loop over relevant features and construct a Directed Acyclic Graph(DAG) whose nodes record the
 * 		feature's F-Rel value and edges record the F2-Rel value of two nodes.
 * 	<p>According to the original article
 * 		<a href="https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242">"An efficient 
 * 		semi-supervised representatives feature selection algorithm based on information theory"</a>,
 * 		Edges of the DAG must meet the criteria: <strong>F1_Rel(F<sub>i</sub>, C) >= F1_Rel(F<sub>j</sub>, C) 
 * 		& F2_Rel(F<sub>i</sub>, F<sub>j</sub>) >= F1_Rel(F<sub>j</sub>, C)</strong>.
 * </li>
 * <li>
 * 	<strong>Calculate Feature 2 Relevance: F2_Rel</strong>
 * 	<p>Calculate the F2-Rel(F<sub>i</sub>, F<sub>j</sub>) where F<sub>i</sub>, F<sub>j</sub> are attributes/
 * 		features of relevant features.
 * </li>
 * 
 * @author Benjamin_L
 */
public class DirectedAcyclicGraphConstruction 
	extends DefaultProcedureContainer<Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[]>
	implements StatisticsCalculated, ReportMapGenerated<String, Map<String, Object>> 
{
	@SuppressWarnings("unused")
	private boolean logOn;
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	
	private Map<String, Object> localParameters;

	public DirectedAcyclicGraphConstruction(ProcedureParameters paramaters, boolean logOn) {
		super(paramaters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();

		reportKeys = new LinkedList<>();
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Directed Acyclic Graph Construction(SRFS)";
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
			// 1. Relevance feature loop controller
			new TimeCountedProcedureComponent<Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[]>(
					ComponentTags.TAG_SIG,
					this.getParameters(), 
					(component) -> {
//						if (logOn)	log.info("1. "+component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("relevantFeatures"),
								getParameters().get("relevantFeatureF1Rels"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						int p=0;
						Integer[] relevantFeatures = (Integer[]) parameters[p++];
						Map<Integer, Double> relevantFeatureF1Rels = (Map<Integer, Double>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						ProcedureComponent<?> comp1 = getComponents().get(1);
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 10: DAG = NULL;
						Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[] dagEdges = 
								new Collection[relevantFeatures.length];
						// 11: for each pair of features {F[i], F[j]} in S & i<j, do:
						for (int i=0; i<relevantFeatures.length; i++) {
							// initiate, collect edges of node i pointing at other points
							//	(i--weight-->j).
							dagEdges[i] = new LinkedList<>();

							TimerUtils.timePause((TimeCounted) component);
							localParameters.put("i", i);
							TimerUtils.timeContinue((TimeCounted) component);
							
							// go over j = [i+1: n]
							for (int j=i+1; j<relevantFeatures.length; j++) {
								// 12: F2_Rel(F<sub>i</sub>, F<sub>j</sub>) = 
								//		&beta; * USU(F<sub>i</sub>, F<sub>j</sub>) +
								//		(1-&beta;) * SU(F[i], C)
								TimerUtils.timePause((TimeCounted) component);
								localParameters.put("j", j);
								double f2RelOfiNj = (double) comp1.exec();
								TimerUtils.timeContinue((TimeCounted) component);
								
								// 13: if F1_Rel(F<sub>i</sub>, C) >= F1_Rel(F<sub>j</sub>, C) & 
								//			F2_Rel(F<sub>i</sub>, F<sub>j</sub>) >= F1_Rel(F<sub>j</sub>, C)
								//		then
								double f1RelOfiNC = relevantFeatureF1Rels.get(relevantFeatures[i]);
								double f1RelOfjNC = relevantFeatureF1Rels.get(relevantFeatures[j]);
								if (f1RelOfiNC >= f1RelOfjNC && f2RelOfiNj >= f1RelOfjNC) {
									// 14: Add <F<sub>i</sub>, F<sub>j</sub>> to DAG as a directed edge;
									//	(with F2_Rel(F<sub>i</sub>, F<sub>j</sub>) as edge weight)
									//	i --f2RelOfiNj--> j
									dagEdges[i].add(new DirectedAcyclicGraghWeightedEdge<>(j, f2RelOfiNj));
								}
							}
						}
						return dagEdges;
					}, 
					(component, dagEdges) -> {
						/* ------------------------------------------------------------------------------ */
						getParameters().setNonRootParameter("dagEdges", dagEdges);
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
				}.setDescription("Feature loop controller"),
			// 2. Calculate Feature 2 Relevance: F2_Rel
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
								localParameters.get("j"),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get("tradeOff"),
								getParameters().get("relevantFeatures"),
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
						int j = (int) parameters[p++];
						SemisupervisedRepresentativeCalculations4EntropyBased calculation = 
								(SemisupervisedRepresentativeCalculations4EntropyBased) 
								parameters[p++];
						double tradeOff = (double) parameters[p++];
						Integer[] relevantFeatures = (Integer[]) parameters[p++];
						InformationEntropyCache infoEntropyCache = (InformationEntropyCache) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 12: F2_Rel(F<sub>i</sub>, F<sub>j</sub>) = 
						//		&beta; * USU(F<sub>i</sub>, F<sub>j</sub>) +
						//		(1-&beta;) * SU(F[i], C)
						FeatureRelevance2Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
								ConditionalEntropyCalculation4SemisupervisedRepresentative, 
								MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
							param = 
								FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased
									.ParameterLoader
									.loadFeature2RelevanceCalculationParamters(
										tradeOff, 
										labeledUniverses, allUniverses, 
										attributes, relevantFeatures[i], new IntegerArrayIterator(relevantFeatures[j]), 
										calculation,
										infoEntropyCache
									);
						/* ------------------------------------------------------------------------------ */
						return calculation.getRelevanceCalculation()
										.calculateF2Rel(param)
										.getResult().doubleValue();
					}, 
					(component, f2RelOfiNj) -> {
						/* ------------------------------------------------------------------------------ */
						localParameters.put("f2RelOfiNj", f2RelOfiNj);
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
				}.setDescription("Calculate Feature 2 Relevance: F2_Rel"),
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[] exec() throws Exception {
		ProcedureComponent<?>[] comps = initComponents();
		for (ProcedureComponent<?> each: comps)	this.getComponents().add(each);
		return (Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[]) comps[0].exec();
	}

	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}