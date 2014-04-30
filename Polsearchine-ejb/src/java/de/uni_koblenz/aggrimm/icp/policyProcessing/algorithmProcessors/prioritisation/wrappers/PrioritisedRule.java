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
package de.uni_koblenz.aggrimm.icp.policyProcessing.algorithmProcessors.prioritisation.wrappers;

import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;

/**
 * <p>Makes {@code FlowControlRuleMethod} adds a {@code priority}-field. (In
 * earlier versions it also made {@code FlowControlRuleMethod} serialisable,
 * which was implemented in pphi r293 due to request)
 *
 * @author mruster
 */
public class PrioritisedRule extends FlowControlRuleMethod implements Comparable<PrioritisedRule> {

	private static final long serialVersionUID = -3175442848526499113L;
	Integer priority;

	public PrioritisedRule(int priority) {
		super();
		this.priority = priority;
	}

	public PrioritisedRule(FlowControlRuleMethod rule) {
		super();

		setChannel(rule.getChannel());
		setChannelSpecifier(rule.getChannelSpecifier());
		setContent(rule.getContent());
		setContentSpecifiers(rule.getContentSpecifiers());
		setControlMethodStatements(rule.getControlMethodStatements());
		setControlPolicy(rule.getControlPolicy());
		setControlledTopics(rule.getControlledTopics());
		setEnforcingSystem(rule.getEnforcingSystem());
		setExternType(rule.getExternType());
		setPostConditionRule(rule.getPostConditionRule());
		setPreConditionRule(rule.getPostConditionRule());
		setReceiver(rule.getReceiver());
		setReceiverSpecifier(rule.getReceiverSpecifier());
		setResourceStatement(rule.getResourceStatement());
		setResponsibleOperator(rule.getResponsibleOperator());
		setRuleConstraint(rule.getRuleConstraint());
		setRuleDataProvider(rule.getRuleDataProvider());
		setSenderSpecifier(rule.getSenderSpecifier());
		setSenders(rule.getSenders());
		setUri(rule.getUri());

		this.priority = 0;
	}

	public PrioritisedRule() {
		super();
		this.priority = 0;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * <p>This method allows comparing {@code PrioritisedRule}s by their priority
	 * and their priority alone. It can be used for sorting by priority.
	 * <p>Note: this class has a natural ordering that is inconsistent with
	 * equals.
	 *
	 * @param otherRule rule with a higher, lower or the same priority.
	 *
	 * @return 0 if same priority, >0 if otherRule has smaller priority, else
	 *          otherRule has higher priority.
	 */
	@Override
	public int compareTo(PrioritisedRule otherRule) {
		return this.priority.compareTo(otherRule.getPriority());
	}
}
