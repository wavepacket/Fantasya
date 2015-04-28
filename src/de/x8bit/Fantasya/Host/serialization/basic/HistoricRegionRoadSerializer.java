package de.x8bit.Fantasya.Host.serialization.basic;


import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Partei.FactionWay;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight;
import de.x8bit.Fantasya.Host.serialization.util.SerializedData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Serializer to load and save streets in a region. */

public class HistoricRegionRoadSerializer implements ObjectSerializer<Partei> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean isValidKeyset(Set<String> keys) {
		return (keys.contains("factionID")
				&& keys.contains("coordX")
				&& keys.contains("coordY")
				&& keys.contains("coordZ")
				&& keys.contains("direction"));
	}

	@Override
	public Partei load(Map<String, String> mapping) {
		// check for faction
		int factionID = Integer.decode(mapping.get("factionID"));
		
		Partei faction = null;
		for (Partei possibleFaction : Partei.getPlayerFactionList())
			if (possibleFaction.getNummer() == factionID) {
				faction = possibleFaction;
				break;
			}
		
		if (faction == null) {
			logger.warn("Invalid roads for historic region of {}; player faction not found.",
					factionID);
			return null;
		}
		
		if (faction.getFactionWay() != FactionWay.PLAYER) {
			logger.warn("Invalid roads for historic region of {}; faction is not a player faction.",
					factionID);
			return null;
		}
		
		// check that the region exists
		Coordinates coords = Coordinates.create(
			Integer.decode(mapping.get("coordX")),
			Integer.decode(mapping.get("coordY")),
			Integer.decode(mapping.get("coordZ")));
		
		// check/get the direction of the street
		
		Richtung direction;
		try {
			direction = Richtung.getRichtung(mapping.get("direction"));
			if (!faction.getFactionAtlas().addDataBaseRegionSightRoad(coords, direction)) {
				logger.warn("Error loading roads for historic region sight: Region sight not found for faction {} at {}", faction, coords);
				return null;
			}
		} catch (IllegalArgumentException ex) {
			logger.warn("Error loading street at {}; Invalid direction \"{}\"; detailed error: {}",
					coords,
					mapping.get("direction"),
					ex);
			return null;
		}
		
		return faction;
	}

	@Override
	public SerializedData save(Partei faction) {
		SerializedData data = new SerializedData();
		
		// Partei ist Spielerpartei?
		if (faction.getFactionWay() != FactionWay.PLAYER) return data;
				
		
		Collection<RegionSight> rsCollection = faction.getFactionAtlas().getFactionAtlas();
		
		for (RegionSight rs : rsCollection) {
			for (Richtung direction : rs.getRoads()) {
				Map<String, String> dirMap = new HashMap<String, String>();
				dirMap.put("factionID", String.valueOf(faction.getNummer()));
				dirMap.put("coordX", String.valueOf(rs.getCoordinates().getX()));
				dirMap.put("coordY", String.valueOf(rs.getCoordinates().getY()));
				dirMap.put("coordZ", String.valueOf(rs.getCoordinates().getZ()));
				dirMap.put("direction", String.valueOf(direction.getShortcut()));
				data.add(dirMap);
			}
		}

		return data;
	}
}
