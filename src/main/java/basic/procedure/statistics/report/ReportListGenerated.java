package basic.procedure.statistics.report;

import java.util.List;

/**
 * An interface for {@link Report} whose items are collected in a {@link List}.
 * 
 * @author Benjamin_L
 * 
 * @see {@link List}
 * @see {@link ReportMapGenerated}
 *
 * @param <Item>
 * 		Type of report item in {@link List}.
 */
public interface ReportListGenerated<Item> extends ReportGenerated<List<Item>> {

}
