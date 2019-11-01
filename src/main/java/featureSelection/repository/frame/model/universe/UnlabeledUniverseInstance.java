package featureSelection.repository.frame.model.universe;

/**
 * A model for an Universe Instance, represents a line of data in a Decision Table. Different
 * from {@link UniverseInstance}, data of this entity doesn't contain decision value(label).
 * 
 * @see {@link UniverseInstance}
 * 
 * @author Benjamin_L
 */
public class UnlabeledUniverseInstance extends UniverseInstance {
	public UnlabeledUniverseInstance(int[] value) {
		super(value);
	}
	
	/**
	 * Get the attribute value by index.
	 * <p>
	 * <strong>NOTICE</strong>: Unlabeled Universe Instance contains attribute values only, so different from 
	 * {@link UniverseInstance}, all values returned in this method are attribute values.
	 * <p>
	 * Due to no decision value(label) in this Instance, index starts from 1(without the label).
	 * 
	 * @param index
	 * 		The index of the value. (starts from 1, no decision value)
	 * @return the int value./-1 if the index is illegal.
	 */
	@Override
	public int getAttributeValue(int index){
		try {	return getAttributeValues()[index-1];	}catch(IndexOutOfBoundsException e) {	return -1;	}
	}

	/**
	 * Get the condition attribute values.
	 * <p>
	 * <strong>NOTICE</strong>: Unlabeled Universe Instance contains attribute values only, so different from 
	 * {@link UniverseInstance}, this method returns {@link #getAttributeValues()}.
	 * 
	 * @return The condition attribute values.
	 */
	@Override
	public int[] getConditionAttributeValues() {
		return this.getAttributeValues();
	}

	/**
	 * Compare using attribute values only.
	 */
	@Override
	public int compareTo(UniverseInstance o) {
		int cmp;
		for (int i=1; i<=getAttributeValues().length; i++) {
			cmp = getAttributeValue(i) - o.getAttributeValue(i);
			if ( cmp!=0 )	return cmp;
		}
		return 0;
	}
}