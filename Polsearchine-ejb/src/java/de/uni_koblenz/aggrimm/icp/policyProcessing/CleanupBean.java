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
package de.uni_koblenz.aggrimm.icp.policyProcessing;

import de.uni_koblenz.aggrimm.icp.facades.local.ICleanupLocal;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author mruster
 */
@LocalBean
@Singleton
public class CleanupBean implements Serializable, ICleanupLocal {

	private final static Logger LOGGER = Logger.getLogger(CleanupBean.class.getCanonicalName());
	private static final long serialVersionUID = 7732048551396073828L;

	/**
	 * <p>Overlapping rules with the same priority that control the information
	 * flow the same way are being detected and removed by this method. The list
	 * <b>must be</b> free of conflicts. Only the most general rules will be kept.
	 *
	 * <p>Only the first region will be used!
	 *
	 * @see
	 * SharedMethods#getFlowControlRuleMethodRegion(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod)
	 * @param ruleGroup {@code List} with rules that may overlap each
	 *                   other but may not be in conflict.
	 *
	 * @return complete {@code List} without overlapping rules (preferring more
	 *          general ones).
	 */
	public static List<PrioritisedRule> getOverlappingFreeRules(List<PrioritisedRule> ruleGroup) {
		for (int i = 0; i < ruleGroup.size(); i++) {
			PrioritisedRule referenceRule = ruleGroup.get(i);
			String referenceURI = SharedMethods.getFlowControlRuleMethodRegion(referenceRule);
			IExternType referenceContent = referenceRule.getContent().getInformationObject().getExternType();
			boolean shouldReferenceRuleBeAdded = true;

			for (int j = i + 1; j < ruleGroup.size(); j++) {
				PrioritisedRule currentRule = ruleGroup.get(j);
				String currentURI = SharedMethods.getFlowControlRuleMethodRegion(currentRule);
				IExternType currentContent = currentRule.getContent().getInformationObject().getExternType();
				boolean shouldCurrentRuleBeAdded = true;

				if (referenceContent.equals(SEFCOURLContentType.WEB_SITE)) {
					if (currentURI.startsWith(referenceURI)) {
						shouldCurrentRuleBeAdded = false;
					}
				}
				if (shouldCurrentRuleBeAdded) {
					if (currentContent.equals(SEFCOURLContentType.WEB_SITE)) {
						if (referenceURI.startsWith(currentURI)) {
							shouldReferenceRuleBeAdded = false;
						}
					} else {
						if (referenceURI.equals(currentURI)) {
							shouldCurrentRuleBeAdded = false;
						}
					}
				}
				if (!shouldCurrentRuleBeAdded) {
					ruleGroup.remove(j--);
				}
			}
			if (!shouldReferenceRuleBeAdded) {
				ruleGroup.remove(i--);
			}
		}
		return ruleGroup;
	}
	private List<List<PrioritisedRule>> prioritisedRuleGroups;

	public CleanupBean() {
		this.prioritisedRuleGroups = new LinkedList<>();
	}

	@Override
	public void setPrioritisedRuleGroups(List<List<PrioritisedRule>> prioritisedRuleGroups) {
		this.prioritisedRuleGroups = prioritisedRuleGroups;
	}

