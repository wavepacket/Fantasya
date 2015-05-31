package de.x8bit.Fantasya.Atlantis.util;

import java.util.HashSet;
import java.util.Set;

import de.x8bit.Fantasya.Atlantis.Richtung;

/**
 * Verwaltung von Koordinaten ... das umfasst auch das Verschieben bzw. die Bewegung zu Nachbarregionen
 * @author  mogel, Michael Jahn
 */
@SuppressWarnings("rawtypes")
public final class Coordinates implements Comparable {
	
	private static final StringBuilder STRING_BUILD = new StringBuilder();
	private static final Set<Coordinates> COORDINATES_SET = new HashSet<Coordinates>();

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
		if ((x < ConstantsFactory.MIN_COORDINATES_XY) || (x > ConstantsFactory.MAX_COORDINATES_XY)) {
			throw new IllegalArgumentException("Der Koordinatenbereich für x ist -8192 bis 8191 - versucht " + x);
		}
		if ((y < ConstantsFactory.MIN_COORDINATES_XY) || (y > ConstantsFactory.MAX_COORDINATES_XY)) {
			throw new IllegalArgumentException("Der Koordinatenbereich für y ist -8192 bis 8191 - versucht " + y);
		}
		if ((z < ConstantsFactory.MIN_COORDINATES_Z) || (z > ConstantsFactory.MAX_COORDINATES_Z)) {
			throw new IllegalArgumentException("Der Koordinatenbereich für Welt ist -4 bis +3 - versucht " + z);
		}

		this.x = x;
		this.y = y;
		this.z = z;

