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
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ContentSpecifier;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.InformationObject;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mruster
 */
public final class PreferSingleFileOrWebSite {

	private PreferSingleFileOrWebSite() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 *
	 * @param rules         {@code List} of {@code PrioritisedRule}s
	 *                       containing VALID {@code PrioritisedRule}s.
	 * @param preferWebSite {@code true} if {@code WEB_SITE}s should be preferred,
	 *                       {@code false} means that single files are being preferred.
	 *
	 * @return prioritised {@code List}s of {@code PrioritisedRule}s.
	 */
	public static List<List<PrioritisedRule>> apply(List<PrioritisedRule> rules, boolean preferWebSite) {
		List<PrioritisedRule> singleFileRules = new LinkedList<>();
		List<PrioritisedRule> webSiteRules = new LinkedList<>();
		for (PrioritisedRule rule : rules) {
			List<ContentSpecifier> contentSpecifiers = rule.getContentSpecifiers();
			if (!contentSpecifiers.isEmpty()) {
				InformationObject informationObject = contentSpecifiers.get(0).getContent().getInformationObject();
				switch ((SEFCOURLContentType) informationObject.getExternType()) {
					case WEB_PAGE:
						singleFileRules.add(rule);
						break;
					case WEB_SITE:
						webSiteRules.add(rule);
						break;
					default:
						throw new IllegalArgumentException("Unknown SEFCOURLContentType found as Content. You may add this INFOType to the list of known single-file-types within the source code. Its ExternType was: " + informationObject.getExternType());
				}
			}
		}
		LinkedList<List<PrioritisedRule>> result = new LinkedList<>();
		if (preferWebSite) {
			result.add(webSiteRules);
			result.add(singleFileRules);
		} else {
			result.add(singleFileRules);
			result.add(webSiteRules);
		}
		return result;
	}
}
