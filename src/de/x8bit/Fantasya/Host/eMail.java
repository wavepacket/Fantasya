package de.x8bit.Fantasya.Host;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import de.x8bit.Fantasya.Atlantis.Messages.BigError;
import de.x8bit.Fantasya.Atlantis.Messages.SysErr;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;

/**
 * @author  mogel
 */
public class eMail
{
	private static Socket socket = null;
	private static Scanner in = null;
	private static PrintWriter out = null;
	
	/** Absender der Auswertungen*/
	private static String sender = "fantasya@example.com";
	public static String getSender() { return sender; }
	public static void setSender(String value) { sender = value; }
	
	/** SMTP Server */
	private static String server = "127.0.0.1";
	public static String getServer() { return server; }
	public static void setServer(String value) { server = value; }
	
	/** Empfänger der Auswertung */
	private String empfaenger = "fantasya@example.com";
	public void setEmpfaenger(String value) { empfaenger = value; }
	public String getEmpfaenger() { return empfaenger; }
	
	/** SUBJECT der eMail*/
	private String subject = "[Fantasya] Information";
	public void setSubject(String value) { subject = value; }
	public String getSubject() { return subject; } 
	
	
	/** 
	 * Textblock der eMail
	 * <b>public</b> weil aus Main.java darauf zugegriffen wird (Rundschreiben)  
	 */
	public ArrayList<String> Text = new ArrayList<String>();
	
	public void Send()
	{
        if (socket == null)
		{
			new SysMsg("Verbindung zum SMTP '" + getServer() + "' aufbauen");
			try
			{
				socket = new Socket(getServer(), 25 );
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream());
				read(); // SMTP-Status/Server
				send("EHLO Fantasya");
				read();
			} catch(Exception ex) { new SysErr(ex.getMessage()); socket = null; return; }
		}
		
		send("MAIL FROM:<" + getSender() + ">");
		read();
		send("RCPT TO:<" + getEmpfaenger() + ">");
		read();
		send("DATA");
		read();
		send("To: " + getEmpfaenger());
		send("From: " + getSender());
		send("Subject: " + getSubject());
        // TODO: Zeichenmüll verhindern?
        // send("Content-Transfer-Encoding: 8bit");
        // send("Content-Type: text/plain; charset=ISO-8859-1");
        // send("X-Mailer: Fantasya PbEM Host v" + Main.FANTAVERSION + " (http://www.fantasya-pbem.de/)");
		send("");
		for(int i = 0; i < Text.size(); i++) send(Text.get(i));
		send(".");
		read();
	}
	
	private void send(String msg)
	{
		new SysMsg(5, "<<< " + checkMsg(msg));
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (Exception ex) {
            new BigError(ex, "Problem bei eMail.send().out.write()");
        }
	}
	
	private void read()
	{
		try {
			String msg = checkMsg(in.nextLine());
			new SysMsg(5, ">>> " + msg);
		} catch(Exception ex) {
			new SysErr(ex.getMessage());
		}
	}
	
	private String checkMsg(String msg)	{
		if (msg.indexOf("@") > 0) return msg.substring(0, 10) + " <cut>";
		if (msg.startsWith("http://")) return msg.substring(0, 10) + " <cut>";
		return msg;
	}
}
