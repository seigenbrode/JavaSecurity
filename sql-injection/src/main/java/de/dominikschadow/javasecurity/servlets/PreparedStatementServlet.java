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

import de.dominikschadow.javasecurity.domain.Customer;
import de.dominikschadow.javasecurity.listener.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet using a Prepared Statement to query the in-memory-database.
 * User input is not modified and used directly in the SQL query.
 *
 * @author Dominik Schadow
 */
@WebServlet(name = "PreparedStatementServlet", urlPatterns = {"/PreparedStatementServlet"})
public class PreparedStatementServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String name = request.getParameter("name");
        logger.info("Received " + name + " as POST parameter");

        String query = "SELECT * FROM customer WHERE name = ? ORDER BY CUST_ID";
        List<Customer> customers = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = ConnectionListener.con.prepareStatement(query);
            stmt.setString(1, name);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustId(rs.getInt(1));
                customer.setName(rs.getString(2));
                customer.setStatus(rs.getString(3));
                customer.setOrderLimit(rs.getInt(4));

                customers.add(customer);
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html><head>");
            out.println("<title>SQL Injection - Prepared Statement</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/css/styles.css\" />");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>SQL Injection - Prepared Statement</h1>");
            out.println("<p><strong>Input</strong> " + name + "</p>");
            out.println("<h2>Customer Data</h2>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Name</th>");
            out.println("<th>Status</th>");
            out.println("<th>Order Limit</th>");
            out.println("</tr>");

            for (Customer customer : customers) {
                out.println("<tr>");
                out.println("<td>" + customer.getCustId() + "</td>");
                out.println("<td>" + customer.getName() + "</td>");
                out.println("<td>" + customer.getStatus() + "</td>");
                out.println("<td>" + customer.getOrderLimit() + "</td>");
                out.println("</tr>");
            }

            out.println("<table>");
            out.println("<p><a href=\"index.jsp\">Home</a></p>");
            out.println("</body>");
            out.println("</html>");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
