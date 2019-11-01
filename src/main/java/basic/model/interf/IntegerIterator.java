package basic.model.interf;

/**
 * Iterator for Integer values.
 * <p>
 * <strong>PS</strong>: The most secure way to execute the iterator is to <code>reset()</code> before an 
 * iteration.
 * 
 * @author Benjamin_L
 */
public interface IntegerIterator extends Cloneable {
	IntegerIterator reset();
	boolean skip(int length);
	
	/**
	 * Get the next int element.
	 * 
	 * @return int value.
	 */
	int next();
	
	/**
	 * Size of integer elements in total.
	 * 
	 * @return an int value as the size.
	 */
	int size();
	
	/**
	 * Get if has more element.
	 * 
	 * @return <code>true</code> if it has. / <code>false</ccode> if not.
	 */
	boolean hasNext();
	/**
	 * Current index of int elements. (Starts from 0)
	 * 
	 * @return an int value as index.
	 */
	int currentIndex();
	
	IntegerIterator clone();
}