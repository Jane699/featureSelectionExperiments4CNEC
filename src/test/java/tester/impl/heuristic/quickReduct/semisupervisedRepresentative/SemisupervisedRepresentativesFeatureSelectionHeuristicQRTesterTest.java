package tester.impl.heuristic.quickReduct.semisupervisedRepresentative;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.model.algStrategyRepository.semisupervisedRepresentative.calculationPack.SemisupervisedRepresentativeCalculations4EntropyBased;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;

class SemisupervisedRepresentativesFeatureSelectionHeuristicQRTesterTest extends BasicTester {
	private boolean logOn = false;
	
	@Test
	void testExec() throws Exception {
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		SemisupervisedRepresentativesFeatureSelectionHeuristicQRTester tester;
		
		parameters = new ProcedureParameters()
						// Set labeled universe: labeled U
						.setNonRootParameter(ParameterConstants.PARAMETER_LABELED_UNIVERSE, universe)
						// Set unlabeled universe: unlabeled U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNLABELED_UNIVERSE, new ArrayList<>(0))
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set &alpha;
						//.setNonRootParameter("featureRelevantThreshold", 0.0)	// no setting: auto
						// Set &beta;
						//.setNonRootParameter("tradeOff", 0.0)					// no setting: auto
						// Set entropy based SRFS calculation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, SemisupervisedRepresentativeCalculations4EntropyBased.class);
		// Create a SRFS tester.
		tester = new SemisupervisedRepresentativesFeatureSelectionHeuristicQRTester(parameters, logOn);
		
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}

}
