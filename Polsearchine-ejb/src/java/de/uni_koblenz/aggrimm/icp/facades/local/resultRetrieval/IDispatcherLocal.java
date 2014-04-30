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
package de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval;

import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;
import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface IDispatcherLocal {

	/**
	 * <p>This function is Polsearchine's dispatcher.
	 * It detects the currently used search engine and forwards all necessary
	 * information to the interface agents. As only one search engine is being
	 * used at a time, we spare the extra step of a display mechanism for
	 * merging results. Instead the results are being returned from here to the
	 * later presentation layers. These therefore make up our real display
	 * mechanism.
	 * <p>If {@code SEARCH_ENGINE} is unspecified, Bing will be chosen as a fall
	 * back search engine but a warning message is being logged.
	 *
	 * @param encodedSearchTerm the already URL-encoded search term.
	 * @param source            the source to search. The support depends on the
	 *                           search engines.
	 * @param market            the market to search. The support depends on the
	 *                           search engines.
	 * @param top               the amount of results to return.
	 * @param skip              the amount of results to skip.
	 *
	 * @return {@code IWebResult} if {@code market} is "web"
	 *          and {@code IImageResult} if market is "image".
	 * @throws Exception
	 */
	IResultsContainer<IResult> doSearch(String encodedSearchTerm, String source, String market, int top, int skip) throws Exception;
}
