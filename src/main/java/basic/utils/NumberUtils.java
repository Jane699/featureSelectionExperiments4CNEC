package basic.utils;

import java.math.BigDecimal;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {
	public static final double DECIMAL_WITH_5_ZERO = 1.0E-06,	// 5 zeros
								DECIMAL_WITH_6_ZERO = 1.0E-07,	// 6 zeros
								DECIMAL_WITH_7_ZERO = 1.0E-08,	// 7 zeros
								DECIMAL_WITH_8_ZERO = 1.0E-09,	// 8 zeros
								DECIMAL_WITH_9_ZERO = 1.0E-10,	// 9 zeros
								DECIMAL_WITH_10_ZERO = 1.0E-11,	//10 zeros
								DECIMAL_WITH_11_ZERO = 1.0E-12,	//11 zeros
								DECIMAL_WITH_12_ZERO = 1.0E-13,	//12 zeros
								DECIMAL_WITH_13_ZERO = 1.0E-14,	//13 zeros
								DECIMAL_WITH_14_ZERO = 1.0E-15,	//14 zeros
								DECIMAL_WITH_15_ZERO = 1.0E-16,	//15 zeros
								DECIMAL_WITH_16_ZERO = 1.0E-17,	//16 zeros
								DECIMAL_WITH_17_ZERO = 1.0E-18,	//17 zeros
								DECIMAL_WITH_18_ZERO = 1.0E-19,	//18 zeros
								DECIMAL_WITH_19_ZERO = 1.0E-20,	//18 zeros
								DECIMAL_WITH_20_ZERO = 1.0E-21,	//20 zeros
								DECIMAL_WITH_25_ZERO = 1.0E-26,	//25 zeros
								DECIMAL_WITH_30_ZERO = 1.0E-31;	//30 zeros
	
	/**
	 * Transfer the given double value into string with limited decimal places quoted.
	 * 
	 * @see {@link String#format(String, Object...)}
	 * 
	 * @param decimal
	 * 		The decimal places.
	 * @param value
	 * 		A {@link double} value to be transfered.
	 * @return Transfered string value.
	 */
	public static String decimalLeftString(int decimal, double value) {
		return String.format("%."+decimal+"f", value);
	}
	
	public static <K> void count(Map<K, Integer> counter, K key, int countTimes) {
		Integer count = counter.get(key);
		if (count==null)	counter.put(key, countTimes);
		else				counter.put(key, count+countTimes);
	}


	public static Double getDouble(Object value) {
		if (value==null)	return null;
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).doubleValue();
		}else if (value instanceof String) {
			try {	return Double.valueOf(value.toString());	}catch(Exception e) {	return null;	}
		}else {
			return (double) value;
		}
	}
	
	public static Integer getInteger(Object value) {
		if (value==null)	return null;
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).intValue();
		}else if (value instanceof String) {
			try {	return Integer.valueOf(value.toString());	}catch(Exception e) {	return null;	}
		}else {
			return (int) value;
		}
	}
}