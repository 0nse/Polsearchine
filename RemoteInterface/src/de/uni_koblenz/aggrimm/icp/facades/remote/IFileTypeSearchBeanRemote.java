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

import de.uni_koblenz.aggrimm.icp.logic.resultClasses.FileTypeResult;
import javax.ejb.Remote;

/**
 *
 * <p>Facelet for accessing remote test for contained known file type.
 *
 * @author mruster
 */
@Remote
public interface IFileTypeSearchBeanRemote {

	/**
	 * <p>Checks if there is a known file type in the search query. If so,
	 * extracts the file type and returns a special class that then contains the
	 * found file type as well as the rest of the input String.
	 *
	 * @param searchInput input that MIGHT contain a known file type.
	 *
	 * @return object that can contain the found file type and the remaining
	 *          {@code String}.
	 */
	FileTypeResult findKnownFileTypeIfAny(String searchInput);
}
