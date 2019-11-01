package featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance;

import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.frame.support.calculation.FeatureRelevance;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.ConditionalEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.InformationEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.MutualInformationEntropyCalculation;

/**
 * Feature relevance calculations for <strong>Semi-supervised Representative Feature Selection</strong> 
 * using Mutual information theory and entropy calculations.
 * <p>
 * Implementations should base on the original paper 
 * <a href="https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242">
 * "An efficient semi-supervised representatives feature selection algorithm based on information theory"</a> 
 * by Yintong Wang, Jiandong Wang, Hao Liao, Haiyan Chen.
 * 
 * @see {@link SemisupervisedRepresentativeStrategy}
 * 
 * @author Benjamin_L
 *
 * @param <V>
 * 		Type of Feature relevance.
 * @param <Items>
 * 		Type of items to be calculated.
 * @param <InfoEntropyCal>
 * 		Type of implemented {@link InformationEntropyCalculation}
 * @param <CondEntropyCal>
 * 		Type of implemented {@link ConditionalEntropyCalculation}
 * @param <MutualInfoEntropyCal>
 * 		Type of implemented {@link MutualInformationEntropyCalculation}
 */
public interface FeatureRelevance4SemisupervisedRepresentative<V, Items,
																CondEntropyCal extends ConditionalEntropyCalculation,
																MutualInfoEntropyCal extends MutualInformationEntropyCalculation> 
	extends FeatureRelevance<V>, 
			SemisupervisedRepresentativeStrategy
{
	public static final String CALCULATION_NAME = "Relevance";
	
	/**
	 * F_Rel(F<sub>i</sub>, F<sub>j</sub>) = &beta; * H(F<sub>i</sub>) + (1-&beta;) * I(F<sub>i</sub>, F<sub>j</sub>), 
	 * where F<sub>j</sub> = C.
	 * <p>
	 * i.e. <strong>F_Rel(F<sub>i</sub>, C) = &beta; * H(F<sub>i</sub>) + (1-&beta;) * I(F<sub>i</sub>, C)</strong>
	 * <p>
	 * I(F<sub>i</sub>, C) = H(C) - H(C|F<sub>i</sub>)
	 * 
	 * @see {@link InformationEntropyCalculation}
	 * @see {@link ConditionalEntropyCalculation}
	 * @see {@link MutualInformationEntropyCalculation}
	 * @see {@link FeatureRelevanceParams4SemisupervisedRepresentative}
	 * 
	 * @param param
	 * 		{@link FeatureRelevanceParams4SemisupervisedRepresentative} instance with parameters: 
	 * 		(labeled U)/F<sub>i</sub>, H(F<sub>i</sub>) of U/F<sub>i</sub>, (labeled U)/C, H(C), C, &beta;, 
	 * 		ConditionalEntropyCalculation, MutualInformationEntropyCalculation.
	 * @return <code>this</code> instance.
	 */
	FeatureRelevance4SemisupervisedRepresentative<V, Items, CondEntropyCal, MutualInfoEntropyCal> 
		calculateFRel(
			FeatureRelevanceParams4SemisupervisedRepresentative<Items, CondEntropyCal, MutualInfoEntropyCal> param
		);
	
	/**
	 * F1_Rel(F<sub>i</sub>, C) = 
	 * 		&beta; * <strong>UI(F<sub>i</sub>)</strong>/H(F<sub>i</sub>) + 
	 * 		(1-&beta;) * <strong>SU(F<sub>i</sub>, C)</strong>
	 * <p>
	 * <strong>UI(F<sub>i</sub>)</strong> 
	 * 		= 1/n &Sigma;<sub>j=1:n</sub> I(F<sub>i</sub>; F<sub>j</sub>)
	 * 		= H(F<sub>i</sub>) - 1/(n-1) * 
	 * 			&Sigma;<sub>j=1:n,j!=i</sub> H(F<sub>i</sub> | F<sub>j</sub>)</strong>,
	 * <p>
	 * where n = |dimension|
	 * <p>
	 * <strong>SU(F<sub>i</sub>, C)</strong> = 2 * [ I(F<sub>i</sub>; C) / ( H(F<sub>i</sub>) + H(C) ) ]
	 * 
	 * @param param
	 * 		{@link FeatureRelevanceParams4SemisupervisedRepresentative} instance with parameters: 
	 * 		labeled U, U, attributes of U, F<sub>i</sub>, (labeled U)/F<sub>i</sub>, H(F<sub>i</sub>) of
	 * 		U/F<sub>i</sub> and (labeled U)/F<sub>i</sub>, H(C) of (labeled U)/C, &beta;, 
	 * 		ConditionalEntropyCalculation, MutualInformationEntropyCalculation.
	 * @return <code>this</code> instance.
	 */
	FeatureRelevance4SemisupervisedRepresentative<V, Items, CondEntropyCal, MutualInfoEntropyCal> 
		calculateF1Rel(
			FeatureRelevance1Params4SemisupervisedRepresentative<Items, CondEntropyCal, MutualInfoEntropyCal> param
		);

	/**
	 * F2_Rel(F<sub>i</sub>, C) = 
	 * 		&beta; * <strong>USU(F<sub>i</sub>, C)</strong> + 
	 * 		(1-&beta;) * <strong>SU(F<sub>i</sub>, C)</strong>
	 * <p>
	 * <strong>USU(F<sub>i</sub>, F<sub>j</sub>)</strong> 
	 * 		= 2 * [ <strong>UI(F<sub>i</sub>; F<sub>j</sub>)</strong> / ( H(F<sub>i</sub>) + H(F<sub>j</sub>) ) ]
	 * <p>
	 * <strong>UI(F<sub>i</sub>; F<sub>j</sub>)</strong> 
	 * 		= <strong>UI(F<sub>i</sub>)</strong> - <strong>UI(F<sub>i</sub>|F<sub>j</sub>)</strong>
	 * <p>
	 * <strong>UI(F<sub>i</sub>)</strong> 
	 * 		= 1/n &Sigma;<sub>j=1:n</sub> I(F<sub>i</sub>; F<sub>j</sub>)
	 * 		= H(F<sub>i</sub>) - 1/(n-1) * 
	 * 			&Sigma;<sub>j=1:n,j!=i</sub> H(F<sub>i</sub> | F<sub>j</sub>)</strong>,
	 * <p>
	 * <strong>UI(F<sub>i</sub>|F<sub>j</sub>)</strong> 
	 * 		= UI(F<sub>i</sub>) / H(F<sub>i</sub>) * H(F<sub>i</sub> | F<sub>j</sub>)
	 * <p>
	 * 
	 * @param param
	 * 		{@link FeatureRelevance2Params4SemisupervisedRepresentative} instance with parameters: 
	 * 		labeled U, U, attributes of U, F<sub>i</sub>, (labeled U)/F<sub>i</sub>, H(F<sub>i</sub>) of
	 * 		U/F<sub>i</sub> and (Labeled U)/F<sub>i</sub>, F<sub>j</sub>, H(F<sub>j</sub>) of 
	 * 		U/F<sub>j</sub> and (Labeled U)/F<sub>j</sub>, &beta; 
	 * 		ConditionalEntropyCalculation, MutualInformationEntropyCalculation, 
	 * 		SymmetricalUncertaintyCalculation.
	 * @return <code>this</code> instance.
	 */
	FeatureRelevance4SemisupervisedRepresentative<V, Items, CondEntropyCal, MutualInfoEntropyCal> 
		calculateF2Rel(
			FeatureRelevance2Params4SemisupervisedRepresentative<Items, CondEntropyCal, MutualInfoEntropyCal> param
		);
}
