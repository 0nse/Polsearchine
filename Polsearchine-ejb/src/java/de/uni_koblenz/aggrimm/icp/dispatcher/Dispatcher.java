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
package de.uni_koblenz.aggrimm.icp.dispatcher;

/**
 * <p>This is Polsearchine's dispatcher.
 *
 * It decides what interface agents to use and forwards all necessary
 * parameters. It then gathers the result and returns it to the presentation
 * layer.
 *
 * @author mruster
 */
import de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval.IDispatcherLocal;
import de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval.IBingRetrieverLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.ejb.EJB;

@Stateless
public class Dispatcher implements Serializable, IDispatcherLocal {

	private final static Logger LOGGER = Logger.getLogger(Dispatcher.class.getCanonicalName());
	private static final long serialVersionUID = -3961711570010266624L;
	@EJB
	private IBingRetrieverLocal bingRetriever;
	@Resource(name = "SEARCH_ENGINE")
	private String SEARCH_ENGINE;

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
	@Override
	public IResultsContainer<IResult> doSearch(String encodedSearchTerm, String source, String market, int top, int skip) throws Exception {
		IResultsContainer<IResult> result;

		assert (SEARCH_ENGINE != null);
		switch (SEARCH_ENGINE) {
			case ("bing"):
				result = bingRetriever.doSearch(encodedSearchTerm, source, market, top, skip);
				break;
			default:
				result = bingRetriever.doSearch(encodedSearchTerm, source, market, top, skip);
				LOGGER.log(Level.WARNING, "Please specify an implemented search engine in web.xml. Falling back to bing.");
		}
		return result;
	}
}
