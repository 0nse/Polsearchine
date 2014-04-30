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
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.logic.PreferSingleFileOrWebSite;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.util.List;

/**
 * <p>The flow control rules are ordered according to their {@code Content}. If
 * it's a {@code WebSite} it gets a higher priority than if it's a
 * single file like a {@code WebPage}.
 *
 *
 * @author mruster
 */
public class PreferWebSiteToSingleFileAlgorithm extends PrioritisationAlgorithm {

	/**
	 * @param group {@code List} of {@code PrioritisedRule}s containing
	 *               VALID {@code PrioritisedRule}s.
	 *
	 * @return prioritised {@code List}s of {@code PrioritisedRule}s.
	 */
	@Override
	protected List<List<PrioritisedRule>> executeAlgorithm(List<PrioritisedRule> group) {
		return PreferSingleFileOrWebSite.apply(group, true);
	}
}
