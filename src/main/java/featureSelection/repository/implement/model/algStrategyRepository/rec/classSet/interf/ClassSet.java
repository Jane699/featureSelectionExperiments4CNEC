package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf;

import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.ClassSetType;

/**
 * An interface for class item.The interface itself represents a line of an overall table which can be considered 
 * as a set.
 * 
 * @author Benjamin_L
 *
 * @param <Item> can be the class of Universe, which represents a record of a Universe, or can be the class 
 * 		EquivalenceClassItem, which represents a table set of Equivalence class items. 
 */
public interface ClassSet<Item> {		
	/**
	 * Add an Item instance.
	 * 
	 * @param item
	 * 		The instance to be added
	 */
	public void addClassItem(Item item);
		
	/**
	 * Return the type of the items
	 * 
	 * @return the type in int
	 */
	public ClassSetType getType();
	
	/**
	 * Get the number of the items
	 * 
	 * @return the size of the items in int
	 */
	public int getItemSize();
	
	/**
	 * Get the number of the universe instances.
	 * 
	 * @return An int value
	 */
	public int getUniverseSize();
}