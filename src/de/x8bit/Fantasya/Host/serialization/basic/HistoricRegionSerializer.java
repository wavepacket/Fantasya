package de.x8bit.Fantasya.Host.serialization.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Partei.FactionWay;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight;
import de.x8bit.Fantasya.Atlantis.util.atlas.RegionSight.RegionSightSource;
import de.x8bit.Fantasya.Host.serialization.util.SerializedData;

public class HistoricRegionSerializer implements ObjectSerializer<Partei> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean isValidKeyset(Set<String> keys) {
		return (keys.contains("factionID")
				&& keys.contains("coordX")
				&& keys.contains("coordY")
				&& keys.contains("coordZ")
				&& keys.contains("source")
				&& keys.contains("turn")
				&& keys.contains("terrain")
				&& keys.contains("name"));
	}

	/** Load historic region for a faction.
	 *
	 * @param mapping a key/value map that describes the taxing behavior.
	 * @return the faction for historic regions.
	 * @throws IllegalArgumentException if the party does not exist in the collection.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Partei load(Map<String, String> mapping) {
		
		// check for faction
		int factionID = Integer.decode(mapping.get("factionID"));
		
		Partei faction = null;
		for (Partei possibleFaction : Partei.getPlayerFactionList()) {
			if (possibleFaction.getNummer() == factionID) {
				faction = possibleFaction;
				break;
			}
		}
		
		if (faction == null) {
			logger.warn("Invalid historic region of {}; player faction not found.",
					factionID);
			return null;
		}
		
		if (faction.getFactionWay() != FactionWay.PLAYER) {
			logger.warn("Invalid historic region of {}; faction is not a player faction.",
					factionID);
			return null;
		}
		
		// check that the region exists
		Coordinates coords = Coordinates.create(
			Integer.decode(mapping.get("coordX")),
			Integer.decode(mapping.get("coordY")),
			Integer.decode(mapping.get("coordZ")));
		
		RegionSightSource source = RegionSightSource.getSourceForString(mapping.get("source"));
		int turn = Integer.decode(mapping.get("turn"));
		Class<? extends Region> terrain;
		try {
			terrain = (Class<? extends Region>) Class.forName(
					"de.x8bit.Fantasya.Atlantis.Regions." + mapping.get("terrain"));
		} catch (Exception ex) {
			logger.warn("Error loading region sight. Bad region type \"{]\" for region sight \"{}\" of faction  \"{}\".",
					mapping.get("terrain"), mapping.get("name"), faction.getNummerBase36());
			return null;
		}
		String name = mapping.get("name");
		
		faction.getFactionAtlas().addDataBaseRegionSight(turn, coords, source, name, terrain);
		
		return faction;
	}

	/** Saves the historic region data.
	 *
	 * @param partei the faction whose tax data is saved.
	 * @return a data object that contains all custom taxes that this party imposes.
	 */
	@Override
	public SerializedData save(Partei faction) {		
		SerializedData data = new SerializedData();
		
		// Partei ist Spielerpartei?
		if (faction.getFactionWay() != FactionWay.PLAYER) return data;
		
		
		Collection<RegionSight> rsCollection = faction.getFactionAtlas().getFactionAtlas();
		
		for (RegionSight rs : rsCollection) {
			Map<String, String> rsMap = new HashMap<String, String>();
			rsMap.put("factionID", String.valueOf(faction.getNummer()));
			rsMap.put("coordX", String.valueOf(rs.getCoordinates().getX()));
			rsMap.put("coordY", String.valueOf(rs.getCoordinates().getY()));
			rsMap.put("coordZ", String.valueOf(rs.getCoordinates().getZ()));
			rsMap.put("source", RegionSightSource.getStringForSource(rs.getRegionSightSource()));
			rsMap.put("turn", String.valueOf(rs.getTurn()));
			rsMap.put("terrain", String.valueOf(rs.getTerrainName()));
			rsMap.put("name", String.valueOf(rs.getName()));
			data.add(rsMap);
		}
		
		return data;
	}
}
