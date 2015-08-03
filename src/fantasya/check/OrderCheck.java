package fantasya.check;

import java.util.ArrayList;
import java.util.List;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Messages.Debug;
import de.x8bit.Fantasya.Host.EVA.BefehleEinlesen;
import de.x8bit.Fantasya.Host.EVA.util.BefehlsMuster;
import de.x8bit.Fantasya.Host.EVA.util.ZATMode;
import de.x8bit.Fantasya.Host.EVA.util.BefehlsMuster.Art;

public class OrderCheck {

	public static void main(String[] args) {
		// Befehlsdatei mit Pfad als argument
		// 1. Befehlsdatei auf Existenz prüfen.
		// 2. Über den Namen der Datei bekommt man die playerID. 
		//		Diese Partei mit seinen Einheiten aus der Datenbank laden
		// 3. Befehlsdatei über FileOrderReader laden
		// 4. Die Nachrichten der Partei sind die Fehlermeldungen der Befehle.
		//		Diese in einer Datei speichern und per Skript an den Spieler verschicken.
	}

}
/*
 * // Wenn Befehls-Check-Modus, dann Partei ohne (neue) Befehle ignorieren:
		if (ZATMode.CurrentMode().isBefehlsCheck()) {
			if (check.getParteien().isEmpty()) {
				System.out.println("Es gibt keine neuen Befehlsdateien zum überprüfen.");
				System.exit(0);
			}

			List<Partei> ignoreList = new ArrayList<Partei>();
			ignoreList.addAll(Partei.PROXY);
			for (Partei p : check.getParteien()) {
				if (check.hasValidBefehle(p)) {
					ignoreList.remove(p);
				}
			}
			for (Partei p : ignoreList) {
				ZATMode.CurrentMode().setIgnore(p);
			}
			if (ZATMode.CurrentMode().hatIgnorierteParteien()) {
				for (int pNr : ZATMode.CurrentMode().getIgnorierteParteiNummern()) {
					new Debug("Ignoriere Partei " + Partei.getPartei(pNr));
				}
			}
		} 
		
		for (BefehlsMuster pattern : getMuster()) {
			addTemplate(pattern.getRegex());
		} */
/*
 * public static List<BefehlsMuster> getMuster() {
        List<BefehlsMuster> retval = new ArrayList<BefehlsMuster>();

		// Einstieg
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 1, "^(fantasya) [a-z0-9]{1,4} (\")[a-zA-Z0-9]+(\")$", "f", Art.KURZ));
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 1, "^(eressea) [a-z0-9]{1,4} (\")[a-zA-Z0-9]+(\")$", "e", Art.KURZ));
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 1, "^(partei) [a-z0-9]{1,4} (\")[a-zA-Z0-9]+(\")$", "p", Art.KURZ));

		// diverses von Magellan
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 2, "^(locale) [a-zA-Z0-9]+$", "l", Art.KURZ)); 	// welche Sprache auch immer verwendet wird
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 3, "^(region) .+$", "r", Art.KURZ));

		// Rundenmarkierung - FCheck meldet dann entsprechend eine Warnung ... dadurch
		// können Spieler nicht unbedingt den entsprechenden falschen Rundenbefehl einschicken
		retval.add(new BefehlsMuster(BefehleEinlesen.class, 6, "^(runde) [0-9]{1,4}$", "r", Art.KURZ));

		// eine Einheit beginnt
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 4, "^(einheit) [a-z0-9]{1,4}$", "e", Art.KURZ));

		// ... der nächste Bitte
        retval.add(new BefehlsMuster(BefehleEinlesen.class, 5, "^(naechster)$", "n", Art.KURZ));

		return retval;
    }
 * */
