package api;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/restaurants")
public class RestaurantServlet extends HttpServlet {
    private Connection conn;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest", "root", "119191"
            );
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        System.out.println("✅ RestaurantServlet called!");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT restaurant_name, city, type, vendor_phone FROM vendors");

            JSONArray restaurants = new JSONArray();

            while (rs.next()) {
                JSONObject r = new JSONObject();
                r.put("name", rs.getString("restaurant_name"));
                r.put("city", rs.getString("city"));
                r.put("type", rs.getString("type"));
                r.put("phone", rs.getString("vendor_phone"));  // ✅ Include this
                restaurants.put(r);
            }

            out.print(restaurants.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Unable to fetch restaurants\"}");
        }
    }
}
