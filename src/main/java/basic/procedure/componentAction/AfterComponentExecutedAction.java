package basic.procedure.componentAction;

import basic.procedure.ProcedureComponent;

/**
 * An interface for actions <strong>after</strong> the execution of {@link ComponentExecution}.
 * <p>
 * Only 1 method in this interface, <code>lambda</code> is recommended:
 * <pre>
 * (component, result)->{
 * 	// do something ...
 * }
 * </pre>
 * 
 * @author Benjamin_L
 * 
 * @see {@link BeforeComponentExecutedAction}
 * @see {@link ComponentExecution}
 * 
 * @param <T>
 * 		Type of {@link ComponentExecution} result.
 */
public interface AfterComponentExecutedAction<T> {
	/**
	 * Execute after {@link ComponentExecution} has been executed.
	 * 
	 * @param component
	 * 		Current {@link ProcedureComponent} to be executed.
	 * @param result
	 * 		Result of {@link ComponentExecution}.
	 * @throws Exception if exceptions occur when executing.
	 */
	void exec(ProcedureComponent<?> component, T result) throws Exception;
}
