package basic.procedure.statistics.report;

import java.util.Map;

/**
 * Generate report.
 * 
 * @author Benjamin_L
 *
 * @param <Key>
 * 		Type of report item key associated with value.
 * @param <Value>
 * 		Type of report item value associated with key.
 */
public interface ReportMapGenerated<Key, Value> extends ReportGenerated<Map<Key, Value>> {
	/**
	 * Get the report key order.
	 * 
	 * @return Keys in {@link String[]} in order.
	 */
	String[] getReportMapKeyOrder();
}