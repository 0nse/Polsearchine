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
public final class PreferXQueryString {

	private PreferXQueryString() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * <p>This algorithm sorts rules according to their query string length. The
	 * more parameter a rule's URL has, the higher is its priority (if sorting for
	 * longest query string length). Parameters are being determined by counting
	 * the amount of '='-{@code char}s within the query {@code String} if they
	 * have any. Else ';' will be counted (if any) and else '&' will be counted.
	 * Information about former prioritisation will be overwritten.
	 *
	 * <p>This method cannot guarantee correctness as RFC 3986 does not specify
	 * how parameters in query-strings must be marked.
	 *
	 * @param rules                     {@code List} of
	 *                                   {@code PrioritisedRule}s
	 *                                   containing VALID {@code PrioritisedRule}s.
	 * @param preferShortestQueryString {@code true} if shorter query strings
	 *                                   should be preferred, {@code false} means
	 *                                   that longer query strings are being preferred.
	 *
	 * @return prioritised {@code List}s of {@code FlowControlRuleMethod}s.
	 *
	 * @throws URISyntaxException if URL of a rule could not be converted to a
	 *                             URI.
	 */
	public static List<List<PrioritisedRule>> apply(List<PrioritisedRule> rules, boolean preferShortestQueryString) throws URISyntaxException {
		List<PrioritisedRule> sortableRules = new LinkedList<>();
		for (PrioritisedRule rule : rules) {
			URI uri = SharedMethods.getURLAsURI(rule);
			String urlQueryString = uri.getQuery();
			int priority;

			if (urlQueryString.contains("=")) {
				priority = prioritiseByKeyValue(urlQueryString);
			} else {
				priority = prioritiseByParameter(urlQueryString);
			}

			rule.setPriority(priority);
			sortableRules.add(rule);
		}

		if (preferShortestQueryString) {
			Collections.sort(sortableRules);
		} else {
			Collections.sort(sortableRules, Collections.reverseOrder());
		}
		return SharedPrioritisationMethods.createGroupedRulesFromPrioritisation(sortableRules);
	}

	/**
	 * @param urlQueryString that should be prioritised.
	 *
	 * @return number of found '='.
	 */
	private static int prioritiseByKeyValue(String urlQueryString) {
		int priority = 0;

		for (int i = 0; i < urlQueryString.length(); i++) {
			// '=' indicates the switch from key to value:
			if (urlQueryString.charAt(i) == '=') {
				priority++;
			}
		}
		/* this case was written before the algorithm tested for '=' symbols:
		 if (urlQueryString.length() > 0 && priority == 0) {
		 // http://a.de/?b has one parameter
		 priority = 1;
		 }
		 */
		return priority;
	}

	/**
	 * @param urlQueryString that should be prioritised.
	 *
	 * @return number of found ';' if any or number of found '&' else.
	 */
	private static int prioritiseByParameter(String urlQueryString) {
		char separator;
		if (urlQueryString.contains(";")) {
			separator = ';';
		} else {
			separator = '&';
		}
		int priority = 1; // priority starts at one so that ?b;a is not 1 but 2

		for (int i = 0; i < urlQueryString.length(); i++) {
			// '&' or ';' indicates the next parameter:
			if (urlQueryString.charAt(i) == separator) {
				priority++;
			}
		}
		if (urlQueryString.length() == 0) {
			priority = 0;
		}
		return priority;
	}
}
