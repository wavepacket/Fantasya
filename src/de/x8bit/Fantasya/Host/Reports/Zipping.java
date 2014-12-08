package de.x8bit.Fantasya.Host.Reports;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;

import de.x8bit.Fantasya.Atlantis.Message;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Messages.BigError;
import de.x8bit.Fantasya.Atlantis.Messages.Debug;
import de.x8bit.Fantasya.Atlantis.Messages.SysErr;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Host.Datenbank;
import de.x8bit.Fantasya.Host.GameRules;
import de.x8bit.Fantasya.Host.Main;
import de.x8bit.Fantasya.Host.eMail;
import de.x8bit.Fantasya.Host.EVA.util.ZATMode;
import de.x8bit.Fantasya.Host.Reports.util.RegionReportComparatorLNR;
import de.x8bit.Fantasya.util.net.SMTPConnector;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Ob wirklich e-mails versendet werden, wird über
 * ZATMode.CurrentMode().isSendReportMails() bestimmt ... 
 * wird mit der Option -reporte automatisch unterdrückt ... wenn doch versendet
 * werden soll (weil änderung an was auch immer) dann muss die Option -email
 * mit angegeben werden.
 */
public class Zipping
{
	/** 
	 * das Salz in der Suppe ... <b>public</b>, damit bei dem einzelnen schreiben
	 * von Reporten das Salt korrekt gesetz werden kann
	 */
	public static String salz = "empty";
	
	/**
	 * 
	 * @param owner - ZIP für diese Partei
	 */
	public Zipping(Partei p)
	{
		CheckSalt();
		
		String files [] = new String [] {
											GameRules.getRunde() + "-" + p.getNummerBase36() + ".cr",
											GameRules.getRunde() + "-" + p.getNummerBase36() + ".zr",
											GameRules.getRunde() + "-" + p.getNummerBase36() + ".nr",
											GameRules.getRunde() + "-" + p.getNummerBase36() + ".nr2",
											GameRules.getRunde() + "-" + p.getNummerBase36() + ".xml"
										};
		
		try 
		{
			File f = new File("zip/" + GameRules.getRunde());
			if (!f.exists()) f.mkdirs();
			
			byte[] buf = new byte[65536];
			ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream(new FileOutputStream("zip/" + GameRules.getRunde() + "/" + p.getNummerBase36() + ".zip")) );
			for (int i = 0; i < files.length; ++i) 
			{
				String fname = files[i];
				InputStream in = new BufferedInputStream(new FileInputStream("reporte/" + fname));
				out.putNextEntry(new ZipEntry(fname));
				int len;
				while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
				in.close();
			}
			if (p.getMonster() > 1)	{
				InputStream in = null;
				if (p.getMonster() == 2) in = new BufferedInputStream(new FileInputStream("world.xml")); // TODO geo.xml
				if (p.getMonster() == 3) in = new BufferedInputStream(new FileInputStream("world.xml"));
				out.putNextEntry(new ZipEntry("monster.xml"));
				int len;
				while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
				in.close();
			}
		   out.close();
		   
		   // löschen im ZIP-Verzeichnis
		   f = new File("zip/" + (GameRules.getRunde() - 10));
		   if (f.exists())
		   {
			   for(File d : f.listFiles()) d.delete();
			   f.delete();
		   }
		} catch (IOException e) 
		{
			new SysErr(e.toString());
		}
		
