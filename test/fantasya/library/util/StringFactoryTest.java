package fantasya.library.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringFactoryTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void testCheckValueForSQLStatement() {
		String[] inputArray = {"0", "", null, "-1", "1375086", "-13F4", "Rok'All", "Rok\'All", "Rok\\'All"};
		String[] outputArray = {"0", "", "", "-1", "1375086", "-13F4", "Rok\\'All", "Rok\\'All", "Rok\\'All"};
		
		if (inputArray.length != outputArray.length) {
			fail("Input array and output array has not same length.");
		}
		
		String input;
		String output;
		
		for (int i = 0; i < inputArray.length && i < outputArray.length; i++) {
			input = inputArray[i];
			output = outputArray[i];
			// System.out.println(output);
			assertEquals("String " + input + " with index " + i + " has problems.", output, StringFactory.checkValueForSQLStatement(input));
		}
	}
}
