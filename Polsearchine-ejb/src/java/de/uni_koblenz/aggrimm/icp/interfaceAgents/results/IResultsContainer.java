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
package de.uni_koblenz.aggrimm.icp.interfaceAgents.results;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>{@code Collection} for {@code IResult} that can store the results total
 * amount.
 *
 * @author mruster
 */
public interface IResultsContainer<IResult> extends Collection<IResult>, Serializable {

	long getResultsTotal();

	void setResultsTotal(long resultsTotal);
}
