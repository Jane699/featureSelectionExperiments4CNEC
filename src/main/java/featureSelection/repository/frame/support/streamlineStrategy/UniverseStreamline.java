package featureSelection.repository.frame.support.streamlineStrategy;

/**
 * An interface for {@link UniverseInstance} deleting/removing action.
 * 
 * @author Benjamin_L
 *
 * @param <Input>
 * 		Type of Input.
 * @param <InputItem>
 * 		Type of Input Item. Used to check if item is removable.
 * @param <Output>
 * 		Type of Output.
 */
public interface UniverseStreamline<Input, InputItem, Output> {
	Output streamline(Input in) throws Exception;
	boolean removable(InputItem item);
}