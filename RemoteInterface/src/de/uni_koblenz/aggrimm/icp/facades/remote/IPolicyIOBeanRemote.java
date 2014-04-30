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
package de.uni_koblenz.aggrimm.icp.facades.remote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.ejb.Remote;

/**
 *
 * @author mruster
 */
@Remote
public interface IPolicyIOBeanRemote {

	public byte uploadFile(String fileContent, String fileName) throws IOException;

	public ArrayList<File> getStoredPolicies();

	public boolean deletePolicy(String policyFileName);
}
