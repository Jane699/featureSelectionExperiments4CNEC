package featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack;

import basic.model.interf.Calculation;
import featureSelection.repository.frame.support.algStrategy.semisupervisedRepresentative.SemisupervisedRepresentativeStrategy;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.conditionalEntropy.semisupervisedRepresentative.ConditionalEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.informationEntropy.semisupervisedRepresentative.InformationEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.mutualInformationEntropy.semisupervisedRepresentative.MutualInformationEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.markovBlanket.approximate.symmetricalUncertainty.mutualInformationEntropy.MutualInformationEntropyBasedSymmetricalUncertaintyCalculation;
import featureSelection.repository.implement.support.calculation.relevance.semisupervisedRepresentative.FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased;
import lombok.Data;

/**
 * A class for integrating <strong>Semi-supervised Representative Feature Selection</strong> Calculations 
 * using entropy calculation.
 * <p>
 * Generally, this class serves as a container to contain {@link Calculation}s used in SRFS calculations,
 * including:
 * <li>{@link FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased}: relevanceCalculation</li>
 * <li>{@link InformationEntropyCalculation4SemisupervisedRepresentative}: infoEntropyCalculation</li>
 * <li>{@link ConditionalEntropyCalculation4SemisupervisedRepresentative}: condEntropyCalculation</li>
 * <li>{@link MutualInformationEntropyCalculation4SemisupervisedRepresentative}: mutualInfoEntropyCalculation</li>
 * <li>{@link MutualInformationEntropyBasedSymmetricalUncertaintyCalculation}: symmetricalUncertaintyCalculation</li>
 * 
 * @author Benjamin_L
 */
@Data
public class SemisupervisedRepresentativeCalculations4EntropyBased 
	implements SemisupervisedRepresentativeStrategy,
				Calculation<Object>
{
	public static final String CALCULATION_NAME = "SRFS-Entropy+Relevance";
	
	private FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased relevanceCalculation;
	private InformationEntropyCalculation4SemisupervisedRepresentative infoEntropyCalculation;
	private ConditionalEntropyCalculation4SemisupervisedRepresentative condEntropyCalculation;
	private MutualInformationEntropyCalculation4SemisupervisedRepresentative mutualInfoEntropyCalculation;
	private MutualInformationEntropyBasedSymmetricalUncertaintyCalculation symmetricalUncertaintyCalculation;

	public SemisupervisedRepresentativeCalculations4EntropyBased() {
		relevanceCalculation = new FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased();
		infoEntropyCalculation = new InformationEntropyCalculation4SemisupervisedRepresentative();
		condEntropyCalculation = new ConditionalEntropyCalculation4SemisupervisedRepresentative();
		mutualInfoEntropyCalculation = new MutualInformationEntropyCalculation4SemisupervisedRepresentative();
		symmetricalUncertaintyCalculation = new MutualInformationEntropyBasedSymmetricalUncertaintyCalculation();
	}
	
	public long sumCalculationTimes() {
		return relevanceCalculation.getCalculationTimes() + 
				infoEntropyCalculation.getCalculationTimes() +
				condEntropyCalculation.getCalculationTimes() +
				mutualInfoEntropyCalculation.getCalculationTimes() +
				symmetricalUncertaintyCalculation.getCalculationTimes();
	}
	
	public long sumCalculationAttributeLength() {
		return relevanceCalculation.getCalculationAttributeLength() + 
				infoEntropyCalculation.getCalculationAttributeLength() +
				condEntropyCalculation.getCalculationAttributeLength() +
				mutualInfoEntropyCalculation.getCalculationAttributeLength();
	}

	@Override
	public Object getResult() {
		throw new RuntimeException("Unimplemented method!");
	}

	/**
	 * Get the sum of calculation times.
	 * 
	 * @see {@link #sumCalculationTimes()}
	 */
	@Override
	public long getCalculationTimes() {
		return sumCalculationTimes();
	}
	
	/**
	 * Get the sum of calculation attribute length.
	 * 
	 * @see {@link #sumCalculationAttributeLength()}
	 */
	public long getCalculationAttributeLength() {
		return sumCalculationAttributeLength();
		
	}
}