package basic.procedure.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A entity for Procedure parameter value with value itself and value class.
 * 
 * @author Benjamin_L
 *
 * @param <T>
 * 		Type of procedure parameter value.
 */
@Data
@AllArgsConstructor
public class ProcedureParameterValue<T> {
	private T value;
	private Class<T> classOfValue;
	
	@Override
	public String toString() {
		return value + ", " + classOfValue.getName();
	}
}
