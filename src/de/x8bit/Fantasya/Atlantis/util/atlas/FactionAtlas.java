package de.x8bit.Fantasya.Atlantis.util.atlas;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.x8bit.Fantasya.Atlantis.Building;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.Buildings.Leuchtturm;
import de.x8bit.Fantasya.Atlantis.Messages.Debug;
import de.x8bit.Fantasya.Atlantis.Regions.Lavastrom;
import de.x8bit.Fantasya.Atlantis.Regions.Ozean;
import de.x8bit.Fantasya.Atlantis.Regions.Sandstrom;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.DefaultConstantsFactory;
import de.x8bit.Fantasya.Atlantis.util.atlas.Island.IslandType;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight.RegionSightSource;

public abstract class FactionAtlas {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(FactionAtlas.class);
	
	protected final Partei faction;
	
	protected final Set<RegionSight> factionAtlas = new TreeSet<RegionSight>();
	
	public FactionAtlas(Partei faction) {
		this.faction = faction;
	}
	
	public RegionSight getRegionSight(Coordinates coordinates) {
		for (RegionSight rs : factionAtlas) {
			if (rs.equals(coordinates)) return rs;
		}
		return null;
	}
	
	public boolean hasRegionSight(Coordinates coordinates) {
		for (RegionSight rs : factionAtlas) {
			if (rs.equals(coordinates)) return true;
		}
		return false;
	}
	
	/**
	 * returns faction atlas without update and without invisible regions
	 * 
	 * @return faction atlas
	 */
	
	public Set<RegionSight> getFactionAtlas() {
		return (factionAtlas == Collections.EMPTY_SET) ? factionAtlas : Collections.unmodifiableSet(factionAtlas);
	}
	
	/**
	 * returns undetaild region sight. if source is HISTORIC, BATTLE or TRAVEL, this will add to historic atlas
	 * 
	 * @param turn
	 * @param coords
	 * @param source
	 * @param name
	 * @param terrain
	 * @param roads
	 * @return region sight with no details
	 */
	
	public abstract boolean addHistoricRegionSight(int turn, Coordinates coordinates, RegionSightSource source, String name, Class<? extends Region> terrain, Set<Richtung> roads);
	
	public abstract boolean addHistoricRegionSight(Coordinates coordinates, RegionSightSource source);
	
	protected abstract void clearHistoricRegionSight();
		
	public abstract RegionSight addDataBaseRegionSight(int turn, Coordinates coordinates, RegionSightSource source, String name, Class<? extends Region> terrain);
	
	public abstract boolean addDataBaseRegionSightRoad(Coordinates coordinates, Richtung direction);
	
	public abstract void addBuildingSeen(Coordinates coords, RegionSightSource source);
	
	protected abstract void clearBuildingSeen();
	
	public abstract void addMagicalSeen(Coordinates coords, RegionSightSource source);
	
	/**
	 * updates historic atlas to faction atlas implementing building seen, magical seen and unit seen with neighbours
	 */
	
	protected abstract void prepareForTurnReport();
	
	protected abstract void prepareForTurn();
	
	protected abstract void prepareForReport();
	
	protected static RegionSight getRegionSight(Coordinates coordinates, Set<RegionSight> atlas) {
		for (RegionSight rs : atlas) {
			if (rs.equals(coordinates)) return rs;
		}
		return null;
	}
	
	public static void prepareAllFactionsForTurnReport() {
		for (Partei faction : Partei.getPlayerFactionList()) {
			faction.getFactionAtlas().clearBuildingSeen();
		}
		for (Building b : Building.PROXY) {
			if (b.getClass() == Leuchtturm.class) {
				((Leuchtturm)b).leuchten();
			}
		}
		for (Partei faction : Partei.getPlayerFactionList()) {
			faction.getFactionAtlas().prepareForTurnReport();
			new Debug("Partei " + faction + " hat einen Atlas mit " + faction.getFactionAtlas().factionAtlas.size() + " Regionen im Regal.");
		}
		
		Partei.OMNI_FACTION.getFactionAtlas().prepareForTurnReport();
		
		for (Partei faction : Partei.getNPCFactionList()) {
			faction.getFactionAtlas().prepareForTurnReport();
			new Debug("Partei " + faction + " hat einen Atlas mit " + faction.getFactionAtlas().factionAtlas.size() + " Regionen im Regal.");
		}
	}
	
	public static void prepareAllFactionsForTurn() {
		for (Partei faction : Partei.getPlayerFactionList()) {
			faction.getFactionAtlas().prepareForTurn();
		}
		// FactionAtlas.updateAtlasAllFactions();
	}
	
