package de.x8bit.Fantasya.util.net;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Host.Datenbank;

/**
 *
 */
public class SMTPConnector {

    private String SMTP_FROM = "hapebe@gmail.com";
    private String SMTP_HOST_NAME = "foo.bar";
    private String SMTP_PORT = "465";
    private String USERNAME = "defaultUser";
    private String PASSWORD = "defaultPassword";

    public SMTPConnector() {
        Datenbank db = new Datenbank("SMTPConnector auth settings");
        SMTP_FROM = db.ReadSettings("smtp.from", SMTP_FROM);
        SMTP_HOST_NAME = db.ReadSettings("smtp.host", SMTP_HOST_NAME);
        SMTP_PORT = db.ReadSettings("smtp.port", SMTP_PORT);
        USERNAME = db.ReadSettings("smtp.username", USERNAME);
        PASSWORD = db.ReadSettings("smtp.password", PASSWORD);
        db.Close();
    }

    public void sendBefehlsCheck(Partei p, String bericht) throws MessagingException {
        BodyPart bp0 = new MimeBodyPart();
        bp0.setText(bericht);

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(bp0);


        this.sendSSLMessage(
                new String[]{ p.getEMail() },
                "[Fantasya] Befehlsverarbeitung",
                mp,
                SMTP_FROM
        );
    }

    public void sendSSLMessage(String recipients[], String subject,
            Multipart contents, String from) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.port", SMTP_PORT);
        Session session = Session.getInstance(props, new MyAuthenticator());

		// construct the message
        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);
        msg.setSubject(subject);
        msg.setContent(contents);

		// send it
		Transport.send(msg);
    }

    private class MyAuthenticator extends Authenticator {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(USERNAME, PASSWORD);
        }
    }
}
