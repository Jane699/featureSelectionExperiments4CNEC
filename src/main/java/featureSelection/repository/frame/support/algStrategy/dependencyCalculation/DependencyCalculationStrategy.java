package featureSelection.repository.frame.support.algStrategy.dependencyCalculation;

import featureSelection.repository.frame.support.algStrategy.AlgStrategy;

/**
 * An interface for implementations involve <strong>Dependency Calculation Algorithm</strong> proposed by 
 * Muhammad Summair Raza, Usman Qamar.
 * <p>
 * Check out papers:
 * <li>
 * <a href="https://www.sciencedirect.com/science/article/pii/S0020025516000785">
 * "An incremental dependency calculation technique for feature selection using rough sets"</a> 
 * </li>
 * <li>
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0031320318301432">
 * "A heuristic based dependency calculation technique for rough set theory"</a> 
 * </li>
 * <li>
 * <a href="https://www.sciencedirect.com/science/article/abs/pii/S0888613X17300178">
 * "Feature selection using rough set-based direct dependency calculation by avoiding the positive region"</a>
 * </li>
 * 
 * @author Benjamin_L
 */
public interface DependencyCalculationStrategy extends AlgStrategy {}