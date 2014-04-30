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

import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.LegalAuthorization;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.OrganizationalMotivation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.ResponsibleOperator;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlPolicyMethod;
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
public final class PolicyValidator {

	private final static Logger LOGGER = Logger.getLogger(PolicyValidator.class.getCanonicalName());

	private PolicyValidator() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 * <p>This method validates a given {@code FlowControlPolicyMethod}. It checks
	 * accessibility and cardinalities. Also, warning messages are being logged if
	 * any regulations were violated.
	 *
	 * @param policy             the actual policy that should be verified.
	 * @param enforcingSystem    {@code IExternType} of system to check for.
	 * @param metaPolicyOperator {@code ResponsibleOperator} which must be
	 *                            identical to the one used in {@code policy}.
	 *
	 * @return {@code true} if {@code policy} is a valid policy, {@code false}
	 *          else.
	 */
	public static boolean isValid(FlowControlPolicyMethod policy, IExternType enforcingSystem, ResponsibleOperator metaPolicyOperator) {
		try {
			if (!SharedValidator.hasMatchingEnforcingSystem(policy, enforcingSystem)) {
				throw new IllegalArgumentException("EnforcingSystem");
			}
			if (!SharedValidator.hasResponsibleOperator(policy)) {
				throw new IllegalArgumentException("ResponsibleOperator");
			}
			if (!SharedValidator.hasMatchingResponsibleOperatorString(policy, metaPolicyOperator)) {
				throw new IllegalArgumentException("ResponsibleOperator-value");
			}
			if (!hasValidLegalAuthorizationAndOrganizationalMotivation(policy.getLegalAuthorizations(), policy.getOrganizationalMotivations())) {
				throw new IllegalArgumentException("LegalAuthorization- and OrganizationalMotivation");
			}
			return true;
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Policy with wrong or inaccessible {0} found: {1}", new Object[]{e.getMessage(), policy.getUri()});
			return false;
		}
	}

	/**
	 * <p>This function returns a {@code LinkedList} of
	 * {@code OrganizationalMotivation}s which are all valid. Therefore, it can be
	 * empty. A new list is created in memory instead of reusing the old one. This
	 * allows the parameter to be more generic and still working on O(1) for
	 * adding.
	 *
	 * @param l {@code List} that contains {@code OrganizationalMotivation}s which
	 *           should be checked.
	 *
	 * @return {@code LinkedList<OrganizationalMotivation>} with only valid
	 *          authorizations left.
	 */
	@Deprecated
	public static LinkedList<OrganizationalMotivation> getValidOrganizationalMotivationsList(List<OrganizationalMotivation> l) {
		LinkedList<OrganizationalMotivation> validOrganizationalMotivationsList = new LinkedList<>();
		for (OrganizationalMotivation motivation : l) {
			if (hasValidOrganizationalMotivation(motivation)) {
				validOrganizationalMotivationsList.add(motivation);
			}
		}
		return validOrganizationalMotivationsList;
	}

	/**
	 * @param motivation that should have a valid URI as
	 *                    {@code OrganizationalMotivation}.
	 *
	 * @return {@code true} if motivation has non-empty {@code URI} value,
	 *          {@code false} else.
	 */
	public static boolean hasValidOrganizationalMotivation(OrganizationalMotivation motivation) {
		try {
			String codeOfConduct = motivation.getCodeOfConductDescription().getUri();
			if (codeOfConduct.isEmpty()) {
				return false;
			}
			URI uri = new URI(codeOfConduct);
			uri.getClass(); // this is just dummy code for preventing the compiler to remove it
			return true;
		} catch (URISyntaxException | NullPointerException e) {
			return false;
		}
	}

	/**
	 * <p>This function returns a {@code LinkedList} of
	 * {@code LegalAuthorization}s which are all valid. Therefore, it can be
	 * empty. A new list is created in memory instead of reusing the old one. This
	 * allows the parameter to be more generic and still working on O(1) for
	 * adding.
	 *
	 * @param l {@code List} that contains {@code LegalAuthorization}s which
	 *           should be checked.
	 *
	 * @return {@code LinkedList<LegalAuthorization>} with only valid
	 *          authorizations left.
	 */
	@Deprecated
	public static LinkedList<LegalAuthorization> getValidLegalAuthorizationsList(List<LegalAuthorization> l) {
		LinkedList<LegalAuthorization> validLegalAuthorizationsList = new LinkedList<>();
		for (LegalAuthorization authorization : l) {
			if (hasValidLegalAuthorization(authorization)) {
				validLegalAuthorizationsList.add(authorization);
			}
		}
		return validLegalAuthorizationsList;
	}

	/**
	 *
	 *
	 * @param authorization that should have a valid URI as
	 *                       {@code RegulationNorm}.
	 *
	 * @return {@code true} if authorization has non-empty {@code URI} value,
	 *          {@code false} else.
	 */
	public static boolean hasValidLegalAuthorization(LegalAuthorization authorization) {
		try {
			String regulationNorm = authorization.getRegulationNorm().getUri();
			if (regulationNorm.isEmpty()) {
				return false;
			}
			URI uri = new URI(regulationNorm);
			uri.getClass(); // this is just dummy code for preventing the compiler to remove it
			return true;
		} catch (URISyntaxException | NullPointerException e) {
			return false;
		}
	}

	/**
	 *
	 * @param legalAuthorizations      a list of
	 *                                  {@code OrganizationalMotivation}s.
	 * @param organizationalMotivation a list of {@code LegalAuthorization}s.
	 *
	 * @return {@code true} if all {@code LegalAuthorization}s and
	 *          {OrganizationalMotivation}s are accessible and only one of them is
	 *          zero (if any). Also all must contain valid URIs.
	 */
	public static boolean hasValidLegalAuthorizationAndOrganizationalMotivation(List<LegalAuthorization> legalAuthorizations, List<OrganizationalMotivation> organizationalMotivation) {

		boolean valid = true;
		if (legalAuthorizations.isEmpty()) {
			if (organizationalMotivation.isEmpty()) {
				valid = false;
			}
		}
		for (LegalAuthorization authorization : legalAuthorizations) {
			if (!hasValidLegalAuthorization(authorization)) {
				valid = false;
				break;
			}
		}
		for (OrganizationalMotivation motivation : organizationalMotivation) {
			if (!hasValidOrganizationalMotivation(motivation)) {
				valid = false;
				break;
			}
		}
		return valid;
	}
}
