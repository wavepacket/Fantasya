package de.x8bit.Fantasya.Host.serialization.basic;

import de.x8bit.Fantasya.Atlantis.Allianz.AllianzOption;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Host.serialization.util.SerializedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Saves and loads alliance information.
 *
 * On loading, this class attaches alliance information to the affected party.
 * On saving, all this alliance information is serialized.
 */

// TODO: Allianzen sind vom Design her viel zu kompliziert. Vereinfache das
// Ganze. Entferne AllianzOption. Alles nebenbei.

public class AllianzSerializer implements ObjectSerializer<Partei> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean isValidKeyset(Set<String> keys) {
		return (keys.contains("partei")
				&& keys.contains("partner")
				&& keys.contains("optionen"));
	}

	/** Attaches the alliance information to the corresponding party.
	 *
	 * @param mapping the serialized form of the alliance data.
	 * @return the party that formed the alliance or null on error.
	 */
	@Override
	public Partei load(Map<String, String> mapping) {
		int parteiId = Integer.decode(mapping.get("partei"));
		int partnerId = Integer.decode(mapping.get("partner"));
		AllianzOption option = AllianzOption.ordinal(mapping.get("optionen"));

		// find the two parties.
		Partei partei = Partei.getFaction(parteiId);
		Partei partner = Partei.getFaction(partnerId);

		// log all sorts of errors
		if (option == AllianzOption.Alles) {
			logger.warn("Deprecated alliance option \"Alles\" was used.");
		}
		if (partei == null) {
			logger.warn("Invalid alliance between " + parteiId
					+ " and " + partnerId + "; partei not found.");
			return null;
		}
		if (partner == null) {
			logger.warn("Invalid alliance between " + parteiId
					+ " and " + partnerId + "; alliance partner not found.");
			return null;
		}

		logger.debug("Loaded alliance of type {} between {} and {}",
				option, partei.getName(), partner.getName());
		partei.setAllianz(partnerId, option, true);
		return partei;
	}

	/** Serializes and returns all alliance information for the party. */
	@Override
	public SerializedData save(Partei partei) {
		SerializedData data = new SerializedData();

		for (int partner : partei.getAllianzen().keySet()) {
			// check that the partner exists.
			boolean found = (Partei.getFaction(partner) != null);

			if (!found) {
				logger.warn("Partner " + partner + " of alliances of " + partei.getNummer()
						+ " does not exist. Skipping entry.");
				continue;
			}

			// save the alliance options.
			for (AllianzOption option : AllianzOption.values()) {
				if (option == AllianzOption.Alles || !partei.hatAllianz(partner, option)) {
					continue;
				}

				Map<String,String> item = new HashMap<String,String>();
				item.put("partei", String.valueOf(partei.getNummer()));
				item.put("partner", String.valueOf(partner));
				item.put("optionen", option.toString());

				data.add(item);
			}
		}

		return data;
	}

}
