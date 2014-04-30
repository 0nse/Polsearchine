/*
 * Copyright 2013 Michael Ruster.
 *
 * This file is part of Polsearchine.
 *
 * Polsearchine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polsearchine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Polsearchine. If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Abstract class to implement the strategy pattern for prioritisation
 * algorithms.
 * Each realisation implements an {@code executeAlgorithm} method including the
 * algorithm logic for a specific unsorted group of {@code PrioritisedRule}s.
 * Then, {@code apply} will put them back again together to several prioritised
 * groups.
 *
 * @author mruster
 */
public abstract class PrioritisationAlgorithm {

	/**
	 * @param group of {@code PrioritisedRule}s, that should be sorted
	 *               (prioritised).
	 *
	 * @return rules split up into sorted (prioritised) groups.
	 */
	protected abstract List<List<PrioritisedRule>> executeAlgorithm(List<PrioritisedRule> group) throws URISyntaxException;

	/**
	 * @param groupedRules groups of already sorted
	 *                      {@code FlowControlRuleMethod}s.
	 *
	 * @return groups of finer (or equally) sorted {@code FlowControlRuleMethod}s.
	 * @throws RuntimeException if a rule could not be converted into an
	 *                           {@code URI}-object.
	 */
	public List<List<PrioritisedRule>> apply(List<List<PrioritisedRule>> groupedRules) {
		try {
			List<List<PrioritisedRule>> prioritisedRules = new LinkedList<>();
			for (List<PrioritisedRule> group : groupedRules) {
				List<List<PrioritisedRule>> prioritisedGroupedRules = executeAlgorithm(group);

				prioritisedRules.addAll(prioritisedGroupedRules);
			}
			return prioritisedRules;

		} catch (URISyntaxException ex) {
			throw new RuntimeException("A rule contained a region that was not a valid URI.");
		}
	}
}
