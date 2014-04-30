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
package de.uni_koblenz.aggrimm.icp.interfaceAgents;

import de.uni_koblenz.aggrimm.icp.entities.info.PolicyEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.AbstractMetaInformationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.ControlledTopicEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.LegalAuthorizationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.OrganizationalMotivationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.FilteredImageResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.FilteredWebResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.AbstractFilteredResult;
import de.uni_koblenz.aggrimm.icp.facades.local.database.IDatabaseQueryLocal;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.NoResultException;

/**
 * <p>This abstract class supplies basic JSON manipulation methods for
 * converting a JSONString into an {@code IResultsContainer}. The concrete
 * conversion of JSON and retrieving the results are not implemented in this
 * method but must be delegated to an implementation of this method for a
 * specific search engine.
 * <p>It also hosts shared methods for constructing filtered results.
 *
 * @author mruster
 */
public abstract class AbstractResultParser {

	@Resource(name = "SEARCH_ENGINE_URI")
	protected String SEARCH_ENGINE_URI;
	protected boolean areInformationFlowsAllowed;
	@EJB
	protected IDatabaseQueryLocal databaseQueryHelper;

	/**
	 * @param resultJSONString result String that has yet to be parsed to JSON.
	 * @param source           from where the results are ("web" or "image").
	 * @param skip             integer of manually skipped values.
	 *
	 * @return parsed <code>IWebResult</code>s in
	 *          an <code>IResultsContainer</code>.
	 * @throws ParseException           if the {@code resultJSONString} cannot be
	 *                                   properly parsed.
	 * @throws IllegalArgumentException if {@code source} is neither "web" nor
	 *                                   "image".
	 */
	public IResultsContainer<IResult> parseJSONString(String resultJSONString, String source, int skip) throws ParseException {
		if (!source.equals("web") && !source.equals("image")) {
			throw new IllegalArgumentException("The source parameter \"" + source + "\" is unknown.");
		}
		JSONParser parser = new JSONParser();
		JSONObject parsedString = (JSONObject) (parser.parse(resultJSONString));

		return convertToResultContainer(parsedString, source, skip);
	}

	/**
	 * @param o   JSONObject with keys.
	 * @param key to look for.
	 *
	 * @return value of {@code o}'s {@code key} as String or empty String.
	 */
	protected String getValue(JSONObject o, String key) {
		Object obj = o.get(key);
		return (obj == null) ? "" : obj.toString();
	}

	/**
	 * @param o   JSONObject with keys.
	 * @param key to look for.
	 *
	 * @return value of {@code o}'s {@code key} parsed to int or 0 if
	 *          {@code null}.
	 */
	protected int getIntValue(JSONObject o, String key) {
		Object obj = o.get(key);
		return (obj == null) ? 0 : Integer.parseInt(obj.toString());
	}

	protected abstract IResultsContainer<IResult> convertToResultContainer(JSONObject parsedString, String source, int skip);

	/**
	 *
	 * @param isImage {@code true} if {@code FilteredImageResult} should be
	 *                 returned, {@code FilteredWebResult} else.
	 *
	 * @return AbstractFilteredResult with basic attributes set with
	 *          {@code SEARCH_ENGINE_URI}.
	 */
	private AbstractFilteredResult createFilterResultForDefaultRule(boolean isImage) {
		AbstractFilteredResult filteredResult = chooseFilterResultClass(isImage);

		AbstractMetaInformationEntity thisEnforcingSystem = new ControlledTopicEntity();
		thisEnforcingSystem.setUri(SEARCH_ENGINE_URI);

		filteredResult.getControlledTopics().add((ControlledTopicEntity) thisEnforcingSystem);

		thisEnforcingSystem = new RuleDataProviderEntity();
		filteredResult.setRuleDataProvider((RuleDataProviderEntity) thisEnforcingSystem);

		thisEnforcingSystem = new OrganizationalMotivationEntity();
		filteredResult.getOrganizationalMotivations().add((OrganizationalMotivationEntity) thisEnforcingSystem);
		return filteredResult;
	}

	/**
	 *
	 * @param regulatingRule {@code AbstractRuleEntity} with {@code PolicyEntity}.
	 * @param isImage        {@code true} if {@code FilteredImageResult} should be
	 *                        returned, {@code FilteredWebResult} else.
	 *
	 * @return concrete instance of {@code AbstractFilteredResult} with shared
	 *          attributes set.
	 */
	private AbstractFilteredResult createFilteredResultFromRule(AbstractRuleEntity regulatingRule, boolean isImage) {
		AbstractFilteredResult filteredResult = chooseFilterResultClass(isImage);
		PolicyEntity policyEntity = regulatingRule.getPolicy();

		List<ControlledTopicEntity> controlledTopics = regulatingRule.getControlledTopics();
		filteredResult.setControlledTopics(controlledTopics);

		RuleDataProviderEntity ruleDataProvider = regulatingRule.getRuleDataProvider();
		filteredResult.setRuleDataProvider(ruleDataProvider);

		List<LegalAuthorizationEntity> legalAuthorizations = policyEntity.getLegalAuthorizations();
		filteredResult.setLegalAuthorizations(legalAuthorizations);

		List<OrganizationalMotivationEntity> organizationalMotivations = policyEntity.getOrganizationalMotivations();
		filteredResult.setOrganizationalMotivations(organizationalMotivations);

		return filteredResult;
	}

	/**
	 *
	 * @param isImage {@code true} if {@code FilteredImageResult} should be
	 *                 returned, {@code FilteredWebResult} else.
	 *
	 * @return depending on {@code isImage}, returns initialised
	 *          {@code FilteredImageResult} or {@code FilteredWebResult}.
	 */
	private AbstractFilteredResult chooseFilterResultClass(boolean isImage) {
		AbstractFilteredResult filteredResult;
		if (isImage) {
			filteredResult = new FilteredImageResult();
		} else {
			filteredResult = new FilteredWebResult();
		}
		return filteredResult;
	}

	/**
	 *
	 * <p>This method checks whether {@code resultURL} needs to be filtered or
	 * not. It returns {@code null} if no filtering is necessary and a
	 * {@code FilteredImageResult} or {@code FilteredWebResult} else (depending on
	 * the value of {@code isImage}).
	 *
	 * @param resultURL result that should be displayed or regulated.
	 * @param isImage   {@code true} if {@code FilteredImageResult} should be
	 *                   returned, {@code FilteredWebResult} else.
	 *
	 * @return {@code null} if result must not be filtered;
	 *          {@code AbstractFilteredResult} with values set else.
	 */
	protected AbstractFilteredResult createFilteredResultIfNeeded(String resultURL, boolean isImage) {
		AbstractFilteredResult result = null;
		try {
			AbstractRuleEntity regulatingRule = databaseQueryHelper.getRegulatingRule(resultURL);
			if (regulatingRule == null) {
				throw new NoResultException();
			}

			if (!regulatingRule.isInformationFlowAllowed()) {
				result = createFilteredResultFromRule(regulatingRule, isImage);
			}
		} catch (NoResultException e) {
			if (!areInformationFlowsAllowed) {
				result = createFilterResultForDefaultRule(isImage);
			}
		}
		return result;
	}
}
