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
package de.uni_koblenz.aggrimm.icp.policyProcessing;

import de.uni_koblenz.aggrimm.icp.facades.local.database.IEntityLocal;
import de.uni_koblenz.aggrimm.icp.entities.info.DefaultRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.PolicyEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.URLRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.ControlledTopicEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.LegalAuthorizationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.OrganizationalMotivationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCORuleType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.LegalAuthorization;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.OrganizationalMotivation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.RuleDataProvider;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.entity.ControlledTopic;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author mruster
 */
@Stateless
public class EntityBean implements Serializable, IEntityLocal {

	private final static Logger LOGGER = Logger.getLogger(EntityBean.class.getCanonicalName());
	private static final long serialVersionUID = -334295547754359987L;

	/**
	 * @param defaultRuleMethod a {@code FlowControlRuleMethod} for a default
	 *                           rule.
	 *
	 * @return {@code DefaultRuleEntity} from {@code defaultRuleMethod}.
	 */
	@Override
	public DefaultRuleEntity createDefaultRuleEntityFromControlMethod(FlowControlRuleMethod defaultRuleMethod) {
		DefaultRuleEntity defaultRuleEntity = new DefaultRuleEntity();
		IExternType defaultRuleType = defaultRuleMethod.getExternType();

		switch ((SEFCORuleType) defaultRuleType) {
			case URL_ALLOWING_RULE_METHOD:
			case HASH_VALUE_ALLOWING_RULE_METHOD:
				defaultRuleEntity.setIsAllowingDefaultRule(true);
				break;
			case URL_BLOCKING_RULE_METHOD:
			case HASH_VALUE_BLOCKING_RULE_METHOD:
				defaultRuleEntity.setIsAllowingDefaultRule(false);
				break;
			default:
				throw new IllegalArgumentException("Unknown rule type: " + defaultRuleMethod.getUri());
		}

		return defaultRuleEntity;
	}

	/**
	 * @param policyMethod to turn into an entity.
	 *
	 * @return {@code PolicyEntity} with {@code LegalAuthorizationEntity} and
	 *          {@code OrganizationalMotivationEntity}.
	 */
	@Override
	public PolicyEntity createPolicyEntityFromControlMethod(FlowControlPolicyMethod policyMethod) {
		PolicyEntity policyEntity = new PolicyEntity();

		List<LegalAuthorization> authorizations = policyMethod.getLegalAuthorizations();
		List<LegalAuthorizationEntity> authorizationEntities = createLegalAuthorizationEntities(authorizations);
		policyEntity.setLegalAuthorizations(authorizationEntities);

		List<OrganizationalMotivation> motivations = policyMethod.getOrganizationalMotivations();
		List<OrganizationalMotivationEntity> motivationEntities = createOrganizationalMotivationEntities(motivations);
		policyEntity.setOrganizationalMotivations(motivationEntities);

		return policyEntity;
	}

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
	@Override
	public AbstractRuleEntity createRuleEntityFromPrioritisedRule(PrioritisedRule prioritisedRule) {
		AbstractRuleEntity entity = createRuleEntityFromControlMethod(prioritisedRule);

		int priority = prioritisedRule.getPriority();
		entity.setPriority(priority);

		return entity;
	}

