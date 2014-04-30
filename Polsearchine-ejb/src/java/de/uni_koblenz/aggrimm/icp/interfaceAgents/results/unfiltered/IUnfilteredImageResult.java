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

/**
 *
 * @author mruster
 */
public interface IUnfilteredImageResult extends IUnfilteredResult {

	/**
	 * @return MIME type of image.
	 */
	String getContentType();

	void setContentType(String contentType);

	/**
	 * @return file size in kilobytes with two decimal places as int.
	 */
	double getFileSize();

	void setFileSize(double fileSize);

	/**
	 * @return height in pixels as int.
	 */
	int getHeight();

	void setHeight(int height);

	/**
	 * @return width in pixels as int.
	 */
	int getWidth();

	void setWidth(int width);

	/**
	 * @return URL of the website the image.
	 */
	String getSourceURL();

	void setSourceURL(String sourceURL);

	/**
	 * @return a thumbnail of the image result.
	 */
	IUnfilteredThumbnail getThumbnail();

	void setThumbnail(IUnfilteredThumbnail thumbnail);
}
