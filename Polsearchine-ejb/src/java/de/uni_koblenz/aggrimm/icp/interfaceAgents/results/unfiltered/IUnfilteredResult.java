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
package de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered;

import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import java.io.Serializable;

/**
 * <p>Interface for the most general methods an unfiltered result object will
 * have to offer.
 * The therefore available attributes are obviously implied.
 *
 * @author mruster
 */
public interface IUnfilteredResult extends IResult, Serializable {

	String getDisplayURL();

	String getUrl();

	String getTitle();

	void setDisplayURL(String displayURL);

	void setUrl(String url);

	void setTitle(String title);
}
