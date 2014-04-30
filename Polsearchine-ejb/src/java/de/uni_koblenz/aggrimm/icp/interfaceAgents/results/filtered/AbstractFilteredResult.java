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
package de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered;

import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.ControlledTopicEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.LegalAuthorizationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.OrganizationalMotivationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import java.io.Serializable;
import java.util.List;

/**
 * <p>Interface for the most general methods a filtered {@code IResult} object
 * will have to offer.
 *
 * @author mruster
 */
public abstract class AbstractFilteredResult implements IResult, Serializable {

	private List<LegalAuthorizationEntity> legalAuthorizations;
	private List<OrganizationalMotivationEntity> organizationalMotivations;
	private RuleDataProviderEntity ruleDataProvider;
	private List<ControlledTopicEntity> controlledTopics;

	public List<LegalAuthorizationEntity> getLegalAuthorizations() {
		return legalAuthorizations;
	}

	public void setLegalAuthorizations(List<LegalAuthorizationEntity> legalAuthorizations) {
		this.legalAuthorizations = legalAuthorizations;
	}

	public List<OrganizationalMotivationEntity> getOrganizationalMotivations() {
		return organizationalMotivations;
	}

	public void setOrganizationalMotivations(List<OrganizationalMotivationEntity> organizationalMotivations) {
		this.organizationalMotivations = organizationalMotivations;
	}

	public RuleDataProviderEntity getRuleDataProvider() {
		return ruleDataProvider;
	}

	public void setRuleDataProvider(RuleDataProviderEntity ruleDataProvider) {
		this.ruleDataProvider = ruleDataProvider;
	}

	public List<ControlledTopicEntity> getControlledTopics() {
		return controlledTopics;
	}

	public void setControlledTopics(List<ControlledTopicEntity> controlledTopics) {
		this.controlledTopics = controlledTopics;
	}
}
