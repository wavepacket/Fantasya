package de.x8bit.Fantasya.Atlantis.Helper;

import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author hapebe
 */
public class StartPosition extends HashSet<Coordinates> {
	private static final long serialVersionUID = -6393701701859960211L;
	protected Coordinates zentrum;

	@Override
	public boolean add(Coordinates e) {
		if (this.size() < 3) return super.add(e);
		return false;
	}

	/**
	 * @return true, wenn genau 3 Koordinaten enthalten sind.
	 */
	public boolean istOkay() {
		if (this.size() == 3) return true;
		return false;
	}

	public void setZentrum(Coordinates c) {
		if (!this.contains(c)) throw new IllegalArgumentException("Koordinate " + c + " gehört nicht zu dieser Startposition.");
		zentrum = c;
	}

	public Coordinates getZentrum() {
		return zentrum;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		List<Coordinates> me = new ArrayList<Coordinates>();
		me.addAll(this);
		Collections.sort(me);

		List<String> parts = new ArrayList<String>();
		for (Coordinates c : me) {
			if (c.equals(getZentrum())) {
				parts.add("C" + c.toString(false));
			} else {
				parts.add(c.toString(false));
			}
		}

		return "{" + StringUtils.aufzaehlung(parts) + "}";
	}
}
