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

/**
 *
 * <p>As "Group" is a reserved Java Persistence QL keyword, PolsearchineGroup
 * was chosen as class and table
 *
 * @author mruster
 */
@Entity
public class PolsearchineGroup implements Serializable {

	private static final long serialVersionUID = -3248379639440670528L;
	@Id
	private String name;
	@ManyToMany(mappedBy = "groups")
	private List<PolsearchineUser> users;

	/**
	 * <p>initialises lists.
	 */
	public PolsearchineGroup() {
		this.users = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PolsearchineUser> getUsers() {
		return users;
	}

	public void setUsers(List<PolsearchineUser> users) {
		this.users = users;
	}

	public boolean addUser(PolsearchineUser user) {
		return users.add(user);
	}

	public boolean removeUser(PolsearchineUser user) {
		return users.remove(user);
	}
}
