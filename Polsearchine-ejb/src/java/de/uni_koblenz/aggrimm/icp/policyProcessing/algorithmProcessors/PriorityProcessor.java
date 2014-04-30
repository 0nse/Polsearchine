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

import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.IPriorityProcessorLocal;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferLongestDomainNameAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferLongestPathAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferLongestQueryStringAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferShortestDomainNameAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferShortestPathAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferShortestQueryStringAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferSingleFileToWebSiteAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.PreferWebSiteToSingleFileAlgorithm;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOAlgorithmType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.PolicyPriorityRole;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.RulePriorityRole;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.AlgorithmType;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author mruster
 */
@Singleton
@LocalBean
public class PriorityProcessor implements Serializable, IAlgorithmProcessor, IPriorityProcessorLocal {

	private static final long serialVersionUID = 4043437194263695450L;
	private List<PolicyPriorityRole> policyPriorityRoles;
	private List<RulePriorityRole> rulePriorityRoles;
	private List<List<PrioritisedRule>> groupedRules;
	private final static Logger LOGGER = Logger.getLogger(PriorityProcessor.class.getCanonicalName());

	public PriorityProcessor() {
		this.policyPriorityRoles = new LinkedList<>();
		this.rulePriorityRoles = new LinkedList<>();
		this.groupedRules = new LinkedList<>();
	}

	@Override
	public void setPolicyPriorityRoles(List<PolicyPriorityRole> policyPriorityRoles) {
		this.policyPriorityRoles = policyPriorityRoles;
	}

	@Override
	public void setRulePriorityRoles(List<RulePriorityRole> rulePriorityRoles) {
		this.rulePriorityRoles = rulePriorityRoles;
	}

	@Override
	public void setGroupedRules(List<List<PrioritisedRule>> groupedRules) {
		this.groupedRules = groupedRules;
	}

	/**
	 * <p>This method applies {@code policyPriorityRoles}s and afterwards
	 * {@code rulePriorityRoles}s.
	 *
	 * @return {@code List} of {@code List} of {@code FlowControlRuleMethod}s,
	 *          sorted according to the priorityRoles.
	 */
	@Override
	public List<List<PrioritisedRule>> apply() {
		applyPolicyPriorityRoles();
		applyRulePriorityRoles();
		return this.groupedRules;
	}

	/**
	 * <p>This method applies the {@code policyPriorityRoles}. Unsupported and
	 * unknown algorithms will throw.
	 * {@code HANDLE_ALL_POLICIES_WITH_EQUAL_PRIORITY_ALGORITHM} is valid!
	 */
	private void applyPolicyPriorityRoles() {
		assert (!policyPriorityRoles.isEmpty());
		for (PolicyPriorityRole role : policyPriorityRoles) {
			try {
				switch ((SEFCOAlgorithmType) role.getAlgorithm().getExternType()) {
					case HANDLE_ALL_POLICIES_WITH_EQUAL_PRIORITY_ALGORITHM:
						if (policyPriorityRoles.size() > 1) {
							throw new IllegalArgumentException("Test algorithm should have been used but other policy-priority-algorithms have been found. You should either use the test algorithm OR 1..n proper algorithms.");
						}

						LOGGER.log(Level.SEVERE, "Test algorithm found. Polsearchine will tolerate this and will not prioritise any policy.");
						// remove policy grouping:
						List<PrioritisedRule> ungroupedRules = new LinkedList<>();
						for (List<PrioritisedRule> group : groupedRules) {
							ungroupedRules.addAll(group);
						}
						groupedRules.clear();
						groupedRules.add(ungroupedRules);
						break;
					default:
						throw new UnsupportedOperationException("An unknown rule priority algorithm should have been executed.");
				}
			} catch (ClassCastException e) {
				switch ((AlgorithmType) role.getAlgorithm().getExternType()) {
					case PREFER_OLDEST_POLICY_ALGORITHM:
					case PREFER_LATEST_POLICY_ALGORITHM:
					case EVALUATE_RULE_ORDERING_ALGORITHM:
					case EVALUATE_POLICY_ORDERING_ALGORITHM:
						throw new UnsupportedOperationException("An unsupported rule priority algorithm should have been executed.");
					default:
						throw new UnsupportedOperationException("An unknown rule priority algorithm should have been executed.");
				}
			}
		}
	}

	/**
	 * <p>This method applies the {@code rulePriorityRoles}. Unsupported and
	 * unknown algorithms will throw.
	 */
	private void applyRulePriorityRoles() {
		for (RulePriorityRole role : rulePriorityRoles) {
			try {
				switch ((SEFCOAlgorithmType) role.getAlgorithm().getExternType()) {
					case PREFER_WEB_SITE_TO_SINGLE_FILE_ALGORITHM:
						groupedRules = new PreferWebSiteToSingleFileAlgorithm().apply(groupedRules);
						break;
					case PREFER_SINGLE_FILE_TO_WEB_SITE_ALGORITHM:
						groupedRules = new PreferSingleFileToWebSiteAlgorithm().apply(groupedRules);
						break;
					case PREFER_SHORTEST_PATH_ALGORITHM:
						groupedRules = new PreferShortestPathAlgorithm().apply(groupedRules);
						break;
					case PREFER_LONGEST_PATH_ALGORITHM:
						groupedRules = new PreferLongestPathAlgorithm().apply(groupedRules);
						break;
					case PREFER_SHORTEST_QUERY_STRING_ALGORITHM:
						groupedRules = new PreferShortestQueryStringAlgorithm().apply(groupedRules);
						break;
					case PREFER_LONGEST_QUERY_STRING_ALGORITHM:
						groupedRules = new PreferLongestQueryStringAlgorithm().apply(groupedRules);
						break;
					case PREFER_SHORTEST_DOMAIN_NAME_ALGORITHM:
						groupedRules = new PreferShortestDomainNameAlgorithm().apply(groupedRules);
						break;
					case PREFER_LONGEST_DOMAIN_NAME_ALGORITHM:
						groupedRules = new PreferLongestDomainNameAlgorithm().apply(groupedRules);
						break;
					default:
						throw new UnsupportedOperationException("An unknown rule priority algorithm should have been executed.");
				}
			} catch (ClassCastException e) {
				switch ((AlgorithmType) role.getAlgorithm().getExternType()) {
					default:
						throw new UnsupportedOperationException("An unknown rule priority algorithm should have been executed.");
				}
			}
		}
	}
}
