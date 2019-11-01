package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.ClassSetType;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.ClassSet;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An Class for rough equivalence class items, represents a line of an overall rough equivalence class table 
 * which can be considered as a rough equivalence class set, whose item is the Class EquivalenceClassItem.
 * 
 * @author Benjamin_L
 */
public class RoughEquivalentClass<EquClass extends EquivalentClass> implements ClassSet<EquClass> {
	protected Collection<EquClass> equItem;
	protected ClassSetType type;
	protected int decision, universeSize;
		
	public RoughEquivalentClass() {
		equItem = new LinkedList<>();
	}
	
	/**
	 * Add an EquivalenceClassItem instance.
	 * <p> 
	 * If it is the 1st element, then set the key value, the values of the sub attributes, the decision 
	 * value.
	 * 
	 * @param item
	 * 		The instance to be added with
	 * @return true if added successfully./false if the instance already exists
	 */
	@Override
	public void addClassItem(EquClass item) {
		if (!equItem.isEmpty()) {
			if (ClassSetType.POSITIVE.equals(type) && item.sortable() && decision==item.getDecisionValue()){
				// do nothing
			}else if (ClassSetType.NEGATIVE.equals(type) && !item.sortable()) {
				// do nothing
			}else if (!ClassSetType.BOUNDARY.equals(type)){
				type = ClassSetType.BOUNDARY;	//E.cons = 0
				decision = -1;					//E.dec = -1
			}
		}else {
			type = item.getType();
			decision = item.getDecisionValue();
		}
		equItem.add(item);
		universeSize += item.getItemSize();//*/
	}

	/**
	 * Return the type of the equivalence class items
	 * 
	 * <P>ClassSetType.POSITIVE = 1,
	 * <P>ClassSetType.BOUNDARY = 0,
	 * <P>ClassSetType.NEGATIVE = -1;
	 * 
	 * @return -1 , 0, 1
	 */
	public ClassSetType getType() {
		return type;
	}
	
	/**
	 * Return the decision value
	 * 
	 * @return an int value of the decision value
	 */
	public int getDecisionValue() {
		return decision;
	}
	
	/**
	 * Get the EquivalenceClassItem set
	 * 
	 * @return a set of EquivalenceClassItem instances
	 */
	public Collection<EquClass> getItemSet() {
		return equItem;
	}
	
	/**
	 * Get the EquivalenceClassItem set's size
	 * 
	 * @return the size of the EquivalenceClassItem set
	 */
	public int getItemSize() {
		return equItem.size();
	}
	
	/**
	 * Get the size of universe instances
	 * 
	 * @return An int value
	 */
	public int getUniverseSize() {
		return universeSize;
	}
}