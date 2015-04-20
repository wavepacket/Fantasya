package de.x8bit.Fantasya.Atlantis.util.atlas;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.DefaultConstantsFactory;

public abstract class Island implements Comparable<Island>  {
	
	public enum IslandType {
		LAND, OCEAN, LAVAFLOW, SANDRIVER, UNKNOWN;
	}
	
	private final int id;
	private final IslandType type;
	protected final Set<Coordinates> coordinateSet = new TreeSet<Coordinates>();
	protected Coordinates centralCoordinates = DefaultConstantsFactory.NO_COORDINATES_VALUE;
	protected String name = null;
	protected String description = null;
	
	public Island(int id, IslandType type) {
		if (id < 0) throw new IllegalArgumentException("An Island ID cannot be lower than 0");
		if (id == 0 && type != IslandType.UNKNOWN) throw new IllegalArgumentException("Only the unknonwn Island can have id 0");
		this.id = id;
		this.type = type;
	}
	
	public int getID() {
		return id;
	}
	
	public IslandType getIslandType() {
		return type;
	}
	
	public boolean hasCoordinate(Coordinates coordinate) {
		for (Coordinates possibleCoordinate : coordinateSet) {
			if (possibleCoordinate.equals(coordinate)) return true;
		}
		return false;
	}
	
	protected void setCoordinates(Set<Coordinates> coordinateSet) {
		if (coordinateSet == null) return;
		this.coordinateSet.clear();
		this.coordinateSet.addAll(coordinateSet);
		this.centralCoordinates = Coordinates.getCentralCoordinates(coordinateSet);
	}
	
	public Set<Coordinates> getCoordinateSet() {
		return Collections.unmodifiableSet(this.coordinateSet);
	}
	
	public Coordinates getCentralCoordinates() {
		return centralCoordinates;
	}
	
	public abstract void setName(String name);
	
	public String getName() {
		return name;
	}
	
	public abstract void setDescription(String description);
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Island)) return false;
		
		Island equalIsland = (Island) obj;
		
		if (this.id == equalIsland.id) return true;
		
		return false;
	}
	
	@Override
	public int compareTo(Island compareIsland) {
		if (compareIsland.id > this.id) return +1;
		if (compareIsland.id < this.id) return -1;
		
		return 0;
	}
}
