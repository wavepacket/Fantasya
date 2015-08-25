package fantasya.library.language;

import static org.junit.Assert.*;

import org.junit.Test;

public class GermanLanguageTest {
	
	private Language getLanguage() {
		return Language.getLanguage("de_DE");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailNormalize() {
		Language language = getLanguage();
		language.normalize(null);
		language.normalize(" 	");
	}
	
	@Test
	public void testReturnOfNormalize() {
		Language language = getLanguage();
		
		String[] outputStringArray = {"Holzfaellen", "Aerger", "Strassenbau"};
		
		String[] inputStringArray = {"Holzfällen", "Ärger", "Straßenbau"};
		
		if (outputStringArray.length != inputStringArray.length) {
			fail("Length of input and output array are not equal.");
		}
		
		for (int i = 0; i < inputStringArray.length; i++) {
			assertEquals(outputStringArray[i], language.normalize(inputStringArray[i]));
		}
	}
}
