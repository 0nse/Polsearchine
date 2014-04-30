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
package de.uni_koblenz.aggrimm.icp.logic.remote;

import de.uni_koblenz.aggrimm.icp.facades.remote.IPolicyIOBeanRemote;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.faces.bean.ManagedProperty;

/**
 * <p>Bean that deals with IO-activity of policy files.
 *
 * Meaning, this Bean can create, retrieve and delete policy files stored on the
 * disk. It is declared Singleton to prevent race conditions which would end up
 * in nasty misbehaviour with IO-actions.
 *
 * @author mruster
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PolicyIOBean implements IPolicyIOBeanRemote, Serializable {

	private static final long serialVersionUID = 6598040698839924489L;
	@Resource(name = "OWL_PATH")
	private String OWL_PATH;
	@Resource(name = "POLICY_FILE_EXTENSION")
	private String POLICY_FILE_EXTENSION;
	@ManagedProperty(value = "#{param.fileName}")
	private final static Logger LOGGER = Logger.getLogger(PolicyIOBean.class.getCanonicalName());

	/**
	 * <p>Obtains a file and creates it if possible under {@code OWL_PATH}.
	 *
	 * The file extension is determined by the globally set
	 * {@code POLICY_FILE_EXTENSION}.
	 * <p>If a file was successfully created, the name
	 * will be logged as INFO. The name itself will be calculated from
	 * {@code fileName} which is only a name suggestion! It will probably be
	 * modified to fit the internal naming scheme.
	 *
	 * @param fileContent String created from an InputStream containing file
	 *                     content to be saved.
	 * @param fileName    the name the file SHOULD have.
	 *
	 * @return byte values that represent HTTP status codes. 201 if created
	 *          successfully, 400 if the file name could not be read and 404 if the
	 *          file was not found within the {@code InputStream}.
	 * @throws IOException if there was a problem while trying to write the file.
	 */
	@Override
	@Lock(LockType.WRITE)
	public byte uploadFile(String fileContent, String fileName) throws IOException {
		OutputStream persistentContent = null;
		try {
			String[] uploadFileName = splitAtExtension(fileName);
			if (uploadFileName.length != 2) {
				return (byte) 400; // Bad request - file name could not be read
			} else {
				String persistentFileName = getAvailableFileName(uploadFileName[0]) + '.' + POLICY_FILE_EXTENSION;
				persistentContent = new FileOutputStream(new File(
								OWL_PATH + File.separator + persistentFileName));

				persistentContent.write(fileContent.getBytes("UTF-8"));
				/* This code is only for dealing with an InputStream
				 int read;
				 final byte[] bytes = new byte[1024];

				 while ((read = fileContent.read(bytes)) != -1) {
				 persistentContent.write(bytes, 0, read);
				 }
				 */
				LOGGER.log(Level.INFO, "{0} uploaded.", persistentFileName);
				return (byte) 201; // Created - successfully created.
			}
		} catch (FileNotFoundException fne) {
			LOGGER.log(Level.INFO, "File not found: {0}", fne);
			if (persistentContent != null) {
				persistentContent.flush();
				persistentContent.close();
			}
			return (byte) 404; // File not found - maybe hacking, maybe just an error
		} finally { // close streams that might have been opened:
			if (persistentContent != null) {
				persistentContent.flush();
				persistentContent.close();
			}
		}
	}

	/**
	 * <p>This method returns policies that are stored within {@code OWL_PATH},
	 * have {@code POLICY_FILE_EXTENSION} as extension, are all lower case and
	 * don't contain any spaces.
	 *
	 * @return {@code ArrayList} of {@code File}s which match the formerly
	 *         mentioned conditions.The files are alphabetically sorted by file
	 *         name.
	 */
	@Override
	@Lock(LockType.READ)
	public ArrayList<File> getStoredPolicies() {
		File dir = new File(OWL_PATH);
		assert (dir.isDirectory());

		ArrayList<File> result = new ArrayList<>();
		File[] content = dir.listFiles();
		Arrays.sort(content);
		for (final File file : content) {
			if (hasExtension(file, POLICY_FILE_EXTENSION)) {
				String fileName = file.toString();
				if (!fileName.contains(" ")) {
					String fileNameLowerCase = fileName.toLowerCase(Locale.ENGLISH);
					if (fileName.equals(fileNameLowerCase)) {
						result.add(file);
					}
				}
			}
		}
		return result;
	}

	/**
	 * <p>Uses the {@code policyFileName} variable to delete the file from disk.
	 * Checks if the file is just the file name (say it does not contain any
	 * separator's to get into another directory) and has the right file
	 * extension.
	 *
	 * @return true if file was deleted, false if could not be deleted or was
	 *          invalid.
	 */
	@Override
	@Lock(LockType.WRITE)
	public boolean deletePolicy(String policyFileName) {
		if (!policyFileName.contains(File.separator)) {
			if (hasExtension(new File(policyFileName), POLICY_FILE_EXTENSION)) {
				File f = new File(OWL_PATH + File.separator + policyFileName);
				boolean isDeleted = f.delete();
				if (isDeleted) {
					LOGGER.log(Level.INFO, "{0} deleted.", policyFileName);
				} else {
					LOGGER.log(Level.WARNING, "{0} could not be deleted", policyFileName);
				}
				return isDeleted;
			}
		}
		return false;
	}

	/**
	 *
	 * @param f         File having a name with a dot separating the extension.
	 * @param extension the extension {@code f} should have. Case sensitive!
	 *
	 * @return true if file has the extension {@code extension}, false else.
	 */
	private boolean hasExtension(File f, String extension) {
		String[] splitFile = splitAtExtension(f.getName());
		if (splitFile.length != 2) {
			return false;
		}
		return (extension.equals(splitFile[1]));
	}

	/**
	 *
	 * @param fileName String with extension as suffix separated by a dot.
	 *
	 * @return String array with index 0 containing the actual file name and 1 the
	 *          file extension in lowercase or an empty String array if invalid.
	 */
	private String[] splitAtExtension(String fileName) {
		int dotPosition = fileName.lastIndexOf('.');
		if (dotPosition == -1) {
			return new String[0];
		} else {
			String[] result = new String[2];
			result[0] = fileName.substring(0, dotPosition);
			result[1] = fileName.substring(dotPosition + 1).toLowerCase(Locale.ENGLISH);
			return result;
		}
	}

	/**
	 *
	 * <p>Finds first free available file name within {@code OWL_PATH} with
	 * {@code POLICY_FILE_EXTENSION}. The name contains an underscore and ends
	 * with the first freely available positive number starting from zero. It will
	 * be in lowercase. Spaces are also converted to underscores.
	 * <p>Example: if the directory is empty and we call this method three times,
	 * with {@code fileName} "Example", we will get:
	 * <ul>
	 * <li>1st call: {@code "example_0"}
	 * <li>2nd call: {@code "example_1"}
	 * <li>3rd call: {@code "example_2"}
	 * </ul>
	 *
	 * @param fileName String containing the name without any extension in it.
	 *
	 * @return first freely available name to be stored under {@code OWL_PATH}
	 *          with {@code POLICY_FILE_EXTENSION} as extension.
	 */
	private String getAvailableFileName(String fileName) {
		final String dottedExtension = '.' + POLICY_FILE_EXTENSION;
		int i = 0;

		fileName = fileName.replace(" ", "_");
		fileName = fileName.toLowerCase(Locale.ENGLISH) + '_';
		String tempFileName = OWL_PATH + File.separator + fileName;

		File f = new File(tempFileName + i + dottedExtension);
		while (f.exists()) {
			i++;
			f = new File(tempFileName + i + dottedExtension);
		}
		return (fileName + i);
	}
}
