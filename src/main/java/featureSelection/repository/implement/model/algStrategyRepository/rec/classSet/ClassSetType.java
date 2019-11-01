package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet;

public enum ClassSetType {
	/**
	 * 1-REC
	 */
	POSITIVE(1), 
	/**
	 * -1-REC
	 */
	NEGATIVE(-1), 
	/**
	 * 0-REC
	 */
	BOUNDARY(0);
	
	private int code;
	ClassSetType(int code) {
		this.code = code;
	}

	/**
	 * Get the code value of current enum.
	 * 
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
}
