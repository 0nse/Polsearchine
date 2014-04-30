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

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>UserLoginBean with methods for managing logged in users.
 * This method is <b>not</b> being used at the moment but serves as inspiration
 * for implementing a form-based login with a managed bean.
 *
 * The code is mostly inspired by some stack overflow answers:
 * {@link http://stackoverflow.com/questions/2206911} (accessed 31.01.2012,
 * 00:30) and {@link http://stackoverflow.com/questions/2180206} (accessed
 * 31.01.2012 13:30)
 *
 * @author mruster
 */
@ManagedBean
@SessionScoped
public class UserLoginBean implements Serializable {

	private static final long serialVersionUID = -5376877993590446427L;
	private String userName;
	private final static Logger LOGGER = Logger.getLogger(BackendBean.class.getCanonicalName());

	public UserLoginBean() {
	}

	public String getUserName() {
		if (userName == null) {
			Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
			if (principal != null) {
				userName = principal.getName();
			}
		}
		return userName;
	}

	public boolean isLoggedIn() {
		return (getUserName() != null);
	}

	public void invalidateSession() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		userName = null;
		FacesContext.getCurrentInstance().responseComplete();
	}
}