		this.hashCode = toHashCode(this);
	}
	
	
	/**
	 * @return
	 * @uml.property  name="x"
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return
	 * @uml.property  name="y"
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return
	 * @uml.property  name="welt"
	 */
	public int getZ() {
		return z;
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}
	
	/**
	 * liefert die Koordinaten um die angegebene Richtung verschoben ... es wird
	 * ein neues Objekt erstellt !
	 * @param richtung - die Richtung in die verschoben werden soll
	 * @return neuen Koordinaten
	 */
	public Coordinates shiftDirection(Richtung direction) {
		return create(x + direction.getDx(), y + direction.getDy(), z);
	}
	
	/**
	 * @return alle direkten Nachbarkoordinaten - also alle, f�r die
	 * getDistance() 1 liefert.
	 */
	public Set<Coordinates> getNeighbours() {
		Set<Coordinates> neighbours = new HashSet<Coordinates>();

		for (Richtung direction : Richtung.values()) {
			neighbours.add(shiftDirection(direction));
		}

		return neighbours;
	}
	
	public int getDistance(Coordinates destination) {
		int dx = destination.getX() - this.getX();
		int dy = destination.getY() - this.getY();

		int absDX = Math.abs(dx);
		int absDY = Math.abs(dy);

		if ((dx < 0) && (dy > 0)) {
			return Math.max(absDX, absDY);
		}
		if ((dx > 0) && (dy < 0)) {
			return Math.max(absDX, absDY);
		}
		return absDX + absDY;
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
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		if (!(obj instanceof Coordinates)) return false;
		
		final Coordinates equalCoordinate = (Coordinates) obj;
		if (this.hashCode == equalCoordinate.hashCode) return true;
		
		return false;
	}
	
	public int compareTo(Coordinates compareCoordinate) {
		if (this.hashCode() > compareCoordinate.hashCode()) return -1;
		
		if (this.hashCode() < compareCoordinate.hashCode()) return +1;

		return 0;
	}

	@Override
	public int compareTo(Object obj) {
		if (obj == null) throw new IllegalArgumentException("Coordinate cannot be compared. Object is 'NULL'!");
		
		if (!(obj instanceof Coordinates)) {
			throw new IllegalArgumentException("Coordinate cannot be compared. "
					+ "Object is class " + obj.getClass().getSimpleName() + " .");
		}

		return compareTo((Coordinates) obj);
	}
	
	private static int toHashCode(Coordinates coordinates) {
		int x1 = ((coordinates.x * -1) - 1) + 8192;
		int y1 = coordinates.y + 8192;
		int z1 = coordinates.z + 4;
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
	
	/**
	 * @param map
	 * @return den Mittelpunkt des Koordinaten-Sets - kann eine Koordinate sein, die nicht in map enthalten ist.
	 */
	public static Coordinates getCentralCoordinates(Set<Coordinates> coordinatesSet) {
		if (coordinatesSet == null) throw new IllegalArgumentException("Map is referenced to 'NULL'.");
		
		if (coordinatesSet.isEmpty()) throw new IllegalArgumentException("Map has no Coordinates.");

		double sumX = 0;
		double sumY = 0;
		double sumZ = 0;

		for (Coordinates c : coordinatesSet) {
			sumX += c.getX();
			sumY += c.getY();
			sumZ += c.getZ();
		}
		double n = coordinatesSet.size();
		return create(
				(int) Math.round(sumX / n),
				(int) Math.round(sumY / n),
				(int) Math.round(sumZ / n));
	}
	
	public static Coordinates create(int x, int y, int z) {
		
		Coordinates coordinates = null;
		
		for (Coordinates possibleCoordinates : COORDINATES_SET) {
			if (possibleCoordinates.x == x
					&& possibleCoordinates.y == y
					&& possibleCoordinates.z == z)
				coordinates = possibleCoordinates;
		}
		
		if (coordinates == null) {
			coordinates = new Coordinates(x, y, z);
			COORDINATES_SET.add(coordinates);
		}
		
		return coordinates;
	}
	
	// -----------------------------------------------------------

	/**
	 * liefert die WHERE-Klausel f�r diese Koordinaten<br/>
	 * <i>koordx = $X AND koordy = $Y AND welt = $W</i>
	 * @param lang - ob die kurzen Koordinaten (also nur Koordinaten) oder die langen (in Verbindung mit anderen)
	 * @return SQL-String f�r die DB-Abfrage
	 */
	public String Where(boolean lang) {
		return "koordx=" + x + " AND koordy=" + y + " AND welt=" + z;
	}

	public Richtung getRichtungNach(Coordinates ziel) {
		if (ziel.getZ() != this.getZ()) {
			throw new UnsupportedOperationException("Die Koordinaten liegen nicht in der gleichen Welt - getRichtungNach() ist damit derzeit nicht m�glich.");
		}
		if (ziel.getX() == getX() && ziel.getY() == getY()) {
			throw new IllegalArgumentException("Die Koordinaten m�ssen verschieden sein.");
		}

		int deltaX = ziel.getX() - this.getX();
		int deltaY = ziel.getY() - this.getY();

		if (deltaX == 0) {
			if (deltaY < 0) {
				return Richtung.Suedwesten;
			} 
			return Richtung.Nordosten;
		}
		if (deltaY == 0) {
			if (deltaX < 0) {
				return Richtung.Westen;
			}
			return Richtung.Osten;
		}

		// weder X noch Y sind 0:
		if ((deltaX > 0) && (deltaY > 0)) {
			return Richtung.Osten; // das ist dann immer mehr als einen Schritt entfernt, aber fangen wir's an.
		}
		if ((deltaX > 0) && (deltaY < 0)) {
			return Richtung.Suedosten;
		}
		if ((deltaX < 0) && (deltaY > 0)) {
			return Richtung.Nordwesten;
		}
		if ((deltaX < 0) && (deltaY < 0)) {
			return Richtung.Westen;
		}

		throw new RuntimeException("Das ist unm�glich!");
	}

	public static Coordinates fromString(String s) {
		String src = s.replaceAll("\\(", "").replaceAll("\\)", "");
		Coordinates c = null;
		try {
			String[] coords = src.split("\\s");
			if (coords.length < 2) {
				return null;
			}

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);

			int z = 1;
			if (coords.length >= 3) {
				z = Integer.parseInt(coords[2]);
			}

			c = create(x, y, z);

		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Coords aus '" + s + "' ist nicht m�glich - " + ex.getMessage());
		}
		return c;
	}
}
