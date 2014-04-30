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
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Abstract class to implement the strategy pattern for conflict solution
 * algorithms.
 * Each realisation implements an {@code executeAlgorithm} method including the
 * algorithm logic for a specific unsorted group of
 * {@code FlowControlRuleMethod}s. Then, {@code prioritiseRules} will put them
 * back again together to several prioritised groups.
 *
 * @author mruster
 */
public abstract class ConflictSolutionAlgorithm {

	/**
	 *
	 * @param groupedRules       rules that contain a conflict to be solved.
	 * @param affectedGroupIndex index of group that contains a detected conflict.
	 * @param conflictingRules   found conflicting rules.
	 *
	 * @return rules split up into sorted (prioritised) groups.
	 */
	protected abstract List<List<PrioritisedRule>> executeAlgorithm(List<List<PrioritisedRule>> groupedRules, int affectedGroupIndex, List<PrioritisedRule> conflictingRules);

	/**
	 * <p>This method iterates over prioritised groups again and again, executing
	 * this object's algorithm, until all conflicts have been solved.
	 *
	 * @param groupedRules                     {@code List} of prioritised
	 *                                         {@code List} of
	 *                                         {@code FlowControlRuleMethod}s,
	 *                                          that should have their conflicts solved.
	 * @param isLocalConflictSolutionAlgorithm guarantees that only conflicts are
	 *                                          being detected of rules within the
	 *                                          same policy (and with the same
	 *                                          priority).
	 *
	 * @return groupedRules
	 */
	public List<List<PrioritisedRule>> apply(List<List<PrioritisedRule>> groupedRules, boolean isLocalConflictSolutionAlgorithm) {
		for (int i = 0; i < groupedRules.size(); i++) {
			List<PrioritisedRule> group = groupedRules.get(i);
			List<PrioritisedRule> conflictingRules = findFirstConflictingRules(group, isLocalConflictSolutionAlgorithm);

			if (!conflictingRules.isEmpty()) {
				groupedRules = executeAlgorithm(groupedRules, i, conflictingRules);
				i = 0; // algorithms could have modified earlier groups too; therefore reset
			}
		}

		return groupedRules;
	}

	/**
	 * <p>Finds the first group of rules that have an overlapping region (if any).
	 * This method looks for the first overlapping occurrence instead of returning
	 * all overlapping rules. This is because after solving one conflict, the
	 * others might have disappeared (or at least changed) too.
	 *
	 *
	 * <p>A cleanup of rules is necessary after all the conflicts are being solved
	 * before persisting.
	 *
	 * <p>Only the first region will be used!
	 *
	 * @see
	 * SharedMethods#getFlowControlRuleMethodRegion(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod)
	 * @param ruleGroup          {@code List} with rules that can be in
	 *                            conflict with each other.
	 * @param mustBeOfSamePolicy is bool and guarantees that
	 *                            duplicates/conflicts will only be detected
	 *                            if two (or more) rules are from the same
	 *                            policy.
	 *
	 * @return first found conflict as a {@code LinkedList}.
	 */
	public LinkedList<PrioritisedRule> findFirstConflictingRules(List<PrioritisedRule> ruleGroup, boolean mustBeOfSamePolicy) {
		LinkedList<PrioritisedRule> resultRuleGroup = new LinkedList<>();
		for (int i = 0; i < ruleGroup.size(); i++) {
			PrioritisedRule referenceRule = ruleGroup.get(i);
			String referenceURI = SharedMethods.getFlowControlRuleMethodRegion(referenceRule);
			IExternType referenceContent = referenceRule.getContent().getInformationObject().getExternType();
			IExternType referenceExternType = referenceRule.getExternType();
			FlowControlPolicyMethod referencePolicy = referenceRule.getControlPolicy().getControlMethod();

			for (int j = i + 1; j < ruleGroup.size(); j++) {
				IExternType currentExternType = referenceRule.getExternType();
				if (currentExternType.equals(referenceExternType)) {
					// ignore non-conflicting (but overlapping) rules. Therefore, skip this round.
					continue;
				}

				boolean isInConflict = false;
				PrioritisedRule currentRule = ruleGroup.get(j);

				if (mustBeOfSamePolicy) {
					// if both rules must be of same policy but are not, we may ignore
					// this rule and simply continue with the next one
					FlowControlPolicyMethod currentPolicy = currentRule.getControlPolicy().getControlMethod();
					if (!currentPolicy.equals(referencePolicy)) {
						continue;
					}
				}

				String currentURI = SharedMethods.getFlowControlRuleMethodRegion(currentRule);
				IExternType currentContent = currentRule.getContent().getInformationObject().getExternType();
				if (referenceContent.equals(SEFCOURLContentType.WEB_SITE)) {
					if (currentURI.startsWith(referenceURI)) { // reference rule contains current rule
						isInConflict = true;
					}
				}
				if (!isInConflict) {
					if (currentContent.equals(SEFCOURLContentType.WEB_SITE)) {
						if (referenceURI.startsWith(currentURI)) { // current rule contains reference rule
							isInConflict = true;
						}
					} else {
						if (referenceURI.equals(currentURI)) { // both rules regulate over the same region
							isInConflict = true;
						}
					}
				}
				if (isInConflict) {
					resultRuleGroup.add(currentRule);
				}
			}
			if (!resultRuleGroup.isEmpty()) {
				resultRuleGroup.add(referenceRule);
				break;
			}
		}
		return resultRuleGroup;
	}
}
