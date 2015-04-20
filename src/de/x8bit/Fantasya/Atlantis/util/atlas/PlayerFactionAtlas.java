package de.x8bit.Fantasya.Atlantis.util.atlas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.DefaultConstantsFactory;
import de.x8bit.Fantasya.Atlantis.util.atlas.Island.IslandType;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight.RegionSightSource;
import de.x8bit.Fantasya.Host.GameRules;

public class PlayerFactionAtlas extends FactionAtlas {
	
	private final Set<RegionSight> historicAtlas = new HashSet<RegionSight>();
	private Set<RegionSight> buildingAtlas = Collections.emptySet();
	private Set<RegionSight> magicAtlas = Collections.emptySet();
	private final Set<RegionSight> unitRegionSightAtlas = new HashSet<RegionSight>();
	
	public PlayerFactionAtlas(Partei faction) {
		super(faction);
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
	
	public boolean addHistoricRegionSight(int turn, Coordinates coordinates, RegionSightSource source, String name, Class<? extends Region> terrain, Set<Richtung> roads) {
		// 1. ist die RegionsSicht historischer Natur?
		if(!source.isHistoric()) return false;
		
		// 2. Existiert die RegionsSicht noch nicht im Atlas, wird sie einfach hinzugefuegt.
		if (!historicAtlas.contains(coordinates)) return historicAtlas.add(new RegionSight(turn, coordinates, source, name, terrain, roads));
		
		RegionSight oldRS = getRegionSight(coordinates, historicAtlas);
		
		// 3. Die neuere RegionsSicht wird uebernommen.
		int oldTurn = oldRS.getTurn();
		if (turn > oldTurn) {
			oldRS.update(turn, source, name, terrain, roads);
			return true;
		}
		if (turn < oldTurn) return false;
				
		// 4. Die höherwertige Quelle wird uebernommen.
		int oldValue = oldRS.getValue();
		int addValue = source.getValue();
		if (addValue > oldValue) {
			oldRS.update(turn, source, name, terrain, roads);
			return true;
		}
		if (addValue < oldValue) return false;
				
		// 5. Die beiden RegionsSichten sind ebenwuerdig, die alte bleibt in der Liste.
		return false;
	}
	
	public boolean addHistoricRegionSight(Coordinates coordinates, RegionSightSource source) {
		Region region = Region.Load(coordinates);
		int stonesForDirection = region.getSteineFuerStrasse();
		Set<Richtung> newRoads = new HashSet<Richtung>();
		if(region.istBetretbar(null) && stonesForDirection > 0) {	
			for (Richtung richtung : Richtung.values()) {
				if (region.getStrassensteine(richtung) >= stonesForDirection)
					newRoads.add(richtung);
			}
		}
		
		return addHistoricRegionSight(GameRules.getRunde(), coordinates, source, region.getName(), region.getClass(), newRoads);
	}
	
	private boolean addHistoricRegionSight(RegionSight rs) {
		
		// 1. ist die RegionsSicht historischer Natur?
		if(!rs.getRegionSightSource().isHistoric()) return false;
			
		// 2. Existiert die RegionsSicht noch nicht im Atlas, wird sie einfach hinzugefuegt.
		if (!historicAtlas.contains(rs.getCoordinates())) return historicAtlas.add(rs);
		
		RegionSight oldRS = getRegionSight(rs.getCoordinates(), historicAtlas);
		historicAtlas.remove(oldRS); // Ein Set fügt kein Element hinzu, daß es schon gibt. Das existierende wird auch nicht ersetzt.
		
		// 3. Die neuere RegionsSicht wird uebernommen.
		int addSourceRunde = rs.getTurn();
		int oldSourceRunde = oldRS.getTurn();
		if (addSourceRunde > oldSourceRunde) return historicAtlas.add(rs);
		if (addSourceRunde < oldSourceRunde) { historicAtlas.add(oldRS); return false; }
		
		// 4. Die höherwertige Quelle wird uebernommen.
		int oldSourceValue = oldRS.getRegionSightSource().getValue();
		int addSourceValue = rs.getRegionSightSource().getValue();
		if (addSourceValue > oldSourceValue) return historicAtlas.add(rs);
		if (addSourceValue < oldSourceValue) { historicAtlas.add(oldRS); return false; }
		
		// 5. Die beiden RegionsSichten sind ebenwuerdig, die alte bleibt in der Liste.
		historicAtlas.add(oldRS);
		return false;
	}
	
	@Override
	protected void clearHistoricRegionSight() {
		historicAtlas.clear();
	}
	
	public RegionSight addDataBaseRegionSight(int turn, Coordinates coordinates, RegionSightSource source, String name, Class<? extends Region> terrain) {
		RegionSight rs = new RegionSight (turn, coordinates, source, name, terrain);
		
		factionAtlas.add(rs);
		
		return rs;
	}
	
	public boolean addDataBaseRegionSightRoad(Coordinates coordinates, Richtung direction) {
		RegionSight rs = getRegionSight(coordinates, factionAtlas);
		if (rs != null) {
			rs.setDirection(direction);
			return true;
		}
		return false; // LOGGEN!!!!!!!!!!
	}
	
	public void addBuildingSeen(Coordinates coords, RegionSightSource source) {
		if (!source.isBuilding() || !Region.hasRegion(coords)) return;
		if (buildingAtlas == Collections.EMPTY_MAP) buildingAtlas = new HashSet<RegionSight>();
		setRegionSightForUpdate(getNewRegionSight(coords, source), buildingAtlas);
	}
	
	@Override
	protected void clearBuildingSeen() {
		buildingAtlas.clear();
	}
	
	public void addMagicalSeen(Coordinates coords, RegionSightSource source) {
		if (!source.isMagic() || !Region.hasRegion(coords)) return;
		if (magicAtlas == Collections.EMPTY_MAP) magicAtlas = new HashSet<RegionSight>();
		setRegionSightForUpdate(getNewRegionSight(coords, source), magicAtlas);
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
	
	private RegionSight moveRegionSightToChaos(RegionSight rs) {
		return new RegionSight(rs.getTurn(), rs.getCoordinates(), (rs.getRegionSightSource().canBeUnseenTerritory()) ? rs.getRegionSightSource() : RegionSightSource.HISTORIC, DefaultConstantsFactory.INVISIBLE_TERRAIN_STRING_VALUE, DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS);
	}
	
	private boolean setRegionSightForUpdate(RegionSight rs, Set<RegionSight> atlas) {
		Coordinates coordinates = rs.getCoordinates(); 
		
		// 1. Existiert die RegionsSicht noch nicht im Atlas, wird sie einfach hinzugefuegt.
		if (!atlas.contains(coordinates)) return atlas.add(rs);
		
		RegionSight oldRS = getRegionSight(coordinates, atlas);
		atlas.remove(oldRS); // Ein Set fügt kein Element hinzu, daß es schon gibt. Das existierende wird auch nicht ersetzt.
		
		// 2. Die neuere RegionsSicht wird uebernommen.
		int addSourceRunde = rs.getTurn();
    	int oldSourceRunde = oldRS.getTurn();
    	if (addSourceRunde > oldSourceRunde) {
    		if (rs.getRegionSightSource() == RegionSightSource.NEIGHBOUR && !rs.isInvisibleTerrain()) rs.setRoads(oldRS.getRoads());
    		return atlas.add(rs);
    	}
    	if (addSourceRunde < oldSourceRunde) { atlas.add(oldRS); return false; }
    	
    	// 3. Die detailliertere RegionsSicht wird uebernommen.
    	boolean addDetailed = rs.isDetailed();
    	boolean oldDetailed = oldRS.isDetailed();
    	if (addDetailed && !oldDetailed) return atlas.add(rs);
    	if (!addDetailed && oldDetailed) { atlas.add(oldRS); return false; }
    	
    	// 4. Die höherwertige Quelle wird uebernommen.
    	int addSourceValue = rs.getValue();
    	int oldSourceValue = oldRS.getValue();
    	if (addSourceValue > oldSourceValue) return atlas.add(rs);
    	if (addSourceValue < oldSourceValue) { atlas.add(oldRS); return false; }
    	
    	// 5. Die beiden RegionsSichten sind ebenwuerdig, die alte bleibt in der Liste.
    	atlas.add(oldRS);
        return false;
	}
	
	private boolean setRegionSightForUpdate(RegionSight rs) {
		Coordinates coords = rs.getCoordinates();
		Region region = Region.Load(coords);
		
		if (region.getClass() == DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS || !faction.canAccess(region)) rs = moveRegionSightToChaos(rs);
		
		return setRegionSightForUpdate(rs, factionAtlas);
	}
	
	private void addForUpdate(Set<RegionSight> atlas) {
		for (RegionSight rs : atlas) {
			setRegionSightForUpdate(rs);
			for (Coordinates nCoords : rs.getCoordinates().getNeighbours()) {
				if (magicAtlas.contains(nCoords)) continue;
               	if (buildingAtlas.contains(nCoords)) continue;
				if (unitRegionSightAtlas.contains(nCoords)) continue;
                RegionSight nrs = new RegionSight(nCoords, RegionSightSource.NEIGHBOUR);
                setRegionSightForUpdate(nrs);
            }
		}
	}
	
	/**
	 * updates historic atlas to faction atlas implementing building seen, magical seen and unit seen with neighbours
	 */
	
	public void prepareForTurnReport() {
		// 1. Eine Liste von allen Regionen, in denen eine Einheit steht.
		Set<Unit> units = faction.getEinheiten();
		Map<Coordinates, Region> unitRegions = new HashMap<Coordinates, Region>();
		for (Unit unit : units) {
			if (!unitRegions.containsKey(unit.getCoordinates())) {
				Region region = Region.Load(unit.getCoordinates());
				unitRegions.put(region.getCoordinates(), region);
			}
		}
		
		// 2. Abgleich, ob sich Regionen geändert haben, in denen nach dem letzten Update noch Einheiten befanden.
		// 2.1. Regionen jetzt ohne Einheiten werden historisch, falls während einer Änderung
		//			die Funktion addHistoricalRegionSight(RegionSight) nicht genutzt wurde.
		for (RegionSight rs : unitRegionSightAtlas) {
			if (!unitRegions.containsKey(rs.getCoordinates())) {
				rs.update(rs.getTurn(), RegionSightSource.HISTORIC, rs.getName(), rs.getTerrain(), rs.getRoads());
				addHistoricRegionSight(rs);
			}
		}
		// 2.2. Die Liste der aktuellen Regionen mit Einheiten wird übernommen bzw. neu erstellt
		Set<RegionSight> tempUnitRegionSightAtlas = new HashSet<RegionSight>(unitRegionSightAtlas);
		unitRegionSightAtlas.clear();
		for (Region r : unitRegions.values()) {
			Coordinates coordinates = r.getCoordinates();
			if (tempUnitRegionSightAtlas.contains(coordinates)) {
				unitRegionSightAtlas.add(getRegionSight(coordinates, tempUnitRegionSightAtlas));
			}
			else
				unitRegionSightAtlas.add(getNewRegionSight(coordinates, RegionSightSource.UNIT));
		}
		
		// 3. Löschen der bisher geltenden Karte.
		factionAtlas.clear();
		
		// 4. Erstellen der Karte
		// 4.1. Historische Karte wird hinzugefügt
		
		factionAtlas.addAll(historicAtlas);
		
		// 4.2. Fernsicht und derren Nachtbarn wird hinzugefügt
		addForUpdate(magicAtlas);
		
		// 4.3. Leuchtturmkarte und derren Nachtbarn wird hinzugefügt
		addForUpdate(buildingAtlas);
		
		// 3.4. Regionen mit Einheiten und derren Nachtbarn werden hinzugefügt.
		addForUpdate(unitRegionSightAtlas);
		
		// ------------------------------------- Inseln ----------------------------------------
		// 1. Die Inseln, die existieren ihre Koordinaten zuweisen
		for (Island privateIsland : islandSet) {
			
		}
		// 1. Alle Regionen, die eine Insel haben, dieser zuweisen.
		List<Region> regionWithoutIslandList = new ArrayList<Region>();
		// Inseln werden geladen und sollen nur ein update bekommen...
		
		for (RegionSight rs : factionAtlas) {
			if (rs.getTerrain() == DefaultConstantsFactory.INVISIBLE_TERRAIN_CLASS) continue;
			if (region.getPublicIslandID() == 0) {regionWithoutIslandList.add(region); continue;}
			Island island = getIsland(region.getCoordinates());
			if (island == null) { island = new PublicIsland(region.getPublicIslandID(), FactionAtlas.getIslandType(region.getClass())); islandSet.add(island);}
			island.coordinateSet.add(region.getCoordinates());
		}
					// 2. neue Insel erstellen und die Regionen dieser Insel erfassen.
					int currentId = 1;
					Set<Island> islandsOfRegionSet = new HashSet<Island>();
			        for (Region region: regionWithoutIslandList) {
			        	for (; currentId < Integer.MAX_VALUE; currentId++) {
			        		if (getIsland(currentId) == null) break;
			        	}
			        	
			            if (region.getCoordinates().getZ() == 0) continue; // imaginäre Region...
			            
			            Island island = new PublicIsland(currentId, FactionAtlas.getIslandType(region.getClass()));
			            
			            // new Debug("Modus @ " + c + " = " + modus);
			            // eine noch nicht erfasste Region:
			            boolean erkannt = selectIsland(region, island);

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
		            			mergeIslands(newIsland, islandsOfRegionSet);
		            			islandSet.add(newIsland);
		            			
		            			for (Coordinates newIslandCoordinates : newIsland.getCoordinateSet()) {
		            				islandRegion = Region.Load(newIslandCoordinates);
		                			islandRegion.setPublicIslandID(newIsland.getID());
		                		}
			            	}
			            }
			        }
			        for (Island island : islandSet) {
		            	island.centralCoordinates = Coordinates.getCentralCoordinates(island.getCoordinateSet());
		            }
	}
	
