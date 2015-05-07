package de.x8bit.Fantasya.Host.ManualTests.io;

import java.io.BufferedReader;
import java.io.StringReader;

import de.x8bit.Fantasya.Host.EVA.io.CleanOrderReader;

public class CleanOrderReaderTest {
	
	private static String s00 = "MACHE Holz";
	private static String s01 = "MACHE 15 Schwert\nBETRETE GEBAEUDE tx\nNACH o w"; // Befehle gehen so durch
	private static String s02 = "\tHELFE  4 KAEMPFE "; // Leerzeichen rausnehmen
	private static String s03 = "BESCHREIBE EINHEIT 7 \"bli blopp\\\n bla blupp\""; // Befehle Zusammenfuegen
	private static String s04 = "MACHE Gold\n ; hilfe kommentar\nSTIRB"; // temporäre Kommentare rausnehmen, die am Anfang der Zeile stehen.
	private static String s05 = "MACHE Gold\n // hilfe nochmal Gold absahnen :)\nSTIRB"; // bleibende Kommentare, die am Anfang der Zeile stehen.
	private static String s06 = "MACHE Gold // hilfe nochmal Gold absahnen :)\nSTIRB"; // bleibende Kommentare, der nach einem Befehl ohne '"' steht.
	private static String s07 = "BESCHREIBE EINHEIT \"bla\"// hilfe nochmal Gold absahnen :)\nSTIRB"; // bleibende Kommentare, der nach einem Befehl mit '"' steht.
	private static String s08 = "BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :)\"\nSTIRB"; // Merkmal des bleibenden Kommentars innerhalb einer Beschreibung
	private static String s09 = "BESCHREIBE EINHEIT \"bla\"; hilfe nochmal Gold absahnen :)\nSTIRB"; // temporäre Kommentare, der nach einem Befehl mit '"' steht.
	private static String s10 = "BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :)\"// ... und es macht Spaß\nSTIRB"; // Merkmal des bleibenden Kommentars innerhalb und außerhalb einer Beschreibung
	private static String s11 = "BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :) // tja, mal sehen ;)\"// ... und es macht Spaß\nSTIRB"; // Merkmal des bleibenden Kommentars innerhalb und außerhalb einer Beschreibung
	private static String s12 = "MACHE 15 Schwert\n\nBETRETE GEBAEUDE tx\n  \nNACH o w"; // Befehle gehen so durch, sind aber leerzeilen bzw. nur mit leerzeichen vorhanden
	private static String s13 = "BESCHREIBE EINHEIT 7 \"bli blopp\\"; // Befehle Zusammenfuegen
	private static String s14 = "BESCHREIBE EINHEIT 7 \"bli blopp\\\n bla blupp \\\n test\""; // Befehle Zusammenfuegen
	
	
	public static void main(String[] args) {
		testComplete();
	}
	
