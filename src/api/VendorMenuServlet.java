// VendorMenuServlet.java
package api;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/vendorMenu")
public class VendorMenuServlet extends HttpServlet {
    private Connection conn;

    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodnest", "root", "119191");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String action = req.getParameter("action");
        String phone = req.getParameter("vendor_phone");
        res.setContentType("text/plain");

        try {
            if ("add".equals(action)) {
                String name = req.getParameter("item_name");
                double price = Double.parseDouble(req.getParameter("item_price"));
                String type = req.getParameter("item_type");

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO vendor_menu_items(vendor_phone, item_name, item_price, item_type) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, phone);
                ps.setString(2, name);
                ps.setDouble(3, price);
                ps.setString(4, type);

                int rows = ps.executeUpdate();
                res.getWriter().write(rows > 0 ? "success" : "fail");

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("item_id"));

                PreparedStatement ps = conn.prepareStatement("DELETE FROM vendor_menu_items WHERE id = ? AND vendor_phone = ?");
                ps.setInt(1, id);
                ps.setString(2, phone);

                int rows = ps.executeUpdate();
                res.getWriter().write(rows > 0 ? "deleted" : "fail");

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("item_id"));
                String name = req.getParameter("item_name");
                double price = Double.parseDouble(req.getParameter("item_price"));
                String type = req.getParameter("item_type");

                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE vendor_menu_items SET item_name = ?, item_price = ?, item_type = ? WHERE id = ? AND vendor_phone = ?"
                );
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setString(3, type);
                ps.setInt(4, id);
                ps.setString(5, phone);

                int rows = ps.executeUpdate();
                res.getWriter().write(rows > 0 ? "success" : "fail");
            } else {
                res.getWriter().write("invalid-action");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            res.getWriter().write("fail");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String phone = req.getParameter("vendor_phone");
        res.setContentType("application/json");

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM vendor_menu_items WHERE vendor_phone = ?");
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json.append(String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"price\":%.2f,\"type\":\"%s\"},",
                    rs.getInt("id"), rs.getString("item_name"), rs.getDouble("item_price"), rs.getString("item_type")
                ));
            }
            if (json.length() > 1) json.setLength(json.length() - 1);
            json.append("]");

            res.getWriter().write(json.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().write("[]");
        }
    }
}
