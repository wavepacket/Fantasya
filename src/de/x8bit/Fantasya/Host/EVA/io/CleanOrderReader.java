package de.x8bit.Fantasya.Host.EVA.io;

import java.io.BufferedReader;
import java.io.IOException;

public class CleanOrderReader {
	
	private static final String TEMP_COMMENT_START = ";";
	private static final String PERMANENT_COMMENT_START = "//";
	
	private static final int TRIM_BOTH = 0;
	private static final int TRIM_START = 1;
	private static final int TRIM_END = 2;
	
	private BufferedReader in;
	private String nextLineComment = null;
	private final StringBuffer buf = new StringBuffer();

	public CleanOrderReader(BufferedReader in) {
		if (in == null) {
			throw new IllegalArgumentException("BufferedReader in is 'NULL'. CleanOrderReader needs a BufferedReader.");
		}
		this.in = in;
	}
	
	public String readOrder() {
		
		String line;
		
		if (nextLineComment != null) {
			line = nextLineComment;
			nextLineComment = null;
			return line;
		}
		
		buf.setLength(0);
		
		line = nextLine();
		
		// Wenn das Ende des Streams erreicht ist oder ein Fehler aufgetreten ist, wird null zurückgegeben.
		if (line == null) {
			return null;
		}
		
		// StringBuffer bekommt die Zeile.
		buf.append(line);
		
		// Zeile wird mit nächster Zeile bei "\\" am Ende zusammengefügt.
		while (buf.charAt(buf.length() - 1) == '\\') {
			buf.delete(buf.length() - 1, buf.length());
			line = nextLine();
			if (line == null) {
				break;
			}
			buf.append(line);
		}
		
		// Die Zeile wird getrimmt.
		trimStringBuffer(buf, TRIM_BOTH);
		
		// Wenn die gesamte Zeile ein bleibende Kommentar ('//') ist, zurueckgeben
		if (buf.charAt(0) == '/' && buf.charAt(1) == '/') {
			return buf.toString();
		}
		
		// Wenn die gesamte Zeile ein temporärer Kommentar (';') ist, löschen und neue Zeile suchen
		if (buf.charAt(0) == ';') {
			return readOrder();
		}
		
		// Wenn der Kommentar hinter einem Befehl steht.
		cutOutCommentAfterOrder(buf, 0);
		
		// Mehrfache Leerzeichen aus dem buf herausholen
		cutOutRepeatedSpace(buf);
		
		// deutsche Umlaute aendern
		replaceGermanSpecialCharactersInStringBuffer(buf);
		
		// Befehle in Kleinbuchstaben
		toLowerCaseStringBuffer(buf);
		
		// Als String zurueckgeben.
		return buf.toString();
	}	
	
	private void cutOutCommentAfterOrder(StringBuffer buf, int begin) {
		String commentType = null;
		if (buf.indexOf(PERMANENT_COMMENT_START, begin)> -1) {
			commentType = PERMANENT_COMMENT_START;
		}
		else if (buf.indexOf(TEMP_COMMENT_START, begin)> -1) {
			commentType = TEMP_COMMENT_START;
		}
		if (commentType == null) {
			return;
		}
		int commentMark = buf.indexOf(commentType, begin);
		int quotationMark = buf.indexOf("\"");
		int quotationMarks = 0;
		boolean isComment = false;
		if (quotationMark > -1) {
			while (quotationMark > -1 && quotationMark < commentMark) {
				quotationMarks++;
				quotationMark = buf.indexOf("\"", quotationMark + 1);
			}
			// Wenn quotationMarks 0 ist oder durch 2 teilbar (gerade) dann ist es ein Komentar.
			if (quotationMarks == 0 || quotationMarks % 2 == 0) {
				isComment = true;
			}
		}
		else {
			isComment = true;
		}
		int newCommentMark = buf.indexOf(commentType, quotationMark + 1);
		if (isComment) {
			if (commentType != TEMP_COMMENT_START) {
				nextLineComment = buf.substring(commentMark);
			}
			buf.delete(commentMark, buf.length());
			trimStringBuffer(buf, TRIM_END);
		}
		else if (newCommentMark > -1) {
			cutOutCommentAfterOrder(buf, newCommentMark);
		}
	}
	
