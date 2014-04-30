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

import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOAlgorithmType;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOEnforcingSystemType;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCORuleType;
import de.uni_koblenz.aggrimm.icp.policyProcessing.inFOParser.externTypes.SEFCOURLContentType;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.base.Control;
import de.uni_koblenz.aggrimm.icp.info.model.technical.control.entity.InFOIdentifier;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.AllowingFlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.AllowingFlowControlRuleSituation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.DenyingFlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.DenyingFlowControlRuleSituation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlMetaPolicyMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlMetaPolicySituation;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleMethod;
import de.uni_koblenz.aggrimm.icp.info.model.technical.flow.define.FlowControlRuleSituation;
import de.uni_koblenz.aggrimm.icp.info.parser.technical.TechnicalParser;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.AlgorithmType;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.IExternType;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.INFOType;
import de.uni_koblenz.aggrimm.icp.info.parser.utils.RDFType;
import de.uni_koblenz.aggrimm.icp.triplestore.ResourceWrapper;
import de.uni_koblenz.aggrimm.icp.triplestore.StatementWrapper;
import de.uni_koblenz.aggrimm.icp.triplestore.TripleStore;
import java.util.Iterator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This class extends the TechnicalParser with the ability to parse
 * explicit rules and to add a defaultRule to a parsed MetaPolicy-Instance.
 *
 * @author Erwin Schens (erwinschens@uni-koblenz.de), mruster
 *
 */
public class TechnicalSearchEngineParser extends TechnicalParser {

	private static final Logger LOGGER = Logger.getLogger(TechnicalSearchEngineParser.class.getCanonicalName());

	/**
	 * <p>This method returns the found and matching {@code IExternType}.
	 * {@code INFOType} and {@code AlgorithmType} are tested last to make sure
	 * that SEFCO can override the more generic InFO-Parser implementations.
	 *
	 * @param value an {@code IExternType} {@code enum} value.
	 *
	 * @return {@code IExternType} with matching {@code value} or null.
	 */
	@Override
	protected IExternType getIExternType(String value) {
		if (SEFCORuleType.fromString(value) == null) {
			if (SEFCOAlgorithmType.fromString(value) == null) {
				if (SEFCOEnforcingSystemType.fromString(value) == null) {
					if (SEFCOURLContentType.fromString(value) == null) {
						if (AlgorithmType.fromString(value) == null) {
							if (INFOType.fromString(value) == null) {
								return null;
							} else {
								return INFOType.fromString(value);
							}
						} else {
							return AlgorithmType.fromString(value);
						}
					} else {
						return SEFCOURLContentType.fromString(value);
					}
				} else {
					return SEFCOEnforcingSystemType.fromString(value);
				}
			} else {
				return SEFCOAlgorithmType.fromString(value);
			}
		} else {
			return SEFCORuleType.fromString(value);
		}
	}

	public TechnicalSearchEngineParser(TripleStore tripleStore) {
		super(tripleStore);
	}

	/**
	 * <p>Parses the {@code IExternType} with predicate RDF::type (if it has one)
	 * as an enum of the given subject.
	 *
	 * @param subject to search corresponding info type.
	 *
	 * @return IExternType
	 */
	@Override
	protected IExternType parseExternTypeOfResource(ResourceWrapper subject) {
		Iterator<? extends StatementWrapper> statementIterator = this.tripleStore.listStatements(subject, null, null);

		while (statementIterator.hasNext()) {
			StatementWrapper statement = statementIterator.next();

			if (statement.getObject().isLiteral()) {
				continue;
			}

			if (statement.getPredicate().getURI().equals(RDFType.TYPE.getValue())) {
				IExternType result = getIExternType(statement.getResourceObject().getURI());
				if (result != null) {
					return result;
				}
			}
		}
		LOGGER.log(Level.SEVERE, "Could not parse ExternType of resource: {0}", subject.getURI());
		return null;
	}

	/**
	 * <p>returns a concrete info model entity with given subject type and class
	 * to instantiate. It calls the super method and overwrites the set
	 * {@code IExternType}. This is redundant but easier to maintain.
	 *
	 * @param subject       to retrieve model entity from.
	 * @param type          {@code IExternType} as enum.
	 * @param infoClassType class type to instantiate.
	 *
	 * @return If type is not equal to subject RDF::type then it returns and in
	 *          any other case the matching {@code InFOIdentifier}
	 */
	@Override
	protected InFOIdentifier getInFOIdentifierInstance(ResourceWrapper subject, IExternType type, Class<? extends InFOIdentifier> infoClassType) {
		InFOIdentifier infoInstance = super.getInFOIdentifierInstance(subject, type, infoClassType);
		if (infoInstance != null) {
			infoInstance.setExternType(getIExternType(type.getValue()));
		}
		return infoInstance;
	}

