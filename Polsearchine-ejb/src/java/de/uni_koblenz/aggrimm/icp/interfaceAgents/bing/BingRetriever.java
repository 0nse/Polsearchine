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

import de.uni_koblenz.aggrimm.icp.interfaceAgents.IRetriever;
import de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval.IBingRetrieverLocal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.json.simple.parser.ParseException;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.bing.results.BingResultsContainer;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import java.nio.charset.Charset;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.DatatypeConverter;

/**
 * <p>Bing Interface Agent that is responsible for retrieving the appropriate
 * results.
 *
 * @author mruster
 */
@LocalBean
@Stateless
public class BingRetriever implements IBingRetrieverLocal, IRetriever {

	@EJB
	private BingResultParser brc;
	/**
	 * <p>This is the API-key needed for using the Bing Search API.
	 */
	@Resource(name = "BING_API_KEY")
	private String API_KEY;
	/**
	 * the list of markets has been parsed from
	 * {@link https://skydrive.live.com/view.aspx?resid=9C9479871FBFA822!109&app=Word}
	 * pages 14-17, on the 1st of December 2012:
	 */
	final static private String[] KNOWN_MARKETS = {"ar-XA", "bg-BG", "cs-CZ", "da-DK", "de-AT", "de-CH", "de-DE", "el-GR", "en-AU", "en-CA", "en-GB", "en-ID", "en-IE", "en-IN", "en-MY", "en-NZ", "en-PH", "en-SG", "en-US", "en-XA", "en-ZA", "es-AR", "es-CL", "es-ES", "es-MX", "es-US", "es-XL", "et-EE", "fi-FI", "fr-BE", "fr-CA", "fr-CH", "fr-FR", "he-IL", "hr-HR", "hu-HU", "it-IT", "ja-JP", "ko-KR", "lt-LT", "lv-LV", "nb-NO", "nl-BE", "nl-NL", "pl-PL", "pt-BR", "pt-PT", "ro-RO", "ru-RU", "sk-SK", "sl-SL", "sv-SE", "th-TH", "tr-TR", "uk-UA", "zh-CN", "zh-HK", "zh-TW"};
	/**
	 * the list of file types has been parsed from
	 * {@link https://skydrive.live.com/view.aspx?resid=9C9479871FBFA822!109&app=Word}
	 * pages 18-19, on the 14th of April 2013:
	 */
	final private String[] KNOWN_FILE_TYPES = {"DOC", "DWF", "FEED", "HTM", "HTML", "PDF", "PPT", "RTF", "TEXT", "TXT", "XLS"};
	private final Logger LOGGER = Logger.getLogger(BingRetriever.class.getCanonicalName());

	/**
	 * @return markets that Bing supports
	 */
	@Override
	public String[] getKNOWN_MARKETS() {
		// making an extra copy prevents exposing of internal array representation:
		String[] result = KNOWN_MARKETS.clone();
		return result;
	}

	/**
	 * @return file types that Bing supports
	 */
	@Override
	public String[] getKNOWN_FILE_TYPES() {
		// making an extra copy prevents exposing of internal array representation:
		String[] result = KNOWN_FILE_TYPES.clone();
		return result;
	}

