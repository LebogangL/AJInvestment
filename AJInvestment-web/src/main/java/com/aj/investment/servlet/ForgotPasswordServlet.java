package com.aj.investment.servlet;

import com.aj.investment.db.DBConnection;
import com.aj.investment.service.EmailService;

//import jakarta.mail.Message;
//import jakarta.mail.Session;
//import jakarta.mail.Transport;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;

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

import java.util.Properties;
import java.util.UUID;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet
extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out =
                response.getWriter();

        String email =
                request.getParameter("email");

        try {

            Connection conn =
                    DBConnection.getConnection();

            String sql =
            "SELECT * FROM Logdata "
            + "WHERE email=?";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setString(1, email);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                int logdataId =
                rs.getInt("id");

                String userEmail =
                rs.getString("email");
                
                String firstName =
rs.getString("firstName");

                // GENERATE TOKEN
                String token =
                UUID.randomUUID().toString();

                // SAVE TOKEN
                String insertTokenSql =
                "INSERT INTO passwordresettokens "
                + "(logdata_id, token) "
                + "VALUES (?, ?)";

                PreparedStatement tokenPs =
                conn.prepareStatement(insertTokenSql);

                tokenPs.setInt(1, logdataId);
                tokenPs.setString(2, token);

                tokenPs.executeUpdate();

                // RESET LINK WITH TOKEN
                String resetLink =
"https://unrepined-addyson-untold.ngrok-free.dev/"
+ "AJInvestment/reset_password.html"
+ "?token=" + token;

                // EMAIL DETAILS
               // String from =
                //"letau202@gmail.com";

                //String password =
                //"yzzfidkmchqpeklw";

                //Properties props =
                //new Properties();

                //props.put(
                //"mail.smtp.host",
               // "smtp.gmail.com");

                //props.put(
               // "mail.smtp.port",
               // "587");

                //props.put(
                //"mail.smtp.auth",
                //"true");

                //props.put(
                //"mail.smtp.starttls.enable",
                //"true");

               // Session session =
               // Session.getInstance(
               // props,
               // new jakarta.mail.Authenticator(){

                    //@Override
                    //protected jakarta.mail.PasswordAuthentication
                    //getPasswordAuthentication(){

                        //return new jakarta.mail
                        //.PasswordAuthentication(
                        //from,
                        ////password);
                   // }
               // });

                //Message message =
                //new MimeMessage(session);

                //message.setFrom(
               // new InternetAddress(from));

                //message.setRecipients(
                //Message.RecipientType.TO,
                //InternetAddress.parse(userEmail));

                //message.setSubject(
                //"Reset Your Password");

                //message.setText(
               // "Click the link below "
               // + "to reset your password:\n\n"
               // + resetLink);

                // SEND EMAIL
               // Transport.send(message);
EmailService emailService =
new EmailService();

emailService.sendPasswordResetEmail(
        userEmail,
        firstName,
        resetLink
);
                // SUCCESS PAGE
                out.println(
                "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Email Sent</title>"

                + "<style>"

                + "body{"
                + "background:#05070d;"
                + "font-family:Arial;"
                + "display:flex;"
                + "justify-content:center;"
                + "align-items:center;"
                + "height:100vh;"
                + "margin:0;"
                + "}"

                + ".box{"
                + "width:500px;"
                + "background:#0d111b;"
                + "border:1px solid #d4af37;"
                + "border-radius:15px;"
                + "padding:50px;"
                + "text-align:center;"
                + "color:white;"
                + "}"

                + "h1{"
                + "color:#d4af37;"
                + "font-size:45px;"
                + "margin-bottom:20px;"
                + "}"

                + "p{"
                + "font-size:22px;"
                + "line-height:1.6;"
                + "color:#dfe6f3;"
                + "}"

                + "a{"
                + "display:inline-block;"
                + "margin-top:30px;"
                + "padding:15px 30px;"
                + "background:#d4af37;"
                + "color:black;"
                + "text-decoration:none;"
                + "font-weight:bold;"
                + "border-radius:10px;"
                + "}"

                + "</style>"
                + "</head>"

                + "<body>"

                + "<div class='box'>"

                + "<h1>Email Sent</h1>"

                + "<p>"
                + "A password reset link has been "
                + "sent to your email address."
                + "<br><br>"
                + "Please check your inbox "
                + "or spam folder."
                + "</p>"

                + "<a href='login.html'>"
                + "Back To Login"
                + "</a>"

                + "</div>"

                + "</body>"
                + "</html>"
                );

                tokenPs.close();

            } else {

                out.println(
                "<h1 style='color:red;'>"
                + "Email not found"
                + "</h1>");
            }

            rs.close();
            ps.close();
            conn.close();

        } catch(Exception e){

            e.printStackTrace();

            out.println(
            "<h1 style='color:red;'>"
            + "Error sending email"
            + "</h1>");
        }
    }
}