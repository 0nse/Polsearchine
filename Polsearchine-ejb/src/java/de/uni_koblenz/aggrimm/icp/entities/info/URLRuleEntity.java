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
package de.uni_koblenz.aggrimm.icp.entities.info;

import java.net.URI;
import javax.persistence.Entity;

/**
 *
 * @author mruster
 */
@Entity
public class URLRuleEntity extends AbstractRuleEntity {

	private static final long serialVersionUID = 1079157990301982855L;
	private String regionURL;

	public String getRegionURI() {
		return regionURL;
	}

	/**
	 * @param regionURI URI which will be set with {@code toASCIIString}.
	 */
	public void setRegionURI(URI regionURI) {
		this.regionURL = regionURI.toASCIIString();
	}

	public void setRegionURI(String regionURI) {
		this.regionURL = regionURI;
	}
}
