package featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance;

import java.util.Collection;

import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.ConditionalEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.InformationEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.MutualInformationEntropyCalculation;
import featureSelection.repository.implement.support.calculation.markovBlanket.approximate.symmetricalUncertainty.mutualInformationEntropy.MutualInformationEntropyBasedSymmetricalUncertaintyCalculation;
import lombok.Data;

/**
 * An entity for <strong>Semi-supervised Representative</strong> Feature Relevance 1 calculation parameters.
 * (F_Rel1) With the following fields/parameters:
 * <li><strong>{@link #labeledInstances}</strong>: {@link Collection}
 * <p>	Labeled {@link UniverseInstance} {@link Collection}. (i.e. <strong>labeled U</strong>)
 * </li>
 * <li><strong>{@link #allInstances}</strong>: {@link Collection}
 * <p>	Labeled+Unlabeled {@link UniverseInstance} {@link Collection}. (i.e. <strong>U</strong>)
 * </li>
 * <li><strong>{@link #attributes}</strong>: {@link int[]}
 * <p>	All attributes of {@link UniverseInstance}.
 * </li>
 * <li><strong>{@link #equClassesAttribute}</strong>: {@link int}
 * <p>	Attributes used in Equivalent Classes partitioning: <strong>F<sub>i</sub></strong>. For 
 * 		calculations of UI(F<sub>i</sub>), H(F<sub>i</sub> | F<sub>j</sub>) where j in 1:n and j!=i
 * 		and n = |attributes|.
 * </li>
 * <li><strong>{@link #equClassesOfLabeledUniverses}</strong>: {@link Collection}
 * <p>	Equivalent Classes partitioned by F<sub>i</sub>: <strong>(labeled U)/F<sub>i</sub></strong>. 
 * 		For calculations of H(C|F<sub>i</sub>).
 * </li>
 * <li><strong>{@link #infoEntropyOfEquClassesOfLabeledUniverses}</strong>: {@link double}
 * <p>	Information entropy of F<sub>i</sub>: <strong>H(F<sub>i</sub>)</strong>, using labeled 
 * 		{@link UniverseInstance}s only.
 * </li>
 * <li><strong>{@link #infoEntropyOfEquClassesOfAllUniverses}</strong>: {@link double}
 * <p>	Information entropy of F<sub>i</sub>: <strong>H(F<sub>i</sub>)</strong>, using labeled 
 * 		and unlabeled {@link UniverseInstance}s.
 * </li>
 * <li><strong>{@link #condAttributes}</strong>: {@link int}
 * <p>	Attributes used in the partitioning of U/C, i.e. <strong>C</strong>.
 * </li>
 * <li><strong>{@link #condInfoEntropy}</strong>: {@link double}
 * <p>	Information entropy of C: <strong>H(C)</strong>.
 * </li>
 * <li><strong>{@link #tradeOff}</strong>: {@link double}
 * <p>	Superivsed/Semi-supervised/Un-supervised trade-off, marked as <strong>&beta;</strong>.
 * <p>	<strong>supervise</strong> only:    &beta; = <strong>0</strong>; 
 * <p>	<strong>un-supervise</strong> only: &beta; = <strong>1</strong>; 
 * <p>	<strong>semi-supervise</strong>:    &beta; = <strong>(0, 1)</strong>.
 * </li>
 * <li><strong>{@link #condEntropyCalculation}</strong>: {@link condEntropyCalculation}
 * <p>	{@link CondEntropyCal} for <strong>H(F<sub>i</sub>|F<sub>j</sub>)</strong>.
 * </li>
 * <li><strong>{@link #mutualInfoEntropyCalculation}</strong>: {@link InformationEntropyCalculation}
 * <p>	{@link MutualInfoEntropyCal} for <strong>I(F<sub>i</sub>; F<sub>j</sub>)</strong>.
 * </li>
 * <li><strong>{@link #args}</strong>: {@link Object[]}
 * <p>	Extra arguments.
 * </li>
 * 
 * @author Benjamin_L
 */
@Data
public class FeatureRelevance1Params4SemisupervisedRepresentative<Items, 
																CondEntropyCal extends ConditionalEntropyCalculation,
																MutualInfoEntropyCal extends MutualInformationEntropyCalculation> 
{
	/**
	 * Labeled Universe instances
	 */
	private Collection<UniverseInstance> labeledInstances;
	/**
	 * Unlabeled+Labeled Universe instances
	 */
	private Collection<UniverseInstance> allInstances;
	/**
	 * All attributes of {@link UniverseInstance}
	 */
	private int[] attributes;

	/**
	 * F<sub>i</sub>
	 */
	private int equClassesAttribute;
	/**
	 * (labeled U)/F<sub>i</sub>
	 */
	private Collection<Collection<UniverseInstance>> equClassesOfLabeledUniverses;
	/**
	 * H(F<sub>i</sub>), bases on U/F<sub>i</sub>.
	 */
	private double infoEntropyOfEquClassesOfLabeledUniverses;
	/**
	 * H(F<sub>i</sub>), bases on (labeled U)/F<sub>i</sub>.
	 */
	private double infoEntropyOfEquClassesOfAllUniverses;

	/**
	 * C
	 */
	private IntegerIterator condAttributes;
	/**
	 * H(C), bases on (labeled U)/C.
	 */
	private double infoEntropyOfCondEquClassesOfLabeledUniverses;
	
	/**
	 * &beta;
	 */
	private double tradeOff;
	
	private CondEntropyCal condEntropyCalculation;
	private MutualInfoEntropyCal mutualInfoEntropyCalculation;
	private MutualInformationEntropyBasedSymmetricalUncertaintyCalculation symmetricalUncertaintyCalculation;

	private Object[] args;
}