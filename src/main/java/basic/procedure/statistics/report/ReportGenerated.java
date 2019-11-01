package basic.procedure.statistics.report;

/**
 * Generate report.
 * 
 * @author Benjamin_L
 * 
 * @see {@link ReportListGenerated}
 * @see {@link ReportMapGenerated}
 *
 * @param <Report>
 */
public interface ReportGenerated<Report> {
	/**
	 * Get the name of the report.
	 * 
	 * @return The name in {@link String}.
	 */
	String reportName();
	/**
	 * Get the report.
	 * 
	 * @return {@link Report}.
	 */
	Report getReport();
}