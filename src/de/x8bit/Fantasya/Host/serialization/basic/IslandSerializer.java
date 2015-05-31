package de.x8bit.Fantasya.Host.serialization.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Partei.FactionWay;
import de.x8bit.Fantasya.Atlantis.util.ConstantsFactory;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.util.atlas.Island;
import de.x8bit.Fantasya.Atlantis.util.atlas.Island.IslandType;
import de.x8bit.Fantasya.Host.serialization.util.SerializedData;

public class IslandSerializer implements ObjectSerializer<Partei> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean isValidKeyset(Set<String> keys) {
		return (keys.contains("factionID")
				&& keys.contains("coordX")
				&& keys.contains("coordY")
				&& keys.contains("coordZ")
				&& keys.contains("islandID")
				&& keys.contains("name")
				&& keys.contains("description")
				&& keys.contains("explorationTurn"));
	}
	
	/** Load islands for a faction.
	 *
	 * @param mapping a key/value map that describes the taxing behavior.
	 * @return the faction for historic regions.
	 * @throws IllegalArgumentException if the party does not exist in the collection.
	 */
	@Override
	public Partei load(Map<String, String> mapping) {
		
		// check for faction
		int factionID = Integer.decode(mapping.get("factionID"));
		
		Partei faction = Partei.getFaction(factionID);
		
		if (faction == null) {
			logger.warn("Invalid island of player faction {}; player faction not found.",
					factionID);
			return null;
		}
		
		if (faction.getFactionWay() != FactionWay.PLAYER) {
			logger.warn("Invalid island of player faction {}; faction is not a player faction.",
					factionID);
			return null;
		}
		
		Coordinates anchorCoordinates = Coordinates.create(
			Integer.decode(mapping.get("coordX")),
			Integer.decode(mapping.get("coordY")),
			Integer.decode(mapping.get("coordZ")));
		
		int islandID = Integer.decode(mapping.get("islandID"));
		int explorationTurn = Integer.decode(mapping.get("explorationTurn"));
		
		String name = mapping.get("name");
		if (name.length() == 0)
			name = null;
		
		String description = mapping.get("description");
		if (description.length() == 0)
			description = null;
		
		faction.getAtlas().addDataBaseIsland(islandID, explorationTurn, IslandType.LAND, name, description, anchorCoordinates);
		
		return faction;
	}

	/** Saves the player island data.
	 *
	 * @param partei the faction whose tax data is saved.
	 * @return a data object that contains all custom taxes that this party imposes.
	 */
	@Override
	public SerializedData save(Partei faction) {		
		SerializedData data = new SerializedData();
		
		// Partei ist Spielerpartei?
		if (faction.getFactionWay() != FactionWay.PLAYER) return data;
		
		
		Collection<Island> islandCollection = faction.getAtlas().getIslandSet();
		
		for (Island island : islandCollection) {
			Coordinates anchorCoordinates = island.getAnchorCoordinates();
			Map<String, String> islandMap = new HashMap<String, String>();
			islandMap.put("factionID", String.valueOf(faction.getNummer()));
			islandMap.put("coordX", String.valueOf(anchorCoordinates.getX()));
			islandMap.put("coordY", String.valueOf(anchorCoordinates.getY()));
			islandMap.put("coordZ", String.valueOf(anchorCoordinates.getZ()));
			islandMap.put("islandID", String.valueOf(island.getID()));
			String value = island.getName();
			if (value == null)
				islandMap.put("name", ConstantsFactory.EMPTY_STRING);
			else
				islandMap.put("name", value);
			value = island.getDescription();
			if (value == null)
				islandMap.put("description", ConstantsFactory.EMPTY_STRING);
			else
				islandMap.put("description", value);
			islandMap.put("explorationTurn", String.valueOf(island.getExplorationTurn()));
			data.add(islandMap);
		}
		
		return data;
	}
}
