package fantasya.library.id.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdentifiableAtlantisTest {
	
	private IdentifiableTest getIdentifiableTest(String id) {
		return new IdentifiableTest(id);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailIdentifiableAtlantisNullCreation() {
		getIdentifiableTest(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailIdentifiableAtlantisEmptyCreation() {
		getIdentifiableTest("");
	}
	
	@Test
	public void testIdentifiableAtlantisCreationAndId() {
		String id1 = "bLk";
		IdentifiableTest idTest1 = getIdentifiableTest(id1);
		
		assertEquals(id1, idTest1.getId());
		
		String id2 = "56";
		IdentifiableTest idTest2 = getIdentifiableTest(id2);
		
		assertEquals(id2, idTest2.getId());
		
		String id3 = "Holz";
		IdentifiableTest idTest3 = getIdentifiableTest(id3);
		
		assertEquals(id3, idTest3.getId());
	}
	
	@Test
	public void testEquals() {
		String id1 = "bLk";
		IdentifiableTest idTest1 = getIdentifiableTest(id1);
		
		String id2 = "56";
		IdentifiableTest idTest2 = getIdentifiableTest(id2);
		
		IdentifiableTest idTest3 = getIdentifiableTest(id2);
		
		// two exactly the same objects of same class
		assertEquals(true, idTest2.equals(idTest2));
		
		// two different objects of same class
		assertEquals(false, idTest2.equals(idTest1));
		
		// two different objects of same class with exact same id-object
		assertEquals(false, idTest2.equals(idTest3));
		
		// two different objects of a different class but exactly the same id
		assertEquals(false, idTest2.equals(id2));
		
		// two different objects of a different class
		assertEquals(false, idTest2.equals(id1));
	}
	
	@Test
	public void testHashCode() {
		String id1 = "bLk";
		IdentifiableTest idTest1 = getIdentifiableTest(id1);
		
		
		assertEquals(id1.hashCode(), idTest1.hashCode());
	}
	
	@Test
	public void testCommpare() {
		String id1 = "55";
		IdentifiableTest idTest1 = getIdentifiableTest(id1);
		
		String id2 = "56";
		IdentifiableTest idTest2 = getIdentifiableTest(id2);
		
		
		assertEquals(-1, idTest1.compareTo(idTest2));
		
		assertEquals(1, idTest2.compareTo(idTest1));
		
		assertEquals(0, idTest1.compareTo(idTest1));
	}

	/*@Test
	public void test() {
		fail("Not yet implemented");
	}*/
	private class IdentifiableTest extends IdentifiableAtlantis {

		protected IdentifiableTest(String id) {
			super(id);
		}
		
	}
}
