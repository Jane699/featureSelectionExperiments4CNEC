package basic.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {
	
	/**
	 * Combinatorial number of 2.
	 * <p> 2的组合值 : C (suffix, 2).
	 * 
	 * @param suffix
	 * 		The parameter down.
	 * @return A double value.
	 */
	public static double combinatorialNumOf2(int suffix) {
		if (suffix<0) {	throw new RuntimeException("Illegal parameter : "+suffix);	}
		return suffix==0 ? 0 : suffix * (suffix-1.0) / 2.0;
	}
	
}
