package featureSelection.statistics;

import featureSelection.utils.DBUtils;

public class Constants {
	
	public final static class Database {
		public static final String TABLE_DATASET = "DATASET";
		public static final String TABLE_ALGORITHM = "ALGORITHM";
		public static final String TABLE_PARAMETER_EXT_QR = "PARAMETER_EXT_QR";
		public static final String TABLE_PARAMETER_GENERAL = "PARAMETER_GENERAL";
		public static final String TABLE_RUN_TIME_EXT_QR = "RUN_TIME_EXT_QR";
		public static final String TABLE_RUN_TIME_GENERAL = "RUN_TIME_GENERAL";
		
		public static final String EXPERIMENT_MARK = DBUtils.EXP_MARK_C_NEC;
	}
	
	public final static class PlainRecordInfo {
		public final static String DATASET_ID = "dataset.id";
		public final static String DATASET_NAME = "dataset";

		public final static String CONTAINER_ID = "id";
		public final static String DATETIME = "datetime";
		public final static String DATABASE_UNIQUE_ID= "database.uniqueID";
		
		public final static String TOTAL_TIME = "time(运行总时间)";
		public final static String PURE_TIME = "time(除去检查总时间)";
		public final static String RUN_TIMES = "times(次数)";

		public final static String ALG_ID = "Alg.id";
		public final static String ALG = "Alg";
		public final static String PARAMETER_ID = "Parameter.id";
		public final static String ORIGINAL_UNIVERSE_SIZE = "|U|";
		public final static String PURE_UNIVERSE_SIZE = "|AU|";
		public final static String UNIVERSE_PURE_RATE = "Ratio_U";
		public final static String ATTRIBUTE_SIZE = "|Attr|";
		public final static String ATTRIBUTE_LIST = "Attr";
		public final static String DECISION_VALUE_NUMBER = "|D|";

		public final static String CALCULATION_TIMES = "Cal. Times";
		public final static String CALCULATION_ATTR_LEN = "sum(Cal. AttrLen)";
		
		public final static String SIGNIFICANCE_HISTORY = "SIGs";
		public final static String SIGNIFICANCE_SUM = "sum(SIGs)";
		public final static String SIGNIFICANCE_AVERAGE = "ave(SIGs)";

		public final static String TOTAL_UNIVERSE_POS_REMOVE_NUMBER_HISTORY = "remove POS(U)";
		public final static String TOTAL_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE = "Sum(remove POS(U))";
		public final static String TOTAL_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION = "Eva(remove POS(U))";
		
		public final static String TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY = "remove NEG(U)";
		public final static String TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE = "Sum(remove NEG(U))";
		public final static String TOTAL_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION = "Eva(remove NEG(U))";
		
		public final static String TOTAL_UNIVERSE_REMOVE_NUMBER_HISTORY = "remove U";
		public final static String TOTAL_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE = "Sum(remove U)";

		public final static String TOTAL_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION = "Eva(remove U)";
		public final static String TOTAL_UNIVERSE_REMOVE_TOTAL_EVALUATION = "Eva(Sum(remove U))";
		
		public final static String COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_HISTORY = "remove POS(U/C)";
		public final static String COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE = "Sum(remove POS(U/C))";
		public final static String COMPACTED_UNIVERSE_POS_REMOVE_NUMBER_EVALUATION = "Eva(remove POS(U/C))";
		
		public final static String COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_HISTORY = "remove NEG(U/C)";
		public final static String COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE = "Sum(remove NEG(U/C))";
		public final static String COMPACTED_UNIVERSE_NEG_REMOVE_NUMBER_EVALUATION = "Eva(remove NEG(U/C))";
		
		public final static String COMPACTED_UNIVERSE_REMOVE_NUMBER_HISTORY = "remove U/C";
		public final static String COMPACTED_UNIVERSE_REMOVE_NUMBER_SUM_4_TOTAL_UNIVERSE = "Sum(remove U/C)";

		public final static String COMPACTED_UNIVERSE_REMOVE_INDIVIDUAL_EVALUATION = "Eva(remove U/C)";
		public final static String COMPACTED_UNIVERSE_REMOVE_TOTAL_EVALUATION = "Eva(Sum(remove U/C))";
		
		public final static String TIME_INIT = "init_T";
		public final static String TIME_COMPRESS = "comp_T";
		public final static String TIME_CORE = "core_T";
		public final static String TIME_RED = "red_T";
		public final static String TIME_INSPECT = "red_inspect_T";
		
		public final static String CORE_INCLUDE = "Core ?";
		public final static String CORE_SIZE = "|Core|";
		public final static String CORE_LIST = "Core";
		public final static String CORE_ATTRIBUTE_EXAM_SIZE = "|CoreAttrUsed|";
		
		public final static String REDUCT_LIST = "Reduct";
		public final static String REDUCT_SIZE = "|Reduct|";
		public final static String REDUNDANT_LIST_BEFORE_INSPECT = "Redundant";
		public final static String REDUNDANT_SIZE_BEFORE_INSPECT = "|Redundant|";

		public final static String PROCEDURE_CLASSES_INFO = "Procedures";
		public final static String PROCEDURE_INFO = "Procedure Tester Info";
	}
	
	public final static class Report {
		public final static String REPORT_FILE_BASE_FOLDER = "reports";
		public final static String REPORT_SINGLE_REPORT_FOLDER = "single reports";

		public final static String REPORT_FIRST_COLUMN_DESCRIPTION = "Description";
	}
}