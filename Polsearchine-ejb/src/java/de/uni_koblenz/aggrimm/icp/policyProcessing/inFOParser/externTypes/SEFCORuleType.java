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

import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;

/**
 * <p>This enum hosts the SEFCO-specific types of {@code Rule}s.
 *
 * @author mruster
 */
public enum SEFCORuleType implements IExternType {

	/* Allowing rules */
	URL_ALLOWING_RULE_METHOD("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#URLAllowingRuleMethod"),
	URL_ALLOWING_RULE_SITUATION("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#URLAllowingRuleSituation"),
	HASH_VALUE_ALLOWING_RULE_METHOD("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#HashValueAllowingRuleMethod"),
	HASH_VALUE_ALLOWING_RULE_SITUATION("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#HashValueAllowingRuleSituation"),
	/* Blocking rules	 */
	URL_BLOCKING_RULE_METHOD("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#URLBlockingRuleMethod"),
	URL_BLOCKING_RULE_SITUATION("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#URLBlockingRuleSituation"),
	HASH_VALUE_BLOCKING_RULE_METHOD("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#HashValueBlockingRuleMethod"),
	HASH_VALUE_BLOCKING_RULE_SITUATION("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#HashValueBlockingRuleSituation");
	private String value;

	@Override
	public String getValue() {
		return this.value;
	}

	SEFCORuleType(String text) {
		this.value = text;
	}

	public static SEFCORuleType fromString(String text) {
		if (text != null) {
			for (SEFCORuleType b : SEFCORuleType.values()) {
				if (text.equals(b.value)) {
					return b;
				}
			}
		}
		return null;
	}
}
