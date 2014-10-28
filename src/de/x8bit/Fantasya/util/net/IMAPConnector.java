package de.x8bit.Fantasya.util.net;

import de.x8bit.Fantasya.Atlantis.Messages.BigError;
import de.x8bit.Fantasya.Host.Datenbank;
import de.x8bit.Fantasya.Host.Main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

/** Liest emails von einer IMAP-Mailbox, parst sie, und schreibt die Kommandos
 * lokal raus.
 *
 * @author hb
 */
public class IMAPConnector {

    private static String DEFAULT_PROTOCOL = "imaps";
    private static String DEFAULT_HOST = "foo.bar";
    private static String DEFAULT_USERNAME = "fantasya@foo.bar";
    private static String DEFAULT_PASSWORD = "unset";

    private String content;
    private List<String> log;

    public IMAPConnector() {
        this.log = new ArrayList<String>();
    }

    /**
     * Durchsucht den Ordner "Fya" der IMAP-Mailbox nach Mails mit "fantasya beta" im Betreff und
     * speichert ggf. deren text/plain-Inhalt im Ordner befehle-inbox. Die Mails werden dann gelöscht.
     * @return Anzahl der gefundenen Mails
     * @throws MessagingException
     * @throws IOException
     */
    @SuppressWarnings("resource")
	public int befehleHolen() throws MessagingException, IOException {
		// Connect to the server
        Store store = openStore();
        if (!store.isConnected()) {
			throw new MessagingException("IMAPConnector - Verbindung nicht erfolgreich.");
		}

		// open the mailbox
        Folder fya = store.getFolder("Fya");
        fya.open(Folder.READ_WRITE);
        log.add("opened folder Fya: " + fya.getMessageCount() + " messages.");
        System.out.println("Verbunden - es gibt " + fya.getMessageCount() + " Nachrichten.");

		// loop through the messages
        Message[] messages = fya.getMessages();
        int validCount = 0;
        for (Message m : messages) {
            content = null;

            // Get some headers
            String mimeType = m.getContentType();
            log.add(
                    "------------------------------------------------------------------------------\n" +
                    "DATE:      " + m.getSentDate() + "\n" +
                    "FROM:      " + m.getFrom()[0] + "\n" +
                    "SUBJECT:   " + m.getSubject() + "\n" +
                    "MIME-TYPE: " + mimeType.replaceAll("\n", "")
            );

			// extract the meat (body) of the mail
            Object o = m.getContent();
            if (o instanceof String) {
                log.add("**String content:**\n" + (String)o + "\n");
                String[] mimeTypeParts = mimeType.split(" ");
                if (mimeTypeParts[0].equalsIgnoreCase("text/plain;")) {
                    // wird dann in die Datei geschrieben.
                    content = (String)o;
                }
            } else if (o instanceof Multipart) {

                processMultipart((Multipart)o, 1);
                
            } else if (o instanceof InputStream) {
                StringBuilder sb = new StringBuilder();
                sb.append("**This is an InputStream message**\n");
                InputStream is = (InputStream)o;
                // Assumes character content (not binary images)
                int c;
                while ((c = is.read()) != -1) {
                    sb.append(c);
                }
                log.add(sb.toString());
            }

            log.add("------------------------------------------------------------------------------\n\n\n");


			// saves the content
            if (content != null) {
                validCount ++;
				befehleSpeichern(content);
                m.setFlag(Flags.Flag.DELETED, true);
            } else {
                // Absender benachrichtigen. (?)
            }

        } // next message

        // "true" actually deletes flagged messages from folder
        fya.close(true);
        store.close();

        writeLog();

        return validCount;
    }

    private Store openStore() throws MessagingException {
        Datenbank db = new Datenbank("IMAPConnector - IMAP auth data");
        String protocol = db.ReadSettings("befehle.protocol", DEFAULT_PROTOCOL);
        String host = db.ReadSettings("befehle.host", DEFAULT_HOST);
        String username = db.ReadSettings("befehle.username", DEFAULT_USERNAME);
        String password = db.ReadSettings("befehle.password", DEFAULT_PASSWORD);
        db.Close();

        if (Main.getSFlag("imap_pwd").length() > 0) {
            password = Main.getSFlag("imap_pwd");
        }

        Properties sessionProps = new Properties();

        Session session = Session.getDefaultInstance(sessionProps);

        Store store = null;
        System.out.println("auth: " + protocol + "://" + username + ":" + password + "@" + host);
        try {
            store = session.getStore(protocol);
            store.connect(host, username, password);
        } catch (NoSuchProviderException ex) {
            new BigError(ex);
        }
        
        return store;
    }

    private void processMultipart(Multipart mp, int level) throws MessagingException, IOException {
        int count = mp.getCount();
        log.add("**Multipart content on level " + level + ", it has " + count + " BodyParts in it**");
        for (int j = 0; j < count; j++) {
            // Part are numbered starting at 0
            BodyPart b = mp.getBodyPart(j);
            MimeBodyPart mimePart = (MimeBodyPart) b;
            String mimeType = b.getContentType();
            String encoding = mimePart.getEncoding();
            log.add( "BodyPart " + (j + 1) + " is of MimeType " + mimeType + " encoding: " + encoding);
            
            Object o = b.getContent();
            if (o instanceof String) {
                log.add("**String**\n" + (String)o);
                String[] mimeTypeParts = mimeType.split(" ");
                if (mimeTypeParts[0].equalsIgnoreCase("text/plain;")) {
                    // wird dann in die Datei geschrieben.
                    content = (String)o;
                }
            } else if (o instanceof Multipart) {

                // rekursiv:
                processMultipart((Multipart)o, level + 1);
                
            }
            else if (o instanceof InputStream) {
                log.add("**This is an InputStream BodyPart**");
            }
        }
    }

    private void writeLog() {
		if (log.isEmpty()) {
			return;
		}

        File f = new File("imap-log.txt");
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));

			for (String s : log) {
				out.append(s + "\n");
			}

			out.close();
		} catch (Exception ex) {
			new BigError(ex);
		} finally {
			try {
				out.close();
			} catch (IOException ex) { }
		}
    }

    private void befehleSpeichern(String content) {
        File f = new File("befehle-inbox");
        if (!f.exists()) {
            if(!f.mkdir()) {
                new BigError("Ordner 'befehle-inbox' existiert nicht und kann auch nicht angelegt werden.");
            }
        }

		int idx = 1;
        f = new File("befehle-inbox/" + idx + ".mail");
        while (f.exists()) {
            idx++;
            f = new File("befehle-inbox/" + idx + ".mail");
        }
        
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
            out.append(content);
            out.close();
        } catch (Exception ex) {
            new BigError(ex);
        } finally {
            try { 
				out.close();
			} catch (IOException ex) { }
        }
    }
}
