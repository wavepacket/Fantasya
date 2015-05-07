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

	public CleanOrderReader(BufferedReader in) {
		if (in == null) {
			throw new IllegalArgumentException("BufferedReader in is 'NULL'. CleanOrderReader needs a BufferedReader.");
		}
		this.in = in;
	}
	
	public String readOrder() {
		if (nextLineComment != null) {
			String comment = nextLineComment;
			nextLineComment = null;
			return comment;
		}
		
		final StringBuffer buf = new StringBuffer();
		buf.setLength(0);
		
		if (!trimmedNewLine(buf, TRIM_BOTH)) {
			return null;
		}
		
		if (buf.charAt(buf.length() - 1) == '\\') {
			StringBuffer addBuf = new StringBuffer();
			do {
				buf.delete(buf.length() - 1, buf.length());
				addBuf.setLength(0);
				if (!trimmedNewLine(addBuf, TRIM_END)) {
					break;
				}
				buf.append(addBuf);
			} while (buf.charAt(buf.length() - 1) == '\\');
		}
		// Zeile wird mit nächster Zeile bei "\\" am Ende zusammengefügt.
		/*while (buf.charAt(buf.length() - 1) == '\\') {
			buf.delete(buf.length() - 1, buf.length());
			if (!trimmedNewLine(tempBuf, TRIM_END)) {
				break;
			}
			buf.append(tempBuf);
		} */
		
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
			trimBuf(buf, TRIM_END);
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
	
	private boolean trimmedNewLine(StringBuffer buf, int trimIt) {
		try {
			// buf wird geleert
			buf.setLength(0);
			// Neue Zeile holen
			String line;
		
			line = in.readLine();
 
			// Solange die Zeile leer ist, wird eine Neue geholt
			while (line != null && line.length() == 0) {
				line = in.readLine();
			}
			// Wenn keine Zeile vorhanden ist, wird false zurückgegeben
			if (line == null) {
				return false;
			}
			// Zeile wird dem buf hinzugefügt
			buf.append(line);
			// vordere und/oder hintere Leerzeichen werden entfernt
			trimBuf(buf, trimIt);
			/* das vorhergehende wird solange praktiziert 
			 * bis eine Zeile mindestens ein Zeichen hat oder
			 *  der BufferedReader leer ist ('NULL') */
			while (buf.length() == 0) {
				if (!trimmedNewLine(buf, trimIt)) {
					return false;
				}
			}
			
			return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void trimBuf(StringBuffer buf, int trimIt) {
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
}
