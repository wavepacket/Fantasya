package de.x8bit.Fantasya.Host.ManualTests.io;

import java.io.BufferedReader;
import java.io.StringReader;

import de.x8bit.Fantasya.Host.EVA.io.CleanOrderReader;

public class CleanOrderReaderTest {
	
	private static String[] testOrderStringArray =  {
			"MACHE Holz",
			"MACHE 15 Schwert\nBETRETE GEBAEUDE tx\nNACH o w", // Befehle gehen so durch
			"\tHELFE  4 KAEMPFE ", // Leerzeichen rausnehmen
			"BESCHREIBE EINHEIT 7 \"bli blopp\\\n bla blupp\"", // Befehle Zusammenfuegen
			"MACHE Gold\n ; hilfe kommentar\nSTIRB", // temporäre Kommentare rausnehmen, die am Anfang der Zeile stehen.
			"MACHE Gold\n // hilfe nochmal Gold absahnen :)\nSTIRB", // bleibende Kommentare, die am Anfang der Zeile stehen.
			"MACHE Gold // hilfe nochmal Gold absahnen :)\nSTIRB", // bleibende Kommentare, der nach einem Befehl ohne '"' steht.
			"BESCHREIBE EINHEIT \"bla\"// hilfe nochmal Gold absahnen :)\nSTIRB", // bleibende Kommentare, der nach einem Befehl mit '"' steht.
			"BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :)\"\nSTIRB", // Merkmal des bleibenden Kommentars innerhalb einer Beschreibung
			"BESCHREIBE EINHEIT \"bla\"; hilfe nochmal Gold absahnen :)\nSTIRB", // temporäre Kommentare, der nach einem Befehl mit '"' steht.
			"BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :)\"// ... und es macht Spaß\nSTIRB", // Merkmal des bleibenden Kommentars innerhalb und außerhalb einer Beschreibung
			"BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :) // tja, mal sehen ;)\"// ... und es macht Spaß\nSTIRB", // Merkmal des bleibenden Kommentars innerhalb und außerhalb einer Beschreibung
			"MACHE 15 Schwert\n\nBETRETE GEBAEUDE tx\n  \nNACH o w", // Befehle gehen so durch, sind aber leerzeilen bzw. nur mit leerzeichen vorhanden
			"BESCHREIBE EINHEIT 7 \"bli blopp\\", // Befehle Zusammenfuegen
			"BESCHREIBE EINHEIT 7 \"bli blopp\\\n bla blupp \\\n test\"", // Befehle Zusammenfuegen
			"MACHE 15 Schwert\nBETRETE GEBÄUDE tx\nNACH o w\nBETRETE GEBÄUDE xy\n// test Ä Ö Ü ß ä ö ü", // Befehle gehen so durch mit Umlauten
			"MACHE 15 Schwert\nBETRETE GEBÄUDE tx\nNACH o w\nMACHE STRAßE no\nBENENNE EINHEIT \"test Ä Ö Ü ß ä ö ü\"" // Befehle gehen so durch mit Umlauten
	};
	
	
	public static void main(String[] args) {
		testComplete();
	}
	
	private static void testComplete() {
		CleanOrderReader cOR;
		String str;
		for (int i = 0; i < testOrderStringArray.length; i++) {
			str = testOrderStringArray[i];
			System.out.println("\nTest " + i + ":");
			System.out.println("Befehlstext: ");
			System.out.println(str + '\n');
			System.out.println("Befehle:");
			cOR = new CleanOrderReader(new BufferedReader(new StringReader(str)));
			str = cOR.readOrder();
			while (str != null) {
				System.out.println("'" + str + "'");
				str = cOR.readOrder();
			}
		}
	}
	
	
}
