package featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An simple entity for Weighted Edge in a Directed Acyclic Graph.
 * 
 * @author Benjamin_L
 *
 * @param <Node>
 * 		Type of graph node
 * @param <W>
 * 		Type of weight.
 */
@Data
@AllArgsConstructor
public class DirectedAcyclicGraghWeightedEdge<Node, W> {
	private Node toNode;
	private W weight;
}