	private static void testComplete() {
		CleanOrderReader cOR00 = new CleanOrderReader(new BufferedReader(new StringReader(s00)));
		CleanOrderReader cOR01 = new CleanOrderReader(new BufferedReader(new StringReader(s01)));
		CleanOrderReader cOR02 = new CleanOrderReader(new BufferedReader(new StringReader(s02)));
		CleanOrderReader cOR03 = new CleanOrderReader(new BufferedReader(new StringReader(s03)));
		CleanOrderReader cOR04 = new CleanOrderReader(new BufferedReader(new StringReader(s04)));
		CleanOrderReader cOR05 = new CleanOrderReader(new BufferedReader(new StringReader(s05)));
		CleanOrderReader cOR06 = new CleanOrderReader(new BufferedReader(new StringReader(s06)));
		CleanOrderReader cOR07 = new CleanOrderReader(new BufferedReader(new StringReader(s07)));
		CleanOrderReader cOR08 = new CleanOrderReader(new BufferedReader(new StringReader(s08)));
		CleanOrderReader cOR09 = new CleanOrderReader(new BufferedReader(new StringReader(s09)));
		CleanOrderReader cOR10 = new CleanOrderReader(new BufferedReader(new StringReader(s10)));
		CleanOrderReader cOR11 = new CleanOrderReader(new BufferedReader(new StringReader(s11)));
		CleanOrderReader cOR12 = new CleanOrderReader(new BufferedReader(new StringReader(s12)));
		CleanOrderReader cOR13 = new CleanOrderReader(new BufferedReader(new StringReader(s13)));
		CleanOrderReader cOR14 = new CleanOrderReader(new BufferedReader(new StringReader(s14)));
		
		System.out.println("Test 00:");
		System.out.println("Befehlstext: ");
		System.out.println(s00 + '\n');
		System.out.println("Befehle:");
		String s = cOR00.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR00.readOrder();
		}
		System.out.println("\nTest 01:");
		System.out.println("Befehlstext: ");
		System.out.println(s01 + '\n');
		System.out.println("Befehle:");
		s = cOR01.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR01.readOrder();
		}
		System.out.println("\nTest 02:");
		System.out.println("Befehlstext: ");
		System.out.println(s02 + '\n');
		System.out.println("Befehle:");
		s = cOR02.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR02.readOrder();
		}
		System.out.println("\nTest 03:");
		System.out.println("Befehlstext: ");
		System.out.println(s03 + '\n');
		System.out.println("Befehle:");
		s = cOR03.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR03.readOrder();
		}
		System.out.println("\nTest 04:");
		System.out.println("Befehlstext: ");
		System.out.println(s04 + '\n');
		System.out.println("Befehle:");
		s = cOR04.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR04.readOrder();
		}
		System.out.println("\nTest 05:");
		System.out.println("Befehlstext: ");
		System.out.println(s05 + '\n');
		System.out.println("Befehle:");
		s = cOR05.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR05.readOrder();
		}
		System.out.println("\nTest 06:");
		System.out.println("Befehlstext: ");
		System.out.println(s06 + '\n');
		System.out.println("Befehle:");
		s = cOR06.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR06.readOrder();
		}
		System.out.println("\nTest 07:");
		System.out.println("Befehlstext: ");
		System.out.println(s07 + '\n');
		System.out.println("Befehle:");
		s = cOR07.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR07.readOrder();
		}
		System.out.println("\nTest 08:");
		System.out.println("Befehlstext: ");
		System.out.println(s08 + '\n');
		System.out.println("Befehle:");
		s = cOR08.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR08.readOrder();
		}
		System.out.println("\nTest 09:");
		System.out.println("Befehlstext: ");
		System.out.println(s09 + '\n');
		System.out.println("Befehle:");
		s = cOR09.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR09.readOrder();
		}
		System.out.println("\nTest 10:");
		System.out.println("Befehlstext: ");
		System.out.println(s10 + '\n');
		System.out.println("Befehle:");
		s = cOR10.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR10.readOrder();
		}
		System.out.println("\nTest 11:");
		System.out.println("Befehlstext: ");
		System.out.println(s11 + '\n');
		System.out.println("Befehle:");
		s = cOR11.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR11.readOrder();
		}
		System.out.println("\nTest 12:");
		System.out.println("Befehlstext: ");
		System.out.println(s12 + '\n');
		System.out.println("Befehle:");
		s = cOR12.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR12.readOrder();
		}
		System.out.println("\nTest 13:");
		System.out.println("Befehlstext: ");
		System.out.println(s13 + '\n');
		System.out.println("Befehle:");
		s = cOR13.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR13.readOrder();
		}
		System.out.println("\nTest 14:");
		System.out.println("Befehlstext: ");
		System.out.println(s14 + '\n');
		System.out.println("Befehle:");
		s = cOR14.readOrder();
		while (s != null) {
			System.out.println("'" + s + "'");
			s = cOR14.readOrder();
		}
	}
	
	
}
