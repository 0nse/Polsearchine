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
package de.uni_koblenz.aggrimm.icp.servlets.helper;

/**
 * <p>A simple helper class for creating HTML-tags.
 *
 * @author mruster
 */
public class TagHelper {

	/**
	 * <p>Creates {@code tag> with {@code attributeValues} as {@code
	 * attribute}-attribute. The input is neither being validated nor escaped!
	 *
	 * @param tag             non empty name for tag.
	 * @param attribute       String for attribute to create; use empty String if
	 *                         no
	 *                         attribute should be created.
	 * @param attributeValues String array of attribute values.
	 * @param content         if empty String, the {@code tag} will immediately be
	 *                      closed.
	 *
	 * @return {@code content} embedded in {@code tag} with {@code attributes} as
	 *         class-attribute.
	 */
	public static String createTag(String tag, String attribute, String[] attributeValues, String content) {
		StringBuilder result = openTagWithAttributes(tag, attribute, attributeValues);
		if (content.isEmpty()) {
			result.append(" />");
			return result.toString();
		}
		result.append('>');
		result.append(content);
		result.append("</");
		result.append(tag);
		result.append('>');
		return result.toString();
	}

	/**
	 *
	 * @param tag     to create.
	 * @param content to be inside of tag.
	 *
	 * @return String of <{@code tag}>{@code content}</{@code tag}>.
	 */
	public static String createTag(String tag, String content) {
		return createTag(tag, "", new String[]{}, content);
	}

	/**
	 * <p>This method just redirects to the main {@code createTag}-method allowing
	 * clearer code for not having to create a new String-array when only needing
	 * one attribute value.
	 *
	 * @see #createTag(java.lang.String, java.lang.String, java.lang.String[],
	 * java.lang.String)
	 * @return {@code tag} with {@code content} and {@code attributeValue} for
	 *          {@code attribute}.
	 */
	public static String createTag(String tag, String attribute, String attributeValue, String content) {
		return createTag(tag, attribute, new String[]{attributeValue}, content);
	}

	/**
	 * <p>This method just redirects to the main {@code createTag}-method sparing
	 * the caller to explicitly declare empty {@code content}.
	 *
	 * @see #createTag(java.lang.String, java.lang.String, java.lang.String[],
	 * java.lang.String)
	 * @return {@code tag} without {@code content} and {@code attributeValues} for
	 *          {@code attribute}.
	 */
	public static String createTag(String tag, String attribute, String[] attributeValues) {
		return createTag(tag, attribute, attributeValues, "");
	}

	/**
	 * <p>This method just redirects to the main {@code createTag}-method sparing
	 * the caller to explicitly declare empty {@code content} and creating a
	 * String-array with only one value.
	 *
	 * @see #createTag(java.lang.String, java.lang.String, java.lang.String[],
	 * java.lang.String)
	 * @return {@code tag} without {@code content} and {@code attributeValue} for
	 *          {@code attribute}.
	 */
	public static String createTag(String tag, String attribute, String attributeValue) {
		return createTag(tag, attribute, new String[]{attributeValue}, "");
	}

	/**
	 * <p>This method just redirects to the main {@code createOpeningTag}-method
	 * sparing the caller to explicitly declare a String-array with just one
	 * {@code attributeValue}.
	 *
	 * @see #createOpeningTag(java.lang.String, java.lang.String,
	 * java.lang.String[])
	 * @return unclosed "<{@code tag} {@code attribute}\"{@code
	 *          attributeValue}\"".
	 */
	public static String createOpeningTag(String tag, String attribute, String attributeValue) {
		return createOpeningTag(tag, attribute, new String[]{attributeValue});
	}

	/**
	 * @see #openTagWithAttributes(java.lang.String, java.lang.String,
	 * java.lang.String[])
	 * @return closed opening {@code tag} with {@code attribute} and its
	 *         {@code attributeValues}.
	 */
	public static String createOpeningTag(String tag, String attribute, String[] attributeValues) {
		return openTagWithAttributes(tag, attribute, attributeValues).append('>').toString();
	}

	/**
	 *
	 * @param tag             name of tag to open.
	 * @param attribute       attribute to insert into opening tag.
	 * @param attributeValues attribute's values to insert.
	 *
	 * @return unclosed "<{@code tag} {@code attribute}\"{@code
	 *          attributeValues}\"".
	 */
	private static StringBuilder openTagWithAttributes(String tag, String attribute, String[] attributeValues) {
		StringBuilder result = new StringBuilder("<");
		result.append(tag);
		if (!attribute.isEmpty()) {
			result.append(' ');
			result.append(createAttributeString(attribute, attributeValues));
		}
		return result;
	}

	/**
	 *
	 * @param attributes valid Strings for class-attributes without spaces or
	 *                    duplicates.
	 *
	 * @return String with {@code classes} space-separated in "{@code name}=""".
	 */
	public static String createAttributeString(String name, String[] attributes) {
		StringBuilder result = new StringBuilder(name);
		result.append("=\"");
		for (String s : attributes) {
			result.append(s).append(' ');
		}
		return result.toString().trim() + '"';
	}
}
