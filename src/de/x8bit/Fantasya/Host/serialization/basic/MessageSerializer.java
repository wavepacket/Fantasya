package de.x8bit.Fantasya.Host.serialization.basic;

import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.Message;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Host.serialization.util.SerializedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Serializer to load/save messages. 
 *
 * Note: In contrast to the old serialization algorithm, I have removed the use
 * of time stamps. They were basically never used anyway.
 */

// TODO: I have not modified the serialization routines to keep the table layout,
// even though they seem buggy. Most importantly, the coordinate (0,0,0) is interpreted
// as the message having no coordinate, although the coordinate is not
// documented anywhere as being in whatever way special.
public class MessageSerializer implements ObjectSerializer<Message> {

	/** Constructs a new serializer.
	 * 
	 * @param factionList a list of all active parties
	 * @param coordsList a list of all available coordinates
	 * @param unitList a list of all available units
	 */
	public MessageSerializer() {}

	@Override
	public boolean isValidKeyset(Set<String> keys) {
		return (keys.contains("kategorie")
				&& keys.contains("text")
				&& keys.contains("partei")
				&& keys.contains("koordx")
				&& keys.contains("koordy")
				&& keys.contains("welt")
				&& keys.contains("einheit"));
	}

	@Override
	public Message load(Map<String, String> mapping) {
		// load the message type
		Message msg = null;
		try {
			msg = (Message) Class.forName(
					"de.x8bit.Fantasya.Atlantis.Messages." + mapping.get("kategorie")).newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not load message type.", e);
		}

		msg.setText(mapping.get("text"));
		
		Set<Partei> allFactions = new HashSet<Partei>();
		allFactions.add(Partei.OMNI_FACTION);
		allFactions.addAll(Partei.getNPCFactionList());
		allFactions.addAll(Partei.getPlayerFactionList());
		
		// load the player that the message corresponds to
		int pid = Integer.decode(mapping.get("partei"));
		
		Partei p = Partei.getFaction(pid);
		
		if (p != null) {
			msg.setPartei(p);
		}

		if (pid != 0 && msg.getFaction() == null) {
			// TODO: proper logging
			return msg;
			//throw new IllegalArgumentException("Partei not found in list.");
		}

		// load the coordinates that the message refers to
		Coordinates coords = Coordinates.create(Integer.decode(mapping.get("koordx")),
				Integer.decode(mapping.get("koordy")),
				Integer.decode(mapping.get("welt")));
		if (coords.getX() != 0 || coords.getY() != 0 || coords.getZ() != 0) {
			if (!Region.CACHE.keySet().contains(coords)) {
				// TODO: proper logging
				return msg;
				//throw new IllegalArgumentException("Coordinate not found on map.");
			}
			msg.setCoordinates(coords);
		}

		// load the unit that the message refers to
		int uid = Integer.decode(mapping.get("einheit"));
		Unit u = Unit.CACHE.get(uid);
		if (u != null) {
			msg.setUnit(u);
		}

		if (uid != 0 && msg.getUnit() == null) {
			// TODO: proper logging
			return msg;
			//throw new IllegalArgumentException("Unit not found in list.");
		}

		return msg;
	}

	@Override
	public SerializedData save(Message object) {
		// Compatibility notes: I do not save a couple of seemingly pointless
		// properties:
		// 1. "zeit" was a property that saw only use that I could identify as
		//    pointless.
		// 2. Debuglevel was a constant zero and never loaded.
		Map<String, String> data = new HashMap<String, String>();

		data.put("kategorie", object.getClass().getSimpleName());
		data.put("text", object.getText());

		data.put("partei", "0");
		if (object.getFaction() != null) {
			data.put("partei", String.valueOf(object.getFaction().getNummer()));
		}

		data.put("koordx", "0");
		data.put("koordy", "0");
		data.put("welt", "0");
		if (object.getCoordinates() != null) {
			Coordinates coords = object.getCoordinates();
			data.put("koordx", String.valueOf(coords.getX()));
			data.put("koordy", String.valueOf(coords.getY()));
			data.put("welt", String.valueOf(coords.getZ()));
		}

		data.put("einheit", "0");
		if (object.getUnit() != null) {
			data.put("einheit", String.valueOf(object.getUnit().getNummer()));
		}

		return new SerializedData(data);
	}
}