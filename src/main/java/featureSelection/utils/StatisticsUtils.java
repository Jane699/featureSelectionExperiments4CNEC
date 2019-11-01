package featureSelection.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import basic.model.interf.Calculation;
import basic.procedure.ProcedureContainer;
import basic.procedure.parameter.ProcedureParameters;
import basic.utils.DateTimeUtils;
import basic.utils.NumberUtils;
import basic.utils.StringUtils;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.statistics.Constants;
import featureSelection.statistics.info.DatasetInfo;
import featureSelection.statistics.record.PlainRecord;
import featureSelection.statistics.record.PlainRecordItem;
import featureSelection.statistics.record.RecordFieldInfo;
import lombok.experimental.UtilityClass;
import tester.frame.procedure.parameter.ParameterConstants;
import tester.frame.statistics.heuristic.ComponentTags;
import tester.utils.ProcedureUtils;
import tester.utils.TimerUtils;

@UtilityClass
public class StatisticsUtils {
	/**
	 * Utilities for titles.
	 * 
	 * @author Benjamin_L
	 */
	public static class Title {
		/**
		 * Add titles for basic info. of a record, including: 
		 * <li>DATETIME 
		 * 		<p> The date time info., usually is the date time of saving the statistics.
		 * 		<p>{@link Constants.PlainRecordInfo#DATETIME}
		 * </li>
		 * <li>CONTAINER_ID: 
		 * 		<p> The ID of the main execution {@link ProcedureContainer}.
		 * 		<p>{@link Constants.PlainRecordInfo#CONTAINER_ID}
		 * </li>
		 * <li>DATABASE_UNIQUE_ID: 
		 * 		<p> The ID of the executed dataset.
		 * 		<p>{@link Constants.PlainRecordInfo#DATABASE_UNIQUE_ID}
		 * </li>
		 * 
		 * @param titles
		 * 		A {@link List} of {@link RecordFieldInfo} to be loaded with titles.
		 */
		private static void addRecordBasicInfo(List<RecordFieldInfo> titles) {
			// DATETIME
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.DATETIME, 
										"日期时间", 
										null, null
									)
			);
			// CONTAINER_ID
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.CONTAINER_ID, 
										"记录ID", 
										null, null
									)
			);
			// DATABASE_UNIQUE_ID
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.DATABASE_UNIQUE_ID, 
										"数据库记录ID", 
										Constants.Database.TABLE_RUN_TIME_GENERAL, "UNIQUE_ID"
									)
			);
		}
		
		/**
		 * Add titles for dataset and algorithm basic info., including: 
		 * <li>DATASET_ID 
		 * 		<p> The ID of the dataset.
		 * 		<p>{@link Constants.PlainRecordInfo#DATASET_ID}
		 * </li>
		 * <li>DATASET 
		 * 		<p> The name of the dataset.
		 * 		<p>{@link Constants.PlainRecordInfo#DATASET}
		 * </li>
		 * <li>ORIGINAL_UNIVERSE_SIZE 
		 * 		<p> The number of {@link UniverseInstance}/instance executed.
		 * 		<p>{@link Constants.PlainRecordInfo#ORIGINAL_UNIVERSE_SIZE}
		 * </li>
		 * <li>PURE_UNIVERSE_SIZE 
		 * 		<p> The number of non-repeated {@link UniverseInstance}/instance executed: <strong>|U|<strong>
		 * 		<p>{@link Constants.PlainRecordInfo#PURE_UNIVERSE_SIZE}
		 * </li>
		 * <li>UNIVERSE_PURE_RATE 
		 * 		<p> The rate of non-repeated {@link UniverseInstance}/instance executed: 
		 * 			rate = |U/C| / |U|.
		 * 		<p>{@link Constants.PlainRecordInfo#UNIVERSE_PURE_RATE}
		 * </li>
		 * <li>ATTRIBUTE_SIZE 
		 * 		<p> The number of condition attributes a {@link UniverseInstance} contains: <strong>|C|<strong>
		 * 		<p>{@link Constants.PlainRecordInfo#ATTRIBUTE_SIZE}
		 * </li>
		 * <li>ATTRIBUTE_LIST 
		 * 		<p> The list of condition attributes a {@link UniverseInstance}: <strong>C<strong>
		 * 		<p>{@link Constants.PlainRecordInfo#ATTRIBUTE_LIST}
		 * </li>
		 * <li>DECISION_VALUE_NUMBER 
		 * 		<p> The number of non-repeated decision values of the executed {@link UniverseInstance}: 
		 * 			<strong>|D|<strong>
		 * 		<p>{@link Constants.PlainRecordInfo#DECISION_VALUE_NUMBER}
		 * </li>
		 * 
		 * @param titles
		 * 		A {@link List} of {@link RecordFieldInfo} to be loaded with titles.
		 */
		private static void addDatasetAlgorithmInfo(List<RecordFieldInfo> titles) {
			// DATASET_ID
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.DATASET_ID, 
										"数据集ID", 
										Constants.Database.TABLE_RUN_TIME_GENERAL, "DATASET_ID"
									)
			);
			// DATASET
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.DATASET_NAME, 
										"数据集名字", 
										Constants.Database.TABLE_DATASET, "NAME"
									)
			);
			/* ----------------------------------------------------------------------------------- */
			// ORIGINAL_UNIVERSE_SIZE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.ORIGINAL_UNIVERSE_SIZE, 
										"数据集原始记录个数", 
										Constants.Database.TABLE_DATASET, "INFO_UNIVERSE"
									)
			);
			// PURE_UNIVERSE_SIZE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.PURE_UNIVERSE_SIZE, 
										"数据集压缩后记录个数", 
										null, null
									)
			);
			// UNIVERSE_PURE_RATE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.UNIVERSE_PURE_RATE, 
										"数据集压缩后记录占比=数据集压缩后记录个数/数据集原始记录个数", 
										Constants.Database.TABLE_DATASET, "INFO_COMPRESS_RATE"
									)
			);
			// ATTRIBUTE_SIZE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.ATTRIBUTE_SIZE, 
										"数据集条件属性个数", 
										Constants.Database.TABLE_DATASET, "INFO_CONDITION_ATTRIBUTE"
									)
			);
			// ATTRIBUTE_LIST
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.ATTRIBUTE_LIST, 
										"数据集条件属性序列列表", 
										null, null
									)
			);
			// DECISION_VALUE_NUMBER
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.DECISION_VALUE_NUMBER, 
										"数据集决策属性个数", 
										Constants.Database.TABLE_DATASET, "INFO_DECISION_ATTRIBUTE_NUMBER"
									)
			);
		}

		private static void addCommonRuntimeInfo(List<RecordFieldInfo> titles) {
			// ALG_ID
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.ALG_ID, 
										"算法ID", 
										Constants.Database.TABLE_RUN_TIME_GENERAL, "ALGORITHM_ID"
									)
			);
			// ALG
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.ALG, 
										"算法简称", 
										Constants.Database.TABLE_ALGORITHM, "NAME"
									)
			);
			// PARAMETER_ID
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.PARAMETER_ID, 
										"算法参数ID", 
										Constants.Database.TABLE_RUN_TIME_GENERAL, "PARAMETER_ID"
									)
			);
			// TOTAL_TIME
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_TIME, 
										"记录所用时间(ms)", 
										Constants.Database.TABLE_RUN_TIME_GENERAL, "TIME_TOTAL"
									)
			);
			// PURE_TIME
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.PURE_TIME, 
										"除去检验时间的记录所用时间(ms)=记录所用时间-检验时间", 
										null, null
									)
			);
			// RUN_TIMES
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.RUN_TIMES, 
										"相同实验重复跑的次数", 
										Constants.Database.TABLE_RUN_TIME_GENERAL, "TIMES"
									)
			);
		}
		
		private static void addCalculationInfo(List<RecordFieldInfo> titles) {
			// CALCULATION_TIMES
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.CALCULATION_TIMES, 
										"sum(Cal.计算次数)", 
										null, null
									)
			);
			// CALCULATION_ATTR_LEN
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.CALCULATION_ATTR_LEN, 
										"sum(Cal.计算属性长度)", 
										null, null
									)
			);
		}
		
		private static void addPositiveRegionRemoving(List<RecordFieldInfo> titles) {
			// SIGNIFICANCE_HISTORY
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.SIGNIFICANCE_HISTORY, 
										"SIG记录", 
										null, null
									)
			);
			// SIGNIFICANCE_SUM
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.SIGNIFICANCE_SUM, 
										"sum(SIG记录)", 
										null, null
									)
			);
			// SIGNIFICANCE_AVERAGE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.SIGNIFICANCE_AVERAGE, 
										"SIG增加平均数 = ~记录 / ~次数", 
										null, null
									)
			);
			
			// remove POS(U)：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_HISTORY
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_HISTORY, 
										"删减POS(U)", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "POS_REMOVE_U_LIST"
									)
			);
			// Sum(remove POS(U))：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE, 
										"删减POS(U)总数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "POS_REMOVE_U_SIZE"
									)
			);
			// Eva(remove POS(U))：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION, 
										"删减POS(U)贡献评估=删减数/删减前剩余数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "POS_REMOVE_U_EVAL"
									)
			);
			
			// remove NEG(U)：TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY, 
										"删减NEG(U)", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "NEG_REMOVE_U_LIST"
									)
			);
			// Sum(remove NEG(U))：TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE, 
										"删减NEG(U)总数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "NEG_REMOVE_U_SIZE"
									)
			);
			// Eva(remove NEG(U))：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION, 
										"删减NEG(U)贡献评估=删减数/删减前剩余数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "NEG_REMOVE_U_EVAL"
									)
			);
			
			// remove U：TOTAL_UNIVERSE_REMOVE_NUMBER_HISTORY
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_NUMBER_HISTORY, 
										"删减U", 
										null, null
									)
			);
			// Sum(remove U)：TOTAL_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE, 
										"删减U总数", 
										null, null
									)
			);
			// Eva(remove U)：TOTAL_UNIVERSE_REMOVE_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION, 
										"删减贡献评估(U)=删减数/删减前剩余数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "EVAL_REMOVE_U_LIST"
									)
			);
			// Eva(Sum(remove U))：TOTAL_UNIVERSE_REMOVE_TOTAL_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_TOTAL_EVALUATION, 
										"删减贡献评估(U)=sum(删减数)/sum(删减前剩余数)", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "EVAL_REMOVE_U_AVERAGE"
									)
			);
			
			// remove POS(U/C)：COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_HISTORY
