package tester.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;

import basic.model.interf.Calculation;
import basic.procedure.Procedure;
import basic.procedure.ProcedureComponent;
import basic.procedure.ProcedureContainer;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import basic.procedure.statistics.report.ReportGenerated;
import basic.procedure.statistics.report.ReportMapGenerated;
import basic.procedure.timer.TimeCounted;
import basic.utils.LoggerUtil;
import featureSelection.repository.frame.support.calculation.FeatureImportance;
import featureSelection.repository.frame.support.calculation.featureImportance.DependencyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.InConsistencyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.PositiveRegionCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.CombinationConditionEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.LiangConditionEntropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.ShannonConditionEnpropyCalculation;
import featureSelection.repository.frame.support.calculation.featureImportance.entropy.mutualInformation.MutualInformationEntropyCalculation;
import tester.frame.procedure.component.TimeCountedProcedureComponent;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.Constants;

/**
 * Utilities for {@link ProcedureContainer} and {@link ProcedureComponent}.
 * 
 * @author Benjamin_L
 */
public class ProcedureUtils {
	
	/**
	 * Check if "exit" mark in the given {@link ProcedureParameters}.
	 * 
	 * @see {@link ParameterConstants#PARAMETER_PROCEDURE_EXIT_MARK}
	 * 
	 * @param procedureParameters
	 * 		{@link ProcedureParameters} instance that contains Procedure parameters.
	 * @return true if the "exit" mark is in the {@link ProcedureParameters} and marked as true.
	 */
	public static boolean procedureExitMark(ProcedureParameters procedureParameters) {
		Boolean exitMark = procedureParameters.get(ParameterConstants.PARAMETER_PROCEDURE_EXIT_MARK);
		return exitMark!=null && exitMark;
	}
	
	public static class ShortName {
		/**
		 * Extract {@link FeatureImportance} info.(class/instance) from {@link ProcedureParameters} who 
		 * marks class as {@link ParameterConstants#PARAMETER_SIG_CALCULATION_CLASS} and marks instance
		 * as {@link ParameterConstants#PARAMETER_SIG_CALCULATION_INSTANCE}.
		 * 
		 * @see {@link ParameterConstants#PARAMETER_SIG_CALCULATION_CLASS}
		 * @see {@link ParameterConstants#PARAMETER_SIG_CALCULATION_INSTANCE}
		 * 
		 * @param parameters
		 * 		{@link ProcedureParameters} instance that contains Procedure parameters.
		 * @return "UNKNOWN" if failed to extract info./the name of implemented {@link FeatureImportance}.
		 */
		public static String calculation(ProcedureParameters parameters) {
			Class<? extends Calculation<?>> calculationClass = parameters.get(ParameterConstants.PARAMETER_SIG_CALCULATION_CLASS);
			Calculation<?> calculationInstance = parameters.get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE);
			if (calculationClass!=null) {
				try {
					return calculationClass.getField("CALCULATION_NAME".intern())
											.get(calculationClass)
											.toString();
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e
				) {
					return calculation(calculationClass);
				}
			}else if (calculationInstance!=null) {
				try {
					return calculationInstance.getClass()
											.getField("CALCULATION_NAME".intern())
											.get(calculationClass)
											.toString();
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e
				) {
					return calculation(calculationClass);
				}
			}else {
				return "UNKNOWN".intern();
			}
		}
		
