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
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors;

import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.ILocalConflictSolutionProcessorLocal;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.conflictSolution.DiscardConflictingRulesAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.logic.SharedPrioritisationMethods;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.LocalConflictSolutionRole;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.AlgorithmType;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author mruster
 */
@Singleton
@LocalBean
public class LocalConflictSolutionProcessor implements Serializable, IAlgorithmProcessor, ILocalConflictSolutionProcessorLocal {

	private static final long serialVersionUID = 2852668017510210022L;
	private List<List<PrioritisedRule>> groupedRules;

	@Override
	public void setGroupedRules(List<List<PrioritisedRule>> groupedRules) {
		this.groupedRules = groupedRules;
	}

	/**
	 * <p>This method applies {@code localConflictSolutionRoles}s. For information
	 * on grouping and ungrouping:
	 *
	 * @see #groupRulesByAdjacentPolicy(java.util.List)
	 * @see #groupRulesByPriority(java.util.List)
	 *
	 * @return {@code List} of {@code List} of {@code PrioritisedRule}s,
	 *          without any (or at least with less) conflicts within the groups.
	 */
	@Override
	public List<List<PrioritisedRule>> apply() {
		groupedRules = SharedPrioritisationMethods.groupRulesByAdjacentPolicy(this.groupedRules);
		List<List<PrioritisedRule>> conflictLessGroupedRules = new LinkedList<>();

		for (List<PrioritisedRule> policyRuleGroup : groupedRules) {
			assert (!policyRuleGroup.isEmpty());
			FlowControlPolicyMethod currentPolicy = policyRuleGroup.get(0).getControlPolicy().getControlMethod();
			List<LocalConflictSolutionRole> localConflictSolutionRoles = currentPolicy.getLocalConflictSolutionRoles();

			List<List<PrioritisedRule>> wrappedGroup = new LinkedList<>();
			wrappedGroup.add(policyRuleGroup);

			for (LocalConflictSolutionRole role : localConflictSolutionRoles) {
				switch ((AlgorithmType) role.getAlgorithm().getExternType()) {
					case DISCARD_CONFLICTING_RULES_ALGORITHM:
						wrappedGroup = new DiscardConflictingRulesAlgorithm().apply(wrappedGroup, true);
						break;
					default:
						throw new UnsupportedOperationException("An unknown global conflict solution algorithm should have been executed.");
				}
			}
			assert (wrappedGroup.size() == 1);
			conflictLessGroupedRules.add(wrappedGroup.get(0));
		}
		conflictLessGroupedRules = SharedPrioritisationMethods.groupRulesByPriority(conflictLessGroupedRules);
		return conflictLessGroupedRules;
	}
}
