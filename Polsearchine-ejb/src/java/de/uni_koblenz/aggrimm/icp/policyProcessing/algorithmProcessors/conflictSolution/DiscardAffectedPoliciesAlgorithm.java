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
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.conflictSolution;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Affected policies are being deleted from all groups.
 *
 * @author mruster
 */
public class DiscardAffectedPoliciesAlgorithm extends ConflictSolutionAlgorithm {

	/**
	 *
	 * @param groupedRules       prioritised rules with at least one conflict.
	 * @param affectedGroupIndex this variable has no importance for this
	 *                            algorithm.
	 * @param conflictingRules   rules that were conflicting within a prioritised
	 *                            group.
	 *
	 * @return rules with all rules from affected policies being removed.
	 */
	@Override
	protected List<List<PrioritisedRule>> executeAlgorithm(List<List<PrioritisedRule>> groupedRules, int affectedGroupIndex, List<PrioritisedRule> conflictingRules) {
		List<List<PrioritisedRule>> conflictlessGroupedRules = new LinkedList<>();

		for (List<PrioritisedRule> ruleGroup : groupedRules) {
			for (PrioritisedRule conflictingRule : conflictingRules) {
				ruleGroup.remove(conflictingRule);
			}

			if (!ruleGroup.isEmpty()) {
				conflictlessGroupedRules.add(ruleGroup);
			}
		}

		return conflictlessGroupedRules;
	}
}
