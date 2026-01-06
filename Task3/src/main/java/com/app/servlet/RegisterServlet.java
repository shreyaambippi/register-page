package com.app.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class RegisterServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/userdb",
            "root",
            "root123"
        );
    }

    // GET → Check email availability
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        response.setContentType("text/plain");

        try (Connection con = getConnection()) {
            PreparedStatement ps =
                con.prepareStatement("SELECT id FROM users WHERE email=?");
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                response.getWriter().print("Email already exists");
            } else {
                response.getWriter().print("Email available");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Error");
        }
    }

    // POST → Register user
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        int age = Integer.parseInt(request.getParameter("age"));
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String pincode = request.getParameter("pincode");

        try (Connection con = getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(name,email,password,age,phone,address,pincode) VALUES (?,?,?,?,?,?,?)"
            );

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password); // ⚠️ Hash in real projects
            ps.setInt(4, age);
            ps.setString(5, phone);
            ps.setString(6, address);
            ps.setString(7, pincode);

            ps.executeUpdate();

            out.println("<h3 style='color:green;'>Registration Successful!</h3>");

        } catch (SQLIntegrityConstraintViolationException e) {
            out.println("<h3 style='color:red;'>Email already exists!</h3>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3 style='color:red;'>Something went wrong!</h3>");
        }
    }
}
