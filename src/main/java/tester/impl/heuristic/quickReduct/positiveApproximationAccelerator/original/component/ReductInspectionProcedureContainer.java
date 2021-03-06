package tester.impl.heuristic.quickReduct.positiveApproximationAccelerator.original.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import basic.model.imple.integerIterator.IntegerCollectionIterator;
import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.algroithm.algStrategyRepository.positiveApproximationAccelerator.PositiveApproximationAcceleratorOriginalAlgorithm.Basic;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;
import featureSelection.repository.implement.support.calculation.algStrategyCalculation.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorCalculation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.container.DefaultProcedureContainer;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.frame.statistics.heuristic.Constants;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

/**
 * Reduct inspection for <strong>Positive Approximation Accelerator (ACC)</strong> Feature Selection.
 * This procedure contains 2 ProcedureComponents: 
 * <li>
 * 	<strong>Inspection procedure controller</strong>
 * 	<p>Control to loop over attributes in reduct to inspect redundant.
 * </li>
 * <li>
 * 	<strong>Inspect reduct redundancy</strong>
 * 	<p>Check the redundancy of an attribute by calculating its inner significance. Considered redundant if 
 * 		the inner significance is 0(i.e. sig(red)=sig(red-{a})).
 * </li>
 * 
 * @author Benjamin_L
 */
