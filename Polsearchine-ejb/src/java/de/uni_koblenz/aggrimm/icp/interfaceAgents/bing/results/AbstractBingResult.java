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

import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredResult;

/**
 * <p>This class implements {@code IUnfilteredResult} and adds a
 * {@code bingID}-attribute.
 *
 * <p>Attribute descriptions are mostly taken from the official Bing API schema
 * descriptions.
 *
 * @author mruster
 */
public class AbstractBingResult implements IUnfilteredResult {

	private static final long serialVersionUID = 1L;
	/**
	 * <p>Bing returned identifier.
	 */
	protected String bingID;
	/**
	 * <p>The shortened URL that is being displayed. For example <a
	 * href="http://www.scai.fraunhofer.de/fileadmin/ArbeitsgruppeTrottenberg/SS08/seminar/hempel_vortrag.pdf">www.scai.fraunhofer.de/fileadmin/ArbeitsgruppeTrottenberg/SS08/...</a>.
	 */
	protected String displayURL;
	/**
	 * <p>URL of the full-size image or web result.
	 */
	protected String url;
	/**
	 * <p>Title of the result.
	 */
	protected String title;

	public String getBingID() {
		return bingID;
	}

	public void setBingID(String bingID) {
		this.bingID = bingID;
	}

	@Override
	public String getDisplayURL() {
		return displayURL;
	}

	@Override
	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}
}