//			titles.add(
//					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_HISTORY, 
//										"删减POS(U/C)", 
//										null, null
//									)
//			);
			// Sum(remove POS(U/C))：COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
//			titles.add(
//					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE, 
//										"删减POS(U/C)总数", 
//										null, null
//									)
//			);
			// Eva(remove POS(U/C))：COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION, 
										"删减POS(U/C)贡献评估=删减数/删减前剩余数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "POS_REMOVE_UC_EVAL"
									)
			);
			
			// remove NEG(U/C)：COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY
//			titles.add(
//					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY, 
//										"删减NEG(U/C)", 
//										null, null
//									)
//			);
			// Sum(remove NEG(U/C))：COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
//			titles.add(
//					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE, 
//										"删减NEG(U/C)总数", 
//										null, null
//									)
//			);
			// Eva(remove NEG(U/C))：COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION, 
										"删减NEG(U/C)贡献评估=删减数/删减前剩余数", 
										Constants.Database.TABLE_RUN_TIME_EXT_QR, "NEG_REMOVE_UC_EVAL"
									)
			);
			
			// remove U/C：COMPACTED_UNIVERSE_REMOVE_NUMBER_HISTORY
//			titles.add(
//					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_NUMBER_HISTORY, 
//										"删减U/C", 
//										null, null
//									)
//			);
			// Sum(remove U/C)：COMPACTED_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
