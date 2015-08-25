package fantasya.library.language;

import static org.junit.Assert.*;

import org.junit.Test;

public class LanguageTest {
	
	private Language getLanguage() {
		return Language.getLanguage("de");
	}

	@Test
	public void testGetLanguage() {
		Language language = getLanguage();
		
		assertEquals(language.getLanguage(), "de");
	}
	
	@Test
	public void testToString() {
		Language language = getLanguage();
		
		assertEquals(language.toString(), "de");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetTranslationFail() {
		Language language = getLanguage();
		String[] keyArray = {null};
		String[] valueArray = {null};
		
		if (keyArray.length != valueArray.length) {
			fail("Length of input and output array are not equal.");
		}
		
		for (int i = 0; i < keyArray.length; i++) {
			assertEquals(valueArray[i], language.getTranslation(keyArray[i]));
		}
	}
	
	@Test
	public void testGetTranslation() {
		Language language = getLanguage();
		String[] keyArray = {"library.rules.regiontype.name.t_plain", "Test"};
		String[] valueArray = {"Ebene", "Test"};
		
		if (keyArray.length != valueArray.length) {
			fail("Length of input and output array are not equal.");
		}
		
		for (int i = 0; i < keyArray.length; i++) {
			assertEquals(valueArray[i], language.getTranslation(keyArray[i]));
		}
	}

}
