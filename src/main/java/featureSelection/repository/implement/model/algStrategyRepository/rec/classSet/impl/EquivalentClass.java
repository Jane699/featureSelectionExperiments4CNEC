package featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.impl;

import basic.model.IntArrayKey;
import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.ClassSetType;
import featureSelection.repository.implement.model.algStrategyRepository.rec.classSet.interf.ClassSet;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Class for saving Equivalence Class items, all universes in an Equivalent Class are considered 
 * equivalent based on <code>attrValue</code> for no further partition can be made based on 
 * <code>attrValue</code>.
 * 
 * @see {@link UniverseInstance}
 * @see {@link ClassSet}
 * 
 * @author Benjamin_L
 */
@Data
@NoArgsConstructor
public class EquivalentClass implements ClassSet<UniverseInstance>, Cloneable {	
	protected int[] attrValue;
	protected Integer decValue;
	protected int universeCount;
	
	public EquivalentClass(UniverseInstance universe) {
		decValue = universe.getAttributeValue(0);	// dec
		attrValue = universe.getConditionAttributeValues();
		universeCount = 1;
	}
	
	/**
	 * Set un-sortable, meaning multiple decision values in {@link ClassSet}.
	 */
	public EquivalentClass setUnsortable() {
		if (sortable())	decValue = null;
		return this;
	}
	
	/**
	 * Get whether sortable
	 * 
	 * @return true if sortable
	 */
	public boolean sortable() {
		return decValue!=null;
	}
	
	/**
	 * Return the sorting ability of the Universe
	 * 
	 * @return the sorting ability in int : 0 - true , 1 - false
	 */
	@Override
	public ClassSetType getType() {
		return sortable()? ClassSetType.POSITIVE : ClassSetType.NEGATIVE ;
	}

	/**
	 * Get the attribute value by index
	 * 
	 * @param index
	 * 		The index of the attribute.(attribute starts from 0)
	 * @return an int value
	 */
	public int getAttributeValueAt(int index) {
		return attrValue[index];
	}

	/**
	 * Get the attribute's values.
	 * 
	 * @return An int array of the attribute's values
	 */
	public int[] getAttributeValues() {
		return attrValue;
	}

	/**
	 * Get the decision value.
	 * 
	 * @return -1 if not sortable./ the decision value if sortable.
	 */
	public int getDecisionValue() {
		return sortable()? decValue: -1;
	}
	
	/**
	 * Add an universe instance.
	 * 
	 * @param ins
	 * 		The universe instance to be added.
	 */
	public void addClassItem(UniverseInstance ins) {
		universeCount++;
	}
	
	/**
	 * Merge the given <code>equClass</code> with <code>this</code>: 
	 * <pre>
	 * if (universeCount==0) {
	 * 	attrValue = equClass.attrValue;
	 * 	this.decValue = equClass.decValue;
	 * }else {
	 * 	if (sortable()) {
	 * 		if (this.decValue!=equClass.decValue) {
	 * 			decValue = null;
	 * 			return true;
	 * 		}
	 * 	}
	 * }
	 * universeCount+=equClass.universeCount;
	 * return false;
	 * </pre>
	 * 
	 * @param equClass
	 * 		{@link EquivalentClass} to be merged.
	 * @return True if <code>this.decValue</code> change into <code>null</code>(i.e. cnst=false)
	 */
	public boolean mergeClassItemsAndClassSetTypeHasChanged(EquivalentClass equClass) {
		boolean changed = false;
		if (universeCount==0) {
			attrValue = equClass.attrValue;
			this.decValue = equClass.decValue;
		}else {
			if (sortable()) {
				if (this.decValue!=equClass.decValue) {
					decValue = null;
					if (!changed)	changed = true;
				}
			}
		}
		universeCount+=equClass.universeCount;
		return changed;
	}
	
	/**
	 * Get the universe set's size
	 * 
	 * @return the size of the universe set
	 */
	public int getItemSize() {
		return universeCount;
	}
		
	@Override
	public int getUniverseSize() {
		return getItemSize();
	}
	
	public String toString() {
		return String.format("(Ux%d) %s %s d=%d", universeCount, getType(), new IntArrayKey(attrValue), getDecisionValue());
	}

	/**
	 * Create a new {@link EquivalentClass} instance as the clone and fields are set based on the 
	 * original one.
	 */
	@Override
	public EquivalentClass clone() {
		EquivalentClass clone = new EquivalentClass();
		clone.setAttrValue(attrValue);
		clone.setDecValue(decValue);
		clone.setUniverseCount(universeCount);
		return clone;
	}
}