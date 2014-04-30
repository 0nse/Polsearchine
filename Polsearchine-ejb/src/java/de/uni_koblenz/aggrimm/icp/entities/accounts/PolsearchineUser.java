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
package de.uni_koblenz.aggrimm.icp.entities.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * <p>As "User" is a reserved SQL-99 keyword, PolsearchineUser was chosen as
 * class and table name.
 *
 * @author mruster
 */
@Entity
public class PolsearchineUser implements Serializable {

	private static final long serialVersionUID = 1331646535186085982L;
	@Id
	private String name;
	@ManyToMany
	@JoinTable(name = "User_Group")
	private List<PolsearchineGroup> groups;
	@NotNull
	private String passwordDigest;

	/**
	 * <p>initialises list.
	 */
	public PolsearchineUser() {
		this.groups = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PolsearchineGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<PolsearchineGroup> groups) {
		this.groups = groups;
	}

	public String getPasswordDigest() {
		return passwordDigest;
	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	public boolean removeGroup(PolsearchineGroup group) {
		return groups.remove(group);
	}

	public boolean addGroup(PolsearchineGroup group) {
		return groups.add(group);
	}
}