	/**
	 * <p>This method will transform a {@code FlowControlRuleMethod} into a
	 * {@code AbstractRuleEntity}. Depending on the rule's type, different subclasses of
	 * {@code AbstractRuleEntity} are being used and returned. Policies are not being set
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
	@Override
	public AbstractRuleEntity createRuleEntityFromControlMethod(FlowControlRuleMethod ruleMethod) {
		AbstractRuleEntity entity;
		String regionURI = SharedMethods.getFlowControlRuleMethodRegion(ruleMethod);

		switch ((SEFCORuleType) ruleMethod.getExternType()) {
			case URL_ALLOWING_RULE_METHOD:
			case URL_BLOCKING_RULE_METHOD:
				entity = new URLRuleEntity();
				URLRuleEntity.class.cast(entity).setRegionURI(regionURI);
				if (ruleMethod.getExternType() == SEFCORuleType.URL_ALLOWING_RULE_METHOD) {
					entity.setIsInformationFlowAllowed(true);
				} else {
					entity.setIsInformationFlowAllowed(false);
				}
				break;
			case HASH_VALUE_ALLOWING_RULE_METHOD:
			case HASH_VALUE_BLOCKING_RULE_METHOD:
				throw new IllegalArgumentException("Unsupported rule type: " + ruleMethod.getUri());
			default:
				throw new IllegalArgumentException("Unknown rule type: " + ruleMethod.getUri());
		}
		IExternType contentType = ruleMethod.getContent().getInformationObject().getExternType();
		entity.setContent(contentType);

		List<ControlledTopic> topics = ruleMethod.getControlledTopics();
		List<ControlledTopicEntity> topicEntities = createControlledTopicEntities(topics);
		entity.setControlledTopics(topicEntities);

		RuleDataProvider dataProvider = ruleMethod.getRuleDataProvider();
		RuleDataProviderEntity dataProviderEntitiy = createRuleDataProviderEntity(dataProvider);
		entity.setRuleDataProvider(dataProviderEntitiy);

		/* this part has been removed - see method comment
		 FlowControlPolicyMethod policy = ruleMethod.getControlPolicy().getControlMethod();
		 PolicyEntity policyEntity = createPolicyEntityFromControlMethod(policy);
		 entity.setPolicy(policyEntity);
		 */
		return entity;
	}

	/**
	 * @param ruleDataProvider InFO-parser-class.
	 *
	 * @return JPA-{@code Entity} from InFO-parser-class.
	 */
	@Override
	public RuleDataProviderEntity createRuleDataProviderEntity(RuleDataProvider ruleDataProvider) {
		RuleDataProviderEntity entity = new RuleDataProviderEntity();
		String uri = ruleDataProvider.getAgent().getUri();
		entity.setUri(uri);
		return entity;
	}

	/**
	 *
	 * @param controlledTopics {@code List} of parsed objects from InFO-parser.
	 *
	 * @return {@code List} of JPA-entities.
	 */
	@Override
	public List<ControlledTopicEntity> createControlledTopicEntities(List<ControlledTopic> controlledTopics) {
		List<ControlledTopicEntity> entities = new LinkedList<>();
		for (ControlledTopic topic : controlledTopics) {
			entities.add(createControlledTopicEntity(topic));
		}
		return entities;
	}

	/**
	 *
	 * @param legalAuthorizations {@code List} of parsed objects from InFO-parser.
	 *
	 * @return {@code List} of JPA-entities.
	 */
	@Override
	public List<LegalAuthorizationEntity> createLegalAuthorizationEntities(List<LegalAuthorization> legalAuthorizations) {
		List<LegalAuthorizationEntity> entities = new LinkedList<>();
		for (LegalAuthorization topic : legalAuthorizations) {
			entities.add(createLegalAuthorizationEntity(topic));
		}
		return entities;
	}

	/**
	 *
	 * @param organizationalMotivations {@code List} of parsed objects from
	 *                                   InFO-parser.
	 *
	 * @return {@code List} of JPA-entities.
	 */
	@Override
	public List<OrganizationalMotivationEntity> createOrganizationalMotivationEntities(List<OrganizationalMotivation> organizationalMotivations) {
		List<OrganizationalMotivationEntity> entities = new LinkedList<>();
		for (OrganizationalMotivation topic : organizationalMotivations) {
			entities.add(createOrganizationalMotivationEntity(topic));
		}
		return entities;
	}

	// non-public methods following - beyond there be dragons™!
	/**
	 * @param organizationalMotivation InFO-parser-class.
	 *
	 * @return JPA-{@code Entity} from InFO-parser-class.
	 */
	private OrganizationalMotivationEntity createOrganizationalMotivationEntity(OrganizationalMotivation organizationalMotivation) {
		OrganizationalMotivationEntity entity = new OrganizationalMotivationEntity();
		String uri = organizationalMotivation.getCodeOfConductDescription().getUri();
		entity.setUri(uri);
		return entity;
	}

	/**
	 * @param legalAuthorization InFO-parser-class.
	 *
	 * @return JPA-{@code Entity} from InFO-parser-class.
	 */
	private LegalAuthorizationEntity createLegalAuthorizationEntity(LegalAuthorization legalAuthorization) {
		LegalAuthorizationEntity entity = new LegalAuthorizationEntity();
		String uri = legalAuthorization.getRegulationNorm().getUri();
		entity.setUri(uri);
		return entity;
	}

	/**
	 * @param controlledTopic InFO-parser-class.
	 *
	 * @return JPA-{@code Entity} from InFO-parser-class.
	 */
	private ControlledTopicEntity createControlledTopicEntity(ControlledTopic controlledTopic) {
		ControlledTopicEntity entity = new ControlledTopicEntity();
		String uri = controlledTopic.getUri();
		entity.setUri(uri);
		return entity;
	}
}
