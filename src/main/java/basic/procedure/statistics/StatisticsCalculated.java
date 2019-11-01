package basic.procedure.statistics;

import java.util.Map;

/**
 * Storing calculated statistics using {@link Map} with in String key-Object value format.
 * 
 * @author Benjamin_L
 */
public interface StatisticsCalculated {
	/**
	 * Get the name of the statistics.
	 * 
	 * @return The name in {@link String}.
	 */
	String staticsName();
	/**
	 * A {@link Map} contains statistics.
	 * 
	 * @return statistics in {@link Map} with String keys and Object values.
	 */
	Map<String, Object> getStatistics();
}