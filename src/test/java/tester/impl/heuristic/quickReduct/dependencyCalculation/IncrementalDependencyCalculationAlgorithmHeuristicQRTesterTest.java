package tester.impl.heuristic.quickReduct.dependencyCalculation;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.incrementalDependencyCalculation.DependencyCalculation4IDC;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.incrementalDependencyCalculation.PositiveRegion4IDC;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;

class IncrementalDependencyCalculationAlgorithmHeuristicQRTesterTest extends BasicTester {
	private boolean byCore = true;
	private boolean logOn = false;

	@Test
	void testExecDep() throws Exception {
		// Set significance deviation.
		double sigDeviation = DEP_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		IncrementalDependencyCalculationAlgorithmHeuristicQRTester<Integer> tester;
		
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set if execute core
				.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
				// Set using IDC.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, DependencyCalculation4IDC.class)
				// Set deviation.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new IncrementalDependencyCalculationAlgorithmHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
	
	@Test
	void testExecPos() throws Exception {
		// Set significance deviation.
		int sigDeviation = POS_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		IncrementalDependencyCalculationAlgorithmHeuristicQRTester<Integer> tester;
		
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set if execute core
				.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
				// Set using IDC.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, PositiveRegion4IDC.class)
				// Set deviation.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new IncrementalDependencyCalculationAlgorithmHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
}