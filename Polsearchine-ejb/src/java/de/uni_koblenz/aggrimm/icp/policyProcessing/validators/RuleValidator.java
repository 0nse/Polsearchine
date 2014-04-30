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
package de.uni_koblenz.aggrimm.icp.policyProcessing.validators;

import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCORuleType;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.Content;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ContentSpecifier;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ControlledTopic;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mruster
 */
public final class RuleValidator {

	private final static Logger LOGGER = Logger.getLogger(RuleValidator.class.getCanonicalName());

	private RuleValidator() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * <p>This method validates a given {@code FlowControlRuleMethod}. It checks
	 * accessibility and cardinalities. Also, warning messages are being logged if
	 * any regulations were violated. {@code ContentSpecifier}s are already shrunk
	 * down if possible.
	 *
	 * @param rule the actual rule that should be verified.
	 *
	 * @return {@code true} if {@code rule} is a valid rule, {@code false} else.
	 */
	public static boolean isValid(FlowControlRuleMethod rule) {
		try {
			if (!hasRuleDataProvider(rule)) {
				throw new IllegalArgumentException("RuleDataProvider");
			}
			if (!hasValidControlledTopics(rule.getControlledTopics(), true)) {
				throw new IllegalStateException("ControlledTopic");
			}
			if (rule.getSenderSpecifier() != null) {
				throw new UnsupportedOperationException("SenderSpecifier");
			}
			if (!rule.getSenders().isEmpty()) {
				throw new UnsupportedOperationException("Sender");
			}

			switch ((SEFCORuleType) rule.getExternType()) {
				case URL_ALLOWING_RULE_METHOD:
				case URL_BLOCKING_RULE_METHOD:
					if (!hasValidContentSpecifiersForURLRegions(rule, SEFCOURLContentType.class, true)) {
						throw new IllegalStateException("ContentSpecifier/Content/Region");
					}
					break;
				case HASH_VALUE_BLOCKING_RULE_METHOD:
				//TODO: implement Rules for Hashes
				case HASH_VALUE_ALLOWING_RULE_METHOD:
				default:
					throw new UnsupportedOperationException("rule type");
			}
			return true;

		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Rule with wrong or inaccessible {0} found: {1}", new Object[]{e.getMessage(), rule.getUri()});
			return false;

		} catch (IllegalStateException e) {
			LOGGER.log(Level.WARNING, "Rule with empty {0}-list found: {1}", new Object[]{e.getMessage(), rule.getUri()});
			return false;

		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "Rule was not of SEFCORule type: {1}", new Object[]{e.getMessage(), rule.getUri()});
			return false;

		} catch (UnsupportedOperationException e) {
			LOGGER.log(Level.WARNING, "Rule contained element {0}, which is either unknown or not supported: {1}", new Object[]{e.getMessage(), rule.getUri()});
			return false;
		}
	}

	/**
	 * @param rule                        with {@code ContentSpecifier}s to check.
	 * @param type                        to filter the {@code ContentSpecifier}s
	 * @param mustContainContentSpecifier {@code true} if there must be at least
	 *                                     one {@code ContentSpecifier} that
	 *                                     matches the rule type.
	 *
	 * @return true if {@code Content} is equal for every {@code ContentSpecifier}
	 *          which have a {@code Content} of {@code Class type} and all {@code Region}s
	 *          contain a valid {@code URL}.
	 */
	public static boolean hasValidContentSpecifiersForURLRegions(FlowControlRuleMethod rule, Class type, boolean mustContainContentSpecifier) {
		List<ContentSpecifier> contentSpecifiers = rule.getContentSpecifiers();
		List<ContentSpecifier> filteredContentSpecifiers = getMatchingContentSpecifiers(contentSpecifiers, type);
		assert (filteredContentSpecifiers.size() <= contentSpecifiers.size());

		IExternType ruleContent = rule.getContent().getInformationObject().getExternType();

		if (mustContainContentSpecifier) {
			if (filteredContentSpecifiers.isEmpty()) {
				return false;
			}
		}
		if (!hasIdenticalContents(filteredContentSpecifiers)) {
			return false;
		}
		for (ContentSpecifier specifier : filteredContentSpecifiers) {
			if (!hasValidURLRegions(specifier, ruleContent)) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 *
	 * @param topic that should have a known {@code TopicContent} value.
	 *
	 * @return {@code true} if topic has non-empty {@code String} value,
	 *          {@code false} else.
	 */
	public static boolean hasControlledTopic(ControlledTopic topic) {
		try {
			String topicContent = topic.getTopic().getTopicContent();
			if (topicContent.isEmpty()) {
				return false;
			}
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}

	/**
	 *
	 * @param controlledTopics a list of {@code ControlledTopic}s.
	 * @param mustContainTopic {@code true} , if 1..*, {@code false} if
	 *                          0..*.
	 *
	 * @return {@code true} if all {@code ControlledTopic}s are accessible and
	 *          {@code mustContainTopic} is not validated.
	 */
	public static boolean hasValidControlledTopics(List<? extends ControlledTopic> controlledTopics, boolean mustContainTopic) {
		boolean valid = true;
		if (mustContainTopic) {
			if (controlledTopics.isEmpty()) {
				valid = false;
			}
		}
		for (ControlledTopic topic : controlledTopics) {
			if (!hasControlledTopic(topic)) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	/**
	 * <p>This function returns a {@code LinkedList} of {@code ControlledTopic}s
	 * which are all valid. Therefore, it can be empty. It can be used for a
	 * DiscardUnkownRuleComponents- or DiscardIncompleteRuleComponents-algorithm.
	 * A new list is created in memory instead of reusing the old one. This allows
	 * the parameter to be more generic and still working on O(1) for adding.
	 *
	 * @param controlledTopics {@code List} that contains {@code ControlledTopic}s
	 *                          which should be checked.
	 *
	 * @return {@code LinkedList<ControlledTopic>} with only valid topics left.
	 */
	public static LinkedList<ControlledTopic> getValidControlledTopicsList(List<ControlledTopic> controlledTopics) {
		LinkedList<ControlledTopic> validControlledTopicsList = new LinkedList<>();
		for (ControlledTopic topic : controlledTopics) {
			if (hasControlledTopic(topic)) {
				validControlledTopicsList.add(topic);
			}
		}
		return validControlledTopicsList;
	}

	/**
	 *
	 *
	 * @param rule {@code FlowControlRuleMethod} that should be tested for
	 *              having a {@code RuleDataProvider} with a valid Agent's URI.
	 *
	 * @return {@code true} if {@code RuleDataProvider}'s
	 *          {@code Agent}-{@code String} is accessible, {@code false} else.
	 */
	public static boolean hasRuleDataProvider(FlowControlRuleMethod rule) {
		try {
			URI uri = new URI(rule.getRuleDataProvider().getAgent().getUri());
			uri.getClass(); // just so that the compiler won't remove the URI creation
			return true;
		} catch (URISyntaxException ex) {
			return false;
		}
	}

	/**
	 * <p>This method checks which of the {@code contentSpecifiers} describe a
	 * content of class {@code type}. It can be used to find rules that have
	 * {@code ContentSpecifier}s that do or do not match their type.
	 *
	 * @param contentSpecifiers {@code List} of {@code ContentSpecifier}s that
	 *                           should be tested for {@code type} and if needed
	 *                           shortened.
	 * @param type              {@code Class} that a {@code ContentSpecifier}'s
	 *                           {@code Content}'s {@code InformationObject}'s
	 *                           {@code IExternType} must be castable to.
	 *
	 * @return {@code LinkedList} of {@code ContentSpecifier}s, whose
	 *          {@code Content} is of the same type as is {@code type}.
	 */
	public static LinkedList<ContentSpecifier> getMatchingContentSpecifiers(List<ContentSpecifier> contentSpecifiers, Class type) {
		LinkedList<ContentSpecifier> l = new LinkedList<>();
		for (ContentSpecifier specifier : contentSpecifiers) {
			Content currentContent = specifier.getContent();
			IExternType currentInformationObject = currentContent.getInformationObject().getExternType();

			try {
				type.cast(currentInformationObject);
				l.add(specifier);
			} catch (ClassCastException e) {
				// pass
			}
		}
		return l;
	}

	/**
	 * @param contentSpecifiers {@code List} of {@code ContentSpecifiers} which
	 *                           should all have the same {@code Content}.
	 *
	 * @return true if {@code List} has only 0..1 elements, or if {@code Content}
	 *          is the same throughout all specifiers.
	 */
	public static boolean hasIdenticalContents(List<ContentSpecifier> contentSpecifiers) {
		if (contentSpecifiers.size() < 2) {
			return true;
		}
		Content refContent = contentSpecifiers.get(0).getContent();
		for (ContentSpecifier specifier : contentSpecifiers) {
			if (!refContent.equals(specifier.getContent())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>Returns false if URL was no valid URI and if a {@code WEB_SITE}'s URL
	 * is not a path. In the later case, the method checks for query-strings and
	 * fragments. Also, then the URL must end on a slash or must have no path at
	 * all.
	 *
	 * @param specifier a {@code ContentSpecifier} which should have valid URIs as
	 *                   {@code Region}.
	 *
	 * @return {@code true} if ALL {@code Region}s were proper URLs.
	 */
	public static boolean hasValidURLRegions(ContentSpecifier specifier, IExternType content) {
		try {
			String regionURL = SharedMethods.getContentSpecifierRuleMethodRegion(specifier);

			URI uri = new URI(regionURL);

			if (content.equals(SEFCOURLContentType.WEB_SITE)) {
				if (!regionURL.endsWith("/")) {
					if (uri.getPath().length() != 0) {
						return false;
					}
				} else if (uri.getQuery() != null) {
					return false;
				} else if (uri.getFragment() != null) {
					return false;
				}
			}
			return true;
		} catch (URISyntaxException ex) {
			return false;
		}
	}
}
