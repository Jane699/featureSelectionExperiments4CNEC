package tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.support.calculation.entropy.combinationConditionEntropy.roughEquivalentClassBased.extension.IncrementalDecision.CCECalculation4IDREC;
import featureSelection.repository.implement.support.calculation.entropy.liangConditionEntropy.roughEquivalentClassBased.extension.IncrementalDecision.LCECalculation4IDREC;
import featureSelection.repository.implement.support.calculation.entropy.shannonConditionEnpropy.roughEquivalentClassBased.extension.IncrementalDecision.SCECalculation4IDREC;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.core.ClassicImprovedCoreProcedureContainer;
import tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.component.inspect.ClassicImprovedReductInspectionProcedureContainer;

class RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTesterTest extends BasicTester {
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
		RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<Double> tester;
		
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using SCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, SCECalculation4IDREC.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create a CNEC tester.
		tester = new RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<>(parameters, logOn);
		// Set "Get Core" sub procedure container: ClassicImprovedCoreProcedureContainer
		tester.getComponent("Get Core")
			.setSubProcedureContainer(
				"CoreProcedureContainer",
				new ClassicImprovedCoreProcedureContainer<Double>(tester.getParameters(), logOn)
			);
		// Set "Reduct Inspection" sub procedure container: ClassicImprovedReductInspectionProcedureContainer
		tester.getComponent("Reduct Inspection")
			.setSubProcedureContainer(
				"ReductInspectionProcedureContainer", 
				new ClassicImprovedReductInspectionProcedureContainer<Double>(tester.getParameters(), logOn)
			);
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
		RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<Double> tester;
		
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using LCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, LCECalculation4IDREC.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create a CNEC tester.
		tester = new RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<>(parameters, logOn);
		// Set "Get Core" sub procedure container: ClassicImprovedCoreProcedureContainer
		tester.getComponent("Get Core")
			.setSubProcedureContainer(
				"CoreProcedureContainer",
				new ClassicImprovedCoreProcedureContainer<Double>(tester.getParameters(), logOn)
			);
		// Set "Reduct Inspection" sub procedure container: ClassicImprovedReductInspectionProcedureContainer
		tester.getComponent("Reduct Inspection")
			.setSubProcedureContainer(
				"ReductInspectionProcedureContainer", 
				new ClassicImprovedReductInspectionProcedureContainer<Double>(tester.getParameters(), logOn)
			);
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
		RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<Double> tester;
		
		parameters = new ProcedureParameters()
						// Set universe: U
						.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
						// Set attributes of universe: C
						.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
						// Set if execute core
						.setNonRootParameter(ParameterConstants.PARAMETER_QR_BY_CORE, byCore)
						// Set using CCE.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, CCECalculation4IDREC.class)
						// Set deviation.
						.setNonRootParameter(ParameterConstants.PARAMETER_SIG_DEVIATION, sigDeviation);
		// Create a CNEC tester.
		tester = new RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTester<>(parameters, logOn);
		// Set "Get Core" sub procedure container: ClassicImprovedCoreProcedureContainer
		tester.getComponent("Get Core")
			.setSubProcedureContainer(
				"CoreProcedureContainer",
				new ClassicImprovedCoreProcedureContainer<Double>(tester.getParameters(), logOn)
			);
		// Set "Reduct Inspection" sub procedure container: ClassicImprovedReductInspectionProcedureContainer
		tester.getComponent("Reduct Inspection")
			.setSubProcedureContainer(
				"ReductInspectionProcedureContainer", 
				new ClassicImprovedReductInspectionProcedureContainer<Double>(tester.getParameters(), logOn)
			);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
}