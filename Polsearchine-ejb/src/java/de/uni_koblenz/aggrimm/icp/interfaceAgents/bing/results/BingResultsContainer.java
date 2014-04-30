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
package de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results;

import java.util.LinkedList;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;

/**
 * <p>Bing implementation of {@code IResultsContainer<IResult>}.
 *
 * @author mruster
 * @param <BingResult> {@code BingWebResult} or {@code BingImageResult}.
 */
public class BingResultsContainer<BingResult> extends LinkedList<IResult> implements IResultsContainer<IResult> {

	private static final long serialVersionUID = 5522413806980967621L;
	private long resultsTotal;
	/**
	 * <p>Bing returned offset. It indicates the position from which on results
	 * should be returned. If it is 30 for example, then the first 30 results will
	 * be skipped and results from 31+ will be returned. If
	 * {@code offset>resultsTotal} it is sometimes set back to zero and other
	 * times to a value less than {@code resultsTotal}.
	 */
	private long offset;
	/**
	 * <p>The {@code skip} value is Polsearchine's skip value, which helps
	 * indicating if there are more results or not. The offset is not reliable. We
	 * therefore need the user entered offset to find out whether there are no
	 * more results to deliver.
	 */
	private long skip;

	@Override
	public long getResultsTotal() {
		return resultsTotal;
	}

	@Override
	public void setResultsTotal(long resultsTotal) {
		assert (resultsTotal >= 0);
		this.resultsTotal = resultsTotal;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getSkip() {
		return skip;
	}

	public void setSkip(long skip) {
		assert (skip >= 0);
		this.skip = skip;
	}

	/**
	 * <p>Returns {@code true} if Container is empty and {@code false} else.
	 *
	 * <p>Bing doesn't clearly state when there are no more results. Instead it
	 * defaults to returning the first {@code top} amount of results as if nothing
	 * happened and sets the {@code offset} to 0 or some other illogical value.
	 * Therefore we check if {@code skip} is >= than {@code resultsTotal}. If
	 * {@code resultsTotal} is zero, then no results have been found. This case is
	 * covered by the other check though.
	 *
	 * @return {@code true} if we suspect that Bing is no longer returning new
	 *         results.
	 */
	@Override
	public boolean isEmpty() {
		return (skip >= resultsTotal);
	}
}
