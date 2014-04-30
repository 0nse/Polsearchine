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
public final class PreferXDomainName {

	private PreferXDomainName() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * <p>This algorithm sorts rules according to their domain name lengths. The
	 * more labels rule's URL's domain has, the higher is its priority (if sorting
	 * for longest rules). Parameters are being determined by counting
	 * the amount of '.'-{@code char}s within the domain name {@code String}.
	 * Priorities within {@code rules} will be overwritten.
	 *
	 * @param rules                    {@code List} of
	 *                                  {@code PrioritisedRule}s
	 *                                  containing VALID {@code PrioritisedRule}s.
	 * @param preferShortestDomainName {@code true} if shorter domain names should
	 *                                  be preferred, {@code false} means that
	 *                                  longer domain names are being preferred.
	 *
	 * @return prioritised {@code List}s of {@code PrioritisedRule}s.
	 *
	 * @throws URISyntaxException if URL of a rule could not be converted to a
	 *                             URI.
	 */
	public static List<List<PrioritisedRule>> apply(List<PrioritisedRule> rules, boolean preferShortestDomainName) throws URISyntaxException {
		List<PrioritisedRule> sortableRules = new LinkedList<>();
		for (PrioritisedRule rule : rules) {
			URI uri = SharedMethods.getURLAsURI(rule);
			String urlDomainName = uri.getHost();
			int priority = 0;

			for (int i = 0; i < urlDomainName.length(); i++) {
				if (urlDomainName.charAt(i) == '.') {
					priority++;
				}
			}
			rule.setPriority(priority);
			sortableRules.add(rule);
		}

		if (preferShortestDomainName) {
			Collections.sort(sortableRules);
		} else {
			Collections.sort(sortableRules, Collections.reverseOrder());
		}
		return SharedPrioritisationMethods.createGroupedRulesFromPrioritisation(sortableRules);
	}
}
