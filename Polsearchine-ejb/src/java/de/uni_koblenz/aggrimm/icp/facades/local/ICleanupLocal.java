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
package de.uni_koblenz.aggrimm.icp.facades.local;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface ICleanupLocal {

	/**
	 * <p>This method cleans up prioritised rules and transfers the information
	 * from {@code List}s within {@code List}s into {@code PrioritisedRule}s.
	 * Before calling this method, make sure the you have the rules grouped by
	 * priority or policy.
	 *
	 * @see SharedPrioritisationMethods#groupRulesByPolicy(java.util.List)
	 * @see SharedPrioritisationMethods#groupRulesByPriority(java.util.List)
	 *
	 * @return prioritised rules without unnecessary ones and with priorities set.
	 */
	List<List<PrioritisedRule>> removePrioritisationAndOverlappingRules();

	void setPrioritisedRuleGroups(List<List<PrioritisedRule>> prioritisedRuleGroups);
}
