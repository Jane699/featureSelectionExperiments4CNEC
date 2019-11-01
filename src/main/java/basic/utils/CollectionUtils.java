package basic.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils {
	/**
	 * Copy {@link HashMap}: 
	 * <pre>
	 * Map<Key, Value> copy = new HashMap<>();
	 * for (Map.Entry<Key, Value> entry: map.entrySet())
	 * 	copy.put(entry.getKey(), entry.getValue());
	 * </pre>
	 * 
	 * @param <Key>
	 * 		the type of keys maintained by this map
	 * @param <Value>
	 * 		 the type of mapped values
	 * @param map
	 * 		The map to be copied.
	 * @return {@link Map}.
	 */
	public static <Key, Value> Map<Key, Value> copyHashMap(Map<Key, Value> map){
		Map<Key, Value> copy = new HashMap<>();
		for (Map.Entry<Key, Value> entry: map.entrySet())	copy.put(entry.getKey(), entry.getValue());
		return copy;
	}
}
