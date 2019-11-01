package tester.frame.procedure.component;

import basic.procedure.ProcedureComponent;
import basic.procedure.componentAction.AfterComponentExecutedAction;
import basic.procedure.componentAction.BeforeComponentExecutedAction;
import basic.procedure.componentAction.ComponentExecution;
import basic.procedure.parameter.ProcedureParameters;
import basic.procedure.timer.TimeCountStatus;
import basic.procedure.timer.TimeCounted;
import lombok.Getter;
import lombok.Setter;
import tester.utils.TimerUtils;

/**
 * An abstract class of <strong>Time Counted</strong> {@link ProcedureComponent}, using 
 * <code>timeStatus</code>, <code>time</code>, <code>start</code> to record time.
 * <p>
 * <li><code>timeStatus</code>: {@link TimeCountStatus}. Mark the current counting status</li> 
 * <li><code>time</code>: {@link long}. Record time counted in nano.</li> 
 * <li><code>start</code>: {@link long}. Record time started in nano.</li>
 * <p>
 * Please use {@link TimerUtils} to control time counting manually.
 * 
 * @see {@link TimerUtils}
 * @see {@link ProcedureComponent}
 * @see {@link TimeCounted}
 * 
 * @author Benjamin_L
 *
 * @param <T>
 * 		Procedure component final execution return type.
 */
public abstract class TimeCountedProcedureComponent<T> 
	extends ProcedureComponent<T> 
	implements TimeCounted 
{
	@Setter @Getter private TimeCountStatus timeStatus;
	@Setter @Getter private long time;
	@Setter @Getter private long start;
	private boolean timerStartOnExecution;
	
	/**
	 * Construct {@link TimeCountedProcedureComponent}. 
	 * <p>
	 * {@link #timerStartOnExecution} is set to true, meaning count time as soon as {@link ComponentExecution}
	 * starts to execute automatically.
	 * 
	 * @see {@link #TimeCountedProcedureComponent(String, ProcedureParameters, BeforeComponentExecutedAction,
	 * 				boolean, ComponentExecution, AfterComponentExecutedAction)}
	 * 
	 * @param tag
	 * 		The tag of the {@link ProcedureComponent}.
	 * @param paramaters
	 * 		Parameters in {@link ProcedureComponent}.
	 * @param beforeAction
	 * 		Actions before the execution.
	 * @param execution
	 * 		Execution.
	 * @param afterAction
	 * 		Actions after the execution.
	 */
	public TimeCountedProcedureComponent(String tag, ProcedureParameters paramaters,
										BeforeComponentExecutedAction beforeAction, 
										ComponentExecution<T> execution,
										AfterComponentExecutedAction<T> afterAction
	) {
		this(tag, paramaters, beforeAction, true, execution, afterAction);
	}
	/**
	 * Construct {@link TimeCountedProcedureComponent}. 
	 * <p>
	 * {@link #timerStartOnExecution} is set to true, meaning counting time as soon as 
	 * {@link ComponentExecution} starts to execute automatically.
	 * 
	 * @see {@link #TimeCountedProcedureComponent(String, ProcedureParameters, BeforeComponentExecutedAction,
	 * 				ComponentExecution, AfterComponentExecutedAction)}
	 * 
	 * @param tag
	 * 		The tag of the {@link ProcedureComponent}.
	 * @param paramaters
	 * 		Parameters in {@link ProcedureComponent}.
	 * @param beforeAction
	 * 		Actions before the execution.
	 * @param timerStartOnExecution
	 * 		Whether to count time as soon as {@link ComponentExecution} start to execute automatically.
	 * @param execution
	 * 		Execution.
	 * @param afterAction
	 * 		Actions after the execution.
	 */
	public TimeCountedProcedureComponent(String tag, ProcedureParameters paramaters,
										BeforeComponentExecutedAction beforeAction, 
										boolean timerStartOnExecution, 
										ComponentExecution<T> execution,
										AfterComponentExecutedAction<T> afterAction
	) {
		super(tag, paramaters, beforeAction, execution, afterAction);
		time = 0;
		timeStatus = TimeCountStatus.OFF;
		this.timerStartOnExecution = timerStartOnExecution;
	}
	
	@Override
	public T exec() throws Exception {
		if (getBeforeAction()!=null)	getBeforeAction().exec(this);
		
		if (timerStartOnExecution)	TimerUtils.timeStart(this);
		T result = getExecution().exec(this, getLocalParameters());
		TimerUtils.timeStop(this);
		
		if (getAfterAction()!=null)		getAfterAction().exec(this, result);
		return result;
	}
}