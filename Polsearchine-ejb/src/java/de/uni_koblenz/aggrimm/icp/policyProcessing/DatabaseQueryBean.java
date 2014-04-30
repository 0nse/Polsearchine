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

import de.uni_koblenz.aggrimm.icp.facades.local.database.IDatabaseQueryLocal;
import de.uni_koblenz.aggrimm.icp.entities.info.AbstractRuleEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.DefaultRuleEntity;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * <p>This bean reads from the database.
 *
 * @author mruster
 */
@LocalBean
@Stateless
public class DatabaseQueryBean implements Serializable, IDatabaseQueryLocal {

	private static final long serialVersionUID = -7059641486997748225L;
	@PersistenceContext(unitName = "Polsearchine-ejbPU")
	private EntityManager entityManager;
	private final static Logger LOGGER = Logger.getLogger(DatabaseQueryBean.class.getCanonicalName());

	/**
	 * @return {@code true} if default rule allows informations flows;
	 *          {@code false} else.
	 */
	@Override
	public boolean doesDefaultRuleAllowInformationFlows() {
		DefaultRuleEntity dre = entityManager.createQuery("SELECT dre FROM DefaultRuleEntity dre", DefaultRuleEntity.class).
						getSingleResult();
		return dre.isIsAllowingDefaultRule();
	}

	/**
	 * <p>If a {@code WEB_PAGE} is an exact match to the {@code url} then it must
	 * be the most concrete (and highest prioritised) matching rule. There also
	 * may only be one {@code WEB_SITE} that fits the URL's region AND has the
	 * highest priority. If you cannot guarantee these two assumptions, you have
	 * created the database wrong.
	 *
	 * @param url which is used to find a matching regulating rule.
	 *
	 * @return matching regulating rule. If none is found {@code null} is
	 *          returned.
	 */
	@Override
	public AbstractRuleEntity getRegulatingRule(String url) throws NoResultException {
		try {
			return entityManager.createQuery(
							"SELECT ure FROM URLRuleEntity ure"
							+ " WHERE ure.contentURI = :contentType AND  ure.regionURL = :url", AbstractRuleEntity.class).
							setParameter("contentType", SEFCOURLContentType.WEB_PAGE.getValue()).
							setParameter("url", url).
							getSingleResult();
		} catch (NoResultException ne1) {
			try {
				return entityManager.createQuery(
								"SELECT ure FROM URLRuleEntity ure"
								+ " WHERE ure.contentURI = :contentType AND  :currentURL LIKE CONCAT(ure.regionURL, '%')"
								+ " AND LENGTH(ure.regionURL) <= LENGTH(:currentURL)"
								+ " ORDER BY ure.priority DESC", AbstractRuleEntity.class).
								setParameter("contentType", SEFCOURLContentType.WEB_SITE.getValue()).
								setParameter("currentURL", url).
								setMaxResults(1).
								getSingleResult();
			} catch (NoResultException ne2) {
				// Throwing a NoResultException here would seem quite reasonable.
				// The problem with this approach is that this would cause a
				// TransactionRolledbackLocalException to be thrown.
				return null;
			}
		}
	}
}
