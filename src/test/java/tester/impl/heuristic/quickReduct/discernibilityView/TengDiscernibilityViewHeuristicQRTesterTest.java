package tester.impl.heuristic.quickReduct.discernibilityView;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import basic.procedure.parameter.ProcedureParameters;
import basic.utils.ArrayUtils;
import featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.discernibilityView.Streamline4TengDiscernibilityView;
import featureSelection.repository.implement.support.calculation.discernibility.tengDiscernibilityView.DiscernibilityCalculation4TengDiscernibilityView;
import tester.BasicTester;
import tester.frame.procedure.parameter.ParameterConstants;

class TengDiscernibilityViewHeuristicQRTesterTest extends BasicTester {
	private boolean logOn = false;

	@Test
	void testExec4IntegerValue() throws Exception {
		// Initiate attributes: 1~C
		int attrLength = universe.get(0).getAttributeValues().length-1;
		int[] attributes = ArrayUtils.initIncrementalValueIntArray(attrLength, 1, 1);
		
		ProcedureParameters parameters;
		TengDiscernibilityViewHeuristicQRTester<Integer> tester;
		
		parameters = new ProcedureParameters()
				// Set universe: U
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE, universe)
				// Set attributes of universe: C
				.setNonRootParameter(ParameterConstants.PARAMETER_ATTRIBUTES, attributes)
				// Set using FAR-DV.
				.setNonRootParameter(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS, DiscernibilityCalculation4TengDiscernibilityView.class)
				// Set universe streamline
				.setNonRootParameter(ParameterConstants.PARAMETER_UNIVERSE_STREAMLINE_CLASS, Streamline4TengDiscernibilityView.class);
		// Create an FAR-DV tester.
		tester = new TengDiscernibilityViewHeuristicQRTester<>(parameters, logOn);
		// Execute and get the reduct.
		Collection<Integer> red = tester.exec();
		// Print info.
		System.out.println("result : "+red);
		System.out.println("total time : "+tester.getTime());
		System.out.println("tag time : "+tester.getTimeDetailByTags());
	}
}