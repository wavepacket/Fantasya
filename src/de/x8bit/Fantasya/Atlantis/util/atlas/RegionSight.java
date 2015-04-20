package de.x8bit.Fantasya.Atlantis.util.atlas;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.DefaultConstantsFactory;
import de.x8bit.Fantasya.Host.GameRules;

public final class RegionSight implements Comparable<RegionSight> {
	public enum RegionSightSource {
		UNIT(true, false, 100, "Unit", -1),
		FAR(true, false, 75, "Far", "far", 1),
		LIGHTHOUSE(true, false, 50, "Lighthouse", "lighthouse", 2),
		
		BATTLE(false, false, 30, "Battle", "battle", 0),
		TRAVEL(false, false, 20, "Travel", "travel", 0),
		HISTORIC(false, true, 10, "Historic", "historic", 0),
		
		NEIGHBOUR(false, true, 0, "Neighbour", "neighbour", 0),
		
		MAP(true, false, -10, "Atlantis", -1);
		
		private boolean detailed;
		private int value = 0;
		private String errorName;
		private String crName;
		
		private boolean canBeUnseenTerritory = false;
		private boolean historic = false;
		private boolean building = false;
		private boolean magic = false;
		
		private RegionSightSource(boolean detailed, boolean canBeUnseenTerritory, int value, String errorName, String crName, int type) {
			init(detailed, canBeUnseenTerritory, value, errorName, type);
			this.crName = crName;
		}
		
		private RegionSightSource(boolean detailed, boolean canBeUnseenTerritory, int value, String errorName, int type) {
			init(detailed, canBeUnseenTerritory, value, errorName, type);
		}
		
		private void init(boolean detailed, boolean canBeUnseenTerritory, int value, String errorName, int type) {
			this.canBeUnseenTerritory = canBeUnseenTerritory;
			this.detailed = detailed;
			this.value = value;
			this.errorName = errorName;
			switch (type) {
			case 0:
				historic = true;
				break;
			case 1:
				magic = true;
				break;
			case 2:
				building = true;
				break;
			default:
				break;
			}
		}
		
		public boolean canBeUnseenTerritory() {
			return canBeUnseenTerritory;
		}
		
		public boolean isHistoric() {
			return historic;
		}
		
		public boolean isMagic() {
			return magic;
		}
		
		public boolean isBuilding() {
			return building;
		}
		
		public int getValue() {
			return value;
		}
		
		public String getErrorName() {
			return errorName;
		}
		
		public String getCRName() {
			return crName;
		}
		
		public boolean isDetailed() {
			return detailed;
		}
		
		public static RegionSightSource getSourceForString(String sourceString) {
			if (sourceString == null | sourceString.length() == 0) {
				// loggen
				return RegionSightSource.MAP;
			}
			for (RegionSightSource source : RegionSightSource.values()) {
				if (sourceString.equals(source.getErrorName()))
					return source;
			}
			// loggen
			return RegionSightSource.MAP;
		}
		
		public static String getStringForSource(RegionSightSource source) {
			return source.getErrorName();
		}
	}
	
	private final Coordinates coordinates;
	private int turn;
	private RegionSightSource source;
	private int islandID;
	private boolean isInvisibleTerrain = false;
	/**
     * nur wichtig, falls es sich um eine RegionsSicht aus der Vergangenheit handelt.
     */
	private Class<? extends Region> terrain = null;

    /**
     * nur wichtig, falls es sich um eine RegionsSicht aus der Vergangenheit handelt.
     */
	private String name;

    /**
     * nur wichtig, falls es sich um eine RegionsSicht aus der Vergangenheit handelt.
     */
	private Set<Richtung> roads = Collections.emptySet();
	
	/**
	 * 
	 * Eine aktuelle RegionsSicht einer Region wird erstellt. 
	 * 
	 * @param coords
	 * @param details
	 */
	
	protected RegionSight(Coordinates coordinates, RegionSightSource source) {
        this.coordinates = coordinates;
        init(GameRules.getRunde(), source);
	}
	
	/**
	 * 
	 * Einer nicht der aktuellen Region entsprechenden RegionsSicht wird erstellt.
	 * 
	 * @param turn
	 * @param coords
	 * @param details
	 */

	protected RegionSight(int turn, Coordinates coordinates, RegionSightSource source) {
        this.coordinates = coordinates;
		init(turn, source);
	}
	
