package fantasya.library.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringFactory {
	
	/**
	 * 
	 * Get exception details as string
	 * 
	 * @param ex Exception for getting details
	 * @return String with details
	 */
	
	public static String getExceptionDetails(Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static String only8bit(String s) {
		  StringBuffer r = new StringBuffer( s.length() );
		  r.setLength( s.length() );
		  int current = 0;
		  for (int i = 0; i < s.length(); i ++) {
			 char cur = s.charAt(i);
			 if (cur < 256) r.setCharAt( current++, cur );
		  }
		  return r.toString();
	}
	
	/**
	 * check for ' to get a valid sql-statement
     * @param value to check
     * @return checked value for using in a sql statement
     */
	public static String checkValueForSQLStatement(String value) {
		
		if (value == null || value.isEmpty()) {
			return "";
		}
		
		value = value.trim();
		
		int valueLength = value.length();
		boolean isNumber = true;
		
		for (int i = 0; i < valueLength; i++) {
			if (i == 0 && value.charAt(i) == '-') {
				continue;
			}
			if (value.charAt(i) >= '0' && value.charAt(i) <= '9') {
				continue;
			}
			isNumber = false;
		}
		
		if (isNumber) {
			return value;
		}
		
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < valueLength; i++) {
			boolean add = true;
			if (value.charAt(i) == '\'') {
				sb.append('\\');
			}
			// -- ? -- if (value.charAt(i) == '"') sb.append('\\');
			if (value.charAt(i) == '\\') {
				add = false;
			}
			if (add) {
				sb.append(value.charAt(i));
			}
		}
		
		return sb.toString();
	}
}