	/**
	 * <p>Only call this method if you can be sure that there are no duplicates
	 * within each rule-group ({@code List} within the {@code List}) anymore! For
	 * doing so, check {@code findDuplicates()} referenced below.
	 * <p>This method will run through every group and determines which rules from
	 * lower prioritised groups can either be removed or must be kept.
	 * If there is a more precise rule in a lower prioritised group, the higher
	 * prioritised rule will be removed when they are of the same
	 * {@code IExternType}. If you don't want lower prioritised rules to replace
	 * higher ones, check out this method in revision 158 or 159.
	 * Again: Make sure to remove unnecessary rules within a group beforehand:
	 *
	 * @see #removeRedundantRulesWithinGroups()
	 *
	 * <p>Only the first region will be used!
	 *
	 * @see
	 * SharedMethods#getFlowControlRuleMethodRegion(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod)
	 *
	 * @see ConflictSolutionAlgorithm#findDuplicates(java.util.List, boolean) for
	 * similar code.
	 *
	 * <p>Only the first region will be used!
	 *
	 * @see
	 * SharedMethods#getFlowControlRuleMethodRegion(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod)
	 */
	private void removeRedundantRulesByPrioritisation() {
		for (int i = 0; i < prioritisedRuleGroups.size() - 1; i++) { // iterate over rule groups
			List<PrioritisedRule> referenceRuleGroup = prioritisedRuleGroups.get(i);

			for (int j = 0; j < referenceRuleGroup.size(); j++) { // iterate over rule groups' rules
				boolean isReferenceRuleUnneeded = false;
				// rule with higher priority:
				PrioritisedRule referenceRule = referenceRuleGroup.get(j);
				String referenceURI = SharedMethods.getFlowControlRuleMethodRegion(referenceRule);
				IExternType referenceContent = referenceRule.getContent().getInformationObject().getExternType();
				IExternType referenceExternType = referenceRule.getExternType();

				for (int k = i + 1; k < prioritisedRuleGroups.size(); k++) { // iterate over lower rule groups
					// lower priotised group than referenceRuleGroup:
					List<PrioritisedRule> currentRuleGroup = prioritisedRuleGroups.get(k);

					for (int l = 0; l < currentRuleGroup.size(); l++) { // iterate over lower rule groups' rules
						boolean isCurrentRuleUnneeded = false;
						PrioritisedRule currentRule = currentRuleGroup.get(l);
						String currentURI = SharedMethods.getFlowControlRuleMethodRegion(currentRule);
						IExternType currentContent = currentRule.getContent().getInformationObject().getExternType();

						if (referenceContent.equals(SEFCOURLContentType.WEB_SITE)) {
							if (currentURI.startsWith(referenceURI)) { // reference rule contains current rule
								isCurrentRuleUnneeded = true;
							}
						}
						if (!isCurrentRuleUnneeded) {
							if (currentContent.equals(SEFCOURLContentType.WEB_SITE)) {
								if (referenceURI.startsWith(currentURI)) { // current rule contains reference rule
									IExternType currentExternType = referenceRule.getExternType();
									if (currentExternType.equals(referenceExternType)) { // if of same type, the lower prioritised rule may be used
										isReferenceRuleUnneeded = true;
									} // else both rules are important and must be kept!
								}
							} else {
								if (referenceURI.equals(currentURI)) { // both rules regulate over the same region
									isCurrentRuleUnneeded = true;
								}
							}
						}
						if (isCurrentRuleUnneeded) {
							// removes currentRule and modifies counter (to prevent duplicates):
							currentRuleGroup.remove(l--);
							// i does not need to be modified as i < j
						}
					}
					// update changes in the list (no matter if there were any, simply merge):
					prioritisedRuleGroups.set(k, currentRuleGroup);
				}
				if (isReferenceRuleUnneeded) {
					referenceRuleGroup.remove(j--);
					prioritisedRuleGroups.set(i, referenceRuleGroup);
				}
			}
		}
	}

	/**
	 * <p>This method removes rules that are expressed by other ones within the
	 * same group.
	 *
	 * @see ConflictSolutionAlgorithm#findDuplicates(java.util.List, boolean)
	 */
	private void removeRedundantRulesWithinGroups() {
		for (int i = 0; i < prioritisedRuleGroups.size(); i++) {
			List<PrioritisedRule> currentRuleGroup = prioritisedRuleGroups.get(i);
			currentRuleGroup = getOverlappingFreeRules(currentRuleGroup);
			prioritisedRuleGroups.set(i, currentRuleGroup);
		}
	}

	/**
	 * <p>This method cleans up prioritised rules and transfers the information
	 * from {@code List}s within {@code List}s into {@code PrioritisedRule}s.
	 * This means that all rules will be "reprioritised" with all unique
	 * rule group priorities. Every rule within a rule group will share the same
	 * priority. Before calling this method, make sure the you have the rules
	 * grouped by priority or policy.
	 *
	 * @see SharedPrioritisationMethods#groupRulesByPolicy(java.util.List)
	 * @see SharedPrioritisationMethods#groupRulesByPriority(java.util.List)
	 *
	 * @return prioritised rules without unnecessary ones and with priorities set.
	 */
	@Override
	public List<List<PrioritisedRule>> removePrioritisationAndOverlappingRules() {
		removeRedundantRulesWithinGroups();
		removeRedundantRulesByPrioritisation();

		int prioritisedRuleGroupsSize = prioritisedRuleGroups.size();
		for (int i = 0; i < prioritisedRuleGroupsSize; i++) {
			// assign new priorities with the first rule being highest:
			int currentPriority = prioritisedRuleGroupsSize - i;
			List<PrioritisedRule> currentRuleGroup = prioritisedRuleGroups.get(i);

			if (!currentRuleGroup.isEmpty()) {
				for (int j = 0; j < currentRuleGroup.size(); j++) {
					PrioritisedRule currentRule = currentRuleGroup.get(j);
					// add priorities per policy for every included rule:
					currentRule.setPriority(currentPriority);
					currentRuleGroup.set(j, currentRule);
				}
				prioritisedRuleGroups.set(i, currentRuleGroup);
			}
		}
		return prioritisedRuleGroups;
	}
}
