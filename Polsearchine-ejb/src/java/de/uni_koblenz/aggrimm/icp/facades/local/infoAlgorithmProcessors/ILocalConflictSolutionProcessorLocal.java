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

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface ILocalConflictSolutionProcessorLocal {

	/**
	 * <p>This method applies {@code localConflictSolutionRoles}s. For information
	 * on grouping and ungrouping:
	 *
	 * @see #groupRulesByPolicy(java.util.List)
	 * @see #groupRulesByPriority(java.util.List)
	 *
	 * @return {@code List} of {@code List} of {@code PrioritisedRule}s,
	 *          without any (or at least with less) conflicts within the groups.
	 */
	List<List<PrioritisedRule>> apply();

	void setGroupedRules(List<List<PrioritisedRule>> groupedRules);
}
