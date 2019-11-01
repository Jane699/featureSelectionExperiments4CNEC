package basic.utils;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

/**
 * Utilities for array.
 * 
 * @author Benjamin_L
 */
@UtilityClass
public class ArrayUtils {
	public static final int ARRAY_TO_STRING_20_ITEMS = 20;
	public static final int ARRAY_TO_STRING_100_ITEMS = 100;
	public static final int ARRAY_TO_STRING_500_ITEMS = 500;
	public static final int ARRAY_TO_STRING_1000_ITEMS = 1000;
	
	/**
	 * Transfer {@link int[]} to {@link String} with limited shown length of String.
	 * 
	 * @see {@link Arrays#toString(int[])}
	 * 
	 * @param array
	 * 		The {@link int[]} to be transfered.
	 * @param maxShownLength
	 * 		The max length of {@link String} shown.
	 * @return {@link String}.
	 */
	public static String intArrayToString(int[] array, int maxShownLength) {
		if (array==null)	return "".intern();
		if (maxShownLength>0 && array.length>maxShownLength) {
			StringBuilder builder = new StringBuilder();
			builder.append("\"[".intern());
			for (int i=0; i<maxShownLength; i++)	builder.append(array[i]+",".intern());
			builder.append("...]\"".intern());
			return builder.toString();						
		}else {
			return "\""+Arrays.toString(array)+"\"";
		}
	}

	/**
	 * Transfer {@link byte[]} to {@link String} with limited shown length of String.
	 * 
	 * @see {@link Arrays#toString(byte[])}
	 * 
	 * @param array
	 * 		The {@link byte[]} to be transfered.
	 * @param maxShownLength
	 * 		The max length of {@link String} shown.
	 * @return {@link String}.
	 */
	public static String byteArrayToString(byte[] array, int maxShownLength) {
		if (array==null)	return "".intern();
		if (array.length>maxShownLength) {
			StringBuilder builder = new StringBuilder();
			builder.append("\"[".intern());
			for (int i=0; i<maxShownLength; i++)	builder.append(array[i]+",".intern());
			builder.append("...]\"".intern());
			return builder.toString();						
		}else {
			return "\""+Arrays.toString(array)+"\"";
		}
	}

	/**
	 * Transfer {@link double[]} to {@link String} with limited shown length of String.
	 * 
	 * @see {@link Arrays#toString(double[])}
	 * 
	 * @param array
	 * 		The {@link double[]} to be transfered.
	 * @param maxShownLength
	 * 		The max length of {@link String} shown.
	 * @return {@link String}.
	 */
	public static String doubleArrayToString(double[][] array) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int a=0; a<array.length; a++) {
			builder.append("[");
			for (int b=0; b<array[a].length; b++) {
				builder.append(array[a][b]);
				if (b!=array[a].length-1)	builder.append(",");
			}
			builder.append("]");
			if (a!=array.length-1)	builder.append(",");
		}
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Initiate int[] with incremental elements.
	 * <p>
	 * This method is just like <code>np.arange()</code> in <code>Python</code>.
	 * 
	 * @param length
	 * 		The length of the {@link int[]}.
	 * @param startValue
	 * 		The 1st value in array.
	 * @param increment
	 * 		Increment of each value in array.(Step)
	 * @return
	 */
	public static int[] initIncrementalValueIntArray(int length, int startValue, int increment) {
		int[] array = new int[length];
		for (int i=0, value=startValue; i<length; i++, value+=increment)	array[i] = value;
		return array;
	}
}