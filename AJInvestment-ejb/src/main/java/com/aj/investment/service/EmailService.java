package com.aj.investment.service;

import jakarta.ejb.Stateless;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Logger;

@Stateless
public class EmailService {

    private static final Logger LOGGER =
            Logger.getLogger(
            EmailService.class.getName());

    // ─────────────────────────────────────────────
    // SMTP CONFIGURATION
    // ─────────────────────────────────────────────
    private static final String SMTP_HOST =
            "smtp.gmail.com";

    private static final int SMTP_PORT =
            587;

    private static final String SMTP_USER =
            "letau202@gmail.com";

    private static final String SMTP_PASS =
            "nfsj aoxh ixfe aafc";

    // ─────────────────────────────────────────────
    // FROM DETAILS
    // ─────────────────────────────────────────────
    private static final String FROM_ADDRESS =
            "letau202@gmail.com";

    private static final String FROM_NAME =
            "AJ Investment";


    // ─────────────────────────────────────────────
    // SEND VERIFICATION EMAIL
    // ─────────────────────────────────────────────
    public void sendVerificationEmail(
            String toEmail,
            String firstname,
            String token,
            String verifyUrl)
            throws MessagingException {

        Session mailSession =
                buildSession();

        MimeMessage msg =
                new MimeMessage(mailSession);

        try {

            msg.setFrom(
            new InternetAddress(
            FROM_ADDRESS,
            FROM_NAME,
            "UTF-8"));

        } catch (Exception e){

            msg.setFrom(
            new InternetAddress(
            FROM_ADDRESS));
        }

        msg.setRecipient(
        Message.RecipientType.TO,
        new InternetAddress(toEmail));

        msg.setSubject(
        "Verify Your AJ Investment Account");

        String html = """
        <html>

        <body style='font-family:Arial;
                     background:#f4f4f4;
                     padding:40px;'>

            <div style='max-width:600px;
                        background:white;
                        padding:40px;
                        margin:auto;
                        border-radius:10px;
                        box-shadow:0 2px 10px rgba(0,0,0,0.1);'>

                <h1 style='color:#c9a84c;'>
                    AJ Investment
                </h1>

                <h2>Hello %s</h2>

                <p>
                    Thank you for registering
                    with AJ Investment.
                </p>

                <p>
                    Please click the button below
                    to verify your account.
                </p>

                <a href='%s'
                   style='display:inline-block;
                          padding:15px 25px;
                          background:#c9a84c;
                          color:black;
                          text-decoration:none;
                          border-radius:5px;
                          font-weight:bold;'>

                    Verify Account
                </a>

                <p style='margin-top:30px;
                          color:gray;'>

                    If the button does not work,
                    copy and paste this link
                    into your browser:
                </p>

                <p>
                    <a href='%s'>
                        %s
                    </a>
                </p>

            </div>

        </body>

        </html>
        """.formatted(
                firstname,
                verifyUrl,
                verifyUrl,
                verifyUrl);

        msg.setContent(
        html,
        "text/html;charset=UTF-8");

        LOGGER.info(
        "Sending verification email to: "
        + toEmail);

        Transport.send(msg);

        LOGGER.info(
        "Verification email sent successfully!");
    }


    // ─────────────────────────────────────────────
    // SEND PASSWORD RESET EMAIL
    // ─────────────────────────────────────────────
    public void sendPasswordResetEmail(
            String toEmail,
            String firstname,
            String resetUrl)
            throws MessagingException {

        Session mailSession =
                buildSession();

        MimeMessage msg =
                new MimeMessage(mailSession);

        try {

            msg.setFrom(
            new InternetAddress(
            FROM_ADDRESS,
            FROM_NAME,
            "UTF-8"));

        } catch (Exception e){

            msg.setFrom(
            new InternetAddress(
            FROM_ADDRESS));
        }

        msg.setRecipient(
        Message.RecipientType.TO,
        new InternetAddress(toEmail));

        msg.setSubject(
        "Reset Your Password");

        String html = """
        <html>

        <body style='font-family:Arial;
                     background:#f4f4f4;
                     padding:40px;'>

            <div style='max-width:600px;
                        background:white;
                        padding:40px;
                        margin:auto;
                        border-radius:10px;
                        box-shadow:0 2px 10px rgba(0,0,0,0.1);'>

                <h1 style='color:#c9a84c;'>
                    AJ Investment
                </h1>

                <h2>Hello %s</h2>

                <p>
                    We received a request to
                    reset your password.
                </p>

                <p>
                    Click the button below
                    to continue.
                </p>

                <a href='%s'
                   style='display:inline-block;
                          padding:15px 25px;
                          background:#c9a84c;
                          color:black;
                          text-decoration:none;
                          border-radius:5px;
                          font-weight:bold;'>

                    Reset Password
                </a>

                <p style='margin-top:30px;
                          color:gray;'>

                    If you did not request this,
                    ignore this email.
                </p>

                <p style='margin-top:20px;
                          color:gray;'>

                    Or copy this link:
                </p>

                <p>
                    <a href='%s'>
                        %s
                    </a>
                </p>

            </div>

        </body>

        </html>
        """.formatted(
                firstname,
                resetUrl,
                resetUrl,
                resetUrl);

        msg.setContent(
        html,
        "text/html;charset=UTF-8");

        LOGGER.info(
        "Sending password reset email to: "
        + toEmail);

        Transport.send(msg);

        LOGGER.info(
        "Password reset email sent successfully!");
    }


    // ─────────────────────────────────────────────
    // BUILD MAIL SESSION
    // ─────────────────────────────────────────────
    private Session buildSession() {

        Properties props =
                new Properties();

        props.put(
        "mail.smtp.host",
        SMTP_HOST);

        props.put(
        "mail.smtp.port",
        String.valueOf(SMTP_PORT));

        props.put(
        "mail.smtp.auth",
        "true");

        props.put(
        "mail.smtp.starttls.enable",
        "true");

        props.put(
        "mail.smtp.starttls.required",
        "true");

        props.put(
        "mail.smtp.ssl.trust",
        SMTP_HOST);

        return Session.getInstance(
        props,

        new Authenticator() {

            @Override
            protected PasswordAuthentication
            getPasswordAuthentication() {

                return new PasswordAuthentication(
                        SMTP_USER,
                        SMTP_PASS
                );
            }
        });
    }
}