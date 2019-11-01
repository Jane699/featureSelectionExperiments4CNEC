package basic.utils;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerUtil {
	
	public static void methodInit(Logger logger, Class<?> clazz, String methodName, String...params) {
		if (logger==null||params==null)	return;
		StringBuilder builder = new StringBuilder(clazz.getSimpleName()+".java"+" - "+methodName+"()");
		if (params!=null&&params.length!=0) {
			builder.append(" - param :");
			for (int i=0; i<params.length; i++) {
				builder.append(" "+params[i]);
				if (i!=params.length-1)	builder.append(",");
			}
		}
		logger.info(builder.toString());
	}
	
	public static String printLine(String symbol, int length) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<length; i++) builder.append(symbol);
		builder.append("\r\n");
		return builder.toString();
	}
	
	public static void printLine(Logger logger, String symbol, int length) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<length; i++) builder.append(symbol);
		logger.info(builder.toString());
	}

	public static <T> void printArray(Logger logger, String arrayName, int width, 
										@SuppressWarnings("unchecked") T...array
	) {
		int[] widthStr = new int[width];
		String[] arrayStr = new String[array.length];
		for (int i=0; i<array.length; i++) {
			arrayStr[i] = array[i].toString();
			int pos = i % width;
			if (widthStr[pos] < arrayStr[i].length())	widthStr[pos] = arrayStr[i].length();
		}
		StringBuilder builder = null;
		printLine(logger, "-", 50);
		logger.info(arrayName+" ( size : "+array.length+" ) : ");
		printLine(logger, "-", 50);
		for (int i=0; i<array.length; i++) {
			if ( i % width == 0 )	builder = new StringBuilder("	"+" | ");
			builder.append(String.format("%"+widthStr[i%width]+"s"+" | ", arrayStr[i]));
			if ( i % width == width-1) {
				logger.info(builder.toString());
				builder=null;
			}
		}
		if (builder!=null)	logger.info(builder.toString());
		printLine(logger, "-", 50);
	}
	
	public static <T> void printCollection(Logger logger, String arrayName, int width, Collection<T> collection) {
		int[] widthStr = new int[width];
		String[] arrayStr = new String[collection.size()];
		int i=0;
		for (T t: collection) {
			arrayStr[i] = t.toString();
			int pos = i % width;
			if (widthStr[pos] < arrayStr[i].length())	widthStr[pos] = arrayStr[i].length();
			i++;
		}
		StringBuilder builder = null;
		printLine(logger, "-", 50);
		logger.info(arrayName+" ( size : "+collection.size()+" ) : ");
		printLine(logger, "-", 50);
		for (i=0; i<collection.size(); i++) {
			if ( i % width == 0 )	builder = new StringBuilder("	"+" | ");
			builder.append(String.format("%"+widthStr[i%width]+"s"+" | ", arrayStr[i]));
			if ( i % width == width-1) {
				logger.info(builder.toString());
				builder=null;
			}
		}
		if (builder!=null)	logger.info(builder.toString());
		printLine(logger, "-", 50);
	}

	public static <Key, Value> void printMap(Logger logger, String mapName, int width, Map<Key, Value> map) {
		int i=0;		String[] keyValue = new String[map.size()];
		for (Map.Entry<Key, Value> entry : map.entrySet())	keyValue[i++] = entry.getKey()+" = "+entry.getValue();
		printArray(logger, mapName, width, keyValue);
	}
	
	public static void print2DDouble(Logger logger, String name, int space, double[][] data) {
		print2DDouble(logger, name, space, data, 2);
	}

	public static void print2DDouble(Logger logger, String name, int space, double[][] data, int decimal) {
		logger.info(spaceFormat(space, "{} : double[{}][{}]" ), name, data.length, data[0].length);
		StringBuilder builder;
		for (int r=0; r<data.length; r++) {
			builder = new StringBuilder();
			for (int c=0; c<data[r].length; c++) {
				if (data[r][c]>=0)		builder.append(" ");
				else					builder.append("-");
				builder.append(String.format("%."+decimal+"f", Math.abs(data[r][c])));
				if (c<data[r].length-1)	builder.append(",");
			}
			logger.info(spaceFormat(space+1, "Row {}: [{}]"), r+1, builder.toString());
		}
	}
	
	public static String spaceFormat(int space, String content) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<space; i++)	builder.append("	");
		return builder.append(content).toString();
	}
}