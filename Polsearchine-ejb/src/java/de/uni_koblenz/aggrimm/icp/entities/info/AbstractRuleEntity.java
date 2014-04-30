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
package de.uni_koblenz.aggrimm.icp.entities.info;

import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.ControlledTopicEntity;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.RuleDataProviderEntity;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author mruster
 */
@MappedSuperclass
public abstract class AbstractRuleEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@OneToOne
	private PolicyEntity policy;
	@OneToOne
	private RuleDataProviderEntity ruleDataProvider;
	@OneToMany
	private List<ControlledTopicEntity> controlledTopics;
	private boolean isInformationFlowAllowed;
	/**
	 * <p>JPA does not allow the extensible enum pattern. Therefore we use the URI
	 * workaround.
	 */
	private String contentURI;
	private int priority;

	public String getContentURI() {
		return contentURI;
	}

	public void setContentURI(String contentURI) {
		this.contentURI = contentURI;
	}

	public void setContent(IExternType content) {
		this.contentURI = content.getValue();
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public RuleDataProviderEntity getRuleDataProvider() {
		return ruleDataProvider;
	}

	public void setRuleDataProvider(RuleDataProviderEntity ruleDataProvider) {
		this.ruleDataProvider = ruleDataProvider;
	}

	public List<ControlledTopicEntity> getControlledTopics() {
		return controlledTopics;
	}

	public void setControlledTopics(List<ControlledTopicEntity> controlledTopics) {
		this.controlledTopics = controlledTopics;
	}

	public boolean isInformationFlowAllowed() {
		return isInformationFlowAllowed;
	}

	public void setIsInformationFlowAllowed(boolean isInformationFlowAllowed) {
		this.isInformationFlowAllowed = isInformationFlowAllowed;
	}

	public PolicyEntity getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyEntity policy) {
		this.policy = policy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof AbstractRuleEntity)) {
			return false;
		}
		AbstractRuleEntity other = (AbstractRuleEntity) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.uni_koblenz.aggrimm.icp.entities.info.RuleMethod[ id=" + id + " ]";
	}
}
