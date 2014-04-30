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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Servlet for displaying the legal text for SEARCH_ENGINE dynamically.
 *
 * @author mruster
 */
@WebServlet(name = "SearchEngineLegalTextServlet", urlPatterns = {"/SearchEngineLegalTextServlet"})
public class SearchEngineLegalTextServlet extends HttpServlet {

	private static final long serialVersionUID = 5733118404496469565L;
	@Resource(name = "SEARCH_ENGINE")
	private String SEARCH_ENGINE;
	@Resource(name = "LEGAL_TEXT_PATH")
	private String LEGAL_TEXT_PATH;
	private final static Logger LOGGER = Logger.getLogger(SearchEngineLegalTextServlet.class.getCanonicalName());

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * <p>Prints legal text for activated search engine or nothing if no legal
	 * text is found. In this case, there will also be a message logged on the
	 * SEVERE level.
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
		FileInputStream in = null;
		BufferedReader br = null;
		InputStreamReader insr = null;

		try (PrintWriter out = response.getWriter()) {
			File legalText = new File(LEGAL_TEXT_PATH + File.separator + SEARCH_ENGINE);
			assert (legalText.isFile());
			if (legalText.isFile()) {
				in = new FileInputStream(legalText);
				insr = new InputStreamReader(in, Charset.forName("UTF-8"));
				br = new BufferedReader(insr);
				String line;
				while ((line = br.readLine()) != null) {
					out.println(line);
				}
			} else {
				LOGGER.log(Level.SEVERE, "The legal text for the current search engine could not be found or opened. The missing file is {0}", legalText);
			}
		} catch (IOException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.log(Level.SEVERE, null, ex);
		} finally {
			if (in != null) {
				in.close();
			}
			if (insr != null) {
				insr.close();
			}
			if (br != null) {
				br.close();
			}
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
