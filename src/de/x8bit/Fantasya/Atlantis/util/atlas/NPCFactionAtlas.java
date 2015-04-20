package de.x8bit.Fantasya.Atlantis.util.atlas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight.RegionSightSource;
import de.x8bit.Fantasya.Host.GameRules;

public class NPCFactionAtlas extends FactionAtlas {
	
	public NPCFactionAtlas(Partei faction) {
		super(faction);
	}

	@Override
	public boolean addHistoricRegionSight(int turn, Coordinates coordinates,
			RegionSightSource source, String name, Class<? extends Region> terrain,
			Set<Richtung> roads) {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] does not have historic regions.");
	}

	@Override
	public boolean addHistoricRegionSight(Coordinates coordinates,
			RegionSightSource source) {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] does not have historic regions.");
	}

	@Override
	protected void clearHistoricRegionSight() {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] does not have historic regions.");
	}

	@Override
	public RegionSight addDataBaseRegionSight(int turn,
			Coordinates coordinates, RegionSightSource source, String name,
			Class<? extends Region> terrain) {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] does not load regions out of database.");
	}

	@Override
	public boolean addDataBaseRegionSightRoad(Coordinates coordinates,
			Richtung direction) {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] does not load regions out of database.");
	}

	@Override
	public void addBuildingSeen(Coordinates coords, RegionSightSource source) {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] only sees regions with details with units.");
	}

	@Override
	protected void clearBuildingSeen() {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] only sees regions with details with units.");
	}

	@Override
	public void addMagicalSeen(Coordinates coords, RegionSightSource source) {
		throw new UnsupportedOperationException("NPC faction [" + faction.getNummerBase36() + "] only sees regions with details with units.");
	}
	
	private RegionSight getNewRegionSight(Coordinates coords, RegionSightSource source) {
		Region region = Region.Load(coords);
		int stonesForDirection = region.getSteineFuerStrasse();
		Set<Richtung> newRoads = new HashSet<Richtung>();
		if(region.istBetretbar(null) && stonesForDirection > 0) {	
			for (Richtung richtung : Richtung.values()) {
				if (region.getStrassensteine(richtung) >= stonesForDirection)
					newRoads.add(richtung);
			}
		}
		return new RegionSight(GameRules.getRunde(), coords, source, region.getName(), region.getClass(), newRoads); 
	}

	@Override
	protected void prepareForTurnReport() {
		// Atlas löschen
		factionAtlas.clear();
		islandSet.clear();
		// Alle Regionen, in denen Einheiten sind.
		Set<Unit> units = faction.getEinheiten();
		Map<Coordinates, Region> unitRegions = new HashMap<Coordinates, Region>();
		for (Unit unit : units) {
			if (!unitRegions.containsKey(unit.getCoordinates())) {
				Region region = Region.Load(unit.getCoordinates());
				unitRegions.put(region.getCoordinates(), region);
			}
		}
		
		// Regionen mit Einheiten
		for (Region r : unitRegions.values()) {
			Coordinates coordinates = r.getCoordinates();
			factionAtlas.add(getNewRegionSight(coordinates, RegionSightSource.UNIT));
		}
		// Nachtbarregionen
		for (RegionSight rs : factionAtlas) {
			for (Coordinates nCoords : rs.getCoordinates().getNeighbours()) {
				if (factionAtlas.contains(nCoords)) continue;
	            factionAtlas.add(getNewRegionSight(nCoords, RegionSightSource.NEIGHBOUR));
	        }
		}
		// ------------------------------------- Inseln ----------------------------------------
		// Zu allen Regionen die öffentlichen Inseln raussuchen und dem islandSet hinzufügen
		for (RegionSight rs : factionAtlas) {
			if (rs.isInvisibleTerrain()) continue;
			islandSet.add(Partei.OMNI_FACTION.getFactionAtlas().getIsland(rs.getCoordinates()));
		}
	}

	@Override
	protected void prepareForTurn() {}
	
	@Override
	protected void prepareForReport() {
		prepareForTurnReport();
	}
}