	/**
	 *
	 * <p>Returns the results queried for.
	 *
	 * @param encodedSearchTerm the term to search for.
	 * @param source            where to search.
	 * @param market            for localised results.
	 * @param top               number of results.
	 * @param skip              offset for the starting point of results returned.
	 *
	 * For more details on the parameters, please have a look at:
	 *
	 * @see #createBasicQueryString(java.lang.String, java.lang.String,
	 * java.lang.String, int, int)
	 *
	 * @return {@code IWebResult} if {@code market} is "web"
	 *          and {@code IImageResult} if market is "image" in an
	 *          {@code BingResultsContainer}.
	 * @throws MalformedURLException if {@code createBasicQueryString}'s result
	 *                                cannot be transformed into a URL.
	 * @throws URISyntaxException    if {@code createBasicQueryString}'s result
	 *                                cannot be transformed into a URL.
	 * @throws IOException           if an I/O exception occurs while trying to
	 *                                open the URL connection or while trying to
	 *                                read the results.
	 * @throws ParseException        if the resultString cannot be properly parsed
	 *                                from.
	 */
	@Override
	public BingResultsContainer<IResult> doSearch(String encodedSearchTerm, String source, String market, int top, int skip) throws MalformedURLException, URISyntaxException, IOException, ParseException {
		String queryString = createBasicQueryString(encodedSearchTerm, source, market, top, skip);
		URL query = new URL(queryString);
		URLConnection queryURLConnection = query.openConnection();

		if (API_KEY == null) {
			throw new IllegalStateException("The API key for bing could not be read from the application.xml. Make sure it was set.");
		}
		byte[] apiRequestBytes = ("ignored:" + API_KEY).getBytes(Charset.forName("UTF-8"));
		String encodedApiKey = DatatypeConverter.printBase64Binary(apiRequestBytes);

		queryURLConnection.setRequestProperty("Authorization", "Basic " + encodedApiKey);
		try (BufferedReader in = new BufferedReader(
						new InputStreamReader(queryURLConnection.getInputStream(), Charset.forName("UTF-8")))) {
			String resultString = in.readLine(); // it's a one-liner, so this is enough
			IResultsContainer<IResult> resultList = brc.parseJSONString(resultString, source, skip);

			assert (resultList instanceof BingResultsContainer<?>);
			return (BingResultsContainer<IResult>) resultList;
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "Bing input stream could not be fetched: {0}", e);
			throw new IOException("Couldn't fetch Bing input stream.");
		}
	}

	/**
	 * <p>Returns the URL for a basic query with {@code input} searching on
	 * {@code source}.
	 *
	 * <p>This methods does not add any further parameters like file type or
	 * latitude. Currently supported
	 * {@code source} types are "web" and "image". Bing also supports "video",
	 * "news" and "spell" as well as combining those.
	 * <p>Additionally this method escapes the {@code input} to RFC 2396 and RFC
	 * 3986 standard.
	 * <p>We also add our options:
	 * <ul>
	 * <li>Sources={@code source} where we fall back to "web" if the value is
	 * neither "image" nor "web".
	 * <li>Adult=off for no adult content filtering, say no pre-filtering.
	 * <li>DisableQueryAlterations for only results matching exactly the input.
	 * <li>DisableHostCollapsing for not removing results that bing thinks are
	 * duplicates (prevents prefiltering).
	 * <li>$market=String as the market to search for; if unknown, en-US is used.
	 * <li>$top=INT for the amount of results; default is 50.
	 * <li>$skip=INT for the amount of results to skip, default is 0.
	 * <li>$format=JSON for JSON output instead of Atom for XML output.
	 * </ul>
	 *
	 * @param input  search query that should be processed - must be escaped
	 *                according to RFC 3986!
	 * @param source to search, currently only 'image' and 'web' supported.
	 * @param market to search (bing default is determined by IP, cookies etc.)
	 * @param skip   amount of results to skip (bing default=0).
	 * @param top    amount of results to fetch (bing default=50).
	 *
	 * @throws MalformedURLException    if {@code input} couldn't be used for a
	 *                                   correct URI.
	 * @throws URISyntaxException       if {@code input} couldn't be used for a
	 *                                   correct URI.
	 * @throws IllegalArgumentException if {@code source} was unknown or
	 *                                   {@code top} or {@code skip} were out of a
	 *                                   logical range.
	 * @return the most basic Bing query URL without any further parameters.
	 */
	private String createBasicQueryString(String input, String source, String market, int top, int skip) throws MalformedURLException, URISyntaxException {
		source = source.toLowerCase(Locale.ENGLISH);
		switch (source) {
			case "web":
			case "image":
				break;
			default:
				throw new IllegalArgumentException("An unsupported source was specified: " + source);
		}
		if (top <= 0 || skip < 0) {
			throw new IllegalArgumentException("top, skip or both were outside of a logical range: top=" + top + ", skip=" + skip);
		}

		String webFileType = "";
		String[] fileTypeInputSplit = input.split("filetype%3A", 2); // split at first found filetype
		if (fileTypeInputSplit.length == 2) { // contains "filetype:" encoded in RFC 3986
			String inputBeforeFiletype = fileTypeInputSplit[0];
			// The second array element will contain the trailing string, which might
			// start with a file type. Therefore, it is being split at the first space:
			fileTypeInputSplit = fileTypeInputSplit[1].split(" ", 2);
			// The first array element will now contain either a possible file type
			// or the whole trailing string (which can still be a file type):
			fileTypeInputSplit[0] = fileTypeInputSplit[0].toUpperCase(Locale.ENGLISH);
			if (Arrays.asList(KNOWN_FILE_TYPES).contains(fileTypeInputSplit[0])) { // is known file type
				webFileType = fileTypeInputSplit[0];
				input = inputBeforeFiletype;
				if (fileTypeInputSplit.length == 2) {
					input += fileTypeInputSplit[1]; // add trailing search input if any
				}
				System.err.println(webFileType + "\n" + input);
			}
		}
		URI uri = new URI("https", "api.datamarket.azure.com", "/Bing/Search/v1/Composite", "Query='" + input, null);
		// add the Apostrophes as RFC 3986 encoded Strings manually because
		// otherwise the URI creation would have handled them wrong.
		String encodedStringURI = uri.toASCIIString().replaceFirst("'", "%27") + "%27";
		// now we add our options:
		if (!"".equals(webFileType)) {
			encodedStringURI += createParameter("WebFileType", webFileType, true);
		}
		if (!Arrays.asList(KNOWN_MARKETS).contains(market)) {
			market = "en-US"; //set default market if parameter value is unknown
		}
		encodedStringURI += createParameter("Sources", source, true);
		encodedStringURI += createParameter("WebSearchOptions", "DisableQueryAlterations%2BDisableHostCollapsing", true);
		encodedStringURI += createParameter("Adult", "Off", true);
		encodedStringURI += createParameter("Market", market, true);
		encodedStringURI += createParameter("top", top, false);
		encodedStringURI += createParameter("skip", skip, false);
		encodedStringURI += createParameter("format", "JSON", false);
		return encodedStringURI;
	}

	private static String createParameter(String name, Object value, boolean isString) {
		if (isString) {
			return "&" + name + "=%27" + value.toString() + "%27";
		}
		return "&$" + name + "=" + value.toString();
	}
}
