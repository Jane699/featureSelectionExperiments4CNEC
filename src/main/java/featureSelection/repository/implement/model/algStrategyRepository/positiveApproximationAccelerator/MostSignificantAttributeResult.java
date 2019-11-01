package featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MostSignificantAttributeResult<Sig> {
	private int attribute;
	private Sig maxSig;
	private Collection<EquivalentClass> equClasses;
}
