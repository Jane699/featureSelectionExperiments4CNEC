package basic.model;

import java.util.Arrays;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A wrapped entity for {@link int[]} as key in {@link java.util.Map}. The following methods are overrided
 * and is of importance:
 * <li>{@link IntArrayKey#equals(Object)}</li>
 * <li>{@link IntArrayKey#hashCode()}</li>
 * <p>
 * When constructing, {@link IntArrayKey#IntArrayKey(int...)} is recommended.
 * <p>
 * Call {@link IntArrayKey#key()} to get the {@link int[]}.

 * @author Benjamin_L
 */
@NoArgsConstructor
public class IntArrayKey {
	@Setter @Getter private int[] key;
	
	public IntArrayKey(int...key) {
		this.key = key;
	}

	public int[] key() {
		return key;
	}
	
	/**
	 * Indicates whether the given {@link Object} <code>k</code> is "equal to" <code>this</code> one:
	 * <p>
	 * <li><code>k</code> is {@link int[]} or {@link IntArrayKey}</li>
	 * <li>{@link Arrays#equals(int[], int[])} returns true</li>
	 * 
	 * @see {@link Arrays#equals(int[], int[])}
	 * 
	 * @return true if equal, false if not.
	 */
	@Override
	public boolean equals(Object k) {
		if (k instanceof int[])				return Arrays.equals((int[])k, key);
		else if (k instanceof IntArrayKey)	return Arrays.equals(((IntArrayKey)k).key(), key);
		else 								return false;
	}
	
	/**
	 * The hashcode of current instance. Set based on instance field {@link #key}:
	 * <pre>return Arrays.hashCode(key);</pre>
	 * 
	 * @see {@link Arrays#hashCode(int[])}
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(key);
	}
	
	public String toString() {
		return Arrays.toString(key);
	}
}
