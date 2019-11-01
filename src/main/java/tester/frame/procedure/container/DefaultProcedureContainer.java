package tester.frame.procedure.container;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import basic.procedure.ProcedureComponent;
import basic.procedure.ProcedureContainer;
import basic.procedure.parameter.ProcedureParameterValue;
import basic.procedure.parameter.ProcedureParameters;
import basic.utils.LoggerUtil;
import basic.utils.StringUtils;

/**
 * An abstract class for Default {@link ProcedureContainer}. In this container, {@link #shortName()} 
 * is used as description also.
 * <p>
 * Specially, this container can notify {@link #progressUpdatee} as its {@link #progress} changes. In
 * order to do so, {@link #progressUpdatee} need to be set before {@link #exec()} by calling 
 * {@link #setProgressUpdatee(Object)}.
 * <pre>
 * Object anObject = ...
 * ...
 * 
 * DefaultProcedureContainer container = new DefaultProcedureContainer(parameters);
 * ...
 * container.setProgressUpdatee(anObject);
 * ...
 * T result = container.exec();
 * // Then container executes ...
 * // Container does something ...
 * // Container updates progress ...
 * // (anObject.notify();)	// once {@link #progress} changes, anObject.notify() will be call by the container.
 * // Container does something ...
 * <pre>
 * 
 * @author Benjamin_L
 *
 * @param <T>
 * 		Type of Procedure Container execution result.
 */
public abstract class DefaultProcedureContainer<T> 
	implements ProcedureContainer<T>
{
	public static int idCounter = 0;
	public int id = ++idCounter;
	
	private Logger log;
	private ProcedureParameters parameters;
	private List<ProcedureComponent<?>> components;
	
	public DefaultProcedureContainer(ProcedureParameters paramaters) {
		this.parameters = paramaters;
		components = new LinkedList<>();
	}
	public DefaultProcedureContainer(Logger log, ProcedureParameters parameters) {
		this.log = log;
		this.parameters = parameters;
		components = new LinkedList<>();
	}

	public int id() {
		return id;
	}
	
	@Override
	public String description() {
		return shortName();
	}

	@Override
	public ProcedureParameters getParameters() {
		return parameters;
	}

	@Override
	public List<ProcedureComponent<?>> getComponents() {
		return components;
	}
	
	public abstract ProcedureComponent<?>[] initComponents();

	@SuppressWarnings("unchecked")
	@Override
	public T exec() throws Exception {
		parametersDisplay(log);
		
		for (ProcedureComponent<?> each : initComponents())	this.components.add(each);
		Object result = null;
		for (ProcedureComponent<?> component: components) {
			result = component.exec();
		}
		
		return (T) result;
	}
	
	private void parametersDisplay(Logger log) throws IllegalArgumentException, IllegalAccessException {
		if (log!=null) {
			LoggerUtil.printLine(log, "=", 70);
			log.info("Parameters of 【"+this.description()+"】 : ");
			LoggerUtil.printLine(log, "-", 70);
			for (Entry<String, ProcedureParameterValue<?>> entry: this.getParameters().getParameters().entrySet()) {
				parametersDisplayOne(entry.getKey(), entry.getValue().getValue(),
									entry.getValue().getClassOfValue()
				);
			}
			LoggerUtil.printLine(log, "=", 70);
		}
	}
	
	private void parametersDisplayOne(String key, Object value, Class<?> valueClass) {
		if (value instanceof Collection) {
			log.info("	|{}| = {}, {}", key, 
										((Collection<?>) value).size(), 
										valueClass.getSimpleName()
					);
		}else if (value instanceof Map) {
			log.info("	|{}| = {}, {}", key, 
										((Map<?,?>) value).size(), 
										valueClass.getSimpleName()
					);
		}else if (value instanceof int[]) {
			log.info("	|{}| = {}, {}", key, 
										((int[]) value).length, 
										"int[]"
					);
		}else  {
			log.info("	{} = {}", key, value==null? null: StringUtils.shortString(value.toString(), 95));
		}
	}
}