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
package de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes;

/**
 * <p>This enum hosts all {@code Algorithm}s exclusively (?) known to SEFCO.
 *
 * @author mruster
 */
public enum SEFCOAlgorithmType implements ISEFCOType {

	PREFER_SINGLE_FILE_TO_WEB_SITE_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferSingleFileToWebSiteAlgorithm"),
	PREFER_WEB_SITE_TO_SINGLE_FILE_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferWebSiteToSingleFileAlgorithm"),
	PREFER_LONGEST_DOMAIN_NAME_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferLongestDomainNameAlgorithm"),
	PREFER_SHORTEST_DOMAIN_NAME_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferShortestDomainNameAlgorithm"),
	PREFER_LONGEST_PATH_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferLongestPathAlgorithm"),
	PREFER_SHORTEST_PATH_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferShortestPathAlgorithm"),
	PREFER_LONGEST_QUERY_STRING_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferLongestQueryStringAlgorithm"),
	PREFER_SHORTEST_QUERY_STRING_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#PreferShortestQueryStringAlgorithm"),
	//
	HANDLE_ALL_POLICIES_WITH_EQUAL_PRIORITY_ALGORITHM("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#HandleAllPoliciesWithEqualPriorityAlgorithm");
	private String value;

	@Override
	public String getValue() {
		return this.value;
	}

	SEFCOAlgorithmType(String text) {
		this.value = text;
	}

	public static SEFCOAlgorithmType fromString(String text) {
		if (text != null) {
			for (SEFCOAlgorithmType b : SEFCOAlgorithmType.values()) {
				if (text.equals(b.value)) {
					return b;
				}
			}
		}
		return null;
	}
}
