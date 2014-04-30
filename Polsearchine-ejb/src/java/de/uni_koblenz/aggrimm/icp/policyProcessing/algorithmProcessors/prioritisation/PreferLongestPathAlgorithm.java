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

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.logic.PreferXPath;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <p>The flow control rules are ordered according to the length of the URL's
 * local path, beginning with the longest path. The length of a path corresponds
 * to the number of directory sections it contains.
 * <p>This description is quoted from the official InFO-wiki (last accessed
 * 18.07.2013, 5:00 pm). The entry was made by Andreas Kasten, 2013.
 *
 * @see
 * http://icp.it-risk.iwvi.uni-koblenz.de/wiki/Search_Engine-based_Flow_Control_Ontology
 *
 * @author mruster
 */
public class PreferLongestPathAlgorithm extends PrioritisationAlgorithm {

	/**
	 * @param group {@code List} of {@code PrioritisedRule}s containing
	 *               VALID {@code PrioritisedRule}s.
	 *
	 * @return prioritised {@code List}s of {@code PrioritisedRule}s.
	 */
	@Override
	protected List<List<PrioritisedRule>> executeAlgorithm(List<PrioritisedRule> group) throws URISyntaxException {
		return PreferXPath.apply(group, false);
	}
}
