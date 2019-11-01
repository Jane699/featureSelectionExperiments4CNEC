package featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.ConditionalEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.InformationEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.MutualInformationEntropyCalculation;
import lombok.Data;

/**
 * An entity for <strong>Semi-supervised Representative</strong> Feature Relevance calculation parameters.
 * (F_Rel) With the following fields/parameters:
 * <li><strong>{@link #equClassesOfLabeledInstances}</strong>: {@link Items}
 * <p>	Equivalent Classes partitioned by F<sub>i</sub>: <strong>U/F<sub>i</sub></strong> using labeled + 
 * 		unlabeled Universes.
 * </li>
 * <li><strong>{@link #infoEntropyOfEquClassesOfAllInstances}</strong>: {@link double}
 * <p>	Info. entropy of the Equivalent Classes partitioned by F<sub>i</sub> using labeled + unlabeled 
 * 		Universes: <strong>H(F<sub>i</sub>)</strong>.
 * </li>
 * <li><strong>{@link #condEquClassesOfLabeledInstances}</strong>: {@link Items}
 * <p>	Equivalent Classes partitioned by C: <strong>(labeled U)/C</strong> using labeled Universes.
 * </li>
 * <li><strong>{@link #infoEntropyOfCondEquClassesOfLabeledInstances}</strong>: {@link double}
 * <p>	Information entropy of C: <strong>H(C)</strong> using labeled Universes.
 * </li>
 * <li><strong>{@link #condAttributes}</strong>: {@link IntegerIterator}
 * <p>	Attributes used in the partitioning of U/C, i.e. <strong>C</strong>.
 * </li>
 * <li><strong>tradeOff</strong>: {@link double}
 * <p>	Superivsed/Semi-supervised/Un-supervised trade-off, marked as <strong>&beta;</strong>.
 * <p>	<strong>supervise</strong> only:    &beta; = <strong>0</strong>; 
 * <p>	<strong>un-supervise</strong> only: &beta; = <strong>1</strong>; 
 * <p>	<strong>semi-supervise</strong>:    &beta; = <strong>(0, 1)</strong>.
 * </li>
 * <li><strong>condEntropyCalculation</strong>: {@link condEntropyCalculation}
 * <p>	{@link CondEntropyCal} for <strong>H(F<sub>i</sub>|F<sub>j</sub>)</strong>.
 * </li>
 * <li><strong>mutualInfoEntropyCalculation</strong>: {@link InformationEntropyCalculation}
 * <p>	{@link MutualInfoEntropyCal} for <strong>I(F<sub>i</sub>; F<sub>j</sub>)</strong>.
 * </li>
 * <li><strong>args</strong>: {@link Object[]}
 * <p>	Extra arguments including: 
 * 		<li>Labeled Universe size in {@link int}: <strong>|labeled U|</strong></li>
 * </li>
 * 
 * @author Benjamin_L
 */
@Data
public class FeatureRelevanceParams4SemisupervisedRepresentative<Items, 
																CondEntropyCal extends ConditionalEntropyCalculation,
																MutualInfoEntropyCal extends MutualInformationEntropyCalculation> 
{
	/**
	 * (labeled U)/F<sub>i</sub>
	 */
	private Items equClassesOfLabeledInstances;
	/**
	 * H(F<sub>i</sub>), bases on U/F<sub>i</sub>
	 */
	private double infoEntropyOfEquClassesOfAllInstances;

	/**
	 * (labeled U)/C
	 */
	private Items condEquClassesOfLabeledInstances;
	/**
	 * H(C)
	 */
	private double infoEntropyOfCondEquClassesOfLabeledInstances;
	/**
	 * C
	 */
	private IntegerIterator condAttributes;
	
	/**
	 * &beta;
	 */
	private double tradeOff;
	
	private CondEntropyCal condEntropyCalculation;
	private MutualInfoEntropyCal mutualInfoEntropyCalculation;
	
	private Object[] args;
}