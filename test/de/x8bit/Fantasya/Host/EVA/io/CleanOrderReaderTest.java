package de.x8bit.Fantasya.Host.EVA.io;

import java.io.BufferedReader;
import java.io.StringReader;
import static org.junit.Assert.*;
import org.junit.Test;

public class CleanOrderReaderTest {
	
	private CleanOrderReader generateReader(String s) {
		return new CleanOrderReader(new BufferedReader(new StringReader(s)));
	}

	// note: may be bad style to throw in the constructor (memory leak?), although
	// this should not be a serious issue for us
	@Test(expected = IllegalArgumentException.class)
	public void rejectNullStream() {
		new CleanOrderReader(null);
	}

	@Test
	public void readMultipleCommandsSequentially() {
		String command1 = "MACHE 15 Schwert";
		String command2 = "BETRETE GEBAEUDE tx";
		String command3 = "NACH o w";
		CleanOrderReader reader = generateReader(command1 + "\n" + command2 + "\n" + command3);

		assertEquals(command1, reader.readOrder());
		assertEquals(command2, reader.readOrder());
		assertEquals(command3, reader.readOrder());
	}

	@Test
	public void removeWhitespaces() {
		String trimmedCommand = "HELFE 4 KAEMPFE";
		String input = "\tHELFE    4 KAEMPFE ";
		CleanOrderReader reader = generateReader(input);

		assertEquals(trimmedCommand, reader.readOrder());
	}

	@Test
	public void joinLines() {
		String command = "HELFE 4 KAEMPFE";
		String input = "HELFE 4 \\\nKAEMPFE";

		CleanOrderReader reader = generateReader(input);

		assertEquals(command, reader.readOrder());
	}


	// not yet fixed/migrated remainder
	private static String s04 = "MACHE Gold\n ; hilfe kommentar\nSTIRB"; // temporäre Kommentare rausnehmen, die am Anfang der Zeile stehen.
	private static String s05 = "MACHE Gold\n // hilfe nochmal Gold absahnen :)\nSTIRB"; // bleibende Kommentare, die am Anfang der Zeile stehen.
	private static String s06 = "MACHE Gold // hilfe nochmal Gold absahnen :)\nSTIRB"; // bleibende Kommentare, der nach einem Befehl ohne '"' steht.
	private static String s07 = "BESCHREIBE EINHEIT \"bla\"// hilfe nochmal Gold absahnen :)\nSTIRB"; // bleibende Kommentare, der nach einem Befehl mit '"' steht.
	private static String s08 = "BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :)\"\nSTIRB"; // Merkmal des bleibenden Kommentars innerhalb einer Beschreibung
	private static String s09 = "BESCHREIBE EINHEIT \"bla\"; hilfe nochmal Gold absahnen :)\nSTIRB"; // temporäre Kommentare, der nach einem Befehl mit '"' steht.
	private static String s10 = "BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :)\"// ... und es macht Spaß\nSTIRB"; // Merkmal des bleibenden Kommentars innerhalb und außerhalb einer Beschreibung
	private static String s11 = "BESCHREIBE EINHEIT \"bla// hilfe nochmal Gold absahnen :) // tja, mal sehen ;)\"// ... und es macht Spaß\nSTIRB"; // Merkmal des bleibenden Kommentars innerhalb und außerhalb einer Beschreibung
	private static String s12 = "MACHE 15 Schwert\n\nBETRETE GEBAEUDE tx\n  \nNACH o w"; // Befehle gehen so durch, sind aber leerzeilen bzw. nur mit leerzeichen vorhanden


	@Test
	public void testComplete() {
		CleanOrderReader cOR04 = new CleanOrderReader(new BufferedReader(new StringReader(s04)));
		CleanOrderReader cOR05 = new CleanOrderReader(new BufferedReader(new StringReader(s05)));
		CleanOrderReader cOR06 = new CleanOrderReader(new BufferedReader(new StringReader(s06)));
		CleanOrderReader cOR07 = new CleanOrderReader(new BufferedReader(new StringReader(s07)));
		CleanOrderReader cOR08 = new CleanOrderReader(new BufferedReader(new StringReader(s08)));
		CleanOrderReader cOR09 = new CleanOrderReader(new BufferedReader(new StringReader(s09)));
		CleanOrderReader cOR10 = new CleanOrderReader(new BufferedReader(new StringReader(s10)));
		CleanOrderReader cOR11 = new CleanOrderReader(new BufferedReader(new StringReader(s11)));
		CleanOrderReader cOR12 = new CleanOrderReader(new BufferedReader(new StringReader(s12)));
		
		System.out.println("\nTest 04:");
		System.out.println("Befehlstext: ");
		System.out.println(s04 + '\n');
		System.out.println("Befehle:");
		String s = cOR04.readOrder();
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
	}
}
