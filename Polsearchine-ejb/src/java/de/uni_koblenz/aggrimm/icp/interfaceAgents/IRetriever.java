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
package de.uni_koblenz.aggrimm.icp.interfaceAgents;

import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.json.simple.parser.ParseException;

/**
 * <p>This is the interface for all interface agents. They must implement a
 * {@code doSearch}-method for searching as well as {@code getKNOWN_FILE_TYPES}
 * and {@code getKNOWN_MARKETS} for supported file types and markets.
 *
 * @author mruster
 */
public interface IRetriever {

	/**
	 *
	 * <p>Returns the results queried for.
	 *
	 * @param encodedSearchTerm the term to search for pre encoded to remove chars
	 *                           that are not allowed within an URI.
	 * @param source            what to search ('image' or 'web').
	 * @param market            for localised results.
	 * @param top               amount of results.
	 * @param skip              offset for the starting point of results returned.
	 *
	 * @return {@code IWebResult} if {@code market} is "web"
	 *          and {@code IImageResult} if market is "image" in an
	 *          {@code IResultsContainer}.
	 * @throws MalformedURLException if a result's URL cannot be transformed into
	 *                                a URL.
	 * @throws URISyntaxException    if a result's URL cannot be transformed into
	 *                                a URL.
	 * @throws IOException           if an I/O exception occurs while trying to
	 *                                open the URL connection or while trying to
	 *                                read the results.
	 * @throws ParseException        if the resultString cannot be properly
	 *                                parsed.
	 */
	IResultsContainer<IResult> doSearch(String encodedSearchTerm, String source, String market, int top, int skip) throws MalformedURLException, URISyntaxException, IOException, ParseException;

	/**
	 * @return file types that the backend search engine supports. If none is
	 *          supported, an empty array must be returned.
	 */
	String[] getKNOWN_FILE_TYPES();

	/**
	 * @return markets that the backend search engine supports. If none is
	 *          supported, an empty array must be returned.
	 */
	String[] getKNOWN_MARKETS();
}
