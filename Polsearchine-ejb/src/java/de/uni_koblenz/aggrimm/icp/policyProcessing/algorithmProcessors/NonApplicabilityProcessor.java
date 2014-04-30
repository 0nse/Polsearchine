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

import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.INonApplicabilityProcessorLocal;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.base.Control;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.NonApplicabilityRole;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleSituation;
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
public class NonApplicabilityProcessor implements Serializable, INonApplicabilityProcessorLocal {

	private static final long serialVersionUID = -501299868844474083L;
	private FlowControlPolicyMethod policy;
	private List<NonApplicabilityRole> nonApplicabilityRoles;

	@Override
	public void setPolicy(FlowControlPolicyMethod policy) {
		this.policy = policy;
	}

	@Override
	public void setNonApplicabilityRoles(List<NonApplicabilityRole> nonApplicabilityRoles) {
		this.nonApplicabilityRoles = nonApplicabilityRoles;
	}

	public NonApplicabilityProcessor() {
		nonApplicabilityRoles = new LinkedList<>();
	}

	/**
	 * @param controlRule that was not applicable and therefore needs to
	 *                     be handled by {@code NonApplicabilityRole}.
	 *
	 * @return policy with handled non applicability. {@code null} if policy has
	 *          been discarded!
	 */
	@Override
	public FlowControlPolicyMethod apply(Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation> controlRule) {

		/**
		 * <p>some algorithms make the evaluation of further algorithms
		 * unnecessary/impossible. This boolean serves as indicator if the loop must
		 * stop before having iterated over every non applicability algorithm.
		 */
		boolean isNonApplicabilitySolved = false;
		for (NonApplicabilityRole role : nonApplicabilityRoles) {
			switch ((AlgorithmType) role.getAlgorithm().getExternType()) {
				case DISCARD_NON_APPLICABLE_RULE_ALGORITHM:
					assert (!policy.getFlowControlRules().isEmpty());
					assert (policy.getFlowControlRules().indexOf(controlRule) != -1);
					policy.getFlowControlRules().remove(controlRule);
					isNonApplicabilitySolved = true;
					break;
				case DISCARD_NON_APPLICABLE_POLICY_ALGORITHM:
					policy = null;
					isNonApplicabilitySolved = true;
					break;
				default:
					throw new UnsupportedOperationException("An unknown non applicability algorithm should have been executed.");
			}
			if (isNonApplicabilitySolved) {
				break;
			}
		}
		return policy;
	}
}
