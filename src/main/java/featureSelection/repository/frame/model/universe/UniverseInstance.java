package featureSelection.repository.frame.model.universe;

import java.util.Arrays;

import basic.model.interf.IntegerIterator;
import lombok.Getter;

/**
 * A model for an Universe Instance, represents a line of data(an instance) in a Decision Table.
 * 
 * @author Benjamin_L
 */
public class UniverseInstance implements Comparable<UniverseInstance>{
	private int[] attributeValues;
		
	@Getter private static int numCounter = 1;
	private final int num = numCounter++;
	
	public UniverseInstance(int[] value){
		if (value!=null)	setAttributeValue(value);
	}
	
	/**
	 * Reset <code>numCounter</code>(ID) into 1.
	 */
	public static void resetID() {
		numCounter = 1;
	}
	
	/**
	 * Set <code>numCounter</code> into the given id.
	 * 
	 * @param id
	 * 		The ID number to be set to.
	 */
	public static void setID(int id) {
		numCounter = id;
	}
	
	/**
	 * Get the index number of this Universe
	 * 
	 * @return an int value
	 */
	public int getNum(){
		return num;
	}
		
	/**
	 * Get how many value the universe contains
	 * 
	 * @return an int value
	 */
	public int getValueLength(){
		return attributeValues.length;
	}

	/**
	 * Set the instance's attribute values. 
	 * 
	 * @param value 
	 * 		Values to be set in order.(0 as decision value, condition attributes start from 1)
	 * @return  <code>true</code> if set attribute values successfully.
	 */
	public boolean setAttributeValue(int...value){
		attributeValues = value;
		return true;
	}
	
	/**
	 * Get the attribute value by index.
	 * 
	 * @param index
	 * 		The index of the value. (starts from 1, 0 as decision attribute)
	 * @return the int value./-1 if the index is illegal.
	 */
	public int getAttributeValue(int index){
		try {	return attributeValues[index];	}catch(IndexOutOfBoundsException e) {	return -1;	}
	}

	/**
	 * Get attribute values.
	 * 
	 * @return an array of Integer values.
	 */
	public int[] getAttributeValues(){
		return attributeValues;
	}
	
	/**
	 * Get the condition attribute values.
	 * 
	 * @return The condition attribute values.
	 */
	public int[] getConditionAttributeValues() {
		return Arrays.copyOfRange(attributeValues, 1, attributeValues.length);
	}

	public String toString(){
		if(attributeValues==null)	return "Instance-"+num;
	
		StringBuilder builder = new StringBuilder();
		builder.append("Instance-"+num+"	");
		for (int i=1; i<attributeValues.length; i++) {
			builder.append(attributeValues[i]);
			if (i!=attributeValues.length-1)	builder.append(", ");
		}
		builder.append("	"+"d = "+attributeValues[0]);
		return builder.toString();
	}

	@Override
	public int compareTo(UniverseInstance o) {
		int cmp;
		for ( int i=1; i<attributeValues.length; i++ ) {
			cmp = this.getAttributeValue(i) - o.getAttributeValue(i);
			if ( cmp!=0 )	return cmp;
		}
		return this.getAttributeValue(0) - o.getAttributeValue(0);
	}
	
	public int compareTo(UniverseInstance o, IntegerIterator attributes) {
		int cmp, attr;
		attributes.reset();
		for (int i=0; i<attributes.size(); i++) {
			attr = attributes.next();
			cmp = this.getAttributeValue(attr) - o.getAttributeValue(attr);
			if ( cmp!=0 )	return cmp;
		}
		return 0;
	}
}