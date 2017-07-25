package com.gomeplus.bigdata.TMonitor.Models;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public final class MailSender {
    private String smtpUserName;
    private String smtpPassword;
    private Session mailSession;
    private Transport transport;

    public MailSender() {
        this(null);
    }

    public MailSender(Properties properties) {
        try {
            initialize(properties);
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void initialize(Properties properties)
            throws IOException, MessagingException {
        if (properties == null) {
            InputStream inputStream = MailSender.class
                    .getResourceAsStream("/mail.properties");
            properties = new Properties();
            properties.load(inputStream);
        }

        smtpUserName = properties.getProperty("mail.smtp.user");
        smtpPassword = properties.getProperty("mail.smtp.password");
        String smtpHost = properties.getProperty("mail.smtp.host");
        mailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUserName, smtpPassword);
            }
        });
        transport = mailSession.getTransport();
        transport.connect(smtpHost, smtpUserName, smtpPassword);
    }

    public MailSender sendMessage(String toUsers, String subject,
                            String content, String contentType) throws MessagingException {
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(smtpUserName));
        message.setRecipients(Message.RecipientType.TO, toUsers);
        message.setSubject(subject);
        if (contentType == null)
            contentType = "text/plain;charset=UTF-8";
        message.setContent(content, contentType);
        transport.sendMessage(message, message.getAllRecipients());

        return this;
    }

    public void destroy() {
        try {
            transport.close();
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }
}
