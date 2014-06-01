package de.x8bit.Fantasya.Atlantis.Messages;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Host.Datenbank;
import de.x8bit.Fantasya.Host.SMSKaufen;
import de.x8bit.Fantasya.Host.eMail;

/**
 * sollte irgend wann mal eine Instanz von der Klasse auftauchen, dann gab es ernsthafte Probleme  
 * @author  mogel
 */
public class BigError extends de.x8bit.Fantasya.Atlantis.Message
{
	private static int count = 0;
	
	
	
	/** TRUE wenn eine e-mail beim Crash gesendet werden soll*/
	private static boolean email = false;
	public static boolean getEMail() { return email; }
	public static void setEMail(boolean value) { email = value; } 
	
	/** Empfänge für Crash-Email */
	private static String empfaenger = "user@example.com";
	public static void setEmpfaenger(String value) { empfaenger = value; }
	public static String getEmpfaenger() { return empfaenger; }
	
	/**
	 * Konstruktor für die Instantiierung via Reflection beim Laden aus der DB
	 */
	public BigError() {}

	public BigError(Exception ex)
	{
		super();
		
		System.err.println(ex.getCause());
		System.err.println(ex.getMessage());
		
		StringBuilder msg = new StringBuilder();
		msg.append(ex.getClass().getSimpleName());
		
		for(StackTraceElement ste : ex.getStackTrace())
		{
			msg.append("-");
			msg.append(ste.getFileName()).append(":").append(ste.getLineNumber());
		}

		
		printMessage(msg.toString());
		ex.printStackTrace();
		Datenbank.ListQueryStack();
		System.exit(1);		// System muss angehalten werden !!!
	}
	
	public BigError(String msg)
	{
		super();
		printMessage(msg);
		Datenbank.ListQueryStack();
		System.exit(1);
	}
	
	public BigError(Exception ex, String msg) {
		super();

		StringBuilder exMsg = new StringBuilder();
		exMsg.append(ex.getClass().getSimpleName());

		for(StackTraceElement ste : ex.getStackTrace())	{
			exMsg.append("-");
			exMsg.append(ste.getFileName()).append(":").append(ste.getLineNumber()).append("\n");
		}


		printMessage(msg + "\n\n" + exMsg.toString());
		ex.printStackTrace();
		Datenbank.ListQueryStack();
		System.exit(1);		// System muss angehalten werden !!!
    }

    private void printMessage(String text)
	{
		if (count++ > 0)
		{
			System.err.println("----- SYSTEM STOP");
			System.exit(1);
		}
		
		System.out.println("[-!!!-] " + text);
		print(0, text, Partei.getPartei(0)); // die Nachricht in die DB schieben
	
		Datenbank.ListQueryStack();

		if (email)
		{
			eMail e = new eMail();
			e.setEmpfaenger(empfaenger);
			e.setSubject("CRASH");
			e.Text.add(text + " -- " + lastMessage);
			e.Send();
			
			Datenbank db = new Datenbank("BigError");
			db.SaveSettings("zatstatus.text", "Game Over! - CRASH");
			db.Close();
		}
		
		SMSKaufen.sendSMS(text);
	}
}
