package api;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/vendorAuth")
public class VendorAuthServlet extends HttpServlet {
    private Connection conn;

    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest", "root", "119191"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String action = req.getParameter("action");
        if ("login".equals(action)) handleLogin(req, res);
        else if ("signup".equals(action)) handleSignup(req, res);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String action = req.getParameter("action");
        if ("profile".equals(action)) handleProfile(req, res);
        else if ("logout".equals(action)) {
            req.getSession().invalidate();
            res.getWriter().write("logout");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String phone = req.getParameter("vendor_phone");
        String password = req.getParameter("password");

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM vendors WHERE vendor_phone = ? AND password = ?");
            ps.setString(1, phone);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = req.getSession();
                session.setAttribute("vendor_phone", phone);
                res.getWriter().write("success");
            } else {
                res.getWriter().write("fail");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().write("error");
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO vendors (first_name, last_name, vendor_phone, email, restaurant_name, address, city, type, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, req.getParameter("first_name"));
            ps.setString(2, req.getParameter("last_name"));
            ps.setString(3, req.getParameter("vendor_phone"));
            ps.setString(4, req.getParameter("email"));
            ps.setString(5, req.getParameter("restaurant_name"));
            ps.setString(6, req.getParameter("address"));
            ps.setString(7, req.getParameter("city"));
            ps.setString(8, req.getParameter("type"));
            ps.setString(9, req.getParameter("password"));

            int rows = ps.executeUpdate();
            if (rows > 0) res.getWriter().write("success");
            else res.getWriter().write("fail");
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().write("error");
        }
    }

    private void handleProfile(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        res.setContentType("application/json");

        if (session == null || session.getAttribute("vendor_phone") == null) {
            res.getWriter().write("{}");
            return;
        }

        String phone = (String) session.getAttribute("vendor_phone");

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM vendors WHERE vendor_phone = ?");
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String json = String.format(
                    "{\"first_name\":\"%s\",\"last_name\":\"%s\",\"vendor_phone\":\"%s\",\"email\":\"%s\",\"restaurant_name\":\"%s\",\"city\":\"%s\",\"type\":\"%s\"}",
                    rs.getString("first_name"), rs.getString("last_name"), rs.getString("vendor_phone"),
                    rs.getString("email"), rs.getString("restaurant_name"), rs.getString("city"), rs.getString("type")
                );
                res.getWriter().write(json);
            } else {
                res.getWriter().write("{}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().write("{}");
        }
    }

    public void destroy() {
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}
