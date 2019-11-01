package featureSelection.repository.implement.support.streamlineStrategy.universeStreamline.positiveApproximationAccelerator.original;

import java.util.Collection;
import java.util.Iterator;

import featureSelection.repository.frame.support.streamlineStrategy.UniverseStreamline;
import featureSelection.repository.implement.model.algStrategyRepository.positiveApproximationAccelerator.EquivalentClass;

public class Streamline4PositiveApproximationAccelerator
	implements UniverseStreamline<Collection<EquivalentClass>, EquivalentClass, int[]> 
{
	/**
	 * Remove items that can be removed. Using {@link #removable(EquivalentClass)}.
	 * 
	 * @see {@link #removable(EquivalentClass)}.
	 * 
	 * @param in
	 * 		{@link EquivalentClass} {@link Collection}.
	 * @return number of removed {@link UniverseInstance} and {@link EquivalentClass}.
	 */
	@Override
	public int[] streamline(Collection<EquivalentClass> in) throws Exception {
		int remove = in.size(), removedUniverse = 0;
		EquivalentClass equ;
		Iterator<EquivalentClass> iterator = in.iterator();
		while(iterator.hasNext()) {
			// 18 if h.dec!='/'
			if (removable(equ=iterator.next())) {
				// 19 Delete h
				iterator.remove();
				removedUniverse += equ.getInstances().size();
			}
		}
		return new int[] {removedUniverse, remove - in.size()};
	}
	
	/**
	 * If item's decision value != -1 (i.e. item is positive region), then it is removable.
	 * 
	 * @return <code>true</code> if item can be removed.
	 */
	@Override
	public boolean removable(EquivalentClass item) {
		return item.getDecisionValue()!=-1;
	}
}