	public static void prepareAllFactionsForReport() {
		Partei.OMNI_FACTION.getFactionAtlas().prepareForReport();
		
		for (Partei faction : Partei.getNPCFactionList()) {
			faction.getFactionAtlas().prepareForReport();
		}
		
	}
	
	// ------------------------------------- Inseln ----------------------------------------
	
	protected Set<Island> islandSet = new TreeSet<Island>();
	
	public Set<Island> getIslandSet() {
		return Collections.unmodifiableSet(islandSet);
	}
	
	public int getID(Coordinates coordinate) {
		for (Island island : islandSet) {
			if (island.hasCoordinate(coordinate)) return island.getID();
		}
		return DefaultConstantsFactory.NO_INT_VALUE;
	}
	
	public Island getIsland(int id) {
		for (Island island : islandSet) {
			if (island.getID() == id) return island;
		}
		return null;
	}
	
	public Island getIsland(Coordinates coordinate) {
		for (Island island : islandSet) {
			for (Coordinates possibleCoordinate : island.getCoordinateSet()) {
				if (possibleCoordinate.equals(coordinate)) return island;
			}
		}
		return null;
	}
	
	public abstract Island addDataBaseIsland(int id, int explorationTurn, IslandType type, String name, String description, Coordinates anchorCoordinates);
	
	// ------------------------------------- Inseln ----------------------------------------
	
	protected static IslandType getIslandType (Class<? extends Region> regionClass) {
		IslandType modus = IslandType.LAND;
        if (regionClass == Ozean.class) modus = IslandType.OCEAN;
        else if (regionClass == Lavastrom.class) modus = IslandType.LAVAFLOW;
        else if (regionClass == Sandstrom.class) modus = IslandType.SANDRIVER;
        return modus;
	}
	
	protected static boolean isIslandModus(Class<? extends Region> regionClass, IslandType modus) {
        if (regionClass == DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS) return false;
        
        if (modus == IslandType.LAND) {
            if (regionClass == Ozean.class) return false;
            if (regionClass == Lavastrom.class) return false;
            if (regionClass == Sandstrom.class) return false;
        } else if (modus == IslandType.OCEAN) {
            if (regionClass !=  Ozean.class) return false;
        } else if (modus == IslandType.LAVAFLOW) {
            if (regionClass !=  Lavastrom.class) return false;
        } else if (modus == IslandType.SANDRIVER) {
            if (regionClass !=  Sandstrom.class) return false;
        }
        
        return true;
    }
	
	protected static void mergeIslands(Island newIsland, Set<Island> oldIslandSet, FactionAtlas factionAtlas) {
		// Die neue Insel bekommt alle Koordinaten (Regionen) der alten Insel(teile)n
		// Die alten Inseln werden aus der Inselliste entfernt
		for (Island oldIsland : oldIslandSet) {
				newIsland.coordinateSet.addAll(oldIsland.getCoordinateSet());
				factionAtlas.islandSet.remove(oldIsland);
		}
	}
	
	protected static boolean selectIsland(Coordinates coordinates, Class<? extends Region> regionClass, Island island, boolean isRegion, FactionAtlas factionAtlas) {
		if (!FactionAtlas.isIslandModus(regionClass, island.getIslandType())) return false;
		
		LOGGER.info("RegionSight " + coordinates + " [" + regionClass.getSimpleName() + "] wird der Insel (" + island.getID() + ") mit dem Modus " + island.getIslandType() + " hinzugefügt werden.");
		island.coordinateSet.add(coordinates);
		// Insel soll temporär ganz erfasst werden, um ggf. neu generierte inseln, die mit alten zusammenhängen,
        // mit diesen zusammen zu legen.
		Class<? extends Region> newRegionClass;
		for (Coordinates neighbourCoordinates : coordinates.getNeighbours()) {
			if (isRegion) {
				Region region = Region.Load(neighbourCoordinates);
				if (region == null || region.getClass() == DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS) continue;
				newRegionClass = region.getClass();
			} else {
				RegionSight rs = factionAtlas.getRegionSight(neighbourCoordinates);
				if (rs == null || rs.isInvisibleTerrain()) continue;
				newRegionClass = rs.getTerrain();
			}
			
			if (!island.coordinateSet.contains(neighbourCoordinates)) selectIsland(neighbourCoordinates, newRegionClass, island, isRegion, factionAtlas);
		}
        
        return true;
	}
}
