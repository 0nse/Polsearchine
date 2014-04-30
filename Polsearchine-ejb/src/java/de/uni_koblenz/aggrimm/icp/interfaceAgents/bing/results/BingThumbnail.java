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

import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredThumbnail;

/**
 * <p>Bing implementation of {@code IUnfilteredThumbnail}.
 *
 * @author mruster
 */
public class BingThumbnail implements IUnfilteredThumbnail {

	private static final long serialVersionUID = 573205052985072111L;
	/**
	 * <p>called "MediaUrl" in the returned Bing JSON links to the thumbnail image
	 * that should be displayed.
	 */
	private String url;
	private int fileSize;
	private int height;
	private int width;

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
