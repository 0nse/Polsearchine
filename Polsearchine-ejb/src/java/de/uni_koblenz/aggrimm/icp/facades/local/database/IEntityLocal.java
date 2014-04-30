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
package de.uni_koblenz.aggrimm.icp.facades.local.database;

import de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.DefaultRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.PolicyEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.ControlledTopicEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.LegalAuthorizationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.OrganizationalMotivationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.LegalAuthorization;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.OrganizationalMotivation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.RuleDataProvider;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ControlledTopic;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface IEntityLocal {

	/**
	 *
	 * @param controlledTopics {@code List} of parsed objects from InFO-parser.
	 *
	 * @return {@code List} of JPA-entities.
	 */
	List<ControlledTopicEntity> createControlledTopicEntities(List<ControlledTopic> controlledTopics);

	/**
	 * @param defaultRuleMethod a {@code FlowControlRuleMethod} for a default
	 *                           rule.
	 *
	 * @return {@code DefaultRuleEntity} from {@code defaultRuleMethod}.
	 */
	DefaultRuleEntity createDefaultRuleEntityFromControlMethod(FlowControlRuleMethod defaultRuleMethod);

	/**
	 *
	 * @param legalAuthorizations {@code List} of parsed objects from InFO-parser.
	 *
	 * @return {@code List} of JPA-entities.
	 */
	List<LegalAuthorizationEntity> createLegalAuthorizationEntities(List<LegalAuthorization> legalAuthorizations);

	/**
	 *
	 * @param organizationalMotivations {@code List} of parsed objects from
	 *                                   InFO-parser.
	 *
	 * @return {@code List} of JPA-entities.
	 */
	List<OrganizationalMotivationEntity> createOrganizationalMotivationEntities(List<OrganizationalMotivation> organizationalMotivations);

	/**
	 * @param policyMethod to turn into an entity.
	 *
	 * @return {@code PolicyEntity} with {@code LegalAuthorizationEntity} and
	 *          {@code OrganizationalMotivationEntity}.
	 */
	PolicyEntity createPolicyEntityFromControlMethod(FlowControlPolicyMethod policyMethod);

	/**
	 * @param ruleDataProvider InFO-parser-class.
	 *
	 * @return JPA-{@code Entity} from InFO-parser-class.
	 */
	RuleDataProviderEntity createRuleDataProviderEntity(RuleDataProvider ruleDataProvider);

	/**
	 * <p>This method will transform a {@code FlowControlRuleMethod} into a
	 * {@code AbstractRuleEntity}. Depending on the rule's type, different
	 * subclasses of
	 * {@code AbstractRuleEntity} are being used and returned. Policies are not
	 * being set
	 * to prevent problems with JPA (duplicate policies when persisting multiple
	 * rules with the same policy).
	 * <p>Only the first region will be transformed!
	 *
	 * @see
	 * SharedMethods#getFlowControlRuleMethodRegion(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod)
	 *
	 * @param ruleMethod to turn into an entity.
	 *
	 * @return {@code AbstractRuleEntity} with {@code ControlledTopicEntity} and
	 *          {@code RuleDataProviderEntity}, contentType and URLRegion.
	 */
	AbstractRuleEntity createRuleEntityFromControlMethod(FlowControlRuleMethod ruleMethod);

	/**
	 *
	 * @see
	 * #createDefaultRuleEntityFromControlMethod(de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod)
	 * @param prioritisedRule to turn into an entity.
	 * <p>Only the first region will be transformed!
	 *
	 *
	 * @return {@code AbstractRuleEntity} with {@code ControlledTopicEntity} and
	 *          {@code RuleDataProviderEntity}, contentType, URLRegion and priority.
	 */
	AbstractRuleEntity createRuleEntityFromPrioritisedRule(PrioritisedRule prioritisedRule);
}
