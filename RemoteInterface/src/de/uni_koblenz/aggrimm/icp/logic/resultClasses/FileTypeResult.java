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
package de.uni_koblenz.aggrimm.icp.logic.resultClasses;

import java.io.Serializable;

/**
 * <p>Result class for {@code FileTypeSearchBean}.
 *
 * Contains two Strings as attributes which can be empty if no file type was
 * found.
 *
 * @author mruster
 */
final public class FileTypeResult implements Serializable {

	private static final long serialVersionUID = -3344755250363073980L;
	private String foundFileType;
	private String searchTermRest;

	/**
	 * <p>Indicator for empty attributes. This happens when no file type could be
	 * matched on the input.
	 *
	 * @return {@code true} if both values are empty, {@code false} else.
	 */
	public boolean doesContainFileType() {
		try {
			if (!foundFileType.isEmpty() && !searchTermRest.isEmpty()) {
				return true;
			}
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public FileTypeResult(String foundFileType, String searchTermRest) {
		this.foundFileType = foundFileType;
		this.searchTermRest = searchTermRest;
	}

	public FileTypeResult() {
	}

	public String getFoundFileType() {
		return foundFileType;
	}

	public String getSearchTermRest() {
		return searchTermRest;
	}
}
