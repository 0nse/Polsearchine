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

import de.uni_koblenz.aggrimm.icp.info.parser.utils.INFOType;

/**
 * <p>This enum hosts the types for {@code Content}s needed for URL-based
 * regulation with SEFCO. By now, it should also be present within
 * {@code INFOType} so this is just a backup copy. If more content types are
 * added to SEFCO, they can all be hosted together in this enum instead of
 * splitting it in various {@code IExternType}s.
 *
 * @see INFOType#WEB_PAGE
 * @see INFOType#WEB_SITE
 *
 * @author mruster
 */
public enum SEFCOURLContentType implements ISEFCOContentType {

	WEB_SITE("http://icp.it-risk.iwvi.uni-koblenz.de/ontologies/technical_regulation.owl#WebSite"),
	WEB_PAGE("http://www.ontologydesignpatterns.org/ont/dul/IOLite.owl#WebPage");
	private String value;

	@Override
	public String getValue() {
		return this.value;
	}

	SEFCOURLContentType(String text) {
		this.value = text;
	}

	public static SEFCOURLContentType fromString(String text) {
		if (text != null) {
			for (SEFCOURLContentType b : SEFCOURLContentType.values()) {
				if (text.equals(b.value)) {
					return b;
				}
			}
		}
		return null;
	}
}
