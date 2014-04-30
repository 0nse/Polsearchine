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
package de.uni_koblenz.aggrimm.icp.facades.local;

import javax.ejb.Local;

/**
 *
 * @author mruster
 */
@Local
public interface IGlobalVariablesLocal {

	/**
	 * @return {@code true} iff mandatory global variables are all set.
	 */
	boolean areVariablesNotNull();

	/**
	 * @return {@code true} if directories existed, had the correct permissions
	 *          and mandatory global variables were set; {@code false} else.
	 */
	boolean checkAll();

	/**
	 * @return {@code true} iff {@code LEGAL_TEXT_PATH} exists as a directory and
	 *          the legal text of currently selected {@code SEARCH_ENGINE} exists
	 *          as readable file.
	 */
	boolean isLegalTextReadable();

	/**
	 * @return {@code true} iff {@code OWL_PATH} exists as a directory, is
	 *          readable and writable.
	 */
	boolean isOwlPathWritable();
}
