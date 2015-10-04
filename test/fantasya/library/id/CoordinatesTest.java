package fantasya.library.id;

import static org.junit.Assert.*;

import org.junit.Test;

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
}