        if (ZATMode.CurrentMode().getSendMailMethode().equals(SMTPConnector.class.getSimpleName())) {
            if (p.getEMail().length() == 0) {
                new SysMsg("Partei " + p + " hat keine E-Mail-Adresse - der Report wird nicht verschickt.");
                return;
            }

            SMTPConnector conn = new SMTPConnector();
            File zip = new File("zip/" + GameRules.getRunde() + "/" + p.getNummerBase36() + ".zip");
            try {
                conn.sendReport(p, zip);
            } catch (IOException ex) {
                new BigError(ex);
            } catch (MessagingException ex) {
                new BigError(ex);
            }
        } else {
            // der Normalfall:
            eMail e = new eMail();
            e.setSubject("[Fantasya] Auswertung - Runde " + GameRules.getRunde());
            e.setEmpfaenger(p.getEMail());
            e.Text.add("Hi,");
            e.Text.add("");
			
			greetingsSchreiben(p, e);
			
            e.Text.add("Du kannst die Auswertung von der Website herunter laden:");
            e.Text.add("http://www.fantasya-pbem.de/?site=homepage&action=fantasya&modus=auswertung&gameid=" + Main.GameID + "&select");
            e.Text.add("");
            e.Text.add("Deine Partei: " + p);

            
			botschaftenSchreiben(p, e); // hier die Botschaften einfügen
            

            if (ZATMode.CurrentMode().isDebug()) {
                StringBuffer sb = new StringBuffer();
                for (String line  : e.Text) sb.append(line).append("\n");
                new Debug("AW-Mail an " + e.getEmpfaenger() + ":\n" + sb);
            }

            if (ZATMode.CurrentMode().isSendReportMails()) {
                e.Send();
            }
        }
	}

    private void greetingsSchreiben(Partei p, eMail e) {
        for (Message msg : Message.Retrieve(p, null, null, "Greetings")) {
			e.Text.add(msg.getText());
            e.Text.add("");
		}
	}
	
    private void botschaftenSchreiben(Partei p, eMail e) {
        List<Message> messages = Message.Retrieve(null, null, null, "Botschaft");
        List<Message> parteiBotschaften = new ArrayList<Message>();
        List<Message> einheitenBotschaften = new ArrayList<Message>();
        for (Message msg : messages) {
            if (msg.getUnit() != null) {
                if (msg.getUnit().getOwner() == p.getNummer()) einheitenBotschaften.add(msg);
            } else {
                Partei recipient = msg.getPartei();
                if (recipient != null) {
                    if ((recipient.getNummer() != 0) && (recipient.getNummer() != p.getNummer())) {
                        continue; // ist für eine andere Partei
                    }
                }

                if (msg.getCoords() != null) {
                    Region r = Region.Load(msg.getCoords());
                    if (!p.canAccess(r)) continue; // die Region ist für p nicht sichtbar
                    if (Unit.CACHE.getAll(r.getCoords(), p.getNummer()).isEmpty()) {
						// niemand hier
						continue;
					}
                }
                parteiBotschaften.add(msg);
            }
        }

        if (!parteiBotschaften.isEmpty()) {
            e.Text.add("");
            e.Text.add("Öffentliche & Parteibotschaften");
            e.Text.add("---------------------------------");

            SortedSet<Region> regionen = new TreeSet<Region>(new RegionReportComparatorLNR());
            List<Message> ohneOrt = new ArrayList<Message>();
            for (Message msg : parteiBotschaften) {
                if (msg.getCoords() != null) {
                    regionen.add(Region.Load(msg.getCoords()));
                } else {
                    ohneOrt.add(msg);
                }
            }

            // ohne Region (?)
            if (!ohneOrt.isEmpty()) {
                for (Message msg : ohneOrt) {
                    e.Text.add("");
                    e.Text.add("" + msg.getText());
                }
                e.Text.add("");
                e.Text.add("");
            }

            for (Region r : regionen) {
                e.Text.add(r + " " + p.getPrivateCoords(r.getCoords()) + ":");
                for (Message msg : parteiBotschaften) {
                    if (msg.getCoords() == null) continue;
                    if (!msg.getCoords().equals(r.getCoords())) continue;
                    e.Text.add("");
                    e.Text.add("  " + msg.getText());
                }
                e.Text.add("");
                e.Text.add("");
            }
        }

        if (!einheitenBotschaften.isEmpty()) {
            e.Text.add("");
            e.Text.add("Persönliche Botschaften (an einzelne Einheiten)");
            e.Text.add("---------------------------------");

            SortedSet<Region> regionen = new TreeSet<Region>(new RegionReportComparatorLNR());
            for (Message msg : einheitenBotschaften) {
                regionen.add(Region.Load(msg.getUnit().getCoords()));
            }

            for (Region r : regionen) {
                e.Text.add(r + " " + p.getPrivateCoords(r.getCoords()) + ":");

                SortedSet<Unit> einheiten = new TreeSet<Unit>();
                for (Message msg : einheitenBotschaften) {
                    einheiten.add(msg.getUnit());
                }

                for (Unit u : einheiten) {
                    e.Text.add("");
                    e.Text.add("  An " + u + ":");
                    for (Message msg : einheitenBotschaften) {
                        if (msg.getUnit().getNummer() != u.getNummer()) continue; // dat sind wa nich.
                        e.Text.add("    " + msg.getText());
                    }
                }
                e.Text.add("");
                e.Text.add("");
            }
        }
    }

	private void CheckSalt()
	{
		boolean enabled = Datenbank.isEnabled();
		if (!enabled) Datenbank.Enable();
		if (salz.equals("empty"))
		{
			salz = Long.toString(System.currentTimeMillis());
			Datenbank db = new Datenbank("salz speichern");
			db.SaveSettings("game.salt", salz);
			db.Close();
		}
		if (!enabled) Datenbank.Disable();
	}
}
