package basic.procedure.componentAction;

import basic.procedure.ProcedureComponent;

/**
 * An interface for execution.
 * <p>
 * Only 1 method in this interface, <code>lambda</code> is recommended:
 * <pre>
 * (component, parameters)->{
 * 	// do something ...
 * }
 * </pre>
 * 
 * @author Benjamin_L
 * 
 * @see {@link BeforeComponentExecutedAction}
 * @see {@link AfterComponentExecutedAction}
 * 
 * @param <T>
 * 		Type of {@link ComponentExecution} result.
 */
public interface ComponentExecution<T> {
	/**
	 * Execute.
	 * 
	 * @param component
	 * 		Current {@link ProcedureComponent} to be executed.
	 * @param parameters
	 * 		Parameters used in execution.
	 * @return Execution result in {@link T}.
	 * @throws Exception if exceptions occur when executing.
	 */
	T exec(ProcedureComponent<?> component, Object...parameters) throws Exception;
}
