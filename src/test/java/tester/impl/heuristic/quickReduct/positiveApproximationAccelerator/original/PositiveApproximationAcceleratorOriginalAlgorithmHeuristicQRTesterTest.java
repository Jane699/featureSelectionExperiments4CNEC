package tester.impl.heuristic.quickReduct.positiveApproximationAccelerator.original;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.positiveApproximationAccelerator.CCECalculation4ACCOriginal;
import featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.positiveApproximationAccelerator.LCECalculation4ACCOriginal;
import featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.positiveApproximationAccelerator.SCECalculation4ACCOriginal;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;

class PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTesterTest extends BasicTester {
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
		PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTester<Integer> tester;

		// Set parameters.
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using SCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, SCECalculation4ACCOriginal.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTester<>(parameters, logOn);
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
		PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTester<Integer> tester;

		// Set parameters.
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using LCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, LCECalculation4ACCOriginal.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTester<>(parameters, logOn);
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
		PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTester<Integer> tester;

		// Set parameters.
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using CCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, CCECalculation4ACCOriginal.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}

}