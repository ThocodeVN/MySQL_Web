/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package murach.data;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;



/**
 *
 * @author Duy Thanh
 */
@WebServlet(name = "SqlGatewayServlet", urlPatterns = {"/sqlGateway"})
public class SqlGatewayServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sqlStatement = request.getParameter("sqlStatement");
        String sqlResult = "";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String dbURL = "jdbc:mysql://localhost:3306/testdb?zeroDateTimeBehavior=CONVERT_TO_NULL";
            String username = "root";
            String password = "1234";
            try (Connection connection = DriverManager.getConnection(dbURL, username, password); Statement statement = connection.createStatement()) {
                sqlStatement = sqlStatement.trim();
                if(sqlStatement.length() >= 6){
                    String sqlType = sqlStatement.substring(0, 6);
                    if (sqlType.equalsIgnoreCase("select")){
                        try (ResultSet resultSet = statement.executeQuery(sqlStatement)) {
                            sqlResult = SQLUtil.getHtmlTable(resultSet);
                        }
                    } else {
                        int i = statement.executeUpdate(sqlStatement);
                        if(i == 0) {
                            sqlResult = "<p>The statement executed successfully.</p>";
                        } else {
                            sqlResult = "<p> The statement executed successfully.<br>" +
                                    i + "row(s) affected.</p>";
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e){
            sqlResult = "<p>Error loading the database driver: <br>" + e.getMessage() + "</p>";
        } catch (SQLException e){
            sqlResult = "<p>Error executing the SQL statement: <br>" + e.getMessage() + "</p>";
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("sqlResult", sqlResult);
        session.setAttribute("sqlStatement", sqlStatement);
        
        String url = "/index.jsp";
        getServletContext().getRequestDispatcher(url).forward(request, response);
    }
}
