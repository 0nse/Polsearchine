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
package de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors;

import de.uni_koblenz.aggrimm.icp.info.model.technical.control.base.Control;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.NonApplicabilityRole;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleSituation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface INonApplicabilityProcessorLocal {

	/**
	 * @param controlRule that was not applicable and therefore needs to
	 *                     be handled by {@code NonApplicabilityRole}.
	 *
	 * @return policy with handled non applicability. {@code null} if policy has
	 *          been discarded!
	 */
	FlowControlPolicyMethod apply(Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation> controlRule);

	void setNonApplicabilityRoles(List<NonApplicabilityRole> nonApplicabilityRoles);

	void setPolicy(FlowControlPolicyMethod policy);
}
