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

import de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results.BingResultsContainer;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.ejb.Local;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mruster
 */
@Local
public interface IBingRetrieverLocal {

	/**
	 *
	 * <p>Returns the results queried for.
	 *
	 * @param encodedSearchTerm the term to search for.
	 * @param source            where to search.
	 * @param market            for localised results.
	 * @param top               number of results.
	 * @param skip              offset for the starting point of results returned.
	 *
	 * For more details on the parameters, please have a look at:
	 *
	 * @see #createBasicQueryString(java.lang.String, java.lang.String,
	 * java.lang.String, int, int)
	 *
	 * @return {@code IWebResult} if {@code market} is "web"
	 *          and {@code IImageResult} if market is "image" in an
	 *          {@code BingResultsContainer}.
	 * @throws MalformedURLException if {@code createBasicQueryString}'s result
	 *                                cannot be transformed into a URL.
	 * @throws URISyntaxException    if {@code createBasicQueryString}'s result
	 *                                cannot be transformed into a URL.
	 * @throws IOException           if an I/O exception occurs while trying to
	 *                                open the URL connection or while trying to
	 *                                read the results.
	 * @throws ParseException        if the resultString cannot be properly parsed
	 *
	 */
	BingResultsContainer<IResult> doSearch(String encodedSearchTerm, String source, String market, int top, int skip) throws MalformedURLException, URISyntaxException, IOException, ParseException;

	/**
	 * @return file types that Bing supports
	 */
	String[] getKNOWN_FILE_TYPES();

	/**
	 * @return markets that Bing supports
	 */
	String[] getKNOWN_MARKETS();
}
