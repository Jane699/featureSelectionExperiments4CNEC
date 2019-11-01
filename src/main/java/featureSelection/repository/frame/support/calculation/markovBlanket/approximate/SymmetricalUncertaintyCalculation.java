package featureSelection.repository.frame.support.calculation.markovBlanket.approximate;

import basic.model.interf.Calculation;

/**
 * An interface for <strong>Approximate Markov Blanket</strong> Symmetrical Uncertainty (SU) calculation.
 * <p>
 * Check out papers:
 * <li><a href="https://dl.acm.org/citation.cfm?id=1044700">"An efficient semi-supervised representatives 
 * feature selection algorithm based on information theory"</a> by Wang Yintong, Wang Jiandong, Liao Hao, 
 * Chen Haiyan.
 * </li>
 * <li><a href="https://dl.acm.org/citation.cfm?id=1044700">"Efficient Feature Selection via Analysis of 
 * Relevance and Redundancy"</a> by Lei Yu, Huan Liu.
 * </li>
 * <li><a href="https://www.sciencedirect.com/science/article/abs/pii/S0031320314004804">"An evaluation 
 * of classifier-specific filter measure performance for feature selection"</a> by Cecille Freeman, 
 * Dana Kulić, Otman Basir.
 * </li>
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of calculation result value.
 */
public interface SymmetricalUncertaintyCalculation<V>
	extends Calculation<V> 
{
	public static final String CALCULATION_NAME = "SU";
}