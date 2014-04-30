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
package de.uni_koblenz.aggrimm.icp.servlets;

import de.uni_koblenz.aggrimm.icp.servlets.helper.TagHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.uni_koblenz.aggrimm.icp.entities.info.metaInformation.AbstractMetaInformationEntity;
import de.uni_koblenz.aggrimm.icp.facades.local.resultRetrieval.IDispatcherLocal;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.FilteredImageResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.FilteredWebResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredImageResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.IResultsContainer;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.AbstractFilteredResult;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredThumbnail;
import de.uni_koblenz.aggrimm.icp.interfaceAgents.results.unfiltered.IUnfilteredWebResult;
import java.util.List;
import javax.ejb.EJB;

/**
 * <p>Servlet used for retrieving and displaying the results.
 *
 * @author mruster
 */
@WebServlet(name = "ResultsServlet", urlPatterns = {"/ResultsServlet"})
public class ResultsServlet extends HttpServlet {

	private static final long serialVersionUID = 5110370586544559082L;
	@Resource(name = "IP_RESTRICTION_PATTERN")
	private String IP_RESTRICTION_PATTERN;
	private final static Logger LOGGER = Logger.getLogger(ResultsServlet.class.getCanonicalName());
	@EJB
	private IDispatcherLocal dispatcher;

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * <p>Prints results specified by {@code top}, {@code skip}, {@code source}
	 * and {@code encodedSearchTerm}. Disallows the access to IPs not matching the
	 * {@code IP_RESTRICTION_PATTERN}. In this case and in the case that there are
	 * no more results, a div with identifier will be printed. If needed, messages
	 * are printed.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 *
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		final String encodedSearchTerm = request.getParameter("searchTerm");
		final String source = request.getParameter("source");
		final String ipAddress = request.getRemoteAddr();
		try {
			final int top = Integer.parseInt(request.getParameter("top"));
			final int skip = Integer.parseInt(request.getParameter("skip"));
			/**
			 * TODO: this value is NOT unique iff {@code top} is dynamic throughout
			 * all calls in one session. If you plan to vary {@code top} then you have
			 * to choose another way of calculating an ID!
			 */
			long unusedRegulatedResultCountId = skip + top;

			try (PrintWriter out = response.getWriter()) {
				if (IP_RESTRICTION_PATTERN != null
						&& !ipAddress.startsWith(IP_RESTRICTION_PATTERN)) {
					out.println(createEORString());
					out.println(TagHelper.createTag("div",
																					"class",
																					new String[]{"label", "label-warning", "infoBanner"},
																					String.format("Only users whose IP-address starts with \"%s\" may use this search engine. We are very sorry fot that.", IP_RESTRICTION_PATTERN)));
				} else {
					try {
						IResultsContainer<IResult> results = dispatcher.doSearch(encodedSearchTerm, source, "en-US", top, skip);
						if (!results.isEmpty()) {
							for (IResult result : results) {
								switch (source) {
									case "web":
										if (result instanceof FilteredWebResult) {
											processFilteredWebResult(out, (FilteredWebResult) result, unusedRegulatedResultCountId++);
										} else {
											processWebResult(out, (IUnfilteredWebResult) result);
										}
										break;
									case "image":
										if (result instanceof FilteredImageResult) {
											processFilteredImageResult(out, (FilteredImageResult) result);
										} else {
											processImageResult(out, (IUnfilteredImageResult) result);
										}
										break;
									default:
										throw new IllegalArgumentException(String.format("Unknown source \"%s\" modified by %s", source, ipAddress));
								}
							}
						} else {
							out.println(createEORString());
							out.println( // div element marking end of available results
											TagHelper.createTag("div",
																					"class",
																					new String[]{"label", "label-info", "infoBanner"},
																					"There are no more results."));
						}
						// response.setStatus(HttpServletResponse.SC_OK); // this is default

					} catch (Exception ex) {
						out.println(createEORString());
						out.println(
										TagHelper.createTag("div",
																				"class",
																				new String[]{"label", "label-danger", "infoBanner"},
																				"An internal server error occured (500). Most likely, the backend search engine is having problems. Please try again later."));
						// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						LOGGER.log(Level.SEVERE, "An internal server error occured. Most likely, the bing search API gave up returning results.", ex);
					}
				}
				out.close();
			}
		} catch (NumberFormatException e) { // catch JS obvious JS manipulation and don't load any further content.
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			LOGGER.log(Level.WARNING, "JS manipulation detected.\ntop:\t%{0}nskip:\t{1}\nIP:\t{2}", new Object[]{request.getParameter("top"), request.getParameter("skip"), ipAddress});
		}
	}

	/**
	 * <p>This container serves as an indicator for the AJAX scroll function to
	 * stop monitoring scrolling.
	 *
	 * @return empty div container with endOfResults as id.
	 */
	private String createEORString() {
		return TagHelper.createTag("div",
															 "id",
															 "endOfResults");
	}

	/**
	 *
	 * @param out     active {@code PrintWriter} that will be returned to the
	 *                 caller for output or further processing.
	 * @param results the image result to process.
	 *
	 * @return {@code PrintWriter} with HTML for displaying the {@code result}.
	 */
	private PrintWriter processImageResult(PrintWriter out, IUnfilteredImageResult result) {
		IUnfilteredThumbnail thumbnail = result.getThumbnail();

		out.println( // imageResult-<div>
						TagHelper.createOpeningTag("div",
																			 "class",
																			 "imageResult"));
		out.println("<dt>");
		out.println( // IMAGE LINK ON THUMBNAIL
						TagHelper.createTag("a",
																"href",
																result.getUrl(),
																TagHelper.createTag("img",
																										"src",
																										thumbnail.getUrl())));
		out.println("</dt>");
		out.println( // <dd>
						TagHelper.createOpeningTag("dd",
																			 "class",
																			 new String[]{"popover", "bottom"}));
		out.println( // Arrow-<div>
						TagHelper.createTag("div",
																"class",
																"arrow"));
		out.println( // SOURCE URL LINK ON TITLE
						TagHelper.createTag("a",
																"href",
																result.getSourceURL(),
																result.getTitle()));
		out.println( // <small>
						TagHelper.createOpeningTag("small",
																			 "class",
																			 "popover-content"));
		out.println( // DISPLAY URL
						TagHelper.createTag("div",
																"class",
																new String[]{"displayURL", "text-muted"},
																result.getDisplayURL()));

		/* CONTENT TYPE */
		String bulletIcon = TagHelper.createTag("i",
																						"class",
																						new String[]{"glyphicon", "glyphicon-file"},
																						" ") + ' ';
		String currentElement = result.getContentType();
		if (currentElement.isEmpty()) {
			currentElement = "unknown file type";
		}
		out.println(TagHelper.createTag("div",
																		"class",
																		new String[]{"description", "imageContentType"},
																		bulletIcon + currentElement));

		/* IMAGE RESOLUTION */
		bulletIcon = TagHelper.createTag("i",
																		 "class",
																		 new String[]{"glyphicon", "glyphicon-resize-full"},
																		 " ") + ' ';
		currentElement = result.getWidth() + "x" + result.getHeight() + "px";
		if (currentElement.length() == 3) {
			currentElement = "unknown resolution";
		}
		out.println(TagHelper.createTag("div",
																		"class",
																		new String[]{"description", "imageResolution"},
																		bulletIcon + currentElement));

		/* FILE SIZE */
		bulletIcon = TagHelper.createTag("i",
																		 "class",
																		 new String[]{"glyphicon", "glyphicon-tasks"},
																		 " ") + ' ';
		if (result.getFileSize() == 0) {
			currentElement = "unknown file size";
		} else {
			currentElement = result.getFileSize() + "kb";
		}
		out.println(TagHelper.createTag("div",
																		"class",
																		new String[]{"description", "imageFileSize"},
																		bulletIcon + currentElement));
		out.println("</small>");
		out.println("</dd>");
		out.println("</div>");
		return out;
	}

	/**
	 *
	 * @param out     active {@code PrintWriter} that will be returned to the
	 *                 caller for output or further processing.
	 * @param results the web result to process.
	 *
	 * @return {@code PrintWriter} with HTML for displaying the {@code result}.
	 */
	private PrintWriter processWebResult(PrintWriter out, IUnfilteredWebResult result) {
		out.println("<li>");
		out.println( // TITLE
						TagHelper.createTag("a",
																"href",
																result.getUrl(),
																result.getTitle()));
		out.println("<small>");
		out.println( // DISPLAY URL
						TagHelper.createTag("div",
																"class",
																new String[]{"displayURL", "text-muted"},
																result.getDisplayURL()));
		out.println( // DESCRIPTION
						TagHelper.createTag("div",
																"class",
																"description",
																((IUnfilteredWebResult) result).getDescription()));
		out.println("</small>");
		out.println("</li>");
		return out;
	}

	/**
	 * <p>This method processes a {@code FilteredWebResult} to display it together
	 * with its meta-information. Currently InFO only sets URIs for the important
	 * legal/background information. Therefore, only links are being
	 * created.
	 *
	 * @see #processFilteredResult(java.io.PrintWriter,
	 * de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.AbstractFilteredResult,
	 * long)
	 *
	 * @param out    {@code PrintWriter} that is used for displaying filtered web
	 *                result.
	 * @param result {@code FilteredWebResult} that should be displayed with its
	 *                meta-information extrated.
	 * @param freeId {@code long} used to create HTML-IDs for jQuery-toggling.
	 *
	 * @return {@code PrintWriter} with added {@code result} output.
	 */
	private PrintWriter processFilteredWebResult(PrintWriter out, FilteredWebResult result, long freeId) {
		out.println("<li " // CLICKABLE CARD FOR TOGGLING REGULATION INFORMATION
								+ "class=\"regulatedResultBanner\" "
								+ "onclick=\"toggleRegulationBanner(" + freeId + ")\">"
								+ "Regulated result (click for more information)");
		out.println( // DL WITH ID
						TagHelper.createOpeningTag("dl",
																			 "class",
																			 new String[]{"regulatedWebResult", "well", "regulatedResult_" + freeId}));
		out = processFilteredResult(out, result);
		out.println("</dl>");
		out.println("</li>");
		return out;
	}

	/**
	 * <p>This method processes a {@code FilteredImageResult} to display it
	 * together with its meta-information. Currently InFO only sets URIs for the
	 * important legal/background information. Therefore, only links are being
	 * created.
	 *
	 * @see #processFilteredResult(java.io.PrintWriter,
	 * de.uni_koblenz.aggrimm.icp.interfaceAgents.results.filtered.AbstractFilteredResult,
	 * long)
	 *
	 * @param out    the {@code PrintWriter} to reuse.
	 * @param result the filtered image result to display.
	 *
	 * @return {@code PrintWriter} with added {@code result} output.
	 */
	private PrintWriter processFilteredImageResult(PrintWriter out, FilteredImageResult result) {
		out.println( // imageResult-<div>
						TagHelper.createOpeningTag("div",
																			 "class",
																			 "imageResult"));
		out.println("<dt>");
		out.println( // IMAGE LINK ON THUMBNAIL
						TagHelper.createTag("img",
																"src",
																"/resources/img/filteredThumbnail.png"));
		out.println("</dt>");
		out.println( // POPOVER DD
						TagHelper.createOpeningTag("dd",
																			 "class",
																			 new String[]{"popover", "bottom"}));
		out.println( // ARROW
						TagHelper.createTag("div",
																"class",
																"arrow"));
		out.println( // POPOVER-CONTENT SMALL
						TagHelper.createOpeningTag("small",
																			 "class",
																			 "popover-content"));
		out.println( // IMAGEREGULATIONINFO DL
						TagHelper.createOpeningTag("dl",
																			 "class",
																			 "imageRegulationInfo"));
		out = processFilteredResult(out, result);
		out.println("</dl>");
		out.println("</small>");
		out.println("</dd>");
		out.println("</div>");
		return out;
	}

	/**
	 * <p>This method processes a {@code AbstractFilteredResult} to display it
	 * together with its meta-information. Currently InFO only sets URIs for the
	 * important legal/background information. Therefore only links are being
	 * created. Every {@code AbstractFilteredResult} gets wrapped into a dl-tag
	 * with an unique id calculated from {@code freeId}, which should be
	 * {@code top+skip}.
	 * TODO: output more information when InFO transmits more information.
	 *
	 * @param out    {@code PrintWriter} that is used for displaying filtered web
	 *                result.
	 * @param result {@code AbstractFilteredResult} that should be displayed with
	 *                its
	 *                meta-information extracted.
	 *
	 * @return {@code PrintWriter} with added {@code result} output.
	 */
	private PrintWriter processFilteredResult(PrintWriter out, AbstractFilteredResult result) {
		List<? extends AbstractMetaInformationEntity> l = result.getControlledTopics();

		out.println("<dt>Controlled content:</dt>");
		for (AbstractMetaInformationEntity controlledContent : l) {
			out.println( // CONTROLLEDCONTENT
							TagHelper.createTag("dd",
																	createURIHrefString(controlledContent, "Click for more information.")));
		}

		l = result.getLegalAuthorizations();
		if (!l.isEmpty()) {
			out.println("<dt>Legal authorisation:</dt>");
			for (AbstractMetaInformationEntity legalAuthorization : l) {
				out.println( // LEGAL_AUTHORIZATION
								TagHelper.createTag("dd",
																		createURIHrefString(legalAuthorization, "Click for more information.")));
			}
		}

		l = result.getOrganizationalMotivations();
		if (!l.isEmpty()) {
			out.println("<dt>Organisational motivation:</dt>");
			for (AbstractMetaInformationEntity organizationalMotivation : l) {
				out.println( // MOTIVATIONAL_ORGANIZATION
								TagHelper.createTag("dd",
																		createURIHrefString(organizationalMotivation, "Click for more information.")));
			}
		}

		out.println("<dt>Rule data provider:</dt>");
		out.println( // RULE_DATA_PROVIDER
						TagHelper.createTag("dd",
																createURIHrefString(result.getRuleDataProvider(), "Click for more information.")));
		out.println("</dl>");

		return out;
	}

	/**
	 *
	 * @param metaInformation the {@code AbstractMetaInformationEntity} containing
	 *                         an URI to link to.
	 * @param content         the content of the a-tag.
	 *
	 * @return a {@code String} with a-tag having {@code content} as content and
	 *          linking to the URI of {@code metaInformation}.
	 */
	private String createURIHrefString(AbstractMetaInformationEntity metaInformation, String content) {
		return TagHelper.createTag("a",
															 "href",
															 metaInformation.getUri().toASCIIString(),
															 content);
	}
	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	/**
	 * Handles the HTTP
	 * <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 *
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP
	 * <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 *
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
