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

import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOAlgorithmType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.define.ControlMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.AbstractRole;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.ResponsibleOperator;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.AlgorithmType;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mruster
 */
public final class SharedValidator {

	private SharedValidator() {
		throw new AssertionError("Tried instantiating a noninstantiable utility class");
	}

	/**
	 *
	 *
	 * @param role tests for known {@code Algorithm} in any Role.
	 *
	 * @return {@code true} if an {@code AlgorithmType} or
	 *          {@code SEFCOAlgorithmType} has been found.
	 */
	public static boolean hasRoleAlgorithm(AbstractRole role) {
		try {
			IExternType algorithm = role.getAlgorithm().getExternType();
			if (algorithm instanceof AlgorithmType) {
				return true;
			}
			if (algorithm instanceof SEFCOAlgorithmType) {
				return true;
			}
			return false;
		} catch (NullPointerException | ClassCastException e) {
			return false;
		}
	}

	/**
	 *
	 * @param l               a list of {@code AbstractRole}s.
	 * @param mustContainRole {@code true} , if 1..*, {@code false} if 0..*.
	 *
	 * @return {@code true} if all {@code AbstractRole}s are accessible and
	 *          {@code mustContainRule} is not validated.
	 */
	public static boolean hasValidRoleAlgorithms(List<? extends AbstractRole> l, boolean mustContainRole) {
		boolean valid = true;
		if (mustContainRole) {
			if (l.isEmpty()) {
				valid = false;
			}
		}
		for (AbstractRole role : l) {
			if (!hasRoleAlgorithm(role)) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	/**
	 * <p>This function returns a {@code LinkedList} of
	 * {@code RulePriorityRole}s which are all valid. Therefore, it can be empty.
	 * A new list is created in memory instead of reusing the old one. This allows
	 * the parameter to be more generic and still working on O(1) for adding.
	 *
	 * @param l {@code List} that contains {@code RulePriorityRole}s which
	 *           should
	 *           be checked.
	 *
	 * @return {@code LinkedList<RulePriorityRole>} with only valid roles left.
	 */
	@Deprecated
	public static LinkedList<? extends AbstractRole> getValidRolesList(List<? extends AbstractRole> l) {
		LinkedList<AbstractRole> validRulePriorityRolesList = new LinkedList<>();
		for (AbstractRole role : l) {
			if (hasRoleAlgorithm(role)) {
				validRulePriorityRolesList.add(role);
			}
		}
		return validRulePriorityRolesList;
	}

	/**
	 * <p>This function checks whether a {@code ControlMethod} has the same
	 * {@code ResponsibleOperator} {@code Agent}-{@code String} as the
	 * {@code ResponsibleOperator} {@code operator}. No checks for accessibility
	 * are done. Therefore you must make sure these values are readable before
	 * calling this method!
	 *
	 * @param c        a {@code ControlMethod} that has a
	 *                  {@code ResponsibleOperator} {@code Agent}-{@code String}.
	 * @param operator {@code ResponsibleOperator} that has a
	 *                  {@code Agent}-{@code String}.
	 *
	 * @return {@code true} if the {@code String} is the same, {@code false}
	 *          else.
	 */
	public static boolean hasMatchingResponsibleOperatorString(ControlMethod c, ResponsibleOperator operator) {
		String cString = c.getResponsibleOperator().getAgent().getUri();
		String operatorString = operator.getAgent().getUri();
		return cString.equals(operatorString);
	}

	/**
	 *
	 *
	 * @param c               {@code ControlMethod} that should be tested for
	 *                         having the correct {@code EnforcingSystem}.
	 * @param enforcingSystem {@code IExternType} which c should be tested for.
	 *
	 * @return {@code true} if {@code EnforcingSystem}'s {@code TechnicalSystem}'s
	 *          {@code IExternType}-{@code String}
	 *          is accessible and matches {@code enforcingSystem}, {@code false}
	 *          else.
	 */
	public static boolean hasMatchingEnforcingSystem(ControlMethod c, IExternType enforcingSystem) {
		try {
			String controlMethodEnforcingSystem = c.getEnforcingSystem().getTechnicalSystem().getExternType().getValue();
			if (controlMethodEnforcingSystem.equals(enforcingSystem.getValue())) {
				return true;
			}
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	/**
	 *
	 *
	 * @param c {@code ControlMethod} that should be tested for having a
	 *           {@code ResponsibleOperator} with a valid Agent's URI.y
	 *
	 * @return {@code true} if {@code ResponsibleOperator}'s
	 *          {@code Agent}-{@code String} is accessible,
	 *          {@code false} else.
	 */
	public static boolean hasResponsibleOperator(ControlMethod c) {
		try {
			new URI(c.getResponsibleOperator().getAgent().getUri());
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
