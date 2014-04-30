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

import de.uni_koblenz.aggrimm.icp.facades.remote.ISearchEngineVariablesRemote;
import de.uni_koblenz.aggrimm.icp.facades.remote.IFileTypeSearchBeanRemote;
import de.uni_koblenz.aggrimm.icp.logic.resultClasses.FileTypeResult;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * <p>Bean for checking if a search term contained a known file type.
 *
 * @author mruster
 */
@Stateless
public class FileTypeSearchBean implements IFileTypeSearchBeanRemote {

	@EJB
	private ISearchEngineVariablesRemote searchEngineVariablesRemote;

	/**
	 * <p>Checks if there is a known file type in the search query. If so,
	 * extracts the file type and returns a special class that then contains the
	 * found file type as well as the rest of the input String.
	 *
	 * @param searchTerm input that MIGHT contain a known file type.
	 *
	 * @return object that can contain the found file type and the remaining
	 *          {@code String}.
	 */
	@Override
	public FileTypeResult findKnownFileTypeIfAny(String searchTerm) {
		String searchTermUpper = searchTerm.toUpperCase(Locale.ENGLISH);
		String searchTermRest = "";
		boolean found = false;

		for (String fileType : searchEngineVariablesRemote.getKnownFileTypes()) {
			if (searchTermUpper.startsWith(fileType + ' ')) { // beginning
				found = true;
				searchTermRest = searchTerm.substring(fileType.length() + 1);
			} else if (searchTermUpper.endsWith(' ' + fileType)) { // end
				found = true;
				int fileTypeStartPosition = searchTerm.length() - fileType.length() - 1;
				searchTermRest = searchTerm.substring(0, fileTypeStartPosition);
			} else {
				String fileTypeWithSpaces = ' ' + fileType + ' ';
				if (searchTermUpper.contains(fileTypeWithSpaces)) { // in between
					found = true;
					int fileTypePosition = searchTermUpper.indexOf(fileTypeWithSpaces);
					int fileTypeEndPosition = fileTypePosition + fileTypeWithSpaces.length(); // fileTypePosition + 4
					searchTermRest = searchTerm.substring(0, fileTypePosition) + ' ' + searchTerm.substring(fileTypeEndPosition);
				}
			}
			if (found) {
				return new FileTypeResult(fileType, searchTermRest);
			}
		}

		return new FileTypeResult();
	}
}
