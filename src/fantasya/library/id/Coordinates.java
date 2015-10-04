package fantasya.library.id;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fantasya.library.util.StringFactory;

public final class Coordinates implements Identifiable {
	
	private static final StringBuilder STRING_BUILD = new StringBuilder();
	private static final Set<Coordinates> COORDINATES_SET = new HashSet<Coordinates>();
	private static Logger LOGGER = LoggerFactory.getLogger(Coordinates.class);

	/**
	 * @uml.property  name="x"
	 */
	private final int x;
	/**
	 * @uml.property  name="y"
	 */
	private final int y;
	/**
	 * @uml.property  name="welt"
	 */
	private final int z;
	
	private final int hashCode;
	
	private Coordinates(int x, int y, int z) {
		if ((x < -8192) || (x > 8191)) {
			IllegalArgumentException ex = new IllegalArgumentException("X for coordinates is -8192 bis 8191 - wanted " + x);
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}
		if ((y < -8192) || (y > 8191)) {
			IllegalArgumentException ex = new IllegalArgumentException("Y for coordinates is -8192 bis 8191 - wanted " + y);
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}
		if ((z < -4) || (z > 3)) {
			IllegalArgumentException ex = new IllegalArgumentException("World for coordinates is  -4 bis +3 - wanted " + z);
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}
		
		this.x = x;
		this.y = y;
		this.z = z;

		this.hashCode = toHashCode(this);
	}
	
	/**
	 * Returns the coordinate x.
	 * 
	 * @return coordinate x.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the coordinate y.
	 * 
	 * @return coordinate y.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the coordinate z.
	 * 
	 * @return coordinate z.
	 */
	public int getZ() {
		return z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		
		if (!(obj instanceof Coordinates)) { return false; }
		
		final Coordinates equalCoordinate = (Coordinates) obj;
		if (this.hashCode == equalCoordinate.hashCode()) { return true; }
		
		return false;
	}
	
	public int compareTo(Coordinates compareCoordinate) {
		if (this.hashCode() > compareCoordinate.hashCode()) { return -1; }
		
		if (this.hashCode() < compareCoordinate.hashCode()) { return +1; }

		return 0;
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public int compareTo(Object obj) {
		if (obj == null) {
			IllegalArgumentException ex = new IllegalArgumentException("Coordinates cannot be compared. Object is 'NULL'!");
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}
		
		if (!(obj instanceof Coordinates)) {
			UnsupportedOperationException ex = new UnsupportedOperationException("Coordinates cannot be compared. "
					+ "Object is class " + obj.getClass().getSimpleName() + " .");
			LOGGER.error(StringFactory.getExceptionDetails(ex));
			throw ex;
		}

		return compareTo((Coordinates) obj);
	}
	
	public String toString(boolean forceZ) {
		STRING_BUILD.setLength(0);
		STRING_BUILD.append("(");
		STRING_BUILD.append(x);
		STRING_BUILD.append(", ");
		STRING_BUILD.append(y);
		if (forceZ) {
			STRING_BUILD.append(", ");
			STRING_BUILD.append(z);
		}
		STRING_BUILD.append(")");
		return STRING_BUILD.toString();
	}
	
	@Override
	public String toString() {
		return  toString(true);
	}
	
	@Override
	public Coordinates clone() {
		return this;
	}
	
	private static int toHashCode(Coordinates coordinates) {
		int x1 = ((coordinates.getX() * -1) - 1) + 8192;
		int y1 = coordinates.getY() + 8192;
		int z1 = coordinates.getZ() + 4;
		return (z1 << 28) + (y1 << 14) + x1;
	}
	
	public static Coordinates fromHashCode(int id) {
		int z1 = ((id & 0x70000000) >> 28) - 4;
		int y1 = ((id & 0x0fffc000) >> 14) - 8192;
		int x1 = (id & 0x3fff) - 8192;
		x1++;
		x1 *= -1;

		return create(x1, y1, z1);
	}
	
	public static Coordinates create(int x, int y, int z) {
		
		Coordinates coordinates = null;
		
		for (Coordinates possibleCoordinates : COORDINATES_SET) {
			if (possibleCoordinates.getX() == x
					&& possibleCoordinates.getY() == y
					&& possibleCoordinates.getZ() == z)
				coordinates = possibleCoordinates;
		}
		
		if (coordinates == null) {
			coordinates = new Coordinates(x, y, z);
			COORDINATES_SET.add(coordinates);
		}
		
		return coordinates;
	}

	@Override
	public String getId() {
		return Integer.toString(hashCode);
	}
}