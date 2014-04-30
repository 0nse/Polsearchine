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
import de.uni_koblenz.aggrimm.icp.facades.local.IPolicyProcessingLocal;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author mruster
 */
@Startup
@Singleton
public class InitialisationBean implements Serializable {

	private static final long serialVersionUID = -6502976589774455774L;
	@EJB
	private IGlobalVariablesLocal globalVarBean;
	@EJB
	private IPolicyProcessingLocal parserBean;

	@PostConstruct
	public void initialise() {
		if (globalVarBean.checkAll()) {
			parserBean.processOwlFiles();
		} else {
			throw new RuntimeException("The server could not be started due to previous errors with global variables. See the error log for more information.");
		}

	}
}
