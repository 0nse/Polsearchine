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
package de.uni_koblenz.aggrimm.icp.facades.remote;

import javax.ejb.Remote;

/**
 * <p>This interface helps accessing search engine-wide constants.
 * It is not (!) associated with any Entity and is just built for code
 * redundancy reduction and flexibility.
 *
 * @author mruster
 */
@Remote
public interface ISearchEngineVariablesRemote {

	/**
	 *
	 * @return array of Strings with known markets of currently active search
	 *         engine or {@code null} if none known is detected.
	 */
	String[] getKnownMarkets();

	/**
	 *
	 * @return array of Strings with known file types of currently active search
	 *         engine or {@code null} if none known is detected.
	 */
	String[] getKnownFileTypes();
}