	/**
	 * 
	 * Einer nicht der aktuellen Region entsprechenden RegionsSicht wird erstellt.
	 * 
	 * @param turn
	 * @param coords
	 * @param details
	 * @param source
	 * @param name
	 * @param terrain
	 */

	protected RegionSight(int turn, Coordinates coordinates, RegionSightSource source, String name, Class<? extends Region> terrain) {
        this.coordinates = coordinates;
		init(turn, source, name, terrain);
	}
	
	/**
	 * 
	 * Einer nicht der aktuellen Region entsprechenden RegionsSicht wird erstellt.
	 * 
	 * @param turn
	 * @param coords
	 * @param details
	 * @param source
	 * @param name
	 * @param terrain
	 * @param roads
	 */

	protected RegionSight(int turn, Coordinates coordinates, RegionSightSource source, String name, Class<? extends Region> terrain, Set<Richtung> roads) {
        this.coordinates = coordinates;
		init(turn, source, name, terrain);
		setRoads(roads);
	}
	
	private void init(int turn, RegionSightSource source) {
		Region region = Region.Load(coordinates);
		init(turn, source, region.getName(), region.getClass());
	}
	
	private void init(int turn, RegionSightSource source, String name, Class<? extends Region> terrain) {
		this.turn = turn;
		this.source = source;
		isInvisibleTerrain = false;
		
		if (terrain.equals(DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS)) {
			setRoads(null);
			setName(DefaultConstantsFactory.INVISIBLE_TERRAIN_STRING_VALUE);
			setTerrain(DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS);
			islandID = 0;
			isInvisibleTerrain = true;
		} else {
			setName(name);
			setTerrain(terrain);
		}
	}
	
	public int getIslandID() {
		return islandID;
	}
	
	public void setIslandID(int id) {
		if (id < 1) return;
		this.islandID = id;
	}
	
	public Coordinates getCoordinates() {
		return coordinates;
	}

	public String getTerrainName() {
		return terrain.getSimpleName();
	}
	
	public Class<? extends Region> getTerrain() {
		return terrain;
	}
	
	public boolean isInvisibleTerrain() {
		return isInvisibleTerrain;
	}

	protected void setTerrain(Class<? extends Region> terrain) {
		if (isInvisibleTerrain()) return;
		if (terrain == null) return;
		this.terrain = terrain;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		if (isInvisibleTerrain()) return;
		if (name == null) return;
		this.name = name;
	}
	
	public RegionSightSource getRegionSightSource() {
		return source;
	}
	
	protected void setRegionSightSource(RegionSightSource source) throws IllegalArgumentException {
		if (source == null) throw new IllegalArgumentException("RegionSightSource for RegionSight " + getCoordinates() + " and turn " + getTurn() + "cannot be 'NULL'.");
		this.source = source;
	}

	public int getTurn() {
		return turn;
	}
	
	protected boolean setDirection(Richtung direction) {
		if (isInvisibleTerrain()) return false;
		if (roads == Collections.EMPTY_SET) roads = new HashSet<Richtung>();
		return this.roads.add(direction);
	}

	public Set<Richtung> getRoads() {
		if (roads != Collections.EMPTY_SET) return Collections.unmodifiableSet(roads);
		return roads;
	}

	protected void setRoads(Set<Richtung> roads) {
		if (isInvisibleTerrain()) return;
		if (roads == null || roads.isEmpty()) { this.roads = Collections.emptySet(); return; }
		this.roads = roads;
	}
	
	public String getCRTag() {
		return source.crName;
	}
	
	public int getValue() {
		return source.value;
	}
	
	public boolean isDetailed() {
		return source.detailed;
	}
	
	protected void update(int turn, RegionSightSource source, String name, Class<? extends Region> terrain, Set<Richtung> roads) {
		init(turn, source, name, terrain);
		setRoads(roads);
	}
	
	@Override
	public int hashCode() {
		return coordinates.hashCode();
	}

	@Override
	public int compareTo(RegionSight crs) {
		Coordinates thisCoordinates = this.getCoordinates();
		Coordinates compareCoordinates = crs.getCoordinates();
	
        return thisCoordinates.compareTo(compareCoordinates);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		Coordinates equalCoordinates = null;
		
		if (obj instanceof RegionSight) {
			RegionSight equalsRegionSight = (RegionSight) obj;
			equalCoordinates = equalsRegionSight.getCoordinates();
		}
		
		if (obj instanceof Coordinates) {
			equalCoordinates = (Coordinates) obj;
		}
		
		if (equalCoordinates == null) return false;
		
		return this.coordinates.equals(equalCoordinates);
	}
}
