package basic.procedure.componentAction;

import basic.procedure.ProcedureComponent;

/**
 * An interface for actions <strong>before</strong> the execution of {@link ComponentExecution}.
 * <p>
 * Only 1 method in this interface, <code>lambda</code> is recommended:
 * <pre>
 * (component)->{
 * 	// do something ...
 * }
 * </pre>
 * 
 * @author Benjamin_L
 * 
 * @see {@link ComponentExecution}
 * @see {@link AfterComponentExecutedAction}
 * 
 * @param <T>
 * 		Type of {@link ComponentExecution} result.
 */
public interface BeforeComponentExecutedAction {
	/**
	 * Execute before {@link ComponentExecution} has been executed.
	 * 
	 * @param component
	 * 		Current {@link ProcedureComponent} to be executed.
	 * @throws Exception if exceptions occur when executing.
	 */
	void exec(ProcedureComponent<?> component) throws Exception;
}
