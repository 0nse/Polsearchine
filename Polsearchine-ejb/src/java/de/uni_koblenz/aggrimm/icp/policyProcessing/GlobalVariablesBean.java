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
package de.uni_koblenz.aggrimm.icp.policyProcessing;

import de.uni_koblenz.aggrimm.icp.facades.local.IGlobalVariablesLocal;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author mruster
 */
@LocalBean
@Singleton
public class GlobalVariablesBean implements Serializable, IGlobalVariablesLocal {

	private static final long serialVersionUID = -8666746193239677140L;
	@Resource(name = "OWL_PATH")
	private String OWL_PATH;
	@Resource(name = "POLICY_FILE_EXTENSION")
	private String POLICY_FILE_EXTENSION;
	@Resource(name = "SEARCH_ENGINE")
	private String SEARCH_ENGINE;
	@Resource(name = "LEGAL_TEXT_PATH")
	private String LEGAL_TEXT_PATH;
	@Resource(name = "SEARCH_ENGINE_URI")
	private String SEARCH_ENGINE_URI;
	private final static Logger LOGGER = Logger.getLogger(GlobalVariablesBean.class.getCanonicalName());

	/**
	 * @return {@code true} if directories existed, had the correct permissions
	 *          and mandatory global variables were set; {@code false} else.
	 */
	@Override
	public boolean checkAll() {
		if (areVariablesNotNull()
				&& isLegalTextReadable()
				&& isOwlPathWritable()
				&& isFileExtensionSupported()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return {@code true} iff {@code OWL_PATH} exists as a directory, is
	 *          readable and writable.
	 */
	@Override
	public boolean isOwlPathWritable() {
		File owlPath = new File(OWL_PATH);
		if (!isADirectory(owlPath)) {
			LOGGER.log(Level.SEVERE, "The OWL path for storing policys could not be found or opened.");
			return false;
		}
		try {
			if (!owlPath.canRead()) {
				throw new java.io.IOException("read");
			}
			if (!owlPath.canWrite()) {
				throw new java.io.IOException("written to");
			}
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "OWL path cannot be {0}. You may want to check the access permissions for the user starting the GF-server.", e.getMessage());
			return false;
		}
	}

	/**
	 * @return {@code true} iff {@code LEGAL_TEXT_PATH} exists as a directory and
	 *          the legal text of currently selected {@code SEARCH_ENGINE} exists
	 *          as readable file.
	 */
	@Override
	public boolean isLegalTextReadable() {
		File legalTextPath = new File(LEGAL_TEXT_PATH);
		if (!isADirectory(legalTextPath)) {
			LOGGER.log(Level.SEVERE, "The legal text path could not be found or opened.");
			return false;
		}
		File legalTextFile = new File(LEGAL_TEXT_PATH + File.separator + SEARCH_ENGINE);
		if (!isAFile(legalTextFile)) {
			LOGGER.log(Level.SEVERE, "The legal text for the currently selected search engine could not be opened. Please make sure that {0} exists and can be read.", legalTextFile);
			return false;
		}
		if (!legalTextFile.canRead()) {
			return false;
		}
		return true;
	}

	/**
	 * @return {@code true} iff mandatory global variables are all set.
	 */
	@Override
	public boolean areVariablesNotNull() {
		if (OWL_PATH == null
				|| POLICY_FILE_EXTENSION == null
				|| SEARCH_ENGINE == null
				|| LEGAL_TEXT_PATH == null
				|| SEARCH_ENGINE_URI == null) {
			LOGGER.log(Level.SEVERE, "Only the IP_RESTRICTION_PATTERN environment entry value may be null. All others must be  non-null values.");
			return false;
		}
		return true;
	}

	/**
	 * <p>This method statically tests for supported file extensions. Those should
	 * be equal to the allowed file extensions for uploading (see
	 * Polsearchine-war/web/backend/index.html).The extensions here follow the
	 * extensions that imply file formats supported by Apache Jena (which is used
	 * by the PPhi-InFO-Parser).
	 *
	 * @see https://jena.apache.org/documentation/io/index.html.
	 *
	 * @return {@code true} if {@code POLICY_FILE_EXTENSION} is supported.
	 */
	public boolean isFileExtensionSupported() {
		String currentExtension = POLICY_FILE_EXTENSION.toLowerCase(Locale.ENGLISH);
		List<String> supportedExtensions = Arrays.asList("owl", "ttl", "nt", "nq", "trig", "rdf");
		boolean isFileExtensionSupported = supportedExtensions.contains(currentExtension);
		if (!isFileExtensionSupported) {
			LOGGER.log(Level.SEVERE, "The file extension {0} is not supported.", POLICY_FILE_EXTENSION);
		}
		return isFileExtensionSupported;
	}

	/**
	 * @param path Path to check.
	 *
	 * @return {@code true} if path exists and is a directory.
	 */
	public boolean isADirectory(File path) {
		return (path.exists() && path.isDirectory());
	}

	/**
	 * @param file Path to file to check.
	 *
	 * @return {@code true} if file exists and is not a directory.
	 */
	public boolean isAFile(File file) {
		return (file.exists() && file.isFile());
	}
}
