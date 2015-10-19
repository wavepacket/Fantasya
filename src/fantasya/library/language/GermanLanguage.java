package fantasya.library.language;

import java.util.Locale;

import fantasya.library.util.StringFactory;

/**
 * @author Michael Jahn
 * 
 * An class for the german language.
 */

public class GermanLanguage extends Language {
	
	protected GermanLanguage() {
		super(Locale.GERMAN);
	}
	
	/**
	 * For more details, see {@link fantasya.library.language.Language#normalize(java.lang.String)}
	 */
	@Override
	public String normalize(String notNormal) {
		if (notNormal == null) {
			IllegalArgumentException ex = new IllegalArgumentException("String to normalize for language " + locale.getLanguage() + "is 'NULL'.");
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}
		else if (notNormal.trim().length() == 0) {
			IllegalArgumentException ex = new IllegalArgumentException("String to normalize for language " + locale.getLanguage() + "is empty.");
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}
		
		notNormal = notNormal.replace("\u00e4", "ae"); // ä
		notNormal = notNormal.replace("\u00f6", "oe"); // ö
		notNormal = notNormal.replace("\u00fc", "ue"); // ü
		notNormal = notNormal.replace("\u00c4", "Ae"); // Ä
		notNormal = notNormal.replace("\u00d6", "Oe"); // Ö
		notNormal = notNormal.replace("\u00dc", "Ue"); // Ü
		notNormal = notNormal.replace("\u00df", "ss"); // ß
		
		return notNormal;
	}
}