		/**
		 * Define the type of given {@link FeatureImportance} instance: (check in order)
		 * <li>{@link InConsistencyCalculation}</li>
		 * <li>{@link ShannonConditionEnpropyCalculation}</li>
		 * <li>{@link LiangConditionEntropyCalculation}</li>
		 * <li>{@link CombinationConditionEntropyCalculation}</li>
		 * <li>{@link PositiveRegionCalculation}</li>
		 * <li>{@link DependencyCalculation}</li>
		 * 
		 * @see {@link InConsistencyCalculation#CALCULATION_NAME}
		 * @see {@link ShannonConditionEnpropyCalculation#CALCULATION_NAME}
		 * @see {@link LiangConditionEntropyCalculation#CALCULATION_NAME}
		 * @see {@link CombinationConditionEntropyCalculation#CALCULATION_NAME}
		 * @see {@link PositiveRegionCalculation#CALCULATION_NAME}
		 * @see {@link DependencyCalculation#CALCULATION_NAME}
		 * 
		 * @param calculationClass
		 * 		Implemented {@link FeatureImportance} class.
		 * @return <code>CALCULATION_NAME</code> of the {@link FeatureImportance} interface.
		 */
		public static String calculation(Class<? extends Calculation<?>> calculationClass) {
			if (InConsistencyCalculation.class.isAssignableFrom(calculationClass)) {
				return InConsistencyCalculation.CALCULATION_NAME;
			}else if (ShannonConditionEnpropyCalculation.class.isAssignableFrom(calculationClass)) {
				return ShannonConditionEnpropyCalculation.CALCULATION_NAME;
			}else if (LiangConditionEntropyCalculation.class.isAssignableFrom(calculationClass)) {
				return LiangConditionEntropyCalculation.CALCULATION_NAME;
			}else if (CombinationConditionEntropyCalculation.class.isAssignableFrom(calculationClass)) {
				return CombinationConditionEntropyCalculation.CALCULATION_NAME;
			}else if (MutualInformationEntropyCalculation.class.isAssignableFrom(calculationClass)) {
				return MutualInformationEntropyCalculation.CALCULATION_NAME;
			}else if (PositiveRegionCalculation.class.isAssignableFrom(calculationClass)) {
				return PositiveRegionCalculation.CALCULATION_NAME;
			}else if (DependencyCalculation.class.isAssignableFrom(calculationClass)) {
				return DependencyCalculation.CALCULATION_NAME;
			}else {
				return "UNKNOWN".intern();
			}
		}
		
