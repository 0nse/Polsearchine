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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredImageResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredThumbnail;

/**
 * <p>Bing implementation of {@code IUnfilteredImageResult}.
 *
 * <p>Attribute descriptions are mostly taken from the official Bing API schema
 * descriptions.
 *
 * @author mruster
 */
public class BingImageResult extends AbstractBingResult implements IUnfilteredImageResult {

	private static final long serialVersionUID = 7550027079715875631L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	/**
	 * <p>MIME type of an image, if available.
	 */
	private String contentType;
	/**
	 * <p>Height of the full-size image in pixels, if available.
	 */
	private int height;
	/**
	 * <p>Width of the full-size image in pixels, if available.
	 */
	private int width;
	/**
	 * <p>URL of the website that contains a returned image.
	 */
	private String sourceURL;
	/**
	 * <p>Thumbnail properties of the multimedia element.
	 */
	private IUnfilteredThumbnail thumbnail;
	/**
	 * <P>Size of the full-size image file in kilobytes rounded to two decimal
	 * places, if available.
	 */
	private double fileSize;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String getSourceURL() {
		return sourceURL;
	}

	@Override
	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	@Override
	public IUnfilteredThumbnail getThumbnail() {
		return thumbnail;
	}

	@Override
	public void setThumbnail(IUnfilteredThumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Override
	public double getFileSize() {
		return fileSize;
	}

	@Override
	public void setFileSize(double fileSizeInKiloBytes) {
		this.fileSize = fileSizeInKiloBytes;
	}

	/**
	 *
	 * @param fileSizeInBytes integer of bytes that should be converted to
	 *                         kilobytes with two decimal places.
	 */
	public void setFileSizeFromBytes(int fileSizeInBytes) {
		this.fileSize = (Math.round(fileSizeInBytes / 10.24)) / 100D;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof BingImageResult)) {
			return false;
		}
		BingImageResult other = (BingImageResult) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BingImageResult{" + "url=" + url + '}';
	}
}
