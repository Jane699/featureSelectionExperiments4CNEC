package basic.procedure.parameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

/**
 * Procedure parameters manager. Using a {@link Map} to save parameters and {@link Set} to store 
 * <strong>ROOT</strong> parameter keys/names.
 * <p>
 * Parameters can be set to <strong>ROOT</strong> for re-use. For <strong>non-ROOT</strong> parameters, 
 * they can be deleted by calling {@link #deleteNonRootParameters()} while <strong>ROOT</strong> 
 * parameters stay.
 * 
 * @author Benjamin_L
 */
public class ProcedureParameters {
	@Getter private Map<String, ProcedureParameterValue<?>> parameters;
	@Getter private Set<String> rootParametersKey;
	
	public ProcedureParameters() {
		parameters = new HashMap<>();
		rootParametersKey = new HashSet<>();
	}
	
	/**
	 * Delete all <strong>non-ROOT</strong> parameters.
	 */
	public void deleteNonRootParameters() {
		Iterator<Map.Entry<String, ProcedureParameterValue<?>>> iterator = parameters.entrySet().iterator();
		while (iterator.hasNext()) {
			if (!rootParametersKey.contains(iterator.next().getKey()))	
				iterator.remove();
		}
	}
	
	/**
	 * Set a (ROOT or non-ROOT) parameter in key-value format.
	 * 
	 * @see {@link Map#put(Object, Object)}
	 * 
	 * @param <T> 
	 * 		The value type.
	 * @param root
	 * 		Set as ROOT parameter.
	 * @param key
	 * 		key with which the specified value is to be associated
	 * @param value
	 * 		value to be associated with the specified key
	 * @return <code>this</code> instance.
	 */
	@SuppressWarnings("unchecked")
	public <T> ProcedureParameters set(boolean root, String key, T value) {
		this.parameters.put(key, new ProcedureParameterValue<T>(value, (Class<T>) value.getClass()));
		if (root)	rootParametersKey.add(key);
		else		rootParametersKey.remove(key);
		return this;
	}
	
	/**
	 * Set a non-ROOT parameter in key-value format.
	 * 
	 * @see {@link #set(boolean, String, Object)}
	 * 
	 * @param <T> 
	 * 		The value type.
	 * @param key
	 * 		key with which the specified value is to be associated
	 * @param value
	 * 		value to be associated with the specified key
	 * @return <code>this</code> instance.
	 */
	public <T> ProcedureParameters setNonRootParameter(String key, T value) {
		return set(false, key, value);
	}
	
	/**
	 * Get a parameter value by key.
	 * 
	 * @see {@link Map#get(Object)}
	 * 
	 * @param <T> 
	 * 		The value type.
	 * @param key
	 * 		The key whose associated value is to be returned
	 * @return null if not such key in parameters. / parameter value in {@link T}.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		ProcedureParameterValue<?> paramValue = parameters.get(key);
		return paramValue==null? null: (T) paramValue.getValue();
	}
	
	/**
	 * Get the parameter value {@link Class}.
	 * 
	 * @param key
	 * 		The key whose associated value class is to be returned
	 * @return null if not such key in parameters. / parameter value class of {@link T}.
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> getValueClass(String key){
		ProcedureParameterValue<?> paramValue = parameters.get(key);
		return paramValue==null? null: (Class<T>) paramValue.getClassOfValue();
	}
	
	/**
	 * Get parameter keys.
	 * 
	 * @see {@link Map#keySet()}
	 * 
	 * @return A collection of {@link String} as parameter keys.
	 */
	public Collection<String> keys(){
		return parameters.keySet();
	}
}
