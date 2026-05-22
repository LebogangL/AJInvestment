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

import java.security.MessageDigest;

@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet
extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out =
                response.getWriter();

        String token =
                request.getParameter("token");

        String password =
                request.getParameter("password");

        try {

            Connection conn =
                    DBConnection.getConnection();

            // FIND TOKEN
            String tokenSql =
            "SELECT * FROM passwordresettokens "
            + "WHERE token=?";

            PreparedStatement tokenPs =
                    conn.prepareStatement(tokenSql);

            tokenPs.setString(1, token);

            ResultSet rs =
                    tokenPs.executeQuery();

            if(rs.next()){

                int logdataId =
                rs.getInt("logdata_id");

                // UPDATE PASSWORD
                String updateSql =
                "UPDATE Logdata "
                + "SET password=? "
                + "WHERE id=?";

                PreparedStatement updatePs =
                        conn.prepareStatement(updateSql);

                updatePs.setString(
                1,
                hashPassword(password));

                updatePs.setInt(
                2,
                logdataId);

                updatePs.executeUpdate();

                // DELETE USED TOKEN
                String deleteSql =
                "DELETE FROM passwordresettokens "
                + "WHERE token=?";

                PreparedStatement deletePs =
                        conn.prepareStatement(deleteSql);

                deletePs.setString(1, token);

                deletePs.executeUpdate();

                out.println(
"<!DOCTYPE html>"
+ "<html>"
+ "<head>"
+ "<title>Password Updated</title>"

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

+ "<div class='icon'>🔒</div>"

+ "<h1>Password Updated</h1>"

+ "<p>"
+ "Your password has been successfully changed."
+ "<br><br>"
+ "You can now login securely using your new password."
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
"<!DOCTYPE html>"
+ "<html>"
+ "<head>"
+ "<title>Invalid Reset Link</title>"

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
+ "border:1px solid #ff4d4d;"
+ "border-radius:20px;"
+ "padding:50px;"
+ "text-align:center;"
+ "box-shadow:0 0 30px rgba(255,77,77,0.15);"
+ "}"

+ ".icon{"
+ "font-size:80px;"
+ "margin-bottom:20px;"
+ "}"

+ "h1{"
+ "color:#ff4d4d;"
+ "font-size:40px;"
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
+ "background:#ff4d4d;"
+ "color:white;"
+ "text-decoration:none;"
+ "font-weight:bold;"
+ "border-radius:10px;"
+ "font-size:18px;"
+ "transition:0.3s;"
+ "}"

+ "a:hover{"
+ "background:#ff6666;"
+ "}"

+ "</style>"

+ "</head>"

+ "<body>"

+ "<div class='card'>"

+ "<div class='icon'>❌</div>"

+ "<h1>Invalid Link</h1>"

+ "<p>"
+ "This password reset link is invalid "
+ "or has already expired."
+ "<br><br>"
+ "Please request a new password reset email."
+ "</p>"

+ "<a href='forgot_password.html'>"
+ "Reset Password Again"
+ "</a>"

+ "</div>"

+ "</body>"
+ "</html>"
);
            }

            rs.close();
            tokenPs.close();
            conn.close();

        } catch(Exception e){

            e.printStackTrace();

           out.println(
"<!DOCTYPE html>"
+ "<html>"
+ "<head>"
+ "<title>Password Reset Error</title>"

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
+ "border:1px solid #ff4d4d;"
+ "border-radius:20px;"
+ "padding:50px;"
+ "text-align:center;"
+ "box-shadow:0 0 30px rgba(255,77,77,0.15);"
+ "}"

+ ".icon{"
+ "font-size:80px;"
+ "margin-bottom:20px;"
+ "}"

+ "h1{"
+ "color:#ff4d4d;"
+ "font-size:40px;"
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
+ "background:#ff4d4d;"
+ "color:white;"
+ "text-decoration:none;"
+ "font-weight:bold;"
+ "border-radius:10px;"
+ "font-size:18px;"
+ "transition:0.3s;"
+ "}"

+ "a:hover{"
+ "background:#ff6666;"
+ "}"

+ "</style>"

+ "</head>"

+ "<body>"

+ "<div class='card'>"

+ "<div class='icon'>⚠️</div>"

+ "<h1>Password Reset Failed</h1>"

+ "<p>"
+ "Something went wrong while updating "
+ "your password."
+ "<br><br>"
+ "Please try again or request another "
+ "password reset email."
+ "</p>"

+ "<a href='forgot_password.html'>"
+ "Try Again"
+ "</a>"

+ "</div>"

+ "</body>"
+ "</html>"
);
        }
    }

    private String hashPassword(
            String password)
            throws Exception {

        MessageDigest md =
        MessageDigest.getInstance("SHA-256");

        byte[] hash =
        md.digest(password.getBytes());

        StringBuilder sb =
        new StringBuilder();

        for(byte b : hash){

            sb.append(
            String.format("%02x", b));
        }

        return sb.toString();
    }
}