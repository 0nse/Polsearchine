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

import de.uni_koblenz.aggrimm.icp.facades.local.database.IDatabasePersistenceLocal;
import de.uni_koblenz.aggrimm.icp.entities.info.DefaultRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.PolicyEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.ControlledTopicEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.LegalAuthorizationEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.OrganizationalMotivation;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * <p>This is the bean that modifies the database. It writes to it and therefore
 * concurrency problems could occur. Therefore, this Bean is {@code Singleton}.
 *
 * @author mruster
 */
@LocalBean
@Singleton
public class DatabasePersistenceBean implements Serializable, IDatabasePersistenceLocal {

	private static final long serialVersionUID = 2329482671874153574L;
	@PersistenceContext(unitName = "Polsearchine-ejbPU")
	private EntityManager entityManager;
	private final static Logger LOGGER = Logger.getLogger(DatabasePersistenceBean.class.getCanonicalName());

	/**
	 * <p>Persists all {@code rules}. Pay attention to your persistence strategy
	 * to prevent policies being stored multiple times.
	 *
	 * @see
	 * #persistRule(de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity)
	 *
	 * @param rules with all relations set if you are using cascade-persist.
	 */
	@Override
	public void persistRules(List<AbstractRuleEntity> rules) {
		for (AbstractRuleEntity rule : rules) {
			persistRule(rule);
		}
	}

	/**
	 * <p>Persist a {@code rule}. If you have set all relations set before calling
	 * this method and are using cascade-persist as persistence strategy, you
	 * don't need to call any policy persisting method.
	 *
	 * @param rule with all relations set if you are using cascade-persist.
	 */
	@Override
	public void persistRule(AbstractRuleEntity rule) {
		entityManager.persist(rule);
	}

	/**
	 * <p>This method persists policies. Pay attention to your persistence
	 * strategy.
	 *
	 * @param policies with all relations set if you are using cascade-persist.
	 */
	@Override
	public void persistPolicies(List<PolicyEntity> policies) {
		for (PolicyEntity policy : policies) {
			entityManager.persist(policy);
		}
	}

	/**
	 * <p>This method merges a policy and returns it.
	 *
	 * @param policy with all relations set if you are using cascade-persist.
	 *
	 * @return the policy that just got persisted retrieved from the database.
	 */
	@Override
	public PolicyEntity mergePolicy(PolicyEntity policy) {
		return entityManager.merge(policy);
	}

	/**
	 * <p>This method persists the default rule.
	 *
	 * @param defaultRule with nothing but a {@code boolean} to indicate
	 *                     allowance.
	 */
	@Override
	public void persistsDefaultRule(DefaultRuleEntity defaultRule) {
		entityManager.persist(defaultRule);
	}

	/**
	 * @param organizationalMotivations {@code List} that should be persisted.
	 */
	@Override
	public void persistsOrganizationalMotivations(List<OrganizationalMotivation> organizationalMotivations) {
		entityManager.persist(organizationalMotivations);
	}

	/**
	 * @param legalAuthorizations {@code List} that should be persisted.
	 */
	@Override
	public void persistsLegalAuthorizations(List<LegalAuthorizationEntity> legalAuthorizations) {
		entityManager.persist(legalAuthorizations);
	}

	/**
	 * @param controlledTopics {@code List} that should be persisted.
	 */
	@Override
	public void persistsControlledTopics(List<ControlledTopicEntity> controlledTopics) {
		entityManager.persist(controlledTopics);
	}

	/**
	 * @param ruleDataProvider {@code Entity} that should be persisted.
	 */
	@Override
	public void persistsRuleDataProvider(RuleDataProviderEntity ruleDataProvider) {
		entityManager.persist(ruleDataProvider);
	}

	/**
	 * <p>Deletes all rules (of any known kind), policies, the default rule,
	 * organizational motivations, legal authorizations, controlled topics and
	 * rule data providers.
	 *
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteInFOEntities() {
		return (deleteRules()
						+ deleteRuleDataProvider()
						+ deleteControlledTopic()
						+ deletePolicies()
						+ deleteLegalAuthorizations()
						+ deleteOrganizationalMotivations()
						+ deleteDefaultRule());
	}

	/**
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteDefaultRule() {
		LOGGER.log(Level.WARNING, "Deleting all meta policies.");
		return entityManager.createQuery("DELETE FROM DefaultRuleEntity").executeUpdate();
	}

	/**
	 * @return amount of deleted entries.
	 */
	@Override
	public int deletePolicies() {
		LOGGER.log(Level.WARNING, "Deleting all policies.");
		return entityManager.createQuery("DELETE FROM PolicyEntity").executeUpdate();
	}

	/**
	 * <p>Deletes URL allowing and blocking rules.
	 *
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteURLRules() {
		LOGGER.log(Level.WARNING, "Deleting all URL rules.");
		return entityManager.createQuery("DELETE FROM URLRuleEntity").executeUpdate();
	}

	/**
	 * <p>This method deletes all groups and therefore renders logging impossible.
	 *
	 * @return amount of deleted entries.
	 */
	@Override
	public int deletePolsearchineGroups() {
		LOGGER.log(Level.SEVERE, "Deleting all user groups.");
		return entityManager.createQuery("DELETE FROM PolsearchineGroupEntity").executeUpdate();
	}

	/**
	 * <p>This method deletes all users and therefore renders logging impossible.
	 *
	 * @return amount of deleted entries.
	 */
	@Override
	public int deletePolsearchineUsers() {
		LOGGER.log(Level.SEVERE, "Deleting all users.");
		return entityManager.createQuery("DELETE FROM PolsearchineUserEntity").executeUpdate();
	}

	/**
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteOrganizationalMotivations() {
		LOGGER.log(Level.INFO, "Deleting all organziational motivations.");
		return entityManager.createQuery("DELETE FROM OrganizationalMotivationEntity").executeUpdate();
	}

	/**
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteLegalAuthorizations() {
		LOGGER.log(Level.INFO, "Deleting all legal authorizations.");
		return entityManager.createQuery("DELETE FROM LegalAuthorizationEntity").executeUpdate();
	}

	/**
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteControlledTopic() {
		LOGGER.log(Level.INFO, "Deleting all controlled topics.");
		return entityManager.createQuery("DELETE FROM ControlledTopicEntity").executeUpdate();
	}

	/**
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteRuleDataProvider() {
		LOGGER.log(Level.INFO, "Deleting all rule data providers.");
		return entityManager.createQuery("DELETE FROM RuleDataProviderEntity").executeUpdate();
	}

	/**
	 * <p>Deletes all known rules. This method is identical to
	 * {@code deleteURLRules} as long as HashValueRules and similar are not
	 * implemented yet.
	 *
	 * @return amount of deleted entries.
	 */
	@Override
	public int deleteRules() {
		return deleteURLRules();
	}
}
