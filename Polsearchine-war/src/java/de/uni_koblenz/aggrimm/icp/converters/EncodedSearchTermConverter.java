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
package de.uni_koblenz.aggrimm.icp.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * <p>A simple two-way search term converter for encoding the input according to
 * RFC 3986.
 *
 * @see http://tools.ietf.org/html/rfc3986
 *
 * @author mruster
 */
@FacesConverter(value = "encodedSearchTermConverter")
public class EncodedSearchTermConverter implements Converter {

	private final String[] RESERVED_CHARS = {"!", "#", "$", "&", "'", "(", ")", "*", "+", ",", "/", ":", ";", "=", "?", "@", "[", "]"};
	private final String[] ENCODED_CHARS = {"%21", "%23", "%24", "%26", "%27", "%28", "%29", "%2A", "%2B", "%2C", "%2F", "%3A", "%3B", "%3D", "%3F", "%40", "%5B", "%5D"};

	/**
	 * <p>Encodes reserved characters to percent encoding according to RFC 3986.
	 *
	 * @param context
	 * @param component
	 * @param value     the String that should be returned as an encoded String.
	 *
	 * @return the encoded input.
	 */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || ((String) value).isEmpty()) {
			return null;
		}
		for (int i = 0; i < RESERVED_CHARS.length; i++) {
			value = value.replace(RESERVED_CHARS[i], ENCODED_CHARS[i]);
		}
		return value;
	}

	/**
	 * <p>Returns search term string encoded to its former state.
	 *
	 * <p>Be aware that this function is very basic and pretty easy to manipulate;
	 * i.e. if you search for "%21" you will see "!" afterwards.
	 *
	 * @param context
	 * @param component
	 * @param value     the search term String
	 *
	 * @return value as normal String.
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String result = value.toString();
		for (int i = 0; i < RESERVED_CHARS.length; i++) {
			result = result.replace(ENCODED_CHARS[i], RESERVED_CHARS[i]);
		}
		return result;
	}
}