	private void cutOutRepeatedSpace(StringBuffer buf) {
		for(int index = 1; index < buf.length(); index++) {
			if (buf.charAt(index - 1) == ' ' && buf.charAt(index) == ' ') {
				buf.deleteCharAt(index);
				cutOutRepeatedSpace(buf);
				return;
			}
		}
	}
	
	private String nextLine() {
		try {
			
			String line = "";
			
			// bis eine Zeile mindestens ein Zeichen (außer Leer- und Tabzeichen) hat oder das Ende des Streams erreicht ist. 
			while (line.length() == 0) {
				// Solange die Zeile leer ist, wird eine Neue geholt.
				do {
					// Neue Zeile holen.
					line = in.readLine();
				} while (line != null && line.length() == 0);
				
				// Wenn das Ende des Streams erreicht ist, wird null zurueckgegeben.
				if (line == null) {
					return null;
				}
				
				// Eine Zeile darf nicht nur aus Leerzeichen (inkl. Tab) bestehen.
				if (!hasStringOrder(line)) {
					line = "";
				}
			}
			
			return line;

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean hasStringOrder(String line) {
		
		int length = line.length();
		
		if (length == 0) return false;
		
		char c;
		
		for (int i = 0; i < length; i++) {
			c = line.charAt(i);
			if (c != ' ' && c != '\t') {
				return true;
			}
		}
		return false;
	}
	
	private void trimStringBuffer(StringBuffer buf, int trimIt) {
		int index;
		
		// find \t and replace with ' '
		for (index = 0; index < buf.length(); index++) {
			if (buf.charAt(index) == '\t') {
				buf.setCharAt(index, ' ');
			}
		}
		
		if (trimIt == TRIM_END || trimIt == TRIM_BOTH) {
	         
	       //find the last character which is not space
			for(index = buf.length(); index > 0; index--) {
				if(buf.charAt(index-1) != ' ') {
					break;
				}
			}
	         
	        if (index == 1 && buf.charAt(0) == ' ') {
	        	buf.setLength(0);
	        	return;
	        }
	        else if (index < buf.length()) {
	        	buf.delete(index, buf.length());
			}
		}
		
		
        
        if (trimIt == TRIM_START || trimIt == TRIM_BOTH) {
        	//find the first character which is not space
            for(index = 0; index < buf.length(); index++) {
            	if(buf.charAt(index) != ' ') {
            		break;
            	}
            }
            
            if (index > 0) {
            	buf.delete(0, index);
            }
        }
	}
	
	private void replaceGermanSpecialCharactersInStringBuffer(StringBuffer buf) {
		// Alle Zeichen nach Sonderzeichen durchsuchen.
		char c;
		for (int i = 0; i < buf.length(); i++) {
			// Wenn ein Anfuehrungszeichen, oder Kommentar kommt, dann aufhoeren.
			c = buf.charAt(i);
			if (c == '\"' || c == ';' || (c == '/' && (i + 1) < buf.length() && buf.charAt(i + 1) == '/')) {
				break;
			}
			if (c == '\u00c4' || c == '\u00e4') { // Ä oder ä
				buf.delete(i, i + 1);
				buf.insert(i, "ae");
			}
			else if (c == '\u00d6' || c == '\u00f6') { // Ö oder ö
				buf.delete(i, i + 1);
				buf.insert(i,  "oe");
			}
			else if (c == '\u00dc' || c == '\u00fc') { // Ü oder ü
				buf.delete(i, i + 1);
				buf.insert(i,  "ue");
			}
			else if (c == '\u00df') { // ß
				buf.delete(i, i + 1);
				buf.insert(i,  "ss");
			}
		}
	}
	
	private void toLowerCaseStringBuffer(StringBuffer buf) {
		// Alle Zeichen nach Grossbuchstaben durchsuchen und zu kleinbuchstaben zu machen durchsuchen.
		char c;
		for (int i = 0; i < buf.length(); i++) {
			// Wenn ein Anfuehrungszeichen, oder Kommentar kommt, dann aufhoeren.
			c = buf.charAt(i);
			if (c == '\"' || c == ';' || (c == '/' && (i + 1) < buf.length() && buf.charAt(i + 1) == '/')) {
				break;
			}
			// Wenn ein Grossbuchstabe, dann mache daraus einen Kleinbuchstaben
			if (Character.isUpperCase(c)) {
				buf.setCharAt(i, Character.toLowerCase(c));
			}
		}
	}
}