	public Island addDataBaseIsland(int id, IslandType type, String name, String description, Coordinates coordinates) {
		Island island = new PrivateIsland(id, type);
		island.setName(name);
		island.setDescription(description);
		island.centralCoordinates = coordinates;
		
		islandSet.add(island);
		
		return island;
	}
	
	private boolean selectIsland(RegionSight rs, Island island) {
        if (!FactionAtlas.isIslandModus(rs.getTerrain(), island.getIslandType())) return false;
        
        LOGGER.info("Region " + rs.getCoordinates() + " [" + rs.getTerrain().getSimpleName() + "] wird der Insel (" + island.getID() + ") mit dem Modus " + island.getIslandType() + " hinzugefügt werden.");
        island.coordinateSet.add(rs.getCoordinates());
        // region.setPublicIslandID(island.getPublicID());
        // hinzufügen der Region
        
        // Insel soll temporär ganz erfasst werden, um ggf. neu generierte inseln, die mit alten zusammenhängen,
        // mit diesen zusammen zu legen.
        RegionSight neighbourRegionSight;
        for (Coordinates neighbourCoordinates : rs.getCoordinates().getNeighbours()) {
        	neighbourRegionSight = getRegionSight(neighbourCoordinates);
        	if (neighbourRegionSight == null || neighbourRegionSight.isInvisibleTerrain()) continue;
            //if (getPublicID(neighbourCoordinate) == ConstantFactory.NO_INT_VALUE) {
                // eine noch nicht erfasste Region:
        	if (!island.coordinateSet.contains(neighbourCoordinates)) selectIsland(neighbourRegionSight, island);
            //}
        }
        
        return true;
    }
	
	@Override
	protected void prepareForTurn() {
		// historischer Atlas wird gelöscht ...
		historicAtlas.clear();
		// ... und aktualisiert ...
		historicAtlas.addAll(factionAtlas);
		// ... und alles wird historisch
		for (RegionSight rs : historicAtlas) {
			rs.setRegionSightSource(RegionSightSource.HISTORIC);
		}
		// Alle Atlanten bis auf den historischen löschen.
		magicAtlas.clear();
		buildingAtlas.clear();
		unitRegionSightAtlas.clear();
		factionAtlas.clear();
	}
	
	@Override
	protected void prepareForReport() {
		// Inseln mit den koordinaten
	}
}