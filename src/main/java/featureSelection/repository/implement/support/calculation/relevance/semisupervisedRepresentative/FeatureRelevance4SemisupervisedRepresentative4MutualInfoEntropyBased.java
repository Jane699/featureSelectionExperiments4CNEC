package featureSelection.repository.implement.support.calculation.relevance.semisupervisedRepresentative;

import java.util.Collection;

import basic.model.imple.integerIterator.IntegerArrayIterator;
import basic.model.interf.IntegerIterator;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.semisupervisedRepresentative.SemisupervisedRepresentativeAlgorithm;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.SemisupervisedRepresentativeCalculations4EntropyBased;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache.InformationEntropyCache;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.cache.InformationEntropyCacheResult;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevance1Params4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevance2Params4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevance4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.semisupervisedRepresentative.featureRelevance.FeatureRelevanceParams4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.conditionalEntropy.semisupervisedRepresentative.ConditionalEntropyCalculation4SemisupervisedRepresentative;
import featureSelection.repository.implement.support.calculation.entropy.mutualInformationEntropy.mutualInformationEntropy.semisupervisedRepresentative.MutualInformationEntropyCalculation4SemisupervisedRepresentative;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * An implementation of {@link FeatureRelevance4SemisupervisedRepresentative} for the calculation of 
 * feature relation for <strong>Semi-supervised Representative</strong> which bases on Mutual Info.
 * theory and Entropy calculations.
 * 
 * @author Benjamin_L
 */
