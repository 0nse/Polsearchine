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
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.logic;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mruster
 */
public final class PreferXPath {

	private PreferXPath() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * <p>This algorithm sorts rules according to their path lengths. The more
	 * directories and files a rule's URL has, the higher is its priority (if
	 * sorting for longest rules). Parameters are being determined by counting
	 * the amount of '/'-{@code char}s within the path {@code String}. The last
	 * char within the path {@code String} is only counted if it isn't a
	 * '/'-{@code char} and therefore most probably a file. Prioritisation
	 * information will be overwritten.
	 *
	 * @param rules              {@code List} of {@code PrioritisedRule}s
	 *                            containing VALID {@code PrioritisedRule}s.
	 * @param preferShortestPath {@code true} if shorter paths should be
	 *                            preferred, {@code false} means that longer paths
	 *                            are being preferred.
	 *
	 * @return prioritised {@code List}s of {@code PrioritisedRule}s.
	 *
	 * @throws URISyntaxException if URL of a rule could not be converted to a
	 *                             URI.
	 */
	public static List<List<PrioritisedRule>> apply(List<PrioritisedRule> rules, boolean preferShortestPath) throws URISyntaxException {
		List<PrioritisedRule> sortableRules = new LinkedList<>();
		for (PrioritisedRule rule : rules) {
			URI uri = SharedMethods.getURLAsURI(rule);
			String urlPath = uri.getPath();
			int priority = 0;
			if (urlPath.length() > 0) {
				int i;

				for (i = 0; i < urlPath.length() - 1; i++) {
					char currentChar = urlPath.charAt(i);

					if (currentChar == '/') {
						priority++;
					}
				}
				if (urlPath.charAt(i) != '/') {
					priority++; // this is a file then
				}
			}
			rule.setPriority(priority);
			sortableRules.add(rule);
		}

		if (preferShortestPath) {
			Collections.sort(sortableRules);
		} else {
			Collections.sort(sortableRules, Collections.reverseOrder());
		}
		return SharedPrioritisationMethods.createGroupedRulesFromPrioritisation(sortableRules);
	}
}
