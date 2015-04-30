package de.x8bit.Fantasya.Host.EVA.io;

import java.io.BufferedReader;
import java.io.IOException;

public class CleanOrderReader {
	
	private static StringBuffer buf = new StringBuffer();
	private static StringBuffer tempBuf = new StringBuffer();
	private static String nextLineComment = null;
	
	private BufferedReader in;

	public CleanOrderReader(BufferedReader in) {
		if (in == null) throw new IllegalArgumentException("BufferedReader in is 'NULL'. CleanOrderReader needs a BufferedReader.");
		this.in = in;
	}
	
	public synchronized String readOrder() {
		try {
			if (nextLineComment != null) {
				String comment = nextLineComment;
				nextLineComment = null;
				return comment;
			}
			
			
			buf.setLength(0);
			
			if (!newLine(in, true)) return null;
			
			// Zeile mit Zeichen wird buf hinzugefügt
			buf.append(tempBuf);
			
			// Zeile wird mit nächster Zeile bei "\\" am Ende zusammengefügt.
			while (buf.charAt(buf.length() - 1) == '\\') {
				buf.delete(buf.length() - 1, buf.length());
				if (!newLine(in, false)) break;
				buf.append(tempBuf);
			}
			
			// Wenn die gesamte Zeile ein bleibende Kommentar ('//') ist, zurueckgeben
			if (buf.charAt(0) == '/' && buf.charAt(1) == '/')
				return buf.toString();
			
			// Wenn die gesamte Zeile ein temporärer Kommentar (';') ist, löschen und neue Zeile suchen
			if (buf.charAt(0) == ';')
				return readOrder();
			
			// Wenn der bleibende Kommentar nach einem Befehl kommt, den Kommentar temporär abtrennen.
			
			if (buf.indexOf("//")> -1) {
				cutOutCommentAfterOrder(false, 0);
			}
			
			if (buf.indexOf(";")> -1) {
				cutOutCommentAfterOrder(true, 0);
			}
			
			// Mehrfache Leerzeichen aus dem buf herausholen
			cutOutDoubleSpace();
			
			return buf.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "BUG";
	}
	
	private static void cutOutCommentAfterOrder(boolean tempComment, int begin) {
		String commentType = (tempComment) ? ";" : "//";
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
		} else {
			isComment = true;
		}
		int newCommentMark = buf.indexOf(commentType, quotationMark + 1);
		if (isComment) {
			if (!tempComment) {
				nextLineComment = buf.substring(commentMark);
			}
			buf.delete(commentMark, buf.length());
			tempBuf.setLength(0);
			tempBuf.append(buf);
			trim(false);
			buf.setLength(0);
			buf.append(tempBuf);
		} else if (newCommentMark > -1) {
			cutOutCommentAfterOrder(tempComment, newCommentMark);
		}
	}
	
	private static void cutOutDoubleSpace() {
		tempBuf.setLength(0);
		tempBuf.append(buf);
		for(int index = 1; index < tempBuf.length(); index++) {
			if (tempBuf.charAt(index - 1) == ' ' && tempBuf.charAt(index) == ' ') {
				buf.deleteCharAt(index);
				cutOutDoubleSpace();
				return;
			}
		}
	}
	
	private static boolean newLine(BufferedReader in, boolean trimBoth) throws IOException {
		// tempBuf wird geleert
		tempBuf.setLength(0);
		// Neue Zeile holen
		String line = in.readLine();
		// Solange die Zeile leer ist, wird eine Neue geholt
		while (line != null && line.length() == 0) {
			line = in.readLine();
		}
		// Wenn keine Zeile vorhanden ist, wird false zurückgegeben
		if (line == null) return false;
		// Zeile wird dem tempBuf hinzugefügt
		tempBuf.append(line);
		// vordere und hintere Leerzeichen werden entfernt
		trim(trimBoth);
		/* das vorhergehende wird solange praktiziert 
		 * bis eine Zeile mindestens ein Zeichen hat oder
		 *  der BufferedReader leer ist ('NULL') */
		while (tempBuf.length() == 0) {
			if (!newLine(in, trimBoth)) return false;
		}
		
		return true;
	}
	
	private static void trim(boolean both){
		int index;
		
		// find \t and replace with ' '
		for (index = 0; index < tempBuf.length(); index++) {
			if (tempBuf.charAt(index) == '\t') tempBuf.setCharAt(index, ' ');
		}
         
       //find the last character which is not space
		for(index = tempBuf.length(); index > 0; index--) {
			if(tempBuf.charAt(index-1) != ' ')
				break;
		}
         
        if (index == 1 && tempBuf.charAt(0) == ' ') {
        	tempBuf.setLength(0);
        	return;
        } else if (index < tempBuf.length()) {
        	tempBuf.delete(index, tempBuf.length());
		}
        
        if (both) {
        	//find the first character which is not space
            for(index = 0; index < tempBuf.length(); index++){
            	if(tempBuf.charAt(index) != ' ')
            		break;
            }
            
            if (index > 0)
            	tempBuf.delete(0, index);
        }
	}
}
