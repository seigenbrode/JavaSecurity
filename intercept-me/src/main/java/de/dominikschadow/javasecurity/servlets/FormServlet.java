/*
 * Copyright (C) 2015 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Java Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.javasecurity.servlets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet processing all user input and returns <i>SUCCESS</i> or <i>FAILURE</i> depending on the given input.
 *
 * @author Dominik Schadow
 */
@WebServlet(name = "Form", urlPatterns = {"/form"})
public class FormServlet extends HttpServlet {
	private static final long serialVersionUID = 8978771621385673835L;
	private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String name = request.getParameter("name");

        logger.info("Received {} as name", name);

        String result = "FAILURE";

        if (StringUtils.equals(name, "inject")) {
            result = "SUCCESS";
        }

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html><head>");
            out.println("<title>InterceptMe</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/css/styles.css\" />");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>InterceptMe</h1>");
            out.println("<p>" + result + "</p>");
            out.println("<p><a href=\"index.jsp\">Home</a></p>");
            out.println("</body></html>");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
