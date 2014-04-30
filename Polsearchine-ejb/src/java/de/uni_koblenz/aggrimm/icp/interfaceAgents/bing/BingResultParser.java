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
package de.uni_koblenz.aggrimm.icp.interfaceAgents.bing;

import de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval.IBingResultParserLocal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.AbstractResultParser;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results.BingImageResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results.BingResultsContainer;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results.BingThumbnail;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results.BingWebResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author mruster
 */
@LocalBean
@Stateless
public class BingResultParser extends AbstractResultParser implements IBingResultParserLocal {

	public BingResultParser() {
	}

	/**
	 * @param o      a parsed Bing bingResult string parsed to a JSONObject.
	 * @param source from where the results are ("web" or "image").
	 * @param skip   integer of manually skipped values - needed
	 *                for {@code isEmpty()}.
	 *
	 * @return {@code BingResultsContainer} with all extracted BingResults and
	 *          set {@code resultsTotal} and {@code offset}.
	 *
	 * @throws IllegalArgumentException when {@code source>} is unknown.
	 */
	@Override
	public BingResultsContainer<IUnfilteredResult> convertToResultContainer(JSONObject o, String source, int skip) {
		source = Character.toUpperCase(source.charAt(0)) + source.substring(1);
		o = (JSONObject) o.get("d");
		JSONArray unwrappedJSONArray = (JSONArray) o.get("results");
		o = (JSONObject) unwrappedJSONArray.get(0);
		JSONArray a = (JSONArray) o.get(source);

		BingResultsContainer<IUnfilteredResult> l = new BingResultsContainer<>();
		areInformationFlowsAllowed = databaseQueryHelper.doesDefaultRuleAllowInformationFlows();
		switch (source) {
			case "Web": {
				for (Object resultObject : a) {
					JSONObject result = (JSONObject) resultObject;
					l.add(extractBingWebResult(result));
				}
				break;
			}
			case "Image": {
				for (Object resultObject : a) {
					JSONObject result = (JSONObject) resultObject;
					l.add(extractBingImageResult(result));
				}
				break;
			}
			default:
				throw new IllegalArgumentException("source parameter was unknown: "
																					 + source);
		}

		l.setResultsTotal(getIntValue(o, source + "Total"));
		l.setOffset(getIntValue(o, source + "Offset"));
		l.setSkip(skip);

		return l;
	}

	/**
	 * @param o {@code JSONObject} which contains a JSON encoded
	 *           {@code BingWebResult}.
	 *
	 * @return {@code BingWebResult} from {@code o}; values can be empty Strings
	 *          or {@code FilteredWebResult}.
	 */
	private IResult extractBingWebResult(JSONObject o) {
		String url = getValue(o, "Url");
		IResult result = createFilteredResultIfNeeded(url, false);

		if (result == null) {
			BingWebResult bingResult = new BingWebResult();
			bingResult.setDescription(getValue(o, "Description"));
			bingResult.setDisplayURL(getValue(o, "DisplayUrl"));
			bingResult.setBingID(getValue(o, "ID"));
			bingResult.setTitle(getValue(o, "Title"));
			bingResult.setUrl(url);
			result = bingResult;
		}
		return result;
	}

	/**
	 * @param o {@code JSONObject} which contains a JSON encoded
	 *           {@code BingImageResult}.
	 *
	 * @return {@code BingImageResult} from {@code o}; values can be empty Strings
	 *          or {@code FilteredImageResult}
	 */
	private IResult extractBingImageResult(JSONObject o) {
		String mediaURL = getValue(o, "MediaUrl");
		IResult result = createFilteredResultIfNeeded(mediaURL, true);

		if (result == null) {
			BingImageResult bingResult = new BingImageResult();
			bingResult.setContentType(getValue(o, "ContentType"));
			bingResult.setDisplayURL(getValue(o, "DisplayUrl"));
			bingResult.setUrl(mediaURL);
			bingResult.setSourceURL(getValue(o, "SourceUrl"));
			bingResult.setBingID(getValue(o, "ID"));
			bingResult.setTitle(getValue(o, "Title"));
			bingResult.setHeight(getIntValue(o, "Height"));
			bingResult.setWidth(getIntValue(o, "Width"));
			bingResult.setFileSizeFromBytes(getIntValue(o, "FileSize"));

			BingThumbnail t = extractBingThumbnail((JSONObject) o.get("Thumbnail"));
			bingResult.setThumbnail(t);
			result = bingResult;
		}
		return result;
	}

	/**
	 * @param o {@code JSONObject} which contains a JSON encoded
	 *           {@code BingThumbnail}.
	 *
	 * @return BingThumbnail from {@code o}; values can be empty Strings.
	 */
	private BingThumbnail extractBingThumbnail(JSONObject o) {
		BingThumbnail thumbnail = new BingThumbnail();
		thumbnail.setUrl(getValue(o, "MediaUrl"));
		thumbnail.setHeight(getIntValue(o, "Height"));
		thumbnail.setWidth(getIntValue(o, "Width"));
		thumbnail.setFileSize(getIntValue(o, "FileSize"));
		return thumbnail;
	}
}
