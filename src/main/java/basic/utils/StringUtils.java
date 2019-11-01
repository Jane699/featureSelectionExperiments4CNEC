package basic.utils;

import java.util.Collection;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
	public static String shortString(String str, int maxLength) {
		if (str.length()>maxLength) {
			int part = maxLength / 8, strLength = str.length();
			StringBuilder builder = new StringBuilder();
			builder.append(str.substring(0, part));
			builder.append("...");
			builder.append(str.substring(strLength-5*part, strLength));
			return builder.toString();
		}else {
			return str;
		}
	}
	
	public static <T> String toString(T[] array, int maxItem) {
		StringBuilder builder = new StringBuilder();
		if (array!=null)	builder.append("("+array.length+")");
		builder.append("[");
		if (array!=null) {
			int i=0;
			for (T t: array) {
				i++;
				builder.append(t);
				if (i!=array.length)	builder.append(", ");
				if (i>=maxItem)			break;
			}
			if (i!=array.length)		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static <T> String toString(Collection<T> collection, int maxItem) {
		StringBuilder builder = new StringBuilder();
		if (collection!=null)	builder.append("("+collection.size()+")");
		builder.append("[");
		if (collection!=null) {
			int i=0;
			for (T t: collection) {
				i++;
				builder.append(t);
				if (i!=collection.size())	builder.append(", ");
				if (i>=maxItem)				break;
			}
			if (i!=collection.size())		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static String intToString(int[] array, int maxItem) {
		StringBuilder builder = new StringBuilder();
		if (array!=null)	builder.append("("+array.length+")");
		builder.append("[");
		if (array!=null) {
			int i=0;
			for (int value: array) {
				i++;
				builder.append(value);
				if (i!=array.length)	builder.append(", ");
				if (i>=maxItem)			break;
			}
			if (i!=array.length)		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static String doubleToString(double[] array, int maxItem, int decimalLeft) {
		StringBuilder builder = new StringBuilder();
		if (array!=null)	builder.append("("+array.length+")");
		builder.append("[");
		if (array!=null) {
			int i=0;
			for (double value: array) {
				i++;
				builder.append(String.format("%."+decimalLeft+"f", value));
				if (i!=array.length)	builder.append(", ");
				if (i>=maxItem)			break;
			}
			if (i!=array.length)		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static String longToString(long[] array, int maxItem) {
		StringBuilder builder = new StringBuilder();
		if (array!=null)	builder.append("("+array.length+")");
		builder.append("[");
		if (array!=null) {
			int i=0;
			for (long value: array) {
				i++;
				builder.append(value);
				if (i!=array.length)	builder.append(", ");
				if (i>=maxItem)			break;
			}
			if (i!=array.length)		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static String numberToString(Double[] array, int maxItem, int decimalLeft) {
		if (decimalLeft<0)	decimalLeft = 2;
		StringBuilder builder = new StringBuilder();
		if (array!=null)	builder.append("("+array.length+")");
		builder.append("[");
		if (array!=null) {
			int i=0;
			for (Double d: array) {
				i++;
				builder.append(String.format("%."+decimalLeft+"f", d.doubleValue()));
				if (i!=array.length)	builder.append(", ");
				if (i>=maxItem)			break;
			}
			if (i!=array.length)		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static <T extends Number> String numberToString(T[] array, int maxItem) {
		StringBuilder builder = new StringBuilder();
		if (array!=null)	builder.append("("+array.length+")");
		builder.append("[");
		if (array!=null) {
			int i=0;
			for (T t: array) {
				i++;
				builder.append(t);
				if (i!=array.length)	builder.append(", ");
				if (i>=maxItem)			break;
			}
			if (i!=array.length)		builder.append("...");
		}
		return builder.append("]").toString();
	}
	
	public static String numberToString(Collection<? extends Number> collection, int maxItem, int decimalLeft) {
		if (decimalLeft<0)	decimalLeft = 2;
		StringBuilder builder = new StringBuilder();
		if (collection!=null)	builder.append("("+collection.size()+")");
		builder.append("[");
		if (collection!=null) {
			int i=0;
			for (Number d: collection) {
				i++;
				builder.append(String.format("%."+decimalLeft+"f", d.doubleValue()));
				if (i!=collection.size())	builder.append(", ");
				if (i>=maxItem)				break;
			}
			if (i!=collection.size())		builder.append("...");
		}
		return builder.append("]").toString();
	}
}