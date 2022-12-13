package com.example.clientgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MailSender {
    private Properties properties;

    public MailSender() throws IOException {
        properties = new Properties();
        String propFileName = "Client/ClientGUI/src/main/resources/mail_properties.properties";
        var inputStream = new FileInputStream(propFileName);
        properties.load(inputStream);
    }

    public void send(String subject, String text, String receiverEmail, String attachFilePath) throws Exception {

        var session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("username"), properties.getProperty("password"));
            }
        });
        var mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress("no-reply@gmail.com"));
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
        mimeMessage.setSubject(subject);
        mimeMessage.setText(text);
        if (attachFilePath != null) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(attachFilePath));
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(attachmentPart);
            mimeMessage.setContent(multipart);
        }
        Transport.send(mimeMessage);
    }

//    public void sendRegistrationCode(Profile profile, RegistrationCode code) throws Exception {
//        var session = Session.getDefaultInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(properties.getProperty("username"), properties.getProperty("password"));
//            }
//        });
//        var mimeMessage = new MimeMessage(session);
//        mimeMessage.setFrom(new InternetAddress("textConnectionRacoon@gmail.com"));
//        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(profile.getEmail()));
//        mimeMessage.setSubject("Создание аккаунта в системе e-shop");
//        var text = "Здравствуйте!\n" +
//                "Вы пытаетесь создать аккаунт в системе e-shop.\n" +
//                "Ваш код подтверждения: " + code.getCode();
//        mimeMessage.setText(text);
//        Transport.send(mimeMessage);
//    }
}
