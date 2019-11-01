package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.discernibilityView;

import java.util.Collection;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamlineInput4TengDiscernibilityView {

	private Collection<Collection<UniverseInstance>> globalEquClasses;
	private Collection<Collection<UniverseInstance>> decEquClasses;

	private Collection<Collection<UniverseInstance>> equClasses2BRemoved;
}