		public static String byCore(ProcedureParameters parameters) {
			Boolean byCore = parameters.get(ParameterConstants.PARAMETER_QR_BY_CORE);
			if (byCore!=null) {
				return byCore? "Core".intern(): "NoCore".intern();
			}
			return "NoCore".intern();
		}
	}
	
	public static class Time {
		public static final String SUM_TIME = "ProcedureUtils.SUM";
		
		/**
		 * Sum the total time of the given {@link ProcedureComponent} and all its sub-containers(i.e. 
		 * {@link ProcedureComponent#getSubProcedureContainers()})'s components too.
		 * 
		 * @see {@link TimeCounted}
		 * 
		 * @param component
		 * 		A {@link ProcedureComponent} with at least one component implemented {@link TimeCounted}.
		 * @return Sum time of {@link ProcedureComponent}s.
		 */
		public static long sumProcedureComponentTimes(ProcedureComponent<?> component) {
			long time = 0;
			if (component instanceof TimeCounted)	time += ((TimeCounted) component).getTime();
			if (component.getSubProcedureContainers()!=null && !component.getSubProcedureContainers().isEmpty()) {
				for (ProcedureContainer<?> container: component.getSubProcedureContainers().values()) {
					for (ProcedureComponent<?> com: container.getComponents()) {
						time += sumProcedureComponentTimes(com);
					}
				}
			}
			return time;
		}
		
		/**
		 * Sum the total time of the given {@link ProcedureComponent} and all its sub-containers(i.e. 
		 * {@link ProcedureComponent#getSubProcedureContainers()})'s components too with the same 
		 * <strong>Tag</strong>.
		 * 
		 * @see {@link #sumProcedureComponentsTimesByTags(ProcedureContainer, Map)}
		 * 
		 * @param container
		 * 		A {@link ProcedureContainer} with tagged {@link ProcedureComponent}s.
		 * @return A sum time {@link Map} whose keys are tags and values are the corresponding sum time.
		 */
		public static Map<String, Long> sumProcedureComponentsTimesByTags(ProcedureContainer<?> container) {
			Map<String, Long> timeMap = sumProcedureComponentsTimesByTags(container, new HashMap<>());
			long sum = timeMap.values().stream().reduce(Long::sum).orElse(0L);
			timeMap.put(SUM_TIME, sum);
			return timeMap;
		}
		
		/**
		 * Sum the total time of the given {@link ProcedureComponent} and all its sub-containers(i.e. 
		 * {@link ProcedureComponent#getSubProcedureContainers()})'s components too with the same 
		 * <strong>Tag</strong> and accumulating into the given {@link Map} <code>timeMap</code>.
		 * 
		 * @param container
		 * 		A {@link ProcedureContainer} with tagged {@link ProcedureComponent}s.
		 * @param timeMap
		 * 		A {@link Map} stores accumulated time with tags as keys.
		 * @return A sum time {@link Map} whose keys are tags and values are the corresponding sum time.
		 */
		private static Map<String, Long> sumProcedureComponentsTimesByTags(ProcedureContainer<?> container, Map<String, Long> timeMap) {
			Long time;
			for (ProcedureComponent<?> component : container.getComponents()) {
				if (component instanceof TimeCounted) {
					time = timeMap.get(component.getTag());	if (time==null)	time = (long) 0;
					time += ((TimeCounted) component).getTime();
					timeMap.put(component.getTag(), time);
				}
				
				if (component.getSubProcedureContainers()!=null && !component.getSubProcedureContainers().isEmpty()) {
					for (ProcedureContainer<?> subContainer: component.getSubProcedureContainers().values()) {
						sumProcedureComponentsTimesByTags(subContainer, timeMap);
					}
				}
			}
			return timeMap;
		}
	}

	public static class Statistics{
		
		public static Map<String, Object> countInt(Map<String, Object> statistics, String key, int increment){
			Integer count = (Integer) statistics.get(key);
			if (count==null)	count = 0;
			statistics.put(key, count+increment);
			return statistics;
		}
		
		public static Map<String, Object> combineProcedureStatics(Procedure procedure){
			return combineProcedureStatics(procedure, new HashMap<>());
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static Map<String, Object> combineProcedureStatics(Procedure procedure, Map<String, Object> map){
			if (procedure instanceof StatisticsCalculated) {
				Object value;
				Map<String, Object> currentStatistics = ((StatisticsCalculated) procedure).getStatistics();
				for (Map.Entry<String, Object> entry: currentStatistics.entrySet()) {
					value = map.get(entry.getKey());
					if (value!=null) {
						if (value instanceof Number) {
							if (value instanceof Integer)		value = ((Integer)value) + ((Integer) entry.getValue());
							else if (value instanceof Long)		value = ((Long)value) + ((Long) entry.getValue());
							else if (value instanceof Double)	value = ((Double)value) + ((Double) entry.getValue());
							else								throw new RuntimeException("Unimplemented type : "+value.getClass().getName());
						}else if (value instanceof Collection) {
							try {
								Collection collection = (Collection) value.getClass().newInstance();
								for (Object obj : (Collection) value)				collection.add(obj);
								for (Object obj : (Collection) entry.getValue())	collection.add(obj);
								value = collection;
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}else if (value instanceof Map) {
							((Map) value).putAll((Map) entry.getValue());
						}else if (value instanceof int[]) {
							int[] array = Arrays.copyOf((int[])value, ((int[]) value).length+(((int[])entry.getValue()).length));
							for (int i=((int[]) value).length; i<array.length; i++)
								array[i] = ((int[]) entry.getValue())[i-((int[]) value).length];
							value = array;
						}else if (value instanceof Integer[]) {
							Integer[] array = Arrays.copyOf((Integer[])value, ((Integer[]) value).length+(((Integer[])entry.getValue()).length));
							for (int i=((Integer[]) value).length; i<array.length; i++)
								array[i] = ((Integer[]) entry.getValue())[i-((Integer[]) value).length];
							value = array;
						}else {
							throw new RuntimeException("Unimplemented type : "+value.getClass().getName());
						}
						map.put(entry.getKey(), value);
					}else {
						map.put(entry.getKey(), entry.getValue());
					}
				}
			}
			if (procedure instanceof ProcedureContainer) {
				for (ProcedureComponent<?> component: ((ProcedureContainer<?>) procedure).getComponents())
					combineProcedureStatics(component, map);
			}else {
				if (((ProcedureComponent<?>) procedure).getSubProcedureContainers()!=null && 
					!((ProcedureComponent<?>) procedure).getSubProcedureContainers().isEmpty()
				) {
					for (ProcedureContainer<?> subContainer: ((ProcedureComponent<?>) procedure).getSubProcedureContainers().values())
						combineProcedureStatics(subContainer , map);
				}
			}
			return map;
		}
	
		public static void displayOne(Logger log, StatisticsCalculated statistic) {
			if (statistic!=null && statistic.getStatistics()!=null &&
				!statistic.getStatistics().isEmpty()
			) {
				LoggerUtil.printLine(log, "=", 70);
				log.info("Statistics of 【{}】", statistic.staticsName());
				LoggerUtil.printLine(log, "-", 70);
				for (Map.Entry<String, Object> entry : statistic.getStatistics().entrySet()) {
					if (entry.getValue() instanceof Collection)
						log.info("	"+"{}({}) : {}", entry.getKey(), entry.getValue()==null?0: ((Collection<?>) entry.getValue()).size(), entry.getValue());
					else if (entry.getValue() instanceof Integer[])
						log.info("	"+"{}({}) : {}", entry.getKey(), entry.getValue()==null?0: ((Integer[]) entry.getValue()).length, entry.getValue());
					else
						log.info("	"+"{} : {}", entry.getKey(), entry.getValue());
				}
				LoggerUtil.printLine(log, "=", 70);
			}
		}
		
		public static void displayAll(Logger log, Procedure procedure) {
			if (procedure instanceof StatisticsCalculated)	displayOne(log, (StatisticsCalculated) procedure);
			if (procedure instanceof ProcedureContainer) {
				if (((ProcedureContainer<?>) procedure).getComponents()!=null &&
					!((ProcedureContainer<?>) procedure).getComponents().isEmpty()
				) {
					for (ProcedureComponent<?> comp : ((ProcedureContainer<?>) procedure).getComponents()) {
						displayAll(log, comp);
					}
				}
			}else {
				if (((ProcedureComponent<?>) procedure).getSubProcedureContainers()!=null &&
					!((ProcedureComponent<?>) procedure).getSubProcedureContainers().isEmpty()
				) {
					for (ProcedureContainer<?> con : ((ProcedureComponent<?>) procedure).getSubProcedureContainers().values())
						displayAll(log, con);
				}
			}
		}
	
		public static Collection<Class<?>> classesOfProcedureContainer(ProcedureContainer<?> container){
			Collection<Class<?>> classes = new HashSet<>();
			classes.add(container.getClass());
			if (container.getComponents()!=null)
				for (ProcedureComponent<?> component: container.getComponents())
					classes.addAll(classesOfProcedureComponent(component));
			return classes;
		}
		
		public static Collection<Class<?>> classesOfProcedureComponent(ProcedureComponent<?> component){
			Collection<Class<?>> classes = new HashSet<>();
			classes.add(component.getClass());
			if (component.getSubProcedureContainers()!=null) {
				for (ProcedureContainer<?> container: component.getSubProcedureContainers().values())
					classes.addAll(classesOfProcedureContainer(container));
			}
			return classes;
		}
		
		public static String toJSONInfo(ProcedureContainer<?> container){
			return JSONObject.toJSONString(procedureContainerJSONInfo(container));
		}
		
		public static Map<String, Object> procedureContainerJSONInfo(ProcedureContainer<?> container){
			Map<String, Object> map = new HashMap<>();
			map.put("name", container.shortName());
			map.put("decription", container.description());
			map.put("class", container.getClass().getSimpleName());
			List<ProcedureComponent<?>> components = container.getComponents();
			if (components!=null) {
				Map<String, Map<String, Object>> innerComponents = new HashMap<>(components.size());
				for (ProcedureComponent<?> comp: components)
					innerComponents.put(comp.getDescription(), procedureComponentJSONInfo(comp));
				map.put("components", innerComponents);
			}
			return map;
		}
		
		public static Map<String, Object> procedureComponentJSONInfo(ProcedureComponent<?> component){
			Map<String, Object> map = new HashMap<>();
			
			map.put("tag", component.getTag());
			map.put("description", component.getDescription());
			map.put("class", component.getClass().getSimpleName());
			Map<String, Object> containerMap = new HashMap<>();
			for (Map.Entry<String, ProcedureContainer<?>> entry: component.getSubProcedureContainers().entrySet())
				containerMap.put(entry.getKey(), procedureContainerJSONInfo(entry.getValue()));
			map.put("subContainers", containerMap);
			return map;
		}
	}

	public static class Report {
		
		public static class ExecutionTime {
			@SuppressWarnings("unchecked")
			public static long sum(ReportGenerated<?> report) {
				long sum = 0L;
				if (report.getReport() instanceof Map) {
					sum += sum((Map<String, ?>) report.getReport());
				}
				return sum;
			}
			
			@SuppressWarnings("unchecked")
			private static long sum(Map<String, ?> map) {
				long sum = 0;
				for (Map.Entry<String, ?> entry: map.entrySet()) {
					if (Constants.REPORT_EXECUTION_TIME.equals(entry.getKey())) {
						if (entry.getValue() instanceof Long)
							sum += (long) entry.getValue();
						else if (entry.getValue() instanceof Collection)
							sum += sum((Collection<?>) entry.getValue());
					}else if (entry.getValue() instanceof Map) {
						sum += sum((Map<String, ?>) entry.getValue());
					}
				}
				return sum;
			}
			
			@SuppressWarnings("unchecked")
			private static long sum(Collection<?> collection) {
				long sum = 0;
				for (Object obj: collection) {
					if (obj instanceof Collection) {
						sum += sum((Collection<?>) obj);
					}else if (obj instanceof Map) {
						sum += sum((Map<String, ?>) obj);
					}else if (obj instanceof Long) {
						if (obj!=null)	sum += (Long) obj;
					}
				}
				return sum;
			}
			
			/**
			 * Save executed time of {@link Component} by remark for current round.
			 * 
			 * <p> period = <code>component.getTime()</code> - 
			 * 				<code>localParameters</code>[<code>"executedTime"</code>]
			 * <p> report: { 
			 * 			component.getDescription(): {
			 * 				{@link Constants#REPORT_EXECUTION_TIME}: [<code>component.getTime()</code>]
			 * 			}	
			 * 		}
			 * 
			 * @see {@link ProcedureUtils.Report.ExecutionTime#save(Map, String, long...)}.
			 * 
			 * @param localParameters
			 * 		A {@link Map} as Component local parameters/storage.
			 * @param report
			 * 		{@link Map} with String as key and Map as value.
			 * @param reportMark
			 * 		Mark for current round.
			 * @param component
			 * 		{@link TimeCountedProcedureComponent} to be saved.
			 */
			public static void save(Map<String, Object> localParameters, 
									Map<String, Map<String, Object>> report, String reportMark, 
									TimeCountedProcedureComponent<?> component
			) {
				@SuppressWarnings("unchecked")
				Map<String, Long> executedTime = (Map<String, Long>) localParameters.get("executedTime");
				if (executedTime==null)	localParameters.put("executedTime", executedTime = new HashMap<>());
				Long  historyTime = executedTime.get(component.getDescription());
				if (historyTime==null)	historyTime = 0L;
				if (component.getTime() - historyTime>=0) {
					ProcedureUtils.Report.ExecutionTime.save(report, reportMark, component.getTime() - historyTime);
				}else {
					throw new RuntimeException("Component time is less than history time: "+
							component.getTime()+" < "+historyTime
					);
				}
				executedTime.put(component.getDescription(), component.getTime());
			}
			
			/**
			 * Save the time of {@link TimeCountedProcedureComponent} by {@link Constants#REPORT_EXECUTION_TIME},
			 * named by <code>component.getDescription()</code>.
			 * 
			 * <p> report: { 
			 * 			component.getDescription(): {
			 * 				{@link Constants#REPORT_EXECUTION_TIME}: [<code>component.getTime()</code>]
			 * 			}	
			 * 		}
			 * 
			 * @param report
			 * 		{@link Map} with String as key and Map as value.
			 * @param component
			 * 		A {@link TimeCountedProcedureComponent} instance.
			 */
			public static void save(Map<String, Map<String, Object>> report, 
									TimeCountedProcedureComponent<?> component
			) {
				if (component instanceof TimeCountedProcedureComponent) {
					Map<String, Object> reportContent = report.get(component.getDescription());
					if (reportContent==null)	report.put(component.getDescription(), reportContent = new HashMap<>());
					@SuppressWarnings("unchecked")
					List<Long> executionTimes = (List<Long>) reportContent.get(Constants.REPORT_EXECUTION_TIME);
					if (executionTimes==null)	reportContent.put(Constants.REPORT_EXECUTION_TIME, executionTimes = new LinkedList<>());
					executionTimes.add(((TimeCountedProcedureComponent<?>) component).getTime());
				}
			}
			
			/**
			 * Save the time of {@link TimeCountedProcedureComponent} by {@link Constants#REPORT_EXECUTION_TIME},
			 * named by <code>componentDesc</code>.
			 * 
			 * <p> report: { 
			 * 			componentDesc: {
			 * 				{@link Constants#REPORT_EXECUTION_TIME}: [<code>time[0]</code>, ...]
			 * 			}	
			 * 		}
			 * 
			 * @param report
			 * 		{@link Map} with String as key and Map as value.
			 * @param componentDesc
			 * 		{@link TimeCountedProcedureComponent}'s description.
			 * @param time
			 * 		Execution times to be saved in order.
			 */
			public static void save(Map<String, Map<String, Object>> report, String componentDesc,
									long...time
			) {
				Map<String, Object> reportContent = report.get(componentDesc);
				if (reportContent==null)	report.put(componentDesc, reportContent = new HashMap<>());
				@SuppressWarnings("unchecked")
				List<Long> executionTimes = (List<Long>) reportContent.get(Constants.REPORT_EXECUTION_TIME);
				if (executionTimes==null)	reportContent.put(Constants.REPORT_EXECUTION_TIME, executionTimes = new LinkedList<>());
				for (long t: time)	executionTimes.add(t);
			}
			
			/**
			 * Save execution times of current report record(current round).
			 * 
			 * <p> report: [ 
			 * 			0: {
			 * 				{@link Constants#REPORT_EXECUTION_TIME}: [<code>time[0]</code>, ...]
			 * 			},
			 * 			1: {
			 * 				...
			 * 			}
			 * 		]
			 * 
			 * @param report
			 * 		{@link List} with {@link Map} as report item for the current round.
			 * @param time
			 * 		Execution times to be saved.
			 */
			public static void save(List<Map<String, Object>> report, long...time) {
				List<Long> executionTimes;
				Map<String, Object> reportItem = new HashMap<>();
				reportItem.put(Constants.REPORT_EXECUTION_TIME, executionTimes = new LinkedList<>());
				for (long t: time)	executionTimes.add(t);
			}
		}
	
		public static class DatasetRealTimeInfo {
			/**
			 * Save dataset real-time info.
			 * 
			 * @param report
			 * 		A {@link Map} for report.
			 * @param componentDesc
			 * 		Unique description of {@link ProcedureComponent} for identification.
			 * @param universeSize
			 * 		Current total Universe instance number.
			 * @param compactedUniveseSize
			 * 		The size of compacted universes/structures.
			 * @param reductSize
			 * 		Current reduct size. (One or more reduct)
			 */
			public static void save(Map<String, Map<String, Object>> report, String componentDesc,
									int universeSize, int compactedUniveseSize, int...reductSize
			) {
				// REPORT_CURRENT_UNIVERSE_SIZE
				saveItem(report, componentDesc, Constants.REPORT_CURRENT_UNIVERSE_SIZE, universeSize);
				// REPORT_CURRENT_COMPACTED_UNIVERSE_SIZE
				saveItem(report, componentDesc, Constants.REPORT_CURRENT_COMPACTED_UNIVERSE_SIZE, compactedUniveseSize);
				// REPORT_CURRENT_REDUCT_SIZE
				saveItem(report, componentDesc, Constants.REPORT_CURRENT_REDUCT_SIZE, reductSize);
			}
		}
		
		public static void countInt(
				Map<String, Map<String, Object>> report, String componentDesc,
				String key, int increment
		){
			Map<String, Object> data = report.get(componentDesc);
			if (data==null)	report.put(componentDesc, data=new HashMap<>());
			
			Integer count = (Integer) data.get(key);
			if (count==null)	count = 0;
			data.put(key, count+increment);
		}
		
		public static <V> void saveItem(Map<String, Map<String, Object>> report, String componentDesc,
										String key, V value
		) {
			Map<String, Object> reportContent = report.get(componentDesc);
			if (reportContent==null)	report.put(componentDesc, reportContent = new HashMap<>());
			reportContent.put(key, value);
		}
	
		public static void displayOne(Logger log, ReportGenerated<?> report) {
			if (report!=null && report.getReport()!=null) {
				LoggerUtil.printLine(log, "=", 70);
				log.info("Report of 【{}】", report.reportName());
				LoggerUtil.printLine(log, "-", 70);
				if (report.getReport() instanceof Map) {
					Map<?, ?> map = (Map<?, ?>) report.getReport();
					for (String key : ((ReportMapGenerated<?, ?>) report).getReportMapKeyOrder()) {
						Object value = map.get(key);
						if (value instanceof Collection)
							log.info("	"+"{}({}) : {}", key, value==null?0: ((Collection<?>) value).size(), value);
						else if (value instanceof Integer[])
							log.info("	"+"{}({}) : {}", key, value==null?0: ((Integer[]) value).length, value);
						else
							log.info("	"+"{} : {}", key, value);
					}
					LoggerUtil.printLine(log, "-", 70);
				}else if (report.getReport() instanceof Collection) {
					Collection<?> collection = (Collection<?>) report.getReport();
					int index = 0;
					for (Object value: collection) {
						if (value instanceof Collection)
							log.info("	"+"{}({}) : {}", index, value==null?0: ((Collection<?>) value).size(), value);
						else if (value instanceof Integer[])
							log.info("	"+"{}({}) : {}", index, value==null?0: ((Integer[]) value).length, value);
						else
							log.info("	"+"{} : {}", index, value);
					}
					LoggerUtil.printLine(log, "-", 70);
				}
				log.info("	"+"sum({}) : {}", Constants.REPORT_EXECUTION_TIME, ExecutionTime.sum(report));
				LoggerUtil.printLine(log, "=", 70);
			}
		}
		
		public static void displayAll(Logger log, Procedure procedure) {
			if (procedure instanceof ReportGenerated)	displayOne(log, (ReportGenerated<?>) procedure);
			if (procedure instanceof ProcedureContainer) {
				if (((ProcedureContainer<?>) procedure).getComponents()!=null &&
					!((ProcedureContainer<?>) procedure).getComponents().isEmpty()
				) {
					for (ProcedureComponent<?> comp : ((ProcedureContainer<?>) procedure).getComponents()) {
						displayAll(log, comp);
					}
				}
			}else {
				if (((ProcedureComponent<?>) procedure).getSubProcedureContainers()!=null &&
					!((ProcedureComponent<?>) procedure).getSubProcedureContainers().isEmpty()
				) {
					for (ProcedureContainer<?> con : ((ProcedureComponent<?>) procedure).getSubProcedureContainers().values())
						displayAll(log, con);
				}
			}
		}
	}
}