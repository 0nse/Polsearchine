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
package de.uni_koblenz.aggrimm.icp.entities.info.metaInformation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.MappedSuperclass;

/**
 * <p>This function hosts what the meta information classes all share. In this
 * case it's an URI. As InFO does not have any more precise attributes, this
 * abstract class could also be used as a DTO if it was not abstract.
 * TODO: create DTO classes for extending classes WHEN InFO implements precise
 * attributes!
 *
 * @author mruster
 */
@MappedSuperclass
public abstract class AbstractMetaInformationEntity {

	private String uri;

	public URI getUri() {
		try {
			return new URI(uri);
		} catch (URISyntaxException ex) {
			Logger.getLogger(AbstractMetaInformationEntity.class.getCanonicalName()).log(Level.SEVERE, "A retrieved URI was malformed. This should never happen. The system will NOT operate correctly. The malformed URI was: {0}", new Object[]{uri});
			return null;
		}
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setUri(URI uri) {
		this.uri = uri.toASCIIString();
	}
}
