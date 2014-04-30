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
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.OrganizationalMotivation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface IDatabasePersistenceLocal {

	/**
	 * @return amount of deleted entries.
	 */
	int deleteControlledTopic();

	/**
	 * @return amount of deleted entries.
	 */
	int deleteDefaultRule();

	/**
	 * <p>Deletes all rules (of any known kind), policies, the default rule,
	 * organizational motivations, legal authorizations, controlled topics and
	 * rule data providers.
	 *
	 * @return amount of deleted entries.
	 */
	int deleteInFOEntities();

	/**
	 * @return amount of deleted entries.
	 */
	int deleteLegalAuthorizations();

	/**
	 * @return amount of deleted entries.
	 */
	int deleteOrganizationalMotivations();

	/**
	 * @return amount of deleted entries.
	 */
	int deletePolicies();

	/**
	 * <p>This method deletes all groups and therefore renders logging impossible.
	 *
	 * @return amount of deleted entries.
	 */
	int deletePolsearchineGroups();

	/**
	 * <p>This method deletes all users and therefore renders logging impossible.
	 *
	 * @return amount of deleted entries.
	 */
	int deletePolsearchineUsers();

	/**
	 * @return amount of deleted entries.
	 */
	int deleteRuleDataProvider();

	/**
	 * <p>Deletes all known rules. This method is identical to
	 * {@code deleteURLRules} as long as HashValueRules and similar are not
	 * implemented yet.
	 *
	 * @return amount of deleted entries.
	 */
	int deleteRules();

	/**
	 * <p>Deletes URL allowing and blocking rules.
	 *
	 * @return amount of deleted entries.
	 */
	int deleteURLRules();

	/**
	 * <p>This method merges a policy and returns it.
	 *
	 * @param policy with all relations set if you are using cascade-persist.
	 *
	 * @return the policy that just got persisted retrieved from the database.
	 */
	PolicyEntity mergePolicy(PolicyEntity policy);

	/**
	 * <p>This method persists policies. Pay attention to your persistence
	 * strategy.
	 *
	 * @param policies with all relations set if you are using cascade-persist.
	 */
	void persistPolicies(List<PolicyEntity> policies);

	/**
	 * <p>Persist a {@code rule}. If you have set all relations set before calling
	 * this method and are using cascade-persist as persistence strategy, you
	 * don't need to call any policy persisting method.
	 *
	 * @param rule with all relations set if you are using cascade-persist.
	 */
	void persistRule(AbstractRuleEntity rule);

	/**
	 * <p>Persists all {@code rules}. Pay attention to your persistence strategy
	 * to prevent policies being stored multiple times.
	 *
	 * @see
	 * #persistRule(de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity)
	 *
	 * @param rules with all relations set if you are using cascade-persist.
	 */
	void persistRules(List<AbstractRuleEntity> rules);

	/**
	 * @param controlledTopics {@code List} that should be persisted.
	 */
	void persistsControlledTopics(List<ControlledTopicEntity> controlledTopics);

	/**
	 * <p>This method persists the default rule.
	 *
	 * @param defaultRule with nothing but a {@code boolean} to indicate
	 *                     allowance.
	 */
	void persistsDefaultRule(DefaultRuleEntity defaultRule);

	/**
	 * @param legalAuthorizations {@code List} that should be persisted.
	 */
	void persistsLegalAuthorizations(List<LegalAuthorizationEntity> legalAuthorizations);

	/**
	 * @param organizationalMotivations {@code List} that should be persisted.
	 */
	void persistsOrganizationalMotivations(List<OrganizationalMotivation> organizationalMotivations);

	/**
	 * @param ruleDataProvider {@code Entity} that should be persisted.
	 */
	void persistsRuleDataProvider(RuleDataProviderEntity ruleDataProvider);
}
