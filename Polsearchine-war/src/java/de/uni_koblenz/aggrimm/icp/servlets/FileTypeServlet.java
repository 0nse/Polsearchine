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
import de.uni_koblenz.aggrimm.icp.logic.resultClasses.FileTypeResult;
import de.uni_koblenz.aggrimm.icp.facades.remote.IFileTypeSearchBeanRemote;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Servlet for displaying certain fields when detecting a known file type.
 *
 * @author mruster
 */
@WebServlet(name = "FileTypeServlet", urlPatterns = {"/FileTypeServlet"})
public class FileTypeServlet extends HttpServlet {

	private static final long serialVersionUID = -8131429964815542973L;
	private final static Logger LOGGER = Logger.getLogger(FileTypeServlet.class.getCanonicalName());
	@EJB
	private IFileTypeSearchBeanRemote fileTypeSearchBeanFacade;

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * <p>If there's a file type within the query, this servlet creates an input
	 * field and a label to tell the user, how to search for file types.
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
		try (PrintWriter out = response.getWriter()) {
			String searchTerm = request.getParameter("searchTerm");
			FileTypeResult fileTypeResult = fileTypeSearchBeanFacade.findKnownFileTypeIfAny(searchTerm);

			if (fileTypeResult.doesContainFileType()) {
				String input = String.format("<input type=\"text\" "
																		 + "onclick=\"selectThis(this);\""
																		 + "value=\"filetype:%s %s\" />",
																		 fileTypeResult.getFoundFileType(),
																		 fileTypeResult.getSearchTermRest());

				out.println(TagHelper.createTag("div", "class", "label label-success infoBanner",
																				String.format("Are you looking for %s files? Search for %s",
																											fileTypeResult.getFoundFileType(), input)));
			}
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.log(Level.SEVERE, null, ex);
		}
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
