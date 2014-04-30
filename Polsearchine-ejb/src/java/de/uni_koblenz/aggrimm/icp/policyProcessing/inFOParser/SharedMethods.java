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
package de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser;

import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import de.uni_koblenz.aggrimm.icp.policyProcessing.validators.RuleValidator;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ContentSpecifier;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mruster
 */
public class SharedMethods {

	/**
	 * <p>This method needs rules to be split by {@code ContentSpecifier}s! One
	 * ContentSpecifier per rule.
	 *
	 * @param rule will have its first {@code ContentSpecifier}'s {@code Region}
	 *              extracted.
	 *
	 * @return THE region as {@code URI} from {@code rule}.
	 */
	public static URI getURLAsURI(FlowControlRuleMethod rule) throws URISyntaxException {
		try {
			String url = getFlowControlRuleMethodRegion(rule);
			URI uri = new URI(url);
			return uri;
		} catch (URISyntaxException e) {
			throw new URISyntaxException(e.getInput(), e.getReason());
		}
	}

	/**
	 * <p>This method needs rules to be split by {@code ContentSpecifier}s! One
	 * ContentSpecifier per rule.
	 *
	 * @param rule will have its first {@code ContentSpecifier}'s {@code Region}
	 *              extracted.
	 *
	 * @see
	 * #getContentSpecifierRuleMethodRegion(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ContentSpecifier)
	 * @see
	 * #splitRuleBasedOnContentSpecifiers(de.uni_koblenz.aggrimm.icp.inFOParser.algorithmProcessors.prioritisation.wrappers.PrioritisedRule)
	 *
	 * @return THE region as {@code String} from {@code rule}.
	 */
	public static String getFlowControlRuleMethodRegion(FlowControlRuleMethod rule) {
		return getContentSpecifierRuleMethodRegion(rule.getContentSpecifiers().get(0));
	}

	/**
	 * <p>This method extracts the {@code Region}'s URI as {@code String}.
	 *
	 * @param specifier will have its {@code Region} extracted.
	 *
	 * @return {@code Region}'s URI as {@code String} from {@code specifier}.
	 */
	public static String getContentSpecifierRuleMethodRegion(ContentSpecifier specifier) {
		String url = specifier.getRegion().getRegionContent().getUri();
		return url;
	}

	/**
	 * {@code groupedRules} will be transformed into a {@code List}
	 * containing only a single {@code List} of all rules.
	 *
	 * @param groupedRules rules with multiple levels of priority (rule ordering
	 *                      and policy ordering).
	 *
	 * @return rules ordered but without policy ordering.
	 */
	public static LinkedList<PrioritisedRule> mergeGroupedRules(List<List<PrioritisedRule>> groupedRules) {
		LinkedList<PrioritisedRule> mergedRules = new LinkedList<>();
		for (List<PrioritisedRule> ruleGroup : groupedRules) {
			mergedRules.addAll(ruleGroup);
		} // and merge them into one group
		return mergedRules;
	}

	/**
	 *
	 * @param rules with multiple {@code ContentSpecifier}s.
	 *
	 * @return {@code List} of {@code PrioritisedRule}s with every rule only
	 *          having exactly one {@code ContentSpecifier}.
	 */
	public static List<PrioritisedRule> splitRulesBasedOnContentSpecifiers(List<PrioritisedRule> rules) {
		LinkedList<PrioritisedRule> splitRules = new LinkedList<>();

		for (PrioritisedRule rule : rules) {
			splitRules.addAll(splitRuleBasedOnContentSpecifiers(rule));
		}
		return splitRules;
	}

	/**
	 * <p>This method separates rules into rules with only one {@code Region}. For
	 * this, rules are being split by {@code ContentSpecifier}.
	 * <p>While we're at it, the {@code Region} URLs are being updated as well.
	 * This only happens when the {@code Content} describes a {@code WEB_SITE} but
	 * does not end on a path. In this case, a slash will be added. You will want
	 * to check what URLs are being denounced valid:
	 *
	 * @see
	 * RuleValidator#hasValidURLRegions(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ContentSpecifier,
	 * de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType)
	 *
	 * @param rule with multiple {@code ContentSpecifier}s.
	 *
	 * @return {@code List} of {@code PrioritisedRule}s with every rule only
	 *          having exactly one {@code ContentSpecifier}.
	 */
	public static List<PrioritisedRule> splitRuleBasedOnContentSpecifiers(PrioritisedRule rule) {
		LinkedList<PrioritisedRule> splitRules = new LinkedList<>();

		List<ContentSpecifier> contentSpecifiers = rule.getContentSpecifiers();
		IExternType ruleContent = rule.getContent().getInformationObject().getExternType();

		for (ContentSpecifier contentSpecifier : contentSpecifiers) {
			PrioritisedRule newRule = cloneRule(rule);

			if (ruleContent.equals(SEFCOURLContentType.WEB_SITE)) {
				String newRuleRegionURL = getContentSpecifierRuleMethodRegion(contentSpecifier);
				if (!newRuleRegionURL.endsWith("/")) {
					newRuleRegionURL = newRuleRegionURL + '/';
					contentSpecifier.getRegion().getRegionContent().setUri(newRuleRegionURL);
				}
			}

			List<ContentSpecifier> newContentSpecifier = new LinkedList<>();
			newContentSpecifier.add(contentSpecifier);
			newRule.setContentSpecifiers(newContentSpecifier);
			splitRules.add(newRule);
		}
		return splitRules;
	}

	/**
	 * <p>As {@code PrioritisedRule} is neither {@code Serializable} nor
	 * does it have a {@code clone()}-method, it must be cloned manually.
	 *
	 * @param rule to copy.
	 *
	 * @return a new {@code FlowControlRuleMethod}-object with transferred
	 *          attributes.
	 */
	public static PrioritisedRule cloneRule(PrioritisedRule rule) {
		PrioritisedRule clonedRule = new PrioritisedRule((FlowControlRuleMethod) rule);
		clonedRule.setPriority(rule.getPriority());

		return clonedRule;
	}
}
