package com.aj.investment.servlet;

import com.aj.investment.db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "LoginClientServlet", urlPatterns = {"/LoginClient"})
public class LoginClientServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginClientServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String usernameOrEmail = trimOrEmpty(request.getParameter("username"));
        String password = trimOrEmpty(request.getParameter("password"));

        try (PrintWriter out = response.getWriter()) {
            if (usernameOrEmail.isBlank() || password.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(json("error", "Username/email and password are required"));
                return;
            }

            String passwordHash = sha256(password);
            if (passwordHash == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(json("error", "Password check failed"));
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                LoginUser user = findUser(conn, usernameOrEmail);

                if (user == null || !passwordHash.equalsIgnoreCase(user.passwordHash())) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print(json("error", "Invalid username/email or password"));
                    return;
                }
                
                if(user.verified() == 0){

    response.setStatus(
    HttpServletResponse.SC_UNAUTHORIZED);

    out.print(
    json(
    "error",
    "Please verify your email first"
    ));

    return;
}

                HttpSession session = request.getSession(true);
                session.setAttribute("clientId", user.id());
                session.setAttribute("clientName", user.firstName());
                session.setAttribute("clientUsername", user.username());
                session.setAttribute("clientEmail", user.email());

                LOGGER.info("Client logged in: " + user.username() + " (" + user.email() + ")");
                out.print("{\"status\":\"success\",\"message\":\"Login successful\"}");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Login database error", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(json("error", "Database error. Please try again."));
            }
        }
    }

    private LoginUser findUser(Connection conn, String usernameOrEmail) throws SQLException {
        String sql = """
            SELECT id, firstName, username, email, password, verified
            FROM Logdata
            WHERE email = ? OR username = ?
            LIMIT 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new LoginUser(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("verified")
                );
            }
        }
    }

    private String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String json(String status, String message) {
        String escaped = message.replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                                .replace("\r", "\\r");
        return "{\"status\":\"" + status + "\",\"message\":\"" + escaped + "\"}";
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "SHA-256 hashing failed", e);
            return null;
        }
    }

    private record LoginUser(int id, String firstName, String username,
                             String email, String passwordHash, int verified) {
    }
}