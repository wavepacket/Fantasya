package fantasya.library.id.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import fantasya.library.id.impl.Coordinates;

public class CoordinatesTest {

	@Test(expected = IllegalArgumentException.class)
	public void testFailCoordinatesCreation() {
		Coordinates.create(-8193, 4, 1);
		Coordinates.create(8192, 4, 1);
		
		Coordinates.create(-5, -8193, 1);
		Coordinates.create(-5, 8192, 1);
		
		Coordinates.create(-5, 4, -5);
		Coordinates.create(-5, 4, 4);
	}
	
	@Test
	public void testCoordinatesCreation() {
		Coordinates.create(-8192, 4, 1);
		Coordinates.create(8191, 4, 1);
		
		Coordinates.create(-5, -8192, 1);
		Coordinates.create(-5, 8191, 1);
		
		Coordinates.create(-5, 4, -4);
		Coordinates.create(-5, 4, 3);
	}
	
	@Test
	public void testCoordinatesAndId() {
		Coordinates coordinates = Coordinates.create(-5, 4, 1);
		
		int hashCode = coordinates.hashCode();
		
		Coordinates coordinatesEquals = Coordinates.fromHashCode(hashCode);
		
		assertEquals(coordinates, coordinatesEquals);
	}
	
	@Test
	public void testCompareTo() {
		Coordinates coordinatesDefault = Coordinates.create(0, 0, 1);
		Coordinates coordinatesXBefore = Coordinates.create(-1, 0, 1);
		Coordinates coordinatesXAfter = Coordinates.create(1, 0, 1);
		Coordinates coordinatesYBefore = Coordinates.create(0, 1, 1);
		Coordinates coordinatesYAfter = Coordinates.create(0, -1, 1);
		Coordinates coordinatesZAfter = Coordinates.create(0, 0, -1);
		
		// World before underworld
		assertEquals(-1, coordinatesDefault.compareTo(coordinatesZAfter));
		
		// lower x before higher x
		assertEquals(1, coordinatesDefault.compareTo(coordinatesXBefore));
				
		// lower x before higher x
		assertEquals(-1, coordinatesDefault.compareTo(coordinatesXAfter));
				
		// lower y before higher y
		assertEquals(1, coordinatesDefault.compareTo(coordinatesYBefore));
				
		// lower y before higher y
		assertEquals(-1, coordinatesDefault.compareTo(coordinatesYAfter));
		
	}
}

