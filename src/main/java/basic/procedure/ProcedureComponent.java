package basic.procedure;

import java.util.HashMap;
import java.util.Map;

import basic.procedure.componentAction.AfterComponentExecutedAction;
import basic.procedure.componentAction.BeforeComponentExecutedAction;
import basic.procedure.componentAction.ComponentExecution;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.statistics.StatisticsCalculated;
import lombok.Getter;
import lombok.Setter;

/**
 * Implement {@link Procedure} for procedure components.
 * 
 * @author Benjamin_L
 *
 * @param <T>
 * 		Execution result type.
 */
@Getter
public abstract class ProcedureComponent<T> implements Procedure, StatisticsCalculated {
	private String tag;
	private String description;
	
	private ProcedureParameters parameters;
	private Map<String, ProcedureContainer<?>> subProcedureContainers;

	private Map<String, Object> statistics;
	
	private BeforeComponentExecutedAction beforeAction;
	private AfterComponentExecutedAction<T> afterAction;
	private ComponentExecution<T> execution;
	
	@Setter private Object[] localParameters;
	
	public ProcedureComponent(String tag, ProcedureParameters parameters, 
							BeforeComponentExecutedAction beforeAction, 
							ComponentExecution<T> execution,
							AfterComponentExecutedAction<T> afterAction
	) {
		this.tag = tag;
		this.parameters = parameters;
		this.beforeAction = beforeAction;
		this.afterAction = afterAction;
		this.execution = execution;
		
		subProcedureContainers = new HashMap<>();
		statistics = new HashMap<>();
		init();
	}
	
	public ProcedureComponent<T> setDescription(String desc){
		this.description = desc;
		return this;
	}
	
	public <V> V getParameterValue(String key) {
		return parameters.get(key);
	}
	
	public boolean containsStatistics(String key) {
		return this.statistics.containsKey(key);
	}
	
	public ProcedureComponent<T> setStatistics(String key, Object value){
		this.statistics.put(key, value);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getStatisticsValue(String key) {
		return (V) this.statistics.get(key);
	}
	
	/**
	 * Initiate the Procedure Component.
	 */
	public abstract void init();
	
	/**
	 * Execute the Procedure Component: 
	 * <li>Execute {@link #beforeAction} if it is not null.</li>
	 * <li>Execute {@link #execution}.</li>
	 * <li>Execute {@link #afterAction} if it is not null.</li>
	 * 
	 * @return execution result in {@link T}.
	 * @throws Exception if exceptions occur when executing.
	 */
	public T exec() throws Exception {
		if (beforeAction!=null)	beforeAction.exec(this);
		T result = execution.exec(this, localParameters);
		if (afterAction!=null)	afterAction.exec(this, result);
		return result;
	}

	public ProcedureComponent<T> setSubProcedureContainer(String key, ProcedureContainer<?> container) {
		subProcedureContainers.put(key, container);
		return this;
	}
}