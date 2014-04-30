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
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.logic;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This class contains methods that might be reused by different algorithms.
 *
 * @author mruster
 */
public final class SharedPrioritisationMethods {

	/**
	 * <p>This method groups {@code ruleGroups} by adjacent policies.
	 * This method works on adjacent groups which have different or similar
	 * policies. Say there is set A with policy A, set B with policy A in this
	 * order. Then there will be two {@code Set}s although
	 * there is only one policy. This algorithm merges these two.
	 * <p>The method overwrites the {@code currentPriority}-values so that the
	 * earlier structure can be restored.
	 *
	 * <p>The result will be {@code PrioritisedRule} grouped by their policy.
	 */
	public static List<List<PrioritisedRule>> groupRulesByAdjacentPolicy(List<List<PrioritisedRule>> ruleGroups) {
		List<List<PrioritisedRule>> policyRuleGroups = new LinkedList<>();
		String lastUri = "";
		List<PrioritisedRule> currentPolicyRules = new LinkedList<>();
		int priority = ruleGroups.size();
		for (List<PrioritisedRule> group : ruleGroups) {
			priority--;
			for (PrioritisedRule currentPrioritisedRule : group) {
				currentPrioritisedRule.setPriority(priority);
				String currentUri = currentPrioritisedRule.getControlPolicy().getControlMethod().getUri();
				if ("".equals(lastUri) || currentUri.equals(lastUri)) { // same policy or first iteration
					currentPolicyRules.add(currentPrioritisedRule);
				} else {
					policyRuleGroups.add(currentPolicyRules);
					currentPolicyRules.clear();
					assert (!policyRuleGroups.get(policyRuleGroups.size() - 1).isEmpty());
					currentPolicyRules.add(currentPrioritisedRule);
				}
				lastUri = currentUri;
			}
		}
		policyRuleGroups.add(currentPolicyRules);
		return policyRuleGroups;
	}

	/**
	 * <p>This method groups rules by their {@code currentPriority} and removes
	 * older forms of grouping.
	 *
	 * <p>This method will ignore any forms of former grouping by lists! It will
	 * only keep the order and look for adjacent currentPriority changes. For more
	 * information:
	 *
	 * @see
	 * SharedPrioritisationMethods#createGroupedRulesFromPrioritisation(java.util.List)
	 *
	 * @param ruleGroups {@code PrioritisedRule}s grouped with priorities set.
	 *
	 * @return {@code PrioritisedRule}s grouped by adjacent priorities.
	 */
	public static List<List<PrioritisedRule>> groupRulesByPriority(List<List<PrioritisedRule>> ruleGroups) {
		LinkedList<PrioritisedRule> ungroupedRules = SharedMethods.mergeGroupedRules(ruleGroups);
		return SharedPrioritisationMethods.createGroupedRulesFromPrioritisation(ungroupedRules);
	}

	private SharedPrioritisationMethods() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * <p>This method iterates over a {@code rules} and detects changes in
	 * currentPriority to construct grouped {@code PrioritisedRule}s. No sorting
	 * is done and the degree of differences in priorities does not matter.
	 *
	 * @param rules a {@code List} of {@code PrioritizedRule}s.
	 *
	 * @return a {@code List} with {@code PrioritizedRule}s split into
	 *          {@code List}s of {@code PrioritisedRule}s of same currentPriority.
	 */
	public static List<List<PrioritisedRule>> createGroupedRulesFromPrioritisation(List<PrioritisedRule> rules) {
		List<List<PrioritisedRule>> result = new LinkedList<>();
		List<PrioritisedRule> priorityGroup = new LinkedList<>();

		int lastPriority = -1;

		for (PrioritisedRule rule : rules) {
			int currentPriority = rule.getPriority();

			if (lastPriority == currentPriority) { // add if same priority
				priorityGroup.add(rule);
			} else {
				if (lastPriority == -1) {  // also add if it's the first
					priorityGroup.add(rule);
				} else { // if not first round, add group as list of same prioritised rules
					result.add(priorityGroup);
					priorityGroup = new LinkedList<>(); // clear list (clear() is not possible due to pass by reference)
					priorityGroup.add(rule);
				}
			}
			lastPriority = currentPriority;
		}
		if (!priorityGroup.isEmpty()) { // add last group if needed
			result.add(priorityGroup);
		}
		return result;
	}
}
