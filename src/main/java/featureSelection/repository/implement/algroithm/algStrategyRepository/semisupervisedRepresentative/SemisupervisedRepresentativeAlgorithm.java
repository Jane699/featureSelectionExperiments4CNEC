package featureSelection.repository.implement.algroithm.algStrategyRepository.semisupervisedRepresentative;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import basic.model.IntArrayKey;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.classic.ClassicAttributeReductionHashMapAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.graph.DirectedAcyclicGraghWeightedEdge;

/**
 * Algorithm repository of Semi-supervised Representative Feature Selection, which bases on the paper 
 * <a href="https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242">
 * "An efficient semi-supervised representatives feature selection algorithm based on information theory"</a> 
 * by Yintong Wang, Jiandong Wang, Hao Liao, Haiyan Chen.
 * 
 * @author Benjamin_L
 */
public class SemisupervisedRepresentativeAlgorithm {
	
	/**
	 * Basic algorithms for Semi-supervised Representative Feature Selection. (SRFS)
	 * 
	 * @author Benjamin_L
	 */
	public static class Basic {
		/**
		 * Calculate the Equivalent Classes by given attributes.
		 * 
		 * @see {@link ClassicAttributeReductionHashMapAlgorithm.Basic#equivalentClass(Collection, IntegerIterator)}
		 * 
		 * @param instances
		 * 		A {@link Collection} of {@link UniverseInstance}
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}. (starts from 1, 0 as decision attribute)
		 * @return A {@link Map} of Equivalence Classes with {@link UniverseInstance} in {@link List}.
		 */
		public static Map<IntArrayKey, Collection<UniverseInstance>> equivalentClass(
				Collection<UniverseInstance> instances, IntegerIterator attributes
		){
			return ClassicAttributeReductionHashMapAlgorithm
					.Basic
					.equivalentClass(instances, attributes);
		}
	}
	
	/**
	 * Get <strong>Relevance Features</strong> in descending order sorted by F1-Relevance.
	 * 
	 * @param relevantFeatureF1Rels
	 * 		A {@link Map} whose keys are relevance features and values are the corresponding F1-Relevance 
	 * 		values.
	 * @return relevance features in sorted {@link Integer[]}.
	 */
	public static Integer[] descendingSortRelevanceFeatureByF1Relevance(
			Map<Integer, Double> relevantFeatureF1Rels
	) {
		return relevantFeatureF1Rels.keySet().stream()
				.sorted(
					// sort features by F1-Relevance in descending
					(f1, f2)-> - Double.compare(
						relevantFeatureF1Rels.get(f1).doubleValue(), 
						relevantFeatureF1Rels.get(f2).doubleValue()
					)
				).toArray(Integer[]::new);
	}

	/**
	 * Get sub-graphs of the given graph. For sub-graph, nodes are connected/grouped by pointing at or 
	 * being pointed by another one. For nodes are not connected, they belong to two different sub-graph
	 * respectfully.
	 * 
	 * @param dagEdges
	 * 		Directed Acyclic Graph edges stored in {@link Collection} array. Edges of the <code>i</code>th 
	 * 		node are saved in the <code>i</code>th {@link Collection}.
	 * @return Sub-graph of the given graph whose elements are the node index ranging from 0 to |node|-1.
	 */
	public static Collection<Collection<Integer>> subGraphOf(
			Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[] dagEdges
	) {
		Collection<Collection<Integer>> groupCollector = new LinkedList<>();
		boolean[] searched = new boolean[dagEdges.length];
		for (int i=0; i<dagEdges.length; i++) {
			if (!searched[i]) {
				// group nodes and search for members.
				searchGroupMembersByDFS(dagEdges, groupCollector, null, searched, i);
			}else {
				// already group and search the node, skip.
				continue;
			}
		}
		return groupCollector;
	}
	
	/**
	 * Search group members of <code>currentGroup</code> using Depth First Search.
	 * 
	 * @param dagEdges
	 * 		Edges of a directed graph in {@link Map} array.
	 * @param collector
	 * 		A collector collects groups in {@link Collection}.
	 * @param currentGroup
	 * 		Current group in {@link Collection} with indexes as elements and group members. / 
	 * 		<code>null</code> for initiating a group and searching.
	 * @param searched
	 * 		A {@link boolean[]} marks nodes being searched already with a length of |<code>dagEdges</code>|.
	 * @param nodeIndex
	 * 		Current node index.
	 */
	private static void searchGroupMembersByDFS(
			Collection<DirectedAcyclicGraghWeightedEdge<Integer, Double>>[] dagEdges, 
			Collection<Collection<Integer>> collector, Collection<Integer> currentGroup, 
			boolean[] searched, int nodeIndex
	) {
		// mark grouped.
		if (!searched[nodeIndex])	searched[nodeIndex] = true;
		// Initiate group if needed.
		if (currentGroup==null) {
			collector.add(currentGroup = new HashSet<>());
			// The one who points at others are at the same group with points being pointing at.
			currentGroup.add(nodeIndex);
		}
		// nodes being pointed at is at the same group as the one who points at.
		for (DirectedAcyclicGraghWeightedEdge<Integer, Double> edge: dagEdges[nodeIndex]) {
			// if already group and search the node, skip.
			if (searched[edge.getToNode()]) {
				continue;
			}else {
				// add index(being pointed at) into group.
				if (currentGroup.add(edge.getToNode())) {
					// successfully added index, continue to add points being pointed by it.
					searchGroupMembersByDFS(dagEdges, collector, currentGroup, searched, edge.getToNode());
				}
			}
		}
	}
}