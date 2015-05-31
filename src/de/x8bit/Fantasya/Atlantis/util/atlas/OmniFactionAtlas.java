package de.x8bit.Fantasya.Atlantis.util.atlas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.ConstantsFactory;
import de.x8bit.Fantasya.Atlantis.util.atlas.Island.IslandType;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight.RegionSightSource;

public class OmniFactionAtlas extends FactionAtlas {
	
	private boolean updated = false;
	
	public OmniFactionAtlas(Partei faction) {
		super(faction);
	}

	@Override
	public boolean addHistoricRegionSight(int turn, Coordinates coordinates,
			RegionSightSource source, String name, Class<? extends Region> terrain,
			Set<Richtung> roads) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] does not have historic regions.");
	}

	@Override
	public boolean addHistoricRegionSight(Coordinates coordinates,
			RegionSightSource source) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] does not have historic regions.");
	}

	@Override
	protected void clearHistoricRegionSight() {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] does not have historic regions.");
	}

	@Override
	public RegionSight addDataBaseRegionSight(int turn,
			Coordinates coordinates, RegionSightSource source, String name,
			Class<? extends Region> terrain) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] does not load regions out of database.");
	}

	@Override
	public boolean addDataBaseRegionSightRoad(Coordinates coordinates,
			Richtung direction) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] does not load regions out of database.");
	}
	
	@Override
	public Island addDataBaseIsland(int id, int explorationTurn, IslandType type, String name, String description, Coordinates anchorCoordinates) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] does not load islands out of database.");
	}

	@Override
	public void addBuildingSeen(Coordinates coords, RegionSightSource source) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] sees all regions with details.");
	}

	@Override
	protected void clearBuildingSeen() {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] sees all regions with details.");
	}

	@Override
	public void addMagicalSeen(Coordinates coords, RegionSightSource source) {
		throw new UnsupportedOperationException("Omni faction [" + faction.getNummerBase36() + "] sees all regions with details.");
	}

	@Override
	protected void prepareForTurnReport() {
		if (!updated) {
			factionAtlas.clear();
			for (Region region : Region.CACHE.values()) {
				RegionSight rs = new RegionSight(region.getCoordinates(), RegionSightSource.MAP);
				factionAtlas.add(rs);
			}
			updated = true;
			// ------------------------------------- Inseln ----------------------------------------
			// 1. Alle Regionen, die eine Insel haben, dieser zuweisen.
			List<Region> regionWithoutIslandList = new ArrayList<Region>();
			for (Region region : Region.CACHE.values()) {
				if (region.getClass() == ConstantsFactory.INVISIBLE_TERRAIN_CLASS) continue;
				if (region.getPublicIslandID() == 0) {regionWithoutIslandList.add(region); continue;}
				Island island = getIsland(region.getCoordinates());
				if (island == null) { island = new PublicIsland(region.getPublicIslandID(), 0, FactionAtlas.getIslandType(region.getClass())); islandSet.add(island);}
				island.coordinateSet.add(region.getCoordinates());
			}
			// 2. neue Insel erstellen und die Regionen dieser Insel erfassen.
			int currentId = 1;
			Set<Island> islandsOfRegionSet = new HashSet<Island>();
			Class<? extends Region> regionClass;
			Coordinates regionCoordinates;
	        for (Region region: regionWithoutIslandList) {
	        	for (; currentId < Integer.MAX_VALUE; currentId++) {
	        		if (getIsland(currentId) == null) break;
	        	}
	        	
	        	regionClass = region.getClass();
	        	regionCoordinates = region.getCoordinates();
	        	
	            if (regionCoordinates.getZ() == 0) continue; // imaginäre Region...
	            
	            Island island = new PublicIsland(currentId, 0, FactionAtlas.getIslandType(regionClass));
	            
	            // new Debug("Modus @ " + c + " = " + modus);
	            // eine noch nicht erfasste Region:
	            boolean erkannt = FactionAtlas.selectIsland(regionCoordinates, regionClass, island, true, this);

	            if (erkannt) {
	            	Island newIsland;
	            	Island regionIsland;
	            	Region islandRegion;
	            	islandsOfRegionSet.clear();
	            	for (Coordinates islandCoordinates : island.coordinateSet) {
	            		islandRegion = Region.Load(islandCoordinates);
	            		if (islandRegion.getPublicIslandID() == 0) continue;
	            		regionIsland = getIsland(islandRegion.getPublicIslandID());
	            		islandsOfRegionSet.add(regionIsland);
	            	}
	            	/* Wenn er keine Regionen anderer Inseln hat:
	            	 * 1. Alle Regionen bekommen die InselID
	            	 * 2. Die Insel wird dem SET hinzugefügt
	            	 * 3. currentId++
	            	 */
	            	if (islandsOfRegionSet.isEmpty()) {
	            		for (Coordinates islandCoordinates : island.coordinateSet) {
	            			islandRegion = Region.Load(islandCoordinates);
	            			islandRegion.setPublicIslandID(island.getID());
	            		}
	            		islandSet.add(island);
	            		currentId ++;
	            	}
	            	/* Wenn er Regionen einer anderen Insel zuordnen kann:
	            	 * 1. Inseln aller Regionen herausholen und diese Inseln in einer Liste speichern.
	            	 * 2. Die Insel mit der kleinsten ID als neue Insel übernehmen.
	            	 * 3. Alle Regionen an diese ID anpassen.
	            	 * 4. Die anderen schon existierenden Insel aus dem SET löschen.
	            	 * 5. Die Parteiinseln beachten und diese anpassen. Wie auch immer... <- FEHLT
	            	 */
	            	else {
	            		int minID = Integer.MAX_VALUE;
	            		for (Island possibleIsland : islandsOfRegionSet) {
	            			if (possibleIsland.getID() < minID)
	            				minID = possibleIsland.getID();
	            		}
	            		
	            		if (island.getID() < minID) {
	            			newIsland = island;
	            			currentId ++;
	            		} else {
	            			newIsland = getIsland(minID);
	            			islandsOfRegionSet.remove(newIsland);
	            			islandsOfRegionSet.add(island);
	            		}
	            		
	            		/* aktuelle Insel übernimmt alle
            			 * Koordinaten der anderen insel */
            			FactionAtlas.mergeIslands(newIsland, islandsOfRegionSet, this);
            			islandSet.add(newIsland);
            			
            			for (Coordinates newIslandCoordinates : newIsland.getCoordinatesSet()) {
            				islandRegion = Region.Load(newIslandCoordinates);
                			islandRegion.setPublicIslandID(newIsland.getID());
                		}
	            	}
	            }
	        }
	        for (Island island : islandSet) {
            	island.setCentralCoordinates(Coordinates.getCentralCoordinates(island.getCoordinatesSet()));
            }
		}
	}

	@Override
	protected void prepareForTurn() {}
	
	@Override
	protected void prepareForReport() {
		prepareForTurnReport();
	}
}
