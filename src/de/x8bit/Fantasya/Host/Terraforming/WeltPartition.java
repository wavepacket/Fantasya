package de.x8bit.Fantasya.Host.Terraforming;

import de.x8bit.Fantasya.Host.Terraforming.ProtoInsel;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Jede Instanz dieser Klasse steht für einen Teil einer Weltkarte, der von
 * möglichen anderen Teilen vollständig getrennt ist - also durch Chaos bzw.
 * undefinierte Regionen
 * @author hapebe
 */
@SuppressWarnings("rawtypes")
public class WeltPartition extends TreeMap<Integer, Map<Integer, Integer>> implements Comparable {
	private static final long serialVersionUID = -5578107036551084015L;

	private WeltPartition() {
		
	}

	@SuppressWarnings("unchecked")
	public static List<WeltPartition> Partitioniere(ProtoInsel insel) {
		List<WeltPartition> partitionen = new ArrayList<WeltPartition>();

		WeltPartition zuKlaeren = new WeltPartition();
		for (Region r : insel.alleRegionen()) zuKlaeren.add(r.getCoordinates());

		while (!zuKlaeren.isEmpty()) {
			WeltPartition aktuell = new WeltPartition();

			Coordinates seed = zuKlaeren.getCoordinates().get(0);
			
			// die komplette Partition:
			aktuell.add(seed);

			// diejenigen Koordinaten, die noch "wachsen" können, d.h. die
			// möglicherweise noch Nachbarn haben, die auch zu 'aktuell'
			// gehören könnten.
			Set<Coordinates> aktive = new HashSet<Coordinates>();
			aktive.add(seed);

			// die Startkoordinate braucht nicht mehr geklärt zu werden,
			// sie gehört auf jeden Fall zu 'aktuell'
			zuKlaeren.remove(seed);

			for (boolean gewachsen = true; gewachsen; ) {
				gewachsen = false;
				Set<Coordinates> neueAktive = new HashSet<Coordinates>();

				for (Coordinates c : aktive) {
					for (Coordinates n : c.getNeighbours()) {
						if (!zuKlaeren.contains(n)) continue;
						if (aktuell.contains(n)) continue;

						// gotcha!
						gewachsen = true;
						aktuell.add(n);
						neueAktive.add(n);
						zuKlaeren.remove(n);
					}
					
				}
				aktive = neueAktive;
			}

			partitionen.add(aktuell);
		}

		Collections.sort(partitionen);
		return partitionen;
	}

	public void add(Coordinates c) {
		int x = c.getX();
		int y = c.getY();
		this.add(x, y);
	}

	public void add(int x, int y) {
		if (this.get(x) == null) this.put(x, new TreeMap<Integer, Integer>());
		this.get(x).put(y, 1);
	}

	public boolean contains(Coordinates c) {
		return contains(c.getX(), c.getY());
	}

	public boolean contains(int x, int y) {
		if (this.get(x) == null) return false;
		if (this.get(x).get(y) == null) return false;
		return true;
	}

	public void remove(Coordinates c) {
		int x = c.getX();
		int y = c.getY();
		this.remove(x, y);
	}

	public void remove(int x, int y) {
		if (this.get(x) == null) return;
		this.get(x).remove(y);
		if (this.get(x).isEmpty()) this.remove(x);
	}

	public List<Coordinates> getCoordinates() {
		List<Coordinates> retval = new ArrayList<Coordinates>();
		for (int x : this.keySet()) {
			for (int y : this.get(x).keySet()) {
				retval.add(Coordinates.create(x, y, 0));
			}
		}
		return retval;
	}

	public List<Coordinates> getGrenzen() {
		List<Coordinates> retval = new ArrayList<Coordinates>();
		for (Coordinates c : this.getCoordinates()) {
			for (Coordinates n : c.getNeighbours()) {
				if (!this.contains(n)) {
					retval.add(c);
					break;
				}
			}
		}
		return retval;
	}


    public Coordinates getMittelpunkt() {
		List<Coordinates> coords = this.getCoordinates();

        int sx = 0; int sy = 0;
        int swelt = 0;
        for (Coordinates c : coords) {
            sx += c.getX();
            sy += c.getY();
            swelt += c.getZ();
        }

        return Coordinates.create(
                (int)Math.round((double)sx / (double)coords.size()),
                (int)Math.round((double)sy / (double)coords.size()),
                (int)Math.round((double)swelt / (double)coords.size())
                );
    }





	public int compareTo(Object o) {
		if (!(o instanceof WeltPartition)) throw new UnsupportedOperationException("Kann nur WeltPartitionen untereinander vergleichen.");
		WeltPartition wp2 = (WeltPartition)o;

		if (wp2.getCoordinates().size() > this.getCoordinates().size()) return 1;
		if (wp2.getCoordinates().size() < this.getCoordinates().size()) return -1;
		return 0;
	}

}