@Slf4j
public class FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased
	implements FeatureRelevance4SemisupervisedRepresentative<Double, 
															Collection<Collection<UniverseInstance>>,
															ConditionalEntropyCalculation4SemisupervisedRepresentative,
															MutualInformationEntropyCalculation4SemisupervisedRepresentative>
{
//	private static boolean logOn = false;
	
	@Getter protected long calculationTimes = 0;
	@Getter protected long calculationAttributeLength = 0;
	public void countCalculate(int attrLen) {
		calculationTimes++;
		calculationAttributeLength += attrLen;
	}
	
	@Getter private Double result;
	
	/**
	 * F<sub>j</sub> = C. So, put it into F_Rel(F<sub>i</sub>, F<sub>j</sub>) and I(F<sub>i</sub>, F<sub>j</sub>):
	 * <li><strong>F_Rel(F<sub>i</sub>, C)</strong> = &beta; * H(F<sub>i</sub>) + (1-&beta;) * I(F<sub>i</sub>, C)</li>
	 * <li><strong>I(F<sub>i</sub>, C)</strong> = H(C) - H(C|F)</li>
	 * <p>
	 * Parameter used in calculations are marked below:
	 * <table>
	 * 	<tr align="center">	
	 * 		<td> </td>						<td>Un-supervised</td>		<td>Supervised</td>	
	 * 	</tr>
	 * 	<tr align="center">	
	 * 		<td> </td>						<td>H(F<sub>i</sub>)</td>	<td>I(F<sub>i</sub>, C)</td>	
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>(labeled U)/F<sub>i</sub></td>
	 * 										<td> </td>					<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>i</sub>), bases on U/F<sub>i</sub></td>		
	 * 										<td>√</td>					<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>(labeled U)/C</td>			<td> </td>					<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(C)</td>					<td> </td>					<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>C</td>						<td> </td>					<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>|labeled U|</td>			<td> </td>					<td>√</td>
	 * 	</tr>
	 * </table>
	 * <p>
	 * If &beta; = 0, supervise only case. <strong>H(F<sub>i</sub>)</strong> is <strong>ignored</strong> 
	 * and relevant calculations are be Skipped.
	 * <p>
	 * If &beta; = 1, un-supervise only case. <strong>I(F<sub>i</sub>, C)</strong> is <strong>ignored</strong>.
	 * 
	 * @see {@link FeatureRelevanceParams4SemisupervisedRepresentative}
	 */
	@Override
	public FeatureRelevance4SemisupervisedRepresentative<Double, Collection<Collection<UniverseInstance>>, 
				ConditionalEntropyCalculation4SemisupervisedRepresentative, 
				MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
	calculateFRel(
			FeatureRelevanceParams4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
				ConditionalEntropyCalculation4SemisupervisedRepresentative, 
				MutualInformationEntropyCalculation4SemisupervisedRepresentative> param
	) {
		countCalculate(0);
		
		// Initiate
		int labeledUniverseSize = (int) param.getArgs()[0];
		
		double mutualInfoEntropy;
		// β != 1, semi-supervise or supervise case.
		if (Double.compare(1, param.getTradeOff())!=0) {
			// Calculate H(C|F<sub>i</sub>), bases on labeled U
			double condEntropy = param.getCondEntropyCalculation()
										.calculate(
											// (labeled U)/F[i]
											param.getEquClassesOfLabeledInstances(), 
											// C
											param.getCondAttributes(), 
											// |labeled U|
											labeledUniverseSize
										).getResult().doubleValue();
			// Calculate I(F<sub>i</sub>, C)
			mutualInfoEntropy = param.getMutualInfoEntropyCalculation()
										.calculate(
											// H(C), bases on labeled U
											param.getInfoEntropyOfCondEquClassesOfLabeledInstances(), 
											// H(C|F<sub>i</sub>), bases on labeled U
											condEntropy
										).getResult().doubleValue();
		// β = 1, un-supervise case.
		}else {
			// H(C|F<sub>i</sub>) is ignored in the calculation of F_Rel(F<sub>i</sub>, C)
			mutualInfoEntropy = 0;
		}
		
		// F_Rel(F<sub>i</sub>, C) = &beta; * H(F<sub>i</sub>) + (1-&beta;) * I(F<sub>i</sub>, C)
		result = param.getTradeOff() * param.getInfoEntropyOfEquClassesOfAllInstances() + 
					(1-param.getTradeOff()) * mutualInfoEntropy;
		return this;
	}

	/**
	 * <strong>F1_Rel(F<sub>i</sub>, C)</strong> = 
	 * 			&beta; * UI(F<sub>i</sub>)/H(F<sub>i</sub>) + (1-&beta;) * SU(F<sub>i</sub>, C)
	 * <p>
	 * <strong>UI(F<sub>i</sub>)</strong> = H(F<sub>i</sub>) - 
	 * 			1/(n-1) * &Sigma;<sub>j=1:n,j!=i</sub> H(F<sub>i</sub> | F<sub>j</sub>)</strong>,
	 * where n = |dimension|
	 * <p>
	 * <strong>SU(F<sub>i</sub>, C)</strong> = 2 * [ I(F<sub>i</sub>; C) / ( H(F<sub>i</sub>) + H(C) ) ]
	 * <p>
	 * Parameter used in calculations are marked below:
	 * <table>
	 * 	<tr align="center">	
	 * 		<td> </td>
	 * 		<td>Un-supervised</td>						<td>Supervised</td>	
	 * 	</tr>
	 * 	<tr align="center">	
	 * 		<td> </td>
	 * 		<td>UI(F<sub>i</sub>)/H(F<sub>i</sub>)</td>	<td>SU(F<sub>i</sub>, C)</td>	
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>labeled U</td>
	 * 		<td> </td>									<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>U</td>
	 * 		<td>√</td>									<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>All attributes of U</td>
	 * 		<td>√</td>									<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>F<sub>i</sub></td>
	 * 		<td>√</td>									<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>(labeled U)/F<sub>i</sub></td>
	 * 		<td> </td>									<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>i</sub>) by U//F<sub>i</sub></td>
	 * 		<td>√</td>									<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>i</sub>) by (labeled U)/F<sub>i</sub></td>
	 * 		<td> </td>									<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>C</td>
	 * 		<td> </td>									<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(C) by (labeled U)/C</td>
	 * 		<td> </td>									<td>√</td>
	 * 	</tr>
	 * </table>
	 * <p> 
	 * If &beta; = 0, supervise only case. <strong>UI(F<sub>i</sub>)</strong>, <strong>H(F<sub>i</sub>)</strong>
	 * are ignored and relevant calculations are Skipped.
	 * <p>
	 * If &beta; = 1, un-supervise only case. <strong>SU(F<sub>i</sub>, C)</strong> is <strong>ignored</strong> 
	 * and relevant calculations are Skipped.
	 */
	@Override
	public FeatureRelevance4SemisupervisedRepresentative<Double, Collection<Collection<UniverseInstance>>, 
				ConditionalEntropyCalculation4SemisupervisedRepresentative, 
				MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
	calculateF1Rel(
			FeatureRelevance1Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
				ConditionalEntropyCalculation4SemisupervisedRepresentative, 
				MutualInformationEntropyCalculation4SemisupervisedRepresentative> param
	) {
		countCalculate(param.getAttributes().length-1);
		
		double ui;
		// &beta; > 0, semi-supervise or un-supervise case.
		if (Double.compare(0, param.getTradeOff())!=0) {
			// Calculate UI(F<sub>i</sub>)
			ui = calculateRelevanceOfAFeature(
					// U, D
					param.getAllInstances(), param.getAttributes(),
					// U/F<sub>i</sub>
					param.getEquClassesAttribute(), 
					// H(U/F<sub>i</sub>)
					param.getInfoEntropyOfEquClassesOfAllUniverses(),
					param.getCondEntropyCalculation()
				);
		// &beta; = 0, supervise case.
		}else {
			// UI(F<sub>i</sub>) is ignored in the calculation of F1_Rel(F<sub>i</sub>, C)
			ui = 0;
		}
		
		double su;
		// &beta; < 1, semi-supervise or supervise case.
		if (Double.compare(1, param.getTradeOff())!=0) {
			// SU(F<sub>i</sub>, C) = 2 * [ I(F<sub>i</sub>; C) / ( H(F<sub>i</sub>) + H(C) ) ]
			//	Calculate H(C|F<sub>i</sub>)
			double condEntropy = param.getCondEntropyCalculation()
									.calculate(
										// (labeled U) / F<sub>i</sub>
										param.getEquClassesOfLabeledUniverses(), 
										// C
										param.getCondAttributes(), 
										// |labeled U|
										param.getLabeledInstances().size()
									)
									.getResult().doubleValue();
			//	Calculate I(F<sub>i</sub>; C)
			double mutualInfoEntropy = param.getMutualInfoEntropyCalculation()
											.calculate(
												// H(C)
												param.getInfoEntropyOfCondEquClassesOfLabeledUniverses(), 
												// H(C|F)
												condEntropy
											).getResult().doubleValue();
			//		I(F<sub>i</sub>; C), , H(C)
			su = param.getSymmetricalUncertaintyCalculation()
						.calculate(
							// I(F<sub>i</sub>; C)
							mutualInfoEntropy, 
							// H(F<sub>i</sub>) bases on (labeled U)/F<sub>i</sub>
							param.getInfoEntropyOfEquClassesOfLabeledUniverses(), 
							// H(C)
							param.getInfoEntropyOfCondEquClassesOfLabeledUniverses()
						).getResult().doubleValue();
		// &beta; = 1, un-supervise case.
		}else {
			// SU(F<sub>i</sub>, C) is ignored in the calculation of F1_Rel(F<sub>i</sub>, C)
			su = 0;
		}
		
		// F1_Rel(F<sub>i</sub>, C) = &beta; * UI(F<sub>i</sub>)/H(F<sub>i</sub>) + 
		//								(1-&beta;) * SU(F<sub>i</sub>, C)
		result =  (Double.compare(param.getTradeOff(), 0)==0? 
					0: param.getTradeOff() * ui / param.getInfoEntropyOfEquClassesOfAllUniverses()
				  ) + (1-param.getTradeOff()) * su;
		return this;
	}
	
	/**
	 * <strong>F2_Rel(F<sub>i</sub>, F<sub>j</sub>)</strong> = 
	 * 		&beta; * <strong>USU(F<sub>i</sub>, F<sub>j</sub>)</strong> + 
	 * 		(1-&beta;) * <strong>SU(F<sub>i</sub>, F<sub>j</sub>)</strong>
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
	 * <strong>SU(F<sub>i</sub>, F<sub>j</sub>)</strong> = 2 * [ I(F<sub>i</sub>; F<sub>j</sub>) / ( H(F<sub>i</sub>) + H(F<sub>j</sub>) ) ]
	 * <p>
	 * Parameter used in calculations are marked below:
	 * <table>
	 * 	<tr align="center">	
	 * 		<td> </td>						<td>Un-supervised</td>			<td>Supervised</td>	
	 * 	</tr>
	 * 	<tr align="center">	
	 * 		<td> </td>						<td>USU(F<sub>i</sub>, C)</td>	<td>SU(F<sub>i</sub>, C)</td>	
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>Labeled U</td>				<td> </td>						<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>U</td>						<td>√</td>						<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>All attributes of U</td>	<td>√</td>						<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>F<sub>i</sub></td>			<td>√</td>						<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>(labeled U)/F<sub>i</sub></td>		
	 * 										<td> </td>						<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>i</sub>), bases on U/F<sub>i</sub></td>		
	 * 										<td>√</td>						<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>i</sub>), bases on (Labeled U)/F<sub>i</sub></td>		
	 * 										<td> </td>						<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>F<sub>j</sub></td>			<td>√</td>						<td>√</td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>j</sub>), bases on U/F<sub>i</sub></td>		
	 * 										<td>√</td>						<td> </td>
	 * 	</tr>
	 * 	<tr align="center">
	 * 		<td>H(F<sub>j</sub>), bases on (labeled U)/F<sub>i</sub></td>		
	 * 										<td> </td>						<td>√</td>
	 * 	</tr>
	 * </table>
	 * <p> 
	 * If &beta; = 0, supervise only case. 
	 * <strong>USU(F<sub>i</sub>, C)</strong> is <strong>ignored</strong> and relevant calculations of it 
	 * are Skipped.
	 * <p>
	 * If &beta; = 1, un-supervise only case. <strong>SU(F<sub>i</sub>, C)</strong> is <strong>ignored</strong> 
	 * and relevant calculations of it are Skipped.
	 */
	@Override
	public FeatureRelevance4SemisupervisedRepresentative<Double, Collection<Collection<UniverseInstance>>,
				ConditionalEntropyCalculation4SemisupervisedRepresentative, 
				MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
	calculateF2Rel(
			FeatureRelevance2Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
				ConditionalEntropyCalculation4SemisupervisedRepresentative, 
				MutualInformationEntropyCalculation4SemisupervisedRepresentative> param
	) {
		double usu;
		// &beta; > 0, semi-supervise or un-supervise case.
		if (Double.compare(0, param.getTradeOff())!=0) {
			// Calculate UI(F<sub>i</sub>)
			double ui = calculateRelevanceOfAFeature(
							// U
							param.getAllInstances(), 
							// F
							param.getAttributes(),
							// F<sub>i</sub>
							param.getEquClassesAttribute(), 
							// H(F<sub>i</sub>)
							param.getInfoEntropyOfEquClassesOfAllInstances(),
							param.getCondEntropyCalculation()
						);
			// Calculate UI(F<sub>i</sub>; F<sub>j</sub>)
			double relevanceGain = calculateRelevanceGainBetweenFeatures(
										// U
										param.getAllInstances(),
										// F<sub>i</sub>
										new IntegerArrayIterator(param.getEquClassesAttribute()), 
										// F<sub>j</sub>
										param.getCondAttributes(),
										// UI(F<sub>i</sub>)
										ui, 
										// H(F<sub>i</sub>)
										param.getInfoEntropyOfEquClassesOfAllInstances(),
										param.getCondEntropyCalculation()
									);
			// USU(F<sub>i</sub>, F<sub>j</sub>)
			//		= 2 * [ UI(F<sub>i</sub>; F<sub>j</sub>) / ( H(F<sub>i</sub>) + H(F<sub>j</sub>) ) ]
			usu = 2 * ( relevanceGain / 
						(param.getInfoEntropyOfEquClassesOfAllInstances() + 
						 param.getInfoEntropyOfConEquClassesOfAllInstances()) 
					);
		// &beta; = 0, supervise case.
		}else {
			// USU(F<sub>i</sub>, F<sub>j</sub>) is ignored in the calculation of F2_Rel(F<sub>i</sub>, F<sub>j</sub>)
			usu = 0;
		}

		double su;
		// &beta; < 1, semi-supervise or supervise case.
		if (Double.compare(1, param.getTradeOff())!=0) {
			// SU(F<sub>i</sub>, F<sub>j</sub>) = 
			//		2 * [ I(F<sub>i</sub>; F<sub>j</sub>) / ( H(F<sub>i</sub>) + H(F<sub>j</sub>) ) ]
			//	Calculate H(F<sub>j</sub>|F<sub>i</sub>)
			double condEntropy = param.getCondEntropyCalculation()
									.calculate(
										// (labeled U)/F<sub>i</sub>
										param.getEquClassesOfLabeledInstances(), 
										// F<sub>j</sub>
										param.getCondAttributes(), 
										// |labeled U|
										param.getLabeledInstances().size()
									).getResult().doubleValue();
			//	Calculate I(F<sub>i</sub>; F<sub>j</sub>)
			double mutualInfoEntropy = param.getMutualInfoEntropyCalculation()
											.calculate(
												// H(F<sub>j</sub>), bases on labeled U
												param.getInfoEntropyOfConEquClassesOfLabeledInstances(), 
												// H(F<sub>j</sub>|F<sub>i</sub>)
												condEntropy
											)
											.getResult().doubleValue();
			su = param.getSymmetricalUncertaintyCalculation()
							.calculate(
								// I(F<sub>i</sub>; F<sub>j</sub>)
								mutualInfoEntropy, 
								// H(F<sub>i</sub>), bases on labeled U
								param.getInfoEntropyOfEquClassesOfLabeledInstances(), 
								// H(F<sub>j</sub>),  bases on labeled U
								param.getInfoEntropyOfConEquClassesOfLabeledInstances()
							).getResult().doubleValue();
		// &beta; = 1, un-supervise case.
		}else {
			// SU(F<sub>i</sub>, F<sub>j</sub>) is ignored in the calculation of F2_Rel(F<sub>i</sub>, F<sub>j</sub>)
			su = 0;
		}
		
		// F2_Rel(F<sub>i</sub>, F<sub>j</sub>) = 
		//		&beta; * USU(F<sub>i</sub>, C) + (1-&beta;) * SU(F<sub>i</sub>, F<sub>j</sub>)
		result = param.getTradeOff() * usu + (1-param.getTradeOff()) * su;
		return this;
	}

	
	/**
	 * <strong>UI(F<sub>i</sub>)</strong> 
	 * 		= 1/n &Sigma;<sub>j=1:n</sub> I(F<sub>i</sub>; F<sub>j</sub>)
	 * 		= H(F<sub>i</sub>) - 1/(n-1) * &Sigma;<sub>j=1:n,j!=i</sub> H(F<sub>i</sub> | F<sub>j</sub>)</strong>,
	 * <p>
	 * where n = |dimension|
	 * 
	 * @param universes
	 * 		{@link UniverseInstance} {@link Collection}.
	 * @param attributes
	 * 		Attributes of {@link UniverseInstance}.
	 * @param equClassesAttribute
	 * 		An attribute which is used in partitioning {@link UniverseInstance}s into Equivalent 
	 * 		classes: <strong>F<sub>i</sub></strong>.
	 * @param equClassesInfoEntropy
	 * 		Information entropy of F<sub>i</sub> which is used in partitioning {@link UniverseInstance}s into
	 * 		Equivalent classes: <strong>H(F<sub>i</sub>)</strong>.
	 * @param condEntropyCalculation
	 * 		{@link ConditionalEntropyCalculation4SemisupervisedRepresentative} instance.
	 * @return Relevance of a feature UI(F<sub>i</sub>).
	 */
	public static double calculateRelevanceOfAFeature(
			Collection<UniverseInstance> universes, int[] attributes, 
			int equClassesAttribute, double equClassesInfoEntropy,
			ConditionalEntropyCalculation4SemisupervisedRepresentative condEntropyCalculation
	) {
		// Calculate the sum of H(F<sub>i</sub> | F<sub>j</sub>) where j ranging from [1, n] and j!=i.
		double condEntropy, sum = 0;
		Collection<Collection<UniverseInstance>> condEquClasses;
		// Go through F<sub>j</sub> where j ranging from [1, n] and j!=i
		for (int attr: attributes) {
			// Skip F<sub>i</sub>
			if (attr==equClassesAttribute)	continue;
			// Calculate H(F<sub>i</sub> | F<sub>j</sub>)
			//	Calculate U/F<sub>j</sub>
			condEquClasses = 
				SemisupervisedRepresentativeAlgorithm
					.Basic
					.equivalentClass(universes, new IntegerArrayIterator(attr))
					.values();
			//	U/F<sub>j</sub>, F<sub>i</sub>,  |U|
			condEntropy = condEntropyCalculation.calculate(condEquClasses, new IntegerArrayIterator(equClassesAttribute), universes.size())
												.getResult().doubleValue();
//			if (logOn)	log.info("H(F[{}] | F[{}]) = {}", equClassesAttribute, attr, condEntropy);
			// sum += H(F<sub>i</sub> | F<sub>j</sub>)
			sum += condEntropy;
		}
//		if (logOn)	log.info("sum(H(F[{}] | F[1:n])) = {}", equClassesAttribute, sum);
		// relevance = H(F<sub>i</sub>) - 1/(n-1) * &Sigma;<sub>j=1:n,j!=i</sub> H(F<sub>i</sub> | F<sub>j</sub>)</strong>
		//			 = H(F<sub>i</sub>) - 1/(n-1) * sum
		double relevance = equClassesInfoEntropy - 1.0 / (attributes.length-1) * sum;
//		if (logOn)	log.info("relevance = {} - 1/({}-1) * {} = {}", equClassesInfoEntropy, attributes.length, sum, relevance);
		return relevance;
	}

	/**
	 * <strong>UI(F<sub>i</sub>|F<sub>j</sub>)</strong> 
	 * 		= UI(F<sub>i</sub>) / H(F<sub>i</sub>) * H(F<sub>i</sub> | F<sub>j</sub>)
	 * 
	 * @param universes
	 * 		{@link UniverseInstance} {@link Collection}.
	 * @param gainAttributes
	 * 		Gaining Features/Attributes of {@link UniverseInstance}: <strong>F<sub>i</sub></strong>.
	 * @param condAttributes
	 * 		Conditional Features/Attributes of {@link UniverseInstance}: <strong>F<sub>j</sub></strong>.
	 * @param relevanceOfFi
	 * 		Feature relevance of <code>fi</code>: <strong>UI(F<sub>i</sub>)</strong>
	 * @param infoEntropyOfFi
	 * 		Information entropy of <code>fi</code>: <strong>H(F<sub>i</sub>)</strong>
	 * @param condEntropyCal
	 * 		{@link ConditionalEntropyCalculation4SemisupervisedRepresentative} instance for calculations
	 * 		of H(F<sub>i</sub> | F<sub>j</sub>)
	 * @return Relevance gain <strong>UI(F<sub>i</sub>|F<sub>j</sub>)</strong>.
	 */
	public static double calculateRelevanceGainBetweenFeatures(
			Collection<UniverseInstance> universes, 
			IntegerIterator gainAttributes, IntegerIterator condAttributes, 
			double relevanceOfGain, double infoEntropyOfGain,
			ConditionalEntropyCalculation4SemisupervisedRepresentative condEntropyCal
	) {
		// Calculate H(F<sub>i</sub> | F<sub>j</sub>)
		//	Calculate U/F<sub>j</sub>
		Collection<Collection<UniverseInstance>> condEquClasses = 
			SemisupervisedRepresentativeAlgorithm
				.Basic
				.equivalentClass(universes, condAttributes)
				.values();
		//		U/F<sub>j</sub>
		double condInfoEntropy = condEntropyCal.calculate(condEquClasses, gainAttributes, universes.size())
												.getResult().doubleValue();
		// UI(F<sub>i</sub>|F<sub>j</sub>) = 
		//		UI(F<sub>i</sub>) / H(F<sub>i</sub>) * H(F<sub>i</sub> | F<sub>j</sub>)
		double relevanceGain = relevanceOfGain / infoEntropyOfGain * condInfoEntropy;
		return relevanceGain;
	}

	/**
	 * Load parameters only if they are needed in the future calculations. (Judge by trade-off, i.e. &beta;)
	 * 
	 * @author Benjamin_L
	 */
	public static class ParameterLoader {
		/**
		 * Construct and load parameters for Feature Relevance Calculation based on the given trade-off
		 * value(i.e. &beta;).
		 * <p>
		 * <i>H(F[i])</i>, bases on U/F[i] is loaded if <strong>&beta; != 0</strong> for
		 * 		calculations of <i>&beta; * H(F[i])</i>.
		 * <p>
		 * <i>(labeled U)/F[i]</i>, <i>C</i>, <i>H(C)</i> are loaded if <strong>&beta; != 1</strong> for
		 * 		calculations of <i>(1-&beta;) * I(F[i], C)</i>.
		 * 
		 * @see {@link FeatureRelevanceParams4SemisupervisedRepresentative}
		 * @see {@link FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased#calculateFRel(
		 * 				FeatureRelevanceParams4SemisupervisedRepresentative)}
		 * 
		 * @param tradeOff
		 * 		Trade-off value: &beta;, ranging from 0 to 1. If &beta; = 0, supervise only case; If &beta; 
		 * 		= 1, un-supervise only case.
		 * @param labeledUniverses
		 * 		A {@link Collection} of labeled {@link UniverseInstance}.
		 * @param allUniverses
		 * 		A {@link Collection} of labeled and unlabeled {@link UniverseInstance}.
		 * @param attribute
		 * 		The attribute to be calculated F-Rel.
		 * @param decisionInfoEntropy
		 * 		Info. entropy value calculate by decision attribute: H(D) where D is the decision attribute.
		 * @param calculation
		 * 		{@link SemisupervisedRepresentativeCalculations4EntropyBased} instance.
		 * @return loaded {@link FeatureRelevanceParams4SemisupervisedRepresentative} instance.
		 */
		public static FeatureRelevanceParams4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
						ConditionalEntropyCalculation4SemisupervisedRepresentative, 
						MutualInformationEntropyCalculation4SemisupervisedRepresentative>
		loadFeatureRelevanceCalculationParamters(
				double tradeOff, 
				Collection<UniverseInstance> labeledUniverses, Collection<UniverseInstance> allUniverses,
				int attribute, double decisionInfoEntropy,
				SemisupervisedRepresentativeCalculations4EntropyBased calculation,
				InformationEntropyCache infoEntropyCache
		) {
			FeatureRelevanceParams4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
					ConditionalEntropyCalculation4SemisupervisedRepresentative, 
					MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
				param4FRel = new FeatureRelevanceParams4SemisupervisedRepresentative<>();

			// β = (0, 1]. Semi-supervised, un-supervised.
			if (Double.compare(0, tradeOff)!=0) {
				// -----------------------------------------------------------------------------------
//				log.info("β = (0, 1]");
				// -----------------------------------------------------------------------------------
				// U/F[i] and H(F[i]), bases on U/F[i]
				double infoEntropyOfEquClassesOfAllU = 
						infoEntropyCache.calculateByAllUniverses(
							attribute, 
							calculation.getInfoEntropyCalculation(), 
							allUniverses
						).getEntropyValue();
				// -----------------------------------------------------------------------------------
				// Set H(F[i]), bases on U/F[i]
				param4FRel.setInfoEntropyOfEquClassesOfAllInstances(infoEntropyOfEquClassesOfAllU);
				// -----------------------------------------------------------------------------------
			}else {
				// -----------------------------------------------------------------------------------
//				log.info("β == 0");
				// -----------------------------------------------------------------------------------
				// Skip H(F[i]), bases on U/F[i]
//				log.info("	"+"Skip H(F[i]), bases on U/F[i].");
				// -----------------------------------------------------------------------------------				
			}
			// β = [0, 1). Semi-supervised, supervised.
			if (Double.compare(1, tradeOff)!=0) {
				// -----------------------------------------------------------------------------------
//				log.info("β = [0, 1)");
				// -----------------------------------------------------------------------------------
				// (labeled U)/F[i]
				Collection<Collection<UniverseInstance>> equClassesOfLabeledU = 
					SemisupervisedRepresentativeAlgorithm
						.Basic
						.equivalentClass(labeledUniverses, new IntegerArrayIterator(attribute))
						.values();
				// -----------------------------------------------------------------------------------
				// Set (labeled U)/F[i]
				param4FRel.setEquClassesOfLabeledInstances(equClassesOfLabeledU);
				// Set C
				param4FRel.setCondAttributes(new IntegerArrayIterator(0));
				// Set H(C), bases on labeled U
				param4FRel.setInfoEntropyOfCondEquClassesOfLabeledInstances(decisionInfoEntropy);
				// -----------------------------------------------------------------------------------
			}else {
				// -----------------------------------------------------------------------------------
//				log.info("β == 1");
				// -----------------------------------------------------------------------------------
				// Skip (labeled U)/F[i]
				// Skip C
				// Skip H(C), bases on labeled U
//				log.info("	"+"Skip (labeled U)/F[i].");
//				log.info("	"+"Skip C.");
//				log.info("	"+"Skip H(C).");
				// -----------------------------------------------------------------------------------
			}
			// Set β
			param4FRel.setTradeOff(tradeOff);
			param4FRel.setCondEntropyCalculation(calculation.getCondEntropyCalculation());
			param4FRel.setMutualInfoEntropyCalculation(calculation.getMutualInfoEntropyCalculation());
			param4FRel.setArgs(new Object[] {	labeledUniverses.size()	});
			
			return param4FRel;
		}

		/**
		 * Construct and load parameters for Feature 1 Relevance Calculation based on the given trade-off
		 * value(i.e. &beta;).
		 * <p>
		 * <i>U</i>, <i>Attributes of U</i>, <i>U/F[i]</i>, <i>H(U/F<sub>i</sub>)</i>, are loaded if 
		 * 		<strong>&beta; != 0</strong> for calculations of <i>&beta; * UI(F[i])/H(F[i])</i>.
		 * <p>
		 * <i>(labeled U) / F<sub>i</sub></i>, <i>C</i>, <i>labeled U</i>, <i>H(C)</i>, <i>H(F<sub>i
		 * 		</sub>) bases on (labeled U)/F<sub>i</sub></i> are loaded if <strong>&beta; != 1
		 * 		</strong> for calculations of <i>(1-&beta;) * SU(F<sub>i</sub>, C)</i>.
		 * 
		 * @see {@link FeatureRelevance1Params4SemisupervisedRepresentative}
		 * @see {@link FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased#calculateF1Rel(
		 * 				FeatureRelevance1Params4SemisupervisedRepresentative)}
		 * 
		 * @param tradeOff
		 * 		Trade-off value: &beta;, ranging from 0 to 1. If &beta; = 0, supervise only case; If &beta; 
		 * 		= 1, un-supervise only case.
		 * @param labeledUniverses
		 * 		A {@link Collection} of labeled {@link UniverseInstance}.
		 * @param allUniverses
		 * 		A {@link Collection} of labeled and unlabeled {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @param attributeIndex
		 * 		The index of the attribute to calculate F1-Rel.
		 * @param decisionInfoEntropy
		 * 		Info. entropy value calculate by decision attribute: H(D) where D is the decision attribute.
		 * @param calculation
		 * 		{@link SemisupervisedRepresentativeCalculations4EntropyBased} instance.
		 * @param infoEntropyCacheOfAllUniverse
		 * 		Info. entropy value of each attribute of all universes in a {@link Double[]} as cache for
		 * 		calculation re-use acceleration.
		 * @return loaded {@link FeatureRelevance1Params4SemisupervisedRepresentative} instance.
		 */
		public static FeatureRelevance1Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
						ConditionalEntropyCalculation4SemisupervisedRepresentative, 
						MutualInformationEntropyCalculation4SemisupervisedRepresentative>
		loadFeature1RelevanceCalculationParamters(
				double tradeOff,  
				Collection<UniverseInstance> labeledUniverses, Collection<UniverseInstance> allUniverses,
				int[] attributes, int attributeIndex, double decisionInfoEntropy,
				SemisupervisedRepresentativeCalculations4EntropyBased calculation,
				InformationEntropyCache infoEntropyCache
		) {
			FeatureRelevance1Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
					ConditionalEntropyCalculation4SemisupervisedRepresentative, 
					MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
				param4F1Rel = new FeatureRelevance1Params4SemisupervisedRepresentative<>();
			// β = (0, 1]. Semi-supervised, un-supervised.
			if (Double.compare(0, tradeOff)!=0) {
				// -----------------------------------------------------------------------------------
//				log.info("β = (0, 1]");
				// -----------------------------------------------------------------------------------
				// H(U/F<sub>i</sub>), bases on U
				double infoEntropyOfEquClassesOfAllU = 
						infoEntropyCache.calculateByAllUniverses(
							attributes[attributeIndex], 
							calculation.getInfoEntropyCalculation(), 
							allUniverses
						).getEntropyValue();
				// -----------------------------------------------------------------------------------
				// Set U
				param4F1Rel.setAllInstances(allUniverses);
				// Set Attributes of U
				param4F1Rel.setAttributes(attributes);
				// Set H(U/F<sub>i</sub>)
				param4F1Rel.setInfoEntropyOfEquClassesOfAllUniverses(infoEntropyOfEquClassesOfAllU);
				// -----------------------------------------------------------------------------------
			}else {
				// -----------------------------------------------------------------------------------
//				log.info("β == 0");
				// -----------------------------------------------------------------------------------
				// Skip U
				// Skip Attributes of U
				// Skip H(U/F<sub>i</sub>)
//				log.info("	"+"Skip U.");
//				log.info("	"+"Skip Attributes of U.");
//				log.info("	"+"Skip H(U/F[i]).");
				// -----------------------------------------------------------------------------------
			}
			// β = [0, 1). Semi-supervised, supervised.
			if (Double.compare(1, tradeOff)!=0) {
				// -----------------------------------------------------------------------------------
//				log.info("β = [0, 1)");
				// -----------------------------------------------------------------------------------
				// H((labeled U)/F<sub>i</sub>), bases on labeled U
				InformationEntropyCacheResult infoEntropyCacheResult = 
						infoEntropyCache.calculateByLabeledUniverses(
							attributes[attributeIndex], 
							calculation.getInfoEntropyCalculation(), 
							labeledUniverses
						);
				double infoEntropyOfEquClassesOfLabeledU = infoEntropyCacheResult.getEntropyValue();
				// -----------------------------------------------------------------------------------
				// Set (labeled U) / F<sub>i</sub>
				param4F1Rel.setEquClassesOfLabeledUniverses(infoEntropyCacheResult.getEquivalentClass());
				// Set C
				param4F1Rel.setCondAttributes(new IntegerArrayIterator(0));
				// Set labeled U
				param4F1Rel.setLabeledInstances(labeledUniverses);
				// Set H(C)
				param4F1Rel.setInfoEntropyOfCondEquClassesOfLabeledUniverses(decisionInfoEntropy);
				// Set H(F<sub>i</sub>) bases on (labeled U)/F<sub>i</sub>
				param4F1Rel.setInfoEntropyOfEquClassesOfLabeledUniverses(infoEntropyOfEquClassesOfLabeledU);
				// -----------------------------------------------------------------------------------
			}else {
				// -----------------------------------------------------------------------------------
//				log.info("β == 1");
				// -----------------------------------------------------------------------------------
				// Skip (labeled U) / F<sub>i</sub>
				// Skip C
				// Skip labeled U
				// Skip H(C)
				// Skip H(F<sub>i</sub>) bases on (labeled U)/F<sub>i</sub>
//				log.info("	"+"Skip (labeled U) / F[i].");
//				log.info("	"+"Skip C.");
//				log.info("	"+"Skip labeled U.");
//				log.info("	"+"Skip H(C).");
//				log.info("	"+"Skip H(F[i]) bases on (labeled U)/F[i].");
				// -----------------------------------------------------------------------------------				
			}
			// Set attributes of U.
			param4F1Rel.setAttributes(attributes);
			// Set β
			param4F1Rel.setTradeOff(tradeOff);
			param4F1Rel.setCondEntropyCalculation(calculation.getCondEntropyCalculation());
			param4F1Rel.setMutualInfoEntropyCalculation(calculation.getMutualInfoEntropyCalculation());
			param4F1Rel.setSymmetricalUncertaintyCalculation(calculation.getSymmetricalUncertaintyCalculation());
			
			return param4F1Rel;
		}
	
		/**
		 * Construct and load parameters for Feature 2 Relevance Calculation based on the given trade-off
		 * value(i.e. &beta;).
		 * <p>
		 * <i>U</i>, <i>Attributes of U</i>, <i>F<sub>i</sub></i>, <i>F<sub>j</sub></i>, <i>H(F<sub>i</sub>), 
		 * 		bases on U</i>, <i>H(F<sub>j</sub>), bases on U</i> are loaded if <strong>&beta; != 0
		 * 		</strong> for calculations of <i>&beta; * USU(F[i], F[j])</i>.
		 * <p>
		 * <i>(labeled U) / F<sub>i</sub></i>, F<sub>j</sub>, <i>labeled U</i>, <i>H(F<sub>i</sub>), bases on 
		 * 		labeled U</sub>)</i>, <i>H(F<sub>j</sub>), bases on labeled U</i> are loaded if <strong>
		 * 		&beta; != 1 </strong> for calculations of <i>(1-&beta;) * SU(F[i], F[j])</i>.
		 * 
		 * @see {@link FeatureRelevance2Params4SemisupervisedRepresentative}
		 * @see {@link FeatureRelevance4SemisupervisedRepresentative4MutualInfoEntropyBased#calculateF2Rel(
		 * 				FeatureRelevance2Params4SemisupervisedRepresentative)}
		 * 
		 * @param tradeOff
		 * 		Trade-off value: &beta;, ranging from 0 to 1. If &beta; = 0, supervise only case; If &beta; 
		 * 		= 1, un-supervise only case.
		 * @param labeledUniverses
		 * 		A {@link Collection} of labeled {@link UniverseInstance}.
		 * @param allUniverses
		 * 		A {@link Collection} of labeled and unlabeled {@link UniverseInstance}.
		 * @param attributes
		 * 		Attributes of {@link UniverseInstance}.
		 * @param gainAttribute
		 * 		The gain attribute to calculate F2-Rel.
		 * @param condAttributes
		 * 		The conditional attributes to calculate F2-Rel.
		 * @param calculation
		 * 		{@link SemisupervisedRepresentativeCalculations4EntropyBased} instance.
		 * @return loaded {@link FeatureRelevance1Params4SemisupervisedRepresentative} instance.
		 */
		public static FeatureRelevance2Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
						ConditionalEntropyCalculation4SemisupervisedRepresentative, 
						MutualInformationEntropyCalculation4SemisupervisedRepresentative>
		loadFeature2RelevanceCalculationParamters(
				double tradeOff, 
				Collection<UniverseInstance> labeledUniverses, Collection<UniverseInstance> allUniverses,
				int[] attributes, int gainAttribute, IntegerIterator condAttributes, 
				SemisupervisedRepresentativeCalculations4EntropyBased calculation,
				InformationEntropyCache infoEntropyCache
		) {
			FeatureRelevance2Params4SemisupervisedRepresentative<Collection<Collection<UniverseInstance>>, 
					ConditionalEntropyCalculation4SemisupervisedRepresentative, 
					MutualInformationEntropyCalculation4SemisupervisedRepresentative> 
				param4F2Rel = new FeatureRelevance2Params4SemisupervisedRepresentative<>();
			
			// β = (0, 1]. Semi-supervised, un-supervised.
			if (Double.compare(0, tradeOff)!=0) {
				// -----------------------------------------------------------------------------------
//				log.info("β = (0, 1]");
				// -----------------------------------------------------------------------------------
				// H(U/F<sub>i</sub>), bases on U
				double infoEntropyOfEquClassesOfAllU = 
						infoEntropyCache.calculateByAllUniverses(
							gainAttribute,
							calculation.getInfoEntropyCalculation(), 
							allUniverses
						).getEntropyValue();
				// H(U/F<sub>j</sub>), bases on U
				double infoEntropyOfCondEquClassesOfAllU;
				if (condAttributes.size()==1) {
					infoEntropyOfCondEquClassesOfAllU = 
						infoEntropyCache.calculateByAllUniverses(
							condAttributes.reset().next(), 
							calculation.getInfoEntropyCalculation(), 
							labeledUniverses
						).getEntropyValue();
				}else {
					// U/F[j]
					Collection<Collection<UniverseInstance>> equClassesOfAllU = 
						SemisupervisedRepresentativeAlgorithm
							.Basic
							.equivalentClass(allUniverses, condAttributes)
							.values();
					// calculate H(U/F<sub>j</sub>)
					infoEntropyOfCondEquClassesOfAllU = 
						calculation.getInfoEntropyCalculation()
							.calculate(equClassesOfAllU, allUniverses.size())
							.getResult().doubleValue();
				}
				// -----------------------------------------------------------------------------------
				// Set U
				param4F2Rel.setAllInstances(allUniverses);
				// Set Attributes of U: F
				param4F2Rel.setAttributes(attributes);
				// Set F<sub>i</sub>
				param4F2Rel.setEquClassesAttribute(gainAttribute);
				// Set F<sub>j</sub>
				param4F2Rel.setCondAttributes(condAttributes);
				// Set H(F<sub>i</sub>), bases on U
				param4F2Rel.setInfoEntropyOfEquClassesOfAllInstances(infoEntropyOfEquClassesOfAllU);
				// Set H(F<sub>j</sub>), bases on U
				param4F2Rel.setInfoEntropyOfConEquClassesOfAllInstances(infoEntropyOfCondEquClassesOfAllU);
				// -----------------------------------------------------------------------------------
			}else {
				// -----------------------------------------------------------------------------------
//				log.info("β == 0");
				// -----------------------------------------------------------------------------------
				// Skip U
				// Skip Attributes of U: F
				// Skip F<sub>i</sub>
				// Skip F<sub>j</sub>
				// Skip H(F<sub>i</sub>), bases on U
				// Skip H(F<sub>j</sub>), bases on U
//				log.info("	"+"Skip U.");
//				log.info("	"+"Skip Attributes of U: F.");
//				log.info("	"+"Skip F[i].");
//				log.info("	"+"Skip F[j].");
//				log.info("	"+"Skip H(F<sub>i</sub>), bases on U.");
//				log.info("	"+"Skip H(F<sub>j</sub>), bases on U.");
				// -----------------------------------------------------------------------------------
			}
			
			// β = [0, 1). Semi-supervised, supervised.
			if (Double.compare(1, tradeOff)!=0) {
				// -----------------------------------------------------------------------------------
//				log.info("β = (0, 1]");
				// -----------------------------------------------------------------------------------
				// (labeled U)/F[i]
				Collection<Collection<UniverseInstance>> equClassesOfLabeledU = 
					SemisupervisedRepresentativeAlgorithm
						.Basic
						.equivalentClass(labeledUniverses, new IntegerArrayIterator(gainAttribute))
						.values();
				// H((labeled U)/F<sub>i</sub>), bases on labeled U
				double infoEntropyOfEquClassesOfLabeledU = 
						infoEntropyCache.calculateByLabeledUniverses(
							gainAttribute, 
							calculation.getInfoEntropyCalculation(), 
							labeledUniverses
						).getEntropyValue();
				// H(F[j]), bases on labeled U.
				double infoEntropyOfCondEquClassesOfLabeledU;
				if (condAttributes.size()==1) {
					infoEntropyOfCondEquClassesOfLabeledU = 
						infoEntropyCache.calculateByLabeledUniverses(
							condAttributes.reset().next(), 
							calculation.getInfoEntropyCalculation(),
							labeledUniverses
						).getEntropyValue();
				}else {
					// (labeled U)/F[j]
					Collection<Collection<UniverseInstance>> condEquClassesOfLabeledU = 
							SemisupervisedRepresentativeAlgorithm
								.Basic
								.equivalentClass(labeledUniverses, condAttributes)
								.values();
					// H(F[j]), bases on labeled U.
					infoEntropyOfCondEquClassesOfLabeledU = 
							calculation.getInfoEntropyCalculation()
										.calculate(condEquClassesOfLabeledU, labeledUniverses.size())
										.getResult().doubleValue();
				}
				// -----------------------------------------------------------------------------------
				// Set (labeled U) / F<sub>i</sub>
				param4F2Rel.setEquClassesOfLabeledInstances(equClassesOfLabeledU);
				// Set F<sub>j</sub>
				param4F2Rel.setCondAttributes(condAttributes);
				// Set labeled U
				param4F2Rel.setLabeledInstances(labeledUniverses);
				// Set H(F<sub>i</sub>), bases on labeled U
				param4F2Rel.setInfoEntropyOfEquClassesOfLabeledInstances(infoEntropyOfEquClassesOfLabeledU);
				// Set H(F<sub>j</sub>), bases on labeled U
				param4F2Rel.setInfoEntropyOfConEquClassesOfLabeledInstances(infoEntropyOfCondEquClassesOfLabeledU);
				// -----------------------------------------------------------------------------------
			}else {
				// -----------------------------------------------------------------------------------
//				log.info("β == 1");
				// -----------------------------------------------------------------------------------
				// Skip (labeled U) / F<sub>i</sub>
				// Skip F<sub>j</sub>
				// Skip labeled U
				// Skip H(F<sub>j</sub>), bases on labeled U
				// Skip H(F<sub>i</sub>), bases on labeled U
//				log.info("	"+"Skip (labeled U) / F[i].");
//				log.info("	"+"Skip F[j].");
//				log.info("	"+"Skip labeled U.");
//				log.info("	"+"Skip H(F<sub>j</sub>), bases on labeled U.");
//				log.info("	"+"Skip H(F<sub>i</sub>), bases on labeled U.");
				// -----------------------------------------------------------------------------------				
			}
			// Set β
			param4F2Rel.setTradeOff(tradeOff);
			param4F2Rel.setCondEntropyCalculation(calculation.getCondEntropyCalculation());
			param4F2Rel.setMutualInfoEntropyCalculation(calculation.getMutualInfoEntropyCalculation());
			param4F2Rel.setSymmetricalUncertaintyCalculation(calculation.getSymmetricalUncertaintyCalculation());
			
			return param4F2Rel;
		}
	}
}