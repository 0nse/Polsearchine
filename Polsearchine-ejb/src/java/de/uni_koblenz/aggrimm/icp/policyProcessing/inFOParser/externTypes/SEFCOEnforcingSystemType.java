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
 * <p>This enum hosts possible {@code EnforcingSystem}s for SEFCO.
 *
 * @author mruster
 */
public enum SEFCOEnforcingSystemType implements ISEFCOType {

	SEARCH_ENGINE("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/search_engine_flow_control.owl#SearchEngine");
	private String value;

	@Override
	public String getValue() {
		return this.value;
	}

	SEFCOEnforcingSystemType(String text) {
		this.value = text;
	}

	public static SEFCOEnforcingSystemType fromString(String text) {
		if (text != null) {
			for (SEFCOEnforcingSystemType b : SEFCOEnforcingSystemType.values()) {
				if (text.equals(b.value)) {
					return b;
				}
			}
		}
		return null;
	}
}
