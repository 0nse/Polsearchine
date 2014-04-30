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
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredResult;
import javax.ejb.Local;
import org.json.simple.JSONObject;

/**
 *
 * @author mruster
 */
@Local
public interface IBingResultParserLocal {

	/**
	 * @param o      a parsed Bing bingResult string parsed to a JSONObject.
	 * @param source from where the results are ("web" or "image").
	 * @param skip   integer of manually skipped values - needed
	 *                for {@code isEmpty()}.
	 *
	 * @return {@code BingResultsContainer} with all extracted BingResults and
	 *          set {@code resultsTotal} and {@code offset}.
	 *
	 * @throws IllegalArgumentException when {@code source>} is unknown.
	 */
	BingResultsContainer<IUnfilteredResult> convertToResultContainer(JSONObject o, String source, int skip);
}
