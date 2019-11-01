package featureSelection.repository.frame.model.universe;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A model for {@link UniverseInstance} generating guidance. The key-value map of each column is saved by 
 * <code>columnValueMap</code>. The last ID of generated {@link UniverseInstance} is saved by 
 * <code>lastUniverseID</code>. By calling {@link UniverseInstance#setID(int)}, {@link UniverseInstance}'s
 * id is set.
 * 
 * @author Benjamin_L
 */
@Data
@NoArgsConstructor
public class UniverseGeneratingGuidance {
	private Map<String, Integer>[] columnValueMap;
	private int lastUniverseInstanceID;
	
	/**
	 * Constructor.
	 * 
	 * @param columnSize
	 * 		The column number of {@link UniverseInstance}. (Condition attributes + Decision attribute)
	 */
	@SuppressWarnings("unchecked")
	public UniverseGeneratingGuidance(int columnSize) {
		columnValueMap = new Map[columnSize];
		for (int i=0; i<columnSize; i++)	columnValueMap[i] = new HashMap<>();
		lastUniverseInstanceID = 0;
	}
	
	/**
	 * Set the column value map of <code>columnIndex<code>.
	 * 
	 * @param columnIndex
	 * 		The index of the column. (Starts from 0)
	 * @param columnValueMap
	 * 		The column value {@link Map}.
	 */
	public void setColumnValueMapOf(int columnIndex, Map<String, Integer> columnValueMap) {
		this.columnValueMap[columnIndex] = columnValueMap;
	}
	
	/**
	 * The number of the column of {@link UniverseInstance}.
	 * 
	 * @return the number of the column.
	 */
	public int getColumnSize() {
		return columnValueMap==null? 0: columnValueMap.length;
	}
	
	/**
	 * Transfer and return current {@link UniverseGeneratingGuidance} into 
	 * {@link UniverseGeneratingGuidanceTemplate}.
	 * 
	 * @see {@link UniverseGeneratingGuidanceTemplate}
	 * 
	 * @return transfered {@link UniverseGeneratingGuidanceTemplate} instance.
	 */
	public UniverseGeneratingGuidanceTemplate toUniverseGeneratingGuidanceTemplate() {
		return new UniverseGeneratingGuidanceTemplate(this);
	}
}