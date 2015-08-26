package fantasya.library.language;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fantasya.library.io.IOFactory;
import fantasya.library.util.ExceptionFactory;

/**
 * @author Michael Jahn
 * 
 * An abstract class for a language.
 */

public abstract class Language {
	
	protected static StringBuilder build = new StringBuilder();
	
	private final static Language DEFAULT_LANGUAGE;
	
	private final static Language[] LANGUAGES;

	protected final static Logger LOGGER = LoggerFactory.getLogger(Language.class);
	
	static {
		DEFAULT_LANGUAGE = new GermanLanguage();
		LANGUAGES = new Language[1];
		LANGUAGES[0] = DEFAULT_LANGUAGE;
	}
	
	protected Locale locale;
	protected String toString;
	protected final Properties properties = new Properties();
	
	protected Language(Locale locale) {
		LOGGER.debug("Initialize language: " + locale.getLanguage());
		this.locale = locale;
		this.toString = locale.getLanguage().toLowerCase(locale);
		String propertyFile = IOFactory.PROPERTIES_DIR + "properties_" + this.toString + ".properties";
		try {
			LOGGER.debug("Loading property file: " + propertyFile);
			InputStream inputStream = new FileInputStream(propertyFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			properties.load(reader);
			reader.close();
			inputStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			LOGGER.warn("Language " + locale.getLanguage() + " has proplems with property file " + propertyFile + ": \n" + ExceptionFactory.getExceptionDetails(ex));
		}
	}
	
	/**
	 * Returns the locale of language.
	 * 
	 * @return the locale of language.
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Returns the language of locale.
	 * 
	 * @return the language of locale.
	 */
	public String getLanguage() {
		return locale.getLanguage();
	}
	
	/**
	 * Returns translation of key
	 * @param key
	 * @return translation or key
	 */
	public String getTranslation(String key) throws IllegalArgumentException {
		if (key == null) {
			IllegalArgumentException ex = new IllegalArgumentException("Key for a property of language " + toString() + " is 'NULL'.");
			LOGGER.error(ExceptionFactory.getExceptionDetails(ex));
			throw ex;
		}
		String string = properties.getProperty(key);
		return (string == null) ? key : string;
	}
	
	/**
	 * Normalze String without mutated vowels.
	 * 
	 * @return Normalzed String without mutated vowels.
	 */
	public abstract String normalize (String notNormal);
	
	/**
	 * For more details, see {@link java.lang.Object#toString()}
	 */
	@Override
	public String toString() {
		return this.toString;
	}
	
	public static Language getLanguage(String languageString) {
		if (languageString == null) {
			IllegalArgumentException ex = new IllegalArgumentException("Language is 'NULL' and not supported. Change to default language: " + DEFAULT_LANGUAGE.toString());
			LOGGER.warn(ExceptionFactory.getExceptionDetails(ex));
			return Language.DEFAULT_LANGUAGE;
		}
		
		for (Language language : LANGUAGES) {
			if (language.toString().equalsIgnoreCase(languageString)) {
				return language;
			}
		}
		
		IllegalArgumentException ex = new IllegalArgumentException("Language " + languageString + " is not supported. Change to default language: " + DEFAULT_LANGUAGE.toString());
		LOGGER.warn(ExceptionFactory.getExceptionDetails(ex));
		return Language.DEFAULT_LANGUAGE;
	}
	
	public static Language getDefaultLanguage() {
		return Language.DEFAULT_LANGUAGE;
	}
	
	public static String normalizeAll(String notNormal) {
		
		for (Language language : LANGUAGES) {
			notNormal = language.normalize(notNormal);
		}
		
		return notNormal;
	}
}
