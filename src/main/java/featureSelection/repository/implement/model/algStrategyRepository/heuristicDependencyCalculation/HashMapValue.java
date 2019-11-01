package featureSelection.repository.implement.model.algStrategyRepository.heuristicDependencyCalculation;

/**
 * A structure entity for {@link HeuristicDependencyCalculationAlgorithm}, is used to calculate consistent records.
 * 
 * @author Benjamin_L
 */
public class HashMapValue {
	private boolean cnst;
	private int count;

	public HashMapValue(boolean cnst){
		this.cnst = cnst;
		count = 1;
	}
	
	public void add() {
		count++;
	}
	
	public void setCnst(boolean cons) {
		this.cnst = cons;
	}
	
	public boolean cnst() {
		return cnst;
	}
	
	public int count() {
		return count;
	}

	@Override
	public String toString() {
		return "HashMapValue [cnst=" + cnst + ", count=" + count + "]";
	}
}