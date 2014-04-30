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
package de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser;

import de.uni_koblenz.aggrimm.icp.info.model.technical.control.base.Control;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.*;
import de.uni_koblenz.aggrimm.icp.triplestore.ResourceWrapper;
import de.uni_koblenz.aggrimm.icp.triplestore.TripleStore;

import java.util.List;

/**
 * <p>This Class is the main-interface to InFO, it provides parsing of the
 * Technical / Organizational / Legal - Regulation Patterns.
 *
 * <p>This class needs a valid wrapped TripleStore (e.g. JenaStore).
 *
 * @see
 * 'http://icp.it-risk.iwvi.uni-koblenz.de/mediawiki/images/c/cf/InfoPatternSystem.png'
 *
 * @author Erwin Schens (erwinschens@uni-koblenz.de), mruster
 */
public class SEFCOParser {

	private TechnicalSearchEngineParser technicalSearchEngineParser;

	public SEFCOParser(TripleStore tripleStore) {
		this.technicalSearchEngineParser = new TechnicalSearchEngineParser(tripleStore);
	}

	/**
	 * <p>Add valid inFO-owl files to the TripleStore
	 *
	 * @param fileLoc as URI or with prefix 'file://'
	 */
	public void addFile(String fileLoc) {
		this.technicalSearchEngineParser.getTripleStore().addFile(fileLoc);
	}

	/**
	 * <p>Method returns a list of all parsed MetaPolicies containig Policies
	 * which contains concrete Rules.
	 *
	 * <p>Linking between MetaPolicy - Policy - Rule is also provided by this
	 * method.
	 *
	 * @return List of parsed MetaPolicy Instances.
	 */
	public List<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> getMetaPolicies() {

		List<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> metaPolicies = this.technicalSearchEngineParser.parseMetaPolicies();
		List<Control<FlowControlPolicyMethod, FlowControlPolicySituation>> policies = this.technicalSearchEngineParser.parsePolicies();
		List<Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation>> rules = this.technicalSearchEngineParser.parseRules();

		linkMetaPoliciesToPoliciesToRules(metaPolicies, policies, rules);
		return metaPolicies;
	}

	private void linkMetaPoliciesToPoliciesToRules(List<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> metaPolicies,
																								 List<Control<FlowControlPolicyMethod, FlowControlPolicySituation>> policies,
																								 List<Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation>> rules) {
		//link metapolicies to policies
		for (Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation> mp : metaPolicies) {

			for (ResourceWrapper metaPolicyMethodStatement : mp.getControlMethod().getControlMethodStatements()) {
				for (Control<FlowControlPolicyMethod, FlowControlPolicySituation> p : policies) {
					if (p.getControlMethod().getUri().equals(metaPolicyMethodStatement.getURI())) {
						//found link
						p.getControlMethod().setMetaPolicy(mp);
						mp.getControlMethod().getControlPolicies().add(p);
					}
				}
			}
		}
		//link policies to rules
		for (Control<FlowControlPolicyMethod, FlowControlPolicySituation> p : policies) {

			for (ResourceWrapper policyMethodStatement : p.getControlMethod().getControlMethodStatements()) {
				for (Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation> r : rules) {
					if (r.getControlMethod().getUri().equals(policyMethodStatement.getURI())) {
						//found link
						p.getControlMethod().getFlowControlRules().add(r);
						r.getControlMethod().setControlPolicy(p);
					}
				}
			}
		}
	}
}