@Slf4j
public class ReductInspectionProcedureContainer<Sig extends Number> 
	extends DefaultProcedureContainer<Collection<Integer>>
	implements StatisticsCalculated,
				ReportMapGenerated<String, Map<String, Object>>
{
	private boolean logOn;
	
	@Getter private Map<String, Object> statistics;
	@Getter private Map<String, Map<String, Object>> report;
	private Collection<String> reportKeys;
	private int loopCount;
	
	private Map<String, Object> localParameters;
	
	public ReductInspectionProcedureContainer(ProcedureParameters parameters, boolean logOn) {
		super(parameters);
		this.logOn = logOn;
		statistics = new HashMap<>();
		report = new HashMap<>();
		reportKeys = new LinkedList<>();
		localParameters = new HashMap<>();
	}

	@Override
	public String shortName() {
		return "Inspect reduct";
	}

	@Override
	public String staticsName() {
		return shortName();
	}
	
	@Override
	public String reportName() {
		return shortName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ProcedureComponent<?>[] initComponents() {
		return new ProcedureComponent<?>[] {
			// 1. Inspection procedure controller
			new TimeCountedProcedureComponent<Collection<Integer>>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component)->{
						if (logOn)	log.info(LoggerUtil.spaceFormat(1, "1/{}. {}"), getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								new HashSet<>(getParameters().get(ParameterConstants.PARAMETER_REDUCT_LIST)),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						Collection<Integer> red = (Collection<Integer>) parameters[0];
						/* ------------------------------------------------------------------------------ */
						ProcedureComponent<Object[]> comp1 = (ProcedureComponent<Object[]>) getComponents().get(1);
						localParameters.put("red", red);
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 Go through a in R
						Integer[] redCopy = red.toArray(new Integer[red.size()]);
						for (int attr: redCopy) {
							TimerUtils.timePause((TimeCounted) component);
							loopCount++;
							localParameters.put("attr", attr);
							comp1.exec();
							TimerUtils.timeContinue((TimeCounted) component);
						}
						return red;
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						this.getParameters().setNonRootParameter(ParameterConstants.PARAMETER_REDUCT_LIST_AFTER_INSPECTATION, result);
						/* ------------------------------------------------------------------------------ */
						// Statistics
						/* ------------------------------------------------------------------------------ */
						// Report
						reportKeys.add(component.getDescription());
						//	[DatasetRealTimeInfo]
						ProcedureUtils.Report.DatasetRealTimeInfo.save(
								report, component.getDescription(), 
								((Collection<?>)getParameters().get(ParameterConstants.PARAMETER_UNIVERSE)).size(), 
								0, 
								result.size()
						);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report.ExecutionTime.save(report, (TimeCountedProcedureComponent<?>) component);
						/* ------------------------------------------------------------------------------ */
					}
				){
					@Override public void init() {}
				
					@Override public String staticsName() {
						return shortName()+" | 1. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspection procedure controller"),
			// 2. Inspect reduct redundancy
			new TimeCountedProcedureComponent<Object[]>(
					ComponentTags.TAG_CHECK,
					this.getParameters(), 
					(component)->{
						//if (logOn)	log.info(LoggerUtil.spaceFormat(1, "2/{}. {}"), getComponents().size(), component.getDescription());
						component.setLocalParameters(new Object[] {
								getParameters().get("globalSig"),
								getParameters().get(ParameterConstants.PARAMETER_UNIVERSE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE),
								getParameters().get(ParameterConstants.PARAMETER_SIG_DEVIATION),
								localParameters.get("attr"),
								localParameters.get("red"),
						});
					}, 
					false, (component, parameters) -> {
						/* ------------------------------------------------------------------------------ */
						boolean attributeIsRedundant = true;
						int p=0;
						Sig globalSig = (Sig) parameters[p++];
						Collection<UniverseInstance> universes = (Collection<UniverseInstance>) parameters[p++];
						PositiveApproximationAcceleratorCalculation<Sig> calculation = 
								(PositiveApproximationAcceleratorCalculation<Sig>) parameters[p++];
						Sig sigDeviation = (Sig) parameters[p++];
						int attr = (int) parameters[p++];
						Collection<Integer> red = (Collection<Integer>) parameters[p++];
						/* ------------------------------------------------------------------------------ */
						TimerUtils.timeStart((TimeCounted) component);
						/* ------------------------------------------------------------------------------ */
						// 1 Go through a in R
						Sig examSig;
						Collection<EquivalentClass> equClasses;
						// 2 calculate Sig(R-{a}).
						red.remove(attr);
						equClasses = Basic.equivalentClass(universes, new IntegerCollectionIterator(red));
						calculation.calculate(equClasses, red.size(), universes.size());
						examSig = calculation.getResult();
						// 3 if (R-{a}.sig==C.sig)
						if (calculation.value1IsBetter(globalSig, examSig, sigDeviation)) {
							// 4 R = R-{a}
							red.add(attr);
							attributeIsRedundant = false;
						}
						return new Object[] {
							attr,
							attributeIsRedundant,
						};
					}, 
					(component, result) -> {
						/* ------------------------------------------------------------------------------ */
						int r=0;
						int attr = (int) result[r++];
						boolean attributeIsRedundant = (boolean) result[r++];
						/* ------------------------------------------------------------------------------ */
						// Report
						String reportMark = "Loop["+loopCount+"] Attr["+result[1]+"]";
						reportKeys.add(reportMark);
						//	[REPORT_EXECUTION_TIME]
						ProcedureUtils.Report
									.ExecutionTime
									.save(localParameters, 
											report, 
											reportMark, 
											(TimeCountedProcedureComponent<?>) component
						);
						//	[REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_INSPECT_CURRENT_ATTRIBUTE_VALUE, 
								attr
						);
						//	[REPORT_INSPECT_ATTRIBUTE_REDUNDANT]
						ProcedureUtils.Report.saveItem(
								report, reportMark, 
								Constants.REPORT_INSPECT_ATTRIBUTE_REDUNDANT, 
								attributeIsRedundant
						);
					}
				){
					@Override public void init() {}
							
					@Override public String staticsName() {
						return shortName()+" | 2. of "+getComponents().size()+"."+" "+getDescription();
					}
				}.setDescription("Inspect reduct redundancy"),
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Integer> exec() throws Exception {
		ProcedureComponent<?>[] comps = initComponents();
		for (ProcedureComponent<?> each : comps)	this.getComponents().add(each);
		return (Collection<Integer>) comps[0].exec();
	}

	
	@Override
	public String[] getReportMapKeyOrder() {
		return reportKeys.toArray(new String[reportKeys.size()]);
	}
}