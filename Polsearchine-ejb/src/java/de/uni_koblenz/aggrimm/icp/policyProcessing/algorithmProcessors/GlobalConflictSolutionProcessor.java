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

import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.IGlobalConflictSolutionProcessor;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.conflictSolution.DiscardAffectedPoliciesAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.conflictSolution.DiscardConflictingRulesAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.GlobalConflictSolutionRole;
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
public class GlobalConflictSolutionProcessor implements Serializable, IAlgorithmProcessor, IGlobalConflictSolutionProcessor {

	private static final long serialVersionUID = 1500147692048857394L;
	private List<GlobalConflictSolutionRole> globalConflictSolutionRoles;
	private List<List<PrioritisedRule>> groupedRules;

	public GlobalConflictSolutionProcessor() {
		this.globalConflictSolutionRoles = new LinkedList<>();
		this.groupedRules = new LinkedList<>();
	}

	@Override
	public void setGlobalConflictSolutionRole(List<GlobalConflictSolutionRole> globalConflictSolutionRole) {
		this.globalConflictSolutionRoles = globalConflictSolutionRole;
	}

	@Override
	public void setGroupedRules(List<List<PrioritisedRule>> groupedRules) {
		this.groupedRules = groupedRules;
	}

	/**
	 * <p>This method applies {@code globalConflictSolutionRoles}s.
	 *
	 * @return {@code List} of {@code List} of {@code FlowControlRuleMethod}s,
	 *          without any conflicts.
	 */
	@Override
	public List<List<PrioritisedRule>> apply() {
		for (GlobalConflictSolutionRole role : globalConflictSolutionRoles) {
			switch ((AlgorithmType) role.getAlgorithm().getExternType()) {
				case DISCARD_AFFECTED_POLICIES_ALGORITHM:
					groupedRules = new DiscardAffectedPoliciesAlgorithm().apply(groupedRules, false);
					break;
				case DISCARD_AFFECTED_RULES_ALGORITHM:
					groupedRules = new DiscardConflictingRulesAlgorithm().apply(groupedRules, false);
					break;
				default:
					throw new UnsupportedOperationException("An unknown global conflict solution algorithm should have been executed.");
			}
		}
		return groupedRules;
	}
}