//			titles.add(
//					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE, 
//										"删减U/C总数", 
//										null, null
//									)
//			);
			// Eva(remove U/C)：COMPACTED_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION, 
										"删减贡献评估(U/C)=删减数/删减前剩余数", 
										null, null
									)
			);
			// Eva(Sum(remove U/C))：COMPACTED_UNIVERSE_REMOVE_TOTAL_EVALUATION
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_TOTAL_EVALUATION, 
										"删减贡献评估(U/C)=sum(删减数)/sum(删减前剩余数)", 
										null, null
									)
			);
		}

		private static void addProcedureInfo(List<RecordFieldInfo> titles) {
			// PROCEDURE_INFO
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.PROCEDURE_CLASSES_INFO, 
										"测试运行使用组件", 
										null, null
									)
						);
			// PROCEDURE_INFO
			titles.add(
					new RecordFieldInfo(Constants.PlainRecordInfo.PROCEDURE_INFO, 
										"测试运行组件信息(JSON)", 
										null, null
									)
						);
		}
	
		public static class QuickReduct {
			public static List<RecordFieldInfo> titles(){
				List<RecordFieldInfo> titles = new LinkedList<>();
				addRecordBasicInfo(titles);
				addDatasetAlgorithmInfo(titles);
				addCommonRuntimeInfo(titles);
				addCalculationInfo(titles);
				addPositiveRegionRemoving(titles);
				QuickReduct.addTime(titles);
				QuickReduct.addCore(titles);
				QuickReduct.addReductResult(titles);
				addProcedureInfo(titles);
				return titles;
			}
			
			private static void addTime(List<RecordFieldInfo> titles) {
				// TIME_INIT
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.TIME_INIT, 
											"初始化数据集用时(ms)", 
											Constants.Database.TABLE_RUN_TIME_GENERAL, "TIME_INIT"
										)
				);
				// TIME_COMPRESS
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.TIME_COMPRESS, 
											"压缩数据集记录用时(ms)", 
											Constants.Database.TABLE_RUN_TIME_GENERAL, "TIME_COMPRESS"
										)
				);
				// TIME_COMPRESS
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.TIME_CORE, 
											"求核用时(ms)", 
											Constants.Database.TABLE_RUN_TIME_GENERAL, "TIME_CORE"
										)
				);
				// TIME_RED
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.TIME_RED, 
											"约简流程主要步骤(初始化、检验、求核外)用时(ms)", 
											Constants.Database.TABLE_RUN_TIME_GENERAL, "TIME_REDUCTION"
										)
				);
				// TIME_INSPECT
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.TIME_INSPECT, 
											"检验用时(ms)", 
											Constants.Database.TABLE_RUN_TIME_GENERAL, "TIME_CHECK"
										)
				);
			}

			private static void addCore(List<RecordFieldInfo> titles) {
				// CORE_INCLUDE
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.CORE_INCLUDE, 
											"是否求核？", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "BY_CORE"
										)
				);
				// CORE_SIZE
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.CORE_SIZE, 
											"核结果个数", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "CORE_SIZE"
										)
				);
				// CORE_LIST
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.CORE_LIST, 
											"核结果列表", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "CORE_LIST"
										)
				);
				// CORE_ATTRIBUTE_EXAM_SIZE
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.CORE_ATTRIBUTE_EXAM_SIZE, 
											"求核过程中遍历属性的个数", 
											null, null
										)
				);
			}

			private static void addReductResult(List<RecordFieldInfo> titles) {
				// REDUCT_SIZE
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.REDUCT_SIZE, 
											"约简结果属性数", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "RED_SIZE"
										)
				);
				// REDUCT_LIST
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.REDUCT_LIST, 
											"约简结果列表", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "RED_LIST"
										)
				);
				// REDUNDANT_SIZE_BEFORE_INSPECT
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.REDUNDANT_SIZE_BEFORE_INSPECT, 
											"检验结果冗余属性个数", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "CHECK_REDUNDANCY_SIZE"
										)
				);
				// REDUNDANT_LIST_BEFORE_INSPECT
				titles.add(
						new RecordFieldInfo(Constants.PlainRecordInfo.REDUNDANT_LIST_BEFORE_INSPECT, 
											"检验结果冗余属性列表", 
											Constants.Database.TABLE_RUN_TIME_EXT_QR, "CHECK_REDUNDANCT_LIST"
										)
				);
			}
		}
	}

	/**
	 * Utilities for records correspondent to {@link Title}.
	 * 
	 * @author Benjamin_L
	 */
	public static class Record {
		private static void loadRecordBasicInfo(PlainRecord record, Map<String, RecordFieldInfo> titleMap, 
												Object containerID, String databasedUniqueID
		) {
			// DATETIME
			record.set(titleMap.get(Constants.PlainRecordInfo.DATETIME), 
						DateTimeUtils.currentDateTimeString("MM-dd HH:mm:ss"), 
						null
					);
			// CONTAINER_ID
			record.set(titleMap.get(Constants.PlainRecordInfo.CONTAINER_ID), 
						containerID, 
						null
					);
			// DATABASE_UNIQUE_ID
			record.set(titleMap.get(Constants.PlainRecordInfo.DATABASE_UNIQUE_ID), 
						databasedUniqueID,
						null
					);
		}
		
		private static void loadDatasetAlgorithmInfo(
				PlainRecord record, Map<String, RecordFieldInfo> titleMap, 
				DatasetInfo datasetInfo, int[] attributes, Map<String, Object> statistics
		) {
			/* ----------------------------------------------------------------------------------- */
			// DATASET_ID
			int datasetID = DBUtils.DatasetID.get(datasetInfo.getDatasetName());
			record.set(titleMap.get(Constants.PlainRecordInfo.DATASET_ID), 
						datasetID>=0? datasetID+"": "", 
						null
					);
			// DATASET
			record.set(titleMap.get(Constants.PlainRecordInfo.DATASET_NAME), 
						datasetInfo.getDatasetName(), 
						null
					);
			/* ----------------------------------------------------------------------------------- */
			// ORIGINAL_UNIVERSE_SIZE
			record.set(titleMap.get(Constants.PlainRecordInfo.ORIGINAL_UNIVERSE_SIZE), 
						datasetInfo.getUniverseSize(), 
						null
					);
			// PURE_UNIVERSE_SIZE
			Integer compressedSize = (Integer) statistics.get(tester.frame
																.statistics
																.heuristic
																.Constants
																.STATISTIC_COMPACTED_SIZE
														);
			record.set(titleMap.get(Constants.PlainRecordInfo.PURE_UNIVERSE_SIZE), 
						compressedSize==null? datasetInfo.getUniverseSize(): compressedSize, 
						null
					);
			// UNIVERSE_PURE_RATE
			double rate = compressedSize==null? 100: (((double) compressedSize)/datasetInfo.getUniverseSize()*100.0);
			record.set(titleMap.get(Constants.PlainRecordInfo.UNIVERSE_PURE_RATE), 
						NumberUtils.decimalLeftString(4, rate)+"%", 
						null
					);
			// ATTRIBUTE_SIZE
			record.set(titleMap.get(Constants.PlainRecordInfo.ATTRIBUTE_SIZE), 
						attributes.length, 
						null
					);
			// ATTRIBUTE_LIST
			record.set(titleMap.get(Constants.PlainRecordInfo.ATTRIBUTE_LIST), 
						StringUtils.intToString(attributes, 100), 
						null
					);
			// DECISION_VALUE_NUMBER
			record.set(titleMap.get(Constants.PlainRecordInfo.DECISION_VALUE_NUMBER), 
						datasetInfo.getDecisionAttributeSize(), 
						null
					);
		}
		
		private static void loadCommonRuntimeInfo(PlainRecord record, Map<String, RecordFieldInfo> titleMap, 
												int times, String testName, 
												String algorithmName, ProcedureParameters parameters,
												Map<String, Long> componentTagTimeMap
		) {
			// ALG_ID
			int algID = DBUtils.AlgorithmID.get(algorithmName);
			record.set(titleMap.get(Constants.PlainRecordInfo.ALG_ID), 
						algID>=0? algID+"": "", 
						null
					);
			// ALG
			record.set(titleMap.get(Constants.PlainRecordInfo.ALG), 
						testName, 
						null
					);
			// PARAMETER_ID
			int parameterID = DBUtils.ParameterID.get(parameters);
			record.set(titleMap.get(Constants.PlainRecordInfo.PARAMETER_ID), 
						parameterID>=0? parameterID+"": "", 
						null
					);
			// TOTAL_TIME
			Long total = componentTagTimeMap.get(ProcedureUtils.Time.SUM_TIME);
			record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_TIME), 
						NumberUtils.decimalLeftString(6, total==null?0: TimerUtils.nanoTimeToMillis(total)) , 
						null
					);
			// PURE_TIME
			Long inspectTime = componentTagTimeMap.get(ComponentTags.TAG_CHECK);
			if (inspectTime==null)	inspectTime = (long) 0;
			record.set(titleMap.get(Constants.PlainRecordInfo.PURE_TIME), 
						NumberUtils.decimalLeftString(6, total==null?0: TimerUtils.nanoTimeToMillis(total-inspectTime)), 
						null
					);
			// RUN_TIMES
			record.set(titleMap.get(Constants.PlainRecordInfo.RUN_TIMES), 
						times,
						null
					);
		}
		
		private static void loadCalculationInfo(PlainRecord record, Map<String, RecordFieldInfo> titleMap, 
												ProcedureParameters parameters
		) {
			Calculation<?> calculation = parameters.get(ParameterConstants.PARAMETER_SIG_CALCULATION_INSTANCE);
			// CALCULATION_TIMES
			record.set(titleMap.get(Constants.PlainRecordInfo.CALCULATION_TIMES), 
						calculation==null?0: calculation.getCalculationTimes(), 
						null
				);
			// CALCULATION_ATTR_LEN
			try {
				record.set(titleMap.get(Constants.PlainRecordInfo.CALCULATION_ATTR_LEN), 
							calculation==null?0: calculation.getClass().getMethod("getCalculationAttributeLength").invoke(calculation), 
							null
				);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e
			) {
				// No such method: getCalculationAttributeLength().
				record.set(titleMap.get(Constants.PlainRecordInfo.CALCULATION_ATTR_LEN), "?", null);
			}
		}
		
		private static void loadProcedureInfo(PlainRecord record, Map<String, RecordFieldInfo> titleMap,
											ProcedureContainer<?> container
		) {
			// PROCEDURE_CLASSES_INFO
			Collection<Class<?>> classCollection = ProcedureUtils
													.Statistics
													.classesOfProcedureContainer(container);
			Collection<String> classString = new HashSet<>();
			for (Class<?> clazz: classCollection)
				if (clazz.getSimpleName().length()>0)
					classString.add(clazz.getSimpleName());
			record.set(titleMap.get(Constants.PlainRecordInfo.PROCEDURE_CLASSES_INFO), 
						StringUtils.toString(classString, 50),
						null
			);
			// REDUCT_SIZE
			record.set(titleMap.get(Constants.PlainRecordInfo.PROCEDURE_INFO), 
						ProcedureUtils.Statistics.toJSONInfo(container), 
						null
			);
		}

		public static class Heuristic {
			/**
			 * Load heuristics Quick Reduct data record.
			 * 
			 * @param titles
			 * 		{@link RecordFieldInfo} {@link List} with titles.
			 * @param datasetInfo
			 * 		{@link DatasetInfo}.
			 * @param attributes
			 * 		Attributes of {@link UniverseInstance} in executed order.
			 * @param times
			 * 		Times of execution.
			 * @param parameters
			 * 		{@link ProcedureParameters}.
			 * @param testName
			 * 		Name of the test.
			 * @param statistics
			 * 		{@link Map} of the test as statistics, with {@link String} key and {@link Object} value.
			 * @param componentTagTimeMap
			 * 		{@link Map} of time sum by tag.
			 * @param container
			 * 		{@link ProcedureContainer} of the QR Tester.
			 */
			public static PlainRecord loadHeuristicsQuickReductRecord(
					List<RecordFieldInfo> titles, DatasetInfo datasetInfo, int[] attributes, 
					int times, ProcedureParameters parameters, String testName, Map<String, Object> statistics,
					Map<String, Long> componentTagTimeMap, ProcedureContainer<?> container
			) {
				Map<String, RecordFieldInfo> map = new HashMap<>();	
				for (RecordFieldInfo each : titles)		map.put(each.getField(), each);
				
				PlainRecord record = new PlainRecord(titles);
				
				String databaseUniqueID = DBUtils.generateUniqueID(datasetInfo.getDatasetName(), testName, parameters);
				
				loadRecordBasicInfo(record, map, container.id(), databaseUniqueID);
				loadDatasetAlgorithmInfo(record, map, datasetInfo, attributes, statistics);
				loadCommonRuntimeInfo(record, map, times, testName, container.shortName(), parameters, componentTagTimeMap);
				loadCalculationInfo(record, map, parameters);
				loadPositiveRegionRemoving(record, map, datasetInfo, statistics);
				loadQuickReductTime(record, map, datasetInfo, componentTagTimeMap);
				loadQuickReductCore(record, map, parameters, statistics);
				loadQuickReductReductResult(record, map, statistics);
				loadProcedureInfo(record, map, container);
				return record;
			}
			
			@SuppressWarnings("unchecked")
			private static void loadPositiveRegionRemoving(
					PlainRecord record, Map<String, RecordFieldInfo> titleMap, 
					DatasetInfo datasetInfo, Map<String, Object> statistics
			) {
				// SIGNIFICANCE_HISTORY
				Collection<Number> incrementSig = 
						(Collection<Number>) statistics.get(
								tester.frame
									.statistics
									.heuristic
									.Constants
									.STATISTIC_SIG_HISTORY
						);
				record.set(titleMap.get(Constants.PlainRecordInfo.SIGNIFICANCE_HISTORY), 
							incrementSig, 
							null
						);
				// SIGNIFICANCE_SUM
				record.set(titleMap.get(Constants.PlainRecordInfo.SIGNIFICANCE_SUM), 
							StatisticsUtils.numberSum(incrementSig), 
							null
						);
				// SIGNIFICANCE_AVERAGE
				record.set(titleMap.get(Constants.PlainRecordInfo.SIGNIFICANCE_AVERAGE), 
							NumberUtils.decimalLeftString(
								20, 
								incrementSig==null? 0: StatisticsUtils.numberSum(incrementSig) / incrementSig.size()
							),
							null
						);
				
				// remove POS(U)：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_HISTORY
				Collection<Integer> removePos = 
						(Collection<Integer>) statistics.get(
							tester.frame
									.statistics
									.heuristic
									.Constants
									.STATISTIC_POS_UNIEVRSE_REMOVED
						);
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_HISTORY), 
							StringUtils.toString(removePos, 100), 
							null
						);
				// Sum(remove POS(U))：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE), 
							intSum(removePos), 
							null
						);
				// Eva(remove POS(U))：TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION
				if (removePos!=null) {
					Collection<Double> evalEach = new ArrayList<>(removePos.size());
					int universeSize = datasetInfo.getUniverseSize(), evalDenominator = 0;
					for (int rm : removePos) {
						evalEach.add((rm+0.0) / universeSize);
						evalDenominator += universeSize;
						universeSize -= rm;
					}
					record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION), 
							evalDenominator==0? 0: intSum(removePos) / (evalDenominator+0.0), 
							null
						);
				}else {
					record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION), 
							0, null
						);
				}
				
				// remove NEG(U)：TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY
				Collection<Integer> removeNeg = 
						(Collection<Integer>) statistics.get(
							tester.frame
								.statistics
								.heuristic
								.Constants
								.STATISTIC_NEG_UNIEVRSE_REMOVED
						);
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY), 
							StringUtils.toString(removeNeg, 100), 
							null
						);
				// Sum(remove NEG(U))：TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE), 
							intSum(removeNeg), 
							null
						);
				// Eva(remove NEG(U))：TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION
				if (removeNeg!=null) {
					Collection<Double> evalEach = new ArrayList<>(removeNeg.size());
					int universeSize = datasetInfo.getUniverseSize(), evalDenominator = 0;
					for (int rm : removeNeg) {
						evalEach.add((rm+0.0) / universeSize);
						evalDenominator += universeSize;
						universeSize -= rm;
					}
					record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION), 
							evalDenominator==0? 0: intSum(removeNeg) / (evalDenominator+0.0), 
							null
						);
				}else {
					record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION), 
							0, null
						);
				}
				
				// remove U：TOTAL_UNIVERSE_REMOVE_NUMBER_HISTORY
				Collection<Integer> remove = new ArrayList<>(
												Math.max(removePos==null? 0: removePos.size(), 
															removeNeg==null? 0: removeNeg.size()
												)
											);
				if ((removePos!=null && removePos.size()>0) || 
					(removeNeg!=null && removeNeg.size()>0)
				) {
					Iterator<Integer> posIterator = null, negIterator = null;
					if (removePos!=null)	posIterator = removePos.iterator();
					if (removeNeg!=null)	negIterator = removeNeg.iterator();
					while ((posIterator!=null && posIterator.hasNext()) ||
							(negIterator!=null && negIterator.hasNext())
					) {
						int pos = posIterator==null? 0: posIterator.hasNext()? posIterator.next(): 0;
						int neg = negIterator==null? 0: negIterator.hasNext()? negIterator.next(): 0;
						remove.add(pos+neg);
					}
				}
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_NUMBER_HISTORY), 
							StringUtils.toString(remove, 100), 
							null
						);
				// Sum(remove NEG(U))：TOTAL_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE), 
							remove==null? null: intSum(remove), 
							null
						);
				
				// Eva(remove U)：TOTAL_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION
				Collection<Double> evalEach = new ArrayList<>(remove.size());
				int universeSize = datasetInfo.getUniverseSize(), evalDenominator = 0;
				for (int rm : remove) {
					evalEach.add((rm+0.0) / universeSize);
					evalDenominator += universeSize;
					universeSize -= rm;
				}
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION), 
							StringUtils.numberToString(evalEach, 100, 4), 
							null
						);
				// Eva(Sum(remove U))：TOTAL_UNIVERSE_REMOVE_TOTAL_EVALUATION
				record.set(titleMap.get(Constants.PlainRecordInfo.TOTAL_UNIVERSE_REMOVE_TOTAL_EVALUATION), 
							evalDenominator==0? 0: intSum(remove) / (evalDenominator+0.0), 
							null
						);
				// remove POS(U/C)：COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_HISTORY
				removePos = (Collection<Integer>) statistics.get(tester.frame
																		.statistics
																		.heuristic
																		.Constants
																		.STATISTIC_POS_COMPACTED_UNIEVRSE_REMOVED
																);
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_HISTORY), 
							removePos==null? null: StringUtils.toString(removePos, 100), 
							null
						);
				// Sum(remove POS(U/C))：COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE), 
							removePos==null? null: intSum(removePos), 
							null
						);
				// Eva(remove POS(U/C))：COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION
				if (removePos!=null) {
					evalEach = new ArrayList<>(removePos.size());
					universeSize = datasetInfo.getUniverseSize();
					evalDenominator = 0;
					for (int rm : removePos) {
						evalEach.add((rm+0.0) / universeSize);
						evalDenominator += universeSize;
						universeSize -= rm;
					}
					record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION), 
								evalDenominator==0? 0: intSum(removePos) / (evalDenominator+0.0), 
								null
						);
				}else {
					record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION), 
								0, null
					);
				}
				
				// remove NEG(U/C)：COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY
				removeNeg = (Collection<Integer>) statistics.get(tester.frame
																		.statistics
																		.heuristic
																		.Constants
																		.STATISTIC_NEG_COMPACTED_UNIEVRSE_REMOVED
																);
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY), 
							removeNeg==null? null: StringUtils.toString(removeNeg, 100), 
							null
						);
				// Sum(remove NEG(U/C))：COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE), 
							removeNeg==null? null: intSum(removeNeg), 
							null
						);
				// Eva(remove NEG(U/C))：COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION
				if (removeNeg!=null) {
					evalEach = new ArrayList<>(removeNeg.size());
					universeSize = datasetInfo.getUniverseSize();
					evalDenominator = 0;
					for (int rm : removeNeg) {
						evalEach.add((rm+0.0) / universeSize);
						evalDenominator += universeSize;
						universeSize -= rm;
					}
					record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION), 
								evalDenominator==0? 0: intSum(removeNeg) / (evalDenominator+0.0), 
								null
						);
				}else {
					record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION), 
								0, null
					);
				}
				
				// remove U/C：COMPACTED_UNIVERSE_REMOVE_NUMBER_HISTORY
				remove = new ArrayList<>(Math.max(removePos==null?0: removePos.size(), 
													removeNeg==null?0: removeNeg.size()
												)
										);
				if ((removePos!=null && removePos.size()>0) || 
					(removeNeg!=null && removeNeg.size()>0)
				) {
					Iterator<Integer> posIterator = null, negIterator = null;
					if (removePos!=null)	posIterator = removePos.iterator();
					if (removeNeg!=null)	negIterator = removeNeg.iterator();
					while ((posIterator!=null && posIterator.hasNext()) ||
							(negIterator!=null && negIterator.hasNext())
					) {
						int pos = posIterator==null? 0: posIterator.hasNext()? posIterator.next(): 0;
						int neg = negIterator==null? 0: negIterator.hasNext()? negIterator.next(): 0;
						remove.add(pos+neg);
					}
				}
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_NUMBER_HISTORY), 
							remove==null? null: StringUtils.toString(remove, 100), 
							null
						);
				// Sum(remove U/C)：COMPACTED_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE), 
							remove==null? null: intSum(remove), 
							null
						);
				// Eva(remove U/C)：COMPACTED_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION
				evalEach = new ArrayList<>(remove.size());
				Integer equClassSize = (Integer) statistics.get(tester.frame
															.statistics
															.heuristic
															.Constants
															.STATISTIC_COMPACTED_SIZE
														); 
				evalDenominator = 0;
				if (equClassSize!=null) {
					evalDenominator = 0;
					for (int rm : remove) {
						evalEach.add((rm+0.0) / equClassSize);
						evalDenominator += equClassSize;
						equClassSize -= rm;
					}
				}
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION), 
						StringUtils.numberToString(evalEach, 100, 4), 
						null
					);
				// Eva(Sum(remove U/C))：COMPACTED_UNIVERSE_REMOVE_TOTAL_EVALUATION
				record.set(titleMap.get(Constants.PlainRecordInfo.COMPACTED_UNIVERSE_REMOVE_TOTAL_EVALUATION), 
							evalDenominator==0? 0: intSum(remove) / (evalDenominator+0.0), 
							null
						);
			}
			
			private static void loadQuickReductTime(PlainRecord record, Map<String, RecordFieldInfo> titleMap,
													DatasetInfo datasetInfo, Map<String, Long> componentTagTimeMap
			) {
				// TIME_INIT
				record.set(titleMap.get(Constants.PlainRecordInfo.TIME_INIT), 
							NumberUtils.decimalLeftString(6, TimerUtils.nanoTimeToMillis(datasetInfo.getInitTime())), 
							null
						);
				// TIME_COMPRESS
				Long compressTime = componentTagTimeMap.get(ComponentTags.TAG_COMPACT);
				record.set(titleMap.get(Constants.PlainRecordInfo.TIME_COMPRESS), 
							NumberUtils.decimalLeftString(6, TimerUtils.nanoTimeToMillis(compressTime==null? 0: compressTime)), 
							null
						);
				// TIME_CORE
				Long coreTime = componentTagTimeMap.get(ComponentTags.TAG_CORE);
				record.set(titleMap.get(Constants.PlainRecordInfo.TIME_CORE), 
							NumberUtils.decimalLeftString(6, TimerUtils.nanoTimeToMillis(coreTime==null? 0: coreTime)), 
							null
						);
				// TIME_RED
				Long redTime = componentTagTimeMap.get(ComponentTags.TAG_SIG);
				record.set(titleMap.get(Constants.PlainRecordInfo.TIME_RED), 
							NumberUtils.decimalLeftString(6, TimerUtils.nanoTimeToMillis(redTime==null? 0: redTime)), 
							null
						);
				// TIME_INSPECT
				Long inspectTime = componentTagTimeMap.get(ComponentTags.TAG_CHECK);
				record.set(titleMap.get(Constants.PlainRecordInfo.TIME_INSPECT), 
							NumberUtils.decimalLeftString(6, TimerUtils.nanoTimeToMillis(inspectTime==null? 0: inspectTime)), 
							null
						);
			}
			
			private static void loadQuickReductCore(PlainRecord record, Map<String, RecordFieldInfo> titleMap,
													ProcedureParameters parameters, Map<String, Object> statistics
			) {
				// CORE_INCLUDE
				Boolean byCore = parameters.get("byCore");
				record.set(titleMap.get(Constants.PlainRecordInfo.CORE_INCLUDE), 
							byCore!=null && byCore, 
							null
						);
				// CORE_SIZE
				Integer[] core = (Integer[]) statistics.get(tester.frame
																.statistics
																.heuristic
																.Constants
																.STATISTIC_CORE_LIST
															);
				record.set(titleMap.get(Constants.PlainRecordInfo.CORE_SIZE), 
							core!=null? core.length: 0, 
							null
						);
				// CORE_LIST
				if (core!=null)	Arrays.sort(core);
				record.set(titleMap.get(Constants.PlainRecordInfo.CORE_LIST), 
							StringUtils.numberToString(core, 100), 
							null
						);
				// STATISTIC_CORE_ATTRIBUTE_EXAMED_LENGTH
				Object coreExamSize = statistics.get(tester.frame
															.statistics
															.heuristic
															.Constants
															.STATISTIC_CORE_ATTRIBUTE_EXAMED_LENGTH
													);
				record.set(titleMap.get(Constants.PlainRecordInfo.CORE_ATTRIBUTE_EXAM_SIZE), 
							coreExamSize==null?0: coreExamSize, 
							null
						);
			}	

			private static void loadQuickReductReductResult(PlainRecord record, Map<String, RecordFieldInfo> titleMap,
															Map<String, Object> statistics
			) {
				@SuppressWarnings("unchecked")
				Collection<Integer> redCandidate = (Collection<Integer>) statistics.get(tester.frame
																						.statistics
																						.heuristic
																						.Constants
																						.STATISTIC_RED_BEFORE_INSPECT
																					);
				@SuppressWarnings("unchecked")
				Collection<Integer> red = (Collection<Integer>) statistics.get(tester.frame
																					.statistics
																					.heuristic
																					.Constants
																					.STATISTIC_RED_AFTER_INSPECT
																				);
				int[] redundant = new int[(redCandidate==null?0:redCandidate.size())-(red==null?0:red.size())];
				if (redundant.length!=0 && red!=null && redCandidate!=null) {
					int i=0;	
					for (int attr : redCandidate)	if (!red.contains(attr))	redundant[i++] = attr;
				}
				// REDUCT_SIZE
				record.set(titleMap.get(Constants.PlainRecordInfo.REDUCT_SIZE), 
							red==null? (redCandidate==null?0:redCandidate.size()): red.size(), 
							null
						);
				// REDUCT_LIST
				List<Integer> redList = new ArrayList<>(red!=null? red: redCandidate);
				Collections.sort(redList);
				record.set(titleMap.get(Constants.PlainRecordInfo.REDUCT_LIST), 
							StringUtils.toString(redList, 100), 
							null
						);
				// REDUNDANT_SIZE_BEFORE_INSPECT
				record.set(titleMap.get(Constants.PlainRecordInfo.REDUNDANT_SIZE_BEFORE_INSPECT), 
							redundant.length, 
							null
						);
				// REDUNDANT_LIST_BEFORE_INSPECT
				record.set(titleMap.get(Constants.PlainRecordInfo.REDUNDANT_LIST_BEFORE_INSPECT), 
							StringUtils.intToString(redundant, 100), 
							null
						);
			}
		}
	}
	
	public static String record(RecordFieldInfo title, PlainRecord record, int maxItem, int decimalLeft) {
		PlainRecordItem<?, ?> recordItem = record.getRecordItems().get(title.getField());
		return recordItem==null? "null": record(recordItem.getValue(), maxItem, decimalLeft);
	}
	
	@SuppressWarnings("unchecked")
	public static String record(Object value, int maxItem, int decimalLeft) {
		StringBuilder builder = new StringBuilder();
		if (value==null) {
			builder.append("null");
		}else if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			if (!collection.isEmpty()) {
				if (collection.iterator().next() instanceof Double)
					builder.append(StringUtils.numberToString((Collection<Double>) collection, maxItem, decimalLeft));
				else
					builder.append(StringUtils.toString(collection, maxItem));
			}else {
				builder.append("");
			}
		}else if (value instanceof int[]) {
			builder.append(StringUtils.intToString((int[]) value, maxItem));
		}else if (value instanceof Integer[]) {
			builder.append(StringUtils.numberToString((Integer[]) value, maxItem));
		}else if (value instanceof double[]) {
			builder.append(StringUtils.doubleToString((double[]) value, maxItem, decimalLeft));
		}else if (value instanceof Double[]) {
			builder.append(StringUtils.numberToString((Double[]) value, maxItem, decimalLeft));
		}else if (value instanceof long[]) {
			builder.append(StringUtils.longToString((long[]) value, maxItem));
		}else if (value instanceof Long[]) {
			builder.append(StringUtils.numberToString((Long[]) value, maxItem));
		}else if (value instanceof String[]) {
			builder.append(StringUtils.toString((String[]) value, maxItem));
		}else {
			builder.append(value);
		}
		return builder.toString();
	}

	public static double numberSum(Collection<Number> collection) {
		if (collection==null || collection.isEmpty())	return 0;
		return collection.stream()
						.map(num->num.doubleValue())
						.reduce(Double::sum)
						.orElse(0.0);
	}
	
	public static int intSum(Collection<Integer> collection) {
		if (collection==null || collection.isEmpty())	return 0;
		return collection.stream().reduce(Integer::sum).get();
	}
	
	public static double doubleAverage(double[] array) {
		return array==null?0: Arrays.stream(array).average().getAsDouble();
	}
}