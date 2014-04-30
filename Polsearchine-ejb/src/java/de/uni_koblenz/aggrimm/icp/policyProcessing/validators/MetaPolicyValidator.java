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

import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCORuleType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.AbstractRole;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlMetaPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mruster
 */
public final class MetaPolicyValidator {

	private final static Logger LOGGER = Logger.getLogger(MetaPolicyValidator.class.getCanonicalName());

	private MetaPolicyValidator() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * @param metaPolicy {@code FlowControlMetaPolicyMethod} that should be tested
	 *                    for having a valid {@code defaultRule}.
	 *
	 * @return {@code true} if the meta policy has a {@code defaultRule} that is
	 *          a {@code SEFCORuleType} *_RULE_METHOD.
	 */
	public static boolean hasValidDefaultRule(FlowControlMetaPolicyMethod metaPolicy) {
		try {
			FlowControlRuleMethod defaultRule = metaPolicy.getDefaultRule().getControlMethod();
			SEFCORuleType ruleType = (SEFCORuleType) defaultRule.getExternType();
			switch (ruleType) {
				case HASH_VALUE_ALLOWING_RULE_METHOD:
					return true;
				case HASH_VALUE_BLOCKING_RULE_METHOD:
					return true;
				case URL_ALLOWING_RULE_METHOD:
					return true;
				case URL_BLOCKING_RULE_METHOD:
					return true;
				default:
					return false;
			}
		} catch (NullPointerException | ClassCastException e) {
			return false;
		}
	}

	/**
	 * <p>This method validates a given {@code FlowControlMetaPolicyMethod}. It
	 * checks accessibility and cardinalities. Also, warning messages are being
	 * logged if any regulations were violated.
	 *
	 * @param metaPolicy      should be checked for validity.
	 * @param enforcingSystem that the meta policy must have.
	 *
	 * @return {@code true} if all classes are accessible and satisfy the
	 *          cardinalities, {@code false} else.
	 */
	public static boolean isValid(FlowControlMetaPolicyMethod metaPolicy, IExternType enforcingSystem) {
		try {
			if (!SharedValidator.hasMatchingEnforcingSystem(metaPolicy, enforcingSystem)) {
				throw new IllegalArgumentException("EnforcingSystem");
			}
			if (!SharedValidator.hasResponsibleOperator(metaPolicy)) {
				throw new IllegalArgumentException("ResponsibleOperator");
			}
			if (!hasValidDefaultRule(metaPolicy)) {
				throw new IllegalArgumentException("DefaultRule");
			}
			List<? extends AbstractRole> l;

			l = metaPolicy.getRulePriorityRoles();
			if (!SharedValidator.hasValidRoleAlgorithms(l, true)) {
				throw new IllegalArgumentException("RulePriorityRole");
			}
			l = metaPolicy.getNonApplicabilityRoles();
			if (!SharedValidator.hasValidRoleAlgorithms(l, true)) {
				throw new IllegalArgumentException("NonApplicabilityRole");
			}
			l = metaPolicy.getPolicyPriorityRoles();
			if (!SharedValidator.hasValidRoleAlgorithms(l, true)) {
				throw new IllegalArgumentException("PolicyPriorityRole");
			}
			l = metaPolicy.getGlobalConflictSolutionRoles();
			if (!SharedValidator.hasValidRoleAlgorithms(l, true)) {
				throw new IllegalArgumentException("GlobalConflictSolutionRole");
			}
			return true;

		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Meta policy with wrong or inaccessible {0} found: {1}", new Object[]{e.getMessage(), metaPolicy.getUri()});
			return false;
		}
	}
}
