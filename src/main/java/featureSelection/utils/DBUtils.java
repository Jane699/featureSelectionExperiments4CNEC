package featureSelection.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import basic.procedure.parameter.ProcedureParameters;
import featureSelection.statistics.Constants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tester.utils.ProcedureUtils;

@Slf4j
@UtilityClass
public class DBUtils {
	
	public final static String EXP_MARK_ID_REC = "IDREC-EXP";
	public final static String EXP_MARK_C_NEC = "CNEC-EXP";
	
	public static class DatasetID {
		private static final String FILE_NAME = "datasetIDReflects.properties";
		private static final Properties PROPERTIES;
		
		static {
			PROPERTIES = new Properties();
			try (
				InputStream inStream = DBUtils.class.getResourceAsStream('/'+FILE_NAME);
			){
				PROPERTIES.load(inStream);
			} catch (IOException e) {
				//e.printStackTrace();
				log.error("", e);
			}
		}
		
		/**
		 * Get dataset file id in Database by fileName.
		 * 
		 * @param fileName
		 * 		The name of the dataset file.
		 * @return the corresponding id in Database. / -1 if no property matches.
		 */
		public final static int get(String fileName) {
			String strValue = PROPERTIES.getProperty(fileName);
			try {	return strValue!=null? Integer.valueOf(strValue): -1;	}catch(Exception e) {	return -1;	}
		}
	}

	public static class DatasetName {
		private static final String FILE_NAME = "datasetNameReflects.properties";
		private static final Properties PROPERTIES;
		
		static {
			PROPERTIES = new Properties();
			try (
				InputStream inStream = DBUtils.class.getResourceAsStream('/'+FILE_NAME);
			){
				PROPERTIES.load(inStream);
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		/**
		 * Get dataset file id in Database by fileName.
		 * 
		 * @param fileName
		 * 		The name of the dataset file.
		 * @return the corresponding id in Database. / -1 if no property matches.
		 */
		public final static String get(int datasetID) {
			String strValue = PROPERTIES.getProperty(datasetID+"");
			return strValue;
		}
	}
	
	public static class DatasetScale {
		private static final String FILE_NAME = "datasetScaleReflects.properties";
		private static final Properties PROPERTIES;
		
		static {
			PROPERTIES = new Properties();
			try (
				InputStream inStream = DBUtils.class.getResourceAsStream('/'+FILE_NAME);
			){
				PROPERTIES.load(inStream);
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		/**
		 * Get algorithm id in Database by name.
		 * 
		 * @param datasetName
		 * 		The name of the dataset file.
		 * @return the corresponding id in Database. / -1 if no property matches.
		 */
		public final static String get(int datasetID) {
			String strValue = PROPERTIES.getProperty(datasetID+"");
			return strValue;
		}
	}
	
	public static class AlgorithmID {
		private static final String FILE_NAME = "algorithmIDReflects.properties";
		private static final Properties PROPERTIES;
		
		static {
			PROPERTIES = new Properties();
			try (
				InputStream inStream = DBUtils.class.getResourceAsStream('/'+FILE_NAME);
			){
				PROPERTIES.load(inStream);
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		/**
		 * Get algorithm id in Database by name.
		 * 
		 * @param algorithmName
		 * 		The name of the algorithm.
		 * @return the corresponding id in Database. / -1 if no property matches.
		 */
		public final static int get(String algorithmName) {
			String strValue = PROPERTIES.getProperty(algorithmName);
			try {	return strValue!=null? Integer.valueOf(strValue): -1;	}catch(Exception e) {	return -1;	}
		}
	}
	
	public static class AlgorithmName {
		private static final String FILE_NAME = "algorithmNameReflects.properties";
		private static final Properties PROPERTIES;
		
		static {
			PROPERTIES = new Properties();
			try (
				InputStream inStream = DBUtils.class.getResourceAsStream('/'+FILE_NAME);
			){
				PROPERTIES.load(inStream);
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		/**
		 * Get algorithm name in Database by id.
		 * 
		 * @param algorithmName
		 * 		The name of the algorithm.
		 * @return the corresponding name in Database.
		 */
		public final static String get(int algorithmID) {
			String strValue = PROPERTIES.getProperty(algorithmID+"");
			return strValue;
		}
	}
	
	
	public static class ParameterID {
		private static final String FILE_NAME = "parameterIDReflects.properties";
		private static final Properties PROPERTIES;
		
		static {
			PROPERTIES = new Properties();
			try (
				InputStream inStream = DBUtils.class.getResourceAsStream('/'+FILE_NAME);
			){
				PROPERTIES.load(inStream);
			} catch (IOException e) {
				//e.printStackTrace();
				log.error("", e);
			}
		}
		
		/**
		 * Get algorithm id in Database by name.
		 * 
		 * @param parameters
		 * 		{@link ProcedureParameters} instance.
		 * @return the corresponding id in Database. / -1 if no property matches.
		 */
		public final static int get(ProcedureParameters parameters) {
			String param = ProcedureUtils.ShortName.calculation(parameters)+"-"+
							ProcedureUtils.ShortName.byCore(parameters);
			String strValue = PROPERTIES.getProperty(param);
//			log.info("GET parameter id : "+param);
			try {	return strValue!=null? Integer.valueOf(strValue): -1;	}catch(Exception e) {	return -1;	}
		}
	}

	
	public static String generateUniqueID(String dataset, String algorithm, ProcedureParameters parameters) {
		int datasetID = DBUtils.DatasetID.get(dataset);
		String datasetScale = DBUtils.DatasetScale.get(datasetID);
		String datasetName = DBUtils.DatasetName.get(datasetID);
		int algorithmID = DBUtils.AlgorithmID.get(algorithm);
		String algorithmName = DBUtils.AlgorithmName.get(algorithmID);
		int parameterID = DBUtils.ParameterID.get(parameters);
		
		String uniqueID;
		if (EXP_MARK_ID_REC.equals(Constants.Database.EXPERIMENT_MARK) || 
			EXP_MARK_C_NEC.equals(Constants.Database.EXPERIMENT_MARK)
		) {
			uniqueID = Constants.Database.EXPERIMENT_MARK+"-"+
						datasetScale+"-"+
						algorithmName+"-"+ProcedureUtils.ShortName.calculation(parameters)+"-"+
						datasetName+"-"+
						"param-"+parameterID;
			if (datasetID<0 || datasetScale==null || datasetName==null ||
				algorithmID<0 || algorithmName==null ||
				parameterID<0
			) {
				uniqueID = "[With Unknown Field]"+uniqueID;
			}
		}else {
			uniqueID = "Unknown";
		}
		return uniqueID;
	}
}