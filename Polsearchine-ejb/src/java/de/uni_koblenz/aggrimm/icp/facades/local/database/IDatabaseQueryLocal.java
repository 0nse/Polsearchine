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
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author mruster
 */
@Local
public interface IDatabaseQueryLocal {

	/**
	 * @return {@code true} if default rule allows informations flows;
	 *          {@code false else}.
	 */
	boolean doesDefaultRuleAllowInformationFlows();

	/**
	 * <p>If a {@code WEB_PAGE} is an exact match to the {@code url} then it must
	 * be the most concrete (and highest prioritised) matching rule. There also
	 * may only be one {@code WEB_SITE} that fits the URL's region AND has the
	 * highest priority. If you cannot guarantee these two assumptions, you have
	 * created the database wrong.
	 *
	 * @param url which is used to find a matching regulating rule.
	 *
	 * @return matching regulating rule. If none is found, it throws.
	 * @throws NoResultException is thrown if there is no matching rule.
	 */
	AbstractRuleEntity getRegulatingRule(String url) throws NoResultException;
}
