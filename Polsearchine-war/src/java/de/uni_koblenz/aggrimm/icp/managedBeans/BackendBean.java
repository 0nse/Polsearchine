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
package de.uni_koblenz.aggrimm.icp.managedBeans;

import de.uni_koblenz.aggrimm.icp.facades.remote.IPolicyIOBeanRemote;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author mruster
 */
@ManagedBean
@RequestScoped
public class BackendBean implements Serializable {

	private static final long serialVersionUID = 1526102590035269771L;
	@EJB
	private IPolicyIOBeanRemote policyIOBeanRemote;
	@Resource(name = "OWL_PATH")
	private String OWL_PATH;
	@Resource(name = "POLICY_FILE_EXTENSION")
	private String POLICY_FILE_EXTENSION;
	@Resource(name = "SEARCH_ENGINE")
	private String SEARCH_ENGINE;
	@Resource(name = "IP_RESTRICTION_PATTERN")
	private String IP_RESTRICTION_PATTERN;
	@Resource(name = "LEGAL_TEXT_PATH")
	private String LEGAL_TEXT_PATH;
	@Resource(name = "SEARCH_ENGINE_URI")
	private String SEARCH_ENGINE_URI;
	@ManagedProperty(value = "#{param.fileName}")
	private String deletablePolicy;
	private final static Logger LOGGER = Logger.getLogger(BackendBean.class.getCanonicalName());

	/**
	 * Creates a new instance of BackendBean
	 */
	public BackendBean() {
	}

	/**
	 * <p>Obtains file to upload and lets it be saved.
	 *
	 * <p>Primefaces uses Apache Commons FileUpload:
	 * Apache Commons FileUpload
	 * Copyright 2002-2010 The Apache Software Foundation
	 *
	 * This product includes software developed by
	 * The Apache Software Foundation ({@link http://www.apache.org/}).
	 * <p>Primefaces also uses Apache Commons IO:
	 * Apache Commons IO
	 * Copyright 2002-2012 The Apache Software Foundation
	 *
	 * This product includes software developed by
	 * The Apache Software Foundation ({@link http://www.apache.org/}).
	 *
	 * @param event from PrimeFaces (hopefully) containing the file to be
	 *               uploaded.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		try {
			/**
			 * <p>This conversion to String has to be done due to the fact that
			 * InputStreams are not serializable.
			 */
			String fileContent = IOUtils.toString(file.getInputstream(), "UTF-8");
			try {
				byte httpStatusCode = policyIOBeanRemote.uploadFile(fileContent, fileName);

				switch (httpStatusCode) {
					case ((byte) 400):
						this.addMessage(FacesMessage.SEVERITY_ERROR, "Upload was cancelled", "The file name could not be read");
						break;
					case ((byte) 201):
						this.addMessage(FacesMessage.SEVERITY_INFO, "New file created", "Refresh the page to see changes.");
						break;
					case ((byte) 404):
						this.addMessage(FacesMessage.SEVERITY_ERROR, "File not found", "You either did not specify a file to upload or are trying to upload a file to a protected or nonexistent location.");
						break;
					default:
				}
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE, "There was an IO error while trying to write the file input stream String of: {0}", fileName);
				this.addMessage(FacesMessage.SEVERITY_ERROR, "File could probably not be written", "The file output stream could not write, flush or be closed properly.");
			}
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, "There was an IO error while trying to read the file input stream of: {0}", fileName);
			this.addMessage(FacesMessage.SEVERITY_ERROR, "File could not be read", "The file input stream could not be properly opened/read.");

		}
	}

	/**
	 * <p>This method is just a shortcut for adding a FacesMessage.
	 *
	 * @param status type of message like INFO, WARN, ERROR or FATAL.
	 * @param title  bold formatted title of the message.
	 * @param msg    longer message.
	 */
	private void addMessage(FacesMessage.Severity status, String title, String msg) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(status, title, msg));
	}

	/**
	 * <p>Set deletable policy for two step removal.
	 *
	 * @param deletablePolicy name of policy to delete.
	 */
	public void setDeletablePolicy(String deletablePolicy) {
		this.deletablePolicy = deletablePolicy;
	}

	public ArrayList<File> getStoredPolicies() {
		return policyIOBeanRemote.getStoredPolicies();
	}

	public void deletePolicy() {
		policyIOBeanRemote.deletePolicy(deletablePolicy);
	}

	public String getOwlPath() {
		assert (OWL_PATH != null);
		return OWL_PATH;
	}

	public String getSearchEngine() {
		assert (SEARCH_ENGINE != null);
		return SEARCH_ENGINE;
	}

	public String getPolicyFileExtension() {
		assert (POLICY_FILE_EXTENSION != null);
		return POLICY_FILE_EXTENSION;
	}

	public String getIPRestrictionPattern() {
		return (IP_RESTRICTION_PATTERN == null) ? "none" : IP_RESTRICTION_PATTERN;
	}

	public String getLegalTextPath() {
		assert (LEGAL_TEXT_PATH != null);
		return LEGAL_TEXT_PATH;
	}

	public String getSearchEngineURI() {
		assert (SEARCH_ENGINE_URI != null);
		return SEARCH_ENGINE_URI;
	}
}
