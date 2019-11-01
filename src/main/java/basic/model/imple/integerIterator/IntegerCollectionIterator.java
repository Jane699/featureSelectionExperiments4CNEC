package basic.model.imple.integerIterator;

import java.util.Collection;
import java.util.Iterator;

import basic.model.interf.IntegerIterator;

/**
 * Implemented {@link IntegerIterator} for integer values in a {@link Collection}.
 * <p>
 * <strong>PS</strong>: Call {@link reset()} before an iteration to reset and initiate the iterator.
 * 
 * @author Benjamin_L
 */
public class IntegerCollectionIterator 
	implements IntegerIterator 
{
	private Collection<Integer> collection;
	private Iterator<Integer> iterator;
	private int index;
	
	public IntegerCollectionIterator(Collection<Integer> collection) {
		this.collection = collection;
		index = 0;
	}
	
	public IntegerCollectionIterator reset() {
		iterator = collection.iterator();
		index = 0;	
		return this;
	}

	@Override
	public boolean skip(int length) {
		if (index+length>=collection.size()) {
			return false;
		}else {
			index += length;
			for (int i=0; i<length; i++)	iterator.next();
			return true;
		}
	}
	
	@Override
	public int next() {
		index++;
		return iterator.next();
	}

	@Override
	public int size() {
		return collection.size();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public int currentIndex() {
		return index;
	}

	@Override
	public IntegerCollectionIterator clone() {
		return new IntegerCollectionIterator(collection);
	}
	
	@Override
	public String toString() {
		return "IntegerCollectionIterator " + collection;
	}
}