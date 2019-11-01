package tester.frame.procedure.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import basic.procedure.ProcedureComponent;
import basic.procedure.parameter.ProcedureParameters;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RelevantFeatureSelectProcedureContainer} with <strong>selective</strong> and <strong>replacable</strong> 
 * {@link ProcedureComponent}s.
 * <p>
 * {@link #setComponent} to replace a particular {@link ProcedureComponent} by key.
 * 
 * @see {@link DefaultProcedureContainer}
 * 
 * @author Benjamin_L
 *
 * @param <T>
 * 		Type of execution return result.
 */
@Slf4j
public abstract class SelectiveComponentsProcedureContainer<T> 
	extends DefaultProcedureContainer<T> 
{
	@Getter private Map<String, ProcedureComponent<?>> componentMap;
	
	public SelectiveComponentsProcedureContainer(boolean logOn, ProcedureParameters paramaters) {
		super(logOn? log: null, paramaters);
		componentMap = Collections.synchronizedMap(new HashMap<>());
		
		initDefaultComponents(logOn);
	}

	/**
	 * Initiate components to be executed.
	 */
	public ProcedureComponent<?>[] initComponents(){
		ProcedureComponent<?> component;
		String[] componentKeys = componentsExecOrder();
		ProcedureComponent<?>[] componentArray = new ProcedureComponent[componentKeys.length];
		for (int i=0; i<componentKeys.length; i++) {
			component = componentMap.get(componentKeys[i]);
			if (component==null) {
				throw new RuntimeException("No such component named '"+componentKeys[i]+"' is set.");
			}else {
				componentArray[i] = component;
			}
		}
		return componentArray;
	}

	public abstract void initDefaultComponents(boolean logOn);
	public abstract String[] componentsExecOrder();
	
	public boolean containsComponent(String key) {
		return componentMap.containsKey(key);
	}
	
	public void setComponent(String key, ProcedureComponent<?> component) {
		componentMap.put(key, component);
	}
	
	public ProcedureComponent<?> getComponent(String key){
		return componentMap.get(key);
	}
}