	/**
	 * <p>override super method to parse default rule of meta policy
	 *
	 * @return parsed meta policies.
	 */
	@Override
	public List<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> parseMetaPolicies() {
		List<Control<FlowControlMetaPolicyMethod, FlowControlMetaPolicySituation>> metaPolicies = super.parseMetaPolicies();
		for (Control mp : metaPolicies) {
			this.mapDefaultRule(mp, mp.getControlMethod().getControlMethodStatements());
		}
		return metaPolicies;
	}

	/**
	 * <p>parses the rule type based on its rdf::type and returns an empty rule
	 * instance, this instance needs to be filled by other methods this method
	 * only recognizes rules.
	 *
	 * @param resourceSubject rule subject URI.
	 *
	 * @return Control rule instance or null if rule not found.
	 */
	@Override
	protected Control<? extends FlowControlRuleMethod, ? extends FlowControlRuleSituation> parseRuleType(ResourceWrapper resourceSubject, List<ResourceWrapper> methodStatements, String situationUri) {
		switch ((SEFCORuleType) parseExternTypeOfResource(resourceSubject)) {
			case HASH_VALUE_ALLOWING_RULE_METHOD:
				Control<AllowingFlowControlRuleMethod, AllowingFlowControlRuleSituation> hashValueAllowingRule = new Control<>();
				hashValueAllowingRule.setControlMethod(new AllowingFlowControlRuleMethod());
				hashValueAllowingRule.setControlSituation(new AllowingFlowControlRuleSituation());
				hashValueAllowingRule.getControlMethod().setExternType(SEFCORuleType.HASH_VALUE_ALLOWING_RULE_METHOD);
				hashValueAllowingRule.getControlSituation().setExternType(SEFCORuleType.HASH_VALUE_ALLOWING_RULE_METHOD);
				hashValueAllowingRule.getControlMethod().setUri(resourceSubject.getURI());
				hashValueAllowingRule.getControlSituation().setUri(situationUri);
				mapRuleControl(hashValueAllowingRule, methodStatements);
				return hashValueAllowingRule;
			case HASH_VALUE_BLOCKING_RULE_METHOD:
				Control<DenyingFlowControlRuleMethod, DenyingFlowControlRuleSituation> hashValueDenyingRule = new Control<>();
				hashValueDenyingRule.setControlMethod(new DenyingFlowControlRuleMethod());
				hashValueDenyingRule.setControlSituation(new DenyingFlowControlRuleSituation());
				hashValueDenyingRule.getControlMethod().setExternType(SEFCORuleType.HASH_VALUE_BLOCKING_RULE_METHOD);
				hashValueDenyingRule.getControlSituation().setExternType(SEFCORuleType.HASH_VALUE_BLOCKING_RULE_METHOD);
				hashValueDenyingRule.getControlMethod().setUri(resourceSubject.getURI());
				hashValueDenyingRule.getControlSituation().setUri(situationUri);
				mapRuleControl(hashValueDenyingRule, methodStatements);
				return hashValueDenyingRule;
			case URL_ALLOWING_RULE_METHOD:
				Control<AllowingFlowControlRuleMethod, AllowingFlowControlRuleSituation> urlAllowingRuleSituation = new Control<>();
				urlAllowingRuleSituation.setControlMethod(new AllowingFlowControlRuleMethod());
				urlAllowingRuleSituation.setControlSituation(new AllowingFlowControlRuleSituation());
				urlAllowingRuleSituation.getControlMethod().setExternType(SEFCORuleType.URL_ALLOWING_RULE_METHOD);
				urlAllowingRuleSituation.getControlSituation().setExternType(SEFCORuleType.URL_ALLOWING_RULE_METHOD);
				urlAllowingRuleSituation.getControlMethod().setUri(resourceSubject.getURI());
				urlAllowingRuleSituation.getControlSituation().setUri(situationUri);
				mapRuleControl(urlAllowingRuleSituation, methodStatements);
				return urlAllowingRuleSituation;
			case URL_BLOCKING_RULE_METHOD:
				Control<DenyingFlowControlRuleMethod, DenyingFlowControlRuleSituation> urlBlockingRuleSituation = new Control<>();
				urlBlockingRuleSituation.setControlMethod(new DenyingFlowControlRuleMethod());
				urlBlockingRuleSituation.setControlSituation(new DenyingFlowControlRuleSituation());
				urlBlockingRuleSituation.getControlMethod().setExternType(SEFCORuleType.URL_BLOCKING_RULE_METHOD);
				urlBlockingRuleSituation.getControlSituation().setExternType(SEFCORuleType.URL_BLOCKING_RULE_METHOD);
				urlBlockingRuleSituation.getControlMethod().setUri(resourceSubject.getURI());
				urlBlockingRuleSituation.getControlSituation().setUri(situationUri);
				mapRuleControl(urlBlockingRuleSituation, methodStatements);
				return urlBlockingRuleSituation;
			default:
				LOGGER.log(Level.SEVERE, "Encountered unknown rule element. Its URI was: {0}", resourceSubject.getURI());
		}
		return null;
	}
}
