package tester.impl.heuristic.quickReduct.dependencyCalculation;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.directDependencyCalculation.DependencyCalculation4DDCHash;
import featureSelection.repository.implement.support.calculation.dependency.dependencyCalculation.directDependencyCalculation.DependencyCalculation4DDCSequential;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.directDependencyCalculation.PositiveRegion4DDCHash;
import featureSelection.repository.implement.support.calculation.positiveRegion.dependencyCalculation.directDependencyCalculation.PositiveRegion4DDCSequential;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;

class DirectDependencyCalculationAlgorithmHeuristicQRTesterTest extends BasicTester {
	private boolean byCore = true;
	private boolean logOn = false;

	@Test
	void testExecDepHash() throws Exception {
		// Set significance deviation.
		double sigDeviation = DEP_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer> tester;
		
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set if execute core
				.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
				// Set using DDC-Hash.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, DependencyCalculation4DDCHash.class)
				// Set deviation.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}

	@Test
	void testExecPosHash() throws Exception {
		// Set significance deviation.
		int sigDeviation = POS_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer> tester;
		
		// Set parameters
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set if execute core
				.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
				// Set using DDC-Hash.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, PositiveRegion4DDCHash.class)
				// Set deviation.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
	
	@Test
	void testExecDepSequential() throws Exception {
		// Set significance deviation.
		double sigDeviation = DEP_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer> tester;
		
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set if execute core
				.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
				// Set using DDC-Hash.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, DependencyCalculation4DDCSequential.class)
				// Set deviation.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
	
	@Test
	void testExecPosSequential() throws Exception {
		// Set significance deviation.
		int sigDeviation = POS_SIG_DEVIATION;
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer> tester;
		
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set if execute core
				.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
				// Set using DDC-Hash.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, PositiveRegion4DDCSequential.class)
				// Set deviation.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create an ACC tester.
		tester = new DirectDependencyCalculationAlgorithmHeuristicQRTester<Integer>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
}
