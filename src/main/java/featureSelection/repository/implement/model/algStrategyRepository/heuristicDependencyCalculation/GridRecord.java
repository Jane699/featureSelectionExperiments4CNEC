package featureSelection.repository.implement.model.algStrategyRepository.heuristicDependencyCalculation;

/**
 * A <code>grid record</code> as a line in <code>grid</code> for {@link HeiristicDependencyCalculationAlgorithm}.
 * 
 * @author Benjamin_L
 */
public class GridRecord {
	private int[] conditionalAttributes;
	private int decisionClass;
	private boolean classStatus;					// true:0-Unique, false:1-Non-Unique
	
	public GridRecord(int[] attributeValues, int decisionClass) {
		this.conditionalAttributes = attributeValues;
		this.decisionClass = decisionClass;
		this.classStatus = true;
	}

	public int[] getConditionalAttributes() {
		return conditionalAttributes;
	}
	
	public int getDecisionClass() {
		return decisionClass;
	}
	
	/**
	 * Set the {@link #classStatus} of the grid record: 0 as unique.
	 * 
	 * @param unique
	 * 		The status to be set. true : 0 as unique, false : 1 as non-unique.
	 * @return <code>this</code>.
	 */
	public GridRecord setClassStatus(boolean unique) {
		this.classStatus = unique;
		return this;
	}
	
	/**
	 * Return if {@link #classStatus} is 0, i.e. unique.
	 * 
	 * @return true if unique.
	 */
	public boolean cnst() {
		return classStatus;
	}
	
	@Override
	public String toString() {
		return "GridRecord [conditionalAttributes=" + conditionalAttributes + ", decisionClass=" + decisionClass
				+ ", classStatus=" + classStatus + "]";
	}
}