package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.discernibilityView;

import java.util.Collection;
import java.util.Iterator;

import featureSelection.repository.frame.model.universe.UniverseInstance;
import featureSelection.repository.frame.support.streamlineStrategy.UniverseStreamline;
import featureSelection.repository.implement.algroithm.algStrategyRepository.discernibilityView.TengDiscernibilityViewAlgorithm;

public class Streamline4TengDiscernibilityView
	implements UniverseStreamline<StreamlineInput4TengDiscernibilityView, StreamlineRemovableCriteria4TengDiscernibilityView, Integer> 
{

	@Override
	public Integer streamline(StreamlineInput4TengDiscernibilityView in) throws Exception {
		int removeCount = 0;
		// Loop over M[r] in U[j]/red.
		Collection<UniverseInstance> equClass;
		Iterator<Collection<UniverseInstance>> iterator = in.getEquClasses2BRemoved().iterator();
		while (iterator.hasNext()) {
			equClass = iterator.next();
			if (removable(
					new StreamlineRemovableCriteria4TengDiscernibilityView(
						in.getGlobalEquClasses(), in.getDecEquClasses(), equClass
					)
				)
			) {
				iterator.remove();
				removeCount+=equClass.size();
			}
		}
		return removeCount;
	}

	@Override
	public boolean removable(StreamlineRemovableCriteria4TengDiscernibilityView item) {
		// if  M[r] is a sub-element of U[j]/D
		// 	or M[r] is a sub-element of U[j]/C
		// then U' = U' ∪ M[r], where U' contains universes removable.
		return TengDiscernibilityViewAlgorithm
					.isSubEquivalentClassOf(
						// U[j]/D
						item.getDecEquClasses(),
						// M[r]
						item.getEquClass2BRemoved()
					) ||
				TengDiscernibilityViewAlgorithm
					.isSubEquivalentClassOf(
						// U[j]/C
						item.getGlobalEquClasses(), 
						// M[r]
						item.getEquClass2BRemoved()
					);
	}
}
