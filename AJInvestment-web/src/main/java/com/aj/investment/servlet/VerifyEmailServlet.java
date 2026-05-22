package com.aj.investment.servlet;

import com.aj.investment.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/VerifyEmail")
public class VerifyEmailServlet
extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out =
                response.getWriter();

        String token =
                request.getParameter("token");

        try {

            Connection conn =
                    DBConnection.getConnection();

            // CHECK TOKEN
            String sql =
            "SELECT * FROM "
            + "LogdataID_Email_verification_token "
            + "WHERE email_verification_token=?";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setString(1, token);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                int logdataId =
                rs.getInt("logdata_id");

                // VERIFY ACCOUNT
                String updateSql =
                "UPDATE Logdata "
                + "SET verified=1 "
                + "WHERE id=?";

                PreparedStatement updatePs =
                        conn.prepareStatement(updateSql);

                updatePs.setInt(1, logdataId);

                updatePs.executeUpdate();

                // DELETE TOKEN
                String deleteSql =
                "DELETE FROM "
                + "LogdataID_Email_verification_token "
                + "WHERE email_verification_token=?";

                PreparedStatement deletePs =
                        conn.prepareStatement(deleteSql);

                deletePs.setString(1, token);

                deletePs.executeUpdate();

               out.println(
"<!DOCTYPE html>"
+ "<html>"
+ "<head>"
+ "<title>Email Verified</title>"

+ "<style>"

+ "body{"
+ "margin:0;"
+ "padding:0;"
+ "background:#05070d;"
+ "font-family:Arial,sans-serif;"
+ "display:flex;"
+ "justify-content:center;"
+ "align-items:center;"
+ "height:100vh;"
+ "}"

+ ".card{"
+ "width:500px;"
+ "background:#0d111b;"
+ "border:1px solid #d4af37;"
+ "border-radius:20px;"
+ "padding:50px;"
+ "text-align:center;"
+ "box-shadow:0 0 30px rgba(212,175,55,0.15);"
+ "}"

+ ".icon{"
+ "font-size:80px;"
+ "margin-bottom:20px;"
+ "}"

+ "h1{"
+ "color:#d4af37;"
+ "font-size:42px;"
+ "margin-bottom:20px;"
+ "}"

+ "p{"
+ "color:#dfe6f3;"
+ "font-size:20px;"
+ "line-height:1.6;"
+ "margin-bottom:35px;"
+ "}"

+ "a{"
+ "display:inline-block;"
+ "padding:15px 35px;"
+ "background:#d4af37;"
+ "color:black;"
+ "text-decoration:none;"
+ "font-weight:bold;"
+ "border-radius:10px;"
+ "font-size:18px;"
+ "transition:0.3s;"
+ "}"

+ "a:hover{"
+ "background:#f1cc5d;"
+ "}"

+ "</style>"

+ "</head>"

+ "<body>"

+ "<div class='card'>"

+ "<div class='icon'>✅</div>"

+ "<h1>Email Verified</h1>"

+ "<p>"
+ "Your AJ Investment account has been "
+ "successfully verified."
+ "<br><br>"
+ "You can now securely login to your account."
+ "</p>"

+ "<a href='login.html'>"
+ "Proceed To Login"
+ "</a>"

+ "</div>"

+ "</body>"
+ "</html>"
);

                deletePs.close();
                updatePs.close();

            } else {

                out.println(
                "<h1 style='color:red;'>"
                + "Invalid verification link"
                + "</h1>");
            }

            rs.close();
            ps.close();
            conn.close();

        } catch(Exception e){

            e.printStackTrace();

            out.println(
            "<h1 style='color:red;'>"
            + "Verification failed"
            + "</h1>");
        }
    }
}