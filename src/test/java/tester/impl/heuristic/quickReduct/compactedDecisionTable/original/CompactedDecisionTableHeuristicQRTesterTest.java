package tester.impl.heuristic.quickReduct.compactedDecisionTable.original;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.compactedDecisionTableAlgorithm.CCECalculation4CTOriginalHash;
import featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.compactedDecisionTableAlgorithm.LCECalculation4CTOriginalHash;
import featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.compactedDecisionTableAlgorithm.SCECalculation4CTOriginalHash;
import featureSelection.repository.implement.model.algStrategyRepository.compactedDecisionTable.impl.original.decisionNumber.HashMapDecisionNumber;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;

class CompactedDecisionTableHeuristicQRTesterTest extends BasicTester {
	private boolean byCore = true;
	private boolean logOn = false;

	@Test
	void testExecSCE() throws Exception {
		// Set significance deviation.
		double sigDeviation = SCE_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		CompactedDecisionTableHeuristicQRTester<Double, HashMapDecisionNumber> tester;

		// Set parameters.
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using SCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, SCECalculation4CTOriginalHash.class)
						// Set using HashMapDecisionNumber to store info. in compacted decision table.
						.setNonRootParameter(ParameterConstants.PARAMETER_DECISION_NUMBER_CLASS, HashMapDecisionNumber.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create a CT tester.
		tester = new CompactedDecisionTableHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}

	@Test
	void testExecLCE() throws Exception {
		// Set significance deviation.
		double sigDeviation = LCE_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);

		ProcedureParameters parameters;
		CompactedDecisionTableHeuristicQRTester<Double, HashMapDecisionNumber> tester;
		
		// Set parameters.
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using LCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, LCECalculation4CTOriginalHash.class)
						// Set using HashMapDecisionNumber to store info. in compacted decision table.
						.setNonRootParameter(ParameterConstants.PARAMETER_DECISION_NUMBER_CLASS, HashMapDecisionNumber.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create a CT tester.
		tester = new CompactedDecisionTableHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}

	@Test
	void testExecCCE() throws Exception {
		// Set significance deviation.
		double sigDeviation = CCE_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);

		ProcedureParameters parameters;
		CompactedDecisionTableHeuristicQRTester<Double, HashMapDecisionNumber> tester;
		
		// Set parameters.
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using CCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, CCECalculation4CTOriginalHash.class)
						// Set using HashMapDecisionNumber to store info. in compacted decision table.
						.setNonRootParameter(ParameterConstants.PARAMETER_DECISION_NUMBER_CLASS, HashMapDecisionNumber.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create a CT tester.
		tester = new CompactedDecisionTableHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
}