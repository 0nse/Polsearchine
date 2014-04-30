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

import de.uni_koblenz.aggrimm.icp.facades.local.IPolicyProcessingLocal;
import de.uni_koblenz.aggrimm.icp.entities.info.DefaultRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.PolicyEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity;
import de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers.PrioritisedRule;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SEFCOParser;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.SharedMethods;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOEnforcingSystemType;
import de.uni_koblenz.aggrimm.icp.policyProcessing.validators.MetaPolicyValidator;
import de.uni_koblenz.aggrimm.icp.policyProcessing.validators.PolicyValidator;
import de.uni_koblenz.aggrimm.icp.policyProcessing.validators.RuleValidator;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.base.Control;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.ResponsibleOperator;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlMetaPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlMetaPolicySituation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicySituation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleSituation;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import de.uni_koblenz.aggrimm.icp.facades.local.ICleanupLocal;
import de.uni_koblenz.aggrimm.icp.facades.local.database.IDatabasePersistenceLocal;
import de.uni_koblenz.aggrimm.icp.facades.local.database.IEntityLocal;
import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.IGlobalConflictSolutionProcessor;
import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.ILocalConflictSolutionProcessorLocal;
import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.INonApplicabilityProcessorLocal;
import de.uni_koblenz.aggrimm.icp.facades.local.infoAlgorithmProcessors.IPriorityProcessorLocal;
import de.uni_koblenz.aggrimm.icp.facades.remote.IPolicyIOBeanRemote;
import de.uni_koblenz.aggrimm.icp.triplestore.jena.JenaStore;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 * <p>This class is used for processing the policies. They get parsed,
 * validated, processed and persisted.
 *
 * @author mruster
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PolicyProcessingBean implements Serializable, IPolicyProcessingLocal {

	private static final long serialVersionUID = -8394453216080107229L;
	@EJB
	private IPolicyIOBeanRemote policyIOBeanFacade;
	@EJB
	private IDatabasePersistenceLocal databaseHelper;
	@EJB
	private IEntityLocal entityHelper;
	@EJB
	private INonApplicabilityProcessorLocal nonApplicabilityProcessor;
	@EJB
	private IPriorityProcessorLocal priorityProcessor;
	@EJB
	private IGlobalConflictSolutionProcessor globalConflictSolutionProcessor;
	@EJB
	private ILocalConflictSolutionProcessorLocal localConflictSolutionProcessor;
	@EJB
	private ICleanupLocal cleanupHelper;
	private final static Logger LOGGER = Logger.getLogger(SEFCOParser.class.getCanonicalName());

	@Override
	@Lock(LockType.WRITE)
	public void processOwlFiles() {

		SEFCOParser sefcoParser = new SEFCOParser(new JenaStore());
		ArrayList<File> policyFiles = policyIOBeanFacade.getStoredPolicies();

		for (File policy : policyFiles) {
			String policyURI = policy.toURI().toASCIIString();
			sefcoParser.addFile(policyURI);
		}
		LOGGER.log(Level.FINE, "Started parsing of policy files");

		try {
			List<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> metaPolicies = sefcoParser.getMetaPolicies();
			Iterator<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> iterator = metaPolicies.iterator();

			while (iterator.hasNext()) {
				Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation> controlMetaPolicy = iterator.next();
				FlowControlMetaPolicyMethod metaPolicy = controlMetaPolicy.getControlMethod();

				if (!MetaPolicyValidator.isValid(metaPolicy, SEFCOEnforcingSystemType.SEARCH_ENGINE)) {
					iterator.remove();
				}
			}

			switch (metaPolicies.size()) {
				case 0:
					throw new RuntimeException("No meta policy was found. Polsearchine cannot operate without a meta policy. Aborting.");
				case 1:
					LOGGER.log(Level.FINE, "Found exactly one meta policy. Continuing as planned.");
					break;
				default:
					throw new RuntimeException("Multiple meta policies have been found. Polsearchine can only use one policy at once. Aborting.");
			}
			// use only one meta policy:
			FlowControlMetaPolicyMethod metaPolicy = metaPolicies.get(0).getControlMethod();
			// copy some attributes for easier access (and less loop-redundancy):
			IExternType enforcingSystem = metaPolicy.getEnforcingSystem().getTechnicalSystem().getExternType();
			ResponsibleOperator responsibleOperator = metaPolicy.getResponsibleOperator();

			nonApplicabilityProcessor.setNonApplicabilityRoles(metaPolicy.getNonApplicabilityRoles());

			List<List<PrioritisedRule>> groupedRules = new LinkedList<>();
			for (Control<FlowControlPolicyMethod, FlowControlPolicySituation> controlPolicy : metaPolicy.getControlPolicies()) {
				FlowControlPolicyMethod policy = controlPolicy.getControlMethod();
				nonApplicabilityProcessor.setPolicy(policy);

				if (PolicyValidator.isValid(policy, enforcingSystem, responsibleOperator)) {
					// prevent ConcurrentModificationException:
					List<Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation>> controlRules = new LinkedList<>();
					controlRules.addAll(policy.getFlowControlRules());

					for (Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation> controlRule : controlRules) {
						FlowControlRuleMethod rule = controlRule.getControlMethod();

						if (!RuleValidator.isValid(rule)) {
							policy = nonApplicabilityProcessor.apply(controlRule);
							// if the policy was removed, there are no more policyRules:
							if (policy == null) {
								break;
							}
						}
					}
					if (policy != null) {
						List<PrioritisedRule> policyRules = new LinkedList<>();
						// we have yet to iterate once again because the policyRules within the
						// policy might not only have possibly been removed but altered too:
						for (Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation> controlRule : policy.getFlowControlRules()) {
							PrioritisedRule currentRule = new PrioritisedRule(controlRule.getControlMethod());
							List<PrioritisedRule> splitRules = SharedMethods.splitRuleBasedOnContentSpecifiers(currentRule);
							policyRules.addAll(splitRules);
						}
						if (!policyRules.isEmpty()) {
							groupedRules.add(policyRules);
						}
					}
				}
			}

			if (!groupedRules.isEmpty()) {
				priorityProcessor.setGroupedRules(groupedRules);
				priorityProcessor.setPolicyPriorityRoles(metaPolicy.getPolicyPriorityRoles());
				priorityProcessor.setRulePriorityRoles(metaPolicy.getRulePriorityRoles());
				groupedRules = priorityProcessor.apply();

				localConflictSolutionProcessor.setGroupedRules(groupedRules);
				groupedRules = localConflictSolutionProcessor.apply();

				globalConflictSolutionProcessor.setGroupedRules(groupedRules);
				globalConflictSolutionProcessor.setGlobalConflictSolutionRole(metaPolicy.getGlobalConflictSolutionRoles());
				groupedRules = globalConflictSolutionProcessor.apply();

				cleanupHelper.setPrioritisedRuleGroups(groupedRules);
				groupedRules = cleanupHelper.removePrioritisationAndOverlappingRules();

				databaseHelper.deleteInFOEntities();
				for (List<PrioritisedRule> group : groupedRules) {
					if (!group.isEmpty()) {
						FlowControlPolicyMethod policy = group.get(0).getControlPolicy().getControlMethod();
						PolicyEntity policyEntity = entityHelper.createPolicyEntityFromControlMethod(policy);
						policyEntity = databaseHelper.mergePolicy(policyEntity);

						for (PrioritisedRule rule : group) {
							AbstractRuleEntity ruleEntity = entityHelper.createRuleEntityFromPrioritisedRule(rule);
							ruleEntity.setPolicy(policyEntity); //TODO this duplicates the policies
							databaseHelper.persistRule(ruleEntity);
						}
					}
				}
			} else {
				LOGGER.log(Level.INFO, "No rules found, therefore not using any policies either.");
				// clear the db in the if-else-branches individually to make sure the db is only
				// manipulated when all prior policy processing succeeded:
				databaseHelper.deleteInFOEntities();
			}
			// persist default rule:
			FlowControlRuleMethod defaultRuleMethod = metaPolicy.getDefaultRule().getControlMethod();
			DefaultRuleEntity defaultRuleEntity = entityHelper.createDefaultRuleEntityFromControlMethod(defaultRuleMethod);
			databaseHelper.persistsDefaultRule(defaultRuleEntity);

			LOGGER.log(Level.INFO, "Everything has been persisted as planned. Polsearchine should be fully operational in no time.");
		} catch (NullPointerException e) {
			throw new NullPointerException("Parsing failed most likely due to an unknown element within the OWL-files. If so, this log will contain information of the resource's ExternTypes that could not be parsed. Else this error can also be associated with failed resource injection.");
		}
	}
}
