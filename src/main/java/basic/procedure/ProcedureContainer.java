package basic.procedure;

import java.util.List;

import basic.procedure.parameter.ProcedureParameters;

/**
 * An interface for Procedure Container
 * 
 * @author Benjamin_L
 *
 * @param <T>
 * 		Executions' final result type.
 */
public interface ProcedureContainer<T> extends Procedure {
	/**
	 * The Container ID.
	 * 
	 * @return an {@link int[]} value.
	 */
	int id();
	/**
	 * The short name of the Procedure Container.
	 * 
	 * @return The short name in {@link String}.
	 */
	String shortName();
	/**
	 * The description of the Procedure Container.
	 * 
	 * @return The description in {@link String}.
	 */
	String description();
	
	/**
	 * Parameters for procedure execution.
	 * 
	 * @return Parameters in {@link ProcedureParameters}
	 */
	ProcedureParameters getParameters();
	/**
	 * Get the {@link ProcedureComponent} list of this procedure container.
	 * 
	 * @return A {@link List} of {@link ProcedureComponent}.
	 */
	List<ProcedureComponent<?>> getComponents();
	
	/**
	 * Execute {@link ProcedureComponent}s ({@link #getComponents()}.
	 * 
	 * @return the result of execution.
	 * @throws Exception if exception occur when executing.
	 */
	T exec() throws Exception;
}