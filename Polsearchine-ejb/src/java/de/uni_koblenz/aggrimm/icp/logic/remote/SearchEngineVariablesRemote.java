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
package de.uni_koblenz.aggrimm.icp.logic.remote;

import de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval.IBingRetrieverLocal;
import de.uni_koblenz.aggrimm.icp.facades.remote.ISearchEngineVariablesRemote;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * <p>This is the implementation of the search engine variables facade for
 * remote access.
 *
 * @see ISearchEngineVariablesRemote
 *
 * @author mruster
 */
@Stateless
public class SearchEngineVariablesRemote implements ISearchEngineVariablesRemote {

	@Resource(name = "SEARCH_ENGINE")
	private String SEARCH_ENGINE;
	@EJB
	private IBingRetrieverLocal bingRetriever;//TODO switch to local facade instead

	/**
	 * @return known markets depending on active search engine or empty array
	 */
	@Override
	public String[] getKnownMarkets() {
		switch (SEARCH_ENGINE) {
			case "bing":
				return bingRetriever.getKNOWN_MARKETS();
			default:
				return new String[0];
		}
	}

	/**
	 * @return known markets depending on active search engine or empty array
	 */
	@Override
	public String[] getKnownFileTypes() {
		switch (SEARCH_ENGINE) {
			case "bing":
				return bingRetriever.getKNOWN_FILE_TYPES();
			default:
				return new String[0];
		}
	}
}
